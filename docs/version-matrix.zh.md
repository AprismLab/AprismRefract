# AprismRefract 版本兼容矩阵（简体中文）

> 英文版为规范版本：[version-matrix.md](version-matrix.md)。
> v26.9 | 将 Minecraft 版本映射到加载器版本与 `.aep` 产物名。
> 维护者：BlockConnect@StarsailsClover

<!-- GitHub@NDBlockConnect | BlockConnect@StarsailsClover -->

## 如何阅读本矩阵

每行代表一个 `.aep` 变体。Aprism 运行时通过运行中的 MC 版本（`mcEdit` +
`mcVersion`）与目标加载器版本（`loaderRange`）匹配变体。

一台 MC 26.2（JE）+ MC 26.2 版 Fabric-Support `.aep` 的 Aprism 实例会
扫描 `fabric-mods/` 并分发 Fabric 模组。没有该 `.aep` 时，该文件夹被
静默忽略。

## Fabric（加载器键 `Fa`）

| MC 版本 | Fabric Loader 范围 | .aep 产物 |
|---|---|---|
| 26.2 | [0.16.0, 0.20.0) | `Fabric-Support-A[26.0,28.0)-Fa[0.16.0,0.20.0)-JE-26.2.aep` |
| 26.1.2 | [0.16.0, 0.20.0) | `Fabric-Support-A[26.0,28.0)-Fa[0.16.0,0.20.0)-JE-26.1.2.aep` |
| 1.21.10 | [0.16.0, 0.20.0) | `Fabric-Support-A[26.0,28.0)-Fa[0.16.0,0.20.0)-JE-1.21.10.aep` |
| 1.21.4 | [0.16.0, 0.20.0) | `Fabric-Support-A[26.0,28.0)-Fa[0.16.0,0.20.0)-JE-1.21.4.aep` |
| 1.20.6 | [0.15.0, 0.16.0) | `Fabric-Support-A[26.0,28.0)-Fa[0.15.0,0.16.0)-JE-1.20.6.aep` |
| 1.20.1 | [0.15.0, 0.16.0) | `Fabric-Support-A[26.0,28.0)-Fa[0.15.0,0.16.0)-JE-1.20.1.aep` |
| 1.16.5 | [0.14.0, 0.15.0) | `Fabric-Support-A[26.0,28.0)-Fa[0.14.0,0.15.0)-JE-1.16.5.aep` |

Fabric 仅支持 MC 1.14+。MC 1.8.9 至 1.12.2 不受 Fabric 支持；这些版本请
使用 Forge 或 LiteLoader。

<!-- GitHub@NDBlockConnect | BlockConnect@StarsailsClover -->

## NeoForge（加载器键 `N`）

| MC 版本 | NeoForge Loader 范围 | .aep 产物 |
|---|---|---|
| 26.2 | [21.4.0, 21.5.0) | `NeoForge-Support-A[26.0,28.0)-N[21.4.0,21.5.0)-JE-26.2.aep` |
| 26.1.2 | [21.4.0, 21.5.0) | `NeoForge-Support-A[26.0,28.0)-N[21.4.0,21.5.0)-JE-26.1.2.aep` |
| 1.21.10 | [21.4.0, 21.5.0) | `NeoForge-Support-A[26.0,28.0)-N[21.4.0,21.5.0)-JE-1.21.10.aep` |
| 1.21.4 | [21.4.0, 21.5.0) | `NeoForge-Support-A[26.0,28.0)-N[21.4.0,21.5.0)-JE-1.21.4.aep` |
| 1.20.6 | [21.0.0, 21.1.0) | `NeoForge-Support-A[26.0,28.0)-N[21.0.0,21.1.0)-JE-1.20.6.aep` |
| 1.20.1 | [21.0.0, 21.1.0) | `NeoForge-Support-A[26.0,28.0)-N[21.0.0,21.1.0)-JE-1.20.1.aep` |

NeoForge 于 MC 1.20.1 从 Forge 分叉。1.20.1 以下 MC 版本不受 NeoForge
支持；请使用 Forge。

## Forge（加载器键 `Fo`）

| MC 版本 | Forge Loader 范围 | .aep 产物 |
|---|---|---|
| 26.2 | [54.0.0, 55.0.0) | `Forge-Support-A[26.0,28.0)-Fo[54.0.0,55.0.0)-JE-26.2.aep` |
| 1.21.4 | [52.0.0, 53.0.0) | `Forge-Support-A[26.0,28.0)-Fo[52.0.0,53.0.0)-JE-1.21.4.aep` |
| 1.20.1 | [47.0.0, 48.0.0) | `Forge-Support-A[26.0,28.0)-Fo[47.0.0,48.0.0)-JE-1.20.1.aep` |
| 1.16.5 | [40.0.0, 41.0.0) | `Forge-Support-A[26.0,28.0)-Fo[40.0.0,41.0.0)-JE-1.16.5.aep` |
| 1.12.2 | [14.23.0, 14.24.0) | `Forge-Support-A[26.0,28.0)-Fo[14.23.0,14.24.0)-JE-1.12.2.aep` |
| 1.11.2 | [13.20.0, 13.21.0) | `Forge-Support-A[26.0,28.0)-Fo[13.20.0,13.21.0)-JE-1.11.2.aep` |
| 1.10.2 | [12.18.0, 12.19.0) | `Forge-Support-A[26.0,28.0)-Fo[12.18.0,12.19.0)-JE-1.10.2.aep` |
| 1.8.9 | [11.15.0, 11.16.0) | `Forge-Support-A[26.0,28.0)-Fo[11.15.0,11.16.0)-JE-1.8.9.aep` |

