# Nico Yazawa SpongeForge
Linking Discord & Minecraft Server Nico Style. 

This is a Sponge plugin that aims to link Discord with Minecraft. Now, there are already great plugins for Sponge 
and Spigot out there like [DiscordLink](https://gitlab.com/swordo/discordlink) or [MBDiscordLink](https://ore.spongepowered.org/Eufranio/MBDiscordLink)
which are great and have been quite inspiring. But i needed more control or did not want all of the functionally which these plugins provided.



## Installing

Head over to the releases [page](https://github.com/Nanabell/Nico-Yazawa-SpongeForge/releases) and download the latest version.  
The plugin is currently targeted at [Sponge](https://www.spongepowered.org/) version 7.1.0.

Current Limitations:
- Only Sponge is supported. (You can try some sub-sets of Sponge but i dont guarantee anything here)
    - That includes no Bungeecord support.
- Expects a MongoDB database (can be clustered and or somewhere else but is required to start)
    - If you want the Economy System to create account you will need to set  `create-accounts` to true in the configs
- Requires a valid Discord Bot Token. At the moment the plugin is a total mess if this is not present...
- The plugin **will** stop your server after first install to generate configs and let you change these. This will change but i haven't come around to making this more user friendly
    - Set `core-module/startup-error` to false

## Building from Source
To build this project you will need your Github username & either password or an access token with read package permissions   
I strongly recommend using an access token. Head over [here](https://help.github.com/en/github/authenticating-to-github/creating-a-personal-access-token-for-the-command-line) if you dont know how to.

If you have used gradle before you can just add
 - github.username
 - github.package.personal.access.token  
 
To your gradle.properties file.  
Alternatively you can set the Environment variables `GITHUB_USERNAME` & `GITHUB_PACKAGE_TOKEN` respectively

Then just build the project `gradle build` and the plugin will appear in the `build/libs` directory

# Credits
- Already mentioned [DiscordLink](https://gitlab.com/swordo/discordlink) by [swordo](https://gitlab.com/swordo), for the great starting point and incredible "Troop" Syncing Idea
- Already mentioned [MBDiscordLink](https://ore.spongepowered.org/Eufranio/MBDiscordLink) by [Eufranio](https://ore.spongepowered.org/Eufranio), for getting me started in sponge and acting as a guide for the Discord System
- [Nucleus](https://nucleuspowered.org/index.html) by the [NucleusPowered](https://github.com/orgs/NucleusPowered/people) Team and contributors, for.. well most of how my plugin works, as i've adapted the Module~ish approach and their color scheme and much more.
- [QuickStartModuleLoader](https://github.com/NucleusPowered/QuickStartModuleLoader) by [Daniel Naylor](https://github.com/dualspiral), for the very nice base for the module pattern.