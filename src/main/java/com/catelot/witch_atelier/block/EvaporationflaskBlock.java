package com.catelot.witch_atelier.block;

import com.catelot.witch_atelier.block.entity.EvaporationFlaskBlockEntity;
import com.catelot.witch_atelier.registries.WitchAtelierModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class EvaporationflaskBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty BOILING = BooleanProperty.create("boiling");
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 6);
    private static final VoxelShape SHAPE = box(0, 0, 0, 15, 15, 15);

    public EvaporationflaskBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(BOILING, false).setValue(STAGE, 0).setValue(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return propagatesSkylightDown(state, world, pos) ? 0 : 1;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BOILING, STAGE, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean waterlogged = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return super.getStateForPlacement(context).setValue(BOILING, false).setValue(STAGE, 0).setValue(WATERLOGGED, waterlogged);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, world, pos, oldState, moving);
        world.scheduleTick(pos, this, 20);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(state, world, pos, random);
        updateEvaporation(world, pos, state);
        world.scheduleTick(pos, this, 20);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        super.useWithoutItem(state, world, pos, player, hit);

        if (!world.isClientSide()) {
            handleInteraction(world, pos, player);
        }

        return InteractionResult.SUCCESS;
    }

    private static void updateEvaporation(ServerLevel world, BlockPos pos, BlockState state) {
        boolean heated = isHeatSource(world.getBlockState(pos.below()));
        if (state.getValue(BOILING) != heated) {
            state = state.setValue(BOILING, heated);
            world.setBlock(pos, state, 3);
        }

        if (!heated) {
            return;
        }

        int stage = state.getValue(STAGE);
        if (stage == 3 && incrementTimer(world, pos, "cook_timer") >= 100) {
            world.setBlock(pos, state.setValue(STAGE, 4), 3);
            setTimer(world, pos, "cook_timer", 0);
        } else if (stage == 5 && incrementTimer(world, pos, "evap_timer") >= 100) {
            world.setBlock(pos, state.setValue(STAGE, 6), 3);
            setTimer(world, pos, "evap_timer", 0);
        }
    }

    private static boolean isHeatSource(BlockState state) {
        return state.is(Blocks.LAVA) || state.is(Blocks.FIRE) || state.is(Blocks.MAGMA_BLOCK) || state.is(Blocks.CAMPFIRE);
    }

    private static double incrementTimer(ServerLevel world, BlockPos pos, String key) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {
            return -1;
        }

        double value = blockEntity.getPersistentData().getDouble(key) + 1;
        blockEntity.getPersistentData().putDouble(key, value);
        BlockState state = world.getBlockState(pos);
        world.sendBlockUpdated(pos, state, state, 3);
        return value;
    }

    private static void setTimer(ServerLevel world, BlockPos pos, String key, double value) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {
            return;
        }

        blockEntity.getPersistentData().putDouble(key, value);
        BlockState state = world.getBlockState(pos);
        world.sendBlockUpdated(pos, state, state, 3);
    }

    private static void handleInteraction(Level world, BlockPos pos, Player player) {
        BlockState state = world.getBlockState(pos);
        int stage = state.getValue(STAGE);
        ItemStack heldItem = player.getMainHandItem();

        if (heldItem.is(Items.WATER_BUCKET) && stage == 0) {
            world.setBlock(pos, state.setValue(STAGE, 1), 3);
            consumeOne(player, Items.WATER_BUCKET);
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.BUCKET));
        } else if (heldItem.is(WitchAtelierModItems.SILVERWOOD_FIBER.get()) && stage == 0) {
            world.setBlock(pos, state.setValue(STAGE, 2), 3);
            consumeOne(player, WitchAtelierModItems.SILVERWOOD_FIBER.get());
        } else if (heldItem.is(WitchAtelierModItems.SILVERWOOD_FIBER.get()) && stage == 1) {
            world.setBlock(pos, state.setValue(STAGE, 3), 3);
            consumeOne(player, WitchAtelierModItems.SILVERWOOD_FIBER.get());
        } else if (heldItem.is(Items.WATER_BUCKET) && stage == 2) {
            world.setBlock(pos, state.setValue(STAGE, 3), 3);
            consumeOne(player, Items.WATER_BUCKET);
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.BUCKET));
        } else if (heldItem.isEmpty() && stage == 4) {
            world.setBlock(pos, state.setValue(STAGE, 5), 3);
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(WitchAtelierModItems.SILVERWOOD_TWIG.get()));
        } else if (heldItem.is(Items.GLASS_BOTTLE) && stage == 6) {
            world.setBlock(pos, state.setValue(STAGE, 0), 3);
            consumeOne(player, Items.GLASS_BOTTLE);
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(WitchAtelierModItems.MAGIC_INK.get()));
        } else if (heldItem.is(Items.LEATHER) && stage == 6) {
            consumeOne(player, Items.LEATHER);
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(WitchAtelierModItems.WOOD_BLOOD_STICK.get()));
        }
    }

    private static void consumeOne(Player player, Item item) {
        player.getInventory().clearOrCountMatchingItems(stack -> stack.is(item), 1, player.inventoryMenu.getCraftSlots());
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof MenuProvider menuProvider ? menuProvider : null;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EvaporationFlaskBlockEntity(pos, state);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventId, int eventParam) {
        super.triggerEvent(state, world, pos, eventId, eventParam);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventId, eventParam);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof EvaporationFlaskBlockEntity evaporationFlask) {
                Containers.dropContents(world, pos, evaporationFlask);
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof EvaporationFlaskBlockEntity evaporationFlask) {
            return AbstractContainerMenu.getRedstoneSignalFromContainer(evaporationFlask);
        }
        return 0;
    }
}
