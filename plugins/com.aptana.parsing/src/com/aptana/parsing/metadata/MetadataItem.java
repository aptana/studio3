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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * @author Ingo Muschenetz
 */
public class MetadataItem implements IMetadataItem, Cloneable {

	private static final String EMPTY = ""; //$NON-NLS-1$
	private String name = EMPTY;
	private String description = EMPTY;
	private String deprecatedDescription = EMPTY;
	private String hint = EMPTY;
	private ArrayList<UserAgent> fUserAgents;

	/**
	 * Adds a type to the type list.
	 * 
	 * @param value
	 *            The full name (including namespaces, if any) of type to add.
	 */
	public void addUserAgent(UserAgent value)
	{
		if (fUserAgents == null)
		{
			fUserAgents = new ArrayList<UserAgent>();
		}

		fUserAgents.add(value);
	}
	
	/**
	 * Returns a list of new user agents
	 * @return Returns a list of new user agents
	 */
	public UserAgent[] getUserAgents()
	{
		if(fUserAgents == null)
		{
			return new UserAgent[0];
		}
		
		return fUserAgents.toArray(new UserAgent[0]);
	}
	
	/**
	 * Set the name of the field
	 * 
	 * @param name
	 *            The field name
	 */
	public void setName(String name)
	{
		this.name = (name == null) ? EMPTY : name;
	}

	/**
	 * Gets the name of the field
	 * 
	 * @return The string representing the field name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the description of the field
	 * 
	 * @param description
	 *            The field description
	 */
	public void setDescription(String description)
	{
		this.description = (description == null) ? EMPTY : description;
	}

	/**
	 * Gets the description of the field
	 * 
	 * @return The string representing the field description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the deprecated description of the field
	 * 
	 * @param deprecatedDescription
	 *            The field deprecatedDescription
	 */
	public void setDeprecatedDescription(String deprecatedDescription)
	{
		this.deprecatedDescription = (deprecatedDescription == null) ? EMPTY : deprecatedDescription;
	}

	/**
	 * Gets the deprecated description of the field
	 * 
	 * @return The string representing the field deprecated description
	 */
	public String getDeprecatedDescription()
	{
		return deprecatedDescription;
	}

	/**
	 * Gets the item hint
	 * 
	 * @return The item hint
	 */
	public String getHint()
	{
		return hint;
	}

	/**
	 * Set the hint of the item
	 * 
	 * @param hint
	 *            The hint
	 */
	public void setHint(String hint)
	{
		this.hint = (hint == null) ? EMPTY : hint;
	}

	/*
	 * Methods
	 */
	
	/**
	 * @see com.aptana.parsing.metadata.IMetadataItem#read(java.io.DataInput)
	 */
	public void read(DataInput input) throws IOException
	{
		this.name = input.readUTF();
		this.description = input.readUTF();
		this.hint = input.readUTF();
		
		int size = input.readInt();
		if (size > 0)
		{
			this.fUserAgents = new ArrayList<UserAgent>();
			
			for (int i = 0; i < size; i++)	
			{
				UserAgent param = new UserAgent();
				
				param.read(input);
				this.fUserAgents.add(param);
			}
		}
	}

	/**
	 * @see com.aptana.parsing.metadata.IMetadataItem#write(java.io.DataOutput)
	 */
	public void write(DataOutput output) throws IOException
	{
		output.writeUTF(this.name);
		output.writeUTF(this.description);
		output.writeUTF(this.hint);
		
		if (this.fUserAgents != null)
		{
			output.writeInt(this.fUserAgents.size());
			
			for (int i = 0; i < this.fUserAgents.size(); i++)
			{
				UserAgent param = this.fUserAgents.get(i);
				
				param.write(output);
			}
		}
		else
		{
			output.writeInt(0);
		}

	}

	/**
	 * Write an array list of IMetadataItems as binary
	 * 
	 * @param output
	 *            The output stream to write to
	 * @param list
	 *            The list to write
	 * @throws IOException
	 */
	protected void writeArrayList(DataOutput output, ArrayList<IMetadataItem> list) throws IOException
	{
		output.writeInt(list.size());

		Iterator<IMetadataItem> iter = list.iterator();
		
		while (iter.hasNext())
		{
			IMetadataItem item = iter.next();
			item.write(output);
		}
	}

	/**
	 * Write an array list of IMetadataItems as binary
	 * 
	 * @param output
	 *            The output stream to write to
	 * @param list
	 *            The list to write
	 * @throws IOException
	 */
	protected void writeHashtable(DataOutput output, Hashtable<String, IMetadataItem> list) throws IOException
	{
		output.writeInt(list.size());

		Iterator<IMetadataItem> iter = list.values().iterator();		
		while (iter.hasNext()) {
			IMetadataItem item = iter.next();
			item.write(output);
		}
	}
	
	/**
	 * Returns a list of all the platforms this item is supported by
	 * @return Returns a list of all the platforms this item is supported by
	 */
	public String[] getUserAgentPlatformNames()
	{
		ArrayList<String> al = new ArrayList<String>();
		if (this.fUserAgents != null)
		{
			for (int i = 0; i < this.fUserAgents.size(); i++)
			{
				UserAgent param = this.fUserAgents.get(i);
				al.add(param.getPlatform());
			}
		}

		return al.toArray(new String[al.size()]);
	}
	
	/**
	 * Merges one element with another
	 * @param element
	 */
	public void merge(MetadataItem element)
	{
		if(getName().equals(EMPTY) && !element.getName().equals(EMPTY))
		{
			setName(element.getName());
		}

		if(!element.getDescription().equals(EMPTY))
		{
			String desc = getDescription();
			if(!desc.equals(EMPTY))
			{
				desc += " "; //$NON-NLS-1$
			}
			setDescription(desc + element.getDescription());
		}
		
		if(!element.getDeprecatedDescription().equals(EMPTY))
		{
			String desc = getDeprecatedDescription();
			if(!desc.equals(EMPTY))
			{
				desc += " "; //$NON-NLS-1$
			}
			setDeprecatedDescription(desc + element.getDeprecatedDescription());
		}
		
		if(!element.getHint().equals(EMPTY))
		{
			String desc = getHint();
			if(!desc.equals(EMPTY))
			{
				desc += " "; //$NON-NLS-1$
			}
			setHint(desc + element.getHint());
		}

		UserAgent[] ua = element.getUserAgents();
		for (int i = 0; i < ua.length; i++) {
			addUserAgent(ua[i]);
		}
	}
}
