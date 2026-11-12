package com.oidioomstudio.megacoppergolems.mixin;

import com.oidioomstudio.megacoppergolems.MegaCopperGolemsMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CarvedPumpkinBlock.class)
public class CarvedPumpkinBlockMixin {
    @Inject(method = "trySpawnGolem", at = @At("HEAD"), cancellable = true)
    private void trySpawnMegaCopperGolem(Level level, BlockPos pos, CallbackInfo ci) {
        if (com.oidioomstudio.megacoppergolems.config.MegaCopperGolemsConfig.get().enableStructureSummoning && MegaCopperGolemsMod.checkStructure(level, pos)) {
            MegaCopperGolemsMod.spawnGolemFromStructure(level, pos);
            ci.cancel();
        }
    }
}
