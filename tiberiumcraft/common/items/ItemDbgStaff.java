


package tiberiumcraft.common.items;


import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import tiberiumcraft.common.TiberiumCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;

//NOTE: github does a wildcard search for the word "debug" in filenames and classes; hence dbg
public class ItemDbgStaff extends Item
{
	long time;
	public ItemDbgStaff(int ID)
	{
		super(ID);
		setUnlocalizedName("timeriumcraft.DebugStaff");
		iconString = "tiberiumcraft:DebugStaff";
		setCreativeTab(TiberiumCraft.instance.tabsTiberiumCraft);
		this.setMaxDamage(30);
		GameRegistry.registerItem(this, "DebugStaff");
		LanguageRegistry.addName(this, "FireStorm's Grand Staff of Debugging");
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
	{
		long diff = StrictMath.abs(System.currentTimeMillis() - time);
		if(!entityPlayer.isSneaking())
			time = System.currentTimeMillis();
		if(world.isRemote && diff < 600 && entityPlayer.isSneaking())
		{
			if(Minecraft.getMinecraft().isSingleplayer())
			{
				TiberiumCraft.isDebug = !TiberiumCraft.isDebug;
				
				System.out.format("Debug is now ".concat(TiberiumCraft.isDebug ? "active" : "inactive"));
				
				ChatMessageComponent chat = ChatMessageComponent.createFromText(
						"Debug is now ".concat(TiberiumCraft.isDebug ? "active" : "inactive"));
				entityPlayer.sendChatToPlayer(chat);
			}
			else
			{
				EntityPlayerMP mpPlayer = (EntityPlayerMP)entityPlayer;
				if(MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(mpPlayer.username))
				{
					TiberiumCraft.isDebug = !TiberiumCraft.isDebug;
					
					System.out.format("Debug is now ".concat(TiberiumCraft.isDebug ? "active" : "inactive"));
					
					ChatMessageComponent chat = ChatMessageComponent.createFromText(
							"Debug is now ".concat(TiberiumCraft.isDebug ? "active" : "inactive"));
					entityPlayer.sendChatToPlayer(chat);
				}
			}
			time -= 9001;
		}
		
		return super.onItemRightClick(itemStack, world, entityPlayer);
	}
	
	public static boolean isDebugTool(ItemStack stack)
	{
		if(stack != null)//player's hand is not empty
		{
			Item currentItem = stack.getItem();//get the Item referenced by the ItemStack
			//Assuming that the Item isn't null, check if it is the debug tool, force the tce to update;
			if(currentItem != null && (currentItem instanceof ItemDbgStaff)) return true;
		}
		return false;
	}
	
	public static ItemDbgStaff getMainInstance()
	{
		return (ItemDbgStaff)TiberiumCraft.instance.debugStaff;
	}
	
}
