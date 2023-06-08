@echo off

set "lnkFile=..\arcane_insight.lnk"
set "exeFile=app\arcane_insight.exe"
set "icoFile=..\img\arcane_insight.ico"

echo [InternetShortcut] > "%lnkFile%"
echo URL=file:///%exeFile% >> "%lnkFile%"
echo IconFile=%icoFile% >> "%lnkFile%"
echo IconIndex=0 >> "%lnkFile%"

chcp 65001&cls
cd /d "%~dp0"
powershell.exe -Command "& '.\arcane_insight.exe'"