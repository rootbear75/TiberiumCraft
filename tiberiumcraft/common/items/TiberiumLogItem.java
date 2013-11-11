package tiberiumcraft.common.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockLog;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class TiberiumLogItem extends ItemBlock
{

	public TiberiumLogItem(int par1)
	{
		super(par1);
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubItems(int blockID, CreativeTabs creativeTabs, List list)
	{
		for (int i = 0; i < BlockLog.woodType.length; i++)
		{
			list.add(new ItemStack(blockID, 1, i));
		}
	}

	@Override
	public int getMetadata(int metadata)
	{
		return metadata;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemDisplayName(ItemStack itemStack)
	{
		int meta = itemStack.getItemDamage();
		return "Tiberium " + BlockLog.woodType[meta] + " Log";
	}
}
