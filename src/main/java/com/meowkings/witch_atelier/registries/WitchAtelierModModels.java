package com.meowkings.witch_atelier.registries;

import com.meowkings.witch_atelier.WitchAtelier;
import com.meowkings.witch_atelier.client.model.ModelCustomModel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = WitchAtelier.MODID, value = Dist.CLIENT)
public final class WitchAtelierModModels {
    private WitchAtelierModModels() {
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModelCustomModel.LAYER_LOCATION, ModelCustomModel::createBodyLayer);
    }
}
