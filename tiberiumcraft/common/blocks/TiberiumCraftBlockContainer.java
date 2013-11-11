package tiberiumcraft.common.blocks;

import org.lwjgl.util.vector.Vector3f;

import tiberiumcraft.common.TiberiumCraft;
import tiberiumcraft.common.items.ItemDbgStaff;
import tiberiumcraft.util.Vector3fUtil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class TiberiumCraftBlockContainer extends BlockContainer
{
	
	protected TiberiumCraftBlockContainer(int par1, Material par2Material)
	{
		super(par1, par2Material);
		// TODO Auto-generated constructor stub
	}

	@Override
	public abstract TileEntity createNewTileEntity(World world);
	
	@Override
	public boolean onBlockActivated(
			World world, int x, int y, int z, EntityPlayer entityPlayer, 
			int par6, float par7, float par8, float par9)
	{
		if(TiberiumCraft.isDebug)
		{
			TileEntity te = world.getBlockTileEntity(x, y, z);
			if(te != null)
			{
				if(ItemDbgStaff.isDebugTool(entityPlayer.getCurrentEquippedItem()))
					System.out.println("Doing RMB wand action");
						onDebugActivate(te, !world.isRemote);
			}
			else
				System.out.println("Could not find TileEntity at this location: ".concat(Vector3fUtil.v3fToString(new Vector3f(x, y, z))));
		}
		return super.onBlockActivated(world, x, y, z, entityPlayer, par6, par7, par8, par9);
	}
	
	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer entityPlayer)
	{
		if(TiberiumCraft.isDebug)
		{
			TileEntity te = world.getBlockTileEntity(x, y, z);
			if(te != null)
			{
				if(ItemDbgStaff.isDebugTool(entityPlayer.getCurrentEquippedItem()))
				{
					
					if(entityPlayer.isSneaking())
					{
						System.out.println("Doing sneak-LMB wand action");
						onDebugSneakClick(te, !world.isRemote);
					}
					else
					{
						System.out.println("Doing LMB wand action");
						onDebugClick(te, !world.isRemote);
					}
				}
			}
			else
				System.out.println("Could not find TileEntity at this location: ".concat(Vector3fUtil.v3fToString(new Vector3f(x, y, z))));
		}
		
		super.onBlockClicked(world, x, y, z, entityPlayer);
	}
	
	
	
	protected abstract void onDebugActivate(TileEntity contained, boolean isServer);
	protected abstract void onDebugClick(TileEntity contained, boolean isServer);
	protected abstract void onDebugSneakClick(TileEntity contained, boolean isServer);
	
}
