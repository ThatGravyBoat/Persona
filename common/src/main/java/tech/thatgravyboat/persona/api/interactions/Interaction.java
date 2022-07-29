package tech.thatgravyboat.persona.api.interactions;

import net.minecraft.server.network.ServerPlayerEntity;
import tech.thatgravyboat.persona.api.interactions.types.base.InteractionSerializer;
import tech.thatgravyboat.persona.common.entity.Persona;

import java.util.List;

public interface Interaction<T extends Interaction<T>> {

    static void checkLists(List<Interaction<?>> interactions) {
        boolean canBeUsedInList = false;
        for (Interaction<?> interaction : interactions) {
            if (!interaction.type().canBeInList) {
                if (!canBeUsedInList) canBeUsedInList = true;
                else {
                    throw new IllegalArgumentException("Type of '"+interaction.type().name()+"' can not be used in a list when other types that can not be used in a list are used in the same list.");
                }
            }
        }
    }

    InteractionType type();

    void activate(Persona persona, ServerPlayerEntity player);

    InteractionSerializer<T> serializer();
}
