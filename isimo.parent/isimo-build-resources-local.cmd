set RESOURCESURL=file:c:/isimoresources
set SETTINGSFILE=c:/git/isimo/settings.xml
set JAVA_HOME=C:/java/jdk1.8.0_221
mvn -s %SETTINGSFILE% -Dgecko.driver.url=%RESOURCESURL% -Dfirefox.download.url=%RESOURCESURL% -Dchrome.driver.url=%RESOURCESURL%/chromedriver_win32.zip -Die.driver.download.url=%RESOURCESURL% -Dedge.driver.url=%RESOURCESURL%/edgedriver_win64.zip clean install
