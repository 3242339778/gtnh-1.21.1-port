package gtverse.dumper;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;

/**
 * GTverse recipe dumper.
 *
 * Dumps the full recipe graph of a 1.7.10 pack in two passes:
 *   1. GT RecipeMap.ALL_RECIPE_MAPS    -> dumps/gtverse_gt_recipes.json
 *   2. NEI crafting handlers (recipe-first) -> dumps/gtverse_nei_recipes.json
 *
 * Starts automatically once the client is up. No key binding, no GUI.
 */
@Mod(modid = "gtversedumper", name = "GTverse Dumper", version = "1.0", acceptableRemoteVersions = "*")
public class GtverseDumper {

    public static final String TAG = "[GtverseDumper] ";

    private static volatile boolean loadFinished = false;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        try {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    runDump();
                }
            }, "gtverse-dumper");
            t.setDaemon(true);
            t.start();
            System.out.println(TAG + "worker started");
        } catch (Throwable t) {
            System.out.println(TAG + "worker start failed: " + t);
        }
    }

    private static void runDump() {
        try {
            Refl.init();

            // wait until FML has finished post-init AND the client is truly up
            long deadline = System.currentTimeMillis() + 2400000L;
            while (!loadFinished && System.currentTimeMillis() < deadline) {
                Thread.sleep(250L);
            }
            if (!loadFinished) {
                System.out.println(TAG + "load-complete event never arrived, dumping anyway");
            }
            Object mc = null;
            for (int i = 0; i < 1200; i++) {
                mc = Refl.mcInstance();
                if (mc != null) {
                    break;
                }
                Thread.sleep(500);
            }
            if (mc == null) {
                System.out.println(TAG + "Minecraft instance never became available");
            }

            // let late recipe emitters settle
            Thread.sleep(30000L);

            java.io.File dataDir = Refl.dataDir(mc);
            if (dataDir == null || !dataDir.isDirectory()) {
                dataDir = new java.io.File(System.getProperty("user.dir", "."));
            }
            System.out.println(TAG + "using data dir " + dataDir.getAbsolutePath());
            try {
                DumpJob.begin(dataDir);
            } catch (Throwable t) {
                System.out.println(TAG + "begin failed: " + t);
                return;
            }
            while (true) {
                boolean finished;
                try {
                    finished = DumpJob.step(System.nanoTime() + 20000000L);
                } catch (Throwable t) {
                    System.out.println(TAG + "step failed: " + t);
                    return;
                }
                if (finished) {
                    System.out.println(TAG + "all done");
                    return;
                }
                Thread.sleep(15);
            }
        } catch (Throwable t) {
            System.out.println(TAG + "worker died: " + t);
        }
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        loadFinished = true;
        System.out.println(TAG + "load complete, dump will start shortly");
    }
}
