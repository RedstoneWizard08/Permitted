package redstonedev.permitted.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import redstonedev.permitted.Permitted;
import redstonedev.permitted.client.PermittedClient;

@Mod(Permitted.MOD_ID)
public class PermittedForge {
    public static IEventBus EVENT_BUS;

    public PermittedForge() {
        EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        
        Permitted.REGISTRATE.registerEventListeners(EVENT_BUS);
        Permitted.init();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> PermittedClient::init);
    }
}
