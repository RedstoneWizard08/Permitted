package redstonedev.permitted.fabric;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import redstonedev.permitted.Permitted;

public class PermittedFabricDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator gen) {
        Permitted.REGISTRATE.setupDatagen(gen.createPack(), ExistingFileHelper.withResourcesFromArg());
    }
}
