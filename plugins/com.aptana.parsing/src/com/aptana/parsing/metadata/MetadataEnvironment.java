/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.metadata;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import com.aptana.parsing.lexer.ILexeme;
import com.aptana.parsing.metadata.reader.MetadataObjectsReader;

/**
 * The "environment" of metadata. This can be queried for documentation of items.
 * 
 * @author Ingo Muschenetz
 */
public class MetadataEnvironment
{ 

	private static final String EMPTY = ""; //$NON-NLS-1$
	private Hashtable<String, IMetadataItem> elements = new Hashtable<String, IMetadataItem>();
	private Hashtable<String, FieldMetadata> allFields = new Hashtable<String, FieldMetadata>();
	private Hashtable<String, EventMetadata> allEvents = new Hashtable<String, EventMetadata>();

	/**
	 * Adds an element to the metadata environment
	 * 
	 * @param element
	 *            The element to add
	 */
	public void addElement(ElementMetadata element)
	{
		elements.put(element.getName(), element);
	}

	/**
	 * Gets an element from the environment based upon a lexeme
	 * 
	 * @param lexeme
	 *            The environment item
	 * @return An element, or null if not found.
	 */
	public ElementMetadata getElement(ILexeme lexeme)
	{
		String lexemeText = lexeme.getText().replaceAll("</", EMPTY); //$NON-NLS-1$
		lexemeText = lexeme.getText().replaceAll("<", EMPTY); //$NON-NLS-1$

		return (ElementMetadata) elements.get(lexemeText);
	}

	/**
	 * returns the specified element
	 * 
	 * @param name
	 *            The element name to get
	 * @return The specified element
	 */
	public ElementMetadata getElement(String name)
	{
		if (elements.containsKey(name))
		{
			return (ElementMetadata) elements.get(name);
		}
		else
		{
			return null;
		}
	}

	/**
	 * returns an array of _all_ elements available
	 * 
	 * @return An array of every element
	 */
	public ElementMetadata[] getAllElements()
	{
		return elements.values().toArray(new ElementMetadata[0]);
	}

	/**
	 * returns an array of _all_ elements available, filtered by the specified prefix
	 * 
	 * @param prefix
	 *            The string that prefixes each element
	 * @return An array of every element
	 */
	public ElementMetadata[] getAllElementsWithPrefix(String prefix)
	{
		Collection<IMetadataItem> vals = elements.values();
		ArrayList<ElementMetadata> returnList = new ArrayList<ElementMetadata>();

		Iterator<IMetadataItem> iter = vals.iterator();
		while (iter.hasNext())
		{
			ElementMetadata em = (ElementMetadata) iter.next();
			
			if (em.getName().startsWith(prefix))
			{
				returnList.add(em);
			}
		}

		return returnList.toArray(new ElementMetadata[returnList.size()]);
	}

	/**
	 * Given a string resource, return the relevant MetadataEnvironment
	 * 
	 * @param input
	 *            A stream containing the metadata
	 * @param environment 
	 * @return The MetadataEnvironment
	 */
	public static MetadataEnvironment getMetadataFromResource(InputStream input, MetadataEnvironment environment)
	{
		//MetadataEnvironment environment = new MetadataEnvironment();
		// get documentation
		MetadataObjectsReader reader = new MetadataObjectsReader(environment);

		// load documentation
		try
		{
			reader.load(input);
		}
		catch (Exception e)
		{
		}

		// close the input stream
		try
		{
			input.close();
		}
		catch (IOException e)
		{
		}

		return environment;
	}

	/**
	 * Return the set of all fields
	 * 
	 * @return the Hashtable of all "global" fields;
	 */
	public Hashtable<String, FieldMetadata> getGlobalFields()
	{
		return allFields;
	}

	/**
	 * Sets the set of all fields
	 * 
	 * @param fields
	 *            The set of all "global" fields
	 */
	public void setGlobalFields(Hashtable<String, FieldMetadata> fields)
	{
		this.allFields = fields;
	}

	/**
	 * Return the set of all events
	 * 
	 * @return the Hashtable of all "global" events;
	 */
	public Hashtable<String, EventMetadata> getGlobalEvents()
	{
		return allEvents;
	}

