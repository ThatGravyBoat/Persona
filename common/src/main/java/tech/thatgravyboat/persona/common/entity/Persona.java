package tech.thatgravyboat.persona.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.thatgravyboat.persona.api.Features;
import tech.thatgravyboat.persona.api.NpcData;
import tech.thatgravyboat.persona.api.appearance.Appearance;
import tech.thatgravyboat.persona.api.appearance.Appearances;
import tech.thatgravyboat.persona.api.interactions.Interaction;
import tech.thatgravyboat.persona.common.management.IPersonaHolder;
import tech.thatgravyboat.persona.common.management.PersonaManager;

public class Persona extends Entity implements Merchant {

    private static final EntityDataAccessor<CompoundTag> FEATURES = SynchedEntityData.defineId(Persona.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<CompoundTag> APPEARANCE = SynchedEntityData.defineId(Persona.class, EntityDataSerializers.COMPOUND_TAG);

    private String id;

    @Nullable
    private Features features;
    @Nullable
    private Appearance<?> appearance;
    @Nullable
    private Interaction<?> interaction;

    @Nullable
    private Entity clientEntity;

    private MerchantOffers offers = new MerchantOffers();

    private final boolean sendData;

    public Persona(EntityType<?> type, Level world, boolean sendData) {
        super(type, world);
        this.sendData = sendData;
    }

    public Persona(EntityType<?> type, Level world) {
        this(type, world, true);
    }

    public boolean isInGui() {
        return !this.sendData;
    }

    @Override
    protected void defineSynchedData() {
        getEntityData().define(FEATURES, new CompoundTag());
        getEntityData().define(APPEARANCE, new CompoundTag());
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);
        if (FEATURES.equals(data)) {
            features = Features.fromNbt(getEntityData().get(FEATURES));
            this.refreshDimensions();
        }
        if (APPEARANCE.equals(data)) {
            appearance = Appearances.fromNbt(getEntityData().get(APPEARANCE));
        }
    }

    public void setPersona(NpcData data) {
        this.id = data.id();
        setFeatures(data.features());
        setAppearance(data.appearance());
        this.interaction = data.interaction();
        setCustomName(Component.literal(data.displayName()));
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return features != null ? features.dimensions() : super.getDimensions(pose);
    }

    public void setFeatures(Features features) {
        this.features = features;
        if (sendData) getEntityData().set(FEATURES, Features.toNbt(this.features));
    }

    public void setAppearance(Appearance<?> appearance) {
        this.appearance = appearance;
        if (sendData) getEntityData().set(APPEARANCE, Appearances.toNbt(this.appearance));
    }

    public @Nullable Features getFeatures() {
        return this.features;
    }

    public @Nullable Appearance<?> getAppearance() {
        return this.appearance;
    }

    public Entity getClientEntity(Level world, EntityType<?> type) {
        if (clientEntity == null || !clientEntity.getType().equals(type)) {
            clientEntity = type.create(world);
        }
        return clientEntity;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (hand.equals(InteractionHand.MAIN_HAND)) {
            if (player.getItemInHand(hand).getItem() instanceof PersonaSpawnEgg spawnEgg) {
                spawnEgg.useOnEntity(this);
            } else {
                if (player.level.isClientSide) {
                    setTradingPlayer(player);
                }
                if (interaction != null && player instanceof ServerPlayer serverPlayer) {
                    interaction.activate(this, serverPlayer);
                }
            }
        }
        return super.interact(player, hand);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.id = tag.getString("npcId");
        if (this.level.getServer() instanceof IPersonaHolder holder) {
            PersonaManager manager = holder.getPersonaManager();
            if (manager != null && manager.isAlreadyAnNpc(this.id)) {
                setPersona(manager.getNpc(this.id));
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putString("npcId", this.id);
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    public String npcId() {
        return id;
    }

    //region Trading

    private Player customer;

    @Override
    public void setTradingPlayer(@Nullable Player customer) {
        this.customer = customer;
    }

    @Nullable
    @Override
    public Player getTradingPlayer() { return customer; }

    @Override
    public MerchantOffers getOffers() {
        return offers;
    }

    @Override
    public void overrideOffers(MerchantOffers offers) {
        this.offers = offers;
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        offer.increaseUses();
    }

    @Override
    public void notifyTradeUpdated(ItemStack stack) {}

    @Override
    public int getVillagerXp() {
        return 0;
    }

    @Override
    public void overrideXp(int experience) {}

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.GENERIC_SMALL_FALL;
    }

    @Override
    public boolean isClientSide() {
        return this.level.isClientSide;
    }
    //endregion
}
