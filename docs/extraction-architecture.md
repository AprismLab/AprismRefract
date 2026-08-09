# Loader-Support Extraction Architecture

> v26.1-Alpha.6 | How foreign-loader support lives in AprismRefract, not in
> the Aprism core.

## Background

Aprism Loader is **native**: it does not natively understand Fabric, NeoForge,
Forge, Quilt, or LiteLoader mod formats. Historically the bridges that invoke
those loaders' entrypoint conventions lived inside `aprism-loader-core`. Per
the v26.1 roadmap they are extracted here so the core ships only the seam, and
each loader's support lives on its own Refract branch.

## The seam: `LoaderEntrypointHandler`

The core exposes two types in `com.aprism.loader.loaderext`:

| Type | Role |
|---|---|
| `LoaderEntrypointHandler` | SPI interface. A handler owns the entrypoint convention for one loader key. |
| `LoaderEntrypointRegistry` | Thread-safe registry keyed by loader key. |

```java
public interface LoaderEntrypointHandler {
    String loaderKey();                       // e.g. "Fa", "N", "Fo", "Q", "L"
    void invoke(LoadedModContainer mod, AprismPhase phase);
    default boolean isExclusive() { return true; }
}
```

At dispatch time `AprismRuntime.invokeModEntrypoint` consults the registry
**before** any built-in behaviour:

1. Look up `LoaderEntrypointRegistry.get(mod.loaderKey())`.
2. If a handler is registered, delegate `invoke(mod, phase)` to it.
3. If the handler is **exclusive** (`isExclusive() == true`), stop — the
   handler fully owns that loader's dispatch.
4. Otherwise fall through to the Aprism-native `IAprismMod` dispatch.

This means a Refract extension can fully own a loader's entrypoint lifecycle
without the core importing any loader bridge.

## Registering a handler from an extension

Loader-support extensions receive an `ExtensionContext`. Since v26.1-Alpha.6
the context exposes:

```java
void registerLoaderSupport(String loaderKey, String modFolder);      // folder
void registerEntrypointHandler(String loaderKey, Object handler);    // dispatch
```

`registerEntrypointHandler` validates that the handler implements
`LoaderEntrypointHandler` and that `handler.loaderKey()` matches the
registration key, then registers it. Handlers are cleared on
`AprismRuntime.shutdown()` so every load cycle starts clean.

> The API types the handler as `Object` because `aprism-api` must not depend
> on `aprism-loader-core` (that would be circular). The loader-core
> `ExtensionContextImpl` casts to the interface at registration time.

## What each branch owns

A Refract loader branch supplies everything loader-specific:

- The **entrypoint handler** implementing `LoaderEntrypointHandler` (invokes
  the loader's convention, e.g. Fabric's `onInitialize` / NeoForge's `@Mod`
  construction).
- Any **loader API shims** the handler needs on the classpath (e.g.
  `net.fabricmc.api.ModInitializer`, `net.neoforged.fml.common.Mod`).
- The **support extension** entrypoint (`IAprismExtension`) that registers the
  mod folder and the handler.
- The **`.aep` packaging** that bundles the above.

The Aprism core keeps only: loader-key constants, the `LoaderEntrypointHandler`
contract, the registry, and the Aprism-native fallback.

## Migration status

| Loader | Branch | Handler in Refract | Shim in Refract |
|---|---|---|---|
| Fabric | `fabric` | done (v26.0-Alpha.2) | done (v26.0-Alpha.2) |
| NeoForge | `neoforge` | done (v26.0-Alpha.2) | done (v26.0-Alpha.2) |
| Forge | `forge` | done (v26.0-Alpha.2) | done (v26.0-Alpha.2) |
| Quilt | `quilt` | done (v26.0-Alpha.2) | done (v26.0-Alpha.2) |
| LiteLoader | `liteloader` | done (v26.0-Alpha.2) | done (v26.0-Alpha.2) |

During the transition the core still ships built-in bridges as a fallback so
existing behaviour is preserved; once a branch's handler is registered it takes
precedence via the exclusive path.
