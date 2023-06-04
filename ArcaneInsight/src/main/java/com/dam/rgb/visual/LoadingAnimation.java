package com.dam.rgb.visual;

public class LoadingAnimation implements Runnable {

    private static volatile boolean running = true;

    public static void stopAnimation() {

        running = false;
    }

    public static void startAnimation() {

        running = true;

        LoadingAnimation loadingAnimation = new LoadingAnimation();
        Thread tLoadingAnimation = new Thread(loadingAnimation);
        tLoadingAnimation.start();
    }

    @Override
    public void run() {

        int iteration = 0;

        System.out.println(" /\\___/\\");

        while (running) {

            System.out.print("\r" + Style.LOADING_ANIM[iteration]);

            iteration++;
            if (iteration == 4)
                iteration = 0;

            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                System.err.println("Error: error durante la ejecución de la animación.");
            }
        }
    }
}
