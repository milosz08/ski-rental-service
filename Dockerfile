FROM eclipse-temurin:17-jdk-alpine AS build

ENV BUILD_DIR=/build/ski-rental-service

RUN mkdir -p $BUILD_DIR
WORKDIR $BUILD_DIR

# copy only gradle-based resources for optimized caching
COPY gradle $BUILD_DIR/gradle
COPY gradlew $BUILD_DIR/gradlew
COPY build.gradle $BUILD_DIR/build.gradle
COPY settings.gradle $BUILD_DIR/settings.gradle

RUN chmod +x $BUILD_DIR/gradlew
RUN cd $BUILD_DIR

RUN ./gradlew dependencies --no-daemon

# copy rest of resources
COPY docker $BUILD_DIR/docker
COPY src $BUILD_DIR/src

RUN ./gradlew clean --no-daemon
RUN ./gradlew war --no-daemon

FROM tomee:9.1.2-webprofile AS run

ENV BUILD_DIR=/build/ski-rental-service
ENV ENTRY_DIR=/usr/local/tomee/webapps
ENV WAR_NAME=ROOT.war

RUN rm -rf $ENTRY_DIR/*
COPY --from=build $BUILD_DIR/build/libs/$WAR_NAME $ENTRY_DIR

LABEL maintainer="Miłosz Gilga <miloszgilga@gmail.com>"

EXPOSE 8080
