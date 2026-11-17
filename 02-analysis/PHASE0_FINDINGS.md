# GTVerse Phase 0 — 移植面实测报告

> 对象：`D:\GTNH\GT New Horizons 2.9.0\.minecraft\mods`（GTNH 2.9.0, MC 1.7.10, Forge 10.13.4）
> 目标：MC 1.21.1 + NeoForge 21.1.x + Legacy Connector
> 方法：只读 jar 元数据与字节码字符串常量，不启动游戏
> 生成：`01-tools/analyzer/{scan_jars.py, probe_classpoke.py, probe_all.py}`

---

## 一、基线

| 指标 | 数值 |
|---|---|
| jar 数量 | 242 |
| jar 总体积 | 516.9 MB |
| Java class 总数 | **110,181** |
| class 未压缩体积 | 434.3 MB |
| 配置文件 | 17,062 个 / 312.8 MB |
| 任务条目 | 3,814 |

## 二、三层结构

| 层 | 规模 | 判定 | 处置 |
|---|---|---|---|
| **补丁层** | 30 个 jar，1,375 个耦合类 | 性能/底层改写（hodgepodge、angelica、endlessids、archaicfix、bugtorch、coretweaks、lwjgl3ify 等） | **整层丢弃**，换 Embeddium / ModernFix / FerriteCore |
| **内容层** | 87 个 jar 有 MC 耦合，合计 2,267 类 | 其余上游模组（Thaumcraft、AE2、IC2、Galacticraft、Botania…） | 交给 Legacy Connector 转译 |
| **核心层** | GT5U 54 类 · gtnhlib 56 类 · CoreMod 10 类 | GTNH 自己的配平与机器体系 | **真正的移植工作面** |

## 三、关键数字：892

```
110,181 个 class 中，扣除补丁层后真正直接耦合 Minecraft 的：892 个 = 0.81%
```

这是整个项目的真实边界。不是 11 万行，是 892 个类。

其中最大几项：

| jar | 耦合类 | 说明 |
|---|---|---|
| salisarcana | 166 | TC4 行为修正模组，随 TC4 移植方案走 |
| etfuturum | 100 | 1.7.10 回填新版特性，1.21 原生已有，可直接弃用 |
| gtnhlib | 56 | GTNH 公共库，需重写 |
| **gregtech (GT5U)** | **54** | **整个 GTNH 的心脏，只有 54 个耦合点** |
| Thaumcraft4Tweaks | 47 | 随 TC4 |
| backhand | 39 | 背包/副手，可从现代库借 |
| ServerUtilities | 28 | 换现代替代 |
| gravisuiteneo | 23 | GraviSuite，随太空线 |

## 四、GTNH CoreMod 的真实结构

`GTNewHorizonsCoreMod-2.9.99.jar` 有 46.2 MB，但 coremod 层只有 **13 个类**，真正的 ASM transformer 只有 **3 个**：

```
com/dreammaster/coremod/DreamClassTransformer          ← IClassTransformer 入口
com/dreammaster/coremod/transformers/recipenukers/
    AdvancedSolarPanelTransformer                       ← 删 AdvancedSolarPanel 配方
    BibliocraftTransformer                              ← 删 Bibliocraft 配方
    GraviSuiteTransformer                               ← 删 GraviSuite 配方
```

三个全是「删配方」，不是改引擎。这在 1.21.1 上有现成的一对一替代：KubeJS `event.remove({mod: '...'})`。Legacy Connector 文档也明确支持这种改法。

**结论：不能过 Legacy Connector 的那层「黑魔法」，其实只有 3 个类，且都可以用数据包/KubeJS 复刻。**

## 五、GT5U 的耦合形态

72 个候选类中 54 个触及 MC，绝大多数是 `gregtech/mixin/interfaces/accessors/*` —— Mixin 访问器（为了读写 Minecraft 的私有字段）。这类东西是机械重映射工作，不是设计重写。

## 六、接下来要写的三个模块

| 模块 | 输入 | 输出 | 估算 |
|---|---|---|---|
| **M1 重映射器** | 892 个类 + 1.7.10→1.21.1 映射表 | 可编译的补丁包 | 依赖 mapping 工具链 |
| **M2 配方层替代** | 3 个 recipenuker 的行为 | KubeJS 脚本 | 小，几天 |
| **M3 任务书转换器** | 3,814 个 BetterQuesting json | FTB Quests snbt | 中等，可自动化 |

## 七、未覆盖 / 待验

- 未启动游戏，所有兼容性判断均为静态推断，需 Phase 0-B 实例实测复核
- Legacy Connector 的实际翻译报告（`logs/legacyconnector-report.txt`）尚未取得
- 上游模组的「部分可用」具体指哪些功能缺失，未逐项核对
