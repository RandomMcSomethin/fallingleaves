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