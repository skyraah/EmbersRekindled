package teamroots.embers.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.TileFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import teamroots.embers.EventManager;
import teamroots.embers.SoundManager;
import teamroots.embers.api.EmbersAPI;
import teamroots.embers.api.upgrades.IUpgradeProvider;
import teamroots.embers.api.upgrades.UpgradeUtil;
import teamroots.embers.network.PacketHandler;
import teamroots.embers.network.message.MessageEmberActivationFX;
import teamroots.embers.util.Misc;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class TileEntityBoilerBottom extends TileFluidHandler implements ITileEntityBase, ITickable {
    public static final float BASE_MULTIPLIER = 1.5f;
    public static final int FLUID_CONSUMED = 25;
    public static final float PER_BLOCK_MULTIPLIER = 0.375f;
    public static final int PROCESS_TIME = 20;
    public static int capacity = Fluid.BUCKET_VOLUME * 8;
    Random random = new Random();
    int progress = -1;
    public ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            TileEntityBoilerBottom.this.markDirty();
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (EmbersAPI.getEmberValue(stack) == 0) {
                return stack;
            }
            return super.insertItem(slot, stack, simulate);
        }
    };

    public TileEntityBoilerBottom() {
        super();
        tank = new FluidTank(capacity);
        tank.setTileEntity(this);
        tank.setCanFill(true);
        tank.setCanDrain(true);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("inventory", inventory.serializeNBT());
        tag.setInteger("progress", progress);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        inventory.deserializeNBT(tag.getCompoundTag("inventory"));
        if (tag.hasKey("progress")) {
            progress = tag.getInteger("progress");
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                            EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);
        //TODO: Any fluid container
        if (heldItem.getItem() instanceof ItemBucket || heldItem.getItem() instanceof UniversalBucket) {
            boolean didFill = FluidUtil.interactWithFluidHandler(player, hand, tank);
            this.markDirty();
            return didFill;
        }
        return false;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        this.invalidate();
        Misc.spawnInventoryInWorld(getWorld(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, inventory);
        world.setTileEntity(pos, null);
    }

    public double getMultiplier() {
        double metalMultiplier = EmbersAPI.getMetalCoefficient(world.getBlockState(pos.down()));
        double totalMult = BASE_MULTIPLIER;
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            IBlockState state = world.getBlockState(pos.down().offset(facing));
            if (state.getBlock() == Blocks.LAVA || state.getBlock() == Blocks.FLOWING_LAVA || state.getBlock() == Blocks.FIRE) {
                totalMult += PER_BLOCK_MULTIPLIER * metalMultiplier;
            }
        }
        return totalMult;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.inventory;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void update() {
        List<IUpgradeProvider> upgrades = UpgradeUtil.getUpgrades(world, pos, EnumFacing.HORIZONTALS);
        UpgradeUtil.verifyUpgrades(this, upgrades);
        TileEntity tile = getWorld().getTileEntity(getPos().up());
        boolean cancel = UpgradeUtil.doWork(this,upgrades);
        if (!cancel && tank.getFluid() != null && tank.getFluid().getFluid() == FluidRegistry.WATER && tank.getFluidAmount() >= FLUID_CONSUMED && tile instanceof TileEntityBoilerTop) {
            TileEntityBoilerTop top = (TileEntityBoilerTop) tile;
            progress++;
            if (progress > UpgradeUtil.getWorkTime(this, PROCESS_TIME, upgrades)) {
                progress = 0;
                int i = random.nextInt(inventory.getSlots());
                if (!inventory.getStackInSlot(i).isEmpty()) {
                    ItemStack emberStack = inventory.getStackInSlot(i);
                    double emberValue = EmbersAPI.getEmberValue(emberStack);
                    if (emberValue > 0) {
                        double ember = UpgradeUtil.getTotalEmberProduction(this, emberValue * getMultiplier(), upgrades);
                        if (top.capability.getEmber() <= top.capability.getEmberCapacity() - ember) {
                            tank.drain(FLUID_CONSUMED, true);
                            if (!world.isRemote) {
                                world.playSound(null, getPos().getX() + 0.5, getPos().getY() + 1.5, getPos().getZ() + 0.5, SoundManager.PRESSURE_REFINERY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                                PacketHandler.INSTANCE.sendToAll(new MessageEmberActivationFX(getPos().getX() + 0.5f, getPos().getY() + 1.5f, getPos().getZ() + 0.5f));
                            }
                            top.capability.addAmount(ember, true);
                            inventory.extractItem(i, 1, false);
                            markDirty();
                            top.markDirty();
                        }
                    }
                }
            }
            markDirty();
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        Misc.syncTE(this);
    }
}
