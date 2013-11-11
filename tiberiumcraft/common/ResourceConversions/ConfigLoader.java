


package tiberiumcraft.common.ResourceConversions;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import tiberiumcraft.common.ResourceConversions.Loaders.*;

//All of the code that handles loading the resource configs 
//in from their file to the other classes in this package
public abstract class ConfigLoader
{
	private static boolean overwriteOnError = true;// TODO: load from standard
													// config
	// Lets the program know is the XML has already been copied from internal
	// source
	private static boolean triedOverwrite = false;
	private static File file;
	
	private static List<ConfigLoader> registerLoaders(List<ConfigLoader> loaders)
	{
		loaders.add(new ResourceXMLLoaderV1());
		loaders.add(new ResourceXMLLoaderV2());
		// register versioned loaders here
		
		return loaders;
	}
	
	public static boolean LoadResourceXML(File readFile) throws Exception
	{
		file = readFile;
		List<ConfigLoader> loaders = registerLoaders(new ArrayList<ConfigLoader>());
		
		System.out.println("starting XML loading of resource data");
		Node root;
		Document config;
		if(!file.exists())
		{
			System.out.println("Writting default Config.");
			triedOverwrite = true;
			createFile(file);
		}
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		try
		{
			config = db.parse(file);
		}
		catch(SAXParseException e)
		{
			switch(catchXMLProblem("Error in validating existing XML; attempting to override"))
			{
				case 0:
					return true;
				case 1:
					return false;
				default:
					throw e;
			}
		}
		root = config.getFirstChild();
		if(root == null) throw new XMLParseException("couldn't find XML root");
		String version;
		try
		{
			NamedNodeMap map = root.getAttributes();
			version = map.getNamedItem("version").getNodeValue();
		}
		catch(NullPointerException e)
		{
			switch(catchXMLProblem("No version tag found on XML file; regenerating"))
			{
				case 0:
					return true;
				case 1:
					return false;
				default:
					throw e;
			}
		}
		System.out.println("Version:".concat(version));
		for(ConfigLoader l : loaders)
		{
			if(l.getVersion().equals(version))
			{
				return l.load(config);
			}
			else
			{
				System.out.println("Version of ".concat(l.getVersion()).concat(" did not match."));
			}
		}
		throw new Exception("Unrecognized XML Version");
		
	}
	
	protected abstract boolean load(Document config) throws XMLParseException;
	
	protected abstract String getVersion();
	
	private static void createFile(File file) throws IOException
	{
		URL inputUrl = ConfigLoader.class.getResource("TiberiumCraftConfig.xml");
		System.out.println("Copying first to second:");
		System.out.println(inputUrl.toString());
		System.out.println(file.getAbsolutePath());
		FileUtils.copyURLToFile(inputUrl, file);
		try
		{
			Thread.sleep(1000);
		}
		catch(InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected static List<Node> findNodes(Node parrent, String identifier)
	{
		if(parrent.getNodeType() != Node.ELEMENT_NODE)
		{
			System.err.println("Error: Search node not of element type");
			throw new IllegalArgumentException();
		}
		
		if(!parrent.hasChildNodes()) return null;
		
		List<Node> matches = new ArrayList<Node>();
		NodeList list = parrent.getChildNodes();
		for(int i = 0; i < list.getLength(); i++)
		{
			Node subnode = list.item(i);
			if(subnode.getNodeType() == Node.ELEMENT_NODE) if(subnode.getNodeName().equals(identifier)) matches.add(subnode);
		}
		return matches;
	}
	
	protected static Node findNode(Node node, String tag) throws XMLParseException
	{
		if(node.getNodeType() != Node.ELEMENT_NODE) throw new XMLParseException("Error: Search node not of element type");
		
		if(!node.hasChildNodes()) return null;
		
		NodeList list = node.getChildNodes();
		for(int i = 0; i < list.getLength(); i++)
		{
			Node subnode = list.item(i);
			if(subnode.getNodeType() == Node.ELEMENT_NODE) if(subnode.getNodeName().equals(tag)) return subnode;
		}
		return null;
	}
	
	private static int catchXMLProblem(String consoleMessage) throws Exception
	{
		if(triedOverwrite || !overwriteOnError)
		{
			if(triedOverwrite)
				System.out.println("XML file in JAR is corrupt! Bailing out!");
			else
				System.out.println("Overwrite on XML error is disabled; ".concat("you'll have to fix your own config file :P"));
			return 2;
		}
		else
		{
			System.out.println(consoleMessage);
			triedOverwrite = true;
			try
			{
				createFile(file);
			}
			catch(IOException e)
			{
				System.out.println("Override failed");
				throw e;
			}
			
			if(LoadResourceXML(file))
				return 0;
			else
				return 1;
		}
	}
	
}
