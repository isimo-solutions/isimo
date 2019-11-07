@echo off
cd %~dp0
set /p scenario="Test scenario: " || set scenario="helloworld"
set /p env="Enviroment: " || set env="default"
set /p browser="Browser: " || set browser="internetExplorer"

call mvn -e -Disimo.closebrowseronerror=false -Denv=%env% -Dscenario=%scenario% -Disimo.nocommandline=false -Disimo.commandlineonerror=true -Dtest.target=test -Disimo.browser=%browser% -Disimo.report=false -Dtout=100000000 test
pause