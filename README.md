# SkiRental Service Web Application
[![Generic badge](https://img.shields.io/badge/Made%20with-Jakarta%20EE%2010-1abc9c.svg)](https://jakarta.ee/release/10/)&nbsp;&nbsp;
[![Generic badge](https://img.shields.io/badge/Build%20with-Gradle-green.svg)](https://gradle.org/)&nbsp;&nbsp;
[![Generic badge](https://img.shields.io/badge/Web%20Server-Wildfly/JBOSS%20-brown.svg)](https://www.wildfly.org/)&nbsp;&nbsp;
[![Generic badge](https://img.shields.io/badge/Packaging-WAR-yellow.svg)](https://en.wikipedia.org/wiki/WAR_(file_format))&nbsp;&nbsp;
<br><br>
Simple enterprise-class application for managing a ski rental company. The software is closed (only people in the enterprise can use it). The project was made for a subject in college. Created only for learning purposes. Jakarta EE and EJB specifications were used in the development. This application can be run on an application server that supports Jakarta EE and EJB, such as JBOSS/Wildfly or GlassFish.

> DISCLAIMER: I realize that the very specification of servlets, JSP pages and monolithic applications is no longer a standard today, but for the purpose of a simple project, it is just fine.

## Table of content
* [Application stack](#application-stack)
* [Clone and install](#clone-and-install)
* [Prepare runtime configuration](#prepare-runtime-configuration)
* [Project status](#project-status)

<a name="application-stack"></a>
## Application stack
* [Jakarta EE](https://jakarta.ee/release/10/)
* [EJB (Enterprise JavaBean)](https://www.oracle.com/java/technologies/enterprise-javabeans-technology.html)
* [JSP/JSTL](https://www.oracle.com/java/technologies/jspt.html)
* [Bootstrap](https://getbootstrap.com/)
* [MySQL](https://www.mysql.com/)
* [Hibernate](https://hibernate.org/)
* [Liquibase](https://www.liquibase.org/)

<a name="clone-and-install"></a>
## Clone and install

To install the program on your computer, use the command below (or use the build-in GIT system in your IDE environment):
```
$ git clone https://github.com/Milosz08/SUoT_SkiRental_Service ski-rental-service
```

<a name="prepare-runtime-configuration"></a>
## Prepare runtime configuration
1. Before you run the application, create `.env` file in ROOT context of project and filled with database connection details:
```properties
DB_URL             = jdbc:[dbType]://[dbHost]:[dbPort]/[dbName]
DB_USERNAME        = xxxxx -> insert here database username 
DB_PASSWORD        = xxxxx -> insert here database password
```
2. To start application, download Wildfly/JBOSS application server from here:
* [download for Windows/x86](https://github.com/wildfly/wildfly/releases/download/27.0.1.Final/wildfly-27.0.1.Final.zip)
* [download for MacOS/UNIX](https://github.com/wildfly/wildfly/releases/download/27.0.1.Final/wildfly-27.0.1.Final.tar.gz)
3. Create new user in Wildfly application server by running `add-user.sh` (for UNIX) or `add-user.bat` (for windows) in `bin` directory in Wildfly application server folder. 
4. Configure runtime server in your IDE [example for IntellijIDEA](https://medium.com/geekculture/how-to-configure-and-deploy-webapps-with-wildfly-application-server-in-intellij-idea-f104a6c2a0db).
5. By default, application listen on `127.0.0.1:8080` and Wildfly admin dashboard is available on `127.0.0.1:9990`.

<a name="project-status"></a>
## Project status
Project is still in development.
