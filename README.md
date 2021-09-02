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
	...
	</actions>  
</scenario>
```
Between "actions" tags write actions you want to be performed during test launch. For example, the following testcase will open the Google Search and start the query with the keyword "Hello World":

```
<scenario xmlns="http://isimo.com/scenario/1.0" timeout="100000000">
	<actions>
		<open url="http://google.com"/>
		<input css="input[name='q']" value="Hello World"/>
		<click xpath="//input[@name='btnK']"/>
	</actions>
</scenario>
```
### Built-in functions
It's possible to use the built-in functions in the actions code. The general syntax to be used in the actions is: {function(arg1,...,argN)}.

There are two very common usecases for that: current date/time generation and property expansion, for example the following code will store the current date + 30 days in the   [java.text.SimpleDate format](https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html) dd.MM.yyyy:
```
<scenario xmlns="http://isimo.com/scenario/1.0" timeout="100000000">
	<actions>
		<store variable="current_date" text="{sysdate(30,dd.MM.yyyy)}" type="text"/>
	</actions>
</scenario>
```
The following will open the url contained in the propery url.to.open:

```
<scenario xmlns="http://isimo.com/scenario/1.0">
	<actions>
		<open url="{property(url.to.open)}"/>
	</actions>
</scenario>
```

The following built-in functions are available currently:

|Function|Parameters|Description|
|--------|----------|-------|
|property|property name|returns the value of the given property|
|sysdate|first parameter - number of days to add to the current date (can be negative), second, optional is the outputpattern, default output pattern is dd.MM.yyyy|returns the date in the given pattern|
|systime|first parameter - number of minues to add to the current date/time (can be negative)|same as sysdate but the parameter value passes the number of minutes|
|absolutepath|relative resource file path|returns absolute path to the return file - this function is useful for file dialog interactions with robot action|
|randominteger|max integer|returns random integer from the interval 0...max integer|
|urlencode|String to urlencode|Url encoded value of the input string|
|randomstring|Length of the random string|Random string of a given length|

### Properties, variables
As an input each testcase receives the set of property values. This initial set is stored as test.properties file in the target subdirectory of the project. The eventual set of properties is calculated using the sequence of property files defined in the framework. The most important property files used in this sequence are config/default.properties and config/env/${env}.properties, where env is an input property passed to the maven project. The properties from the latter file override the values from the config/default.properties. This allows to define all properties on the general level (config/default.properties) and override them depending on the currently used environment. The property values are available from the isimo api. They can also be used in the testcase actions, for example the following code will open the browser and navigate to the url defined by the property url.to.open:
```
<scenario xmlns="http://isimo.com/scenario/1.0">
	<actions>
		<open url="{property(url.to.open)}"/>
	</actions>
</scenario>
```

### Including testscenarios
Special action include allows to include other testcases in the execution of a given testcase, for example the following scenario will execute the included scenario include/login.xml:
```
<scenario xmlns="http://isimo.com/scenario/1.0">
	<actions>
		<include scenario="include/login"/>
	</actions>
</scenario>
```
The included scenarios may be parameterized, for example:
```
<scenario xmlns="http://isimo.com/scenario/1.0">
	<actions>
		<include scenario="include/login" username="someuser" password="somepassword"/>
	</actions>
</scenario>
```
The attributes passed to the included scenario can be used in the following way (example contents of the include/login.xml):
```
<scenario xmlns="http://isimo.com/scenario/1.0" timeout="100000000">
	<actions>
		<open url="http://some.url"/>
		<input css="input[id='username']" value="{property(attr:username)}"/>
		<input css="input[id='password']" value="{property(attr:password)}"/>
		<click css="button.login-btn"/>
		<maximize/>
	</actions>
</scenario>
```

### Most commonly used core actions

#### include

Includes another testcase in the testcase execution

| Attribute name | Description |
|----------------|---------------|
| scenario | Testcase file path without .xml extension |
| \<any attributename\> | Any attribute name will be passed to the subscenario, the attributes will be available as properties in the subscenario under the names attr:\<attributename\>|

#### robot

Executes automated typing using java.awt.Robot

| Attribute name | Description |
|----------------|---------------|
| texttotype | Text to type automatically |
| sendenter | Click enter after typing, defaults to false|
| sendtab | Click tab after typing, defaults to false|

### Most commonly used web actions

#### Web Selectors
Web selectors allow for referencing elements in the webpage content. Isimo uses the following types of selectors. Selectors are expressed as attributes on various web action elements:

| Attribute/Selector name | Description | Example selector usage | Example element found |
|-------------------------|-------------|---------|-----------------------|
| ID			  | Simple referencing using elements id attribute | ```<click id="somebutton"/>``` | ```<button id="somebutton">``` |
| CSS                     | Referencing using CSS selectors, see https://www.w3schools.com/cssref/css_selectors.asp for extesive documentation, less powerful than XPath | ```<click css=".someclass"/\>``` | ```<button class="someclass"/>``` |
| XPath                     | Referencing using XPath expressions, see https://www.w3schools.com/xml/xpath_intro.asp for tutorial | ```<click xpath="//span[id='parent' and button[@class='someclass']]"/>``` | ```<span id="parent"><button class="someclass"/></span>``` |

#### store
Stores the given value as a property in the current testcase. There are several usage scenarios which are driven by the type attribute. If the type=count, then stored variable will be the number of elements returned by the given selector, for example this will store the number of rows of the given table:

```
<store type="count" xpath="//table[id='sometable']//tr" variable="numberofrows"/>
```
If type=text, then expression provided as text attribute will be stored
```
<store type="text" text="sometext" variable="somevariable"/>
```
If type is one of id,xpath,css then the respective text content of the selected HTML element will be stored:
```
<store type="xpath" xpath="//div[id='somediv']" content="true"/>
```
or the attribute value of the selected element will be stored (here, storing the value of the class attribute):

```
<store type="xpath" xpath="//div[id='somediv']" attribute="class"/>
```

| Attribute name | Description |
|----------------|---------------|
| type | One of 'text','count' or 'id','xpath','css' |
| variable | Name of the testcase property to store the value |
| attribute | Name of the HTMLE attribute, the value of which should be stored |
| content |  boolean, if set to true, the text content of the found HTML element will be stored |
| urlencode | boolean, if set to true, the value will be urlencoded before storing |
| <selector> | One of the web selectors used to reference the element |

#### click

Clicks on the element in the webpage provided by the selector expression. Additionally it may have the following attributes:

| Attribute name | Description |
|----------------|---------------|
| visible | boolean attribute determining if before clicking the visibility of the element should be verified, default to true, set explicitly to false to skip this check |

#### open

Opens the given URL also initializing the Selenium WebDriver

| Attribute name | Description |
|----------------|-------------|
| url            | URL to open |

#### input

Inputs the given text in the given web input control.

| Attribute name | Description |
|----------------|-------------|
| value          | Value to input |
| clear | Boolean value determining whether to clear the field before input, defaults to true|
| tab | Boolean value determining whether to press tab after input, defaults to false|


#### maximize
Maximizes the browser window

### Custom actions
It's possible to implement own custom actions by extending the AtomicAction abstract class and adding @IsimoAction stereotype. You can follow the examples in the Java code of the standard actions.



