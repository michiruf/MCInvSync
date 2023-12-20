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

Path: `/config/invsync.json`

```json

```

## License

[MIT License](https://github.com/michiruf/MCInvSync/blob/master/LICENSE)
