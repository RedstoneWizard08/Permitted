package redstonedev.permitted.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import redstonedev.permitted.client.PermittedClient;

@Environment(EnvType.CLIENT)
public class PermittedFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PermittedClient.init();
    }
}
