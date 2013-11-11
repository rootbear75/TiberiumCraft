package tiberiumcraft.common;

import java.io.File;
import net.minecraftforge.common.Configuration;

public class Config
{
	
	static Configuration config;
	
	public static int tiberiumCrystalItemID,
					  tiberiumGunID,
					  tiberiumCrystalBlockID,
					  tiberiumLogBlockID;
	
	public static float placementRadius = 8F,
						spreadFraction = .10F,
						spreadCost = .15F,
						absorbRadius = 35;
	
	public static int growthTimeMin = 200,
					  growthTimeAdd = 5800,
					  spreadUpdatesMin = 20,
					  spreadUpdatesAdd = 100;

	public static int strikesPerTick = 5,
					  spreadRadius = 50;
	
	public static boolean debug = false;

	public static int debugStaffItemID = 1337;

	
	public static void Load(File file)
	{
		config = new Configuration(file);
		
		try
		{
			config.load();
			tiberiumCrystalItemID = config.getItem("IDs",  				"Tiberium Crystal Item ID", 5001).getInt();
			tiberiumGunID = config.getItem("IDs",  						"Tiberium Gun Item ID", 5002).getInt();
			tiberiumCrystalBlockID = config.getBlock("IDs", 			"Tiberium Crystal Block ID", 500).getInt();
			tiberiumLogBlockID = config.getBlock("IDs", 				"Tree Trunk ID", 501).getInt();
																		// TODO: Add proper names
			growthTimeMin = (int) config.get("Tiberium Properties", 	"growthTimeMin",	 growthTimeMin).getDouble(growthTimeMin);
			growthTimeAdd = (int) config.get("Tiberium Properties", 	"growthTimeAdd", 	 growthTimeAdd).getDouble(growthTimeAdd);
			spreadUpdatesMin = (int) config.get("Tiberium Properties", 	"spreadUpdatesMin",  spreadUpdatesMin).getDouble(spreadUpdatesMin);
			spreadUpdatesAdd = (int) config.get("Tiberium Properties", 	"spreadUpdatesAdd",  spreadUpdatesAdd).getDouble(spreadUpdatesAdd);
			placementRadius = (float) config.get("Tiberium Properties", "placementRadius",	 placementRadius).getDouble(placementRadius);
			spreadFraction = (float) config.get("Tiberium Properties", 	"spreadFraction",	 spreadFraction).getDouble(spreadFraction);
			spreadCost = (float) config.get("Tiberium Properties", 		"spreadCost",		 spreadCost).getDouble(spreadCost);
			absorbRadius = (float) config.get("Tiberium Properties", 	"absorbRadius",		 absorbRadius).getDouble(absorbRadius);

			strikesPerTick = config.get("IonStorm", 					"strikesPerTick", 	strikesPerTick).getInt(strikesPerTick);
			spreadRadius = config.get("IonStorm", 					"spreadRadius", 	spreadRadius).getInt(spreadRadius);
			
			debug = config.get("Debugging", "setDevMode", false).getBoolean(debug);
			debugStaffItemID = config.get("Debugging", "debugStaffItemID", 1337).getInt(1337);
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
		finally
		{
			config.save();
		}
	}
	
}
