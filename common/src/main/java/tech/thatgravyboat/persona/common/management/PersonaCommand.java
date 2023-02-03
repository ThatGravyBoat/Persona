package tech.thatgravyboat.persona.common.management;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class PersonaCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("personas")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.literal("list")
                        .executes(source -> {
                            if (source.getSource().getServer() instanceof IPersonaHolder holder && holder.getPersonaManager() != null) {
                                MutableComponent text = Component.literal("Npcs: ");
                                for (String npc : holder.getPersonaManager().npcIds()) {
                                    text.append("\n - " + npc);
                                }
                                source.getSource().sendSuccess(text, false);
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("remove")
                        .then(Commands.argument("npc", StringArgumentType.word()).executes(source -> {
                            if (source.getSource().getServer() instanceof IPersonaHolder holder && holder.getPersonaManager() != null) {
                                String npc = StringArgumentType.getString(source, "npc");
                                if (holder.getPersonaManager().deleteNpc(npc)) {
                                    source.getSource().sendSuccess(Component.literal("Removed npc."), false);
                                } else {
                                    source.getSource().sendSuccess(Component.literal("Could not remove npc."), false);
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                .then(Commands.literal("refresh")
                        .executes(source -> {
                            if (source.getSource().getServer() instanceof IPersonaHolder holder && holder.getPersonaManager() != null) {
                                holder.getPersonaManager().updateData();
                                source.getSource().sendSuccess(Component.literal("Data refreshed."), false);
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );
    }
}
