package tech.thatgravyboat.persona.common.registry.fabric;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.common.entity.Persona;

import java.util.function.Supplier;

public class RegistryImpl {


    public static <T extends Entity> Supplier<EntityType<T>> registerEntity(String id, EntityType.EntityFactory<T> factory, float width, float height, SpawnGroup group) {
        EntityType<T> entityType = EntityType.Builder.create(factory, group).setDimensions(width, height).build(id);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(Personas.MOD_ID, id), entityType);
        return () -> entityType;
    }

    public static <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        T register = Registry.register(Registry.ITEM, new Identifier(Personas.MOD_ID, id), item.get());
        return () -> register;
    }
}
