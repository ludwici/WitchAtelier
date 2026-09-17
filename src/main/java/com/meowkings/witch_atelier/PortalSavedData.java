package com.meowkings.witch_atelier;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

public final class PortalSavedData extends SavedData {
    private static final String DATA_NAME = WitchAtelier.MODID + "_portals";

    public String portalDatabase = "";
    public String portalLinks = "";

    public static PortalSavedData get(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            throw new IllegalStateException("PortalSavedData is server-side only");
        }
        ServerLevel overworld = serverLevel.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(PortalSavedData::new, PortalSavedData::load), DATA_NAME);
    }

    public static PortalSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        PortalSavedData data = new PortalSavedData();
        data.portalDatabase = tag.getString("portal_database");
        data.portalLinks = tag.getString("portal_links");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putString("portal_database", portalDatabase);
        tag.putString("portal_links", portalLinks);
        return tag;
    }
}
