package com.rekindled.embers.blockentity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.NotNull;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.event.AlchemyResultEvent;
import com.rekindled.embers.api.event.AlchemyStartEvent;
import com.rekindled.embers.api.event.MachineRecipeEvent;
import com.rekindled.embers.api.misc.AlchemyResult;
import com.rekindled.embers.api.tile.IBin;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.api.tile.ISparkable;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.AlchemyCircleParticleOptions;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.particle.StarParticleOptions;
import com.rekindled.embers.recipe.AlchemyContext;
import com.rekindled.embers.recipe.IAlchemyRecipe;
import com.rekindled.embers.recipe.IAlchemyRecipe.PedestalContents;
import com.rekindled.embers.util.Misc;
import com.rekindled.embers.util.sound.ISoundController;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class AlchemyTabletBlockEntity extends BlockEntity implements ISparkable, ISoundController, IExtraCapabilityInformation {

	public static final Direction[] UPGRADE_SIDES = {
			Direction.NORTH,
			Direction.SOUTH,
			Direction.WEST,
			Direction.EAST,
			Direction.DOWN
	};
	public static final int CONSUME_AMOUNT = 2;
	public static final int SPARK_THRESHOLD = 1000;
	public static final int PROCESSING_TIME = 40;

	public TabletItemStackHandler inventory = new TabletItemStackHandler(1, this);
	public IItemHandler outputHandler = new IItemHandler() {
		@Override
		public int getSlots() {
			return inventory.getSlots();
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			return inventory.getStackInSlot(slot);
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			return inventory.insertItem(slot,stack,simulate);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (AlchemyTabletBlockEntity.this.outputMode)
				return inventory.extractItem(slot,amount,simulate);
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot) {
			return inventory.getSlotLimit(slot);
		}

		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			return inventory.isItemValid(slot, stack);
		}
	};
	public LazyOptional<IItemHandler> holder = LazyOptional.of(() -> inventory);
	public LazyOptional<IItemHandler> outputHolder = LazyOptional.of(() -> outputHandler);
	public boolean outputMode = false;
	public int progress = 0;
	public int process = 0;
	static Random rand = new Random();
	public IAlchemyRecipe cachedRecipe = null;
	protected List<UpgradeContext> upgrades = new ArrayList<UpgradeContext>();

	public static final int SOUND_PROCESS = 1;
	public static final int[] SOUND_IDS = new int[]{SOUND_PROCESS};

	HashSet<Integer> soundsPlaying = new HashSet<>();

	public AlchemyTabletBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.ALCHEMY_TABLET_ENTITY.get(), pPos, pBlockState);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		if (nbt.contains("outputMode"))
			outputMode = nbt.getBoolean("outputMode");
		progress = nbt.getInt("progress");
		inventory.deserializeNBT(nbt.getCompound("inventory"));
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.putBoolean("outputMode", outputMode);
		nbt.putInt("progress", progress);
		nbt.put("inventory", inventory.serializeNBT());
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbt = super.getUpdateTag();
		nbt.putInt("progress", progress);
		nbt.put("inventory", inventory.serializeNBT());
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, AlchemyTabletBlockEntity blockEntity) {
		blockEntity.handleSound();
		if (blockEntity.progress > 0) {
			if (blockEntity.process < 20) {
				blockEntity.process++;
			}

			List<AlchemyPedestalTopBlockEntity> pedestals = getNearbyPedestals(level, pos);

			for (AlchemyPedestalTopBlockEntity pedestal : pedestals) {
				pedestal.setActive(3);

				level.addParticle(StarParticleOptions.EMBER, pedestal.getBlockPos().getX() + 0.5f, pedestal.getBlockPos().getY() + 0.75f, pedestal.getBlockPos().getZ() + 0.5f, 0, 0.00001, 0);
				for (int j = 0; j < 16; j++) {
					float coeff = rand.nextFloat();
					float x = (pos.getX() + 0.5f) * coeff + (1.0f - coeff) * (pedestal.getBlockPos().getX() + 0.5f);
					float y = (pos.getY() + 0.875f) * coeff + (1.0f - coeff) * (pedestal.getBlockPos().getY() + 0.75f);
					float z = (pos.getZ() + 0.5f) * coeff + (1.0f - coeff) * (pedestal.getBlockPos().getZ() + 0.5f);
					level.addParticle(GlowParticleOptions.EMBER, x, y, z, 0, 0.00001, 0);
				}
			}

			if (level.getGameTime() % 10 == 0 && pedestals.size() > 0) {
				AlchemyPedestalTopBlockEntity pedestal = pedestals.get(rand.nextInt(pedestals.size()));
				float dx = (pos.getX() + 0.5f) - (pedestal.getBlockPos().getX() + 0.5f);
				float dy = (pos.getY() + 0.875f) - (pedestal.getBlockPos().getY() + 0.75f);
				float dz = (pos.getZ() + 0.5f) - (pedestal.getBlockPos().getZ() + 0.5f);
				float speed = 0.5f;
				for (int j = 0; j < 20; j++) {
					level.addParticle(StarParticleOptions.EMBER, pedestal.getBlockPos().getX() + 0.5f, pedestal.getBlockPos().getY() + 0.75f, pedestal.getBlockPos().getZ() + 0.5f, dx * speed, dy * speed, dz * speed);
				}
			}
		} else if (blockEntity.progress == 0) {
			if (blockEntity.process > 0) {
				blockEntity.process--;
			}
		}
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, AlchemyTabletBlockEntity blockEntity) {
		blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos, UPGRADE_SIDES); //Defer to when events are added to the upgrade system
		UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
		if (blockEntity.progress > 0) {
			if (level.getGameTime() % 10 == 0) {
				List<AlchemyPedestalTopBlockEntity> pedestals = getNearbyPedestals(level, pos);
				if (blockEntity.progress < PROCESSING_TIME) {
					blockEntity.progress++;
					blockEntity.setChanged();
				} else {
					List<PedestalContents> contents = getPedestalContents(pedestals);
					AlchemyContext context = new AlchemyContext(blockEntity.inventory.getStackInSlot(0), contents, ((ServerLevel) level).getSeed());
					blockEntity.cachedRecipe = Misc.getRecipe(blockEntity.cachedRecipe, RegistryManager.ALCHEMY.get(), context, level);
					if (blockEntity.cachedRecipe != null) {
						AlchemyResult result = blockEntity.cachedRecipe.getResult(context);

						AlchemyResultEvent event = new AlchemyResultEvent(blockEntity, blockEntity.cachedRecipe, result, CONSUME_AMOUNT);
						UpgradeUtil.throwEvent(blockEntity, event, blockEntity.upgrades);

						ItemStack stack = event.isFailure() ? event.getResult().createResultStack(event.getResultStack().copy()) : event.getResultStack().copy();
						SoundEvent finishSound = event.isFailure() ? EmbersSounds.ALCHEMY_FAIL.get() : EmbersSounds.ALCHEMY_SUCCESS.get();
						level.playSound(null, pos, finishSound, SoundSource.BLOCKS, 1.0f, 1.0f);

						if (!event.isFailure()) {
							UpgradeUtil.throwEvent(blockEntity, new MachineRecipeEvent.Success<>(blockEntity, blockEntity.cachedRecipe), blockEntity.upgrades);
							for (AlchemyPedestalTopBlockEntity pedestal : pedestals) {
								pedestal.inventory.setStackInSlot(0, ItemStack.EMPTY);
							}
						} else {
							//this doesn't always consume the same amount of ingredients
							//there is a chance the same pedestal gets cleared multiple times
							for (int i = 0; i < event.getConsumeAmount(); i++) {
								pedestals.get(rand.nextInt(pedestals.size())).inventory.setStackInSlot(0, ItemStack.EMPTY);
							}
						}

						((ServerLevel) level).sendParticles(new GlowParticleOptions(GlowParticleOptions.EMBER_COLOR, 4.0f), pos.getX() + 0.5f, pos.getY() + 0.875, pos.getZ() + 0.5f, 24, 0.1, 0.1, 0.1, 0.5);

						blockEntity.progress = 0;

						BlockEntity outputTile = level.getBlockEntity(pos.below());
						if (outputTile instanceof IBin bin && bin.getInventory().insertItem(0, stack, true).isEmpty()) {
							blockEntity.inventory.extractItem(0, 1, false);
							bin.getInventory().insertItem(0, stack, false);
						} else {
							blockEntity.outputMode = true;
							blockEntity.inventory.extractItem(0, 1, false);
							ItemStack remainder = blockEntity.inventory.forceInsertItem(0, stack, false);
							if (!remainder.isEmpty()) {
								level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, remainder));
							}
							blockEntity.outputMode = true;
						}
					} else {
						blockEntity.progress = 0;
						blockEntity.setChanged();
					}
				}
			}
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && cap == ForgeCapabilities.ITEM_HANDLER) {
			if (side == Direction.DOWN) {
				return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, outputHolder);
			}
			return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, holder);
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		holder.invalidate();
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level instanceof ServerLevel)
			((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
	}

	@Override
	public void sparkProgress(BlockEntity tile, double ember) {
		if (progress != 0 || ember < SPARK_THRESHOLD)
			return;

		List<PedestalContents> pedestals = getPedestalContents(getNearbyPedestals(level, worldPosition));
		AlchemyContext context = new AlchemyContext(inventory.getStackInSlot(0), pedestals, ((ServerLevel) level).getSeed());
		cachedRecipe = Misc.getRecipe(cachedRecipe, RegistryManager.ALCHEMY.get(), context, level);

		AlchemyStartEvent event = new AlchemyStartEvent(this, context, cachedRecipe);
		UpgradeUtil.throwEvent(this, event, upgrades);

		if (event.getRecipe() != null) {
			((ServerLevel) level).sendParticles(AlchemyCircleParticleOptions.DEFAULT, worldPosition.getX() + 0.5, worldPosition.getY() + 1.01, worldPosition.getZ() + 0.5, 5, 0, 0, 0, 1);
			progress = 1;
			setChanged();
			level.playSound(null, worldPosition, EmbersSounds.ALCHEMY_START.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
		}
	}

	public static ArrayList<PedestalContents> getPedestalContents(List<AlchemyPedestalTopBlockEntity> pedestals) {
		ArrayList<PedestalContents> contents = new ArrayList<>();
		for (AlchemyPedestalTopBlockEntity pedestal : pedestals) {
			contents.add(pedestal.getContents());
		}
		return contents;
	}

	public static ArrayList<AlchemyPedestalTopBlockEntity> getNearbyPedestals(Level world, BlockPos pos) {
		ArrayList<AlchemyPedestalTopBlockEntity> pedestals = new ArrayList<>();
		BlockPos.MutableBlockPos pedestalPos = pos.mutable();
		for (int i = -3; i < 4; i ++) {
			for (int j = -3; j < 4; j ++) {
				pedestalPos.set(pos.getX()+i,pos.getY()+1,pos.getZ()+j);
				BlockEntity tile = world.getBlockEntity(pedestalPos);
				if (tile instanceof AlchemyPedestalTopBlockEntity) {
					if (((AlchemyPedestalTopBlockEntity) tile).isValid())
						pedestals.add(((AlchemyPedestalTopBlockEntity) tile));
				}

			}
		}
		return pedestals;
	}

	@Override
	public void playSound(int id) {
		switch (id) {
		case SOUND_PROCESS:
			EmbersSounds.playMachineSound(this, SOUND_PROCESS, EmbersSounds.ALCHEMY_LOOP.get(), SoundSource.BLOCKS, true, 1.5f, 1.0f, (float) worldPosition.getX() + 0.5f, (float) worldPosition.getY() + 1.0f, (float) worldPosition.getZ() + 0.5f);
			break;
		}
		soundsPlaying.add(id);
	}

	@Override
	public void stopSound(int id) {
		soundsPlaying.remove(id);
	}

	@Override
	public boolean isSoundPlaying(int id) {
		return soundsPlaying.contains(id);
	}

	@Override
	public int[] getSoundIDs() {
		return SOUND_IDS;
	}

	@Override
	public boolean shouldPlaySound(int id) {
		return id == SOUND_PROCESS && progress > 0;
	}

	@Override
	public boolean hasCapabilityDescription(Capability<?> capability) {
		return capability == ForgeCapabilities.ITEM_HANDLER;
	}

	@Override
	public void addCapabilityDescription(List<Component> strings, Capability<?> capability, Direction facing) {
		if (capability == ForgeCapabilities.ITEM_HANDLER && facing == Direction.DOWN) {
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.OUTPUT, Embers.MODID + ".tooltip.goggles.item", Component.translatable(Embers.MODID + ".tooltip.goggles.item.alchemy_result")));
		} else {
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.BOTH, Embers.MODID + ".tooltip.goggles.item", null));
		}
	}

	public static class TabletItemStackHandler extends ItemStackHandler {

		AlchemyTabletBlockEntity entity;

		public TabletItemStackHandler(int size, AlchemyTabletBlockEntity entity) {
			super(size);
			this.entity = entity;
		}

		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}

		public ItemStack forceInsertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
			if (stack.isEmpty())
				return ItemStack.EMPTY;

			if (!isItemValid(slot, stack))
				return stack;

			validateSlotIndex(slot);

			ItemStack existing = this.stacks.get(slot);

			int limit = 64;

			if (!existing.isEmpty()) {
				if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
					return stack;

				limit -= existing.getCount();
			}
			if (limit <= 0)
				return stack;

			boolean reachedLimit = stack.getCount() > limit;

			if (!simulate) {
				if (existing.isEmpty()) {
					this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
				} else {
					existing.grow(reachedLimit ? limit : stack.getCount());
				}
				onContentsChanged(slot);
			}
			return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
		}

		@Override
		protected void onContentsChanged(int slot) {
			if (getStackInSlot(slot).isEmpty())
				entity.outputMode = false;
			entity.setChanged();
		}
	}
}
