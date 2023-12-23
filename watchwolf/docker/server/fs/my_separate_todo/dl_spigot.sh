		if [ $no_spigot -eq 0 ]; then
			dos2unix "$servers_manager_path/SpigotBuilder.sh" "$servers_manager_path/PaperBuilder.sh"

			source "$servers_manager_path/SpigotBuilder.sh" # getAllVersions/buildVersion
			source "$servers_manager_path/PaperBuilder.sh" # getAllPaperVersions/buildPaperVersion

			# download the first <num_processes> Spigot versions
			num_downloading_containers=`getAllVersions | grep -c $'\n'`
			num_pending_containers=$(($num_downloading_containers > $num_processes ? $num_downloading_containers - $num_processes : 0))
			while read version; do
				buildVersion "$servers_manager_path/server-types/Spigot" "$version" >/dev/null 2>&1
			done <<< "$(getAllVersions | head -n $num_processes)" # get the first <num_processes> versions
		fi


		## TODO Theres another section for spigot in the original file, check it out