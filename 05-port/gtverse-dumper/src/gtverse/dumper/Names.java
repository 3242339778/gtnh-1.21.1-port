package gtverse.dumper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resolves unlocalized name / display name / ore dictionary tags at runtime.
 * Runtime is MCP class names + srg member names, so every call goes through
 * name-based reflection; nothing here touches the compile-time stubs.
 */
public final class Names {

    private static final Map<String, Method> CACHE = new HashMap<String, Method>();
    private static boolean oreLookupFailed;

    private Names() {
    }

    /** find a public method by name (and optional parameter count) */
    private static Method find(Class<?> c, String[] names, int argCount) {
        for (Method m : c.getMethods()) {
            for (String n : names) {
                if (!m.getName().equals(n)) {
                    continue;
                }
                if (argCount >= 0 && m.getParameterTypes().length != argCount) {
                    continue;
                }
                try {
                    m.setAccessible(true);
                } catch (Throwable ignored) {
                }
                return m;
            }
        }
        return null;
    }

    private static Method cached(Class<?> c, String key, String[] names, int argCount) {
        String k = c.getName() + "#" + key + "#" + argCount;
        if (CACHE.containsKey(k)) {
            return CACHE.get(k);
        }
        Method m = find(c, names, argCount);
        CACHE.put(k, m);
        return m;
    }

    private static Object call(Object target, Method m, Object... args) {
        if (m == null) {
            return null;
        }
        try {
            return m.invoke(target, args);
        } catch (Throwable t) {
            return null;
        }
    }

    private static Object itemOf(Object stack) {
        if (stack == null) {
            return null;
        }
        Method m = cached(stack.getClass(), "getItem", new String[]{"getItem", "func_77973_b"}, 0);
        return call(stack, m);
    }

    /** Item.getUnlocalizedName(), e.g. "gt.metaitem.01.dustIron" */
    public static String un(Object stack) {
        Object item = itemOf(stack);
        if (item == null) {
            return null;
        }
        Method m = cached(item.getClass(), "getUnlocalizedName",
                new String[]{"getUnlocalizedName", "func_77658_a"}, 0);
        Object s = call(item, m);
        return s == null ? null : String.valueOf(s);
    }

    /** ItemStack.getDisplayName() first (honours custom NBT names), then Item.getItemStackDisplayName(stack) */
    public static String dn(Object stack) {
        if (stack == null) {
            return null;
        }
        Method m = cached(stack.getClass(), "getDisplayName", new String[]{"getDisplayName", "func_82833_r"}, 0);
        Object s = call(stack, m);
        if (s != null) {
            String v = String.valueOf(s);
            if (v.length() > 0 && !v.startsWith("item.") && !v.startsWith("tile.")) {
                return v;
            }
        }
        Object item = itemOf(stack);
        if (item == null) {
            return s == null ? null : String.valueOf(s);
        }
        Method m2 = cached(item.getClass(), "getItemStackDisplayName",
                new String[]{"getItemStackDisplayName", "func_77653_i"}, 1);
        Object s2 = call(item, m2, stack);
        if (s2 != null) {
            return String.valueOf(s2);
        }
        return s == null ? null : String.valueOf(s);
    }

    /** Forge OreDictionary tags for this stack, capped at 4 entries. */
    public static String[] od(Object stack) {
        if (stack == null || oreLookupFailed) {
            return null;
        }
        try {
            Class<?> c = Class.forName("net.minecraftforge.oredict.OreDictionary");
            Method ids = cached(c, "getOreIDs", new String[]{"getOreIDs"}, 1);
            Method name = cached(c, "getOreName", new String[]{"getOreName"}, 1);
            if (ids == null || name == null) {
                oreLookupFailed = true;
                return null;
            }
            Object r = call(null, ids, stack);
            if (!(r instanceof int[])) {
                return null;
            }
            int[] arr = (int[]) r;
            List<String> out = new ArrayList<String>();
            for (int i = 0; i < arr.length && out.size() < 4; i++) {
                Object n = call(null, name, Integer.valueOf(arr[i]));
                if (n != null) {
                    String v = String.valueOf(n);
                    if (v.length() > 0 && !"Unknown".equals(v)) {
                        out.add(v);
                    }
                }
            }
            return out.toArray(new String[out.size()]);
        } catch (Throwable t) {
            oreLookupFailed = true;
            return null;
        }
    }
}
