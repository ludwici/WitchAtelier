package com.catelot.witch_atelier.registries;

import com.catelot.witch_atelier.WitchAtelier;
import com.catelot.witch_atelier.world.inventory.CanvasGUIMenu;
import com.catelot.witch_atelier.world.inventory.MagicGuideGUIMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class WitchAtelierModMenus {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, WitchAtelier.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<CanvasGUIMenu>> CANVAS_GUI = REGISTRY.register("canvas_gui", () -> IMenuTypeExtension.create(CanvasGUIMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<MagicGuideGUIMenu>> MAGIC_GUIDE_GUI = REGISTRY.register("magic_guide_gui", () -> IMenuTypeExtension.create(MagicGuideGUIMenu::new));

    private WitchAtelierModMenus() {
    }
}
