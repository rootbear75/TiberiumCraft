


package tiberiumcraft.client;

import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.client.registry.RenderingRegistry;
import tiberiumcraft.client.render.RenderLightningBolt;
import tiberiumcraft.client.render.RenderTiberiumGun;
import tiberiumcraft.common.CommonProxy;
import tiberiumcraft.common.TiberiumCraft;
import tiberiumcraft.common.entity.LightningBolt;

public class ClientProxy extends CommonProxy
{
	
	@Override
	public void registerRenderers()
	{
		MinecraftForgeClient.registerItemRenderer(TiberiumCraft.instance.tiberiumGun.itemID, new RenderTiberiumGun());
		RenderingRegistry.registerEntityRenderingHandler(LightningBolt.class, new RenderLightningBolt());
	}
	
}
