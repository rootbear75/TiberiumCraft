


package tiberiumcraft.common.ResourceConversions.OutputDefs;


import java.util.ArrayList;
import java.util.List;

//Defines the item that ResourceDef corresponds to
//and provides options for scaling the output
public abstract class OutputDef
{
	public float getConversionFactor()
	{
		return factor;
	}
	
	public int getDefID()
	{
		return defID;
	}
	
	private static List<OutputDef> defs = new ArrayList<OutputDef>();
	
	public OutputDef(int defID, float factor)
	{
		this.defID = defID;
		this.factor = factor;
	}
	
	public static OutputDef Create(String outputName, String outputID, int defID, float conversionFactor)
	{
		OutputDef temp;
		if(outputID == "-1")
		{
			String[] name = outputName.split(":");
			switch(name[0])
			{
				case "OreDictionary":
				{
					temp = new OutputDefOreDictionary(defID, conversionFactor, name[1]);
					break;
				}
				case "LiquidRegistry":
				{
					temp = new OutputDefLiquid(defID, conversionFactor);
					break;
				}
				case "Internal":
				{
					temp = new OutputDefCustom(defID, conversionFactor);
					break;
				}
				default:
					throw new IllegalArgumentException("Expected to find string match; invalid OutputDef");
			}
			
		}
		else
		{
			String[] itemIdAndDamage = outputID.split(":");
			temp = new OutputDefItem(defID, conversionFactor, outputName, Integer.parseInt(itemIdAndDamage[0]),
					itemIdAndDamage.length == 2 ? Integer.parseInt(itemIdAndDamage[1]) : 0);
		}
		defs.add(temp);
		return temp;
	}
	
	public OutputDef[] getOutputDefs()
	{
		OutputDef[] out = new OutputDef[defs.size()];
		defs.toArray(out);
		return out;
	}
	
	public static OutputDef getByIndex(int i)
	{
		for(OutputDef d : defs)
		{
			if(d.defID == i) return d;
		}
		return null;
	}
	
	public int defID;
	public float factor;
	
}
