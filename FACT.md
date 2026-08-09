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

## 4. Known Issues & Roadmap

### Critical Issues (v26.0-Alpha.2 blockers)

**ISSUE-001: Ambiguous MC version support in extension manifests**
- **Severity:** Critical
- **Impact:** Users cannot determine which .aep file works with their MC version
- **Root cause:** loaderRange specifies loader version but mcVersion:null implies
  "all MC versions" when in reality each loader version targets specific MC versions
- **Resolution:** Create MC-version-specific .aep variants with explicit mcVersion
  declarations; update artifact naming to include MC version segment

**ISSUE-002: Forge branch manifest misconfiguration**
- **Severity:** Critical
- **Impact:** forge branch produces NeoForge-Support.aep instead of Forge-Support.aep
- **Root cause:** forge/aprism.extension.json contains copy-pasted NeoForge config
  (loaderKey:"N", modFolder:"neoforge-mods/")
- **Resolution:** Correct manifest to loaderKey:"Fo", modFolder:"forge-mods/"

**ISSUE-003: Single loader version per extension limits MC version coverage**
- **Severity:** High
- **Impact:** Cannot support wide MC version range (1.8.9–26.2) with single .aep
  per loader
- **Example:** Fabric Loader 0.16.x supports MC 1.21.4+ but 0.15.x needed for
  1.20.x; Forge 14.23.x for 1.12.2 vs 52.x for 1.21.4
- **Resolution:** Multi-variant release strategy per loader branch

### v26.0-Alpha.2 Roadmap

**Release Goals:**
1. Fix forge branch manifest (ISSUE-002)
2. Implement MC-version-specific extension variants (ISSUE-001, ISSUE-003)
3. Publish version compatibility matrix documentation
4. Release 15+ .aep variants covering MC 1.8.9–26.2 target range

**Target MC Version Coverage:**
- Modern: 26.2, 26.1.2, 1.21.10, 1.21.4, 1.20.6, 1.20.1
- Legacy: 1.16.5, 1.12.2, 1.11.2, 1.10.2, 1.9.4, 1.8.9

**Loader Version Mapping (preliminary):**
```
Fabric:
  - [0.16.0,0.17.0) → MC 1.21.4, 1.21.10, 26.1.2, 26.2
  - [0.15.0,0.16.0) → MC 1.20.1, 1.20.6
  - [0.14.0,0.15.0) → MC 1.16.5
  - Not applicable to MC 1.8.9–1.12.2 (Fabric requires 1.14+)

NeoForge:
  - [21.4.0,21.5.0) → MC 1.21.4, 1.21.10, 26.1.2, 26.2
  - [21.0.0,21.1.0) → MC 1.20.1, 1.20.6
  - Not applicable to MC <1.20.1 (NeoForge fork started at 1.20.1)

Forge:
  - [52.0.0,53.0.0) → MC 1.21.4
  - [47.0.0,48.0.0) → MC 1.20.1
  - [40.0.0,41.0.0) → MC 1.16.5
  - [14.23.0,14.24.0) → MC 1.12.2
  - [13.20.0,13.21.0) → MC 1.11.2
  - [12.18.0,12.19.0) → MC 1.10.2
  - [11.15.0,11.16.0) → MC 1.8.9

Quilt:
  - [0.29.0,0.30.0) → MC 1.21.4+
  - [0.26.0,0.27.0) → MC 1.20.x
  - Not applicable to MC <1.14

LiteLoader:
  - [1.12.0,1.13.0) → MC 1.12.2
  - [1.11.0,1.12.0) → MC 1.11.2
  - [1.10.0,1.11.0) → MC 1.10.2
  - [1.8.0,1.9.0) → MC 1.8.9
```

## 5. Session Log

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

### Session 2026-08-10 (v26.0-Alpha.2 planning - version compatibility audit)
- [INVESTIGATION] Conducted comprehensive version compatibility audit across all
  five loader branches against Minecraft Java Edition 1.8.9–26.2 target range.
- [CRITICAL ISSUE A] Version ranges too narrow and MC version mapping unclear:
  Current loaderRange declarations (Fabric [0.16.0,0.17.0), NeoForge
  [21.4.0,21.5.0), etc.) cover only single minor versions; manifest fields
  mcEdit:null and mcVersion:null imply "all MC versions" but different MC
  versions require different loader versions (e.g., Fabric Loader 0.16.x supports
  MC 1.21.4+ but not 1.8.9–1.12.2).
