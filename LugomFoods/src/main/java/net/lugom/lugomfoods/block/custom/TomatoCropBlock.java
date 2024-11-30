package net.lugom.lugomfoods.block.custom;

import net.lugom.lugomfoods.entity.ModEntities;
import net.lugom.lugomfoods.entity.custom.TomatoDudeEntity;
import net.lugom.lugomfoods.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;

public class TomatoCropBlock extends CropBlock {
    private final Random random = Random.create();
    public static final int MAX_AGE = 6;
    public static final IntProperty AGE = IntProperty.of("age", 0, MAX_AGE);
    protected static final VoxelShape AGE_TO_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    public TomatoCropBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        return ModItems.TOMATO_SEEDS;
    }

    @Override
    protected int getGrowthAmount(World world) {
        return MathHelper.nextInt(world.random, 1, 3);
    }

    @Override
    public IntProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(ModItems.TOMATO_SEEDS);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return AGE_TO_SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public boolean shouldDropItemsOnExplosion(Explosion explosion) {
        return false;
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        if (!world.isClient) {
            boolean chance = (float) random.nextBetween(0, 100) / 100 < 0.05F;
            if (this.isMature(state) && chance && isTomatoCropNearbyMaxAge(world, pos)) {
                TomatoDudeEntity tomatoDudeEntity = ModEntities.TOMATO_DUDE.create(world);
                if (tomatoDudeEntity != null) {
                    tomatoDudeEntity.refreshPositionAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0, 0.0F);
                    world.spawnEntity(tomatoDudeEntity);
                    changeCropBlockState(state, world, pos, tomatoDudeEntity);
                }
            }
        }
    }

    private void changeCropBlockState(BlockState state, World world, BlockPos pos, Entity sourceEntity) {
        world.playSound(null, pos, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
        BlockState blockState = state.with(AGE, Integer.valueOf(3));
        world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(sourceEntity, blockState));
    }

    private boolean isTomatoCropNearbyMaxAge(WorldView world, BlockPos pos) {
        for (BlockPos blockPos : BlockPos.iterate(pos.add(-1, 0, -1), pos.add(1, 0, 1))) {
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.getBlock() instanceof TomatoCropBlock tomatoCrop && blockPos.compareTo(pos) != 0) {
                if(tomatoCrop.isMature(blockState)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if(state.get(AGE) == MAX_AGE)
        {
            float chance = (float) random.nextBetween(1, 100) / 100;
            if(chance < 0.05f) {
                dropStack(world, pos, new ItemStack(ModItems.TOMATO_GOLDEN, 1));
            }
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(AGE) < MAX_AGE-1) {
            return super.onUse(state, world, pos, player, hand, hit);
        }
        boolean isGreen = state.get(AGE) == MAX_AGE-1;
        if (player.getStackInHand(hand).isOf(Items.SHEARS) && !isGreen) {
            float chance = (float) world.random.nextBetween(1, 100) / 100;
            if(chance < 0.05f) {
                dropStack(world, pos, new ItemStack(ModItems.TOMATO_GOLDEN, 1));
            }
        }
        if(isGreen) {
            if(player.getStackInHand(hand).isOf(Items.BONE_MEAL)) {
                return super.onUse(state, world, pos, player, hand, hit);
            }
            dropStack(world, pos, new ItemStack(ModItems.TOMATO_GREEN, 1 + world.random.nextInt(3)));
        }
        else {
            dropStack(world, pos, new ItemStack(ModItems.TOMATO, 1 + world.random.nextInt(3)));
        }
        changeCropBlockState(state, world, pos, player);
        return ActionResult.success(world.isClient);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return state.get(AGE) == MAX_AGE;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(AGE) == MAX_AGE ? 1 : 0;
    }
}
