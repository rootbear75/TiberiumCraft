


package tiberiumcraft.common.items;


import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import tiberiumcraft.common.TiberiumCraft;
import tiberiumcraft.common.ResourceConversions.TBResource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TiberiumCrystalItem extends Item
{
	
	public TiberiumCrystalItem(int ID)
	{
		super(ID);
		setUnlocalizedName("timeriumcraft.crystalitem");
		iconString = "tiberiumcraft:crystalitem";
		setCreativeTab(TiberiumCraft.instance.tabsTiberiumCraft);
		GameRegistry.registerItem(this, "tiberiumCrystalItem");
		LanguageRegistry.addName(this, "Tiberium Crystal");
	}
	
	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void addInformation(ItemStack is, EntityPlayer player, List info, boolean bool)
	{
		NBTTagCompound tag = is.stackTagCompound;
		
		List<TBResource> resources = TBResource.readListFromTag(tag);
		
		if (resources != null && resources.size() > 0)
		{
			info.add("Contains:");
			for (TBResource def : resources)
			{
				info.add(String.format("%sx %s", def.amount, def.resource.getDisplayName()));
			}
		}

	}
	
}
