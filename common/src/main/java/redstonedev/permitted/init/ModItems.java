package redstonedev.permitted.init;

import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.world.item.Item;
import redstonedev.permitted.Permitted;
import redstonedev.permitted.items.Permit;

public class ModItems {
    public static final ItemEntry<Permit> PERMIT = Permitted.REGISTRATE.item("permit", Permit::new)
            .model((ctx, prov) -> {
            })
            .register();

    public static final ItemEntry<Item> DIAMOND_PERMIT = Permitted.REGISTRATE.item("diamond_permit", Item::new)
            .model((ctx, prov) -> {
            })
            .register();
    
    public static void init() {
        // This is just here so the class gets loaded
    }
}
