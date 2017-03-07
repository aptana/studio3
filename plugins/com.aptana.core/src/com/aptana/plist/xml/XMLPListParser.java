/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.plist.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.internal.preferences.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.plist.IPListParser;

@SuppressWarnings("restriction")
public class XMLPListParser implements IPListParser
{

	private static DateFormat ISO_8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); //$NON-NLS-1$
	static
	{
		TimeZone tz = TimeZone.getTimeZone("UTC"); //$NON-NLS-1$
		ISO_8601.setTimeZone(tz);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> parse(File file) throws IOException
	{
		DocumentBuilder builder = null;
		try
		{
			// Avoid loading external plist DTD, since it can hit the network, which is really un-necessary.
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); //$NON-NLS-1$
			builder = factory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			IdeLog.logError(CorePlugin.getDefault(), e);
		}
		if (builder != null)
		{
			Document d = null;
			try
			{

				d = builder.parse(file);
			}
			catch (Exception e)
			{
				// May have failed due to invalid characters in XML, which happens often with TM themes,
				// So let's sanitize the XML
				String raw = IOUtil.read(new FileInputStream(file), IOUtil.UTF_8);
				raw = stripNonValidXMLCharacters(raw);
				try
				{
					d = builder.parse(new ByteArrayInputStream(raw.getBytes(IOUtil.UTF_8)));
				}
				catch (Exception e1)
				{
					IdeLog.logWarning(CorePlugin.getDefault(), e1);
				}
			}
			if (d != null)
			{
				Element doc = d.getDocumentElement();
				if (doc != null && doc.getFirstChild() != null && doc.getFirstChild().getNextSibling() != null)
				{
					Node root = doc.getFirstChild().getNextSibling();
					return (Map<String, Object>) parseNode((Element) root);
				}
			}
		}
		return Collections.emptyMap();
	}

	private static String stripNonValidXMLCharacters(String in)
	{
		if (in == null || (StringUtil.EMPTY.equals(in)))
		{
			return StringUtil.EMPTY;
		}
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < in.length(); i++)
		{
			char current = in.charAt(i);
			if ((current == 0x9) || (current == 0xA) || (current == 0xD) || ((current >= 0x20) && (current <= 0xD7FF))
					|| ((current >= 0xE000) && (current <= 0xFFFD)) || ((current >= 0x10000) && (current <= 0x10FFFF)))
			{
				out.append(current);
			}
			else
			{
				// out.append("&#").append((int) current).append(';'); // Broken, can we insert them in some way that
				// does work?
			}
		}
		return out.toString();
	}

	private Object parseNode(Element node)
	{
		String tagName = node.getTagName();
		if (tagName.equals("string")) //$NON-NLS-1$
		{
			return node.getTextContent();
		}
		else if (tagName.equals("real")) //$NON-NLS-1$
		{
			return Float.parseFloat(node.getTextContent());
		}
		else if (tagName.equals("integer")) //$NON-NLS-1$
		{
			return Long.parseLong(node.getTextContent());
		}
		else if (tagName.equals("true")) //$NON-NLS-1$
		{
			return Boolean.TRUE;
		}
		else if (tagName.equals("false")) //$NON-NLS-1$
		{
			return Boolean.FALSE;
		}
		else if (tagName.equals("date")) //$NON-NLS-1$
		{
			try
			{
				// ISO 8601 format text
				String raw = node.getTextContent();
				return ISO_8601.parse(raw);
			}
			catch (Exception e)
			{
				IdeLog.logError(CorePlugin.getDefault(), e);
			}
			return new Date();
		}
		else if (tagName.equals("data")) //$NON-NLS-1$
		{
			try
			{
				String raw = node.getTextContent();
				// FIXME Implement our own Base64 decoder!
				return Base64.decode(raw.getBytes(IOUtil.UTF_8));
			}
			catch (UnsupportedEncodingException e)
			{
				IdeLog.logError(CorePlugin.getDefault(), e);
			}
			return new byte[0];
		}
		else if (tagName.equals("array")) //$NON-NLS-1$
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
		else if (tagName.equals("dict")) //$NON-NLS-1$
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
