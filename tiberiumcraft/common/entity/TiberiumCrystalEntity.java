


package tiberiumcraft.common.entity;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.io.ByteArrayDataInput;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Quaternion;

import tiberiumcraft.common.Config;
import tiberiumcraft.common.TiberiumCraft;
import tiberiumcraft.common.ResourceConversions.BlockValueDef;
import tiberiumcraft.common.ResourceConversions.ConversionDef;
import tiberiumcraft.common.ResourceConversions.TBResource;
import tiberiumcraft.util.QuaternionUtil;
import tiberiumcraft.util.TiberiumUtility;
import tiberiumcraft.util.Vector3fUtil;

public class TiberiumCrystalEntity extends TileEntity
{
	public static boolean loaded = false;
	public static int growthTimeMin;
	public static int growthTimeAdd;
	public static int spreadUpdatesMin;
	public static int spreadUpdatesAdd;
	public static float placementRadius;
	public static float spreadFraction;
	public static float spreadCost;
	public static float absorbRadius;
	public int updateInTicks = 1000000;
	public int spreadInUpdates = 1000;
	
	List<TBResource> resources = new ArrayList<TBResource>();
	private static Random random;
	
	public TiberiumCrystalEntity()
	{
		super();
		for(Field f : getClass().getFields())
		{
			if(loaded) break;
			try
			{
				if(Modifier.isStatic(f.getModifiers()) && !f.getName().equals("loaded") && !f.getName().equals("INFINITE_EXTENT_AABB"))
				{
					System.out.println("loading field with name " + f.getName());
					System.out.println("field is of type " + f.getType().getName());
					Field siblingField = Config.class.getField(f.getName());
					f.set(this, siblingField.get(siblingField));
					System.out.println("succesfully loaded " + f.getDouble(f));
				}
			}
			catch(NoSuchFieldException e)
			{
				System.out.println("failed to find above field in config file; did someone forget to add it to the default config?");
			}
			catch(SecurityException e)
			{
				e.printStackTrace();
			}
			catch(IllegalArgumentException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch(IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(!loaded)
		{
			loaded = true;
			System.out.println(growthTimeMin);
			System.out.println(growthTimeAdd);
			System.out.println(spreadUpdatesMin);
			System.out.println(spreadUpdatesAdd);
		}
		if(growthTimeMin < 20 || spreadUpdatesMin <= 1)
		{
			
			System.out.println("Failed to load sane config values; overriding with non-game-crashing numbers");
			growthTimeMin = 1000;// 10 seconds
			spreadUpdatesMin = 5;// 50 seconds
		}
		if(random == null)
		{
			random = new Random();
		}
		
		updateInTicks = (int) (growthTimeMin + growthTimeAdd * StrictMath.pow(random.nextDouble(), 1.5));
		spreadInUpdates = (int) (spreadUpdatesMin + spreadUpdatesAdd * StrictMath.pow(random.nextDouble(), 1.5));
		if(updateInTicks < 20 || spreadInUpdates < 1)
		{
			System.out.format("I don't even know how you broke it this badly...%n " + "something horible happened in TiberiumCrystalEntity at <%1,%2,%3>%n",
					xCoord, yCoord, zCoord);
			updateInTicks = 1000000;
			spreadInUpdates = 1000000;
		}
		
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if(!worldObj.isRemote)
		{
			if(updateInTicks-- > 0) return;
			
			updateInTicks = 200;
			
			if(worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == null) return;
			
			IonStormMaster.CreateOrMergeStorm(this.worldObj, new Vector3f(xCoord, 0, zCoord));
			
			updateInTicks = (int) (growthTimeMin + growthTimeAdd * StrictMath.pow(worldObj.rand.nextDouble(), 2));
			
			// select a random block in absorb radius
			Quaternion rot = QuaternionUtil.random(worldObj.rand.nextDouble(), worldObj.rand.nextDouble(), worldObj.rand.nextDouble());
			Vector3f vect = QuaternionUtil.rotate(rot, Vector3fUtil.unitX);
			vect.scale(absorbRadius * (float) (1 - StrictMath.sqrt(worldObj.rand.nextDouble())));
			
			
			int x = xCoord + (int) (vect.x + .5F);
			int y = yCoord + (int) (vect.y + .5F);
			int z = zCoord + (int) (vect.z + .5F);

			
			int blockID = worldObj.getBlockId(x, y, z);
			// int blockMeta = worldObj.getBlockMetadata(x, y, z);
			// Block block = Block.blocksList[blockID];
			BlockValueDef blockVal = BlockValueDef.lookup(blockID);
			if(blockVal != null)
			{
				// if it is on whitelist, add the resource value to this block's
				// resource list
				for(ConversionDef cd : blockVal.getAllConversions())
				{
					int i = resources.indexOf(cd.getResType());
					if(i != -1)
					{
						resources.get(i).amount += cd.getAmount();
					}
					else
					{
						resources.add(new TBResource(cd.getResType(), cd.getAmount()));
					}
				}
				// roll for a chance to destroy the absorbed block
				if(worldObj.rand.nextDouble() < .15)
				{
					// replace harvested block with depleted version
				}
			}
			
			//System.out.println("Spreading in "+spreadInUpdates+" updates");
			if(spreadInUpdates-- > 0) return;
			System.out.println("calling doSpread");
			doSpread();
		}
	}
	
	public void debugForceUpdate()
	{
		if(TiberiumCraft.isDebug)
		{
			updateInTicks = 0;
			spreadInUpdates = 0;
		}
		else
		{
			System.out.println("Can't use debug commands in non debug mode!");
		}
	}
	
	private void doSpread()
	{
		System.out.println("Attempting Spread");
		spreadInUpdates = (int) (spreadUpdatesMin + spreadUpdatesAdd * StrictMath.pow(worldObj.rand.nextDouble(), 1.5));
		
		// select a random block in placement radius		
		double theta = 2 * StrictMath.PI * worldObj.rand.nextDouble();
		int x = xCoord + (int) (placementRadius * StrictMath.cos(theta));
		int y = yCoord + (int) (2 * placementRadius * (worldObj.rand.nextDouble() - 0.5));
		int z = zCoord + (int) (placementRadius * StrictMath.sin(theta));
		y = TiberiumUtility.getValidHeight(worldObj, x, z, y, placementRadius / 2);
		if(y != -1)
			spreadTo(x, y, z);
		else
			System.out.println("Failed spread attempt at ".concat(Vector3fUtil.v3fToString(new Vector3f(x, y, z))));
	}
	
	private void spreadTo(int x, int y, int z)
	{
		System.out.println("Spread successful at ".concat(Vector3fUtil.v3fToString(new Vector3f(x, y, z))));
		worldObj.setBlock(x, y, z, worldObj.getBlockId(this.xCoord, yCoord, zCoord));
		
		try
		{
			TiberiumCrystalEntity tce = null;
			while(tce == null)
			{
				tce = ((TiberiumCrystalEntity) worldObj.getBlockTileEntity(x, y, z));
				Thread.sleep(100);
			}
			tce.acceptResources(scaledResources(spreadFraction));
			resources = scaledResources(1 - spreadCost);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public NBTTagCompound getNBTTag()
	{
		NBTTagCompound tag = new NBTTagCompound();
		super.readFromNBT(tag);
		return tag;
		
	}
	
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		resources = TBResource.readListFromTag(tag);
	}
	
	/**
	 * Writes a tile entity to NBT.
	 */
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setCompoundTag("Resources", TBResource.writeListToTag(resources));
	}
	
	@SuppressWarnings("unused")
	private Packet CreateGrowthPacket()
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		
		try
		{
			dos.writeInt(0);
			
			dos.writeInt(xCoord);
			dos.writeInt(yCoord);
			dos.writeInt(zCoord);
			dos.writeInt(worldObj.provider.dimensionId);
			
			// dos.write(); //write update info
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Packet250CustomPayload pkt = new Packet250CustomPayload();
		pkt.channel = "TiberiumCraft";
		pkt.data = bos.toByteArray();
		pkt.length = bos.size();
		pkt.isChunkDataPacket = true;
		return pkt;
	}
	
	public void ReadGrowthPacket(ByteArrayDataInput data)
	{
		
	}
	
	public List<TBResource> getCurrentResources()
	{
		return resources;
	}
	
	private List<TBResource> scaledResources(float factor)
	{
		List<TBResource> tempList = new ArrayList<TBResource>();
		for(int i = 0; i < resources.size(); i++)
		{
			TBResource temp = resources.get(i);
			temp.amount *= factor;
			tempList.add(i, temp);
		}
		return tempList;
	}
	
	public void acceptResources(List<TBResource> newResources)
	{
		boolean found;
		for(TBResource nres : newResources)
		{
			found = false;
			for(int i = 0; i < resources.size(); i++)
			{
				if(resources.get(i).resource == nres.resource)
				{
					TBResource temp = resources.get(i);
					temp.amount += StrictMath.max(0, nres.amount);
					resources.set(i, temp);
					found = true;
					break;
				}
				
			}
			if(!found) resources.add(nres);
		}
	}
	
	public void logDebugInfoToStream(PrintStream stream)
	{
		stream.println("Logging debug info for TiberiumCrystal at:");
		stream.println("<".concat(Double.toString(xCoord)).concat(",").concat(Double.toString(yCoord)).concat(",").concat(Double.toString(zCoord)).concat(">"));
		stream.println("Update in ticks: ".concat(Integer.toString(updateInTicks)));
		stream.println("Spread in updates:".concat(Integer.toString(spreadInUpdates)));
		stream.println("Crystal contains: " + resources.size());
		for(TBResource tbr : resources)
		{
			stream.println(tbr.toString());
		}
		stream.println("End crystal debug info");
		
	}
}
