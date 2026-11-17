# ID_MAP 规范 v4（GTNH 1.7.10 → 1.21.1）

> 本版新增：GTCEu 中文 lang 反查、矿辞→1.21 标签/工具映射、TiC 命名空间落地评估、
> 「补材料」施工分层。所有目标都用包内真实证据核对过，不是按命名惯例推算。

## 1. 数据来源（全部实测）

| 来源 | 规模 | 用途 |
|---|---|---|
| `dumps/gtverse_items.json` | 57,035 条（10.0 MB） | 1.7.10 全部物品：`id/d/n/un/dn/od` |
| `dumps/gtverse_gt_recipes.json` | 187,204 条（92.0 MB） | GT 配方，每条引用带 `un/dn/od` |
| `dumps/gtverse_nei_recipes.json` | 59,873 条（53.3 MB） | NEI 配方 |
| `dumps/gtverse_tic_materials.json` | 184 条 | TiC 材料 id→名 |
| `_gtceu/gtceu.jar` | 16.6 MB | GTCEu 1.21.1 真实注册证据 |
| `_tic3/tic3.jar` | 27.9 MB | TiC 1.21.1 移植（modernfoundry）真实注册证据 |

## 2. 目标索引（运行时依赖，必须先建）

- `gtceu_1211_index.json`：items **687** / blocks **1,597** / materials **653** / tagprefixes **104** / models 2,026
- `gtceu_1211_zh_index.json`：中文 items **683** / blocks **1,580** / materials **653** / tagprefixes **103**
- `modernfoundry_1211_index.json`：items **415** / blocks **437** / patterns **42** / materials **569**

## 3. 关键结论（实测证据）

### 3.1 1.21 没有矿辞，只有标签
GTCEu 包内共 179 个 tag 文件，`values` 数组结构。自有标签 41 个（`batteries`、`circuits`、`lv..max`、
`pickaxe_or_wrench`、`wire_cutter` 等），通用标签在 `data/c/tags/item/*`（dyes、lenses、tools/igniter…）。
**没有** `c:tools/hammer`、`c:ingots/*`、`c:dusts/*` 这类材料通用标签，因此「矿辞→标签」不能凭空编 id，
只能落到 GTCEu 实有标签（本表只用了 12 个可确证的）。

### 3.2 GTCEu 工具是「材料 + 工具类型」动态注册
lang 键 `item.gtceu.tool.<type>` 用 `%s` 模板：`hammer→%s锻造锤`、`file→%s锉`、`saw→%s锯`、
`wrench→%s扳手`、`screwdriver→%s螺丝刀`、`mallet→%s软锤`、`mortar→%s研钵`、`wire_cutter→%s剪线钳`、
`mining_hammer→%s采矿锤`、`rolling_pin→%s熘面杖`、`plunger→%s摻子`、`butchery_knife→%s屠刀`、`crowbar→%s撵棍`。
工具类别键 `gtceu.tool.class.*` 共 40 个。1.7.10 侧对应 `craftingTool*` / `toolHead*` 矿辞。

### 3.3 1.21.1 不存在 `tconstruct` 命名空间（重要）
- Modrinth 上 TiC 官方最高只到 **1.20.1**；`tconstruct` 项目 game_versions 无 1.21.1。
- 唯一 1.21.1 NeoForge 的 TiC 系实现是 **TinkersConstruct-Unofficial-Port-1.21.1-1.6.1**（27.9 MB），
  但它内嵌 **Modern Foundry**，命名空间是 **`modernfoundry`**，jar 内 `assets/tconstruct` 条目数 = **0**。
- 因此 1.7.10 的 `tconstruct:` 引用（10,241 条 / **58,439** 次 = 全表 11.5%）在 1.21.1 **没有同名落点**。

TiC 2→3 部件改名（有据）：`tool_rod→tool_handle`、`tough_rod→tough_handle`、`binding→tool_binding`、
`tough_bind→tough_binding`、`axe_head→small_axe_head`、`metal_pattern→pattern`、`cast→blank_cast`。
无对应：`chunk`（TGregworks 碎片，modernfoundry 只有 steel/cobalt/knightmetal 三种按材料注册）、
`chisel_head`、`shovel_head`、`excavator_head`、`sign_head`、`crossbar`、`full_guard`、`blade`、
`tgregworks.shardcast`。

