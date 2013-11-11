


package tiberiumcraft.common.ResourceConversions;


import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

//The actual information stored in tiberium's metadata
public class TBResource
{
	public ResourceDef resource;
	public float amount;
	
	public TBResource(ResourceDef res, float quantity)
	{
		resource = res;
		amount = quantity;
	}
	
	public static NBTTagCompound writeListToTag(List<TBResource> resources)
	{
		NBTTagCompound resListTag = new NBTTagCompound("Resources");
		resListTag.setInteger("count", resources.size());
		for(int i = 0; i < resources.size(); i++)
		{
			NBTTagCompound resTag = new NBTTagCompound(String.valueOf(i));
			resTag.setInteger("ResDefID", resources.get(i).resource.getID());
			resTag.setFloat("Amount", resources.get(i).amount);
			resListTag.setCompoundTag(String.valueOf(i), resTag);
		}
		return resListTag;
		
	}
	
	public static List<TBResource> readListFromTag(NBTTagCompound tag)
	{
		List<TBResource> resources = new ArrayList<TBResource>();
		if(tag != null)
		{
			NBTTagCompound resListTag = tag.getCompoundTag("Resources");
			for(int i = 0; i < resListTag.getInteger("count"); i++)
			{
				NBTTagCompound TBR = resListTag.getCompoundTag(String.valueOf(i));
				resources.add(new TBResource(ResourceDef.getByIndex(TBR.getInteger("ResDefID")), TBR.getFloat("Amount")));
			}
		}
		return resources;
	}
	
	@Override
	public String toString()
	{
		return resource.toString().concat(":".concat(Float.toString(amount)));
	}
}
