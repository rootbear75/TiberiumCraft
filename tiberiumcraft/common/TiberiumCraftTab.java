package tiberiumcraft.common;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;

public class TiberiumCraftTab extends CreativeTabs
{

	public TiberiumCraftTab(int par1, String par2Str)
	{
		super(par1, par2Str);
	}
	
    @Override
    @SideOnly(Side.CLIENT)
    public int getTabIconItemIndex()
    {
            return TiberiumCraft.instance.tiberiumCrystalItem.itemID;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getTranslatedTabLabel()
    {
            return this.getTabLabel();
    }
	
}
