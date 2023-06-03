# SkiRental Service Web Application
[![](https://img.shields.io/badge/Made%20with-Jakarta%20EE%2010-1abc9c.svg)](https://jakarta.ee/release/10/)&nbsp;&nbsp;
[![](https://img.shields.io/badge/Build%20with-Gradle-green.svg)](https://gradle.org/)&nbsp;&nbsp;
[![](https://img.shields.io/badge/Web%20Container-Apche%20Tomcat%2010.1.8-brown.svg)](https://www.wildfly.org/)&nbsp;&nbsp;
[![](https://img.shields.io/badge/Packaging-WAR-yellow.svg)](https://en.wikipedia.org/wiki/WAR_(file_format))&nbsp;&nbsp;
<br>
> More info about this project you will find [on my personal website](https://miloszgilga.pl/project/ski-rental-service)
> <br>
> See project demo at [ski.miloszgilga.pl](https://ski.miloszgilga.pl)

Simple enterprise-class application for managing a ski rental company. The software is closed (only people in the 
enterprise can use it). This project was made for a subject in college programming course. Created only for learning 
purposes. Jakarta EE specifications were used in the development. This application can be run on any application 
servlet container that supports Jakarta EE 9 and Jakarta Servlet API 6, such as Apache Tomcat or Jetty.

I realize that the specification of servlets, JSP pages and monolithic applications is no longer a standard today. This 
application was created only for learning purposes and familiarization with older "legacy" technologies.

## Table of content
* [Clone and install](#clone-and-install)
* [Prepare configuration and run](#prepare-configuration-and-run)
* [CI/CD Tomcat deployable script](#ci-cd-tomcat-deployable-script)
* [Manage Mailboxes via SSH](#manage-mailboxes-via-ssh)
* [Tech stack](#tech-stack)
* [Author](#author)
* [Project status](#project-status)

<a name="clone-and-install"></a>
## Clone and install
To install the program on your computer, use the command below (or use the build-in GIT system in your IDE environment):
```
$ git clone https://github.com/Milosz08/ski-rental-service
```

<a name="prepare-configuration-and-run"></a>
## Prepare configuration and run
1. Create private and public key (with `known_hosts.dat`) step by step (for managing mailboxes via SSH):
* generate key:
```
$ ssh-keygen -t rsa
```
* change name and move into project directory:
```
$ mv ~/.ssh/known_hosts ~/.ssh/known_hosts.dat
$ cp ~/.ssh/id_rsa ~/.ssh/known_hosts.dat [project base dir]
```
* move key to SSH server:
```
$ ssh-copy-id -i ~/.ssh/id_rsa.pub login@server
```

2. Before you run the application, create `.env` file via this command:
```
$ grep -vE '^\s*$|^#' .env.sample > .env
```
and fill with database, SMTP and SSH server connection details:
```properties
# database credentials
DB_URL                  = jdbc:[dbType]://[dbHost]:[dbPort]/[dbName]
DB_USERNAME             = xxxxx -> insert here database username 
DB_PASSWORD             = xxxxx -> insert here database password
# mail server account credentials
SMTP_HOST               = xxxxx -> insert here SMTP mail server address
SMTP_USER               = xxxxx -> ex. noreply@example.pl
SMTP_PASS               = xxxxx -> insert here SMTP mail server password
# ssh server credentials
SSH_HOST                = xxxxx -> SSH host
SSH_LOGIN               = xxxxx -> SSH login (username)
```
3. Generate `.war` file via gradle task:
```
$ ./gradlew war
```
4. To start application, download, unpack and install Tomcat servlet container 10.1.8:
```
$ curl -O https://dlcdn.apache.org/tomcat/tomcat-10/v10.1.8/bin/apache-tomcat-10.1.8.tar.gz
$ tar xfz apache-tomcat-10.1.8.tar.gz
$ rm ~/apache-tomcat-10.1.8.tar.gz
$ rm -r ~/apache-tomcat-10.1.8/webapps/*
```
4. Run Tomcat via:
```
$ ~/apache-tomcat-10.1.8/bin/startup.sh
```
5. Move created `.war` file into Tomcat webapps directory
```
$ cp [project path]/build/libs/ROOT.war ~/apache-tomcat-10.1.8/webapps/
```
6. Now Tomcat will automatically unpack the archive and run the application (visible in CATALINA logs). If everything is
successful, application should be available at `http://127.0.0.1:8080`.

<a name="ci-cd-tomcat-deployable-script"></a>
## CI/CD Tomcat deployable script
The application has a relatively easy deployment procedure using a gradle script to a LINUX server. Follow the tutorial
below.

1. (on remote host) Install Tomcat 10.1.8:
```
$ cd /opt
$ curl -O https://dlcdn.apache.org/tomcat/tomcat-10/v10.1.8/bin/apache-tomcat-10.1.8.tar.gz
$ tar xfz apache-tomcat-10.1.8.tar.gz
$ rm apache-tomcat-10.1.8.tar.gz
$ rm -r apache-tomcat-10.1.8/webapps/*
```
2. (on remote host) Create service for Tomcat:
```
sudo nano /etc/systemd/system/tomcat.service
```
and fill file with propriet values:
```properties
[Unit]
Description=Apache Tomcat Web Application Container
After=network.target

[Service]
Type=forking

Environment=JRE_HOME=/usr
Environment=CATALINA_PID=/opt/apache-tomcat-10.1.8/temp/tomcat.pid
Environment=CATALINA_HOME=/opt/apache-tomcat-10.1.8
Environment=CATALINA_BASE=/opt/apache-tomcat-10.1.8
Environment=CATALINA_OPTS=-Xms512m -Xmx1024m

ExecStart=/opt/apache-tomcat-10.1.8/bin/startup.sh
ExecStop=/opt/apache-tomcat-10.1.8/bin/shutdown.sh

User=root
UMask=0007
RestartSec=10
Restart=always

[Install]
WantedBy=multi-user.target
```
3. (on remote host) Refresh system services and run:
```
$ sudo systemctl daemon-reload
$ sudo systemctl start tomcat
$ sudo systemctl enable tomcat
```
4. (on local host) Create private and public key with PEM specifications (for CI/CD Tomcat deployment):
* generate key:
```
$ ssh-keygen -t rsa -m PEM
```
> NOTE: You must point to a directory other than `~/.ssh`, so that the keys are not overwritten (for example `~/.ssh-pem`).
* move into project directory:
```
$ mv id_rsa id_rsa_pem
$ cp id_rsa_pem [project base dir]
```
* move key to CI/CD server (where you have Tomcat server):
```
$ ssh-copy-id -i ~/.ssh-pem/id_rsa.pub login@server
```
5. (on local host) Insert in created `.env` file following properties:
```properties
# CI/CD tomcat deploy
CICD_HOST               = xxxxx -> SSH host
CICD_USER               = xxxxx -> SSH login (username)
CICD_CATALINA_BASE      = xxxxx -> CATALINA_BASE, ex. /opt/apache-tomcat-10.1.8/
CICD_PROJECT_BASE       = xxxxx -> application base dir, ex. webapps/
```
6. (on local host) To deploy application on remote Tomcat server, type:
```
$ ./gradlew deployToTomcat
```

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

<a name="tech-stack"></a>
## Tech stack
* Jakarta EE
* JSP/JSTL
* Bootstrap + jQuery
* Hibernate + C3P0 connection pool + MySQL relational database
* Liquibase (database migrations)

<a name="author"></a>
## Author
Created by Miłosz Gilga. If you have any questions about this application, send message: [personal@miloszgilga.pl](mailto:personal@miloszgilga.pl).

<a name="project-status"></a>
## Project status
Project is finished.
