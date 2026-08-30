# Aprism 扩展 SDK 规范（loader-support）（简体中文）

> 英文版为规范版本：[extension-sdk-conventions.md](extension-sdk-conventions.md)。
> AprismRefract 共享文档 | 位于 `main`，供所有加载器分支使用
> 跟踪 Aprism 核心 `.aep` 契约。若 Aprism 核心契约变更，先更新本文档，
> 再传播到加载器分支。

<!-- GitHub@NDBlockConnect | BlockConnect@StarsailsClover -->

本文档定义本仓库每个 loader-support `.aep` 扩展必须遵循的规范约定。它
补充（而非取代）Aprism 核心扩展规范（Aprism docs 07/08 与 FACT.md 9.14）。

## 1. 扩展生命周期

- 扩展在任何模组之前加载（Aprism 加载 phase 1）。
- Aprism 打开实例 `aprism-extensions/` 文件夹中的每个 `.aep`，读取
  `aprism.extension.json`，校验，解出内嵌 `extension.jar`，实例化
  `entrypoint` 指定的类。
- 运行时随后调用 `IAprismExtension.onInitialize(ExtensionContext)`。
- 扩展通过 context 注册能力。对 loader-support 扩展，必需注册恰好一次：
  `context.registerLoaderSupport(loaderKey, modFolder)`。
- 所有扩展初始化完成后，Aprism 在模组发现阶段（phase 2）扫描每个已注册
  模组文件夹。

## 2. 加载器分支与 Aprism 核心的职责划分

以下划分为规范：

| 产物 | 所在位置 |
|---|---|
| `IAprismExtension` 实现类（注册文件夹 + 键） | **本仓库**，加载器分支 |
| `aprism.extension.json` 清单 | **本仓库**，加载器分支 |
| 入口桥接器（按加载器约定调用模组入口） | **Aprism `aprism-loader-core`**（`com.aprism.loader.bridge`） |
| 加载器 API 垫片接口（如 `net.fabricmc.api.*`、`net.neoforged.fml.common.Mod`） | **Aprism `aprism-loader-core`**（运行时无需真实加载器即可定义/实例化加载器模组） |
| 加载器清单读取器（加载器原生清单 -> Aprism 清单） | **Aprism `aprism-manifest`**（`com.aprism.manifest.fallback`） |
| Aprism 侧加载器键 + 模组文件夹常量 | **Aprism `aprism-loader-core`**（`ModDiscoverer`） |

理由：运行时需要垫片接口与入口桥接器才能在进程内实例化并驱动加载器模组；
把它们留在 Aprism 核心，`.aep` 就只需声明文件夹与键。若未来某加载器需要
真正属于扩展的桥接逻辑（例如必须捆绑进 `.aep` 的加载器运行时），把该逻辑
放入加载器分支并在此记录偏差。

<!-- GitHub@NDBlockConnect | BlockConnect@StarsailsClover -->

## 3. 加载器键与文件夹注册表（权威）

| 分支 | 加载器 | 键 | 模组文件夹 | 投影清单 | 入口约定 |
|---|---|---|---|---|---|
| fabric | Fabric | `Fa` | `fabric-mods/` | `fabric.mod.json` -> AprismManifest | `entrypoints.main/client/server`（无参 `onInitialize` 风格方法，经 `FabricEntrypointBridge` 调用） |
| neoforge | NeoForge | `N` | `neoforge-mods/` | `META-INF/neoforge.mods.toml` -> AprismManifest | `@Mod` 注解类，构造注入（经 `NeoForgeEntrypointBridge`） |
| forge | Forge | `Fo` | `forge-mods/` | `META-INF/mods.toml` -> AprismManifest（分节感知；遵守 `mandatory`） | `@Mod` 注解类，IEventBus 构造注入（经 `ForgeEntrypointBridge`） |
| quilt | Quilt | `Q` | `quilt-mods/` | `quilt.mod.json` -> AprismManifest（`quilt_loader` 块；`init` 键 -> `main`） | 入口点（Fabric 兼容：Quilt 内置 Fabric API 兼容层；经 `FabricEntrypointBridge` 分发） |
| liteloader | LiteLoader | `L` | `liteloader-mods/` | `litemod.json` -> AprismManifest | `LiteMod` 接口实现，`init(File)`（经 `LiteLoaderEntrypointBridge`） |

键与文件夹按 Aprism FACT.md 9.14 预留。不得在不同步更新 Aprism
`ModDiscoverer` 与本表的情况下引入新键。

## 4. 扩展清单（`aprism.extension.json`）模板

```json
{
  "extensionId": "<loader>-support",
  "version": "26.9.0-alpha.2",
  "type": "loader-support",
  "aprismRange": "[26.0.0,28.0.0)",
  "loaderKey": "<Key>",
  "loaderRange": "[<loaderMin>,<loaderMax>)",
  "mcEdit": "JE",
  "mcVersion": "<mcVer>",
  "entrypoint": "com.aprism.refract.<loader>.<Loader>SupportExtension",
  "provides": ["<loader>-loader"],
  "depends": {}
}
```

字段规则：
- `type` 必须为 `loader-support`。
- `version` 应标识扩展产物自身的 SemVer 版本。较新的 Aprism 核心将其用于
  扩展依赖范围匹配。
- `loaderKey` 必须与第 3 节注册表及扩展 `onInitialize` 注册的常量一致。
- `aprismRange` 是 Aprism 核心版本的 SemVer 范围；与分支构建所对齐的
  Aprism 次要线保持一致。
- `loaderRange` 是目标加载器运行时的 SemVer 范围（如 Fabric Loader
  版本），用于兼容性提示。