落地率：**4,184 条 / 38,890 次 = 66.5%** 可落到 modernfoundry；其余 6,057 条 / 19,549 次需自研或改设计。

### 3.4 中文 lang 反查（最有效的一步）
GTCEu 自带 `zh_cn.json`（5,571 键 / 786 个 item 键）。用 1.7.10 侧的 `dn` 中文显示名反查：
精确 item / block 名 + `tagprefix 中文模板 × 材料中文名` 组合。净收益 **517 条 / 4,960 次**。

剩余未命中的头部**不是名字没对上，而是 GTCEu 真没有**：
`铝研磨球`(475)、`皂石研磨球`(475)（GTCEu 无 millingball 形态）、`切削器刀刃（平切）`(253)、
`锤/锯/锉刀`(工具本体，走 3.2 的模板)、`小型/大/巨型涡轮`(GTCEu 只有 `%s涡轮扇叶`)、
`富铈混合物粉`(材料不存在)。

### 3.5 「爆改 GTCEu」施工分层（新路线）
用户拍板：走 **fork GTCEu 补注册**，而不是逐个造替代物品。GTCEu 用 `Material × TagPrefix` 矩阵注册，
**补一个 Material 定义，全套形态自动生成**。`gtverse:` 的 176,211 次引用分布：

| 档 | 做法 | 材料/形态数 | 引用 | 占比 |
|---|---|---|---|---|
| 1 | 补 Material 定义（形态 GTCEu 已有） | **2,003 种材料** | **108,023** | **61.3%** |
| 2 | 补 TagPrefix（模具等新形态） | 19 种形状 | 4,681 | 2.7% |
| 3 | 逐项自研（机器/蓝图/特殊装备） | 13,090 项 | 63,507 | 36.0% |

缺的形态只有 **35 种**（`dust` 26,464 / `bolt` 15,948 / `ingot` 12,798 / `plate` 7,970 / `block` 4,835 /
`rod` 4,465 / `small_dust` 3,704 / `nugget` 3,500 / `gem` 3,165 …），缺的材料 **2,003 种**。
→ **补材料的性价比极高：61.3% 的缺口靠加数据定义解决，不用写物品逻辑。**

无 GTCEu 对应材料（确认）：`space`、`adamantium`、`enderium`、`desh`、`pig_iron`、`quantium`、
`draconium`、`bedrockium`、`uranium`、`cast_iron`、`mithril`、`amber`、`void`、`alumina`（GTCEu 只有
`aluminium`，没有 oxide 形态）、`zircaloy-4`、`incoloy-903`、`mar-m200steel`、`artherium`。

## 4. 最终统计（70,685 条 / 508,927 引用）

置信度：high **35.9%**、medium **52.7%**、low **9.6%**、manual **1.7%**
核对状态：`exact` 11.9% + `material+tagprefix` 22.7% + `tic_registry` 7.6% +
`zh_lang` 1.0% + `tag` 0.3% + `tool_type` 0.1% = **43.6% 有包内证据**
命名空间：`gtceu` 39.8% / `gtverse` 34.6% / 无目标 8.3% / `modernfoundry` 7.6% / `tconstruct` 3.8% /
`minecraft` 1.0% / `chisel` 0.8% / `forestry` 0.6% / `ic2` 0.6%

## 5. 复现（一条命令）

```
py run_all.py            # 12 步全跑
py run_all.py --from resolve_by_zh
```

顺序不可颠倒，原因：`build_id_map.py` 会整体覆盖 `id_map.json`，之后的 `resolve_by_*.py`
是**后处理写回**，重跑主生成必须把后处理再跑一遍。

## 6. 边界与未决

- `needs_damage_table` 8,553 条 / 33,244 次：内部名（`gt.metaitem.02` 等）的 damage 语义无表可查，
  已做三分类（`residual_triage.csv`）：A 可救 8,861 次、B 必建 23,778 次、C 无线索 605 次。
- `tconstruct:` 6,057 条未落地 → 需在「自研 tconstruct 命名空间」与「改用 modernfoundry」之间拍板。
- 172 张 GT RecipeMap → GTCEu 1.21.1 RecipeType 名映射仍为 0。
- 配方转换器 0 行代码；1.21.1 实机实例 0 个。
