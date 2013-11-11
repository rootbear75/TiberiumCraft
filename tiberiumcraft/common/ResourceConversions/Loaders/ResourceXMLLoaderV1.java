


package tiberiumcraft.common.ResourceConversions.Loaders;


import java.util.ArrayList;
import java.util.List;

import javax.management.modelmbean.XMLParseException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import tiberiumcraft.common.ResourceConversions.BlockValueDef;
import tiberiumcraft.common.ResourceConversions.ConfigLoader;
import tiberiumcraft.common.ResourceConversions.ConversionDef;
import tiberiumcraft.common.ResourceConversions.ResourceDef;
import tiberiumcraft.common.ResourceConversions.Lists.BlockDef;
import tiberiumcraft.common.ResourceConversions.OutputDefs.OutputDef;

public class ResourceXMLLoaderV1 extends ConfigLoader
{
	private static final String versionID = "1";
	
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
			System.out.println("Loaded ResourceDef");
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
			new BlockValueDef(new BlockDef(Integer.parseInt(map.getNamedItem("minecraftID").getNodeValue())), conversions);
			// new
			// BlockValueDef(Integer.parseInt(map.getNamedItem("resDefID").getNodeValue()),
			// map.getNamedItem("resDefID").getNodeValue(),
			// Integer.parseInt(map.getNamedItem("resDefID").getNodeValue()));
		}
		return true;
	}
	
}
