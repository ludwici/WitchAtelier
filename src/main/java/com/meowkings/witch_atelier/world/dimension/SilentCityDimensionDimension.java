package com.meowkings.witch_atelier.world.dimension;

import com.meowkings.witch_atelier.WitchAtelier;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.DimensionSpecialEffects;

public class SilentCityDimensionDimension {
    @EventBusSubscriber(modid = WitchAtelier.MODID, value = Dist.CLIENT)
    public static class SilentCityDimensionSpecialEffectsHandler {
        @SubscribeEvent
        public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
            DimensionSpecialEffects customEffect = new DimensionSpecialEffects(192f, true, DimensionSpecialEffects.SkyType.NORMAL, false, false) {
                @Override
                public Vec3 getBrightnessDependentFogColor(Vec3 color, float sunHeight) {
                    return color;
                }

                @Override
                public boolean isFoggyAt(int x, int y) {
                    return false;
                }
            };
            event.register(ResourceLocation.fromNamespaceAndPath(WitchAtelier.MODID, "silent_city_dimension"), customEffect);
        }
    }
}
