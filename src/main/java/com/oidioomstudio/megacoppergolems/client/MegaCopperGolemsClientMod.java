package com.oidioomstudio.megacoppergolems.client;

import com.oidioomstudio.megacoppergolems.MegaCopperGolemsMod;
import com.oidioomstudio.megacoppergolems.client.renderer.MegaCopperGolemRenderer;
import com.oidioomstudio.megacoppergolems.config.MegaCopperGolemsConfig;
import com.oidioomstudio.olib.api.OConfigCategory;
import com.oidioomstudio.olib.api.OConfigRegistry;
import com.oidioomstudio.olib.api.OModConfig;
import com.oidioomstudio.olib.api.options.BooleanOption;
import com.oidioomstudio.olib.api.options.FloatSliderOption;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.network.chat.Component;

public class MegaCopperGolemsClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(MegaCopperGolemsMod.MEGA_COPPER_GOLEM, MegaCopperGolemRenderer::new);
        registerConfig();
    }

    private static void registerConfig() {
        MegaCopperGolemsConfig.Data cfg = MegaCopperGolemsConfig.get();

        OConfigRegistry.register(
                OModConfig.builder(MegaCopperGolemsMod.MOD_ID, Component.translatable("entity.mega_copper_golems.mega_copper_golem"), "1.1.2")
                        .category(OConfigCategory.builder("general", Component.translatable("mega_copper_golems.config.general"))
                                .group("attributes", Component.translatable("mega_copper_golems.config.attributes"), g -> g
                                        .add(FloatSliderOption.builder("max_health", Component.translatable("mega_copper_golems.config.max_health"))
                                                .range(50.0f, 500.0f, 10.0f)
                                                .defaultValue((float) cfg.maxHealth)
                                                .getter(() -> (float) cfg.maxHealth)
                                                .setter(v -> cfg.maxHealth = v)
                                                .build())
                                        .add(FloatSliderOption.builder("attack_damage", Component.translatable("mega_copper_golems.config.attack_damage"))
                                                .range(5.0f, 50.0f, 1.0f)
                                                .defaultValue((float) cfg.attackDamage)
                                                .getter(() -> (float) cfg.attackDamage)
                                                .setter(v -> cfg.attackDamage = v)
                                                .build())
                                )
                                .group("features", Component.translatable("mega_copper_golems.config.features"), g -> g
                                        .add(BooleanOption.builder("enable_summon", Component.translatable("mega_copper_golems.config.enable_summon"))
                                                .defaultValue(cfg.enableStructureSummoning)
                                                .getter(() -> cfg.enableStructureSummoning)
                                                .setter(v -> cfg.enableStructureSummoning = v)
                                                .build())
                                        .add(BooleanOption.builder("enable_chest_loot", Component.translatable("mega_copper_golems.config.enable_chest_loot"))
                                                .defaultValue(cfg.enableCopperChestLoot)
                                                .getter(() -> cfg.enableCopperChestLoot)
                                                .setter(v -> cfg.enableCopperChestLoot = v)
                                                .build())
                                )
                                .build())
                        .onSave(MegaCopperGolemsConfig::save)
                        .build()
        );
    }
}
