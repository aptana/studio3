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

/**
 * This is a class that holds type info and descriptions, used when representing return types, parameters, and exceptions.
 */
public class UserAgent implements IMetadataDescription
{
	private static final String EMPTY = ""; //$NON-NLS-1$
	private String fPlatform = EMPTY;
	private String fVersion = EMPTY;
	private String fOs = EMPTY;
	private String fOsVersion = EMPTY;
	private String fDescription = EMPTY;

	/**
	 * Creates a description of a prototype based class (type) that includes the given name if appropriate, and a
	 * description.
	 */
	public UserAgent()
	{
	}
	
	/**
	 * Creates a new user agent
	 * @param platform
	 * @param version
	 * @param os
	 * @param osVersion
	 * @param description
	 */
	public UserAgent(String platform, String version, String os, String osVersion, String description)
	{
		this.fPlatform = platform;
		this.fVersion = version;
		this.fOs = os;
		this.fOsVersion = osVersion;
		this.fDescription = description;
	}

	/**
	 * Read in a binary representation of this object
	 * 
	 * @param input
	 *            The stream to read from
	 * @throws IOException
	 */
	public void read(DataInput input) throws IOException
	{
		this.fPlatform = input.readUTF();
		this.fVersion = input.readUTF();
		this.fOs = input.readUTF();
		this.fOsVersion = input.readUTF();
		this.fDescription = input.readUTF();
	}

	/**
	 * Write out a binary representation of this object
	 * 
	 * @param output
	 *            The stream to write to
	 * @throws IOException
	 */
	public void write(DataOutput output) throws IOException
	{
		output.writeUTF(this.fPlatform);
		output.writeUTF(this.fVersion);
		output.writeUTF(this.fOs);
		output.writeUTF(this.fOsVersion);
		output.writeUTF(this.fDescription);
	}

	/**
	 * @param platform
	 */
	public void setPlatform(String platform)
	{
		this.fPlatform = platform;
	}

	/**
	 * @param version
	 */
	public void setVersion(String version)
	{
		this.fVersion = version;
	}

	/**
	 * @param os
	 */
	public void setOs(String os)
	{
		this.fOs = os;
	}

	/**
	 * @param osVersion
	 */
	public void setOsVersion(String osVersion)
	{
		this.fOsVersion = osVersion;
	}

	/**
	 * @return Returns the Os.
	 */
	public String getOs()
	{
		return fOs;
	}

	/**
	 * @return Returns the OsVersion.
	 */
	public String getOsVersion()
	{
		return fOsVersion;
	}

	/**
	 * @return Returns the Platform.
	 */
	public String getPlatform()
	{
		return fPlatform;
	}

	/**
	 * @return Returns the Version.
	 */
	public String getVersion()
	{
		return fVersion;
	}

	/**
	 * Set the description of the item
	 * 
	 * @param description
	 *            The description
	 */
	public void setDescription(String description)
	{
		this.fDescription = description;
	}

	/**
	 * Gets the item description
	 * 
	 * @return The item description
	 */
	public String getDescription()
	{
		// TODO Auto-generated method stub
		return fDescription;
	}
}
