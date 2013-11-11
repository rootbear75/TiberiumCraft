


package tiberiumcraft.common.blocks;


import java.util.List;
import java.util.Random;

import tiberiumcraft.common.TiberiumCraft;
import tiberiumcraft.common.ResourceConversions.TBResource;
import tiberiumcraft.common.entity.TiberiumCrystalEntity;
import tiberiumcraft.common.items.ItemTiberiumGun;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class TiberiumCrystalBlock extends TiberiumCraftBlockContainer
{
	Icon icon;
	
	public TiberiumCrystalBlock(int id)
	{
		super(id, Material.glass);
		setUnlocalizedName("timeriumcraft.crystalblock");
		setTextureName("tiberiumcraft:tiberiumcrystal");
		setHardness(30F);
		setResistance(5F);
		setLightValue(0.75F);
		MinecraftForge.setBlockHarvestLevel(this, ItemTiberiumGun.MATERIAL.name(), ItemTiberiumGun.MATERIAL.getHarvestLevel());
		setCreativeTab(TiberiumCraft.instance.tabsTiberiumCraft);
		GameRegistry.registerBlock(this, "tiberiumCrystalBlock");
		LanguageRegistry.addName(this, "Tiberium Crystal");
	}
	
	@Override
	public int quantityDropped(Random par1Random)
	{
		return 0;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TiberiumCrystalEntity();
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata)
	{
		return new TiberiumCrystalEntity();
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int i1, int i2)
	{
		final TiberiumCrystalEntity tce = (TiberiumCrystalEntity) world.getBlockTileEntity(x, y, z);
		if(tce != null)
		{
			dropContent(tce.getCurrentResources(), world, tce.xCoord, tce.yCoord, tce.zCoord);
		}
		super.breakBlock(world, x, y, z, i1, i2);
	}
	
	private void dropContent(List<TBResource> resources, World world, int xCoord, int yCoord, int zCoord)
	{
		ItemStack item = new ItemStack(TiberiumCraft.instance.tiberiumCrystalItem, 1);
		
		item.setTagCompound(TBResource.writeListToTag(resources));
		
		EntityItem entityItem = new EntityItem(world, xCoord, yCoord, zCoord, item);
		
		world.spawnEntityInWorld(entityItem);
		
	}
	
	@Override
	public boolean onBlockActivated(World par1World, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9)
	{
		return super.onBlockActivated(par1World, x, y, z, entityPlayer, par6, par7, par8, par9);
	}
	
	@Override
	protected void onDebugActivate(TileEntity te, boolean isServer)
	{
		if(!isServer) return;
		TiberiumCrystalEntity tce = ((TiberiumCrystalEntity) te);
		tce.logDebugInfoToStream(System.out);
	}
	
	@Override
	protected void onDebugSneakClick(TileEntity te, boolean isServer)
	{
		if(!isServer) return;
		TiberiumCrystalEntity tce = ((TiberiumCrystalEntity) te);
		tce.debugForceUpdate();
	}

	@Override
	protected void onDebugClick(TileEntity contained, boolean isServer)
	{
		
	}
}
