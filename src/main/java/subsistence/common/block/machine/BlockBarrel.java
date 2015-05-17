package subsistence.common.block.machine;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import subsistence.common.block.prefab.SubsistenceTileMultiBlock;
import subsistence.common.item.SubsistenceItems;
import subsistence.common.recipe.SubsistenceRecipes;
import subsistence.common.tile.machine.TileStoneBarrel;
import subsistence.common.tile.machine.TileWoodBarrel;
import subsistence.common.util.ArrayHelper;

import java.util.Random;

public final class BlockBarrel extends SubsistenceTileMultiBlock {

    private static final String[] NAMES = new String[]{"wood", "stone"};

    public BlockBarrel() {
        super(Material.wood);
        this.setHardness(0.5f);
    }

    @Override
    public int[] getSubtypes() {
        return ArrayHelper.getArrayIndexes(NAMES);
    }

    @Override
    public String getNameForType(int type) {
        return ArrayHelper.safeGetArrayIndex(NAMES, type);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        if (meta == 0) { //wood
            return new TileWoodBarrel();
        } else {
            return new TileStoneBarrel();
        }
    }

    @Override
    public boolean useCustomRender() {
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getCurrentEquippedItem();
        TileWoodBarrel tile = (TileWoodBarrel) world.getTileEntity(x, y, z);

        if (stack == null && tile.hasLid()) {
            tile.toggleLid();

            if (!world.isRemote)
                player.setCurrentItemOrArmor(0, new ItemStack(SubsistenceItems.barrelLid, 1, tile.getBlockMetadata()));
            return true;
        }
        ItemStack held = player.getHeldItem();

        if (tile != null)
            if (side == 1 && !tile.hasLid()) {

                if (tile.fluid == null && FluidContainerRegistry.isFilledContainer(held)) {
                    FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(held);
                    if (fluidStack != null && tile.addFluid(fluidStack)) {
                        if (!player.capabilities.isCreativeMode) {
                            player.setCurrentItemOrArmor(0, FluidContainerRegistry.EMPTY_BUCKET);
                        }
                    }
                } else if (tile.fluid != null && FluidContainerRegistry.isEmptyContainer(held)) {
                    ItemStack container = FluidContainerRegistry.fillFluidContainer(tile.fluid, FluidContainerRegistry.EMPTY_BUCKET);
                    FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(container);
                    if (container != null && tile.reduceFluid(fluidStack)) {
                        if (!player.capabilities.isCreativeMode) {
                            player.setCurrentItemOrArmor(0, container);
                        }
                    }
                } else if (tile.fluid != null && !FluidContainerRegistry.isEmptyContainer(held)) {
                    tile.addFluid(FluidContainerRegistry.getFluidForFilledItem(held));
                } else if (held != null && Block.getBlockFromItem(held.getItem()) != Blocks.air) {
                    ItemStack itemCopy = held.copy();
                    if (SubsistenceRecipes.BARREL.isAllowed(itemCopy)) {
                        itemCopy.stackSize = 1;
                        if (tile.addItemToStack(itemCopy)) {
                            held.stackSize--;
                            if (held.stackSize <= 0) {
                                player.setCurrentItemOrArmor(0, null);
                            }
                            tile.markForUpdate();
                        }
                    }
                } else {
                    if (tile.contents != null && tile.contents.length > 0) {
                        player.setCurrentItemOrArmor(0, tile.removeItemFromStack());
                    }
                }

            }
        return true;
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean harvest) {
        if (!player.capabilities.isCreativeMode && !world.isRemote && this.canHarvestBlock(player, world.getBlockMetadata(x, y, z))) {

            float motion = 0.7F;
            double motX = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;
            double motY = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;
            double motZ = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;

            EntityItem item = new EntityItem(world, x + motX, y + motY, z + motZ, this.getPickBlock(null, world, x, y, z, player));
            world.spawnEntityInWorld(item);
        }

        return world.setBlockToAir(x, y, z);
    }


    @Override
    public int quantityDropped(Random rand) {
        return 0;
    }

    @Override
    public Item getItemDropped(int i, Random rand, int j) {
        return null;
    }
}