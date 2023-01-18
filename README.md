[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)
[![MIT license](https://img.shields.io/badge/License-MIT-blue.svg)](https://lbesson.mit-license.org/)
[![Download on modrinth](https://github.com/modrinth/art/blob/main/Branding/Badge/badge-dark__184x72.png?raw=true)](https://modrinth.com/mod/mr-invsync)


# MR InvSync

This is a fabric mod that allows you to sync player inventories, health, food level, experience, score, status effects
and advancements across multiple servers or single player minecraft worlds.
SQLite, MySQL and Postgres are supported as data backends.


## Why this fork?

This mod was initially forked from MrNavaStar. His original mod can be found on 
[github](https://github.com/MrNavaStar/InvSync) and [modrinth](https://modrinth.com/mod/invsync).
Reasons for this fork are:

* At the time the project was forked, the database handling was messed up by using an all-in-one table for the player 
  data defined in [SQLib](https://github.com/MrNavaStar/SQLib)
* At the time the project was forked, for each SQL call a database connection was established
* Having a separate upstream for production environment testing
* Details are list in [issue #1](https://github.com/michiruf/MCInvSync/issues/1)

You can support MrNavaStar by [buying him a coffee!](https://ko-fi.com/mrnavastar).


## Installation

This mod requires **Fabric** and **[Fabric API](https://modrinth.com/mod/fabric-api)**.

This mod is available on [modrinth](https://modrinth.com/mod/mr-invsync) with slug `mr-invsync`.
The project source is available on [github/michiruf](https://github.com/michiruf/MCInvSync) with the latest
readme [here](https://github.com/michiruf/MCInvSync/blob/master/README.md).

The configuration of the database must be identical between all servers that shall sync the inventory.


## Example configuration

Path: `/config/InvSync.mcfg`

```properties
DATABASE_TYPE=SQLITE
DEBUG_DELETE_TABLES=false
SQLITE_PATH=./InvSync.db
MYSQL_DATABASE=InvSync
MYSQL_ADDRESS=mysql-host
MYSQL_PORT=3306
MYSQL_USERNAME=username
MYSQL_PASSWORD=password
POSTGRES_DATABASE=InvSync
POSTGRES_ADDRESS=postgres-host
POSTGRES_PORT=5432
POSTGRES_USERNAME=username
POSTGRES_PASSWORD=password
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
SYNCHRONIZATION_DELAY_METHOD=SLEEP
INITIAL_SYNC_OVERWRITE_ENABLED=true
INITIAL_SYNC_SERVER_NAME=ServerA
```

## License

[MIT License](https://github.com/michiruf/MCInvSync/blob/master/LICENSE)