- [CRITICAL ISSUE B] Forge branch manifest configuration error: forge branch's
  aprism.extension.json contains NeoForge configuration instead of Forge-specific
  settings (loaderKey:"N", modFolder:"neoforge-mods/").
- [ANALYSIS] MC version landscape for target range:
  * MC 1.20.x–26.x stable releases: 26.2, 26.1.2, 1.21.10, 1.21.4, 1.20.6, 1.20.1
  * MC 1.8.9–1.12.2 legacy stable: 1.12.2, 1.11.2, 1.10.2, 1.9.4, 1.8.9
  * Loader version mapping per MC version:
    - Fabric: 1.14+ only (no 1.12.2 support); 0.16.x → MC 1.21.4+; 0.15.x → 1.20.x
    - Forge: version-specific (e.g., 14.23.x → 1.12.2; 47.x → 1.20.1; 52.x → 1.21.4)
    - NeoForge: 1.20.1+ only; 21.4.x → MC 1.21.4
    - Quilt: mirrors Fabric version support
    - LiteLoader: 1.7.10–1.12.2 only (1.12.x → MC 1.12.2)
- [ANALYSIS] Aprism Loader native interface usage assessment:
  * Current ExtensionContext API surface is SUFFICIENT and elegantly minimal:
    registerLoaderSupport(loaderKey, modFolder) + event bus/registry/logger access.
  * All complex logic (FabricEntrypointBridge, NeoForgeEntrypointBridge reflection,
    manifest parsing, classloading) lives correctly in Aprism aprism-loader-core.
  * Extension .aep artifacts remain lightweight (few KB) with single-purpose design.
  * Entrypoint bridge architecture verified functional:
    - Fabric: reflectively invokes onInitialize/onInitializeClient/onInitializeServer
    - NeoForge/Forge: @Mod annotation discovery + IEventBus constructor injection
    - LiteLoader: LiteMod interface detection + init(File) invocation
  * No additional ExtensionContext methods required for current architecture.
- [DECISION] v26.0-Alpha.2 release focus: MC-version-specific extension variants
  with explicit loader version mapping.
- [PLAN] Alpha.2 implementation tasks:
  1. Fix forge branch manifest (loaderKey:"Fo", modFolder:"forge-mods/").
  2. Create MC-version-specific .aep variants per loader with precise loaderRange
     and mcVersion declarations (not null).
  3. Document version compatibility matrix (MC version → loader versions → .aep file).
  4. Update artifact naming convention to include MC version:
     <Loader>-Support-A[<aprismVer>)-<LoaderKey>[<loaderVer>)-JE-<mcVer>.aep
     Example: Fabric-Support-A[26.0,27.0)-Fa[0.16,0.17)-JE-1.21.4.aep
  5. Update build.gradle and release.yml to generate multiple artifacts per branch.
  6. Rebuild and E2E-verify all variants against corresponding MC + loader versions.

### Session 2026-08-10 (fabric, v26.0-Alpha.2) - Fabric translation layer extraction
- [DONE] Extracted the Fabric translation layer from the Aprism core into
  this branch, per the loader-support extraction architecture (Aprism
  v26.1-Alpha.6 seam; extraction-architecture.md on main):
  - Migrated FabricEntrypointBridge (reflective Fabric-convention invocation)
    into com.aprism.refract.fabric.
  - Bundled the Fabric API shims (net.fabricmc.api.ModInitializer /
    ClientModInitializer / DedicatedServerModInitializer) in this branch.
  - Added FabricEntrypointHandler implements LoaderEntrypointHandler: owns
    Fabric entrypoint dispatch (manifest-driven entrypoints, IAprismMod native
    path preserved, per-entrypoint isolation, instance retained on container).
  - FabricSupportExtension now registers the handler via
    context.registerEntrypointHandler("Fa", handler) - exclusive, so the core
    never runs any Fabric-specific code for loader key Fa.
- [DONE] Compile alignment bumped from aprism-api-v26.0-Alpha.8.jar to the
  v26.1-Alpha.8 triple (api + loader-core + manifest jars, compileOnly);
  branch-local test suite runs the real Aprism runtime (Mixin + gson as
  test dependencies) without any change to the Aprism repository.
- [DONE] Tests: 13 green (FabricEntrypointBridgeTest 5, FabricEntrypointHandlerTest
  6, FabricExtractionE2ETest 2, 0 skipped). The E2E asserts the registered
  handler is DEFINED BY the AprismClassLoader (loaded from the .aep's embedded
  jar) - the decisive proof the extraction, not any core fallback, owns Fabric
  dispatch.
- [NOTE] The Aprism core still ships its built-in Fabric bridge as a
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
