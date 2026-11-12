package com.oidioomstudio.megacoppergolems.client;

import com.oidioomstudio.megacoppergolems.MegaCopperGolemsMod;
import com.oidioomstudio.olib.client.OModMenuApi;

public class ModMenuIntegration extends OModMenuApi {
    @Override
    protected String getModId() {
        return MegaCopperGolemsMod.MOD_ID;
    }
}

