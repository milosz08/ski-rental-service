# SkiRental Service Web Application
[![Generic badge](https://img.shields.io/badge/Made%20with-Jakarta%20EE%2010-1abc9c.svg)](https://jakarta.ee/release/10/)&nbsp;&nbsp;
[![Generic badge](https://img.shields.io/badge/Build%20with-Gradle-green.svg)](https://gradle.org/)&nbsp;&nbsp;
[![Generic badge](https://img.shields.io/badge/Web%20Server-Wildfly/JBOSS%20-brown.svg)](https://www.wildfly.org/)&nbsp;&nbsp;
[![Generic badge](https://img.shields.io/badge/Packaging-WAR-yellow.svg)](https://en.wikipedia.org/wiki/WAR_(file_format))&nbsp;&nbsp;
<br><br>
Simple enterprise-class application for managing a ski rental company. The software is closed (only people in the enterprise can use it). This project was made for a subject in college programming course. Created only for learning purposes. Jakarta EE and EJB specifications were used in the development. This application can be run on an application server that supports Jakarta EE and EJB, such as JBOSS/Wildfly or GlassFish.

> DISCLAIMER: I realize that the specification of servlets, JSP pages and monolithic applications is no longer a standard today, but for the purpose of a simple project, it is just fine.

## Table of content
* [Clone and install](#clone-and-install)
* [Prepare runtime configuration](#prepare-runtime-configuration)
* [Manage Mailboxes via SSH](#manage-mailboxes-via-ssh)
* [Application stack](#application-stack)
* [Project status](#project-status)

<a name="clone-and-install"></a>
## Clone and install

To install the program on your computer, use the command below (or use the build-in GIT system in your IDE environment):
```
$ git clone https://github.com/Milosz08/SUoT_SkiRental_Service ski-rental-service
```

<a name="prepare-runtime-configuration"></a>
## Prepare runtime configuration
1. Create private and public key (with `known_hosts.dat`) step by step:
* generate key:
```
$ ssh-keygen -t rsa
```
* move key to SSH server:
```
$ ssh-copy-id -i ~/.ssh/id_rsa.pub login@server
```
* put `id_rsa` and `known_hosts.dat` files in root context of application
2. Before you run the application, create `.env` file in ROOT context of project and fill with database and SMTP server connection details:
```properties
# database credentials
DB_URL              = jdbc:[dbType]://[dbHost]:[dbPort]/[dbName]
DB_USERNAME         = xxxxx -> insert here database username 
DB_PASSWORD         = xxxxx -> insert here database password
# mail server account credentials
SMTP_HOST           = xxxxx -> insert here SMTP mail server address
SMTP_USER           = xxxxx -> ex. noreply@example.pl
SMTP_PASS           = xxxxx -> insert here SMTP mail server password
# ssh server credentials
SSH_HOST            = xxxxx -> SSH host
SSH_LOGIN           = xxxxx -> SSH login
```
3. Create directory in `wildfly-27.0.1.Final/standalone/data` with name `ski-rental-service` and create another directory in previous directory with name `bar-codes` and move two sample codes from directory `sample-bar-codes` in project root. 
4. To start application, download Wildfly/JBOSS application server from here:
* [download for Windows/x86](https://github.com/wildfly/wildfly/releases/download/27.0.1.Final/wildfly-27.0.1.Final.zip)
* [download for MacOS/UNIX](https://github.com/wildfly/wildfly/releases/download/27.0.1.Final/wildfly-27.0.1.Final.tar.gz)
5. Create new user in Wildfly application server by running `add-user.sh` (for UNIX) or `add-user.bat` (for windows) in `bin` directory in Wildfly application server folder.
6. Configure runtime server in your IDE [example for IntellijIDEA](https://medium.com/geekculture/how-to-configure-and-deploy-webapps-with-wildfly-application-server-in-intellij-idea-f104a6c2a0db).
7. Run grade build with `copyRsa` task and `processResources` and initiate application (via Gradle Wrapper):
```
$ ./gradlew run copyRsa            # move private key and known_hosts.dat file into build/resources directory
$ ./gradlew run processResources   # replacement values from .env file into xml configuration files
```
8. By default, application listen on `127.0.0.1:8080` and Wildfly admin dashboard is available on `127.0.0.1:9990`.

<a name="manage-mailboxes-via-ssh"></a>
## Manage mailboxes via SSH
in `src/main/resources/ssh/ssh.cfg.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>

<ssh-configuration>
    <properties>
        <ssh-host>@ssh_host@</ssh-host>
        <ssh-login>@ssh_login@</ssh-login>
        <ssh-rsa>/ssh/id_rsa</ssh-rsa>
        <ssh-knownHosts>/ssh/known_hosts.dat</ssh-knownHosts>
    </properties>
    <commands>
        <command executableFor="create-mailbox">
            <!-- insert here command from your SSH server available to create mailbox with email and password properties -->
        </command>
        <command executableFor="update-mailbox-password">
            <!-- insert here command from your SSH server available to update mailbox with email and newPassword properties -->
        </command>
        <command executableFor="delete-mailbox">
            <!-- insert here command from your SSH server available to delete mailbox with email propery -->
        </command>
        <command executableFor="set-mailbox-capacity">
            <!-- insert here command from your SSH server available to set maximal mailbox space with email propery -->
        </command>
    </commands>
</ssh-configuration>
```

<a name="application-stack"></a>
## Application stack
* [Jakarta EE](https://jakarta.ee/release/10/)
* [EJB (Enterprise JavaBean)](https://www.oracle.com/java/technologies/enterprise-javabeans-technology.html)
* [JSP/JSTL](https://www.oracle.com/java/technologies/jspt.html)
* [Bootstrap](https://getbootstrap.com/)
* [MySQL](https://www.mysql.com/)
* [Hibernate](https://hibernate.org/)
* [Liquibase](https://www.liquibase.org/)

<a name="project-status"></a>
## Project status
Project is still in development.
