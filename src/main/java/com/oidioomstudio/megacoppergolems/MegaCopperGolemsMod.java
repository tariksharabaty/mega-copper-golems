package com.oidioomstudio.megacoppergolems;

import com.oidioomstudio.megacoppergolems.config.MegaCopperGolemsConfig;

import com.oidioomstudio.megacoppergolems.entity.MegaCopperGolemEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MegaCopperGolemsMod implements ModInitializer {
    public static final String MOD_ID = "mega_copper_golems";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final ResourceKey<EntityType<?>> GOLEM_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, "mega_copper_golem")
    );

    public static final EntityType<MegaCopperGolemEntity> MEGA_COPPER_GOLEM = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            GOLEM_KEY,
            EntityType.Builder.of(MegaCopperGolemEntity::new, MobCategory.MISC)
                    .sized(1.4F, 2.7F)
                    .clientTrackingRange(10)
                    .build(GOLEM_KEY)
    );

    public static final ResourceKey<Item> SPAWN_EGG_KEY = ResourceKey.create(
            Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, "mega_copper_golem_spawn_egg")
    );

    public static final Item MEGA_COPPER_GOLEM_SPAWN_EGG = Registry.register(
            BuiltInRegistries.ITEM,
            SPAWN_EGG_KEY,
            new SpawnEggItem(new Item.Properties().setId(SPAWN_EGG_KEY).spawnEgg(MEGA_COPPER_GOLEM))
    );

    @Override
    public void onInitialize() {
        LOGGER.info("Mega Copper Golems v1.0.1 by Oidioom Studio initialized for Minecraft 26.3!");
        MegaCopperGolemsConfig.load();
        com.oidioomstudio.olib.api.OCreativeTabs.accept(MEGA_COPPER_GOLEM_SPAWN_EGG);

        // 1. Attributes
        FabricDefaultAttributeRegistry.register(MEGA_COPPER_GOLEM, MegaCopperGolemEntity.createAttributes());

        // 2. Creative Mode Tab - Spawn Eggs tab
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> {
            entries.accept(MEGA_COPPER_GOLEM_SPAWN_EGG);
            LOGGER.info("Mega Copper Golem Spawn Egg added to Spawn Eggs creative tab!");
        });

        // 3. Fallback direct block interaction (works alongside CarvedPumpkinBlockMixin)
        UseBlockCallback.EVENT.register(this::onUseBlock);
    }

    private InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (!MegaCopperGolemsConfig.get().enableStructureSummoning) return InteractionResult.PASS;
        if (heldItem.is(Blocks.CARVED_PUMPKIN.asItem()) || heldItem.is(Blocks.JACK_O_LANTERN.asItem())) {
            BlockPos headPos = hitResult.getBlockPos().relative(hitResult.getDirection());
            if (checkStructure(level, headPos)) {
                if (!level.isClientSide()) {
                    spawnGolemFromStructure(level, headPos);
                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public static boolean checkStructure(Level level, BlockPos headPos) {
        BlockPos bodyPos = headPos.below();
        BlockPos basePos = bodyPos.below();

        BlockState bodyState = level.getBlockState(bodyPos);
        BlockState baseState = level.getBlockState(basePos);

        if (!isCopper(bodyState) || !isCopper(baseState)) {
            return false;
        }

        boolean xArms = isCopper(level.getBlockState(bodyPos.east())) && isCopper(level.getBlockState(bodyPos.west()));
        boolean zArms = isCopper(level.getBlockState(bodyPos.north())) && isCopper(level.getBlockState(bodyPos.south()));

        return xArms || zArms;
    }

    public static void spawnGolemFromStructure(Level level, BlockPos headPos) {
        BlockPos bodyPos = headPos.below();
        BlockPos basePos = bodyPos.below();

        boolean xArms = isCopper(level.getBlockState(bodyPos.east())) && isCopper(level.getBlockState(bodyPos.west()));

        // Destroy head, center, base, and arms
        level.destroyBlock(headPos, false);
        level.destroyBlock(bodyPos, false);
        level.destroyBlock(basePos, false);
        if (xArms) {
            level.destroyBlock(bodyPos.east(), false);
            level.destroyBlock(bodyPos.west(), false);
        } else {
            level.destroyBlock(bodyPos.north(), false);
            level.destroyBlock(bodyPos.south(), false);
        }

        if (level instanceof ServerLevel serverLevel) {
            MegaCopperGolemEntity golem = MEGA_COPPER_GOLEM.create(serverLevel, EntitySpawnReason.TRIGGERED);
            if (golem != null) {
                golem.snapTo(basePos.getX() + 0.5D, (double) basePos.getY(), basePos.getZ() + 0.5D, 0.0F, 0.0F);
                golem.setPlayerCreated(true);
                serverLevel.addFreshEntity(golem);
                level.playSound(null, basePos, SoundEvents.COPPER_PLACE, SoundSource.BLOCKS, 1.0F, 0.8F);
                level.playSound(null, basePos, SoundEvents.COPPER_BULB_TURN_ON, SoundSource.BLOCKS, 1.2F, 0.9F);
                for (net.minecraft.server.level.ServerPlayer serverPlayer : serverLevel.getEntitiesOfClass(net.minecraft.server.level.ServerPlayer.class, golem.getBoundingBox().inflate(10.0D))) {
                    net.minecraft.advancements.triggers.CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, golem);
                }
                LOGGER.info("Mega Copper Golem summoned successfully at {}", basePos);
            }
        }
    }

    public static boolean isCopper(BlockState state) {
        if (state.is(BlockTags.COPPER)) {
            return true;
        }
        Block block = state.getBlock();
        if (Blocks.COPPER_BLOCK.asList().contains(block)) {
            return true;
        }
        if (Blocks.CUT_COPPER.asList().contains(block)) {
            return true;
        }
        if (Blocks.CHISELED_COPPER.asList().contains(block)) {
            return true;
        }
        return block == Blocks.RAW_COPPER_BLOCK;
    }
}
