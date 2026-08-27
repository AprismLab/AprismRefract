# Aprism Extension SDK Conventions (loader-support)

> AprismRefract shared document | lives on `main`, consumed by all loader branches
> Canonical language: English
> Tracks the Aprism core `.aep` contract. If the Aprism core contract changes,
> update THIS document first, then propagate to loader branches.

This document defines the normative conventions every loader-support `.aep`
extension in this repository must follow. It supplements (does not replace)
the Aprism core extension specification (Aprism docs 07/08 and FACT.md 9.14).

## 1. Extension lifecycle

- Extensions load BEFORE any mods (Aprism load phase 1).
- Aprism opens each `.aep` in the instance's `aprism-extensions/` folder,
  reads `aprism.extension.json`, validates it, extracts the embedded
  `extension.jar`, and instantiates the class named in `entrypoint`.
- The runtime then calls `IAprismExtension.onInitialize(ExtensionContext)`.
- The extension registers its capabilities via the context. For loader-support
  extensions the required registration is exactly one call:
  `context.registerLoaderSupport(loaderKey, modFolder)`.
- After all extensions initialized, Aprism scans each registered mod folder
  during mod discovery (phase 2).

## 2. What goes in a loader branch vs in Aprism core

This split is normative:

| Artifact | Lives in |
|---|---|
| The `IAprismExtension` implementation class (registers folder + key) | **this repo**, the loader branch |
| `aprism.extension.json` manifest | **this repo**, the loader branch |
| Entrypoint bridge (invokes the loader's mod entrypoint convention) | **Aprism `aprism-loader-core`** (`com.aprism.loader.bridge`) |
| Loader API shim interfaces (e.g. `net.fabricmc.api.*`, `net.neoforged.fml.common.Mod`) | **Aprism `aprism-loader-core`** (so the runtime can define/instantiate loader mods without the real loader present) |
| Loader manifest reader (projects loader-native manifest to Aprism manifest) | **Aprism `aprism-manifest`** (`com.aprism.manifest.fallback`) |
| Loader key + mod folder constants on the Aprism side | **Aprism `aprism-loader-core`** (`ModDiscoverer`) |

Rationale: the runtime needs the shim interfaces and the entrypoint bridge to
instantiate and drive loader mods in-process; keeping them in Aprism core means
the `.aep` only has to declare the folder and key. If a future loader needs
bridge logic that genuinely belongs in the extension (e.g. a loader runtime that
must be bundled into the `.aep`), put that logic in the loader branch and
document the deviation here.

## 3. Loader key and folder registry (authoritative)

| Branch | Loader | Key | Mod folder | Manifest projected | Entrypoint convention |
|---|---|---|---|---|---|
| fabric | Fabric | `Fa` | `fabric-mods/` | `fabric.mod.json` -> AprismManifest | `entrypoints.main/client/server` (no-arg `onInitialize`-style methods, invoked via `FabricEntrypointBridge`) |
| neoforge | NeoForge | `N` | `neoforge-mods/` | `META-INF/neoforge.mods.toml` -> AprismManifest | `@Mod` annotated class, constructor injection (via `NeoForgeEntrypointBridge`) |
| forge | Forge | `Fo` | `forge-mods/` | `META-INF/mods.toml` -> AprismManifest (section-aware; honors `mandatory`) | `@Mod` annotated class, IEventBus constructor injection (via `ForgeEntrypointBridge`) |
| quilt | Quilt | `Q` | `quilt-mods/` | `quilt.mod.json` -> AprismManifest (`quilt_loader` block; `init` key -> `main`) | entrypoints (Fabric-compatible: Quilt ships a built-in Fabric API compat layer; dispatched via `FabricEntrypointBridge`) |
| liteloader | LiteLoader | `L` | `liteloader-mods/` | `litemod.json` -> AprismManifest | `LiteMod` interface implementation, `init(File)` (via `LiteLoaderEntrypointBridge`) |

Keys and folders are reserved per Aprism FACT.md 9.14. Do not introduce new
keys without updating Aprism `ModDiscoverer` and this table together.

## 4. Extension manifest (`aprism.extension.json`) template

```json
{
  "extensionId": "<loader>-support",
  "version": "26.9.0-alpha.1",
  "type": "loader-support",
  "aprismRange": "[26.0.0,27.0.0)",
  "loaderKey": "<Key>",
  "loaderRange": "[<loaderMin>,<loaderMax>)",
  "mcEdit": null,
  "mcVersion": null,
  "entrypoint": "com.aprism.refract.<loader>.<Loader>SupportExtension",
  "provides": ["<loader>-loader"],
  "depends": {}
}
```

Field rules:
- `type` MUST be `loader-support`.
- `version` SHOULD identify the extension artifact's own SemVer version. It is
  consumed by newer Aprism cores for extension dependency range matching.
- `loaderKey` MUST match the key in Section 3 and the constant registered by
  the extension's `onInitialize`.
- `aprismRange` is a SemVer range of Aprism core versions; keep it aligned with
  the Aprism minor line the branch is built against.
- `loaderRange` is a SemVer range of the TARGET loader runtime (e.g. Fabric
  Loader versions), used for compatibility messaging.
- `entrypoint` MUST be the fully-qualified `IAprismExtension` implementation.
- `provides` SHOULD include `<loader>-loader` so mods depending on the loader
  environment can resolve it.

## 4a. Optional AprismWarp editor catalog

The current AEP format remains a ZIP container with the runtime manifest and
embedded extension jar at its root. A loader-support branch MAY include a
second root-level file named `aprismwarp.editor.json`:

```json
{
  "schema": "aprismwarp.aep-editor/v1",
  "extensionId": "<loader>-support",
  "version": "26.9.0-alpha.1",
  "requires": {
    "aprismRange": ">=26.8.0",
    "workTypes": ["AprismExtension"]
  },
  "capabilities": []
}
```

Rules:

- The catalog MUST be at the AEP ZIP root and MUST be named exactly
  `aprismwarp.editor.json`.
- `schema` MUST be `aprismwarp.aep-editor/v1`.
- `extensionId` SHOULD match `aprism.extension.json`.
- `capabilities` is an editor declaration only; it is not an executable
  extension entrypoint and MUST NOT be loaded by Aprism runtime.
- The catalog is optional metadata for AprismWarp. Its absence MUST NOT make
  an otherwise valid AEP uninstallable or unloadable.
- The packaging configuration is `aprismPackaging.editorManifestFile =
  'aprismwarp.editor.json'`.

<!-- GitHub@NDBlockConnect | BlockConnect@StarsailsClover -->

## 5. Entrypoint class template

```java
package com.aprism.refract.<loader>;

import com.aprism.api.ExtensionContext;
import com.aprism.api.IAprismExtension;

public final class <Loader>SupportExtension implements IAprismExtension {
    public static final String KEY = "<Key>";
    public static final String FOLDER = "<loader>-mods";

    @Override
    public void onInitialize(ExtensionContext context) {
        context.registerLoaderSupport(KEY, FOLDER);
    }
}
```

Rules:
- Class is `final`, package `com.aprism.refract.<loader>`.
- No loader-native types in the entrypoint class signature (it must load even
  when the real loader runtime is absent).
- Registration is the ONLY side effect of `onInitialize`.

## 6. Build conventions (per branch)

- Self-contained Gradle build at branch root; consume the Aprism packaging
  plugin + API/manifest via `pluginManagement.includeBuild` of the Aprism
  workspace.
- `./gradlew build` (compile + tests) and `./gradlew packageAep` (assemble
  the `.aep`).
- Cross-repo E2E test lives in the **Aprism** repo
  (`Refract<Loader>AepE2ETest`): it loads the branch-built `.aep` through the
  real Aprism runtime. Keep this test green when changing the branch.
- Signed commits + signed tags, Conventional Commits, no force-push.

## 7. Artifact naming and release

Per Aprism FACT.md 9.14:
`<Purpose>-A<AprismVerRange>-<LoaderKey><LoaderVerRange>-<MCEdit>-<MCVer>.aep`
e.g. `Fabric-Support-A[26.0,27.0)-Fa[0.16,0.17)-JE-1.21.4.aep`.

Release flow mirrors Aprism: SSH-signed tag `v26.0-Alpha.<n>`, GitHub workflow
builds the `.aep`, signs with cosign keyless, attaches `checksums.txt` +
`.sig` + `.bundle` + CycloneDX SBOM, publishes as a Pre-Release.

## 8. Version alignment rule

An AprismRefract build at `v26.0-Alpha.<n>` is built against the Aprism core
`v26.0-Alpha.<n>` API surface. If Aprism core's extension/loader-support
contract changes, bump the AprismRefract minor or Alpha to re-align and note it
in FACT.md.

## 9. Change log

| Date | Change |
|---|---|
| 2026-08-09 | Initial SDK conventions document created on main. Clarified that entrypoint bridges and loader API shims live in Aprism `aprism-loader-core`, while the `.aep` entrypoint class + manifest live in this repository's loader branches. |
| 2026-08-09 | Refined the Section 3 loader registry to the exact implemented conventions: Forge section-aware `mods.toml` (honors `mandatory`) + IEventBus constructor injection; Quilt `quilt_loader` block with `init` -> `main` projection and Fabric-compatible dispatch (Quilt's built-in Fabric API compat layer); LiteLoader `LiteMod` interface discovery + `init(File)`. All five loader branches are now developed. |
| 2026-08-28 | Adopted the AprismWarp editor catalog extension: optional root-level `aprismwarp.editor.json` using `aprismwarp.aep-editor/v1`; added extension self-version guidance and packaging DSL convention. |
