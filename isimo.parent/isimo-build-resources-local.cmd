set RESOURCESURL=file:c:/isimoresources
set SETTINGSFILE=f:\git\settings.xml
mvn -s %SETTINGSFILE% -Dgecko.driver.url=%RESOURCESURL% -Dfirefox.download.url=%RESOURCESURL% -Dchrome.driver.url=%RESOURCESURL%/chromedriver_win32.zip -Die.driver.download.url=%RESOURCESURL% -Dedge.driver.url=%RESOURCESURL%/edgedriver_win32.zip clean install
