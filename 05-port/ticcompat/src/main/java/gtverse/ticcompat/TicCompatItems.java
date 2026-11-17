package gtverse.ticcompat;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * tconstruct 兼容层物品注册（Legacy Connector / TiC 段）。
 * 由 id_map 缺口清单自动生成：共 126 项（材料驱动 14 / 单例 112）。
 * 材料驱动项的 id 不带材料，材料写在 TicMaterialComponents.MATERIAL 组件里。
 */
public final class TicCompatItems {

    /** 引用权威常量，避免与 TicMaterialComponents.MODID 分叉。 */
    public static final String MODID = TicMaterialComponents.MODID;
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredItem<Item> CHUNK = ITEMS.registerSimpleItem("chunk");
    public static final DeferredItem<Item> TGREGWORKS_SHARDCAST = ITEMS.registerSimpleItem("tgregworks.shardcast");
    public static final DeferredItem<Item> CHISEL_HEAD = ITEMS.registerSimpleItem("chisel_head");
    public static final DeferredItem<Item> SHOVEL_HEAD = ITEMS.registerSimpleItem("shovel_head");
    public static final DeferredItem<Item> CROSSBAR = ITEMS.registerSimpleItem("crossbar");
    public static final DeferredItem<Item> FULL_GUARD = ITEMS.registerSimpleItem("full_guard");
    public static final DeferredItem<Item> SIGN_HEAD = ITEMS.registerSimpleItem("sign_head");
    public static final DeferredItem<Item> EXCAVATOR_HEAD = ITEMS.registerSimpleItem("excavator_head");
    public static final DeferredItem<Item> BLADE = ITEMS.registerSimpleItem("blade");
    public static final DeferredItem<Item> LARGE_GUARD = ITEMS.registerSimpleItem("large_guard");
    public static final DeferredItem<Item> MEDIUM_GUARD = ITEMS.registerSimpleItem("medium_guard");
    public static final DeferredItem<Item> PAN_HEAD = ITEMS.registerSimpleItem("pan_head");
    public static final DeferredItem<Item> LUMBER_HEAD = ITEMS.registerSimpleItem("lumber_head");
    public static final DeferredItem<Item> CROSSBOW_LIMB = ITEMS.registerSimpleItem("crossbow_limb");
    public static final DeferredItem<Item> CROSSBOW_BODY = ITEMS.registerSimpleItem("crossbow_body");
    public static final DeferredItem<Item> BUCKETS = ITEMS.registerSimpleItem("buckets");
    public static final DeferredItem<Item> MATERIALS = ITEMS.registerSimpleItem("materials");
    public static final DeferredItem<Item> GLASS_PANE = ITEMS.registerSimpleItem("glass_pane");
    public static final DeferredItem<Item> DECORATION_MULTIBRICK = ITEMS.registerSimpleItem("decoration.multibrick");
    public static final DeferredItem<Item> HEAVY_PLATE = ITEMS.registerSimpleItem("heavy_plate");
    public static final DeferredItem<Item> CRAFTED_SOIL = ITEMS.registerSimpleItem("crafted_soil");
    public static final DeferredItem<Item> STRANGE_FOOD = ITEMS.registerSimpleItem("strange_food");
    public static final DeferredItem<Item> ARROWHEAD = ITEMS.registerSimpleItem("arrowhead");
    public static final DeferredItem<Item> SLIME_GEL = ITEMS.registerSimpleItem("slime.gel");
    public static final DeferredItem<Item> SPEED_BLOCK = ITEMS.registerSimpleItem("speed_block");
    public static final DeferredItem<Item> PICKAXE_HEAD = ITEMS.registerSimpleItem("pickaxe_head");
    public static final DeferredItem<Item> HATCHET_HEAD = ITEMS.registerSimpleItem("hatchet_head");
    public static final DeferredItem<Item> SWORD_BLADE = ITEMS.registerSimpleItem("sword_blade");
    public static final DeferredItem<Item> WIDE_GUARD = ITEMS.registerSimpleItem("wide_guard");
    public static final DeferredItem<Item> HAND_GUARD = ITEMS.registerSimpleItem("hand_guard");
    public static final DeferredItem<Item> KNIFE_BLADE = ITEMS.registerSimpleItem("knife_blade");
    public static final DeferredItem<Item> FRYPAN_HEAD = ITEMS.registerSimpleItem("frypan_head");
    public static final DeferredItem<Item> SCYTHE_BLADE = ITEMS.registerSimpleItem("scythe_blade");
    public static final DeferredItem<Item> LARGE_SWORD_BLADE = ITEMS.registerSimpleItem("large_sword_blade");
    public static final DeferredItem<Item> JERKY = ITEMS.registerSimpleItem("jerky");
    public static final DeferredItem<Item> SHURIKEN_PART = ITEMS.registerSimpleItem("shuriken_part");
    public static final DeferredItem<Item> BOW_LIMB_PART = ITEMS.registerSimpleItem("bow_limb_part");
    public static final DeferredItem<Item> CROSSBOW_LIMB_PART = ITEMS.registerSimpleItem("crossbow_limb_part");
    public static final DeferredItem<Item> CROSSBOW_BODY_PART = ITEMS.registerSimpleItem("crossbow_body_part");
    public static final DeferredItem<Item> SMELTERY = ITEMS.registerSimpleItem("smeltery");
    public static final DeferredItem<Item> GLASS_PANE_CLEAR_STAINED = ITEMS.registerSimpleItem("glass_pane_clear_stained");
    public static final DeferredItem<Item> DECORATION_MULTIBRICKFANCY = ITEMS.registerSimpleItem("decoration.multibrickfancy");
    public static final DeferredItem<Item> SMELTERY_NETHER = ITEMS.registerSimpleItem("smeltery_nether");
    public static final DeferredItem<Item> DECORATION_MULTIBRICKMETAL = ITEMS.registerSimpleItem("decoration.multibrickmetal");
    public static final DeferredItem<Item> CRAFTING_SLAB = ITEMS.registerSimpleItem("crafting_slab");
    public static final DeferredItem<Item> BLANK_PATTERN = ITEMS.registerSimpleItem("blank_pattern");
    public static final DeferredItem<Item> EXPLOSIVE_SLIME = ITEMS.registerSimpleItem("explosive.slime");
    public static final DeferredItem<Item> SLIME_LEAVES = ITEMS.registerSimpleItem("slime.leaves");
    public static final DeferredItem<Item> SLIME_SAPLING = ITEMS.registerSimpleItem("slime.sapling");
    public static final DeferredItem<Item> TOOL_SHARD = ITEMS.registerSimpleItem("tool_shard");
    public static final DeferredItem<Item> DIAMOND_APPLE = ITEMS.registerSimpleItem("diamond_apple");
    public static final DeferredItem<Item> SLIME_GRASS = ITEMS.registerSimpleItem("slime.grass");
    public static final DeferredItem<Item> SLIME_GRASS_TALL = ITEMS.registerSimpleItem("slime.grass.tall");
    public static final DeferredItem<Item> DECORATION_STONELADDER = ITEMS.registerSimpleItem("decoration.stoneladder");
    public static final DeferredItem<Item> GOLD_HEAD = ITEMS.registerSimpleItem("gold_head");
    public static final DeferredItem<Item> ORE_BERRIES = ITEMS.registerSimpleItem("ore_berries");
    public static final DeferredItem<Item> WOOD_PATTERN = ITEMS.registerSimpleItem("wood_pattern");
    public static final DeferredItem<Item> HEART_CANISTER = ITEMS.registerSimpleItem("heart_canister");
    public static final DeferredItem<Item> LIQUID_SLIME = ITEMS.registerSimpleItem("liquid.slime");
    public static final DeferredItem<Item> MEAT_BLOCK = ITEMS.registerSimpleItem("meat_block");
    public static final DeferredItem<Item> WOOL_SLAB1 = ITEMS.registerSimpleItem("wool_slab1");
    public static final DeferredItem<Item> WOOL_SLAB2 = ITEMS.registerSimpleItem("wool_slab2");
    public static final DeferredItem<Item> TRAP_PUNJI = ITEMS.registerSimpleItem("trap.punji");
    public static final DeferredItem<Item> TRAP_BARRICADE_OAK = ITEMS.registerSimpleItem("trap.barricade.oak");
    public static final DeferredItem<Item> TRAP_BARRICADE_SPRUCE = ITEMS.registerSimpleItem("trap.barricade.spruce");
    public static final DeferredItem<Item> TRAP_BARRICADE_BIRCH = ITEMS.registerSimpleItem("trap.barricade.birch");
    public static final DeferredItem<Item> TRAP_BARRICADE_JUNGLE = ITEMS.registerSimpleItem("trap.barricade.jungle");
    public static final DeferredItem<Item> SLIME_CHANNEL = ITEMS.registerSimpleItem("slime.channel");
    public static final DeferredItem<Item> BLOOD_CHANNEL = ITEMS.registerSimpleItem("blood.channel");
    public static final DeferredItem<Item> SLIME_PAD = ITEMS.registerSimpleItem("slime.pad");
    public static final DeferredItem<Item> DECORATION_STONETORCH = ITEMS.registerSimpleItem("decoration.stonetorch");
    public static final DeferredItem<Item> ORE_BERRIES_ONE = ITEMS.registerSimpleItem("ore.berries.one");
    public static final DeferredItem<Item> ORE_BERRIES_TWO = ITEMS.registerSimpleItem("ore.berries.two");
    public static final DeferredItem<Item> TOOL_STATION_BLOCK = ITEMS.registerSimpleItem("tool_station_block");
    public static final DeferredItem<Item> TOOL_FORGE_BLOCK = ITEMS.registerSimpleItem("tool_forge_block");
    public static final DeferredItem<Item> FURNACE_SLAB = ITEMS.registerSimpleItem("furnace_slab");
    public static final DeferredItem<Item> HELD_ITEM_BLOCK = ITEMS.registerSimpleItem("held_item_block");
    public static final DeferredItem<Item> BATTLE_SIGN_BLOCK = ITEMS.registerSimpleItem("battle_sign_block");
    public static final DeferredItem<Item> MOLTEN_STONE = ITEMS.registerSimpleItem("molten.stone");
    public static final DeferredItem<Item> MOLTEN_EMERALD = ITEMS.registerSimpleItem("molten.emerald");
    public static final DeferredItem<Item> MOLTEN_QUARTZ = ITEMS.registerSimpleItem("molten.quartz");
    public static final DeferredItem<Item> FLUID_ENDER = ITEMS.registerSimpleItem("fluid.ender");
    public static final DeferredItem<Item> LIQUID_BLOOD = ITEMS.registerSimpleItem("liquid.blood");
    public static final DeferredItem<Item> LIQUID_GLUE = ITEMS.registerSimpleItem("liquid.glue");
    public static final DeferredItem<Item> SEARED_SLAB = ITEMS.registerSimpleItem("seared_slab");
    public static final DeferredItem<Item> SPEED_SLAB = ITEMS.registerSimpleItem("speed_slab");
    public static final DeferredItem<Item> LAVA_TANK = ITEMS.registerSimpleItem("lava_tank");
    public static final DeferredItem<Item> LAVA_TANK_NETHER = ITEMS.registerSimpleItem("lava_tank_nether");
    public static final DeferredItem<Item> SEARED_BLOCK = ITEMS.registerSimpleItem("seared_block");
    public static final DeferredItem<Item> SEARED_BLOCK_NETHER = ITEMS.registerSimpleItem("seared_block_nether");
    public static final DeferredItem<Item> CASTING_CHANNEL = ITEMS.registerSimpleItem("casting_channel");
    public static final DeferredItem<Item> TITLE_ICON = ITEMS.registerSimpleItem("title_icon");
    public static final DeferredItem<Item> MANUAL_BOOK = ITEMS.registerSimpleItem("manual_book");
    public static final DeferredItem<Item> SHOVEL = ITEMS.registerSimpleItem("shovel");
    public static final DeferredItem<Item> HATCHET = ITEMS.registerSimpleItem("hatchet");
    public static final DeferredItem<Item> BROADSWORD = ITEMS.registerSimpleItem("broadsword");
    public static final DeferredItem<Item> LONGSWORD = ITEMS.registerSimpleItem("longsword");
    public static final DeferredItem<Item> RAPIER = ITEMS.registerSimpleItem("rapier");
    public static final DeferredItem<Item> CUTLASS = ITEMS.registerSimpleItem("cutlass");
    public static final DeferredItem<Item> FRYPAN = ITEMS.registerSimpleItem("frypan");
    public static final DeferredItem<Item> CHISEL = ITEMS.registerSimpleItem("chisel");
    public static final DeferredItem<Item> LUMBERAXE = ITEMS.registerSimpleItem("lumberaxe");
    public static final DeferredItem<Item> HAMMER = ITEMS.registerSimpleItem("hammer");
    public static final DeferredItem<Item> BATTLEAXE = ITEMS.registerSimpleItem("battleaxe");
    public static final DeferredItem<Item> POTION_LAUNCHER = ITEMS.registerSimpleItem("potion_launcher");
    public static final DeferredItem<Item> CREATIVE_MODIFIER = ITEMS.registerSimpleItem("creative_modifier");
    public static final DeferredItem<Item> CLAY_PATTERN = ITEMS.registerSimpleItem("clay_pattern");
    public static final DeferredItem<Item> KNAPSACK = ITEMS.registerSimpleItem("knapsack");
    public static final DeferredItem<Item> HELMET_WOOD = ITEMS.registerSimpleItem("helmet_wood");
    public static final DeferredItem<Item> CHESTPLATE_WOOD = ITEMS.registerSimpleItem("chestplate_wood");
    public static final DeferredItem<Item> LEGGINGS_WOOD = ITEMS.registerSimpleItem("leggings_wood");
    public static final DeferredItem<Item> BOOTS_WOOD = ITEMS.registerSimpleItem("boots_wood");
    public static final DeferredItem<Item> TRAVEL_GOGGLES = ITEMS.registerSimpleItem("travel_goggles");
    public static final DeferredItem<Item> TRAVEL_VEST = ITEMS.registerSimpleItem("travel_vest");
    public static final DeferredItem<Item> TRAVEL_WINGS = ITEMS.registerSimpleItem("travel_wings");
    public static final DeferredItem<Item> TRAVEL_BOOTS = ITEMS.registerSimpleItem("travel_boots");
    public static final DeferredItem<Item> TRAVEL_GLOVE = ITEMS.registerSimpleItem("travel_glove");
    public static final DeferredItem<Item> TRAVEL_BELT = ITEMS.registerSimpleItem("travel_belt");
    public static final DeferredItem<Item> THROWING_KNIFE = ITEMS.registerSimpleItem("throwing_knife");
    public static final DeferredItem<Item> SHORT_BOW = ITEMS.registerSimpleItem("short_bow");
    public static final DeferredItem<Item> LONG_BOW = ITEMS.registerSimpleItem("long_bow");
    public static final DeferredItem<Item> ARROW_AMMO = ITEMS.registerSimpleItem("arrow_ammo");
    public static final DeferredItem<Item> BOLT_AMMO = ITEMS.registerSimpleItem("bolt_ammo");
    public static final DeferredItem<Item> CLAY_CAST = ITEMS.registerSimpleItem("clay_cast");
    public static final DeferredItem<Item> BONEANA = ITEMS.registerSimpleItem("boneana");
    public static final DeferredItem<Item> SLIMESLING = ITEMS.registerSimpleItem("slimesling");

    private TicCompatItems() {}
}
