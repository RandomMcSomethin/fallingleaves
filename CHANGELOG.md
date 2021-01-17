# Changelog

This file will contain the changes made to the mod as of version 1.5, which is when the `CHANGELOG` file was added to the repository. 
Any changes made before then will have to be understood through an analysis of [the code repository](https://github.com/RandomMcSomethin/fallingleaves).

The version history attempts to follow [Semantic Versioning](https://semver.org/) as closely as possible. Given a version number v`MAJOR`.`MINOR`.`PATCH`, we will increment the:

- MAJOR version when we make incompatible API changes,
- MINOR version when we add new functionality in a backwards compatible manner, and
- PATCH version when we make backwards compatible bug fixes without adding new functionality.

## v1.5.0 (UNRELEASED)

- `[brekitomasson]` - Add a build action as part of the GitHub push process
- `[brekitomasson]` - Add a few calls to `LeafUtil.debugLog()` to make tracing problems easier
- `[brekitomasson]` - Add a hidden configuration option in configuration file to output debug data
- `[brekitomasson]` - Add sliders to the configuration screen's numeric values
- `[brekitomasson]` - Change all numeric configuration options to instead use integers with reasonable minimum and maximum values
- `[brekitomasson]` - Change code namespace from `fallingleaves.fallingleaves` to the more accurate `randommcsomethin.fallingleaves`
- `[brekitomasson]` - Change config keys `leafRate` and `coniferLeafRate` to `leafSpawnRate` and `coniferLeafSpawnRate` to more accurately indicate what these values do
- `[brekitomasson]` - Clean up code style and make it more uniform across all files
- `[brekitomasson]` - Increase Fabric API dependency from `v0.25.1` to `v0.28.3`
- `[brekitomasson]` - Increase Fabric Loader dependency from `v0.10.6` to `v0.10.8`
- `[brekitomasson]` - Increase Gradle dependency from `v6.5.1` to `v6.7`
- `[brekitomasson]` - Merge Override and Conifer configuration and add automatic leaf discovery
- `[brekitomasson]` - Move configuration initialization code from `FallingLeavesClient` into its own file to make future expansion easier
- `[brekitomasson]` - Move leaf initialization code from `FallingLeavesClient` into its own file to make future expansion easier
- `[brekitomasson]` - Refactor multiple one-liners into static methods inside `LeafUtils` for better reusability
- `[brekitomasson]` - Rename all code references of `spruce` to `conifer`
- `[brekitomasson]` - Rewrite all old, commented out `println()` calls to use `LeafUtil.debugLog()`
- `[brekitomasson]` - Update Gradle settings for ease of use
- `[brekitomasson]` - Update ModMenu implementation for added flexibility
- `[Fourmisain]` - improve `averageColor()` performance by looping over `x` first (~10x speed up for 4K textures)
- `[Fourmisain]` - fix issue #9: `textureColor` cache does not respect resource pack loading
- `[Fourmisain]` - fix `randomDisplayTick()` regression: first texture colored leaf is gray due to not setting color
- `[Fourmisain]` - fix `randomDisplayTick()` regression: InputStream isn't closed due to removal of try-with-resources
- `[Fourmisain]` - improve readability of `randomDisplayTick()`, refactor `passesCriteria()` into `isBottomLeafBlock()`
- `[Fourmisain]` - rewrite `spriteToTexture()` to be much simpler and more readable
- `[Fourmisain]` - use active voice in changelog, e.g. "add" instead of "added" and remove periods
- `[brekitomasson]` - add `fabric-api` to the exclude list when implementing modmenu
- `[brekitomasson]` - remove `macos` from the GitHub build action to reduce time spent
- `[Fourmisain]` - rename "overrides" to "leaf settings"
- `[Fourmisain]` - GUI: "flatten" Leaf Block category
- `[Fourmisain]` - GUI: sort Leaf Block category by translated name
- `[Fourmisain]` - `LeafSettingsEntry`: cache translations, add toString()
- `[Fourmisain]` - use Log4J for logging
- `[Fourmisain]` - remove some unneeded `final` for increased readability
- `[Fourmisain]` - only use integer math in `averageColor()` and use `getAlpha()` instead of bit operations
- `[Fourmisain]` - always insert entries from config file into `LeafSettings.entries` to partially remedy `getRegisteredLeafBlocks()` missing blocks
- `[Fourmisain]` - move `useCustomSpawnRate()` to `ConfigDefaults`
- `[Fourmisain]` - `getLeafSettingsEntry()`: use `Set.get()` instead of `.stream().filter()`
- `[brekitomasson]` - config values default to 5 instead of 1.
- `[brekitomasson]` - old `LeafUtil` and TextureCache broken down into multiple separate Util files.
- `[brekitomasson]` - new proxy object for logging.
- `[Fourmisain]` - change custom spawn rate to a spawn rate factor/multiplier
- `[Fourmisain]` - use getters in `FallingLeavesConfig` to do the GUI->code value mapping
- `[Fourmisain]` - refactor `getLeafSpawnRate()` into `getLeafSpawnChance()`
- `[Fourmisain]` - load all registered leaf blocks on world load
- `[Fourmisain]` - double the precision of (conifer) leaf spawn rate; move default accordingly
- `[Fourmisain]` - switch back to using `switch` in `ConfigDefaults`
- `[Fourmisain]` - remove unused `LeafBlockList` annotation
- `[Fourmisain]` - remove unused `FallingLeavesConfig.instance`
- `[Fourmisain]` - remove `LogUtil`
- `[Fourmisain]` - `randomDisplayTick`: factor out color calculation into `calculateBlockColor()`
- `[Fourmisain]` - apply `ConfigDefaults` directly in `LeafSettingsEntry` constructor
- `[Fourmisain]` - replace all fastutil usages, notably switch from `ObjectLinkedOpenHashSet` to `HashMap`
- `[Fourmisain]` - `LeafSettingsGuiProvider`: factor out GUI construction into own methods for readability
- `[Fourmisain]` - (re)move all logic from `LeafSettingsEntry`:
- `[Fourmisain]` - `LeafSettingsEntry.getBlock()` -> `RegistryUtil.getBlock()`
- `[Fourmisain]` - `LeafSettingsEntry.TranslationComparator` -> `util` package
- `[Fourmisain]` - `LeafSettingsEntry.getTranslation()` -> `TranslationComparator.getTranslation()`
- `[Fourmisain]` - remove (now) unused `equals()`/`hashCode()` from `LeafSettingsEntry`
- `[Fourmisain]` - `TranslationComparator` will fall back to comparing keys and putting a warning if there are no translations
- `[Fourmisain]` - add config migration
- `[Fourmisain]` - "flatten" `LeafSettings`; `leafSettings.entries` is now just `leafSettings`
