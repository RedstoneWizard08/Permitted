package redstonedev.permitted;

import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import redstonedev.permitted.model.DynamicPermitModel;

public class PermittedClient {
    public static void init(IEventBus bus) {
        bus.addListener(PermittedClient::registerModelLoaders);
    }

    public static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("permit", DynamicPermitModel.Loader.INSTANCE);
    }
}
