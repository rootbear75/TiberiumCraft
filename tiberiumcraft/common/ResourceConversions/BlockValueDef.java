


package tiberiumcraft.common.ResourceConversions;


import java.util.ArrayList;
import java.util.List;

import tiberiumcraft.common.ResourceConversions.Lists.BlockDef;

//Defines a whitelisted item that Tiberium can absorb
//and a mapping of block absorption onto resource output
public class BlockValueDef
{
	private static List<BlockValueDef> blockValDefs = new ArrayList<BlockValueDef>();
	
	public BlockValueDef(BlockDef block, List<ConversionDef> conversion)
	{
		blockIdentifier = block;
		for(ConversionDef def : conversion)
		{
			values.add(def);
		}
		blockValDefs.add(this);
	}
	
	List<ConversionDef> values = new ArrayList<ConversionDef>();
	BlockDef blockIdentifier;
	
	public float getConversion(ResourceDef resource)
	{
		for(ConversionDef def : values)
		{
			if(def.getResType() == resource) return def.getAmount();
		}
		return 0;
	}
	
	public ConversionDef[] getAllConversions()
	{
		ConversionDef[] temp = new ConversionDef[values.size()];
		values.toArray(temp);
		return temp;
	}
	
	public static BlockValueDef lookup(int id)
	{
		return lookup(new BlockDef(id));
	}
	
	public static BlockValueDef lookup(int id, int damage)
	{
		return lookup(new BlockDef(id, damage));
	}
	
	public static BlockValueDef lookup(BlockDef definition)
	{
		for(BlockValueDef def : blockValDefs)
			if(def.getID().match(definition)) return def;
		return null;
	}
	
	public BlockDef getID()
	{
		return blockIdentifier;
	}
}
