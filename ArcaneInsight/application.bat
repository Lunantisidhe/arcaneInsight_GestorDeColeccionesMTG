@echo off
chcp 65001&cls
cd /d "%~dp0"
powershell.exe -Command "& '.\ArcaneInsight.exe'"