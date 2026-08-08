# AprismRefract

Aprism | Loader-support `.aep` extensions that let Aprism Loader load mods
designed for other Minecraft mod loaders (Fabric, NeoForge, Forge, Quilt,
LiteLoader) and make them work together.

> Author: BlockConnect@StarsailsClover | License: Apache-2.0
> Companion repository of [Aprism](https://github.com/NDBlockConnect/Aprism).

## What this repository is

Aprism Loader is native and does NOT natively understand other loaders' mod
formats. Loader support is delivered exclusively through Aprism Extensions
(`.aep`, type `loader-support`). This repository is where those extensions
live. Each loader gets its own **branch**; the Aprism core stays in the
Aprism repository.

## Branch model (one loader per branch)

| Branch | Loader | Loader key | Mod folder | Status |
|---|---|---|---|---|
| `main` | shared skeleton, docs, extension SDK conventions | - | - | foundation |
| `fabric` | Fabric | `Fa` | `fabric-mods/` | developed (migrated from Aprism) |
| `neoforge` | NeoForge | `N` | `neoforge-mods/` | developed |
| `forge` | Forge | `Fo` | `forge-mods/` | developed |
| `quilt` | Quilt | `Q` | `quilt-mods/` | placeholder |
| `liteloader` | LiteLoader | `L` | `liteloader-mods/` | placeholder |

Rules:

- `main` holds only cross-loader material: this README, FACT.md, the extension
  SDK conventions document, and shared Gradle scaffolding. No loader code.
- Every loader branch is based on `main` and carries ONLY its own loader
  support implementation. Cross-loader shared code goes to `main` first, then
  branches pick it up.
- Loader-support extensions for the same loader never coexist on two
  branches; the branch IS the identity of the loader support.

## Versioning

Mirrors the Aprism scheme (FACT.md section 5 of the Aprism repository):

- Format: `v<Year>.<minor>[-Alpha.<n>]`; `v26` = the 2026 line with ten
  minors `v26.0`-`v26.9`; Alpha.1-Alpha.9 per minor as GitHub Pre-Releases;
  bare number = minor official (GitHub Release); annual edition `v26.2026`
  each December.
- Extension artifact naming follows Aprism FACT.md 9.14:
  `<Purpose>-A<AprismVerRange>-<LoaderKey><LoaderVerRange>-<MCEdit>-<MCVer>.aep`
  e.g. `Fabric-Support-A[26.0,27.0)-Fa[0.16,0.17)-JE-1.21.4.aep`.
- Interface contract: monotonic increment only; deprecation allowed with
  notice; never remove/rename.

## Extension anatomy (loader-support .aep)

```
<name>.aep (ZIP)
  aprism.extension.json      # extensionId, type, aprismRange, loaderRange,
                             # mcEdit, mcVersion, entrypoint, provides
  extension.jar              # entrypoint class + loader bridge code
```

`aprism.extension.json` fields (loader-support):

- `extensionId`: e.g. `aprism:fabric-support`
- `type`: `loader-support`
- `aprismRange`: SemVer range of Aprism versions this extension supports
- `loaderRange`: SemVer range of the target loader runtime (e.g. Fabric
  Loader versions)
- `mcEdit`: `JE` (BE loader-support does not exist per Aprism 9.16)
- `mcVersion`: Minecraft version(s) targeted (null = any)
- `entrypoint`: the `IAprismExtension` implementation class name
- `provides`: capability declarations (optional)

At load time the extension's `onInitialize(ExtensionContext)` registers its
loader key and mod folder via `context.registerLoaderSupport(key, folder)`.
Aprism then scans that folder during mod loading (phase 2), projects the
loader's native manifest to an Aprism manifest, and invokes entrypoints
through the loader's own convention via a bridge shipped with this extension.

## Build & verify

Each loader branch is a self-contained Gradle build. From the branch root:

```
./gradlew build          # compile + tests
./gradlew packageAep     # assemble the .aep (packaging plugin from Aprism)
```

The packaging plugin and Aprism API/manifest dependencies are consumed from
the Aprism build (`pluginManagement.includeBuild` in the branch's
settings.gradle). Signed commits + signed tags are mandatory
(Conventional Commits).

## Release & signing

- Tags: `v26.0-Alpha.<n>` (and bare minor officials), SSH-signed.
- Artifacts: the `.aep`, `checksums.txt` (SHA-256), cosign keyless signature
  (`.sig` + `.bundle`), CycloneDX SBOM; published as GitHub Pre-Releases for
  Alpha builds and GitHub Releases for officials.
- Verify after download: checksum match, then
  `cosign verify-blob <name>.aep --bundle <name>.aep.bundle
  --certificate-identity-regexp https://github.com/NDBlockConnect/AprismRefract
  --certificate-oidc-issuer https://token.actions.githubusercontent.com`.

## Relationship to Aprism

- Aprism core (agent, classloader, runtime, Mixin, remap, packaging) stays in
  the Aprism repository. This includes the entrypoint bridges
  (`FabricEntrypointBridge`, `NeoForgeEntrypointBridge`, ...) and the loader
  API shim interfaces (`net.fabricmc.api.*`, `net.neoforged.fml.common.Mod`,
  ...) which live in Aprism's `aprism-loader-core`.
- This repository contains only the `.aep` entrypoint class + manifest for each
  loader. See [docs/extension-sdk-conventions.md](docs/extension-sdk-conventions.md)
  for the normative split between Aprism core and loader branches.
- Version alignment: an AprismRefract `v26.0-Alpha.<n>` is built against the
  matching Aprism `v26.0-Alpha.<n>` API surface.
