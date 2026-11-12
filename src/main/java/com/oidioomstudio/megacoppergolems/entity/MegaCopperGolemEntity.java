package com.oidioomstudio.megacoppergolems.entity;

import com.oidioomstudio.megacoppergolems.config.MegaCopperGolemsConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MegaCopperGolemEntity extends IronGolem {
    public MegaCopperGolemEntity(EntityType<? extends IronGolem> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return IronGolem.createAttributes()
                .add(Attributes.MAX_HEALTH, 200.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.ATTACK_DAMAGE, 22.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    // --- Peaceful Behavior towards Players ---
    @Override
    public boolean canAttack(LivingEntity target) {
        if (target instanceof Player) {
            return false;
        }
        return super.canAttack(target);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target instanceof Player) {
            return;
        }
        super.setTarget(target);
    }

    @Override
    public void setPersistentAngerTarget(@Nullable net.minecraft.world.entity.EntityReference<LivingEntity> target) {
        if (target != null && target.getEntity(this.level(), LivingEntity.class) instanceof Player) {
            return;
        }
        super.setPersistentAngerTarget(target);
    }

    @Override
    public boolean isAngryAt(LivingEntity entity, ServerLevel level) {
        if (entity instanceof Player) {
            return false;
        }
        return super.isAngryAt(entity, level);
    }

    @Override
    public boolean isAngryAtAllPlayers(ServerLevel level) {
        return false;
    }

    // --- Custom Death Drops: 5-11 Copper Ingots, Rare Copper Chest ---
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);

        // 5-11 Copper Ingots
        int count = 5 + this.random.nextInt(7); // 5 to 11
        this.spawnAtLocation(level, new ItemStack(Items.COPPER_INGOT, count));

        // Rarely a copper chest (~15% chance)
        if (MegaCopperGolemsConfig.get().enableCopperChestLoot && this.random.nextFloat() < 0.15F) {
            ItemStack chest = new ItemStack(Items.CHEST);
            chest.set(DataComponents.CUSTOM_NAME, Component.translatable("item.mega_copper_golems.copper_chest"));
            this.spawnAtLocation(level, chest);
        }
    }

    // --- Repair / Heal with Copper Ingots ---
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(Items.COPPER_INGOT)) {
            float health = this.getHealth();
            this.heal(30.0F);
            if (this.getHealth() == health) {
                return InteractionResult.PASS;
            }
            float pitch = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
            this.playSound(SoundEvents.COPPER_PLACE, 1.0F, pitch);
            itemStack.consume(1, player);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    // --- Attack Sounds ---
    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hurt = super.doHurtTarget(level, target);
        if (hurt) {
            this.playSound(SoundEvents.COPPER_GRATE_HIT, 1.4F, 0.8F);
        }
        return hurt;
    }

    // --- Copper Sounds ---
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.COPPER_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.COPPER_BREAK;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.COPPER_STEP, 1.0F, 1.0F);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.COPPER_BULB_TURN_ON;
    }
}
