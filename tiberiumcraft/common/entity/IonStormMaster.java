package tiberiumcraft.common.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.World;

import org.lwjgl.util.vector.Vector3f;

public class IonStormMaster
{
	// list of all Ion Storms
	private static List<IonStormEntity> storms = new ArrayList<IonStormEntity>();
	
	// config globals; do not change; consider marking with final
	public static final float strengthStep = 5;
	public static final float radiusPerStrengthDensity = 1;
	public static final float strLossPerTick = 1F;
	public static final float strFractionLossTick = 0.01F;
	public static final float addRadius = 50;
	
	public static void CreateOrMergeStorm(World worldObj, Vector3f Pos)
	{
		if(worldObj == null)
		{
			//System.out.println("World Object null on storm creation");
			return;
		}
		validateList();
		float dist = addRadius * 2;
		IonStormEntity closest = null;
		synchronized(storms)
		{
			//System.out.println(Pos);
			for(IonStormEntity s : storms)
			{
				Vector3f temp = new Vector3f();
				Vector3f.sub(s.getPos(), Pos, temp);
				
				if(temp.length() < dist)
				{
					dist = temp.length();
					closest = s;
				}
				
			}
		}
		if(dist <= addRadius && closest != null)
		{
			synchronized(closest.worldObj.loadedEntityList)
			{
				if(!closest.worldObj.loadedEntityList.contains(closest))
				{
					unregisterStorm(closest);
					CreateOrMergeStorm(worldObj, Pos);
				}
			}

			@SuppressWarnings("unused")
			int count = closest.addStrQue(Pos);
			//System.out.println("added storm strength; there are " + String.valueOf(storms.size()) + " storms." + count);
			// any other stuff to the existing storm
		}
		else
		{
			if(closest == null)
			{
				//System.out.println("closest was null; making new storm");
			}
			else
			{
				//System.out.println(dist);
				//System.out.println(closest.getPos());
			}
			IonStormEntity storm = new IonStormEntity(worldObj);
			storm.setPos(Pos);
			
		}
		
	}
	
	public static boolean unregisterStorm(IonStormEntity closest)
	{
		synchronized(storms)
		{
			return storms.remove(closest);
		}
		
	}

	public static void registerStorm(IonStormEntity e)
	{
		validateList();
		synchronized(storms)
		{
			if(!storms.contains(e))
				storms.add(e);
			//System.out.println("added storm; there are now " + String.valueOf(storms.size()) + " storms.");
		}
		e.worldObj.spawnEntityInWorld(e);
		
		
	}
	
	private static boolean validateList()
	{
		if(storms == null)
		{
			storms = new ArrayList<IonStormEntity>();
		}
		synchronized(storms)
		{

			boolean listModified = false;
			while(storms.contains(null))
			{
				storms.remove(null);
			}
			List<IonStormEntity> toRemove = new ArrayList<IonStormEntity>();
			for(IonStormEntity s : storms)
			{
				if(s.worldTime == -1)
					continue;
				long diff = s.worldObj.getTotalWorldTime() - s.worldTime;
				if(diff > 5)
					toRemove.add(s);
				synchronized(s.worldObj.loadedEntityList)
				{
					if(!s.worldObj.loadedEntityList.contains(s))
					{
						toRemove.add(s);
					}
				}
			}
			for(IonStormEntity s : toRemove)
			{
				listModified = listModified || storms.remove(s);
			}
			return listModified;
		}

	}
	

}
