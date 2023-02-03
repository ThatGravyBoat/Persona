package tech.thatgravyboat.persona.common.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import tech.thatgravyboat.persona.common.entity.Persona;
import tech.thatgravyboat.persona.common.entity.PersonaSpawnEgg;

import java.util.function.Supplier;

public class Registry {

    public static final Supplier<EntityType<Persona>> PERSONA = registerEntity("persona", Persona::new, 1f, 2f, MobCategory.MISC);
    public static final Supplier<PersonaSpawnEgg> PERSONA_TOTEM = registerItem("persona_totem", () -> new PersonaSpawnEgg(new Item.Properties()));

    public static void register() {
        //Init class
    }

    @ExpectPlatform
    public static <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Entity> Supplier<EntityType<T>> registerEntity(String id, EntityType.EntityFactory<T> factory, float width, float height, MobCategory group) {
        throw new AssertionError();
    }
}
