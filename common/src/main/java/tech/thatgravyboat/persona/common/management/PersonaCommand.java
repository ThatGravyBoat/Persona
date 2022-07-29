package tech.thatgravyboat.persona.common.management;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class PersonaCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("personas")
                .requires(cs -> cs.hasPermissionLevel(2))
                .then(CommandManager.literal("list")
                        .executes(source -> {
                            if (source.getSource().getServer() instanceof IPersonaHolder holder && holder.getPersonaManager() != null) {
                                LiteralText text = new LiteralText("Npcs: ");
                                for (String npc : holder.getPersonaManager().npcIds()) {
                                    text.append("\n - " + npc);
                                }
                                source.getSource().sendFeedback(text, false);
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("npc", StringArgumentType.word()).executes(source -> {
                            if (source.getSource().getServer() instanceof IPersonaHolder holder && holder.getPersonaManager() != null) {
                                String npc = StringArgumentType.getString(source, "npc");
                                if (holder.getPersonaManager().deleteNpc(npc)) {
                                    source.getSource().sendFeedback(new LiteralText("Removed npc."), false);
                                } else {
                                    source.getSource().sendFeedback(new LiteralText("Could not remove npc."), false);
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                .then(CommandManager.literal("refresh")
                        .executes(source -> {
                            if (source.getSource().getServer() instanceof IPersonaHolder holder && holder.getPersonaManager() != null) {
                                holder.getPersonaManager().updateData();
                                source.getSource().sendFeedback(new LiteralText("Data refreshed."), false);
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );
    }
}
