[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)
[![MIT license](https://img.shields.io/badge/License-MIT-blue.svg)](https://lbesson.mit-license.org/)


# InvSync

<img src="https://raw.githubusercontent.com/michiruf/MCInvSync/master/src/main/resources/assets/invsync/icon.png" width="300" height="300">

This is a fabric mod that Allows you to sync player inventories, health, food level, experience, score, status effects
and advancements across multiple servers or single player minecraft worlds! This is accomplished using a sql database
(SQLite and MySQL supported. SQLite will be setup automatically, you must set up MySQL). Most mods should have no
problem running alongside, and modded items will also be synchronized.

[![name](https://github.com/modrinth/art/blob/main/Branding/Badge/badge-dark__184x72.png?raw=true)](https://modrinth.com/mod/mr-invsync)


## Why this fork?

This mod was initially forked from MrNavaStar. His original mod can be found on 
[github](https://github.com/MrNavaStar/InvSync) and [modrinth](https://modrinth.com/mod/invsync).
Reasons for this fork are:

* Source Code was not published on release (see [here](https://github.com/MrNavaStar/InvSync/issues/21))
* The mod caused the server thread to sleep on player join, which got fixed, but the sleep call was still in the repository
* Database handling was messed up by using an all-in-one table for the player data defined in [SQLib](https://github.com/MrNavaStar/SQLib)
* For each SQL call a database connection was established

You can support MrNavaStar by [buying him a coffee!](https://ko-fi.com/mrnavastar)

## Requirements

* This mod requires [Fabric API](https://modrinth.com/mod/fabric-api)
* You can get the mod on [Modrith](https://modrinth.com/mod/mr-invsync)


## Getting Started

Setup is very simple. Drop the mod into your mods folder on all the servers you want to sync. Starting a server will
generate a config. The mod will not run until you edit the config.

* Configs MUST be identical between all participating servers in order for the mod to function correctly.
* This mod is known to have issues with: **GolfV**


## Example configuration

Path: `/config/InvSync.mcfg`

```properties
DATABASE_TYPE=SQLITE
SQLITE_PATH=./InvSync.db
MYSQL_DATABASE=InvSync
MYSQL_ADDRESS=database-host
MYSQL_PORT=3306
MYSQL_USERNAME=username
MYSQL_PASSWORD=password
SYNC_INVENTORY=true
SYNC_ENDER_CHEST=true
SYNC_HEALTH=true
SYNC_FOOD_LEVEL=true
SYNC_XP_LEVEL=true
SYNC_SCORE=true
SYNC_STATUS_EFFECTS=true
SYNC_ADVANCEMENTS=true
SYNCHRONIZATION_DELAY=true
SYNCHRONIZATION_DELAY_SECONDS=1
INITIAL_SYNC_OVERWRITE_ENABLED=true
INITIAL_SYNC_SERVER_NAME=ServerA
```

## Issues And Requests

If you find any bugs or glitches, be sure to make a bug report under issues and I will do my best to fix it! Just as
well if you have a cool idea for something that I should add, let me know and I will consider adding it!
