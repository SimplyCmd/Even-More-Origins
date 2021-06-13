package io.github.simplycmd.even_more_origins.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.simplycmd.even_more_origins.power.DoubleJumpPower;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin { // Mixin credit to https://www.curseforge.com/minecraft/mc-mods/double-jump-mod
    private boolean doubleJumped = false;
    private int tick = 0;

    @Inject(method="tickMovement", at = @At("HEAD"))
    private void tickMovement(CallbackInfo info) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        if (PowerHolderComponent.hasPower((ClientPlayerEntity)(Object)this, DoubleJumpPower.class)) {
            if (!doubleJumped && player.getVelocity().y < 0 && !player.isOnGround() && !player.isClimbing() && canJump(player) && player.input.jumping) {
                player.jump();
                doubleJumped = true;
            }
            if (player.isOnGround()) doubleJumped = false;
        }
        if (((ClientPlayerEntity)(Object)this).getPassengerList().size() > 0 && ((ClientPlayerEntity)(Object)this).getPassengerList().get(0).getDisplayName().getString().equals("§4§lVampire")) {
            if (((ClientPlayerEntity)(Object)this).getPassengerList().get(0).getDisplayName().getString().equals("§4§lVampire") && !player.isOnGround() && !player.isClimbing() && canJump(player) && player.input.jumping)
                player.jump();
            tick++;
            if (tick >= 600) {
                player.removeAllPassengers();
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 40, 255, false, false, false));
                tick = 0;
            }
        }
    }

    private boolean canJump(ClientPlayerEntity player) {
        return !(player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA && ElytraItem.isUsable(player.getEquippedStack(EquipmentSlot.CHEST))) && !player.isFallFlying() && !player.hasVehicle() && !player.isTouchingWater() && !player.hasStatusEffect(StatusEffects.LEVITATION);
    }
}
