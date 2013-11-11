


package tiberiumcraft.common.ResourceConversions.Lists;


import java.util.ArrayList;
import java.util.List;

public class BlockList
{
	public static BlockList replaceWhitelist;
	public static BlockList placeOnWhitelist;
	public static BlockList placeOnBlacklist;
	
	protected List<BlockDef> list;
	
	public BlockList()
	{
		list = new ArrayList<BlockDef>();
	}
	
	public void addBlock(BlockDef bd)
	{
		if(list.contains(bd)) return;
		list.add(bd);
	}
	
	protected boolean removeBlock(BlockDef bd)
	{
		return list.remove(bd);
	}
	
	public void mergeAdd(BlockList blockList)
	{
		for(BlockDef b : blockList.getArray())
		{
			addBlock(b);
		}
	}
	
	public void mergeSub(BlockList blockList)
	{
		for(BlockDef b : blockList.getArray())
		{
			removeBlock(b);
		}
	}
	
	public BlockDef[] getArray()
	{
		BlockDef[] temp = new BlockDef[list.size()];
		list.toArray(temp);
		return temp;
	}
	
	public boolean isOnList(BlockDef b)
	{
		for(BlockDef bd : list)
		{
			if(bd.match(b)) return true;
		}
		return false;
	}
}
