package com.dam.rgb.db;

import com.dam.rgb.utilities.CollectionNames;
import com.dam.rgb.utilities.Connection;
import com.dam.rgb.visual.LoadingAnimation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BulkCardsImporter {

    // comprueba que la base de datos este al dia. si no lo esta, la reimporta
    public static void checkUpdate() {

        JSONObject bulkData = recoverBulkData();
        if (bulkData == null || bulkData.isEmpty())
            return;

        // recibimos la fecha de ultima actualizacion de las cartas del api de scryfall
        String bulkUpdateDateString = bulkData.getString("updated_at").split("\\+")[0];
        DateTimeFormatter formatterIso = DateTimeFormatter.ISO_DATE_TIME;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy HH:mm");
        LocalDateTime bulkUpdateDate = LocalDateTime.parse(bulkUpdateDateString, formatterIso);

        // recibimos la fecha de ultima actualizacion de nuestra base de datos
        LocalDateTime lastUpdatedDB = DBManager.recoverLastUpdatedDate();

        // comprobamos cual de las dos fechas es mas reciente
        System.out.println("Comprobando la versión de la base de datos...");

        if (bulkUpdateDate.isAfter(lastUpdatedDB)) {

            // base de datos no actualizada
            System.out.println("Se ha encontrado una versión más reciente de la base de datos ("
                    + bulkUpdateDate.format(formatter) + ").");

            System.out.println("Actualizando base de datos...");

            // comienza la animacion de carga
            LoadingAnimation.startAnimation();

            importAllDefaultCards(bulkData, bulkUpdateDate);

            // termina la animacion de carga
            LoadingAnimation.stopAnimation();

            System.out.println("\nSe ha actualizado la base de datos a la versión más reciente.");

        // base de datos actualizada
        } else
            System.out.println("La base de datos está actualizada (versión del "
                    + bulkUpdateDate.format(formatter) + ").");
    }

    // hace una http request para recuperar los datos de todas las cartas
    public static JSONObject recoverBulkData() {

        try {
            // recuperamos todos los datos de las cartas del api de scryfall
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("https://api.scryfall.com/bulk-data"))
                    .header("Accept", "application/json").header("User-Agent", "Mozilla/5.0")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            String responseBody = response.body();
            JSONObject bulkData = new JSONObject(responseBody);

            // recuperamos los datos de las cartas
            JSONArray bulkDataArray = bulkData.getJSONArray("data");

            for (int i = 0; i < bulkDataArray.length(); i++) {
                JSONObject bulkObj = bulkDataArray.getJSONObject(i);
                if (bulkObj.getString("type").equals("oracle_cards"))
                    return bulkObj;
            }

        } catch (JSONException | IOException | InterruptedException e) {
            System.err.println("Error: no se pudieron recibir los datos de las cartas.");
        }

        return null;
    }


    // importa todas las cartas a la base de datos
    public static void importAllDefaultCards(JSONObject bulkData, LocalDateTime bulkUpdateDate) {

        if (bulkData == null || bulkData.isEmpty())
            return;

        // recupera la url de descarga
        String urlName = bulkData.getString("download_uri");

        // si existen las bases de datos, las elimina
        Connection dataConnection = new Connection("data");
        dataConnection.getCollection().drop();
        dataConnection.close();
        Connection cardsConnection = new Connection(CollectionNames.GLOBAL_COLLECTION_NAME);
        cardsConnection.getCollection().drop();
        cardsConnection.close();


        try {
            // lee el fichero json con las cartas para evitar descargarlo
            URL url = new URL(urlName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("memorabilia"))
                    response.append(line);
            }
            reader.close();

            String json = response.toString();
            JSONArray jsonArray = new JSONArray(json);

            // añade las cartas y la fecha de ultima actualizacion a la base de datos
            DBManager.createSeveralCards(DBManager.jsonArrayToDocArray(jsonArray), CollectionNames.GLOBAL_COLLECTION_NAME);
            DBManager.addLastUpdatedDate(bulkUpdateDate);

        } catch (IOException | JSONException e) {
            System.err.println("Error: No se pudieron importar las cartas.");
        }
    }
}
