


package tiberiumcraft.common.worldgeneration;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import tiberiumcraft.common.TiberiumCraft;
import tiberiumcraft.util.TiberiumUtility;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class TiberiumWorldGen implements IWorldGenerator
{
	private List<Block> blackList = new ArrayList<Block>();

	{
		blackList.add(Block.waterlily);
		blackList.add(Block.waterStill);
		blackList.add(Block.waterMoving);
		blackList.add(Block.leaves);
		blackList.add(Block.tallGrass);
		blackList.add(Block.plantRed);
		blackList.add(Block.plantYellow);
		blackList.add(Block.vine);
		blackList.add(Block.snow);
	}

	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{

		BiomeGenBase b = world.getBiomeGenForCoords(chunkX, chunkZ);
		int meta;

		switch ( b.biomeID )
		{
		case 4:
		case 18:
			meta = random.nextInt(2);
			break;
		case 5:
		case 12:
		case 13:
		case 19:
			meta = 2;
			break;
		case 21:
		case 22:
			meta = 3;
			break;
		case 8:
		case 9:
			meta = -1;
			break;
		default:
			meta = 0;
			break;
		}

		switch(world.provider.dimensionId)
		{
		case 0:
			generateSurface(world, random, chunkX * 16, chunkZ * 16, meta);
			break;
		}


	}

	private void generateSurface(World world, Random random, int x, int z, int meta)
	{
		int X = 0;
		int Y = 0;
		int Z = 0;

		Vector3f pos = new Vector3f(x, 0, z);

		List<Vector3f> points = TiberiumUtility.getPtsInCircle(pos, random.nextInt(15));

		if ( random.nextInt(64) == 32 )
		{
			if (meta != -1)
			{
				if(genTree(world, random, x, z, meta))
				{
					for (Vector3f point : points)
					{
						X = (int) point.x;
						Z = (int) point.z;
						Y = TiberiumUtility.getValidHeight(world, X, Z, world.getHeightValue(X,Z), 128);

						if (Y > 0 && random.nextInt(5) == 1 && X != x && Z != z)
						{
							world.setBlock(X, Y, Z, TiberiumCraft.instance.tiberiumCrystalBlock.blockID);
						}
					}
				}
			}
		}
	}

	private boolean genTree(World world, Random random, int x, int z, int meta)
	{
		int y = TiberiumUtility.getValidHeight(world, x, z, world.getHeightValue(x, z), 128);
		if(y <= 0)
			return false;
		for (int i = 0; i < 6; i++)
		{
			world.setBlock(x, y + i, z, TiberiumCraft.instance.tiberiumLogBlock.blockID, meta, 2);
		}
		return true;
	}

}
