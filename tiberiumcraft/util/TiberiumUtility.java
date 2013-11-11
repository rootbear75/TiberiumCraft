


package tiberiumcraft.util;


import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import org.lwjgl.util.vector.Vector3f;

import tiberiumcraft.common.ResourceConversions.Lists.BlockDef;
import tiberiumcraft.common.ResourceConversions.Lists.BlockList;

public class TiberiumUtility
{
	
	private static List<Block> replaceList = new ArrayList<Block>();
	
	{
		replaceList.add(Block.waterlily);
		replaceList.add(Block.waterStill);
		replaceList.add(Block.waterMoving);
		replaceList.add(Block.leaves);
		replaceList.add(Block.tallGrass);
		replaceList.add(Block.plantRed);
		replaceList.add(Block.plantYellow);
		replaceList.add(Block.vine);
		replaceList.add(Block.snow);
	}
	
	public static int getValidHeight(World world, int x, int z, int startY, double maxDelta)
	{
		int y = startY;//world.getHeightValue(x, z);
		System.out.println("starting search from y = ".concat(Integer.toString(y)));
		if(y <= 0) return -1;
		BlockDef block;
		BlockDef blockUnder;
		
		block = new BlockDef(world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z));
		blockUnder = new BlockDef(world.getBlockId(x, y - 1, z), world.getBlockMetadata(x, y, z));
		
		while(true)
		{
			if(y <= 0) return -1;
			if(maxDelta <= 0) return -1;
			boolean blockValid, blockUnderValid;
			try
			{
				blockValid = BlockList.replaceWhitelist.isOnList(block);
				if(!blockUnder.isAir() && blockUnder.getBlock().isOpaqueCube())
				{
					blockUnderValid = !BlockList.placeOnBlacklist.isOnList(blockUnder);
				}
				else
				{
					blockUnderValid = BlockList.placeOnWhitelist.isOnList(blockUnder);
				}
				
			}
			catch(NullPointerException e)
			{
				System.out.println("getValidHeight crashed on: ".concat(Vector3fUtil.v3fToString(new Vector3f(x, y, z))));
				System.out.println("BlockUnder: ".concat(blockUnder.getName()));
				throw e;
			}
			if(blockUnderValid)
			{
				if(blockValid)
				{
					System.out.println("Returning y = ".concat(Integer.toString(y)));
					return y;
				}
				else
				{
					y--;
					block = new BlockDef(world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z));
					blockUnder = new BlockDef(world.getBlockId(x, y - 1, z), world.getBlockMetadata(x, y, z));
				}
			}
			else
			{
				if(!BlockList.replaceWhitelist.isOnList(block))
					return -1;
				else
				{
					y--;
					block = new BlockDef(world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z));
					blockUnder = new BlockDef(world.getBlockId(x, y - 1, z), world.getBlockMetadata(x, y, z));
				}
			}
			maxDelta--;
			
		}
	}
	
	public static List<Vector3f> getPtsInCircle(Vector3f pt, float radius)
	{
		List<Vector3f> temp = new ArrayList<Vector3f>();
		for(float x = (int) (pt.x - radius) - 1; x <= pt.x + radius; x++)
		{
			for(float z = (int) (pt.z - radius) - 1; z <= pt.z + radius; z++)
			{
				Vector3f temp2 = new Vector3f(x, pt.y, z);
				Vector3f temp3 = new Vector3f();
				Vector3f.sub(pt, temp2, temp3);
				if(temp3.length() <= radius) temp.add(temp2);
			}
		}
		return temp;
	}
}
