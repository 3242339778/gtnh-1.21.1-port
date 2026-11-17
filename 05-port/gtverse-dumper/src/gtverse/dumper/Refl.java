package gtverse.dumper;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Naming-agnostic access to Minecraft internals.
 *
 * Runtime uses MCP class names but srg member names (func_/field_), and reflection strings are
 * not remapped. So every member is resolved by trying the MCP name, then the known srg name,
 * then a unique match by signature.
 */
public final class Refl {

    public static Class<?> cItemStack;
    public static Class<?> cItem;
    public static Class<?> cNbt;
    public static Class<?> cFluidStack;
    public static Class<?> cFluid;
    public static Class<?> cRegistry;
    public static Class<?> cMc;

    public static Method mGetItem;
    public static Method mGetDamage;
    public static Method mGetTag;
    public static Method mGetNameForObject;
    public static Method mGetFluid;
    public static Method mFluidName;
    public static Method mMcInstance;

    public static Field fStackSize;
    public static Field fItemRegistry;
    public static Field fFluidAmount;
    public static Field fDataDir;

    private static boolean inited = false;

    public static synchronized void init() {
        if (inited) {
            return;
        }
        inited = true;

        cItemStack = cls("net.minecraft.item.ItemStack");
        cItem = cls("net.minecraft.item.Item");
        cNbt = cls("net.minecraft.nbt.NBTTagCompound");
        cFluidStack = cls("net.minecraftforge.fluids.FluidStack");
        cFluid = cls("net.minecraftforge.fluids.Fluid");
        cRegistry = cls("net.minecraft.util.RegistryNamespaced");
        cMc = cls("net.minecraft.client.Minecraft");

        mGetItem = meth(cItemStack, new String[]{"getItem", "func_77973_b"}, cItem, 0);
        mGetDamage = meth(cItemStack, new String[]{"getItemDamage", "func_77960_j"}, int.class, 0);
        mGetTag = meth(cItemStack, new String[]{"getTagCompound", "func_77978_p"}, cNbt, 0);
        fStackSize = fld(cItemStack, new String[]{"stackSize", "field_77994_a"}, int.class);

        fItemRegistry = fld(cItem, new String[]{"itemRegistry", "field_150901_e"}, cRegistry);
        mGetNameForObject = meth(cRegistry, new String[]{"getNameForObject", "func_148750_c"}, Object.class, 1);

        mGetFluid = meth(cFluidStack, new String[]{"getFluid", "func_151528_c"}, cFluid, 0);
        fFluidAmount = fld(cFluidStack, new String[]{"amount", "field_75953_a"}, int.class);
        mFluidName = meth(cFluid, new String[]{"getName", "func_125516_b"}, String.class, 0);

        mMcInstance = meth(cMc, new String[]{"getMinecraft", "func_71410_x"}, cMc, 0);
        fDataDir = fld(cMc, new String[]{"mcDataDir", "field_71412_D"}, File.class);

        report("ItemStack.getItem", mGetItem);
        report("ItemStack.getItemDamage", mGetDamage);
        report("ItemStack.getTagCompound", mGetTag);
        report("ItemStack.stackSize", fStackSize);
        report("Item.itemRegistry", fItemRegistry);
        report("FluidStack.getFluid", mGetFluid);
        report("FluidStack.amount", fFluidAmount);
        report("Fluid.getName", mFluidName);
        report("Minecraft.getMinecraft", mMcInstance);
        report("Minecraft.mcDataDir", fDataDir);
    }

    private static void report(String what, Object resolved) {
        if (resolved == null) {
            System.out.println(GtverseDumper.TAG + "WARN unresolved: " + what);
        } else if (resolved instanceof Method) {
            System.out.println(GtverseDumper.TAG + "resolved " + what + " -> " + ((Method) resolved).getName());
        } else if (resolved instanceof Field) {
            System.out.println(GtverseDumper.TAG + "resolved " + what + " -> " + ((Field) resolved).getName());
        }
    }

    static Class<?> cls(String name) {
        try {
            return Class.forName(name, false, Refl.class.getClassLoader());
        } catch (Throwable t) {
            System.out.println(GtverseDumper.TAG + "class missing: " + name);
            return null;
        }
    }

    static Method meth(Class<?> c, String[] names, Class<?> ret, int nParams) {
        if (c == null) {
            return null;
        }
        Method[] ms;
        try {
            ms = c.getMethods();
        } catch (Throwable t) {
            return null;
        }
        Method bySig = null;
        int sigCount = 0;
        for (Method m : ms) {
            if (m.getReturnType() != ret) {
                continue;
            }
            if (m.getParameterTypes().length != nParams) {
                continue;
            }
            String nm = m.getName();
            for (String cand : names) {
                if (nm.equals(cand)) {
                    return m;
                }
            }
            sigCount++;
            bySig = m;
        }
        return sigCount == 1 ? bySig : null;
    }

