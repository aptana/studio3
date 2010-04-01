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

/**
 * Represents an "element" of metadata. This could be a HTML tag, a CSS selector, etc...
 * 
 * @author Ingo Muschenetz
 */
public class FieldMetadata extends MetadataItem
{
	private static final String EMPTY = ""; //$NON-NLS-1$
	private ArrayList values = new ArrayList();
	private String type = EMPTY;
	private boolean allowMultipleValues = false;

	/**
	 * Add a value to the metadata object
	 * 
	 * @param val
	 *            The value to add
	 */
	public void addValue(ValueMetadata val)
	{
		values.add(val);
	}

	/**
	 * Returns the list of attached values
	 * 
	 * @return The ArrayList of values
	 */
	public ArrayList getValues()
	{
		return values;
	}

	/**
	 * Returns the associated "type" of the item
	 * 
	 * @param type
	 *            The type to set
	 */
	public void setType(String type)
	{
		this.type = (type == null) ? EMPTY : type;
	}

	/**
	 * Returns the associated "type" of the item
	 * 
	 * @return The type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @see com.aptana.parsing.metadata.IMetadataItem#read(java.io.DataInput)
	 */
	public void read(DataInput input) throws IOException
	{
		super.read(input);
		this.allowMultipleValues = input.readBoolean();

		int size = input.readInt();
		for (int i = 0; i < size; i++)
		{
			ValueMetadata value = new ValueMetadata();

			value.read(input);
			addValue(value);
		}

		this.type = input.readUTF();
	}

	/**
	 * @see com.aptana.parsing.metadata.IMetadataItem#write(java.io.DataOutput)
	 */
	public void write(DataOutput output) throws IOException
	{
		super.write(output);
		output.writeBoolean(this.allowMultipleValues);

		this.writeArrayList(output, values);

		output.writeUTF(this.type);
	}

	/**
	 * Do we allow multiple values?
	 * 
	 * @param b
	 *            True if yes, false if no
	 */
	public void setAllowMultipleValues(boolean b)
	{
		this.allowMultipleValues = b;
	}

	/**
	 * Do we allow multiple values?
	 * 
	 * @return Returns true if there are multiple values.
	 */
	public boolean getAllowMultipleValues()
	{
		return this.allowMultipleValues;
	}
}
