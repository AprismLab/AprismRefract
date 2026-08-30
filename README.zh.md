# AprismRefract（简体中文说明）

> 英文版为规范版本：[README.md](README.md)。
> 若两版有出入，以英文版为准。

<!-- GitHub@NDBlockConnect | BlockConnect@StarsailsClover -->

Aprism | 加载器支持 `.aep` 扩展，使 Aprism Loader 能够加载为其他
Minecraft 模加载器（Fabric、NeoForge、Forge、Quilt、LiteLoader）设计的
模组，并让它们协同工作。

> 作者：BlockConnect@StarsailsClover | 许可证：Apache-2.0
> [Aprism](https://github.com/NDBlockConnect/Aprism) 的配套仓库。

## 本仓库是什么

Aprism Loader 是原生的，并不原生理解其他加载器的模组格式。加载器支持
完全通过 Aprism 扩展（`.aep`，类型 `loader-support`）交付。本仓库就是
这些扩展的所在地。每个加载器拥有自己的**分支**；Aprism 核心保留在
Aprism 仓库中。

## 分支模型（每分支一个加载器）

| 分支 | 加载器 | 加载器键 | 模组文件夹 | 状态 |
|---|---|---|---|---|
| `main` | 共享骨架、文档、扩展 SDK 规范 | - | - | 基础 |
| `fabric` | Fabric | `Fa` | `fabric-mods/` | 已开发（自 Aprism 迁移） |
| `neoforge` | NeoForge | `N` | `neoforge-mods/` | 已开发 |
| `forge` | Forge | `Fo` | `forge-mods/` | 已开发 |
| `quilt` | Quilt | `Q` | `quilt-mods/` | 已开发 |
| `liteloader` | LiteLoader | `L` | `liteloader-mods/` | 已开发 |

规则：

- `main` 只保存跨加载器内容：本 README、FACT.md、扩展 SDK 规范文档和
  共享 Gradle 脚手架。不含加载器代码。
- 每个加载器分支基于 `main`，只携带自己的加载器支持实现。跨加载器共享
  代码先进入 `main`，再由各分支获取。
- 同一加载器的加载器支持扩展从不在两个分支上共存；分支本身就是该加载器
  支持的身份。

<!-- GitHub@NDBlockConnect | BlockConnect@StarsailsClover -->

## 版本控制

沿用 Aprism 方案（Aprism 仓库 FACT.md 第 5 节）：

- 格式：`v<Year>.<minor>[-Alpha <n>]`；`v26` = 2026 线，含十个次要版本
  `v26.0`-`v26.9`；每个次要版本的 Alpha 1-Alpha 9 作为 GitHub
  Pre-Release；纯数字 = 次要版正式版（GitHub Release）；年度版
  `v26.2026` 于每年十二月发布。
- Alpha 记法：文档与显示使用空格形式（`v26.9-Alpha 2`）。点式
  （`v26.9-Alpha.2`）仅用于空格非法的场景：发布标签、产物文件名、命令行
  参数。清单 SemVer 保持点式小写（`26.9.0-alpha.2`）。
- 扩展产物命名遵循 Aprism FACT.md 9.14：
  `<Purpose>-A<AprismVerRange>-<LoaderKey><LoaderVerRange>-<MCEdit>-<MCVer>.aep`
  例：`Fabric-Support-A[26.0,28.0)-Fa[0.16.0,0.20.0)-JE-26.2.aep`。
- 接口契约：只允许单调递增；允许带通知地弃用；绝不移除/重命名。

## 扩展结构（loader-support .aep）

```
<name>.aep (ZIP)
  aprism.extension.json      # extensionId、version、type、aprismRange、
                             # loaderRange、mcEdit、mcVersion、entrypoint、
                             # provides
  aprismwarp.editor.json     # 可选（自 v26.9-Alpha.2）：AprismWarp 编辑器
                             # 目录，schema 为 aprismwarp.aep-editor/v1；
                             # 由 AprismWarp 读取，Aprism 运行时忽略
  extension.jar              # 入口类 + 加载器桥接代码
```

`aprism.extension.json` 字段（loader-support）：

- `extensionId`：如 `fabric-support`
- `version`：扩展自身版本（如 `26.9.0-alpha.2`），用于依赖范围匹配
  （自 v26.9-Alpha.2 起）
- `type`：`loader-support`
- `aprismRange`：本扩展支持的 Aprism 版本 SemVer 范围
  （当前为 `[26.0.0,28.0.0)`）
- `loaderRange`：目标加载器运行时的 SemVer 范围（如 Fabric Loader 版本）
- `mcEdit`：`JE`（按 Aprism 9.16，不存在 BE 加载器支持）
- `mcVersion`：该变体显式目标 MC 版本（32 变体构建系统为每个 MC 版本
  生成一个 .aep；null = 任意）
- `entrypoint`：`IAprismExtension` 实现类全名
- `provides`：能力声明（可选）

<!-- GitHub@NDBlockConnect | BlockConnect@StarsailsClover -->

加载时，扩展的 `onInitialize(ExtensionContext)` 通过
`context.registerLoaderSupport(key, folder)` 注册其加载器键与模组文件夹。
Aprism 随后在模组发现阶段（phase 2）扫描该文件夹，把加载器原生清单投影
为 Aprism 清单，并通过随本扩展交付的桥接器按该加载器自身的约定调用
入口点。

## 构建与验证

每个加载器分支都是独立的 Gradle 构建。在分支根目录：

```
./gradlew build          # 编译 + 测试
./gradlew packageAep     # 组装 .aep（打包插件来自 Aprism）
```

打包插件与 Aprism API/manifest 依赖来自 Aprism 构建（分支
settings.gradle 中的 `pluginManagement.includeBuild`）。签名提交 +
签名标签为强制要求（Conventional Commits）。

## 发布与签名

- 标签：加载器前缀，`<loader>/v26.9-Alpha.<n>`（正式次要版为
  `<loader>/v26.9`），SSH 签名。加载器前缀用于区分五个加载器分支上的
  同版本标签；版本号本身不加改变地沿用 Aprism 方案。例：
  `forge/v26.9-Alpha.2`。当前版本线：`v26.9-Alpha 2`（五个签名
  Pre-Release，共 32 个 `.aep` 变体）。
- 产物：`.aep`、`checksums.txt`（SHA-256）、cosign 无密钥签名
  （`.sig` + `.bundle`）、CycloneDX SBOM；Alpha 构建以 GitHub
  Pre-Release 发布，正式版以 GitHub Release 发布。
- 下载后验证：先校验和匹配，再
  `cosign verify-blob <name>.aep --bundle <name>.aep.bundle
  --certificate-identity-regexp https://github.com/NDBlockConnect/AprismRefract
  --certificate-oidc-issuer https://token.actions.githubusercontent.com`。

<!-- GitHub@NDBlockConnect | BlockConnect@StarsailsClover -->

## 与 Aprism 的关系

- Aprism 核心（agent、类加载器、运行时、Mixin、重映射、打包）保留在
  Aprism 仓库。自加载器支持抽取（Aprism v26.1-Alpha.6 接缝，于
  AprismRefract v26.0-Alpha.2 完成）后，核心只交付
  `LoaderEntrypointHandler` SPI + 注册表与 Aprism 原生回退实现；不再
  携带任何加载器专属翻译层。
- 本仓库的每个加载器分支提供自己的翻译层：入口桥接器、加载器 API 垫片
  接口（`net.fabricmc.api.*`、`net.neoforged.fml.common.Mod`、
  `net.minecraftforge.fml.common.Mod`、
  `com.mumfrey.liteloader.core.LiteMod`）、`LoaderEntrypointHandler`
  实现以及 `.aep` 打包。参见
  [docs/extraction-architecture.md](docs/extraction-architecture.md)
  （接缝设计）与
  [docs/extension-sdk-conventions.md](docs/extension-sdk-conventions.md)
  （规范分工）。
- 版本对齐：AprismRefract `v26.<minor>-Alpha.<n>` 构建时从同级 Aprism
  检出的 `gradle.properties` 动态解析 Aprism 核心对齐（动态对齐；当前
  为 Aprism `v26.8-Alpha.8`，由 `aprismRange` `[26.0.0,28.0.0)` 覆盖）。

<!-- GitHub@NDBlockConnect | BlockConnect@StarsailsClover -->
