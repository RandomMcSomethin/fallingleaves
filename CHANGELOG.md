# Changelog

This file will contain the changes made to the mod as of version 1.5, which is when the `CHANGELOG` file was added to the repository. 
Any changes made before then will have to be understood through an analysis of [the code repository](https://github.com/RandomMcSomethin/fallingleaves).

The version history attempts to follow [Semantic Versioning](https://semver.org/) as closely as possible. Given a version number v`MAJOR`.`MINOR`.`PATCH`, we will increment the:

- MAJOR version when we make incompatible API changes,
- MINOR version when we add new functionality in a backwards compatible manner, and
- PATCH version when we make backwards compatible bug fixes without adding new functionality.

## v1.5.0 (UNRELEASED)

- `[brekitomasson]` - Added a build action as part of the GitHub push process.
- `[brekitomasson]` - Added a few calls to `LeafUtil.debugLog()` to make tracing problems easier.
- `[brekitomasson]` - Added a hidden configuration option in configuration file to output debug data.
- `[brekitomasson]` - Added sliders to the configuration screen's numeric values.
- `[brekitomasson]` - Changed all numeric configuration options to instead use integers with reasonable minimum and maximum values.
- `[brekitomasson]` - Changed code namespace from `fallingleaves.fallingleaves` to the more accurate `randommcsomethin.fallingleaves`.
- `[brekitomasson]` - Changed config keys `leafRate` and `coniferLeafRate` to `leafSpawnRate` and `coniferLeafSpawnRate` to more accurately indicate what these values do.
- `[brekitomasson]` - Cleaned up code style and made it more uniform across all files.
- `[brekitomasson]` - Increased Fabric API dependency from `v0.25.1` to `v0.28.3`.
- `[brekitomasson]` - Increased Fabric Loader dependency from `v0.10.6` to `v0.10.8`.
- `[brekitomasson]` - Increased Gradle dependency from `v6.5.1` to `v6.7`.
- `[brekitomasson]` - Merged Override and Conifer configuration and added automatic leaf discovery.
- `[brekitomasson]` - Moved configuration initialization code from `FallingLeavesClient` into its own file to make future expansion easier.
- `[brekitomasson]` - Moved leaf initialization code from `FallingLeavesClient` into its own file to make future expansion easier.
- `[brekitomasson]` - Refactored multiple one-liners into static methods inside `LeafUtils` for better reusability.
- `[brekitomasson]` - Renamed all code references of `spruce` to `conifer`.
- `[brekitomasson]` - Rewrote all old, commented out `println()` calls to use `LeafUtil.debugLog()`.
- `[brekitomasson]` - Updated Gradle settings for ease of use.
- `[brekitomasson]` - Updated ModMenu implementation for added flexibility.