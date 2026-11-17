package gtverse.ticcompat;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 1.7.10 用 NBT {material:"Aluminium"} 区分同一 item 的材料；
 * 1.21 用 data component 承载，item id 保持不变 => 配方引用不用改。
 */
public final class TicMaterialComponents {

    /** 必须与 neoforge.mods.toml 的 modId 一致，@Mod 与注册命名空间都用它。 */
    public static final String MODID = "tconstruct";

    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MODID);

    /** 材料内部名，CamelCase，如 "Aluminium" */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> MATERIAL =
            COMPONENTS.register("material", () -> DataComponentType.<String>builder()
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                    .build());

    private TicMaterialComponents() {}
}
