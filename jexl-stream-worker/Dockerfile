FROM ubuntu:jammy

# with ubuntu:impish, get an error on apt update
# Problem executing scripts APT::Update::Post-Invoke 'rm -f /var/cache/apt/archives/*.deb /var/cache/apt/archives/partial/*.deb /var/cache/apt/*.bin || true'

# set locale¨
ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8

# set timezone, see: https://stackoverflow.com/a/48430880/795671
ENV TZ=Europe/Stockholm
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Always run update when changing package list, see https://docs.docker.com/develop/develop-images/dockerfile_best-practices/
RUN apt update ; echo 'editthistoforcerun3'

# install curl
RUN apt install -y curl \
    openjdk-17-jre

RUN java --version

# Lines above are common to all Dockerfiles in the project.
######

COPY build/libs/hom-impl-2.jexl-stream-worker-1.0-SNAPSHOT.jar output.jar


# ENTRYPOINT ["java","-jar","output.jar"]

# This is left so that different main classes can be specified, and different properties set
ENTRYPOINT ["java"]