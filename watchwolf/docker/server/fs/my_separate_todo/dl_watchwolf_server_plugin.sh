		# WatchWolf Server as usual-plugins
		watchwolf_server_versions_base_path="https://watchwolf.dev/versions"
		web_contents=`wget -q -O - "$watchwolf_server_versions_base_path"`
		higher_version=`echo "$web_contents" | grep -o -P '(?<=WatchWolf-)[\d.]+(?=-)' | sort --reverse --version-sort --field-separator=. | head -1` # get the current higher version
		higher_version_file=`echo "$web_contents" | grep -o -P "WatchWolf-${higher_version//./\\.}-[\d.]+-[\d.]+\.jar"`
		wget "$watchwolf_server_versions_base_path/$higher_version_file" -P "$servers_manager_path/usual-plugins"