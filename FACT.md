# FACT.md - AprismRefract Project Tracking

> Maintained by BlockConnect@StarsailsClover
> Convention: read & update this file before and after every task session.
> Versioning mirrors Aprism: v26 = 2026 line, minors v26.0-v26.9, Alpha.1-9 per
> minor as GitHub Pre-Releases, bare number = minor official, annual edition
> v26.2026 each December. Extension artifact naming per Aprism FACT.md 9.14.

## 1. Project Identity

- **Name:** AprismRefract
- **Role:** Loader-support `.aep` extensions for Aprism Loader (Fabric,
  NeoForge, Forge, Quilt, LiteLoader). Companion repository of Aprism.
- **Author:** BlockConnect@StarsailsClover
- **Repo:** https://github.com/NDBlockConnect/AprismRefract (GitHub)
- **License:** Apache-2.0

## 2. Branch Model

One loader per branch; `main` holds only shared skeleton/docs.

| Branch | Loader | Key | Folder | Status |
|---|---|---|---|---|
| main | shared skeleton | - | - | foundation |
| fabric | Fabric | Fa | fabric-mods/ | developed (migrated from Aprism) |
| neoforge | NeoForge | N | neoforge-mods/ | developed |
| forge | Forge | Fo | forge-mods/ | developed |
| quilt | Quilt | Q | quilt-mods/ | developed |
| liteloader | LiteLoader | L | liteloader-mods/ | developed |

## 3. Conventions

- Conventional Commits, SSH-signed commits and tags, no force-push to main.
- Build with Gradle; consume Aprism packaging plugin + API via
  pluginManagement.includeBuild of the Aprism workspace.
- No emoji in any artifact.

## 4. Session Log

### Session 2026-08-10 (quilt, v26.0-Alpha.2) - Quilt translation layer extraction
- [DONE] Extracted the Quilt translation layer from the Aprism core into
  this branch, per the loader-support extraction architecture (Aprism
  v26.1-Alpha.6 seam; extraction-architecture.md on main):
  - Migrated QuiltEntrypointBridge (reflective Fabric-convention invocation;
    Quilt's built-in Fabric compatibility layer means Quilt mods implement
    net.fabricmc.api.ModInitializer and its variants) into
    com.aprism.refract.quilt.
  - Bundled the Fabric API shims (net.fabricmc.api.ModInitializer /
    ClientModInitializer / DedicatedServerModInitializer) in this branch.
  - Added QuiltEntrypointHandler implements LoaderEntrypointHandler: owns
    Quilt dispatch (manifest-driven entrypoints with the quilt.mod.json init
    key projected to main, IAprismMod native path preserved,
    per-entrypoint isolation, instance retained on container).
  - QuiltSupportExtension now registers the handler via
    context.registerEntrypointHandler("Q", handler) - exclusive, so the core
    never runs any Quilt-specific code for loader key Q.
- [DONE] Compile alignment bumped to the v26.1-Alpha.8 triple (api +
  loader-core + manifest jars, compileOnly). Branch-local test suite runs
  the real Aprism runtime without any change to the Aprism repository.
- [DONE] Tests: 13 green (QuiltEntrypointBridgeTest 5,
  QuiltEntrypointHandlerTest 6, QuiltExtractionE2ETest 2, 0 skipped).
  The E2E asserts the registered handler is DEFINED BY the AprismClassLoader
  (loaded from the .aep's embedded jar) - the decisive proof the extraction,
  not any core fallback, owns Quilt dispatch.
- [NOTE] The Aprism core still ships its built-in Quilt dispatch as a
  transition fallback (removal lands after all five loaders are extracted).
- Version bumped to v26.0-Alpha.2.

### Session 2026-08-09 (shared docs)
- [DONE] Created docs/extension-sdk-conventions.md on main: the normative
  loader-support extension SDK conventions (lifecycle, what lives in Aprism core
  vs loader branches, loader key/folder registry, manifest + entrypoint
  templates, build/release/version-alignment rules).
- [DONE] Corrected README "Relationship to Aprism": entrypoint bridges and
  loader API shim interfaces live in Aprism aprism-loader-core; this repo holds
  only the .aep entrypoint class + manifest per loader.
- [STATUS] fabric branch: developed + cross-repo E2E verified (Fabric-Support.aep).
- [STATUS] neoforge branch: developed + cross-repo E2E verified (NeoForge-Support.aep).

### Session 2026-08-09 (quilt branch)
- [DONE] Merged main into quilt (picked up docs/extension-sdk-conventions.md).
- [DONE] quilt branch: QuiltSupportExtension (com.aprism.refract.quilt) +
  aprism.extension.json (loaderKey Q, loaderRange [0.29.0,0.30.0), provides
  quilt-loader) + self-contained Gradle build (settings.gradle via
  pluginManagement.includeBuild of ../Aprism/aprism-packaging, compileOnly
  against aprism-api-v26.0-Alpha.8.jar).
- [DONE] ./gradlew build + packageAep green; produced
  Quilt-Support-A[26.0,27.0)-Q[0.29,0.30)-JE-26.2.aep.
- [DONE] Cross-repo E2E verified: Aprism RefractQuiltAepE2ETest loads this
  branch's .aep through the real runtime and invokes a Quilt-style mod's
  init entrypoint (tests=1 skipped=0 failures=0, green).
- [NOTE] Quilt loader ships a built-in Fabric API compatibility layer, so
  Quilt mods implement net.fabricmc.api.ModInitializer and are dispatched via
  Aprism's Fabric-convention bridge; the Quilt-native init entrypoint key is
  projected to main during manifest projection. This branch carries only the
  .aep entrypoint class + manifest per the SDK conventions.
- [STATUS] quilt branch: developed + cross-repo E2E verified (Quilt-Support.aep).

### Session 2026-08-08 (foundation)
- [DONE] Repository initialized (LICENSE + README).
- [DONE] Rewrote README with branch model, versioning (mirrors Aprism),
  extension anatomy (.aep layout + aprism.extension.json fields), build and
  release/signing conventions, and the Aprism relationship statement.
- [DONE] Created FACT.md (this file) and .gitignore.
- [DONE] Configured repo-local SSH signing (same key as Aprism).
- [DONE] Created loader branches (fabric/neoforge/forge/quilt/liteloader)
  from main and pushed.
- [DONE] fabric branch: FabricSupportExtension + aprism.extension.json; build +
  test + package Fabric-Support.aep (bridge/shim live in Aprism loader-core).
- [DONE] neoforge branch: NeoForge-Support skeleton (developed alongside
  Aprism v26.0-Alpha.5).
