package com.rekindled.embers.block;

import javax.annotation.Nullable;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.block.IPipeConnection;
import com.rekindled.embers.blockentity.ItemVacuumBlockEntity;
import com.rekindled.embers.blockentity.PipeBlockEntityBase.PipeConnection;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ItemVacuumBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, IPipeConnection {

	protected static final VoxelShape UP_AABB = Shapes.or(box(4,0,4,12,4,12), box(2,4,2,14,12,14), box(0,12,0,16,16,16));
	protected static final VoxelShape DOWN_AABB = Shapes.or(box(4,4,4,12,16,12), box(2,4,2,14,12,14), box(0,0,0,16,4,16));
	protected static final VoxelShape NORTH_AABB = Shapes.or(box(4,4,4,12,12,16), box(2,2,4,14,14,12), box(0,0,0,16,16,4));
	protected static final VoxelShape SOUTH_AABB = Shapes.or(box(4,4,0,12,12,4), box(2,2,4,14,14,12), box(0,0,12,16,16,16));
	protected static final VoxelShape WEST_AABB = Shapes.or(box(4,4,4,16,12,12), box(4,2,2,12,14,14), box(0,0,0,4,16,16));
	protected static final VoxelShape EAST_AABB = Shapes.or(box(0,4,4,4,12,12), box(4,2,2,12,14,14), box(12,0,0,16,16,16));

	public ItemVacuumBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.FACING, Direction.UP).setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		switch (pState.getValue(BlockStateProperties.FACING)) {
		case UP:
			return UP_AABB;
		case DOWN:
			return DOWN_AABB;
		case EAST:
			return EAST_AABB;
		case WEST:
			return WEST_AABB;
		case SOUTH:
			return SOUTH_AABB;
		case NORTH:
		default:
			return NORTH_AABB;
		}
	}

	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction facing = context.getPlayer().isSecondaryUseActive() ? context.getNearestLookingDirection() : context.getNearestLookingDirection().getOpposite();
		return this.defaultBlockState().setValue(BlockStateProperties.FACING, facing).setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER));
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
		if (pState.getValue(BlockStateProperties.WATERLOGGED)) {
			pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
		}
		return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
	}

	@Override
	public BlockState rotate(BlockState pState, Rotation pRot) {
		return pState.setValue(BlockStateProperties.FACING, pRot.rotate(pState.getValue(BlockStateProperties.FACING)));
	}

	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState.rotate(pMirror.getRotation(pState.getValue(BlockStateProperties.FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.FACING).add(BlockStateProperties.WATERLOGGED);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.ITEM_VACUUM_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return createTickerHelper(pBlockEntityType, RegistryManager.ITEM_VACUUM_ENTITY.get(), ItemVacuumBlockEntity::tick);
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}

	@Override
	public PipeConnection getPipeConnection(BlockState state, Direction direction) {
		return PipeConnection.PIPE;
	}
}
