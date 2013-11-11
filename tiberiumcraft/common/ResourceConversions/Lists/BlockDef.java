


package tiberiumcraft.common.ResourceConversions.Lists;


import java.lang.reflect.Field;

import tiberiumcraft.common.TiberiumCraft;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class BlockDef
{
	protected Block block;
	protected int damage;
	protected boolean isDictionaryRef = false;
	protected String forgeLookup = "";
	protected String packageID;
	
	public boolean isAir()
	{
		if(isDictionaryRef)
			return false;
		return block == null;
	}
	
	public boolean isActualBlock()
	{
		return !isDictionaryRef;
	}
	
	public String getName()
	{
		if(isDictionaryRef)
			return forgeLookup;
		else
			return block.getLocalizedName();
	}
	
	public Block getBlock()
	{
		if(isActualBlock())
			return block;
		else
		{
			System.out.println("Soft Error: Attempted to access a Block from a Definition that's really an OreDictionary wrapper");
			System.out.println("You should check isActualBlock() first");
			if(TiberiumCraft.isDebug)
				throw new IllegalAccessError("Accessing a dictionary reference as block; throwing error because isDebug is set true");
			return null;
		}
	}
	
	/**
	 * Does is this BlockDef the same as d OR does this BlockDef encompass d?
	 * @param d
	 * the BlockDef to test against this.  Is d either equal to this, or a subset of this.
	 * @return
	 * boolean representing the above condition
	 */
	public boolean match(BlockDef d)
	{
		if(d == null)
			return false;
		if(d.isDictionaryRef)
		{
			if(!isDictionaryRef)
				return false;
			else
				return forgeLookup.equals(d.forgeLookup);
		}
		if(isDictionaryRef)
		{
			if(d.block == null)
				return false;
			return OreDictionary.getOres(forgeLookup).contains(new ItemStack(d.block));
		}
		else
		{
			if(this.block == null || d.block == null)
			{
				return this.block == null && d.block == null;
			}
			else
			{
				if(this.block.getClass() != d.block.getClass()) 
					return false;
				if(this.damage == -1) 
					return true;
				if(this.damage == d.damage)
					return true;
				if(this.damage == 0 && d.damage == -1)
					return true;
				return false;
			}
		}
	}
	
	public BlockDef(int ID)
	{
		if(ID < 0)
			block = null;
		else
			block = Block.blocksList[ID];
		damage = -1;
	}
	
	public BlockDef(int ID, int damage)
	{
		block = Block.blocksList[ID];
		this.damage = damage;
	}
	
	public BlockDef(World world, int x, int y, int z)
	{
		block = Block.blocksList[world.getBlockId(x, y, z)];
		damage = world.getBlockMetadata(x, y, z);
	}
	
	public BlockDef(Block b)
	{
		block = b;
		damage = -1;
	}
	
	public BlockDef(Block b, int damage)
	{
		block = b;
		this.damage = damage;
	}
	
	public BlockDef(String NameString, String IDString)
	{
		String[] parts = NameString.split(":");
		String[] IDStrings = IDString.split(":");
		switch(parts[0])
		{
			case "ore_dictonary":
				{
					forgeLookup = parts[1];
					isDictionaryRef = true;
					break;
				}
			case "minecraft":
				{
					if(!parseAsID(IDStrings))
					{
						block = null;
						damage = -2;
						System.out.println("Failed parsing as block ID: "+IDString);
					}
					break;
				}
			default:
				{
					//assume it's a mod item and parse by name
					//there's probably a better way to do this
					//but for now...
					for(Block b : Block.blocksList)
					{
						if(b.getUnlocalizedName() == parts[1])
						{
							block = b;
							if(parts.length == 3)
								damage = Integer.parseInt(parts[2]);
							return;
						}
					}
					System.out.println("failed to find block " + parts[1] + " in unlocalized block names");
					break;
				}
		}


	}

	private boolean parseAsID(String[] parts)
	{
		try
		{
			block = Block.blocksList[Integer.parseInt(parts[0])];
		}
		catch(NumberFormatException e)
		{
			return false;
		}
		if(parts.length == 2)
		{
			try
			{
				damage = Integer.parseInt(parts[1]);
			}
			catch(NumberFormatException e)
			{
				System.out.println("Failed to read damage on block");
				damage = -1;
				return false;
			}
		}
		else
			damage = -1;
		return true;
	}
	
	@SuppressWarnings("unused")
	private boolean attemptParseByNameTex(String[] parts)
	{
		try
		{
			for(Block b : Block.blocksList)	
			{
				if(b == null)
					continue;
				//This method won't function without a base edit 
				//to change the visiblity of the texture name field
				//the following confirms this workaround will work
				//so as not to crash with an IllegalAccessException
				Field f = b.getClass().getField("textureName");
				if(parts[1].equals(b.textureName))
				{
					block = b;
					if(parts.length == 3)
						damage = Integer.parseInt(parts[2]);
					return true;
				}
				
				
			}			
			System.out.println("failed to find block " + parts[1] + " in unlocalized block names");
		}
		catch(NoSuchFieldException e)
		{
			System.out.println("What do you mean that Java 'couldn't find the field...'");
			e.printStackTrace();
		}
		catch(SecurityException e)
		{
			System.out.println("This hack only works with a base edit; only try it in debug mode");
		}
		return false;
	}
}
