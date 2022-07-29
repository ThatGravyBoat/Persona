package tech.thatgravyboat.persona.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import tech.thatgravyboat.persona.api.Features;
import tech.thatgravyboat.persona.api.NpcData;
import tech.thatgravyboat.persona.api.appearance.Appearance;
import tech.thatgravyboat.persona.api.appearance.Appearances;
import tech.thatgravyboat.persona.api.interactions.Interaction;
import tech.thatgravyboat.persona.common.management.IPersonaHolder;
import tech.thatgravyboat.persona.common.management.PersonaManager;

public class Persona extends Entity implements Merchant {

    private static final TrackedData<NbtCompound> FEATURES = DataTracker.registerData(Persona.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
    private static final TrackedData<NbtCompound> APPEARANCE = DataTracker.registerData(Persona.class, TrackedDataHandlerRegistry.NBT_COMPOUND);

    private String id;

    @Nullable
    private Features features;
    @Nullable
    private Appearance<?> appearance;
    @Nullable
    private Interaction<?> interaction;

    @Nullable
    private Entity clientEntity;

    private TradeOfferList offers = new TradeOfferList();

    private final boolean sendData;

    public Persona(EntityType<?> type, World world, boolean sendData) {
        super(type, world);
        this.sendData = sendData;
    }

    public Persona(EntityType<?> type, World world) {
        this(type, world, true);
    }

    public boolean isInGui() {
        return !this.sendData;
    }

    @Override
    protected void initDataTracker() {
        getDataTracker().startTracking(FEATURES, new NbtCompound());
        getDataTracker().startTracking(APPEARANCE, new NbtCompound());
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (FEATURES.equals(data)) {
            features = Features.fromNbt(getDataTracker().get(FEATURES));
            this.calculateDimensions();
        }
        if (APPEARANCE.equals(data)) {
            appearance = Appearances.fromNbt(getDataTracker().get(APPEARANCE));
        }
    }

    public void setPersona(NpcData data) {
        this.id = data.id();
        setFeatures(data.features());
        setAppearance(data.appearance());
        this.interaction = data.interaction();
        setCustomName(new LiteralText(data.displayName()));
        this.calculateDimensions();
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return features != null ? features.dimensions() : super.getDimensions(pose);
    }

    public void setFeatures(Features features) {
        this.features = features;
        if (sendData) getDataTracker().set(FEATURES, Features.toNbt(this.features));
    }

    public void setAppearance(Appearance<?> appearance) {
        this.appearance = appearance;
        if (sendData) getDataTracker().set(APPEARANCE, Appearances.toNbt(this.appearance));
    }

    public @Nullable Features getFeatures() {
        return this.features;
    }

    public @Nullable Appearance<?> getAppearance() {
        return this.appearance;
    }

    public Entity getClientEntity(World world, EntityType<?> type) {
        if (clientEntity == null || !clientEntity.getType().equals(type)) {
            clientEntity = type.create(world);
        }
        return clientEntity;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (hand.equals(Hand.MAIN_HAND)) {
            if (player.getStackInHand(hand).getItem() instanceof PersonaSpawnEgg spawnEgg) {
                spawnEgg.useOnEntity(this);
            } else {
                if (player.world.isClient) {
                    setCustomer(player);
                }
                if (interaction != null && player instanceof ServerPlayerEntity serverPlayer) {
                    interaction.activate(this, serverPlayer);
                }
            }
        }
        return super.interact(player, hand);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.id = nbt.getString("npcId");
        if (this.world.getServer() instanceof IPersonaHolder holder) {
            PersonaManager manager = holder.getPersonaManager();
            if (manager != null && manager.isAlreadyAnNpc(this.id)) {
                setPersona(manager.getNpc(this.id));
            }
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString("npcId", this.id);
    }

    @Override
    public boolean collides() {
        return !this.isRemoved();
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    public String npcId() {
        return id;
    }

    //region Trading

    private PlayerEntity customer;

    @Override
    public void setCustomer(@Nullable PlayerEntity customer) {
        this.customer = customer;
    }

    @Nullable
    @Override
    public PlayerEntity getCustomer() { return customer; }

    @Override
    public TradeOfferList getOffers() {
        return offers;
    }

    @Override
    public void setOffersFromServer(TradeOfferList offers) {
        this.offers = offers;
    }

    @Override
    public void trade(TradeOffer offer) {
        offer.use();
    }

    @Override
    public void onSellingItem(ItemStack stack) {}

    @Override
    public int getExperience() {
        return 0;
    }

    @Override
    public void setExperienceFromServer(int experience) {}

    @Override
    public boolean isLeveledMerchant() {
        return false;
    }

    @Override
    public SoundEvent getYesSound() {
        return SoundEvents.ENTITY_GENERIC_SMALL_FALL;
    }

    @Override
    public boolean isClient() {
        return this.world.isClient();
    }
    //endregion
}
