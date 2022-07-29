package tech.thatgravyboat.persona.common.registry.forge;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.common.entity.Persona;

import java.util.function.Supplier;

public class RegistryImpl {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPE = DeferredRegister.create(ForgeRegistries.ENTITIES, Personas.MOD_ID);
    public static final DeferredRegister<Item> ITEM = DeferredRegister.create(ForgeRegistries.ITEMS, Personas.MOD_ID);

    public static <T extends Entity> Supplier<EntityType<T>> registerEntity(String id, EntityType.EntityFactory<T> factory, float width, float height, SpawnGroup group) {
        return ENTITY_TYPE.register(id, () -> EntityType.Builder.create(factory, group).setDimensions(width, height).build(id));
    }

    public static <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        return ITEM.register(id, item);
    }

}
