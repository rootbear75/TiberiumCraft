package tiberiumcraft.common.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.effect.EntityWeatherEffect;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class LightningBolt extends EntityWeatherEffect
{
    /**
     * Declares which state the lightning bolt is in. Whether it's in the air, hit the ground, etc.
     */
    private int lightningState;

    /**
     * A random long that is used to change the vertex of the lightning rendered in RenderLightningBolt
     */
    public long boltVertex;

    /**
     * Determines the time before the EntityLightningBolt is destroyed. It is a random integer decremented over time.
     */
    private int boltLivingTime;

    public LightningBolt(World world)
    {
    	super(world);
    }
    
    public LightningBolt(World world, double x, double y, double z)
    {
        super(world);
        this.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
        this.lightningState = 2;
        this.boltVertex = this.rand.nextLong();
        this.boltLivingTime = this.rand.nextInt(3) + 1;

        if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doFireTick") && world.difficultySetting >= 2 && world.doChunksNearChunkExist(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z), 10))
        {
            int i = MathHelper.floor_double(x);
            int j = MathHelper.floor_double(y);
            int k = MathHelper.floor_double(z);

            if (world.getBlockId(i, j, k) == 0 && Block.fire.canPlaceBlockAt(world, i, j, k))
            {
                world.setBlock(i, j, k, Block.fire.blockID);
            }

            for (i = 0; i < 4; ++i)
            {
                j = MathHelper.floor_double(x) + this.rand.nextInt(3) - 1;
                k = MathHelper.floor_double(y) + this.rand.nextInt(3) - 1;
                int l = MathHelper.floor_double(z) + this.rand.nextInt(3) - 1;

                if (world.getBlockId(j, k, l) == 0 && Block.fire.canPlaceBlockAt(world, j, k, l))
                {
                    if(worldObj.rand.nextDouble() < .15)
                    	world.setBlock(j, k, l, Block.fire.blockID);
                }
            }
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (this.lightningState == 2)
        {
            //this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "ambient.weather.thunder", 10000.0F, 0.8F + this.rand.nextFloat() * 0.2F);
            //this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "random.explode", 2.0F, 0.5F + this.rand.nextFloat() * 0.2F);
        }

        --this.lightningState;

        if (this.lightningState < 0)
        {
            if (this.boltLivingTime == 0)
            {
                this.setDead();
            }
            else if (this.lightningState < -this.rand.nextInt(10))
            {
                --this.boltLivingTime;
                this.lightningState = 1;
                this.boltVertex = this.rand.nextLong();

                if (!this.worldObj.isRemote && this.worldObj.getGameRules().getGameRuleBooleanValue("doFireTick") && this.worldObj.doChunksNearChunkExist(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 10))
                {
                    int i = MathHelper.floor_double(this.posX);
                    int j = MathHelper.floor_double(this.posY);
                    int k = MathHelper.floor_double(this.posZ);

                    if (this.worldObj.getBlockId(i, j, k) == 0 && Block.fire.canPlaceBlockAt(this.worldObj, i, j, k))
                    {
                        this.worldObj.setBlock(i, j, k, Block.fire.blockID);
                    }
                }
            }
        }

        if (this.lightningState >= 0)
        {
            if (this.worldObj.isRemote)
            {
                this.worldObj.lastLightningBolt = 2;
            }
        }
    }

    protected void entityInit() {}

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {}

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {}
}
