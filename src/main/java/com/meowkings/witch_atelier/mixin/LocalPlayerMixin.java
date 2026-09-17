package com.meowkings.witch_atelier.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Redirect(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private ItemStack checkElytraFly(LocalPlayer instance, EquipmentSlot equipmentSlot) {
        ItemStack item = instance.getItemBySlot(EquipmentSlot.FEET);
        if (item.canElytraFly(instance)) {
            return item;
        }

        return instance.getItemBySlot(equipmentSlot);
    }
}
