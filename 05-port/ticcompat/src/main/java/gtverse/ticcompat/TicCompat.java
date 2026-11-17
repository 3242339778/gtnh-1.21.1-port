package gtverse.ticcompat;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/** tconstruct 兼容层（Legacy Connector 的 TiC 段）。 */
@Mod(TicMaterialComponents.MODID)
public final class TicCompat {

    public TicCompat(IEventBus modBus) {
        TicCompatItems.ITEMS.register(modBus);
        TicMaterialComponents.COMPONENTS.register(modBus);
    }
}
