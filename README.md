# Ski rental Service

[![](https://img.shields.io/badge/Made%20with-Jakarta%20EE-1abc9c.svg)](https://jakarta.ee/release/10/)&nbsp;&nbsp;
[![](https://img.shields.io/badge/Build%20with-Gradle-green.svg)](https://gradle.org/)&nbsp;&nbsp;
[![](https://img.shields.io/badge/Web%20Server-Apache%20TomEE%209.1.2-brown.svg)](https://www.wildfly.org/)&nbsp;&nbsp;
[![](https://img.shields.io/badge/Packaging-WAR-yellow.svg)](https://en.wikipedia.org/wiki/WAR_(file_format))
&nbsp;&nbsp;
<br>
> More info about this project you will find [on my personal website](https://miloszgilga.pl/project/ski-rental-service)
> <br>
> See project demo at [ski.miloszgilga.pl](https://ski.miloszgilga.pl)

Enterprise-class application for managing a ski rental company. Created using Jakarta EE with EJB specifications. This
application can be run on any application server that supports Java 17, Jakarta EE 9, Jakarta Servlet API 6 and EJB,
such as Apache TomEE, Glassfish or JBoss/Wildfly.

I realize that the specification of servlets, JSP pages and monolithic applications is no longer a standard today. This
application was created only for learning purposes and familiarization with older "legacy" technologies.

## Table of content

* [Clone and install](#clone-and-install)
* [Prepare configuration and run](#prepare-configuration-and-run)
* [Manage Mailboxes via SSH](#manage-mailboxes-via-ssh)
* [Tech stack](#tech-stack)
* [Author](#author)
* [Project status](#project-status)
* [License](#license)

<a name="clone-and-install"></a>

## Clone and install

To install the program on your computer, use the command below (or use the build-in GIT system in your IDE environment):

```
$ git clone https://github.com/Milosz08/ski-rental-service
```

<a name="prepare-configuration-and-run"></a>

## Prepare configuration and run

1. Before run project, insert application properties in `.env` file:

```properties
SKI_DEV_S3_USERNAME=<Minio S3 access key for AWS SDK>
SKI_DEV_S3_PASSWORD=<Minio S3 secret key for AWS SDK>
SKI_DEV_MYSQL_USERNAME=<database username>
SKI_DEV_MYSQL_PASSWORD=<database password>
SKI_DEV_MAILHOG_USERNAME=<mailhog username, by default: mailhoguser (check .volumes/mail/mailhog-auth.txt file)>
SKI_DEV_MAILHOG_PASSWORD=<mailhog password, by default: root (check .volumes/mail/mailhog-auth.txt file)>
SKI_DEV_SSH_HOST=<optional, for creating email addresses in production>
SKI_DEV_SSH_LOGIN=<optional, for creating email addresses in production>
```

> [!IMPORTANT]
> If you run this project as standalone application without using Docker containers, this variables should be provided
> as exported *environment variables*.

2. To run project via Docker technology move to root project directory and type:

```
$ docker-compose up -d
```

This command will create 4 Docker containers:

* **ski-mailhog-smtp** - simple mail server, for development purposes,
* **ski-minio-s3** - Minio S3 file storage server,
* **ski-mysql-db** - MySQL application database,
* **ski-rental-app** - TomEE application server with exploded .war archive of this application

Default application ports (can be changed by editing `.env` file):
<table>
  <tr>
    <td>Application</td>
    <td>Port</td>
    <td>Description</td>
  </tr>
  <tr>
    <td rowspan="2">ski-mailhog-smtp</td>
    <td>7591</td>
    <td>SMTP server api port</td>
  </tr>
  <tr>
    <td>7592</td>
    <td>SMTP server web iterface</td>
  </tr>
  <tr>
    <td rowspan="2">ski-minio-s3</td>
    <td>7593</td>
    <td>Minio S3 api for AWS SDK for Java</td>
  </tr>
  <tr>
    <td>7594</td>
    <td>Minio S3 web interface</td>
  </tr>
  <tr>
    <td>ski-mysql-db</td>
    <td>7590</td>
    <td>MySQL database port</td>
  </tr>
  <tr>
    <td>ski-rental-app</td>
    <td>7595</td>
    <td>TomEE server with application</td>
  </tr>
</table>

> [!NOTE]
> Application by default run with `docker` profile. To change profile (`dev`, `docker` or `prod`),
> change `SKI_ENVIRONMENT` environment variable.

<a name="manage-mailboxes-via-ssh"></a>

## Manage mailboxes via SSH

Accessed in `src/main/resources/ssh/ssh.cfg.xml`, run commands only for `prod` environment.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<ssh-configuration>
  <property name="ssh.host">${SKI_SSH_HOST}</property>
  <property name="ssh.login">${SKI_SSH_LOGIN}</property>
  <property name="ssh.private-key.path">${SKI_SSH_PRIVATE_KEY_PATH}</property>
  <property name="ssh.known-hosts.path">${SKI_SSH_KNOWN_HOSTS_PATH}</property>
  <commands>
    <create-mailbox>
      <!-- create mailbox command -->
    </create-mailbox>
    <update-mailbox-password>
      <!-- change mailbox password command -->
    </update-mailbox-password>
    <delete-mailbox>
      <!-- delete mailbox command -->
    </delete-mailbox>
    <set-mailbox-capacity>
      <!-- set mailbox capacity command -->
    </set-mailbox-capacity>
  </commands>
</ssh-configuration>
```

<a name="tech-stack"></a>

## Tech stack

* Jakarta EE
* EJB
* JSP/JSTL
* Bootstrap + jQuery
* Hibernate + C3P0 connection pool + MySQL relational database
* Liquibase (database migrations)
* Docker technology

<a name="author"></a>

## Author

Created by Miłosz Gilga. If you have any questions about this application, send
message: [personal@miloszgilga.pl](mailto:personal@miloszgilga.pl).

<a name="project-status"></a>

## Project status

Project is finished.

<a name="license"></a>

## License

This application is on Apache 2.0 License.
