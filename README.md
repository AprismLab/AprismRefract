# AprismRefract

Aprism | Loader-support `.aep` extensions that let Aprism Loader load mods
designed for other Minecraft mod loaders (Fabric, NeoForge, Forge, Quilt,
LiteLoader) and make them work together.

> Author: BlockConnect@StarsailsClover | License: Apache-2.0
> Companion repository of [Aprism](https://github.com/NDBlockConnect/Aprism).
> 简体中文说明：[README.zh.md](README.zh.md)

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
| `quilt` | Quilt | `Q` | `quilt-mods/` | developed |
| `liteloader` | LiteLoader | `L` | `liteloader-mods/` | developed |

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
  e.g. `Fabric-Support-A[26.0,28.0)-Fa[0.16.0,0.20.0)-JE-26.2.aep`.
- Interface contract: monotonic increment only; deprecation allowed with
  notice; never remove/rename.

<!-- GitHub@NDBlockConnect | BlockConnect@StarsailsClover -->

## Extension anatomy (loader-support .aep)

```
<name>.aep (ZIP)
  aprism.extension.json      # extensionId, version, type, aprismRange,
                             # loaderRange, mcEdit, mcVersion, entrypoint,
                             # provides
  aprismwarp.editor.json     # OPTIONAL (since v26.9-Alpha.2): AprismWarp
                             # editor catalog, schema aprismwarp.aep-editor/v1;
                             # read by AprismWarp, ignored by Aprism runtime
  extension.jar              # entrypoint class + loader bridge code
```

`aprism.extension.json` fields (loader-support):

- `extensionId`: e.g. `fabric-support`
- `version`: extension version (e.g. `26.9.0-alpha.2`), used for dependency
  range matching (since v26.9-Alpha.2)
- `type`: `loader-support`
- `aprismRange`: SemVer range of Aprism versions this extension supports
  (currently `[26.0.0,28.0.0)`)
- `loaderRange`: SemVer range of the target loader runtime (e.g. Fabric
  Loader versions)
- `mcEdit`: `JE` (BE loader-support does not exist per Aprism 9.16)
- `mcVersion`: explicit Minecraft version targeted by this variant (the
  32-variant build system emits one .aep per MC version; null = any)
- `entrypoint`: the `IAprismExtension` implementation class name
- `provides`: capability declarations (optional)

<!-- GitHub@NDBlockConnect | BlockConnect@StarsailsClover -->

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

- Tags: loader-prefixed, `<loader>/v26.9-Alpha.<n>` (and bare
  `<loader>/v26.9` for minor officials), SSH-signed. The loader prefix
  disambiguates same-version tags across the five loader branches; the version
  number itself mirrors the Aprism scheme unchanged. Example:
  `forge/v26.9-Alpha.2`. Current line: `v26.9-Alpha.2` (five signed
  Pre-Releases, 32 `.aep` variants total).
- Artifacts: the `.aep`, `checksums.txt` (SHA-256), cosign keyless signature
  (`.sig` + `.bundle`), CycloneDX SBOM; published as GitHub Pre-Releases for
  Alpha builds and GitHub Releases for officials.
- Verify after download: checksum match, then
  `cosign verify-blob <name>.aep --bundle <name>.aep.bundle
  --certificate-identity-regexp https://github.com/NDBlockConnect/AprismRefract
  --certificate-oidc-issuer https://token.actions.githubusercontent.com`.

## Relationship to Aprism

- Aprism core (agent, classloader, runtime, Mixin, remap, packaging) stays in
  the Aprism repository. Since the loader-support extraction (Aprism
  v26.1-Alpha.6 seam, completed at AprismRefract v26.0-Alpha.2) the core ships
  only the `LoaderEntrypointHandler` SPI + registry and the Aprism-native
  fallback; it no longer carries any loader-specific translation.
- Each loader branch here supplies its OWN translation layer: the entrypoint
  bridge, the loader API shim interfaces (`net.fabricmc.api.*`,
  `net.neoforged.fml.common.Mod`, `net.minecraftforge.fml.common.Mod`,
  `com.mumfrey.liteloader.core.LiteMod`), the `LoaderEntrypointHandler`
  implementation, and the `.aep` packaging. See
  [docs/extraction-architecture.md](docs/extraction-architecture.md) for the
  seam design and [docs/extension-sdk-conventions.md](docs/extension-sdk-conventions.md)
  for the normative split.
- Version alignment: an AprismRefract `v26.<minor>-Alpha.<n>` is built against
  the Aprism core alignment recorded in its `gradle.properties` at build time
  (dynamic alignment; currently Aprism `v26.8-Alpha.8`, covered by
  `aprismRange` `[26.0.0,28.0.0)`).

<!-- GitHub@NDBlockConnect | BlockConnect@StarsailsClover -->
