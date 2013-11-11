package tiberiumcraft.common.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.lwjgl.util.vector.Vector3f;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class IonStormEntity extends Entity
{
	// storm private fields. Save to NBT
	private float strength = -1;
	private int stormDurationTicks = 0;
	private List<Vector3f> addStrQue = new ArrayList<Vector3f>();
	public long worldTime = -1;
	private Timer timer;
	
	public IonStormEntity(World par1World)
	{
		super(par1World);
		
		//create a new timer that will remove this object when it becomes unloaded
		//one would think there'd be an onunload event
		timer = new Timer(true);
		timer.schedule(new unregisterOnNoUpdate(this, worldObj), worldObj.rand.nextInt(1000), 5000);
		
		//register this ionstorm to the master list
		IonStormMaster.registerStorm(this);
	}
	
	public Vector3f getPos()
	{
		return new Vector3f((float) posX, (float) posY, (float) posZ);
	}
	
	public synchronized void setPos(Vector3f v)
	{
		posX = v.x;
		posY = v.y;
		posZ = v.z;
	}

	@Override
	protected void entityInit()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onEntityUpdate() 
	{
		if(isDead)
			return;
		if(stormDurationTicks == 0)
			strength = 10;
		if(strength == -1)
		{
			System.out.println("Delaying storm update until NBT load");
			return;
		}
		synchronized(addStrQue)
		{
			while(addStrQue.size() >= 1)
			{
				Vector3f pos = addStrQue.get(0);
				addStrQue.remove(0);
				addStrengthShift(pos);
				
			}
		}
		
		if(strength > 0)
		{
			stormDurationTicks++;
			double rad = StrictMath.pow(strength, 1.0 / 3) * IonStormMaster.radiusPerStrengthDensity;
			// TODO:change count based on strength of storm
			for(int i = 0; i < StrictMath.sqrt(StrictMath.sqrt(rad)); i++)
			{
				int x = (int) (posX + 2 * (worldObj.rand.nextDouble() - .5) * rad);
				int z = (int) (posZ + 2 * (worldObj.rand.nextDouble() - .5) * rad);
				
				if(worldObj != null)
				{
					//System.out.println("Attempting to spawn bolt");
					worldObj.spawnEntityInWorld(new LightningBolt(worldObj, x, worldObj.getHeightValue(x, z), z));
				}
				else
					System.out.println("Could not spawn lightning bolt!");
				
				if(i > 1) break;
			}
			strength -= IonStormMaster.strLossPerTick;
			strength *= 1 - IonStormMaster.strFractionLossTick;
			strength = StrictMath.max(0, strength);
			
		}
	}
	

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag)
	{
		strength = tag.getFloat("Strength");
		stormDurationTicks = tag.getInteger("StormDurationTicks");
		if(strength == 0 && strength != -1)
		{
			kill();
		}
		else
			IonStormMaster.registerStorm(this);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag)
	{
		super.writeMountToNBT(tag);
		tag.setFloat("Strength", strength);
		tag.setInteger("StormDurationTicks", stormDurationTicks);
		
	}
	
	public int addStrQue(Vector3f pos)
	{
		synchronized(addStrQue)
		{
			addStrQue.add(pos);
			return addStrQue.size();
		}
	}
	
	private void addStrengthShift(Vector3f Pos)
	{
		Vector3f temp = new Vector3f(
				(float) (strength * posX + Pos.x), 
				(float) (strength * posY + Pos.y), 
				(float) (strength * posZ + Pos.z));
		temp.scale(1 / (strength + 1));
		setPos(temp);
		
		strength += IonStormMaster.strengthStep;
		
	}
	
	private class unregisterOnNoUpdate extends TimerTask
	{
		IonStormEntity ionStorm;
		World worldObj;

		public unregisterOnNoUpdate(IonStormEntity ionStorm, World worldObj)
		{
			super();
			this.ionStorm = ionStorm;
			this.worldObj = worldObj;
		}


		@Override
		public void run()
		{
			if(worldObj.getLoadedEntityList().contains(ionStorm) && !ionStorm.isDead)
				return;
			IonStormMaster.unregisterStorm(ionStorm);
			ionStorm.killTimer();
		}
		
	}

	public void killTimer()
	{
		synchronized(timer)
		{
			timer.cancel();
		}
		
	}
	
}