注：Forge 没有 MC 26.x 官方构建（NeoForge 于 1.20.1 分叉）。MC 26.2
变体使用 Forge 54.x（1.21.x 最新 Forge）以实现 Aprism 原生兼容；
MC 26.x 用户应优先选择 NeoForge。

<!-- GitHub@NDBlockConnect | BlockConnect@StarsailsClover -->

## Quilt（加载器键 `Q`）

| MC 版本 | Quilt Loader 范围 | .aep 产物 |
|---|---|---|
| 26.2 | [0.29.0, 0.30.0) | `Quilt-Support-A[26.0,28.0)-Q[0.29.0,0.30.0)-JE-26.2.aep` |
| 26.1.2 | [0.29.0, 0.30.0) | `Quilt-Support-A[26.0,28.0)-Q[0.29.0,0.30.0)-JE-26.1.2.aep` |
| 1.21.10 | [0.29.0, 0.30.0) | `Quilt-Support-A[26.0,28.0)-Q[0.29.0,0.30.0)-JE-1.21.10.aep` |
| 1.21.4 | [0.29.0, 0.30.0) | `Quilt-Support-A[26.0,28.0)-Q[0.29.0,0.30.0)-JE-1.21.4.aep` |
| 1.20.6 | [0.26.0, 0.27.0) | `Quilt-Support-A[26.0,28.0)-Q[0.26.0,0.27.0)-JE-1.20.6.aep` |
| 1.20.1 | [0.26.0, 0.27.0) | `Quilt-Support-A[26.0,28.0)-Q[0.26.0,0.27.0)-JE-1.20.1.aep` |
| 1.16.5 | [0.26.0, 0.27.0) | `Quilt-Support-A[26.0,28.0)-Q[0.26.0,0.27.0)-JE-1.16.5.aep` |

Quilt 与 Fabric 版本支持一致（MC 1.14+）。Quilt 模组通过 Quilt 内置
Fabric 兼容层实现 `net.fabricmc.api.ModInitializer`。

## LiteLoader（加载器键 `L`）

| MC 版本 | LiteLoader 范围 | .aep 产物 |
|---|---|---|
| 1.12.2 | [1.12.0, 1.13.0) | `LiteLoader-Support-A[26.0,28.0)-L[1.12.0,1.13.0)-JE-1.12.2.aep` |
| 1.11.2 | [1.11.0, 1.12.0) | `LiteLoader-Support-A[26.0,28.0)-L[1.11.0,1.12.0)-JE-1.11.2.aep` |
| 1.10.2 | [1.10.0, 1.11.0) | `LiteLoader-Support-A[26.0,28.0)-L[1.10.0,1.11.0)-JE-1.10.2.aep` |
| 1.8.9 | [1.8.0, 1.9.0) | `LiteLoader-Support-A[26.0,28.0)-L[1.8.0,1.9.0)-JE-1.8.9.aep` |

LiteLoader 仅支持 MC 1.7.10 至 1.12.2。不支持 MC 1.14+ 或 26.x。

<!-- GitHub@NDBlockConnect | BlockConnect@StarsailsClover -->

## 汇总：各加载器的 MC 版本覆盖

| MC 版本 | Fabric | NeoForge | Forge | Quilt | LiteLoader |
|---|---|---|---|---|---|
| 26.2 | 是 | 是 | （兼容） | 是 | 否 |
| 26.1.2 | 是 | 是 | 否 | 是 | 否 |
| 1.21.10 | 是 | 是 | 否 | 是 | 否 |
| 1.21.4 | 是 | 是 | 是 | 是 | 否 |
| 1.20.6 | 是 | 是 | 否 | 是 | 否 |
| 1.20.1 | 是 | 是 | 是 | 是 | 否 |
| 1.16.5 | 是 | 否 | 是 | 是 | 否 |
| 1.12.2 | 否 | 否 | 是 | 否 | 是 |
| 1.11.2 | 否 | 否 | 是 | 否 | 是 |
| 1.10.2 | 否 | 否 | 是 | 否 | 是 |
| 1.8.9 | 否 | 否 | 是 | 否 | 是 |

.aep 变体总数：7（Fabric）+ 6（NeoForge）+ 8（Forge）+ 7（Quilt）
+ 4（LiteLoader）= **32 个变体**。

<!-- GitHub@NDBlockConnect | BlockConnect@StarsailsClover -->
