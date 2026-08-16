# AprismRefract Version Compatibility Matrix

> v26.5-Alpha.6 | Maps Minecraft versions to loader versions and .aep artifact names.
> Maintained by BlockConnect@StarsailsClover

## How to read this matrix

Each row represents one `.aep` variant. The Aprism runtime matches a variant
by checking the running MC edition + version against the variant's `mcEdit`
and `mcVersion` fields, and the target loader version against `loaderRange`.

An Aprism instance with MC 26.2 (JE) + the Fabric-Support `.aep` for MC 26.2
will scan `fabric-mods/` and dispatch Fabric mods. Without the `.aep`, the
folder is silently ignored.

## Fabric (loader key `Fa`)

| MC Version | Fabric Loader Range | .aep Artifact |
|---|---|---|
| 26.2 | [0.16.0, 0.17.0) | `Fabric-Support-A[26.0,28.0)-Fa[0.16.0,0.17.0)-JE-26.2.aep` |
| 26.1.2 | [0.16.0, 0.17.0) | `Fabric-Support-A[26.0,28.0)-Fa[0.16.0,0.17.0)-JE-26.1.2.aep` |
| 1.21.10 | [0.16.0, 0.17.0) | `Fabric-Support-A[26.0,28.0)-Fa[0.16.0,0.17.0)-JE-1.21.10.aep` |
| 1.21.4 | [0.16.0, 0.17.0) | `Fabric-Support-A[26.0,28.0)-Fa[0.16.0,0.17.0)-JE-1.21.4.aep` |
| 1.20.6 | [0.15.0, 0.16.0) | `Fabric-Support-A[26.0,28.0)-Fa[0.15.0,0.16.0)-JE-1.20.6.aep` |
| 1.20.1 | [0.15.0, 0.16.0) | `Fabric-Support-A[26.0,28.0)-Fa[0.15.0,0.16.0)-JE-1.20.1.aep` |
| 1.16.5 | [0.14.0, 0.15.0) | `Fabric-Support-A[26.0,28.0)-Fa[0.14.0,0.15.0)-JE-1.16.5.aep` |

Fabric supports MC 1.14+ only. MC 1.8.9 through 1.12.2 are not supported by
Fabric; use Forge or LiteLoader for those versions.

## NeoForge (loader key `N`)

| MC Version | NeoForge Loader Range | .aep Artifact |
|---|---|---|
| 26.2 | [21.4.0, 21.5.0) | `NeoForge-Support-A[26.0,28.0)-N[21.4.0,21.5.0)-JE-26.2.aep` |
| 26.1.2 | [21.4.0, 21.5.0) | `NeoForge-Support-A[26.0,28.0)-N[21.4.0,21.5.0)-JE-26.1.2.aep` |
| 1.21.10 | [21.4.0, 21.5.0) | `NeoForge-Support-A[26.0,28.0)-N[21.4.0,21.5.0)-JE-1.21.10.aep` |
| 1.21.4 | [21.4.0, 21.5.0) | `NeoForge-Support-A[26.0,28.0)-N[21.4.0,21.5.0)-JE-1.21.4.aep` |
| 1.20.6 | [21.0.0, 21.1.0) | `NeoForge-Support-A[26.0,28.0)-N[21.0.0,21.1.0)-JE-1.20.6.aep` |
| 1.20.1 | [21.0.0, 21.1.0) | `NeoForge-Support-A[26.0,28.0)-N[21.0.0,21.1.0)-JE-1.20.1.aep` |

NeoForge forked from Forge at MC 1.20.1. MC versions below 1.20.1 are not
supported by NeoForge; use Forge for those versions.

## Forge (loader key `Fo`)

| MC Version | Forge Loader Range | .aep Artifact |
|---|---|---|
| 26.2 | [54.0.0, 55.0.0) | `Forge-Support-A[26.0,28.0)-Fo[54.0.0,55.0.0)-JE-26.2.aep` |
| 1.21.4 | [52.0.0, 53.0.0) | `Forge-Support-A[26.0,28.0)-Fo[52.0.0,53.0.0)-JE-1.21.4.aep` |
| 1.20.1 | [47.0.0, 48.0.0) | `Forge-Support-A[26.0,28.0)-Fo[47.0.0,48.0.0)-JE-1.20.1.aep` |
| 1.16.5 | [40.0.0, 41.0.0) | `Forge-Support-A[26.0,28.0)-Fo[40.0.0,41.0.0)-JE-1.16.5.aep` |
| 1.12.2 | [14.23.0, 14.24.0) | `Forge-Support-A[26.0,28.0)-Fo[14.23.0,14.24.0)-JE-1.12.2.aep` |
| 1.11.2 | [13.20.0, 13.21.0) | `Forge-Support-A[26.0,28.0)-Fo[13.20.0,13.21.0)-JE-1.11.2.aep` |
| 1.10.2 | [12.18.0, 12.19.0) | `Forge-Support-A[26.0,28.0)-Fo[12.18.0,12.19.0)-JE-1.10.2.aep` |
| 1.8.9 | [11.15.0, 11.16.0) | `Forge-Support-A[26.0,28.0)-Fo[11.15.0,11.16.0)-JE-1.8.9.aep` |

