


package tiberiumcraft.common.ResourceConversions;


import java.util.ArrayList;
import java.util.List;

import tiberiumcraft.common.ResourceConversions.OutputDefs.OutputDef;

//defines a TiberiumCraft internal representation of a "Thing" that Tiberium can absorb
//does nothing on it's own, but is an intermediary between absorbing a block and spitting out an item
public class ResourceDef
{
	private static List<ResourceDef> defs = new ArrayList<ResourceDef>();
	
	public String getMinecraftName()
	{
		return internalName;
	}
	
	public String getDisplayName()
	{
		return displayName;
	}
	
	private String internalName;
	private String displayName;
	public List<OutputDef> outs = new ArrayList<OutputDef>();
	public int defID;
	
	public ResourceDef(String internalName, String displayName, int defID, int[] refIDs)
	{
		this.internalName = internalName;
		this.displayName = displayName;
		for(int i : refIDs)
		{
			outs.add(OutputDef.getByIndex(i));
		}
		this.defID = defID;
		defs.add(this);
	}
	
	public static ResourceDef[] getResourceDefs()
	{
		ResourceDef[] rDefs = new ResourceDef[defs.size()];
		defs.toArray(rDefs);
		return rDefs;
	}
	
	public int getID()
	{
		return defID;
	}
	
	public static ResourceDef getByIndex(int i)
	{
		for(ResourceDef d : defs)
		{
			if(d.getID() == i) return d;
		}
		return null;
	}
	
	@Override
	public String toString()
	{
		return displayName.concat("(Internal:".concat(internalName).concat(")"));
	}
}
