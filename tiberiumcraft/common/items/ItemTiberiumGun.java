package tiberiumcraft.common.items;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import tiberiumcraft.common.TiberiumCraft;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraftforge.common.EnumHelper;

public class ItemTiberiumGun extends ItemTool {

	public static EnumToolMaterial MATERIAL = EnumHelper.addToolMaterial("TIBERIUMGUN", 10, -1, 6.0F, .2f, 0);
	
	static Block[] blocks = {TiberiumCraft.instance.tiberiumCrystalBlock, TiberiumCraft.instance.tiberiumLogBlock};
	
	public ItemTiberiumGun(int id) {
		super(id, 0f, MATERIAL, blocks);
		setUnlocalizedName("timeriumcraft.tiberiumGunItem");
		setCreativeTab(TiberiumCraft.instance.tabsTiberiumCraft);
		GameRegistry.registerItem(this, "tiberiumGunItem");
		LanguageRegistry.addName(this, "Tiberium Harvester");
	}

	@Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase player, EntityLivingBase mob)
    {
        stack.damageItem(2, mob);
        // add status effects to mob
        return true;
    }
}
