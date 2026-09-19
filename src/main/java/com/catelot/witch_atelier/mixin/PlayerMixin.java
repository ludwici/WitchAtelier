package com.catelot.witch_atelier.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Redirect(
            method = "tryToStartFallFlying",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private ItemStack checkElytra(Player instance, EquipmentSlot equipmentSlot) {
        ItemStack item = instance.getItemBySlot(EquipmentSlot.FEET);
        if (item.canElytraFly(instance)) {
            return item;
        }

        return instance.getItemBySlot(equipmentSlot);
    }
}
