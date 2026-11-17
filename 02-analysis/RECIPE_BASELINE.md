# GTNH 2.9.0 配方基线 v1（真实运行时导出）

导出时间：2026-11-15 18:01–19:04（单次连续运行 63.6 分钟）
来源实例：`C:\Users\qqerd\Desktop\mc启动器\Prism Launcher\instances\GTNH`（MC 1.7.10 / Forge 10.13.4.1614 / 325 个 mod 加载）

---

## 一、产出文件

| 文件 | 大小 | 内容 |
|---|---|---|
| `gtverse_gt_recipes.json` | 53.8 MB | GT（GregTech 5.09.54.265）172 张 RecipeMap、187,204 条配方 |
| `gtverse_nei_recipes.json` | 50.8 MB | 非 GT mod 的 NEI 配方，92 种 handler、59,873 条去重配方 |
| `gtverse_items.json` | 5.9 MB | 57,035 条物品（真实注册名 + damage + 数量） |
| `gtverse_manifest.json` | 184 B | 运行元数据（见下） |

清单原始值：

```json
{"items": 57035, "gt_maps": 172, "gt_recipes": 187204,
 "nei_handler_slots": 93940, "nei_unique_recipes": 59873,
 "nei_recipe_hits": 400773, "elapsed_ms": 3817776}
```

**配方总量：187,204 + 59,873 = 247,077 条**，覆盖 **136 个 mod 命名空间**——与 `ns_disposition.csv` 里需要处理的 136 个命名空间数量完全一致。

---

## 二、GT 侧分布

172 张 RecipeMap 中 165 张非空、7 张为空。头部高度集中：

| RecipeMap | 条数 |
|---|---|
| `gt.recipe.fluidsolidifier` | 28,895 |
| `gt.recipe.macerator` | 24,993 |
| `gt.recipe.extruder` | 13,457 |
| `gt.recipe.hammer` | 13,016 |
| `gt.recipe.fluidextractor` | 11,212 |
| `gt.recipe.assembler` | 10,699 |
| `gt.recipe.alloysmelter` | 10,356 |
| `gt.recipe.arcfurnace` | 10,108 |
| `gt.recipe.cuttingsaw` | 9,329 |
| `gt.recipe.canner` | 4,149 |

前 9 张表占全量 59%。`fluidsolidifier + macerator + extruder` 三张就是 GT 的「万物粉碎/成型」底座——迁移时优先还原这三张，能立刻救回 6.7 万条链条。

指标中位数：耗时 250 ticks、EU/t 30。标记 `fake=true` 9,861 条（`fakecuttingsaw` 等「伪配方」，只用于 UI/配方查询展示，不是真实可执行路径），`hidden=true` 191 条。

按 modid 统计配方中出现的物品引用：

gregtech 314,080 / TGregworks 45,446 / TConstruct 36,910 / bartworks 21,939 / miscutils 20,142 / minecraft 10,529 / dreamcraft 10,351 / IC2 7,131 / chisel 3,986 / Forestry 3,144 / cropsnh 3,064 / Ztones 2,172 / Botania 2,124 / harvestcraft 1,948 / Thaumcraft 1,794 / etfuturum 1,671 …

---

## 三、物品清单

- 条目 57,035 / 独立注册名 10,758 / `(id, damage)` 组合 46,620
- ID 是**真实注册名**：`minecraft:stone`、`gregtech:gt.metaitem.01`、`TConstruct:metalPattern`、`bartworks:gt.bwMetaGeneratedingot`……不再是 `类名@hash`

**交叉验证（这是基线可信度的关键证据）**：
把 M3 任务书里引用的 7,267 个物品（`item_resolution.csv`）拿到本次导出里查——**7,237 个命中，命中率 99.59%**。缺的 30 个集中在 bartworks(9) / ic2(7) / minecraft(5)，多为 damage 落在物品清单外或本就未注册的占位物。

---

## 四、NEI 侧分布

93,940 个 handler 槽位被扫描、命中 400,773 次、去重后 59,873 条（去重率 85.1%，说明同一配方被多个物品反复命中——这正是上一版导出失败的原因：不做去重和按物品过滤会产出 441 MB 的重复垃圾）。

| handler | 条数 |
|---|---|
| `codechicken.nei.recipe.ShapedRecipeHandler` | 26,663 |
| `codechicken.nei.recipe.FurnaceRecipeHandler` | 9,482 |
| `ganymedes01.etfuturum.compat.nei.BlastFurnaceRecipeHandler` | 9,146 |
| `codechicken.nei.recipe.ShapelessRecipeHandler` | 4,749 |
| `forestry.factory.recipes.nei.NEIHandlerBottler` | 1,475 |
| `tconstruct.plugins.nei.RecipeHandlerCastingTable` | 819 |
| `com.gtnewhorizons.aspectrecipeindex.nei.AlchemyRecipeHandler` | 772 |
| `…ShapedArcaneRecipeHandler`（神秘工作台） | 580 |
| `net.bdew.neiaddons.forestry.bees.BeeBreedingHandler` | 543 |
| `…InfusionRecipeHandler`（注魔） | 542 |

