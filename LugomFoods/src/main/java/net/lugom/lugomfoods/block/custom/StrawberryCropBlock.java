package net.lugom.lugomfoods.block.custom;

import net.lugom.lugomfoods.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
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
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;

public class StrawberryCropBlock extends CropBlock {
    private final Random random = Random.create();
    public static final int MAX_AGE = 5;
    public static final IntProperty AGE = Properties.AGE_5;
    public static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
    };
    public StrawberryCropBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        return ModItems.STRAWBERRY_SEEDS;
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
        return new ItemStack(ModItems.STRAWBERRY_SEEDS);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return AGE_TO_SHAPE[this.getAge(state)];
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
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if(state.get(AGE) == MAX_AGE)
        {
            float chance = (float) random.nextBetween(1, 100) / 100;
            if(chance < 0.05f) {
                dropStack(world, pos, new ItemStack(ModItems.STRAWBERRY_GOLDEN, 1));
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
                dropStack(world, pos, new ItemStack(ModItems.STRAWBERRY_GOLDEN, 1));
            }
        }
        if(isGreen) {
            if(player.getStackInHand(hand).isOf(Items.BONE_MEAL)) {
                return super.onUse(state, world, pos, player, hand, hit);
            }
            dropStack(world, pos, new ItemStack(ModItems.STRAWBERRY_GREEN, 1 + world.random.nextInt(3)));
        }
        else {
            dropStack(world, pos, new ItemStack(ModItems.STRAWBERRY, 1 + world.random.nextInt(3)));
        }
        world.playSound(null, pos, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
        BlockState blockState = state.with(AGE, Integer.valueOf(3));
        world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, blockState));
        return ActionResult.success(world.isClient);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return state.get(AGE) == MAX_AGE;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if(state.get(AGE) == MAX_AGE) {
            return 1;
        }
        return 0;
    }
}
