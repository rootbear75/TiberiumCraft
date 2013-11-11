


package tiberiumcraft.common.ResourceConversions.Loaders;


import java.util.ArrayList;
import java.util.List;

import javax.management.modelmbean.XMLParseException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import tiberiumcraft.common.TiberiumCraft;
import tiberiumcraft.common.ResourceConversions.BlockValueDef;
import tiberiumcraft.common.ResourceConversions.ConfigLoader;
import tiberiumcraft.common.ResourceConversions.ConversionDef;
import tiberiumcraft.common.ResourceConversions.ResourceDef;
import tiberiumcraft.common.ResourceConversions.Lists.BlockDef;
import tiberiumcraft.common.ResourceConversions.Lists.BlockList;
import tiberiumcraft.common.ResourceConversions.OutputDefs.OutputDef;

public class ResourceXMLLoaderV2 extends ConfigLoader
{
	private static final String versionID = "2";
	
	@Override
	protected String getVersion()
	{
		return versionID;
	}
	
	@Override
	protected boolean load(Document config) throws XMLParseException
	{
		Node root = config.getFirstChild();
		Node resDefs = findNode(root, "ResDefs");
		Node outDefs = findNode(root, "OutDefs");
		Node blockDefs = findNode(root, "BlockValues");
		Node wbLists = findNode(root, "WhiteAndBlackLists");
		
		System.out.println("XML loader starting OutDefs");
		for(Node n : findNodes(outDefs, "OutputDef"))
		{
			NamedNodeMap map = n.getAttributes();
			float conversion;
			try
			{
				conversion = Float.parseFloat(map.getNamedItem("conversion").getNodeValue());
			}
			catch(NullPointerException e)
			{
				conversion = 1.0F;
			}
			
			OutputDef.Create(map.getNamedItem("minecraftItemName").getNodeValue(), map.getNamedItem("minecraftID").getNodeValue(),
					Integer.parseInt(map.getNamedItem("defID").getNodeValue()), conversion);
		}
		
		System.out.println("XML loader starting ResDefs");
		for(Node n : findNodes(resDefs, "ResourceDef"))
		{
			NamedNodeMap map = n.getAttributes();
			if(map == null)
			{
				System.out.println("null node map; skipping loop");
				continue;
			}
			String list = map.getNamedItem("outDefList").getNodeValue();
			String[] commaSepRefList =
				{list};
			if(list.contains(",")) commaSepRefList = list.split(",");
			int[] refs = new int[commaSepRefList.length];
			for(int i = 0; i < commaSepRefList.length; i++)
			{
				refs[i] = Integer.parseInt(commaSepRefList[i]);
			}
			new ResourceDef(map.getNamedItem("internalName").getNodeValue(), map.getNamedItem("displayName").getNodeValue(), Integer.parseInt(map.getNamedItem(
					"defID").getNodeValue()), refs);
			// System.out.println("Loaded ResourceDef");
		}
		
		System.out.println("XML loader starting BlockValues");
		for(Node block : findNodes(blockDefs, "Block"))
		{
			List<ConversionDef> conversions = new ArrayList<ConversionDef>();
			for(Node n : findNodes(block, "ResourceContnet"))
			{
				NamedNodeMap map = n.getAttributes();
				conversions.add(new ConversionDef(ResourceDef.getByIndex(Integer.parseInt(map.getNamedItem("resDefID").getNodeValue())), Float.parseFloat(map
						.getNamedItem("absorbValue").getNodeValue())));
			}
			NamedNodeMap map = block.getAttributes();
			new BlockValueDef(new BlockDef(map.getNamedItem("minecraftName").getNodeValue(), 
					map.getNamedItem("minecraftID").getNodeValue()), conversions);
		}
		
		System.out.println("XML loader starting whitelist blacklist section");
		Node listReplaceable = findNode(wbLists, "Replacable");
		Node listPlaceTrans = findNode(wbLists, "PlaceOnTopTransparent");
		Node listPlaceSolid = findNode(wbLists, "PlaceOnTopSolid");
		
		BlockList.replaceWhitelist = loadBlockList(listReplaceable);
		BlockList.placeOnWhitelist = loadBlockList(listPlaceTrans);
		BlockList.placeOnBlacklist = loadBlockList(listPlaceSolid);
		BlockList.placeOnBlacklist.addBlock(new BlockDef(TiberiumCraft.instance.tiberiumCrystalBlock));
		BlockList.placeOnBlacklist.addBlock(new BlockDef(TiberiumCraft.instance.tiberiumLogBlock));
		
		return true;
	}
	
	private BlockList loadBlockList(Node node) throws XMLParseException
	{
		BlockList myList = new BlockList();
		for(Node n : findNodes(node, "LBlock"))
		{
			NamedNodeMap map = n.getAttributes();
			String sID = map.getNamedItem("minecraftID").getNodeValue();
			String[] parts = sID.split(":");
			BlockDef bd;
			switch(parts.length)
			{
				case 0:
				{
					throw new XMLParseException("Expected non-blank minecraft ID");
				}
				case 1:
				{
					bd = new BlockDef(Integer.parseInt(parts[0]));
					break;
				}
				case 2:
				{
					bd = new BlockDef(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
					break;
				}
				default:
				{
					throw new XMLParseException("Why u do dis?  Found 2 or more ':'s in a minecraft ID");
				}
			}
			myList.addBlock(bd);
		}
		return myList;
	}
	
}
