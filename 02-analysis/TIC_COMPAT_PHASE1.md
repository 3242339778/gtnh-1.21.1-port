# tconstruct 兼容层 · 第一阶段落地报告

> Legacy Connector 的 TiC 段。2026-11-17。
> 结论：**方案 C 已落地为可编译、可打包的 mod，缺口 19,549 次引用全部被 126 个注册项覆盖。**

---

## 一、为什么是这一个数字

1.7.10 侧 `tconstruct` 命名空间共 10,241 条 / 58,439 次引用，其中：

| 状态 | 条目 | 引用 | 处理 |
|---|---|---|---|
| 已落到 modernfoundry | 4,184 | 38,890 | 66.5%，含改名（`tool_rod→tool_handle` 等 7 组） |
| 仍是缺口 | 6,057 | 19,549 | 本阶段用兼容层覆盖 |

缺口归组后是 **126 个部件**：

- **material_driven 14 个**：`chunk`、`chisel_head`、`shovel_head`、`crossbar`、`full_guard`、`sign_head`、`excavator_head`、`blade`、`large_guard`、`medium_guard`、`pan_head`、`lumber_head`、`crossbow_limb`、`crossbow_body`。每个都覆盖 **153 种材料**。
- **singleton 112 个**：44 个有实际引用（`tgregworks.shardcast` 4,287、`buckets` 64、`materials` 40、`glass_pane` 33…），68 个无引用但保留以便对齐完整注册表。

关键判断：缺口不是新机制，是「材料 × 部件」矩阵里 modernfoundry 没注册的那些组合。

---

## 二、核心设计：id 不变，材料进组件

1.7.10 用 NBT 区分材料：`tconstruct:chunk` + `{material:"Aluminium"}`。
1.21 没有 NBT，主流做法是按材料拆 id（`tconstruct:aluminium_chunk`），代价是 **2,142 条引用全改写**。

本阶段改为：**item id 保持 `tconstruct:<part>` 不变，材料写进 data component**。

```java
DataComponentType.<String>builder()
        .persistent(Codec.STRING)
        .networkSynchronized(ByteBufCodecs.STRING_UTF8)
        .build();
```

结果：
- 只需注册 **126 个 id**（不是 2,254 个）
- 配方侧 `tconstruct:chunk` 原样命中，**零改引用**
- 语义与 1.7.10 等价（同一 id + 材料维度），只是承载方式从 NBT 换成 component

API 不是凭记忆写的：从 `neoforge-21.1.265-sources.jar` 里读出 `DataComponentType.Builder` 的真实签名（`persistent(Codec<T>)` / `networkSynchronized(StreamCodec)` / `cacheEncoding()` / `build()`），并对照 `DataComponents.LORE` 的实际写法确认。

---

## 三、命名空间归属

查了那个 1.21.1 唯一 TiC 系移植的元数据：

| 项 | 值 |
|---|---|
| modId | `modernfoundry` |
| displayName | Modern Foundry |
| version | `1.21.1-1.6.1-NeoForge` |
| `assets/modernfoundry` 条目 | 29,868 |
| `assets/tconstruct` 条目 | **0** |

所以 `tconstruct` 这个 modid 完全空置。兼容层直接占用：

- **modId = `tconstruct`**
- **注册命名空间 = `tconstruct`**
- 与 `modernfoundry` 并存，id 不重叠

这样还有个额外好处：任何声明依赖 `tconstruct` 的 mod 能被正确识别。

---

## 四、实际产物

```
D:\GTNH\gtverse\05-port\ticcompat\
├── src\main\java\gtverse\ticcompat\
│   ├── TicCompat.java               @Mod("tconstruct")，注册入口
│   ├── TicCompatItems.java          126 个 registerSimpleItem
│   └── TicMaterialComponents.java   material data component
├── src\main\resources\
│   ├── META-INF\neoforge.mods.toml  modId=tconstruct, ver=0.1.0
│   └── assets\tconstruct\
│       ├── models\item\*.json       126 个
│       ├── lang\zh_cn.json          126 条（用 1.7.10 的中文名）
│       ├── lang\en_us.json          126 条
│       └── textures\item\compat_placeholder.png   16×16 手写 PNG
└── build\libs\
    └── tconstruct-compat-0.1.0.jar  48,825 B / 144 条目
```

数据产物（`02-analysis`）：`tic_gap_manifest.json/csv`、`tic_compat_spec.json/csv`，以及 `id_map.json` 补入的 **2,142 条** `components`（`rule=tic:component`、`verified=tic_component`）。

---

## 五、验证（都跑过了）

| 验证项 | 结果 |
|---|---|
| `javac --release 21` 对 NeoForge 21.1.265 实际编译 | **exit=0**，3 个 class |
| jar zip 完整性 `testzip()` | OK，144 条目 |
| `neoforge.mods.toml` TOML 解析 | OK，modId=`tconstruct` |
| 126 个模型引用的贴图是否存在 | 缺失 **0** |
| 中英 lang 键数 | 各 126，与模型一一对应 |
| jar 内条目含空格（非法 id 残留） | **0** |
| 全流水线 15 步回归 | 全部返回 0 |

顺带修掉一个真 bug：`clay _cast` 这种名字带空格会直接生成非法常量名与非法注册名。做全局校验后发现 **9 个非法 target / 86 引用**（`natura:rare _tree`、`wood's_glass` 等），`fix_bad_ids.py` 修了 **47 条 / 359 引用**，复检归零。

---

## 六、流水线

`run_all.py` 从 12 步扩到 **15 步**，新增：

- `13 fix_bad_ids.py` — id 合法性校验与修复
- `14 analyze_tic_gap.py` — TiC 缺口归组
- `15 build_tic_compat.py` — 生成兼容层（Java + 资源 + component 写回）

顺序不可颠倒：`build_id_map.py` 会整体覆盖 `id_map.json`，所以 `fix_bad_ids` / `build_tic_compat` 必须在其后。

---

## 七、还没做的

1. **没上机实测**。没有 1.21.1 的 NeoForge 运行实例（Prism 里只有 GTNH 1.7.10）。需新建一个实例，装 NeoForge 21.1.x + Modern Foundry + 本 jar，验证 126 个物品能注册、能显示。
2. **转发逻辑未实现**。当前只注册物品，没有把行为转发到 modernfoundry 的实现（冶炼、工具组装）。第一阶段的目的是「先让 id 存在、配方能解析」，机制复用是第二阶段。
3. **gradle 工程文件未建**。目前用 javac 直接编译并用 `jar` 打包，能产出可加载 jar；若要 `runClient` 调试，需补 `build.gradle` / `settings.gradle`（可直接抄 MDK 的，把 mod_id 换成 tconstruct）。
4. **`materials_to_add.csv` 的 1,659 种材料仍未注入 GTCEu 分叉**，档 1 的 Material 注册代码还是 0 行。

---

## 八、下一步建议

按收益排序：

1. **建 1.21.1 实测实例**，先把这 126 个物品跑起来，确认注册与显示无问题 —— 这是唯一还没验证的环节。
2. **档 1 补 Material**（2,003 种 / 108,023 次 = 61.3%），这是全局最大收益点，且是「加数据不写代码」。
3. 档 2 补 19 种 TagPrefix（4,681 次）。
4. 二期做 TiC 的转发逻辑，把冶炼/工具行为接到 modernfoundry。
5. 档 3 的 13,090 项（机器/蓝图/特殊装备）按模块分批。
