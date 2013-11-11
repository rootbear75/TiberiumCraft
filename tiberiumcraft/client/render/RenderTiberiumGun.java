package tiberiumcraft.client.render;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class RenderTiberiumGun implements IItemRenderer
{
	
	IModelCustom gunModel = AdvancedModelLoader.loadModel("/assets/tiberiumcraft/models/TiberiumGun.obj");
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}
	
	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return type == ItemRenderType.ENTITY || type == ItemRenderType.INVENTORY;
	}
	
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		
        GL11.glPushMatrix();
        GL11.glScalef(.025f, .025f, .025f);
        switch ( type )
        {
		case ENTITY:
	        GL11.glScalef(1f, 1f, 1f);
			break;
		case EQUIPPED:
			GL11.glRotatef(-90f, 0f, 1f, 0f);
			GL11.glRotatef(45f, 0f, 0f, 0f);
			GL11.glTranslatef(0f, 0f, -.5f);
			break;
		case EQUIPPED_FIRST_PERSON:
			GL11.glRotatef(25f, 0f, 0f, 1f);
			GL11.glTranslatef(1f, 0f, 0f);
			break;
		case FIRST_PERSON_MAP:
			break;
		case INVENTORY:
			break;
		default:
			break;
        }
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation("tiberiumcraft:models/TiberiumGun.png"));
        gunModel.renderAll();
        GL11.glPopMatrix();
	}
	
}
