package net.lugom.lugomfoods.entity.custom;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.lugom.lugomfoods.entity.ModEntities;
import net.lugom.lugomfoods.item.ModItems;
import net.lugom.lugomfoods.util.ImplementedInventory;
import net.lugom.lugomfoods.screen.custom.TomatoScreenHandler;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TomatoDudeEntity extends TameableEntity implements RangedAttackMob, ExtendedScreenHandlerFactory, ImplementedInventory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(6, ItemStack.EMPTY);

    private static final TrackedData<Boolean> SITTING =
            DataTracker.registerData(TomatoDudeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_CHEST_OPEN =
            DataTracker.registerData(TomatoDudeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> CHEST =
            DataTracker.registerData(TomatoDudeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);


    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationCooldown = 0;
    public final AnimationState chestOpenAnimationState = new AnimationState();
    public final AnimationState chestCloseAnimationState = new AnimationState();
    public final AnimationState sittingAnimationState = new AnimationState();


    private static final double MAX_HEALTH = 15.0D;
    private static final double MOVEMENT_SPEED = 0.3D;
    private static final double ARMOR = 0.5D;
    private static final double ATTACK_DAMAGE = 1.0D;


    public TomatoDudeEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new ProjectileAttackGoal(this, 1.25, 2*20, 10.0F));
        this.goalSelector.add(1, new SitGoal(this));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.15D));
        this.goalSelector.add(3, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false));
        this.goalSelector.add(4, new TemptGoal(this, 1.25D, Ingredient.ofItems(ModItems.TOMATO_GOLDEN), false));
        this.goalSelector.add(5, new FollowParentGoal(this, 1.15D));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 4f));
        this.goalSelector.add(10, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new RevengeGoal(this));

    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SITTING, false);
        this.dataTracker.startTracking(CHEST, false);
        this.dataTracker.startTracking(IS_CHEST_OPEN, false);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, MAX_HEALTH)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(EntityAttributes.GENERIC_ARMOR, ARMOR)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, ATTACK_DAMAGE);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        else {
            Entity entity = source.getAttacker();
            if (!this.getWorld().isClient) {
                this.setSitting(false);
            }
            if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof PersistentProjectileEntity)) {
                amount = (amount + 1.0F) / 2.0F;
            }
        }
        return super.damage(source, amount);
    }

    @Override
    public void attack(LivingEntity target, float pullProgress) {
        if(this.canTarget(target) && !isSitting()) {
            TomatoThrowableEntity tomatoThrowable = new TomatoThrowableEntity(this, this.getWorld());
            double d = target.getEyeY() - 1.1F;
            double e = target.getX() - this.getX();
            double f = d - tomatoThrowable.getY();
            double g = target.getZ() - this.getZ();
            double h = Math.sqrt(e * e + g * g) * 0.2F;
            tomatoThrowable.setVelocity(e, f + h, g, 1.6F, 12.0F);
            this.playSound(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            this.getWorld().spawnEntity(tomatoThrowable);
        }
        else {
            this.setTarget(null);
        }
    }

    private void idleAnimation() {
        if (this.idleAnimationCooldown <= 0 && !this.isSitting()) {
            this.idleAnimationCooldown = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
        }
        else {
            this.idleAnimationCooldown--;
        }
    }

    private void sittingAnimation() {
        if(this.isSitting()) {
            this.sittingAnimationState.start(this.age);
        }
        else {
            sittingAnimationState.stop();
        }
    }

    private void chestAnimation() {
        if (this.isChestOpen()) {
            this.chestOpenAnimationState.start(this.age);
        }
        else {
            this.chestOpenAnimationState.stop();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) {
            this.idleAnimation();
            this.sittingAnimation();
            this.chestAnimation();
        }
    }

    @Override
    protected void updateLimbs(float posDelta) {
        float f;
        if (this.getPose() == EntityPose.STANDING) {
            f = Math.min(posDelta * 6.0F, 1.0F);
        } else {
            f = 0.0F;
        }

        this.limbAnimator.updateLimbs(f, 0.2F);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (this.getWorld().isClient) {
            boolean bl = this.isOwner(player) || this.isTamed() || itemStack.isOf(Items.BONE_MEAL) && !this.isTamed();
            return bl ? ActionResult.CONSUME : ActionResult.PASS;
        }
        if (this.isTamed()) {
            if(this.isOwner(player) && (player.isInPose(EntityPose.CROUCHING) || player.isInPose(EntityPose.FALL_FLYING))) {
                if(itemStack.isOf(Blocks.CHEST.asItem()) && !this.hasChest() && this.canHaveChest()) {
                    this.setHasChest(true);
                    if(!player.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                    }
                    this.playSound(SoundEvents.ENTITY_DONKEY_CHEST, 1f, 1f);
                    return ActionResult.SUCCESS;
                }

                if(this.hasChest()) {
                    if(itemStack.isOf(Items.SHEARS)) {
                        this.dropInventory();
                        this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1f, 1f);
                        return ActionResult.SUCCESS;
                    }
                }
                player.openHandledScreen(this);
                return ActionResult.SUCCESS;
            }
            if (this.isBreedingItem(itemStack) && this.getHealth() < this.getMaxHealth()) {
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }

                this.heal(1F);
                if(this.getHealth() >= this.getMaxHealth()) {
                    this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
                }
                return ActionResult.SUCCESS;
            }
            else {
                ActionResult actionResult = super.interactMob(player, hand);
                if ((!actionResult.isAccepted() || this.isBaby()) && this.isOwner(player)) {
                    this.setSitting(!this.isSitting());
                    this.jumping = false;
                    this.navigation.stop();
                    this.setTarget(null);
                    return ActionResult.SUCCESS;
                }
                else {
                    return actionResult;
                }
            }
        }
        if (itemStack.isOf(Items.BONE_MEAL)) {
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }

            if (this.random.nextInt(3) == 0) {
                this.setOwner(player);
                this.navigation.stop();
                this.setTarget(null);
                this.setSitting(true);
                this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
            }
            else {
                this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_NEGATIVE_PLAYER_REACTION_PARTICLES);
            }

            return ActionResult.SUCCESS;
        }
        return super.interactMob(player, hand);
    }

    @Override
    public void setSitting(boolean sitting) {
        this.dataTracker.set(SITTING, sitting);
    }

    @Override
    public boolean isSitting() {
        return this.dataTracker.get(SITTING);
    }

    public void setHasChest(boolean chest) {
        this.dataTracker.set(CHEST, chest);
    }

    public boolean hasChest() {
        return this.dataTracker.get(CHEST);
    }

    public void setChestOpen(boolean is_chest_open) {
        this.dataTracker.set(IS_CHEST_OPEN, is_chest_open);
    }

    public boolean isChestOpen() {
        return this.dataTracker.get(IS_CHEST_OPEN);
    }

    public boolean canHaveChest() {
        return this.isAlive() && !this.isBaby() && this.isTamed();
    }

    public int getInventoryColumns() {
        return 3;
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        if (target instanceof Tameable tameable) {
            return tameable.getOwner() != this.getOwner() && super.canTarget(target);
        }
        return !this.isOwner(target) && super.canTarget(target);
    }

    @Override
    protected void onTamedChanged() {
        if(this.isTamed()) {
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(MAX_HEALTH*2);
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(MOVEMENT_SPEED*2);
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(ARMOR*2);
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(ATTACK_DAMAGE*2);
            this.setHealth((float)MAX_HEALTH*2);
        }
        else {
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(MAX_HEALTH);
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(MOVEMENT_SPEED);
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(ARMOR);
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(ATTACK_DAMAGE);
        }
    }

    @Override
    public EntityView method_48926() {
        return super.getWorld();
    }

    @Override
    public @Nullable LivingEntity getOwner() {
        return super.getOwner();
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.hasChest()) {
            if (!this.getWorld().isClient) {
                this.dropItem(Blocks.CHEST);
                for (ItemStack itemStack : inventory) {
                    dropStack(itemStack);
                }
                inventory.clear();
            }

            this.setHasChest(false);
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        this.dropInventory();
        this.getWorld().spawnEntity(new ItemEntity(this.getWorld(), this.getX(), this.getY(), this.getZ(), new ItemStack(ModItems.TOMATO_SEEDS, random.nextBetween(1, 4))));
        super.onDeath(damageSource);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(Items.BONE_MEAL);
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        TomatoDudeEntity tomatoDudeEntity = ModEntities.TOMATO_DUDE.create(world);
        if (tomatoDudeEntity != null) {
            UUID uUID = this.getOwnerUuid();
            if (uUID != null) {
                tomatoDudeEntity.setOwnerUuid(uUID);
                tomatoDudeEntity.setTamed(true);
            }
        }

        return tomatoDudeEntity;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_CAT_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_CAT_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_CAT_DEATH;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new TomatoScreenHandler(syncId, playerInventory, this, this);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        ImplementedInventory.super.onOpen(player);
        this.setChestOpen(true);
        this.playSound(SoundEvents.BLOCK_CHEST_OPEN, 0.5f, 1f);
    }

    @Override
    public void onClose(PlayerEntity player) {
        ImplementedInventory.super.onClose(player);
        this.setChestOpen(false);
        this.playSound(SoundEvents.BLOCK_CHEST_CLOSE, 0.5f, 1f);
    }

    @Override
    public Text getDisplayName() {
        if(!this.hasCustomName()) {
            return Text.translatable("entity.lugomfoods.tomato_dude");
        }
        return super.getDisplayName();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        super.readCustomDataFromNbt(nbt);
        this.setHasChest(nbt.getBoolean("ChestedTomatoDude"));
        if(this.hasChest()) {
            Inventories.readNbt(nbt, this.inventory);
        }
        this.setSitting(nbt.getBoolean("Sitting"));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("ChestedTomatoDude", this.hasChest());
        if(this.hasChest()) {
            Inventories.writeNbt(nbt, this.inventory);
        }
        nbt.putBoolean("Sitting", this.isSitting());
        return nbt;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        int id = this.getId();
        packetByteBuf.writeInt(id);
        packetByteBuf.writeInt(id);
    }
}
