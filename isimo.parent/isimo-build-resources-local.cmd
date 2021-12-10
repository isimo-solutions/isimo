set RESOURCESURL=file:c:/isimoresources
mvn -s f:\git\settings.xml -Dgecko.driver.url=%RESOURCESURL% -Dfirefox.download.url=%RESOURCESURL% -Dchrome.driver.url=%RESOURCESURL%/chromedriver_win32.zip -Die.driver.download.url=%RESOURCESURL% -Dedge.driver.url=%RESOURCESURL%/edgedriver_win32.zip clean install
