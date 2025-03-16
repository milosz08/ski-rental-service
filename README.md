# Ski rental service

[[Docker image](https://hub.docker.com/r/milosz08/ski-rental-service-app)] |
[[About project](https://miloszgilga.pl/project/ski-rental-service)]

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
* [License](#license)

## Clone and install

To install the program on your computer, use the command below (or use the build-in GIT system in your IDE environment):

```
$ git clone https://github.com/milosz08/ski-rental-service
```

## Prepare configuration and run

1. Before run project, insert application properties into `.env` file (based on `example.env` file):

```properties
# required variables
SKI_SSH_ENABLED=<determined, if SSH service is enabled/disabled>
SKI_S3_USERNAME=<Minio S3 access key for AWS SDK>
SKI_S3_PASSWORD=<Minio S3 secret key for AWS SDK>
SKI_MYSQL_USERNAME=<database username>
SKI_MYSQL_PASSWORD=<database password>
SKI_MAILHOG_USERNAME=<mailhog username, by default: mailhoguser (check .volumes/mail/mailhog-auth.txt file)>
SKI_MAILHOG_PASSWORD=<mailhog password, by default: root (check .volumes/mail/mailhog-auth.txt file)>
# optional variables
SKI_SSH_HOST=<optional, SSH host for managing mail accounts>
SKI_SSH_LOGIN=<optional, SSH login for managing mail accounts>
SKI_SSH_PRIVATE_KEY_PATH=<optional, SSH private key file path for managing mail accounts>
SKI_SSH_KNOWN_HOSTS_PATH=<optional, SSH known hosts file path for managing mail accounts>
SKI_SSH_CREATE_MAILBOX_CMD=<optional, see Manage mailboxes via SSH section>
SKI_SSH_UPDATE_MAILBOX_CMD=<optional, see Manage mailboxes via SSH section>
SKI_SSH_DELETE_MAILBOX_CMD=<optional, see Manage mailboxes via SSH section>
SKI_SSH_SET_MAILBOX_CAPACITY_CMD=<optional, see Manage mailboxes via SSH section>
```

> [!IMPORTANT]
> If you run this project as standalone application without using Docker containers, this variables
> should be provided as exported *environment variables*.

2. To run project via Docker technology move to root project directory and type:

```
$ docker-compose up -d
```

This command will create 4 Docker containers:

<table>
  <tr>
    <td>Container</td>
    <td>Port</td>
    <td>Description</td>
  </tr>
  <tr>
    <td rowspan="2">ski-mailhog-smtp</td>
    <td><a href="http://localhost:7591">7591</a></td>
    <td>SMTP server api port</td>
  </tr>
  <tr>
    <td><a href="http://localhost:7592">7592</a></td>
    <td>SMTP server web iterface</td>
  </tr>
  <tr>
    <td rowspan="2">ski-minio-s3</td>
    <td><a href="http://localhost:7593">7593</a></td>
    <td>Minio S3 api for AWS SDK for Java</td>
  </tr>
  <tr>
    <td><a href="http://localhost:7594">7594</a></td>
    <td>Minio S3 web interface</td>
  </tr>
  <tr>
    <td>ski-mysql-db</td>
    <td><a href="http://localhost:7590">7590</a></td>
    <td>MySQL database port</td>
  </tr>
  <tr>
    <td>ski-rental-app</td>
    <td><a href="http://localhost:7595">7595</a></td>
    <td>TomEE server with application</td>
  </tr>
</table>

Default application ports (can be changed by editing `.env` file).

> [!IMPORTANT]
> When `ski-minio-s3` container is up, go to container console and invoke `$ ./put-files` script to
> move existing barcodes to S3 object storage.

> [!NOTE]
> Application by default run with `docker` profile. To change profile (`dev`, `docker` or `prod`),
> change `SKI_ENVIRONMENT` environment variable.

## Manage mailboxes via SSH

Before read this section, check if you have enabled SSH:

```properties
SKI_SSH_ENABLED=true
```

To manage mailboxes via SSH, you must have ssh host, login, private key (application not provided connections via
password) and known-hosts file.

```properties
SKI_SSH_HOST=<optional, SSH host for managing mail accounts>
SKI_SSH_LOGIN=<optional, SSH login for managing mail accounts>
SKI_SSH_PRIVATE_KEY_PATH=<optional, SSH private key file path for managing mail accounts>
SKI_SSH_KNOWN_HOSTS_PATH=<optional, SSH known hosts file path for managing mail accounts>
```

You must provide following commands as single string patterns. Additionally, you can use some dynamically passed
variables inside this commands:

**SKI_SSH_CREATE_MAILBOX_CMD**:

* `email` - creating email account address
* `password` - creating email account password

**SKI_SSH_UPDATE_MAILBOX_CMD**:

* `email` - updating email account address
* `newPassword` - updating email account new password

**SKI_SSH_DELETE_MAILBOX_CMD**:

* `email` - deleting email account address

**SKI_SSH_SET_MAILBOX_CAPACITY_CMD**:

* `email` - email account address where capacity is set

Example commands:

```properties
SKI_SSH_CREATE_MAILBOX_CMD="email create -a $[email] -p $[password]"
SKI_SSH_UPDATE_MAILBOX_CMD="email update -a $[email] -np $[newPassword]"
SKI_SSH_DELETE_MAILBOX_CMD="email delete -a $[email]"
SKI_SSH_SET_MAILBOX_CAPACITY_CMD="email quota-set -a $[email] -q 5MB"
```

By default, application will not connected to SSH (if you not provided any SSH environment variables).

## Tech stack

* Java (Jakarta) EE 9,
* EJB (Enterprise Java Beans),
* MySQL relational database system,
* JSP (views) + JSTL,
* Jakarta Mail Api (mail sender) + Freemarker (templates),
* Hibernate (ORM system) + C3P0 (connection pool) + Liquibase (database migrations),
* Bootstrap, jQuery, PopperJS,
* Barcode4J - bar codes generator,
* iText - pdf documents generator,
* Apache TomEE application server with Docker technology.

## Author

Created by Miłosz Gilga. If you have any questions about this application, send
message: [miloszgilga@gmail.com](mailto:miloszgilga@gmail.com).

## License

This application is on Apache 2.0 License.