已主动跳过 `gregtech.* / bartworks.* / ggfab.* / goodgenerator.* / gtnhlanth.* / kubatech.* / cropsnh.* / tectech.*` 的 handler——这些内容由上表的 GT RecipeMap 覆盖，不重复导出。

---

## 五、数据 schema

GT 配方（`maps[].recipes[]`）：

```json
{"in":[{"id":"gregtech:gt.blockframes","d":599,"n":1}],
 "out":[{"id":"gregtech:gt.metaitem.01","d":2599,"n":2}],
 "fin":[{"f":"water","a":100}], "fout":[{"f":"potion.wine","a":12}],
 "ic":[], "oc":[], "dur":200, "eut":120, "sv":0,
 "hidden":false, "fake":false, "enabled":true,
 "spec":{"id":"bartworks:BioLabParts","d":0,"n":1,"nbt":"{Name:\"Saccharomyces cerevisiae var bayanus\"}"}}
```

- `in/out` 为物品槽，`fin/fout` 为流体槽（`f` = Fluid 注册名，`a` = mB）
- `ic/oc` 为电路/线圈等特殊槽，`spec` 为特殊前置（如菌种、编程电路）
- `nbt` 只记录**摘要字符串**（原样 NBT 片段），不展开结构

NEI 配方（`handlers[]`）：

```json
{"h":"...FurnaceRecipeHandler","hn":"烧制","t":"烧制",
 "in":[{"x":51,"y":6,"ch":10000,"alts":[{"id":"minecraft:cobblestone","d":0,"n":1}]}],
 "other":[...], "out":{"x":111,"y":24,"ch":10000,"alts":[...]}}
```

`x/y` 是 NEI 面板内的原像素坐标 → 迁移到 EMI/JEI 时可还原排版；`alts` 是「任选其一」的替代集合（矿辞替换的落点）；`ch` 是机率（10000 = 100%）。

---

## 六、口径与已知边界

1. **只覆盖「配方」**，不含：多方块结构（GT 机器结构由 `structurelib` 数据描述）、矿辞本身（只在 NEI 的 `alts` 里出现）、研究/进度树（另在 M3 任务书里）。
2. **NEI 的覆盖边界**：只能拿到「按某物品查询时该 handler 返回的配方」。完全自定义 GUI、没有 NEI 集成的配方（少数）拿不到；GT 系走的是 RecipeMap 全表遍历，不受此限。
3. `fake=true` 的 9,861 条是展示用伪配方，迁移时**不得**当作可执行配方注册，否则会造出「能查不能用」的幽灵链路。
4. 流体 `potion.wine` 这类是 1.7.10 的 `Fluid.getName()`，1.21.1 需映射到对应 fluid tag。
5. damage 体系在 1.21.1 已不存在，`(id, damage)` 必须整体重映射为 `(id, data components)`——这是下一步 `id_map.json` 的核心任务。

---

## 七、下一步（按依赖顺序）

1. **`id_map.json`**：10,758 个注册名 → 1.21.1 目标 id；46,620 个 `(id,damage)` → 目标 stack（含 components）。以本次 `gtverse_items.json` 为源，以 `ns_disposition.csv` 的 136 条去向为规则。
2. **NEI → EMI/JEI 转换器**：`handlers[]` 扁平结构 → EMI recipe JSON，`alts` 转 tag 引用，`x/y` 转槽位。
3. **GT RecipeMap → GTCEu Modern 表映射**：172 张表名对 1.21.1 GTCEu 7.0.0 的 `RecipeMap`；`fake` 表丢弃、`hidden` 表保留但不进配方查询。三张底座表（fluidsolidifier/macerator/extruder）先做。
4. **验证**：转换后重跑 M3 的 `check_ids.py`，目标是把 7,237 的命中率推到 100%，并把 30 个缺口逐个定性（是缺物品还是缺映射规则）。

---

## 八、复现方式

导出器源码 `D:\GTNH\gtverse\05-port\gtverse-dumper\src\gtverse\dumper\`（`GtverseDumper` / `DumpJob` / `Refl` 三个类 + 9 个桩类），编译产物 `build\gtversedumper-1.0.jar`（17,554 B），装入 `.minecraft\mods\` 后启动游戏即自动导出到 `.minecraft\dumps\`。

关键点：**必须进一次世界**（NEI 的物品清单只有进世界后才建立），导出线程会等它最多 12 分钟；运行期间不要退出游戏，收尾以 `gtverse_manifest.json` 出现为准。
