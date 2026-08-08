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

### Session 2026-08-09 (v26.0-Alpha.1 release)
- [DONE] Merged main (docs + CI workflows) into all five loader branches;
  resolved status-table merge conflicts by adopting main's all-developed table.
- [DONE] Version alignment per SDK conventions Section 8: bumped fabric
  (Alpha.5 -> Alpha.8) and neoforge (Alpha.4 -> Alpha.8) compile-only API
  dependencies to aprism-api-v26.0-Alpha.8.jar; rebuilt and re-verified both
  .aep artifacts.
- [DONE] Rebuilt all five loader-support .aep artifacts against the Aprism
  v26.0-Alpha.8 API surface; all five Aprism cross-repo E2E tests green
  (RefractFabric/NeoForge/Forge/Quilt/LiteLoaderAepE2ETest: tests=1
  skipped=0 failures=0 each; Aprism full suite 256 tests, 0 failures).
- [DONE] Added .github/workflows/build.yml (per-branch CI) and
  release.yml (tag-triggered Pre-Release pipeline) on main; merged into all
  five loader branches. Fixed release.yml gh commands to pass --repo
  (workspace root is not a git repo because both checkouts use path:).
- [DONE] Release tag scheme: loader-prefixed tags (<loader>/v26.0-Alpha.<n>)
  so same-version tags of different loaders never collide; documented in
  README and SDK conventions. Note: a tag only triggers the workflow if the
  tagged commit contains the workflow, so tags were (re)created after merging
  the workflows into the loader branches.
- [DONE] Published FIVE signed Pre-Releases at v26.0-Alpha.1 (each with the
  .aep, checksums.txt SHA-256, cosign keyless .sig + .bundle, and CycloneDX
  SBOM): fabric, neoforge, forge, quilt, liteloader. Downloaded the forge
  artifact and verified its SHA-256 against checksums.txt: match.
- [STATUS] All five loader branches: developed, E2E-verified, released at
  v26.0-Alpha.1. AprismRefract loader-support matrix is complete for the
  v26.0-Alpha.1 cycle.

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
