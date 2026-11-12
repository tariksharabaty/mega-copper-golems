package com.oidioomstudio.megacoppergolems.config;

import com.oidioomstudio.olib.config.OConfigHolder;

public final class MegaCopperGolemsConfig {
    public static class Data {
        public double maxHealth = 200.0;
        public double attackDamage = 22.0;
        public double movementSpeed = 0.28;
        public boolean enableStructureSummoning = true;
        public boolean enableCopperChestLoot = true;
    }

    private static final OConfigHolder<Data> HOLDER = new OConfigHolder<>(
            "mega_copper_golems",
            Data.class,
            Data::new
    );

    private static Data current = new Data();

    public static void load() {
        current = HOLDER.load();
    }

    public static void save() {
        HOLDER.save();
    }

    public static Data get() {
        if (current == null) {
            load();
        }
        return current;
    }
}

