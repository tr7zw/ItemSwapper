# Frequently asked questions

## Table of Content

* [Where are the latest versions of all your mods?](#latest-version)
* [Where are the Forge versions of the mod?](forge-version)
* [Warning! No compatible resourcepack for Itemswapper is enabled. What can I do?](#no-resource-pack)
* [Why isn't the Shulkerbox support working when playing on a server?](#shulkerbox-support)
* [Where can I find the server version of Itemswapper?](#server-version)
* [Why do I get a warning when joining the server?](#server-warning)
* [What is the modpack policy?](#modpack-policy)
* [What is your version support policy?](#support-policy)
* [How can I check if the mod is loaded?](#is-mod-loaded)
* [What is Modrinth?](#modrinth)

---

## Where is the latest versions of Itemswapper? <div id='latest-version'/>

The latest releases of Itemswapper are published to the
projects [Modrith page](https://modrinth.com/mod/itemswapper)
and [Github release](https://github.com/tr7zw/ItemSwapper/releases/latest)
page. If you are using the CurseForge client, check
out [CurseForge](https://www.curseforge.com/minecraft/mc-mods/itemswapper)

---

## Where are the Forge versions of the mod? <div id='forge-version'/>

There are several reasons why there is no "Forge" version of the mod. The main reason is that we are
a group of volunteers who spend their free time working on this project. Maintaining another version
would require even more time. Furthermore, Forge does not make it easy for developers to work with
build-in resource packs, which is one of the main features of this mod. For more information see the
[issue](https://github.com/tr7zw/ItemSwapper/issues/25) that is dedicated to this question.

---

## Warning! No compatible resourcepack for Itemswapper is enabled. What can I do? <div id='no-resource-pack'/>

You see the following warning ``Warning! No compatible resourcepack for Itemswapper is enabled`` if
Itemswapper could not find a resource pack that defines the Items in the switch interface. To load
such a resource pack, go to your resource pack settings in your client and select the build-in
resource pack that ships with Itemswapper.

---

## Why isn't the Shulkerbox support working when playing on a server? <div id='shulkerbox-support'/>

If swapping items from Shulker crates does not work on the server you are playing on, it is probably
because the server does not have the server version of Itemswapper installed. If this is your own
server, you might check out below question about where the server version can be found.

---

## Where can I find the server version of Itemswapper? <div id='server-version'/>

If you are running a Fabric server you only need to install the latest Fabric version of Itemswapper
that matches with the server version. See [here](https://modrinth.com/mod/itemswapper/versions) for
the Fabric version.

If you are running a Bukkit/Paper/Purpur/Spigot server you need to install the appropriate version.
See [here](https://modrinth.com/mod/itemswapper/versions) for the Bukkit/Paper/Purpur/Spigot
version.

---

## Why do I get a warning when joining the server? <div id='server-warning'/>

We have decided that using Itemswapper on a Vanilla server might get you an unfair advantage over
players that are not using Itemswapper. For this reason the server owner is provided with the
ability to block this mod. If the server owner does not have blocked this mod, they might see using
this mod as cheating and so can ban you. Please **use Itemswapper at your own risk** on a server
where the warning is being displayed!

---

## What is the modpack policy? <div id='modpack-policy'/>

All of our mods are currently licensed under
the [GNU LGPLv3](https://github.com/tr7zw/ItemSwapper/blob/1.19/LICENSE), a free and open-source
license. For most intents and purposes, this means you can freely distribute them alongside your
modpack so long as you provide attribution.

---

## What is your version support policy? <div id='support-policy'/>

We generally only publish releases for the latest version of Minecraft (see our mainline branch on
GitHub) as porting to older versions takes a lot of time and resources. However, a 1.18 release is
planed for the future, but it is unlikely to happen if 1.20 will be releases before our planed 1.18
release. For a further version overview check [here](https://tr7zw.github.io/versions/).

---

## How can I check if the mod is loaded? <div id='is-mod-loaded'/>

To check if Itemswapper could be loaded correctly the easiest way is to use an in-game mod menu such
as [Mod Menu](). However, you can also check the log files of your client/server. If the mod could
be loaded properly, the mod and its version will be logged as in below example.

```
[00:37:05] [main/INFO] (FabricLoader/GameProvider) Loading Minecraft 1.19.2 with Fabric Loader 0.14.9
[00:37:05] [main/INFO] (FabricLoader) Loading 45 mods:
	- fabric-api 0.60.0+1.19.2
	[...]
	- fabricloader 0.14.9
	- itemswapper 0.3.2-mc1.19.2
	- minecraft 1.19.2
	- modmenu 4.0.6
```

---

## What is Modrinth? <div id='modrinth'/>

Modrinth is an alternative platform for hosting Minecraft mods. It's generally easier to use and
provides better tooling for developers, but doesn't quite have widespread adoption yet. Where
possible, we recommend players use Modrinth or GitHub to check for the latest versions.
