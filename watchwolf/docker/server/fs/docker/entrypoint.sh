#!/bin/bash

# Original calls:
# get_ip(){ wsl_mode; if [ $? -eq 0 ]; then echo "(Get-NetIPAddress -AddressFamily IPv4 -InterfaceAlias Ethernet).IPAddress" | powershell.exe 2>/dev/null | tail -n2 | head -n1; else hostname -I | awk '{print $1}';fi }
# docker run --privileged=true -i --rm --name ServersManager -p 8000:8000 -v /var/run/docker.sock:/var/run/docker.sock -v "$servers_manager_path":"$servers_manager_path" --env MACHINE_IP=$(get_ip) --env PUBLIC_IP=$(curl ifconfig.me) --env WSL_MODE=$(wsl_mode ; echo $? | grep -c 0) ubuntu:latest

cd $servers_manager_path
dos2unix ServersManager.sh ServersManagerConnector.sh SpigotBuilder.sh PaperBuilder.sh ConnectorHelper.sh
chmod +x ServersManager.sh ServersManagerConnector.sh SpigotBuilder.sh PaperBuilder.sh ConnectorHelper.sh
rm ServersManager.lock 2>/dev/null ;
socat -d -d tcp-l:8000,pktinfo,keepalive,keepidle=10,keepintvl=10,keepcnt=100,ignoreeof,fork system:'bash ./ServersManagerConnector.sh'