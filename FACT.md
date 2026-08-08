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
| forge | Forge | Fo | forge-mods/ | placeholder |
| quilt | Quilt | Q | quilt-mods/ | placeholder |
| liteloader | LiteLoader | L | liteloader-mods/ | placeholder |

## 3. Conventions

- Conventional Commits, SSH-signed commits and tags, no force-push to main.
- Build with Gradle; consume Aprism packaging plugin + API via
  pluginManagement.includeBuild of the Aprism workspace.
- No emoji in any artifact.

## 4. Session Log

### Session 2026-08-08 (foundation)
- [DONE] Repository initialized (LICENSE + README).
- [DONE] Rewrote README with branch model, versioning (mirrors Aprism),
  extension anatomy (.aep layout + aprism.extension.json fields), build and
  release/signing conventions, and the Aprism relationship statement.
- [DONE] Created FACT.md (this file) and .gitignore.
- [DONE] Configured repo-local SSH signing (same key as Aprism).
- [IN PROGRESS] Create loader branches (fabric/neoforge/forge/quilt/liteloader)
  from main and push.
- [TODO] fabric branch: migrate FabricSupportExtension + FabricEntrypointBridge
  + Fabric API shim from Aprism; build + test; package Fabric-Support.aep.
- [TODO] neoforge branch: NeoForge-Support skeleton (developed alongside
  Aprism v26.0-Alpha.5).
