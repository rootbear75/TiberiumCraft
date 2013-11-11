


package tiberiumcraft.common;


import java.io.File;
import java.lang.reflect.Field;

import tiberiumcraft.common.ResourceConversions.ConfigLoader;
import tiberiumcraft.common.blocks.TiberiumCrystalBlock;
import tiberiumcraft.common.blocks.TiberiumLogBlock;
import tiberiumcraft.common.entity.LightningBolt;
import tiberiumcraft.common.entity.TiberiumCrystalEntity;
import tiberiumcraft.common.items.ItemDbgStaff;
import tiberiumcraft.common.items.ItemTiberiumGun;
import tiberiumcraft.common.items.TiberiumCrystalItem;
import tiberiumcraft.common.worldgeneration.TiberiumWorldGen;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "TiberiumCraft", name = "TiberiumCraft", version = "PreAlpha")
@NetworkMod(clientSideRequired = true, serverSideRequired = true, packetHandler = PacketManager.class, channels = "TiberiumCraft")
public class TiberiumCraft
{
	public static boolean isDebug = false;
	
	@SidedProxy(clientSide = "tiberiumcraft.client.ClientProxy", serverSide = "tiberiumcraft.common.CommonProxy")
	public static CommonProxy proxy;
	
	@Instance(value = "TiberiumCraft")
	public static TiberiumCraft instance;
	
	public Block tiberiumLogBlock, tiberiumCrystalBlock;
	
	public Item tiberiumCrystalItem, tiberiumGun, debugStaff;
	
	public CreativeTabs tabsTiberiumCraft = new TiberiumCraftTab(CreativeTabs.getNextID(), "TiberiumCraft");
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Config.Load(event.getSuggestedConfigurationFile());
		try
		{
			Field configField = Config.class.getField("debug");
			isDebug = configField.getBoolean(configField);
		}
		catch(Exception e)
		{
			System.out.println("Failed to load debug mode flag from config");
			isDebug = false;
		}
		
		tiberiumCrystalItem = new TiberiumCrystalItem(Config.tiberiumCrystalItemID);
		tiberiumGun = new ItemTiberiumGun(Config.tiberiumGunID);
		tiberiumCrystalBlock = new TiberiumCrystalBlock(Config.tiberiumCrystalBlockID);
		tiberiumLogBlock = new TiberiumLogBlock(Config.tiberiumLogBlockID);
		debugStaff = new ItemDbgStaff(Config.debugStaffItemID);
		try
		{
			String oldPath = event.getSuggestedConfigurationFile().getAbsolutePath();
			String newPath = oldPath.substring(0, oldPath.lastIndexOf('\\') + 1) + "TiberiumCraftConfig.xml";
			ConfigLoader.LoadResourceXML(new File(newPath));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		proxy.registerRenderers();
		EntityRegistry.registerModEntity(LightningBolt.class, "LightningBolt", 1, this, 50, 2, true);
		GameRegistry.registerWorldGenerator(new TiberiumWorldGen());
		GameRegistry.registerTileEntity(TiberiumCrystalEntity.class, "TiberiumCrystalTileEntity");
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		//get a list of all blocks by name
//		for(Block b : Block.blocksList)
//		{
//			if(b==null)
//				continue;
//			System.out.format("Block %1$s is %2$s and has unlocalized name %3$s%n", b.blockID, b.getLocalizedName(), b.getUnlocalizedName());
//		}
	}
	
	public static boolean forceUseBlockID()
	{
		return true;
	}
}
