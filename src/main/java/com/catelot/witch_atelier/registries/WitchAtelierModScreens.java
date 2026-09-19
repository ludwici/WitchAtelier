package com.catelot.witch_atelier.registries;

import com.catelot.witch_atelier.WitchAtelier;
import com.catelot.witch_atelier.client.gui.CanvasGUIScreen;
import com.catelot.witch_atelier.client.gui.MagicGuideGUIScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = WitchAtelier.MODID, value = Dist.CLIENT)
public final class WitchAtelierModScreens {
    private WitchAtelierModScreens() {
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(WitchAtelierModMenus.CANVAS_GUI.get(), CanvasGUIScreen::new);
        event.register(WitchAtelierModMenus.MAGIC_GUIDE_GUI.get(), MagicGuideGUIScreen::new);
    }
}
