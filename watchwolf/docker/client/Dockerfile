FROM ubuntu:focal

# Contents migrated to docker from:
# https://raw.githubusercontent.com/watch-wolf/WatchWolf/main/WatchWolfSetup.sh

ENV clients_manager_path="/WatchWolf/ClientsManager"
ENV branch="master"

# Fix apt-get update error according to: https://stackoverflow.com/a/71982514
# But it is only needed if > ubuntu 20, but since there are other errors then aswell, go with ubuntu 20 (focal)
#RUN sed -i -e 's/^APT/# APT/' -e 's/^DPkg/# DPkg/' \
#      /etc/apt/apt.conf.d/docker-clean

RUN apt-get update
RUN apt-get install -y wget grep git curl jq mawk dos2unix
RUN apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    rm -f /var/cache/apt/archives/*.deb && \
    rm -f /var/cache/apt/archives/partial/*.deb && \
    rm -f /var/cache/apt/*.bin
RUN git clone -b $branch https://github.com/rogermiranda1000/WatchWolf-Client.git $clients_manager_path

# TODO

COPY fs/ /

ENTRYPOINT /docker/entrypoint.sh