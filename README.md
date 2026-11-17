# GTNH → MC 1.21.1 移植工程（gtverse）

把 **GregTech: New Horizons 2.9.0**（Minecraft 1.7.10 / Forge / 244 个 mod / 517 MB）迁到
**Minecraft 1.21.1 + NeoForge 21.1.266**，以 **GTCEu 7.0.2** 作为运行载体。

目标不是「数量级等价移植」，而是**保留 GTNH 的命名空间与观感**：物品 id 不变、贴图造型不变、
配方数据可追、启动界面尽量贴近。

---

## 一、最终选定的路线

| 项目 | 结论 |
|---|---|
| 运行版本 | 先锁 **1.21.1**（后续再升，升级成本集中在 Legacy Connector 的魔改） |
| 运行载体 | **GTCEu 7.0.2** 爆改，而非移植 GT5u 全量 |
| 命名空间 | **全量保留**（`gtceu` / `gregtech` / `tconstruct` 等原样） |
| 升级路径 | 魔改 Legacy Connector 使其支持新版本，而非每版重写移植层 |
| TiC | 方案 C：单 id + data component，保留 `tconstruct` 前缀 |

### 为什么不直接移植 GT5u

拆 `gregtech-5.09.54.265.jar` 实测：**65.0 MB / 24,387 条目 / 7,241 class / 12,290 png**，
其中含 GTNH 专属 fork 1,388 class。GTCEu 只有 1,962 class，是 **3.7 倍**差距，
且要同步改六个子系统（配方、材料、贴图、GUI、机器、电网）。量级上不可达。

---

## 二、已完成的工作

### 1. 材料图标 GTNH 化（已验收）

- **问题**：GTCEu 的 dust/ingot 造型是「锥形颗粒/细长高光」，与 GTNH 的「圆润粉堆/宽厚方正锭」完全不同。
- **根因**：GTCEu 没有 per-material 贴图，走 `material_sets/<iconset>/<form>` 模板 + 代码 tint。
- **资源包路线被判死**：`GTDynamicResourcePack` 注册的 `gtceu:dynamic_assets` 在资源栈里**恒排第一**，
  任何外挂资源包都压不过它 → 只能改 jar 内源贴图。
- **最终做法**：`patch_gtceu_icons.py` 替换 jar 内 **442 张**贴图（243 实图 + 199 透明叠加层），
  以 GT5u `NONE` 系列为基准（`NONE/dust` 哈希 `a85d2a7eaefb`、`NONE/ingot` 哈希 `19726d11411d`）。
- **产物**：`gtceu-1.21.1-7.0.2-gtnh-icons.jar`（16,315,974 B）。用户实测确认锭宽厚方正、粉圆润。

### 2. 配方数据层

- 自研 **v8 导出器**（全反射 + `FMLLoadCompleteEvent` + 30s 等待 + 探测式注册名 + `waitForItemList()`），
  替代不可修复的旧 `notenoughrecipedumps`。
- 导出结果：**GT 配方 187,204 条 / 91.9 MB**、items 57,035 条、NEI 59,873 条、TiC 材料 184 条。
- `id_map.json` v4：**70,685 条 / 508,927 引用 / 29.4 MB**，
  置信度分布 high 5,231 / medium 35,942 / low 19,404 / manual 10,108。
- 命名空间分布：`gtceu` 39.8% / `gtverse` 34.6%。

### 3. TiC 兼容层（方案 C）

- 保留 `tconstruct` 前缀、单 id + data component。
- 缺口 6,057 条收敛为 **126 个部件**。
- 产出：3 个 Java 源文件 + 126 个模型 json + 中英 lang 各 126 条 + 106 B 占位贴图。
- **产物**：`tconstruct-compat-0.1.0.jar`（48,839 B）。
- 踩坑记录：`modId ticcompat 不存在` 崩溃，根因是三处 MODID 常量不一致，收敛为单一来源后修复。

### 4. GTCEu 的 forge_data 修补

- NeoForge 21.1.265 的 `BlockElement.java` **硬拒绝** `forge_data` 键。
- GTCEu 7.0.2 有 40 个模板模型含该键 → `patch_gtceu_forge_data.py` 重打包，旧键残留 0、新键 48 处。

### 5. 主菜单复刻（星云全景）

- **真实素材来源**：GTNH 的主菜单由 `custommainmenu-1.14.1.jar` 定制，
  素材不在任何 jar 里，而由 `txloader` 从 config 目录加载：
  `config/txloader/load/mainmenu/textures/backgrounds/` 下有 **24 张高清星云**（每张 2–3 MB）。
- **复刻方式**：把 360° 等距柱状投影切成六个立方体面（`PanoCube`），六面 1024×1024，
  墙角连续、无拼缝。**产物**：`GTNH-Launch-Look.zip`（10,214,382 B）。
