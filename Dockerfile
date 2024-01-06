FROM gradle:7.5.1-jdk17 AS build

WORKDIR ski-rental-project

COPY . .

RUN gradle war
RUN cp build/libs/ROOT.war /
RUN rm -rf /ski-rental-project

FROM tomcat:10.1.8

LABEL maintainer="Miłosz Gilga <personal@miloszgilga.pl>"

COPY --from=build /ROOT.war /usr/local/tomcat/webapps
