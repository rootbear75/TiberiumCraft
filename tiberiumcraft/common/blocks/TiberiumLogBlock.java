


package tiberiumcraft.common.blocks;


import java.util.Random;

import tiberiumcraft.common.TiberiumCraft;
import tiberiumcraft.common.items.TiberiumLogItem;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockLog;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraftforge.common.MinecraftForge;

public class TiberiumLogBlock extends BlockLog
{
	Icon[] sideIcon = new Icon[4];
	Icon[] topIcon = new Icon[4];
	
	public TiberiumLogBlock(int id)
	{
		super(id);
		setUnlocalizedName("timeriumcraft.treetrunk");
		setHardness(30F);
		setResistance(30F);
		setCreativeTab(TiberiumCraft.instance.tabsTiberiumCraft);
		MinecraftForge.setBlockHarvestLevel(this, "pickaxe", 2);
		GameRegistry.registerBlock(this, TiberiumLogItem.class, "tiberiumTreetrunkBlock");
		LanguageRegistry.addName(this, "Tiberium Tree Stalk");
	}
	
	@Override
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return blockID;
    }
	
    @SideOnly(Side.CLIENT)
    protected Icon getSideIcon(int i)
    {
        return sideIcon[i];
    }

    @Override
    protected Icon getEndIcon(int i)
    {
        return topIcon[i];
    }
	
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
        for (int i = 0; i < woodType.length; ++i)
        {
            sideIcon[i] = iconRegister.registerIcon("tiberiumcraft:log_" + woodType[i] + "_tiberium");
            topIcon[i] = iconRegister.registerIcon("tiberiumcraft:log_tiberium_top");
        }
	}
}
