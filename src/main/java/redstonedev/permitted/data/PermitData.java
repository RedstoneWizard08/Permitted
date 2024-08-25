package redstonedev.permitted.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import redstonedev.permitted.util.ExtraCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class PermitData {
    public static final Codec<PermitData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ItemStack.CODEC.listOf().xmap(ArrayList::new, ArrayList::new).fieldOf("items").forGetter(data -> new ArrayList<>(data.items.stream().map(Item::getDefaultInstance).toList())),
            Codec.STRING.xmap(Rarity::valueOf, Rarity::name).fieldOf("rarity").forGetter(data -> data.rarity),
            ExtraCodecs.UUID_CODEC.optionalFieldOf("owner").forGetter(data -> data.owner),
            Codec.STRING.optionalFieldOf("ownerName").forGetter(data -> data.ownerName)
    ).apply(inst, PermitData::new));

    public static final PermitData DEFAULT = new PermitData();

    public final ArrayList<Item> items;
    public Rarity rarity;
    public Optional<UUID> owner;
    public Optional<String> ownerName;

    public PermitData() {
        this(List.of(), Rarity.NONE, Optional.empty(), Optional.empty());
    }

    public PermitData(List<ItemStack> items, Rarity rarity, Optional<UUID> owner, Optional<String> ownerName) {
        this.items = new ArrayList<>(items.stream().map(ItemStack::getItem).toList());
        this.rarity = rarity;
        this.owner = owner;
        this.ownerName = ownerName;
    }

    public Component getOwner() {
        return ownerName.map(Component::literal).orElse(Component.translatable("permit.owner.none"));
    }

    public Component getName() {
        var comps = new ArrayList<Component>();

        for (Item item : items) {
            comps.add(item.getName(item.getDefaultInstance()));
        }

        var comp = Component.empty();

        for (int i = 0; i < comps.size(); i++) {
            comp = comp.append(comps.get(i));

            if (i < comps.size() - 2) {
                comp = comp.append(Component.translatable("permit.items.joiner"));
            } else if (i == comps.size() - 2 && comps.size() == 2) {
                comp = comp.append(Component.translatable("permit.items.joiner_and"));
            } else if (i == comps.size() - 2) {
                comp = comp.append(Component.translatable("permit.items.joiner_last"));
            }
        }

        return comp;
    }

    public static PermitData get(ItemStack stack) {
        Tag permit = stack.getOrCreateTag().get("permit");

        return PermitData.CODEC.parse(NbtOps.INSTANCE, permit).result().orElse(new PermitData());
    }

    public static void set(ItemStack stack, PermitData data) {
        stack.getOrCreateTag().put("permit", PermitData.CODEC.encodeStart(NbtOps.INSTANCE, data).result().orElseThrow());
    }

    public static PermitData getOrCreate(ItemStack stack, Supplier<PermitData> factory) {
        Tag permit = stack.getOrCreateTag().get("permit");

        return PermitData.CODEC.parse(NbtOps.INSTANCE, permit).result().orElseGet(factory);
    }

    public static PermitData getOrCreate(ItemStack stack) {
        return getOrCreate(stack, () -> PermitData.DEFAULT);
    }

    public enum Rarity {
        NONE(0),
        DIRT(1),
        IRON(2),
        GOLD(3),
        DIAMOND(4),
        EMERALD(5);

        public final int stars;
        public final String tooltip;

        Rarity(int stars) {
            this.stars = stars;
            this.tooltip = "permit.rarity." + name().toLowerCase();
        }

        public Component getTooltip() {
            return Component.translatable(tooltip);
        }
    }
}
