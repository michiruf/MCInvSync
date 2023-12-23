#!/bin/bash

echo "Downloading usual plugins..."

while read usual_plugin; do
  usual_plugin_name=`echo "$usual_plugin" | jq -r -c '.name + "-" + .version + "-" + .min_mc_version + "-" + .max_mc_version + ".jar"'`
  usual_plugin_url=`echo "$usual_plugin" | jq -r -c '.url'`

  # @ref https://github.com/rogermiranda1000/WatchWolf-ServersManager/blob/fdd45da8fa787b201a48ccca565a4e9f1415b7c3/ServersManager.sh#L56
  spigot_id=`echo "$usual_plugin_url" | grep -o -P '(?<=spigotmc.org/resources/)[^/]+' | grep -o -P '\d+$'`
  if [ -z "$spigot_id" ]; then
    wget -O "$servers_manager_path/usual-plugins/$usual_plugin_name" "$usual_plugin_url"
  else
    # Spigot plugin; get plugin from Spiget website
    spigot_plugin_name=`wget -q -O - "https://api.spiget.org/v2/resources/$spigot_id" | jq -r .name`

    # TODO download a specific version doesn't work
    #spigot_plugin_version=`echo "$usual_plugin_url" | grep -o -P '(?<=/download\?version=)[^/]+$'`
    #if [ -z "$spigot_plugin_version" ]; then
      usual_plugin_url="https://api.spiget.org/v2/resources/$spigot_id/download"
    #else
    #	usual_plugin_url="https://api.spiget.org/v2/resources/$spigot_id/versions/$spigot_plugin_version/download"
    #fi

    wget -O "$servers_manager_path/usual-plugins/$usual_plugin_name" "$usual_plugin_url"
  fi
done <<< `curl -s -N https://watchwolf.dev/api/v1/usual-plugins | jq -c '."usual-plugins" | .[]'` # all usual plugins urls