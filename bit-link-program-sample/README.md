# bit-link-program-sample

Sample RESTful web application for deployment to a Liberty JVM server in CICS. `SampleServlet` takes an Integer temperature and uses the JCICSX API to link to the `CONVERT` COBOL program to convert it from Celcius to Fahrenheit. The link uses BIT containers for both the input and the output. 


## Supporting files
* [`CONVERT.cbl`](src/main/cobol/CONVERT.cbl) - A sample CICS COBOL that returns a converted temperature input using channels and containers.
Download and compile the supplied COBOL program CONVERT and deploy into CICS.


## Pre-reqs

* CICS TS V5.1 or later, due to the usage of the `getString()` methods.
* Java SE 1.7 or later on the z/OS system
* Java SE 1.7 or later on the workstation


## Setup

This sample is a Maven project, which uses the [CICS Bundle Maven plugin](https://github.com/IBM/cics-bundle-maven) to package the web application in a CICS bundle and deploy this to CICS. This requires the CICS bundle deployment API to be enabled in CICS as a [prerequisite](https://www.ibm.com/support/knowledgecenter/en/SSGMCP_5.6.0/configuring/cmci/config-bundle-api.html). Alternatively, if you aren't using Maven, you could take the source from this project and use one of the other methods of deploying the application to CICS such as creating a CICS bundle project in CICS Explorer and adding the source as a dynamic web project include. 

To run the sample as-is, fill out values in the configuration block in pom.xml
   ```xml
   <configuration>
     <defaultjvmserver>DFHWLP</defaultjvmserver>
     <url>http://yourcicsurl.com:9080</url>
     <username>${cics-user-id}</username>
     <password>${cics-password}</password>
     <bunddef>DEMOBUNDLE</bunddef>
     <csdgroup>BAR</csdgroup>
     <cicsplex>CICSEX56</cicsplex>
     <region>IYCWEMW2</region>
   </configuration>
   ```
Running `mvn clean install` will package the web application into a CICS Bundle and install and enable it. 
You can then view the web server `http://yourcicsurl.com:9080/bit-link-program-sample-0.0.1-SNAPSHOT/`

#### Using a web browser you can issue the following HTTP GET requests

* http://yourcicsurl.com:9080/bit-link-program-sample-0.0.1-SNAPSHOT/75
where 75 can be any integer and represents a temperate in Celcius. 
This will invoke the `SampleServlet` class and return the following response with the temperature converted to Fahrenheit:

`Returned from link to 'CONVERT'. 75 degrees celcius = 167 degrees fahrenheit`

