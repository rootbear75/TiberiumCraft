


package tiberiumcraft.common.ResourceConversions.OutputDefs;

public class OutputDefItem extends OutputDef
{
	private String itemName;
	private int itemID;
	private int damage;
	
	public OutputDefItem(int defID, float factor, String itemName, int itemID, int damage)
	{
		super(defID, factor);
		this.itemName = itemName;
		this.itemID = itemID;
		this.damage = damage;
		
	}
	
	public String getItemName()
	{
		return itemName;
	}
	
	public int getItemID()
	{
		return itemID;
	}
	
	public int getItemDamage()
	{
		return damage;
	}
}
