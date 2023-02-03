package tech.thatgravyboat.persona.common.entity;

import com.mojang.math.Vector3d;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import tech.thatgravyboat.persona.client.screens.DebugScreen;
import tech.thatgravyboat.persona.client.screens.NamingScreen;
import tech.thatgravyboat.persona.common.network.NetPacketHandler;
import tech.thatgravyboat.persona.common.network.messages.server.TeleportEntityMessage;

public class PersonaSpawnEgg extends Item {
    public PersonaSpawnEgg(Properties settings) {
        super(settings);
    }

    public void useOnEntity(Persona persona) {
        if (persona.level.isClientSide) {
            openDebugScreen(persona);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide) {
            BlockPos pos = context.getClickedPos();
            BlockPos offset = pos.relative(context.getClickedFace());
            if (context.getItemInHand().hasTag()) {
                double y = context.getClickedFace().equals(Direction.UP) ? context.getClickLocation().y : offset.getY();
                NetPacketHandler.sendToServer(new TeleportEntityMessage(new Vector3d(offset.getX(), y, offset.getZ())));
            } else {
                openScreen(offset);
            }
        }
        return super.useOn(context);
    }

    @Environment(EnvType.CLIENT)
    public void openScreen(BlockPos pos) {
        Minecraft.getInstance().setScreen(new NamingScreen(pos));
    }

    @Environment(EnvType.CLIENT)
    public void openDebugScreen(Persona persona) {
        Minecraft.getInstance().setScreen(new DebugScreen(persona));
    }
}
