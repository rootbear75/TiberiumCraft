


package tiberiumcraft.common.ResourceConversions;

public class ConversionDef
{
	// Defines the conversion of a block absorbtion into the intermediate
	// Tiberium storage values.
	public ResourceDef getResType()
	{
		return resType;
	}
	
	public float getAmount()
	{
		return amount;
	}
	
	public ConversionDef(ResourceDef resType, float amount)
	{
		super();
		this.resType = resType;
		this.amount = amount;
	}
	
	ResourceDef resType;
	float amount;
}
