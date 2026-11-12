package com.oidioomstudio.megacoppergolems.client.renderer;

import com.oidioomstudio.megacoppergolems.MegaCopperGolemsMod;
import com.oidioomstudio.megacoppergolems.client.model.MegaCopperGolemModel;
import com.oidioomstudio.megacoppergolems.entity.MegaCopperGolemEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.IronGolemRenderState;
import net.minecraft.resources.Identifier;

public class MegaCopperGolemRenderer extends MobRenderer<MegaCopperGolemEntity, IronGolemRenderState, MegaCopperGolemModel> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            MegaCopperGolemsMod.MOD_ID, "textures/entity/mega_copper_golem.png"
    );

    public MegaCopperGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new MegaCopperGolemModel(null), 1.2F);
    }

    @Override
    public Identifier getTextureLocation(IronGolemRenderState state) {
        return TEXTURE;
    }

    @Override
    public IronGolemRenderState createRenderState() {
        return new IronGolemRenderState();
    }

    @Override
    public void extractRenderState(MegaCopperGolemEntity entity, IronGolemRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
    }
}