- **结构限制**：1.21.1 的 panorama 是立方体内壁六面，做不出 GTNH 的「左侧黑条 + 竖排按钮」布局
  （那依赖 1.7.10 的 CustomMainMenu）。

### 6. 加载画面

- **真实形态**：GTNH 用的是 `betterloadingscreen`，素材是 `config/txloader/forceload/betterloadingscreen/textures/backgrounds/`
  下 **52 张 2560×1440 成品图**，logo 与版本横幅已烤在图里。其中 `background45.png` 是那张日式木屋
  （"Home sweet Home by N7Paddy"）。
- **1.21.1 的限制**：早期画面由 `fancymodloader/earlydisplay` 绘制，`ColourScheme` 的 `background()`
  只返回 `Colour`（三个 int），**接口层面不接受纹理**。要贴全屏图必须改 `DisplayWindow` 的清屏逻辑
  与 `ElementShader`，等于重写渲染顺序。
- **已做的**：`ColourScheme` 里 RED 的 RGB 从 `(239,50,61)` 改为 `(0,0,0)`（纯黑），
  fox/squirrel 转 1×1 透明。产物 `earlydisplay-4.0.45.jar`（107,554 B）。

### 7. 成就误触发的定位

用户反馈「新世界就完成 6-7 个成就」。解析存档 advancements json：35 键中 27 条 `modernfoundry:`
全 `done=True` → 是 **Modern Foundry 在批量发放成就**，不是 GTCEu 与 1.21.1 不兼容。
已从实例摘除该 mod。

---

## 三、目录结构

```
gtverse/
├── 01-tools/          分析工具
│   ├── analyzer/      jar 扫描、类探测
│   └── bq/            任务书（questbook）解析与审计
├── 02-analysis/       全部 Python 脚本与中间产物
│   ├── patch_*.py     各 mod jar 的贴图/数据修补器
│   ├── build_*.py     id_map / 材料清单 / TiC 兼容层生成器
│   ├── resolve_*.py   多轮侧证写回（zh_cn / 1.21 标签 / TiC）
│   └── *.md           进度与分析报告
├── 03-pack/           任务书转换产物（FTB Quests → 1.21 格式）
└── 05-port/           移植侧
    ├── ticcompat/     TiC 兼容层（Java + 资源）
    ├── gtverse-dumper/ 配方导出器
    ├── gtnh-launchpack/ 主菜单全景包源文件
    └── gtnh-ui-assets/ 界面素材归档
```

---

## 四、关键产物清单

| 文件 | 大小 | 说明 |
|---|---|---|
| `gtceu-1.21.1-7.0.2-gtnh-icons.jar` | 16,315,974 B | GTCEu + GTNH 贴图 + neoforge_data 修补 |
| `GTNH-Launch-Look.zip` | 10,214,382 B | 主菜单星云全景（球面投影六面） |
| `tconstruct-compat-0.1.0.jar` | 48,839 B | TiC 兼容层（126 个部件） |
| `earlydisplay-4.0.45.jar` | 107,554 B | 加载画面黑底 + 去狐狸 |
| `gtverse-idmap-v4.zip` | 3,447,834 B | id 映射表 |
| `tic-compat-bundle.zip` | 2,025,105 B | TiC 兼容层打包 |
| `gtverse-questbook.zip` | 1,107,957 B | 任务书转换产物 |

> 大体积中间数据（`dumps_v4/`、`dumps_v5/` 共 91 MB、`id_map.json` 29 MB）未入库，
> 需要时按 `02-analysis/RECIPE_DUMP_PATH.md` 的描述重新导出。

---

## 五、复现步骤

1. 装好 GTNH 2.9.0（1.7.10），进游戏执行 `NEI 工具 → 导出数据`，产出 `dumps/`。
2. `py 02-analysis/run_all.py` 跑完整 15 步流水线（支持 `--from` 断点）。
3. `py 02-analysis/patch_gtceu_icons.py` 生成 GTNH 贴图版 GTCEu。
4. `py 02-analysis/patch_gtceu_forge_data.py` 修 `forge_data` 键。
5. 组装 1.21.1 实例（NeoForge 21.1.266），放入三个 jar，启用 `GTNH-Launch-Look.zip`。

---

## 六、已知限制与后续

- **主菜单布局**做不出来（缺 1.21 版 CustomMainMenu 类 mod）。
- **加载画面木屋图**做不了（earlydisplay 背景只接受纯色）。
- **配方转换器 0 行代码**：172 张 GT RecipeMap → GTCEu RecipeType 的映射尚未开始。
- **材料注入层未实现**：`materials_to_add.csv` 只是清单（1,659 种待补材料）。
- **TiC 126 个物品是空壳**：无配方、无功能。
