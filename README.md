# Isimo Solutions: Automation Framework

Isimo is a set of technologies and solutions aimed at automation of interactions with IT systems. Isimo is aimed to implement complex end-to-end user interactions combining different kinds of interactions like: user interfaces, database operations, file operations etc. Isimo is Java-based, highly extensible and in its core components are available as open source. Isimo is meant to be simplify and streamline to process of automation reducing the need for programming interactions from usual 100% to about 10-20% thereby allowing to lower the overall costs of maintenance of automation processes and to use non-technical human resources to implement automations. There are several possible usages of Isimo, the most important being:
- Interaction-based test automation (including browser automation based on Selenium)
- Robotic Process Automations
- Automation of browser interactions

The word isimo originates from the Zulu language and simply means scenario.

See more at [Isimo.Solutions Website](https://isimo.solutions)

## Preconditions
To run and use Isimo Solutions you will first need to install and setup:

-   Java SE Development Kit 8
-   Maven 	3.6.0 (or higher)

## Getting Started
See the [Isimo.Solutions Getting Started page](https://isimo.solutions/getting-started) for more detailed instructions.

### Version select

#### Newest release
Copy settings.xml from main git folder to .m2 folder in user files on your computer.

#### Snapshot from git
Navigate to isimo.parent folder and use command:
```
mvn clean install
```

### First project
```
mvn -B archetype:generate -DarchetypeGroupId=com.isimo   
-DarchetypeArtifactId=isimo.archetype   
-DarchetypeVersion=2.3-Sel-3.141.59-SNAPSHOT -DgroupId=<...>   
-DartifactId=<...> -Dversion= <...>  
-DtestcasesDir=<...>
```
Where instead of <…> you need to provide data for new project

Variable description:
- 	archetypeVersion - version of the framework you are using
-   groupId – groupId of the newly created project
-   artifactId – name of the created project
-   version – version number of the created project
-   testcasesDir – name of the folder where project will store testcases files

### Launching first testcase
In the main project folder:
```
mvn -e -Disimo.closebrowseronerror=false -Denv=default -Dtestscenario=helloworld -Disimo.nocommandline=false -Disimo.commandlineonerror=true -Dtest.target=test -Disimo.execution.phases=preparation,test -Disimo.browser=internetExplorer -Disimo.report=false -Dtout=100000000 test
```

### Creating new testcases

In order to create new testcase you need to create .xml file in the testcases folder (which name was specified during project creation). Name of the .xml file is also name of the new testcase.
```
<scenario xmlns="http://isimo.com/scenario/1.0" timeout="100000000">  
	<actions>  
	</actions>  
</scenario>
```
Between "actions" tags write actions you want to be performed during test launch.
