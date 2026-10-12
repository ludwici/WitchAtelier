package com.catelot.witch_atelier.registries;

import com.catelot.witch_atelier.WitchAtelier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class WitchAtelierModBlockTags {
    public static final TagKey<Block> WOOD_PORTAL_PARTS = create("wood_portal_parts");
    public static final TagKey<Block> STONE_PORTAL_PARTS = create("stone_portal_parts");

    private WitchAtelierModBlockTags() {
    }

    private static TagKey<Block> create(String path) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(WitchAtelier.MODID, path));
    }
}