	/**
	 * Sets the set of all fields
	 * 
	 * @param events
	 *            The set of all "global" fields
	 */
	public void setGlobalEvents(Hashtable<String, EventMetadata> events)
	{
		this.allEvents = events;
	}

	/**
	 * Read in the binary serialization of this metadata environment
	 * 
	 * @param input
	 *            The input stream to read from
	 * @throws IOException
	 */
	public void read(DataInput input) throws IOException
	{
		int size = input.readInt();
		for (int i = 0; i < size; i++)
		{
			String key = input.readUTF();
			ElementMetadata element = new ElementMetadata();

			element.read(input);
			this.elements.put(key, element);
		}

		size = input.readInt();
		for (int i = 0; i < size; i++)
		{
			String key = input.readUTF();
			FieldMetadata field = new FieldMetadata();

			field.read(input);
			this.allFields.put(key, field);
		}

		size = input.readInt();
		for (int i = 0; i < size; i++)
		{
			String key = input.readUTF();
			EventMetadata event = new EventMetadata();

			event.read(input);
			this.allEvents.put(key, event);
		}
		
		
		Enumeration<String> enumeration = elements.keys();
		while(enumeration.hasMoreElements())
		{
			String key = enumeration.nextElement();
			ElementMetadata el = (ElementMetadata)elements.get(key) ;

			// Copy the "global" fields in the the individual elements
			Hashtable<String, IMetadataItem> newFields = new Hashtable<String, IMetadataItem>();

			Iterator<IMetadataItem> iter = el.getFields().values().iterator();
			while (iter.hasNext())
			{
				FieldMetadata fm = (FieldMetadata) iter.next();
				if (allFields.containsKey(fm.getName()) && fm.getValues().size() == 0)
				{
					FieldMetadata fmNew = allFields.get(fm.getName());
					newFields.put(fm.getName(), fmNew);
				}
				else
				{
					newFields.put(fm.getName(), fm);
				}
			}
			el.setFields(newFields);

			// Copy the "global" events into the individual elements
			Hashtable<String, IMetadataItem> newEvents = new Hashtable<String, IMetadataItem>();
			Iterator<IMetadataItem> iterEvents = el.getEvents().values().iterator();
			while (iterEvents.hasNext())
			{
				EventMetadata em = (EventMetadata) iterEvents.next();
				if (allEvents.containsKey(em.getName()))
				{
					EventMetadata emNew = allEvents.get(em.getName());
					newEvents.put(em.getName(), emNew);
				}
				else
				{
					newEvents.put(em.getName(), em);
				}
			}
			el.setEvents(newEvents);
		}
	}

	/**
	 * Write out a binary serialization of this metadata environment
	 * 
	 * @param output
	 *            The output stream to write to
	 * @throws IOException
	 */
	public void write(DataOutput output) throws IOException
	{
		this.writeHashtable(output, this.elements);
		this.writeHashtable(output, this.allFields);
		this.writeHashtable(output, this.allEvents);
	}

	/**
	 * Write out a hashtable
	 * 
	 * @param output
	 *            The output stream to write to
	 * @param table
	 *            The hashtable to write
	 * @throws IOException
	 */
	private void writeHashtable(DataOutput output, Hashtable<String, ? extends IMetadataItem> table) throws IOException
	{
		Set<String> keySet = table.keySet();
		String[] keys = keySet.toArray(new String[0]);

		output.writeInt(keys.length);

		for (int i = 0; i < keys.length; i++)
		{
			String key = keys[i];
			IMetadataItem item = table.get(key);

			output.writeUTF(key);
			item.write(output);
		}
	}

	/**
	 * Given a metadata value, returns the documentation for it
	 * 
	 * @param element
	 *            The element to look at
	 * @return A string containing documentation
	 */
	public static String getValueDocumentation(ValueMetadata element)
	{
		StringBuffer docText = new StringBuffer();

		docText.append("<b>" + element.getName() + "</b>"); //$NON-NLS-1$ //$NON-NLS-2$
		docText.append("<br>" + element.getDescription()); //$NON-NLS-1$

		return docText.toString();
	}

}