    static Field fld(Class<?> c, String[] names, Class<?> type) {
        if (c == null || type == null) {
            return null;
        }
        Field[] fs;
        try {
            fs = c.getFields();
        } catch (Throwable t) {
            return null;
        }
        Field byType = null;
        int typeCount = 0;
        for (Field f : fs) {
            if (f.getType() != type) {
                continue;
            }
            String nm = f.getName();
            for (String cand : names) {
                if (nm.equals(cand)) {
                    return f;
                }
            }
            typeCount++;
            byType = f;
        }
        return typeCount == 1 ? byType : null;
    }

    // ------------------------------------------------------------- convenience calls

    public static Object mcInstance() {
        if (mMcInstance == null) {
            return null;
        }
        try {
            return mMcInstance.invoke(null);
        } catch (Throwable t) {
            return null;
        }
    }

    public static File dataDir(Object mc) {
        if (fDataDir == null || mc == null) {
            return null;
        }
        try {
            Object v = fDataDir.get(mc);
            return v instanceof File ? (File) v : null;
        } catch (Throwable t) {
            return null;
        }
    }

    public static Object itemOf(Object stack) {
        if (mGetItem == null || stack == null) {
            return null;
        }
        try {
            return mGetItem.invoke(stack);
        } catch (Throwable t) {
            return null;
        }
    }

    public static String registryName(Object item) {
        if (item == null || fItemRegistry == null) {
            return null;
        }
        if (mGetNameForObject == null) {
            probeRegistryName(item);
        }
        if (mGetNameForObject == null) {
            return null;
        }
        try {
            Object reg = fItemRegistry.get(null);
            Object n = mGetNameForObject.invoke(reg, item);
            return n == null ? null : n.toString();
        } catch (Throwable t) {
            return null;
        }
    }

    /** finds "name for object" by probing, because the srg name is not stable across versions */
    private static void probeRegistryName(Object item) {
        if (cRegistry == null) {
            return;
        }
        Object reg;
        try {
            reg = fItemRegistry.get(null);
        } catch (Throwable t) {
            return;
        }
        if (reg == null) {
            return;
        }
        String self;
        try {
            self = String.valueOf(item);
        } catch (Throwable t) {
            self = null;
        }
        Method[] ms;
        try {
            ms = cRegistry.getMethods();
        } catch (Throwable t) {
            return;
        }
        // prefer methods whose name looks like "name" lookups
        Method best = null;
        for (int pass = 0; pass < 2 && best == null; pass++) {
            for (Method m : ms) {
                Class<?>[] ps = m.getParameterTypes();
                if (ps.length != 1 || ps[0] != Object.class) {
                    continue;
                }
                if (m.getReturnType() == void.class) {
                    continue;
                }
                boolean named = m.getName().toLowerCase().contains("name");
                if (pass == 0 && !named) {
                    continue;
                }
                try {
                    Object r = m.invoke(reg, item);
                    if (!(r instanceof String)) {
                        continue;
                    }
                    String s = (String) r;
                    if (s.length() == 0 || (self != null && self.equals(s))) {
                        continue;
                    }
                    best = m;
                    break;
                } catch (Throwable ignored) {
                    // try the next candidate
                }
            }
        }
        if (best != null) {
            mGetNameForObject = best;
            System.out.println(GtverseDumper.TAG + "registry name resolved by probing -> " + best.getName());
        } else {
            System.out.println(GtverseDumper.TAG + "WARN: item registry name lookup unavailable, using class name");
        }
    }

    public static int damageOf(Object stack) {
        if (mGetDamage == null || stack == null) {
            return 0;
        }
        try {
            Object v = mGetDamage.invoke(stack);
            return v instanceof Integer ? ((Integer) v).intValue() : 0;
        } catch (Throwable t) {
            return 0;
        }
    }

    public static int countOf(Object stack) {
        if (fStackSize == null || stack == null) {
            return 1;
        }
        try {
            return fStackSize.getInt(stack);
        } catch (Throwable t) {
            return 1;
        }
    }

    public static String nbtOf(Object stack) {
        if (mGetTag == null || stack == null) {
            return null;
        }
        try {
            Object tag = mGetTag.invoke(stack);
            if (tag == null) {
                return null;
            }
            String s = tag.toString();
            return s;
        } catch (Throwable t) {
            return null;
        }
    }

    public static String fluidOf(Object fsStack) {
        if (mGetFluid == null || fsStack == null) {
            return null;
        }
        try {
            Object fluid = mGetFluid.invoke(fsStack);
            if (fluid == null) {
                return null;
            }
            if (mFluidName != null) {
                Object n = mFluidName.invoke(fluid);
                if (n != null) {
                    return n.toString();
                }
            }
            return fluid.toString();
        } catch (Throwable t) {
            return null;
        }
    }

    public static int amountOf(Object fsStack) {
        if (fFluidAmount == null || fsStack == null) {
            return 0;
        }
        try {
            return fFluidAmount.getInt(fsStack);
        } catch (Throwable t) {
            return 0;
        }
    }
}
