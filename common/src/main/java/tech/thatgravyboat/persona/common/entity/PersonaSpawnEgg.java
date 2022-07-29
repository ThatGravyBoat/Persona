package tech.thatgravyboat.persona.common.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import tech.thatgravyboat.persona.client.screens.DebugScreen;
import tech.thatgravyboat.persona.client.screens.NamingScreen;
import tech.thatgravyboat.persona.common.network.NetPacketHandler;
import tech.thatgravyboat.persona.common.network.messages.server.TeleportEntityMessage;

public class PersonaSpawnEgg extends Item {
    public PersonaSpawnEgg(Settings settings) {
        super(settings);
    }

    public void useOnEntity(Persona persona) {
        if (persona.world.isClient) {
            openDebugScreen(persona);
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().isClient) {
            BlockPos pos = context.getBlockPos();
            BlockPos offset = pos.offset(context.getSide());
            if (context.getStack().hasNbt()) {
                double y = context.getSide().equals(Direction.UP) ? context.getHitPos().y : offset.getY();
                NetPacketHandler.sendToServer(new TeleportEntityMessage(new Vec3d(offset.getX(), y, offset.getZ())));
            } else {
                openScreen(offset);
            }
        }
        return super.useOnBlock(context);
    }

    @Environment(EnvType.CLIENT)
    public void openScreen(BlockPos pos) {
        MinecraftClient.getInstance().setScreen(new NamingScreen(pos));
    }

    @Environment(EnvType.CLIENT)
    public void openDebugScreen(Persona persona) {
        MinecraftClient.getInstance().setScreen(new DebugScreen(persona));
    }
}
