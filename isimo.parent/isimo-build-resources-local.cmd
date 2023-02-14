set RESOURCESURL=file:f:/isimoresources
set SETTINGSFILE=f:\git\settings.xml
set JAVA_HOME=F:\SoftwareAG103\jvm\jvm
mvn -s %SETTINGSFILE% -Dgecko.driver.url=%RESOURCESURL% -Dfirefox.download.url=%RESOURCESURL% -Dchrome.driver.url=%RESOURCESURL%/chromedriver_win32.zip -Die.driver.download.url=%RESOURCESURL% -Dedge.driver.url=%RESOURCESURL%/edgedriver_win64.zip clean install
