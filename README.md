# Bedrockified
The Bedrockified project aims to implement Bedrock Edition mechanics into Java edition to ensure we understand these mechanics properly.

## Features
The following should have parity between Bedrockified and real Bedrock edition:
- Biome generation (only the biomes, not every tree will be the same)
- Overworld and Nether structure locations
- Slime chunks
- Spawn point

## Installation instructions
There are two easy ways to install Bedrockified, one using [MultiMC](https://multimc.org) and one using the server JAR.
It is also possible to install the client version of Bedrockified on the vanilla launcher, but this is complicated so there are no instructions here.

### MultiMC
1. Download the client version of the Bedrockified zip from the releases page.
1. Download and install MultiMC from [https://multimc.org](https://multimc.org) if you don't have it already.
1. Create a new instance for Minecraft version 1.13.1 (note this is not the latest version!).
1. Click "edit instance", and on the right, "add to Minecraft.jar".
1. Navigate to and select the Bedrockified zip.
1. Start the game.

### Dedicated server
1. Download the server version of the Bedrockified zip from the releases page.
1. Download the 1.13.1 Minecraft server from [Mojang](https://launcher.mojang.com/v1/objects/fe123682e9cb30031eae351764f653500b7396c9/server.jar).
1. Move the server JAR into a separate folder, and execute it once from the command line using `java -jar server.jar`.
1. Open `eula.txt`, read the EULA it links to, then change the value to true.
1. Copy the contents of the Bedrockified zip into the server jar (which is just a zip with a different file extension), overwriting existing entries. How you do this is up to you, but I recommend 7zip.
1. Launch the server again, it should be running Bedrockified. Connect to it with a vanilla 1.13.1 client.

## Installation from source + contribution
Bedrockified uses jarmod-buildsystem-2 by Earthcomputer, built on top of ForgeGradle 3.0 for making JAR mods in Minecraft: Java Edition 1.13+

### Requirements
- You need to have at least JDK8 update 92 for recompilation to work, due to a bug in earlier versions of `javac`. You also cannot use JDK9 or JDK10 yet.
- You need to have `git` installed.
- Eclipse Oxygen.3 or later, due to [this Eclipse bug](https://bugs.eclipse.org/bugs/show_bug.cgi?id=526911).
- Or Intellij

### First-time setup
- Copy all the files in this repository into your new project folder.
- Delete the example mod from inside `patches` (do not delete the `patches` directory itself) and from inside`src/main/java`.
- Edit `conf/settings.json` for your project. Each setting is described in more detail below.
- Run `gradlew setup` to decompile and deobfuscate the code.
- Run `gradlew eclipse` to setup the appropriate Eclipse projects. Do this even if you are planning on using Intellij IDEA.
- If you use Eclipse, open Eclipse, and navigate to `File -> Import -> General -> Existing Projects into Workspace`. Navigate to and select the `projects` subdirectory, and check your mod project, and optionally the clean (unmodified) project too.
- Otherwise, open Intellij IDEA and import the Eclipse project.

### Project layout + management
Once you have setup the project, you should see a file structure in Eclipse which looks something like this:
```
- src/main/java This is where all of the MINECRAFT classes go, i.e. classes which you may or may not have modified, but no classes you have added.
- src/main/resources Similar to src/main/java except for non-java files.
- main-java This is where all of the MOD classes go, i.e. the classes which you have added.
- main-resources Similar to main-java except for non-java files.
```
From outside Eclipse, the file structure looks a little different. However, you should avoid editing these files from outside Eclipse:
```
- src/main/java The MOD classes
- src/main/resources The MOD resources
- patches Patches your mod has made to the MINECRAFT classes, which can be pushed to public repositories
- projects/<modname>/src/main/java * The MINECRAFT classes
- projects/<modname>/src/main/resources * The MINECRAFT resources
- projects/clean/src/main/java * The unmodified MINECRAFT classes
- projects/clean/src/main/resources * The unmodified MINECRAFT resources
* = ignored by git
```

- You should be able to run Minecraft directly from within the IDE.
- Every time you checkout a branch which has changed files in the `patches` directory, you need to run `gradlew setup` again to update the code in `src/main/java` inside Eclipse. This will not try to decompile again like it did the first time, so won't take long.
- Every time you make changes to MINECRAFT classes and want to push to the public repo, you need to run `gradlew genPatches` to update the patch files in the `patches` directory. This takes a few seconds.
- When you are ready to create a release, run `gradlew createRelease`. This may take longer than the other tasks because it is recompiling the code. Once it is done, your releases can be found in the `build/distributions` directory.

### Settings you can change
- `modname` the name of your mod.
- `modversion` the version your mod is on.
- `mcpconfig` the MCPConfig version you are using.
- `mappings` the MCP mappings you are using.
- `mcversion` the Minecraft version.
- `pipeline`, either `joined`, `client` or `server` - whether your mod is to be a client-side-only or server-side-only mod, or to be both and share the same codebase.
- `clientmain` the main class on the client.
- `servermain` the main class on the server.
- `reformat` whether to run Artistic Style on the code to reformat it. Makes the build process a little slower but does mean you can change the formatting options with `conf/astyle.cfg`.
- `customsrg` The custom tsrg file inside the `conf/` folder, to override the one in the MCPConfig distribution, used to deobfuscate even newer Minecraft versions.

### A word of warning
1.13 modding is still in its infancy, and there are already known bugs that occur in the decompiled code which do not occur in vanilla. If you care about maintaining vanilla behaviour, then whenever making a change which may modify a certain vanilla class, make sure to weigh up the benefit of modifying said class against the risk that there might be a decompile bug in the class. This situation is constantly improving as 1.13 modding matures, but for now you can at least minimize the effect by distributing as few modified classes as possible.
