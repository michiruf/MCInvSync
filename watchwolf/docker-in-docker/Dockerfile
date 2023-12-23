FROM ubuntu:focal

# Fix apt-get update error according to: https://stackoverflow.com/a/71982514
# But it is only needed if > ubuntu 20, but since there are other errors then aswell, go with ubuntu 20 (focal)
#RUN sed -i -e 's/^APT/# APT/' -e 's/^DPkg/# DPkg/' \
#      /etc/apt/apt.conf.d/docker-clean

RUN apt-get update
RUN apt-get install -y wget grep git curl jq mawk dos2unix
#RUN apt-get install -y ca-certificates curl gnupg lsb-release && \
#    mkdir -p /etc/apt/keyrings && \
#    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg && \
#    echo \
#      "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
#      $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null && \
#    apt-get install docker-ce dock ber-ce-cli containerd.io docker-compose-plugin
RUN curl -sSL https://get.docker.com/ | sh
RUN apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    rm -f /var/cache/apt/archives/*.deb && \
    rm -f /var/cache/apt/archives/partial/*.deb && \
    rm -f /var/cache/apt/*.bin
COPY fs/setup.sh /
RUN chmod +x /setup.sh
RUN /setup.sh --build --dev
RUN /setup.sh --install --dev

ENTRYPOINT /setup.sh --run --dev
