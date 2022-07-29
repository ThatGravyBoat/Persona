package tech.thatgravyboat.persona;

import com.mojang.logging.LogUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import org.slf4j.Logger;
import tech.thatgravyboat.persona.common.entity.Persona;
import tech.thatgravyboat.persona.common.network.NetPacketHandler;
import tech.thatgravyboat.persona.common.registry.Registry;

import java.util.Map;

public class Personas {

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "persona";

    public static void init() {
        NetPacketHandler.init();
        Registry.register();
    }
}
