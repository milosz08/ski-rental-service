FROM gradle:7.5.1-jdk17 AS build

WORKDIR ski-rental-project

COPY . .

RUN gradle war
RUN cp build/libs/ROOT.war /
RUN rm -rf /ski-rental-project

FROM tomee:9.1.2-webprofile AS run

LABEL maintainer="Miłosz Gilga <personal@miloszgilga.pl>"

RUN rm -rf /usr/local/tomee/webapps/*
COPY --from=build /ROOT.war /usr/local/tomee/webapps
