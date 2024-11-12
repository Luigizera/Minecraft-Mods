package net.lugom.lugomfoods.entity.custom;

import net.lugom.lugomfoods.entity.ModEntities;
import net.lugom.lugomfoods.item.ModItems;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TomatoDudeEntity extends TameableEntity implements RangedAttackMob {

    private static final TrackedData<Boolean> SITTING =
            DataTracker.registerData(TomatoDudeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationCooldown = 0;
    public final AnimationState sittingAnimationState = new AnimationState();
    private int sittingAnimationCooldown = 0;

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
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, MAX_HEALTH)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(EntityAttributes.GENERIC_ARMOR, ARMOR)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, ATTACK_DAMAGE);
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

    private void setupAnimationStates() {
        if (this.idleAnimationCooldown <= 0 && !this.isSitting()) {
            this.idleAnimationCooldown = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
        }
        else {
            this.idleAnimationCooldown--;
        }

        if(this.isSitting() && sittingAnimationCooldown == 0) {
            sittingAnimationCooldown = 40;
            this.sittingAnimationState.start(this.age);
        }

        if(!this.isSitting()) {
            sittingAnimationCooldown = 0;
            sittingAnimationState.stop();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) {
            setupAnimationStates();
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
        Item item = itemStack.getItem();

        Item itemForTaming = ModItems.TOMATO_GOLDEN;

        if(item == itemForTaming && !isTamed()) {
            if(this.getWorld().isClient()) {
                return ActionResult.CONSUME;
            }
            else {
                if(!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }
                if(!this.getWorld().isClient()) {
                    super.setOwner(player);
                    this.navigation.recalculatePath();
                    this.setTarget(null);
                    this.getWorld().sendEntityStatus(this, (byte)7);
                    setSitting(true);
                }
                return ActionResult.SUCCESS;
            }
        }

        if(isTamed() && !this.getWorld().isClient() && hand == Hand.MAIN_HAND) {
            setSitting(!isSitting());
            return ActionResult.SUCCESS;
        }

        if(itemStack.getItem() == itemForTaming) {
            return ActionResult.PASS;
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

    @Override
    public void setTamed(boolean tamed) {
        super.setTamed(tamed);
        if(tamed) {
            getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(MAX_HEALTH*2);
            getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(MOVEMENT_SPEED*2);
            getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(ARMOR*2);
            getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(ATTACK_DAMAGE*2);
        }
        else {
            getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(MAX_HEALTH);
            getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(MOVEMENT_SPEED);
            getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(ARMOR);
            getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(ATTACK_DAMAGE);

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
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        this.getWorld().spawnEntity(new ItemEntity(this.getWorld(), this.getX(), this.getY(), this.getZ(), new ItemStack(ModItems.TOMATO_SEEDS, random.nextBetween(1, 4))));
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
        return SoundEvents.ENTITY_STRAY_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_STRAY_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_STRAY_DEATH;
    }
}
