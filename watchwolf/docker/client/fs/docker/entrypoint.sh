#!/bin/bash

docker run -i --rm --name ClientsManager -p 7000-7199:7000-7199 --env MACHINE_IP=$(get_ip) --env PUBLIC_IP=$(curl ifconfig.me) clients-manager:latest >/dev/null 2>&1 & disown