- `entrypoint` 必须是全限定 `IAprismExtension` 实现。
- `provides` 应包含 `<loader>-loader`，使依赖该加载器环境的模组可解析。

<!-- GitHub@NDBlockConnect | BlockConnect@StarsailsClover -->

## 4a. 可选 AprismWarp 编辑器目录

当前 AEP 格式仍是以运行时清单与内嵌扩展 jar 为根的 ZIP 容器。
loader-support 分支可以包含第二个根级文件 `aprismwarp.editor.json`：

```json
{
  "schema": "aprismwarp.aep-editor/v1",
  "extensionId": "<loader>-support",
  "version": "26.9.0-alpha.2",
  "requires": {
    "aprismRange": ">=26.8.0",
    "workTypes": ["AprismExtension"]
  },
  "capabilities": []
}
```

规则：

- 目录必须位于 AEP ZIP 根且必须精确命名为 `aprismwarp.editor.json`。
- `schema` 必须为 `aprismwarp.aep-editor/v1`。
- `extensionId` 应与 `aprism.extension.json` 一致。
- `capabilities` 仅为编辑器声明；不是可执行扩展入口点，Aprism 运行时
  不得加载。
- 该目录是 AprismWarp 的可选元数据。缺失时不得使原本有效的 AEP 无法
  安装或加载。
- 打包配置为 `aprismPackaging.editorManifestFile =
  'aprismwarp.editor.json'`。

<!-- GitHub@NDBlockConnect | BlockConnect@StarsailsClover -->

## 5. 入口类模板

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

## 6. 分支工程纪律

- 每个分支是独立 Gradle 构建；编译对齐来自同级 Aprism 检出（API/
  loader-core/manifest jar，compileOnly）。
- 分支本地测试套件在真实 Aprism 运行时上运行，无需改动 Aprism 仓库。
- 跨仓库 E2E 测试位于 **Aprism** 仓库（`Refract<Loader>AepE2ETest`）：
  通过真实 Aprism 运行时加载分支构建的 `.aep`。变更分支时保持该测试
  通过。
- 签名提交 + 签名标签，Conventional Commits，禁止强推。

<!-- GitHub@NDBlockConnect | BlockConnect@StarsailsClover -->

## 7. 产物命名与发布

按 Aprism FACT.md 9.14：
`<Purpose>-A<AprismVerRange>-<LoaderKey><LoaderVerRange>-<MCEdit>-<MCVer>.aep`
例：`Fabric-Support-A[26.0,28.0)-Fa[0.16.0,0.20.0)-JE-26.2.aep`。

发布流程沿用 Aprism：SSH 签名标签 `<loader>/v26.9-Alpha.<n>`（加载器
前缀用于区分五分支同版本标签），GitHub 工作流构建全部 `.aep` 变体，逐一
以 cosign 无密钥签名，附 `checksums.txt` + `.sig` + `.bundle` +
CycloneDX SBOM，以 Pre-Release 发布。

## 8. 版本对齐规则

AprismRefract 构建在构建时从同级 Aprism 检出的 `gradle.properties` 动态
解析 Aprism 核心对齐。清单声明的 `aprismRange`（当前
`[26.0.0,28.0.0)`）覆盖分支构建所对齐的 Aprism 核心线（当前 Aprism
`v26.8-Alpha.8`）。Refract 次要版本可以领先核心次要版本；兼容性契约是
范围，而非版本配对规则。若 Aprism 核心的扩展/加载器支持契约发生不兼容
变更，提升 `aprismRange` 并在 FACT.md 记录。

<!-- GitHub@NDBlockConnect | BlockConnect@StarsailsClover -->

## 9. 变更日志

| 日期 | 变更 |
|---|---|
| 2026-08-09 | main 上创建初始 SDK 规范文档。明确入口桥接器与加载器 API 垫片位于 Aprism `aprism-loader-core`，`.aep` 入口类与清单位于本仓库加载器分支。 |
| 2026-08-09 | 将第 3 节加载器注册表细化到已实现约定：Forge 分节感知 `mods.toml`（遵守 `mandatory`）+ IEventBus 构造注入；Quilt `quilt_loader` 块 `init` -> `main` 投影与 Fabric 兼容分发（Quilt 内置 Fabric API 兼容层）；LiteLoader `LiteMod` 接口发现 + `init(File)`。五个加载器分支均已开发。 |
| 2026-08-28 | 采纳 AprismWarp 编辑器目录扩展：可选根级 `aprismwarp.editor.json`（`aprismwarp.aep-editor/v1`）；新增扩展自版本指引与打包 DSL 约定。 |
| 2026-08-28 | 第 4a 节加入 `v26.9-Alpha 2` AEP 格式适配：每个分支的每个 AEP 变体必须携带编辑器目录，`aprismRange` 须匹配 Aprism v26.8，扩展清单须声明 `version` 字段。32/32 变体结构验证通过；经 workflow_dispatch 发布五个 Pre-Release。 |
| 2026-08-28 | 严格审计整改：第 4 节模板更新至交付现实（`version` 26.9.0-alpha.2、`aprismRange` [26.0.0,28.0.0)、显式 `mcEdit`/`mcVersion`）；将过期的无前缀标签方案替换为加载器前缀方案；第 8 节改写为动态 Aprism 核心对齐（基于范围的契约，非版本配对）。 |
| 2026-08-28 | 规范 Alpha 记法：文档/显示使用空格形式 `v26.9-Alpha 2`；点式仅用于发布标签、产物文件名与命令行；清单 SemVer 保持 `26.9.0-alpha.2`。已在本地 allowed signers 登记实际 `aprism_signing` 密钥，修复 SSH 签名身份归属。 |

<!-- GitHub@NDBlockConnect | BlockConnect@StarsailsClover -->
