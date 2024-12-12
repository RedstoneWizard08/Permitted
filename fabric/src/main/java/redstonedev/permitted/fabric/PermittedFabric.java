package redstonedev.permitted.fabric;

import net.fabricmc.api.ModInitializer;
import redstonedev.permitted.Permitted;

public class PermittedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Permitted.init();
        Permitted.REGISTRATE.register();
    }
}
