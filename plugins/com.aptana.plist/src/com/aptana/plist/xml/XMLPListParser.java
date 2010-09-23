package com.aptana.plist.xml;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.internal.preferences.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.aptana.plist.IPListParser;
import com.aptana.plist.PListPlugin;

public class XMLPListParser implements IPListParser
{

	private static final String ISO_8601_DATETIME = "yyyy-MM-dd'T'HH:mm:ss'Z'"; //$NON-NLS-1$

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> parse(File file) throws IOException
	{
		try
		{
			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			Element doc = d.getDocumentElement();
			Node root = doc.getFirstChild().getNextSibling();
			return (Map<String, Object>) parseNode((Element) root);
		}
		catch (Exception e)
		{
			PListPlugin.logError(e);
		}
		return Collections.emptyMap();
	}

	private Object parseNode(Element node)
	{
		String childName = node.getTagName();
		if (childName.equals("string"))
		{
			return node.getTextContent();
		}
		else if (childName.equals("real"))
		{
			return Float.parseFloat(node.getTextContent());
		}
		else if (childName.equals("integer"))
		{
			return Long.parseLong(node.getTextContent());
		}
		else if (childName.equals("true"))
		{
			return Boolean.TRUE;
		}
		else if (childName.equals("false"))
		{
			return Boolean.FALSE;
		}
		else if (childName.equals("date"))
		{
			try
			{
				// ISO 8601 format text
				String raw = node.getTextContent();
				SimpleDateFormat df = new SimpleDateFormat(ISO_8601_DATETIME);
				TimeZone tz = TimeZone.getTimeZone("UTC");
				df.setTimeZone(tz);
				return df.parse(raw);
			}
			catch (Exception e)
			{
				PListPlugin.logError(e);
			}
			return new Date();
		}
		else if (childName.equals("data"))
		{
			try
			{
				String raw = node.getTextContent();
				return Base64.decode(raw.getBytes("UTF-8"));
			}
			catch (UnsupportedEncodingException e)
			{
				PListPlugin.logError(e);
			}
			return new byte[0];
		}
		else if (childName.equals("array"))
		{
			List<Object> array = new ArrayList<Object>();
			NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE)
				{
					array.add(parseNode((Element) child));
				}
			}
			return array;
		}
		else if (childName.equals("dict"))
		{
			Map<String, Object> map = new HashMap<String, Object>();
			NodeList children = node.getChildNodes();
			int i = 0;
			while (i < children.getLength())
			{
				Node child = children.item(i++);
				if (child.getNodeType() == Node.ELEMENT_NODE)
				{
					Node nextElement = children.item(i);
					while (nextElement.getNodeType() != Node.ELEMENT_NODE)
					{
						nextElement = children.item(i++);
					}
					map.put(child.getTextContent(), parseNode((Element) nextElement));
				}
			}
			return map;
		}
		return null;
	}
}
