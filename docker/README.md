# Ski rental service

Enterprise-class application for managing a ski rental company. Created using Jakarta EE with EJB specifications. This
application can be run on any application server that supports Java 17, Jakarta EE 9, Jakarta Servlet API 6 and EJB,
such as Apache TomEE, Glassfish or JBoss/Wildfly.

I realize that the specification of servlets, JSP pages and monolithic applications is no longer a standard today. This
application was created only for learning purposes and familiarization with older "legacy" technologies.

[[GitHub repository](https://github.com/milosz08/ski-rental-service)] |
[[About project](https://miloszgilga.pl/project/ski-rental-service)]

## Build image

```bash
docker build -t milosz08/ski-rental-service-app .
```

## Create container

* Using command:

```bash
docker run -d \
  --name ski-rental-service-app \
  -p 8080:8080 \
  -p SKI_ENVIRONMENT=<dev, prod or docker> \
  -p SKI_SYSTEM_VERSION=<version (show in emails and footer)> \
  -p SKI_SSH_ENABLED=<determine, if SSH is enabled/disabled> \
  -p SKI_MYSQL_URL=<jdbc connection string> \
  -p SKI_MYSQL_USERNAME=<database username> \
  -p SKI_MYSQL_PASSWORD=<database password> \
  -p SKI_SMTP_HOST=<mail server host> \
  -p SKI_SMTP_PORT=<mail server port> \
  -p SKI_SMTP_USERNAME=<mail server username> \
  -p SKI_SMTP_PASSWORD=<mail server password> \
  -p SKI_SMTP_DOMAIN=<mail server domain> \
  -p SKI_S3_HOST=<S3 storage host> \
  -p SKI_S3_ACCESS_KEY=<S3 access key> \
  -p SKI_S3_SECRET_KEY=<S3 secret key> \
  -p SKI_S3_REGION=<S3 region, ex. eu-central-1> \
  -p SKI_S3_PATH_STYLE_ACCESS_ENABLED=<enabled path style access (with slash, set to true for Minio)> \
  -p SKI_SSH_HOST=<optional, SSH host, mailboxes management> \
  -p SKI_SSH_LOGIN=<optional, SSH login, mailboxes management> \
  -p SKI_SSH_PRIVATE_KEY_PATH=<optional, SSH private key file path, mailboxes management> \
  -p SKI_SSH_KNOWN_HOSTS_PATH=<optional, SSH known hosts file path, mailboxes management> \
  -p SKI_SSH_CREATE_MAILBOX_CMD=<optional, create mailbox command> \
  -p SKI_SSH_UPDATE_MAILBOX_CMD=<optional, update mailbox command> \
  -p SKI_SSH_DELETE_MAILBOX_CMD=<optional, delete mailbox command> \
  -p SKI_SSH_SET_MAILBOX_CAPACITY_CMD=<optional, set mailbox quota command> \
  -p SKI_SERVER_HOME=<self refer, ex. https://ski-rental-service.com> \
  milosz08/ski-rental-service-app:latest
```

* Using `docker-compose.yml` file:

```yaml
services:
  ski-rental-service-app:
    container_name: ski-rental-service-app
    image: milosz08/ski-rental-service-app:latest
    ports:
      - '8080:8080'
    environment:
      SKI_ENVIRONMENT: <dev, prod or docker>
      SKI_SYSTEM_VERSION: <version (show in emails and footer)>
      SKI_SSH_ENABLED: <determine, if SSH is enabled/disabled>
      SKI_MYSQL_URL: <jdbc connection string>
      SKI_MYSQL_USERNAME: <database username>
      SKI_MYSQL_PASSWORD: <database password>
      SKI_SMTP_HOST: <mail server host>
      SKI_SMTP_PORT: <mail server port>
      SKI_SMTP_USERNAME: <mail server username>
      SKI_SMTP_PASSWORD: <mail server password>
      SKI_SMTP_DOMAIN: <mail server domain>
      SKI_S3_HOST: <S3 storage host>
      SKI_S3_ACCESS_KEY: <S3 access key>
      SKI_S3_SECRET_KEY: <S3 secret key>
      SKI_S3_REGION: <S3 region, ex. eu-central-1>
      SKI_S3_PATH_STYLE_ACCESS_ENABLED: <enabled path style access (with slash, set to true for Minio)>
      SKI_SSH_HOST: <optional, SSH host, mailboxes management>
      SKI_SSH_LOGIN: <optional, SSH login, mailboxes management>
      SKI_SSH_PRIVATE_KEY_PATH: <optional, SSH private key file path, mailboxes management>
      SKI_SSH_KNOWN_HOSTS_PATH: <optional, SSH known hosts file path, mailboxes management>
      SKI_SSH_CREATE_MAILBOX_CMD: <optional, create mailbox command>
      SKI_SSH_UPDATE_MAILBOX_CMD: <optional, update mailbox command>
      SKI_SSH_DELETE_MAILBOX_CMD: <optional, delete mailbox command>
      SKI_SSH_SET_MAILBOX_CAPACITY_CMD: <optional, set mailbox quota command>
      SKI_SERVER_HOME: <self refer, ex. https://ski-rental-service.com>
    networks:
      - ski-rental-service-network

  # other containers...

networks:
  ski-rental-service-network:
    driver: bridge
```

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

## Author

Created by Miłosz Gilga. If you have any questions about this application, send message:
[miloszgilga@gmail.com](mailto:miloszgilga@gmail.com).

## License

This application is on Apache 2.0 License.
