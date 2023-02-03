package tech.thatgravyboat.persona.common.registry.fabric;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import tech.thatgravyboat.persona.Personas;

import java.util.function.Supplier;

public class RegistryImpl {


    public static <T extends Entity> Supplier<EntityType<T>> registerEntity(String id, EntityType.EntityFactory<T> factory, float width, float height, MobCategory group) {
        EntityType<T> entityType = EntityType.Builder.of(factory, group).sized(width, height).build(id);
        Registry.register(Registry.ENTITY_TYPE, new ResourceLocation(Personas.MOD_ID, id), entityType);
        return () -> entityType;
    }

    public static <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        T register = Registry.register(Registry.ITEM, new ResourceLocation(Personas.MOD_ID, id), item.get());
        return () -> register;
    }
}
