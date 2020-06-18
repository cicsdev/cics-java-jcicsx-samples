# CHAR Link to Program Sample

This sample shows how you can use the JCICSX API to LINK to a CICS Program, passing through a CHAR container, and returning CHAR data as the output of the Program.

This web application takes the string "Hello from Java" and links to the EDUCHAN program with the channel `MYCHANNEL` and an input container called `INPUTDATA`. The EDUCHAN program reads the data from the input container and reverses the string. The output string is placed in the output container `OUTPUTDATA`, read in by the servlet and displayed on the web page.

## Pre-reqs

* CICS TS V5.6 or later
* Java SE 1.8 or later on the z/OS system
* Java SE 1.8 or later on the workstation

## Setup

This application links to the COBOL program EDUCHAN, which can be found in `src/main/cobol`.  
Download and compile the supplied COBOL program EDUCHAN and deploy into CICS.

This sample is a Maven project, which uses the [CICS Bundle Maven plugin](https://github.com/IBM/cics-bundle-maven) to package the web application in a CICS bundle and deploy this to CICS. This requires the CICS bundle deployment API to be enabled in CICS as a [prerequisite](https://www.ibm.com/support/knowledgecenter/en/SSGMCP_5.6.0/configuring/cmci/config-bundle-api.html). Alternatively, if you aren't using Maven, you can use [Gradle](https://github.com/IBM/cics-bundle-gradle) or you could take the source from this project and use one of the other methods of deploying the application to CICS such as creating a CICS bundle project in CICS Explorer and adding the source as a dynamic web project include. 

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
You can then view the web server `http://yourcicsurl.com:9080/char-link-program-sample-0.0.1-SNAPSHOT/`
You should see the message `Hello world! Returned from link to 'EDUCHAN' with a text response of ' avaJ morf olleH'`
