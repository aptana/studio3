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
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Represents an "element" of metadata. This could be a HTML tag, a CSS selector, etc...
 * 
 * @author Ingo Muschenetz
 */
public class ElementMetadata extends MetadataItem
{
	private static final String EMPTY = ""; //$NON-NLS-1$
	private String fullName = EMPTY;
	private Hashtable<String, IMetadataItem> fields = new Hashtable<String, IMetadataItem>();
	private Hashtable<String, IMetadataItem> events = new Hashtable<String, IMetadataItem>();

	/**
	 * Set the name of the element
	 * 
	 * @param fullname
	 *            The element full name
	 */
	public void setFullName(String fullname)
	{
		this.fullName = (fullname == null) ? EMPTY : fullname;
	}

	/**
	 * Gets the full name of the element
	 * 
	 * @return The string representing the element full name
	 */
	public String getFullName()
	{
		return fullName;
	}

	/**
	 * Add a field to the metadata object
	 * 
	 * @param field
	 *            The field to add
	 */
	public void addField(FieldMetadata field)
	{
		fields.put(field.getName(), field);
	}

	/**
	 * Returns the list of attached fields
	 * 
	 * @return The Hashtable of fields
	 */
	public Hashtable<String, IMetadataItem> getFields()
	{
		return fields;
	}

	/**
	 * Add an event to the metadata object
	 * 
	 * @param event
	 *            The event to add
	 */
	public void addEvent(EventMetadata event)
	{
		events.put(event.getName(), event);
	}

	/**
	 * Returns the list of attached events
	 * 
	 * @return The Hashtable of events
	 */
	public Hashtable<String, IMetadataItem> getEvents()
	{
		return events;
	}

	/**
	 * Replaces the current list of fields with a new one
	 * 
	 * @param newFields
	 *            The list of new fields
	 */
	public void setFields(Hashtable<String, IMetadataItem> newFields)
	{
		fields = newFields;
	}

	/**
	 * Replaces the current list of events with a new one
	 * 
	 * @param newEvents
	 *            The list of new events
	 */
	public void setEvents(Hashtable<String, IMetadataItem> newEvents)
	{
		events = newEvents;
	}

	/**
	 * @throws IOException
	 * @see com.aptana.parsing.metadata.IMetadataItem#read(java.io.DataInput)
	 */
	public void read(DataInput input) throws IOException
	{
		super.read(input);
		this.fullName = input.readUTF();

		int size = input.readInt();
		for (int i = 0; i < size; i++)
		{
			FieldMetadata field = new FieldMetadata();

			field.read(input);
			addField(field);
		}

		size = input.readInt();
		for (int i = 0; i < size; i++)
		{
			EventMetadata event = new EventMetadata();

			event.read(input);
			addEvent(event);
		}

	}

	/**
	 * @throws IOException
	 * @see com.aptana.parsing.metadata.IMetadataItem#write(java.io.DataOutput)
	 */
	public void write(DataOutput output) throws IOException
	{
		super.write(output);
		output.writeUTF(this.fullName);

		this.writeHashtable(output, fields);
		this.writeHashtable(output, events);

	}

	/**
	 * Merges one element with another
	 * 
	 * @param element
	 */
	public void merge(ElementMetadata element)
	{
		super.merge(element);

		Collection<IMetadataItem> fieldSet = element.getFields().values();
		for (Iterator<IMetadataItem> iterator = fieldSet.iterator(); iterator.hasNext();)
		{
			FieldMetadata field = (FieldMetadata) iterator.next();
			addField(field);
		}

		Collection<IMetadataItem> eventSet = element.getEvents().values();
		for (Iterator<IMetadataItem> iterator = eventSet.iterator(); iterator.hasNext();)
		{
			EventMetadata event = (EventMetadata) iterator.next();
			addEvent(event);
		}

		if (getFullName() != EMPTY && element.getFullName() != EMPTY)
		{
			setFullName(element.getFullName());
		}
	}
}