Note: Forge does not have official builds for MC 26.x (NeoForge forked at
1.20.1). The MC 26.2 variant uses Forge 54.x (the latest Forge for 1.21.x)
for Aprism native compatibility; users on MC 26.x should prefer NeoForge.

## Quilt (loader key `Q`)

| MC Version | Quilt Loader Range | .aep Artifact |
|---|---|---|
| 26.2 | [0.29.0, 0.30.0) | `Quilt-Support-A[26.0,28.0)-Q[0.29.0,0.30.0)-JE-26.2.aep` |
| 26.1.2 | [0.29.0, 0.30.0) | `Quilt-Support-A[26.0,28.0)-Q[0.29.0,0.30.0)-JE-26.1.2.aep` |
| 1.21.10 | [0.29.0, 0.30.0) | `Quilt-Support-A[26.0,28.0)-Q[0.29.0,0.30.0)-JE-1.21.10.aep` |
| 1.21.4 | [0.29.0, 0.30.0) | `Quilt-Support-A[26.0,28.0)-Q[0.29.0,0.30.0)-JE-1.21.4.aep` |
| 1.20.6 | [0.26.0, 0.27.0) | `Quilt-Support-A[26.0,28.0)-Q[0.26.0,0.27.0)-JE-1.20.6.aep` |
| 1.20.1 | [0.26.0, 0.27.0) | `Quilt-Support-A[26.0,28.0)-Q[0.26.0,0.27.0)-JE-1.20.1.aep` |
| 1.16.5 | [0.26.0, 0.27.0) | `Quilt-Support-A[26.0,28.0)-Q[0.26.0,0.27.0)-JE-1.16.5.aep` |

Quilt mirrors Fabric version support (MC 1.14+). Quilt mods implement
`net.fabricmc.api.ModInitializer` via Quilt's built-in Fabric compatibility
layer.

## LiteLoader (loader key `L`)

| MC Version | LiteLoader Range | .aep Artifact |
|---|---|---|
| 1.12.2 | [1.12.0, 1.13.0) | `LiteLoader-Support-A[26.0,28.0)-L[1.12.0,1.13.0)-JE-1.12.2.aep` |
| 1.11.2 | [1.11.0, 1.12.0) | `LiteLoader-Support-A[26.0,28.0)-L[1.11.0,1.12.0)-JE-1.11.2.aep` |
| 1.10.2 | [1.10.0, 1.11.0) | `LiteLoader-Support-A[26.0,28.0)-L[1.10.0,1.11.0)-JE-1.10.2.aep` |
| 1.8.9 | [1.8.0, 1.9.0) | `LiteLoader-Support-A[26.0,28.0)-L[1.8.0,1.9.0)-JE-1.8.9.aep` |

LiteLoader supports MC 1.7.10 through 1.12.2 only. It does not support MC
1.14+ or 26.x.

## Summary: MC version coverage by loader

| MC Version | Fabric | NeoForge | Forge | Quilt | LiteLoader |
|---|---|---|---|---|---|
| 26.2 | Yes | Yes | (compat) | Yes | No |
| 26.1.2 | Yes | Yes | No | Yes | No |
| 1.21.10 | Yes | Yes | No | Yes | No |
| 1.21.4 | Yes | Yes | Yes | Yes | No |
| 1.20.6 | Yes | Yes | No | Yes | No |
| 1.20.1 | Yes | Yes | Yes | Yes | No |
| 1.16.5 | Yes | No | Yes | Yes | No |
| 1.12.2 | No | No | Yes | No | Yes |
| 1.11.2 | No | No | Yes | No | Yes |
| 1.10.2 | No | No | Yes | No | Yes |
| 1.8.9 | No | No | Yes | No | Yes |

Total .aep variants: 7 (Fabric) + 6 (NeoForge) + 8 (Forge) + 7 (Quilt)
+ 4 (LiteLoader) = **32 variants**.
