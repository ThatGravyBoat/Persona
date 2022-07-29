package tech.thatgravyboat.persona.common.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import tech.thatgravyboat.persona.common.entity.Persona;
import tech.thatgravyboat.persona.common.entity.PersonaSpawnEgg;

import java.util.function.Supplier;

public class Registry {

    public static final Supplier<EntityType<Persona>> PERSONA = registerEntity("persona", Persona::new, 1f, 2f, SpawnGroup.MISC);
    public static final Supplier<PersonaSpawnEgg> PERSONA_TOTEM = registerItem("persona_totem", () -> new PersonaSpawnEgg(new Item.Settings()));

    public static void register() {
        //Init class
    }

    @ExpectPlatform
    public static <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Entity> Supplier<EntityType<T>> registerEntity(String id, EntityType.EntityFactory<T> factory, float width, float height, SpawnGroup group) {
        throw new AssertionError();
    }
}
