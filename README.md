# Vector Space Search Engine

This is a search engine that I created as part of a university course assignment.  At the moment I am not planning on improving or maintaining it.

## Getting started

The project is maven-based.  Have a look in the pom.xml to make note of how the Mysql database is setup.  It is re-created during the mvn install phase.  But depending on your mysql setup, you may need to modify the pom steps.  Also, I am storing my MySql credentials in the settings.xml.

The manual part of the setup is the web site.  Currently I don't have that automated in maven.  So you'll need to copy all the content inside src/main/web into your apache web directory.  The Web Crawler is hard coded to look for the various PHP services at http://localhost/services/... .php.  So you can either change that in the code, or just point apache directly at the above-mentioed src/main/web.  

My full project report, ProjectReport.pdf, can be found at the root of this project. 
