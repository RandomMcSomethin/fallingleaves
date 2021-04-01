# Falling Leaves

This Fabric mod for Minecraft 1.16.x adds a neat little particle effect to leaf blocks. Users can configure which types of leaf blocks will drop leaves and the frequency that these leaves are dropped at.

![](https://i.imgur.com/Tek7xJe.gif)

## Toubleshooting aka "Why are no leaves falling?"

First, make sure Particles are set to "All" or "Decreased" in your video settings.

Some trees do not drop leaves by default, these are: conifer trees (like spruce and pine), large leaved trees (like jungle trees and palms) and shrubs.

You can change this as you like by adjusting "Conifer Leaf Spawn Rate" in the Mod Menu settings and adjusting the spawn rates for specific trees under  "Leaf Settings".

A complete list of all conifer trees is found [here](https://github.com/RandomMcSomethin/fallingleaves/blob/d5cc5ac3074ef513cab6af56d886c9f1424f2d5d/src/main/java/randommcsomethin/fallingleaves/config/ConfigDefaults.java#L9-L21) and a list of all trees with adjusted spawn rates is found [here](https://github.com/RandomMcSomethin/fallingleaves/blob/d5cc5ac3074ef513cab6af56d886c9f1424f2d5d/src/main/java/randommcsomethin/fallingleaves/config/ConfigDefaults.java#L30-L49).

## FAQ

- Is this compatible with trees from other mods? What about Resource Packs?
  - Falling Leaves _should_ be 100% compatible with any modded trees and any resource pack changing leaves!
- Does this need to be installed on the server for multiplayer?
  - Nope! This mod is 100% client-side and should work fine no matter what.
- Lol fabric suxxx gief forge plx.
  - That's not a question. Also, no. Feel free to port it to Forge if you want to, though!
- Do you have any screenshots I could look at before I install this?
  - Sure! Check out the [images section](https://www.curseforge.com/minecraft/mc-mods/falling-leaves-fabric/screenshots) of this mod's Curseforge page.
- I enjoy old things. Can you backport this to Minecraft v1.7?
  - No. We will only focus on supporting the most recently released version of Minecraft.
- I found a problem! Do I just post a comment on your CurseForge page?
  - You **could**, but we'd prefer it if you posted an issue on [our GitHub repository's issue tracker](https://github.com/RandomMcSomethin/fallingleaves/issues). It makes it far easier for us to follow up when developing new versions of this mod.
- I have an idea for something you could do with this mod!
  - Great, we're always happy to hear you out! Use the issue tracker on our Github Repo to post your suggestions.
- Can I include this mod in my modpack?
  - Absolutely! Just remember to put a link to our Curseforge page somewhere in your modpack's documentation.
- What do you get if you jumble the letters in "Falling Leaves Mod"?
  - You get "Five Golden Llamas". We're still not sure if this means anything in particular...
  
## Thanks and Credits

All good developers learn from reading code other people have written, and it's only fair to credit those who have inspired us. For that reason, we give thanks to:

- [user11681](https://github.com/user11681/java), whose work in their `limitless` mod was very helpful when developing our own configuration screen.
- [TehNut](https://github.com/TehNut), whose work in `HWYLA` was very useful in figuring out how to get a mod's name from a block it adds.