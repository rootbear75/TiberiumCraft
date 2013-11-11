


package tiberiumcraft.common.ResourceConversions.OutputDefs;


import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OutputDefOreDictionary extends OutputDef
{
	private String dictionaryName;
	
	public OutputDefOreDictionary(int defID, float factor, String dictionaryName)
	{
		super(defID, factor);
		this.dictionaryName = dictionaryName;
		
	}
	
	public String getName()
	{
		return dictionaryName;
	}
	
	public List<ItemStack> getAllInDictionary()
	{
		return OreDictionary.getOres(dictionaryName);
	}
	
	public ItemStack getFirstInDictionary()
	{
		try
		{
			return OreDictionary.getOres(dictionaryName).get(0);
		}
		catch(Exception e)
		{
			if(!(e instanceof ArrayIndexOutOfBoundsException))
				e.printStackTrace();
			else
				System.out.println("Tried to get ItemStack that wasn't in the ore dictionary: ".concat(dictionaryName));
			return null;
		}
	}
	
	public int getIDOf()
	{
		try
		{
			return getFirstInDictionary().itemID;
		}
		catch(Exception e)
		{
			if(e instanceof NullPointerException)
			{
				System.out.println("Tried to get ID that wasn't in the ore dictionary: ".concat(dictionaryName));
				return -1;
			}
			else
				throw e;
		}
	}
}
