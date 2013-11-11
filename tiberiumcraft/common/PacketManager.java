package tiberiumcraft.common;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import tiberiumcraft.common.entity.TiberiumCrystalEntity;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketManager implements IPacketHandler
{
	
	@Override//move case handling into relevant classes
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		ByteArrayDataInput data = ByteStreams.newDataInput(packet.data);
		int packetID = data.readInt();
		switch(packetID)
		{
			case 1:
				{
					int x = data.readInt();
					int y = data.readInt();
					int z = data.readInt();
					int dim = data.readInt();
					try
					{
						TileEntity te = DimensionManager.getWorld(dim).getBlockTileEntity(x, y, z);
						if(te instanceof TiberiumCrystalEntity)
						{
							TiberiumCrystalEntity tce = (TiberiumCrystalEntity)te;
							tce.ReadGrowthPacket(data);//pass the packet data to the tile entity to deal with
						}
						else
						{
							//notify about network error
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					break;
				}
		}
	}
		
}
