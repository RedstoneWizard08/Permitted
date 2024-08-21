package redstonedev.permitted.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redstonedev.permitted.Permitted;
import redstonedev.permitted.data.PermitData;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.function.Function;

public record DynamicPermitModel(PermitData.Rarity rarity) implements IUnbakedGeometry<DynamicPermitModel> {
    private static final Map<PermitData.Rarity, BakedModel> cache = Maps.newHashMap();
    private static UnbakedModel unbaked = null;
    private static final ResourceLocation missing = new ResourceLocation("minecraft:missingno");

    @SuppressWarnings("deprecation")
    @Override
    @ParametersAreNonnullByDefault
    public BakedModel bake(IGeometryBakingContext ctx, ModelBaker baker, Function<Material, TextureAtlasSprite> getter, ModelState state, ItemOverrides overrides, ResourceLocation id) {
        if (unbaked == null) {
            unbaked = baker.getModel(new ResourceLocation("minecraft", "item/generated"));
        }

        BakedModel baked = unbaked.bake(baker, getter, state, id);

        assert baked != null;

        Function<Material, TextureAtlasSprite> betterGetter = material ->
                getter.apply(!material.texture().equals(missing) ?
                        material :
                        new Material(TextureAtlas.LOCATION_BLOCKS, Permitted.id("item/" + rarity.name().toLowerCase() + "_permit")));

        var builder = CompositeModel.Baked.builder(
                ctx,
                betterGetter.apply(ctx.getMaterial("layer0")),
                new OverrideHandler(overrides, baker, ctx),
                ctx.getTransforms()
        );

        builder.addLayer(baked);
        builder.setParticle(betterGetter.apply(ctx.getMaterial("layer0")));

        return builder.build();
    }

    public static final class Loader implements IGeometryLoader<DynamicPermitModel> {
        public static final Loader INSTANCE = new Loader();

        private Loader() {
        }

        @Override
        @ParametersAreNonnullByDefault
        public @NotNull DynamicPermitModel read(JsonObject json, JsonDeserializationContext context) {
            return new DynamicPermitModel(PermitData.Rarity.NONE);
        }
    }

    private static final class OverrideHandler extends ItemOverrides {
        private final ItemOverrides nested;
        private final ModelBaker baker;
        private final IGeometryBakingContext owner;

        private OverrideHandler(ItemOverrides nested, ModelBaker baker, IGeometryBakingContext owner) {
            this.nested = nested;
            this.baker = baker;
            this.owner = owner;
        }

        @Override
        @ParametersAreNonnullByDefault
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            BakedModel overridden = nested.resolve(originalModel, stack, level, entity, seed);
            if (overridden != originalModel)
                return overridden;
            PermitData data = PermitData.getOrCreate(stack);

            if (!cache.containsKey(data.rarity)) {
                DynamicPermitModel unbaked = new DynamicPermitModel(data.rarity);
                BakedModel baked = unbaked.bake(owner, baker, Material::sprite, BlockModelRotation.X0_Y0, this, Permitted.id("permit"));

                cache.put(data.rarity, baked);

                return baked;
            }

            return cache.get(data.rarity);
        }
    }

    public static final class Builder extends CustomLoaderBuilder<ItemModelBuilder> {
        public Builder(ResourceLocation loaderId, ItemModelBuilder parent, ExistingFileHelper efh) {
            super(loaderId, parent, efh);
        }
    }
}
