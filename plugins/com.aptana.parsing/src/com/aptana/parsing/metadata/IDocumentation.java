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

//import com.aptana.parsing.CodeLocation;
//import com.aptana.parsing.ErrorMessage;

/**
 * @author Robin Debreuil
 */
public interface IDocumentation
{
	/**
	 * TYPE_FUNCTION
	 */
	int TYPE_FUNCTION = 0;

	/**
	 * TYPE_PROPERTY
	 */
	int TYPE_PROPERTY = 1;

	/**
	 * TYPE_PROJECT
	 */
	int TYPE_PROJECT = 2;

	/**
	 * Gets the name of this object. Setting this is optional as it is usually inferred from the code. Used mostly when
	 * adding documentation without code or stub libraries (like documentation from xml).
	 * 
	 * @return Returns the name of this object.
	 */
	String getName();

	/**
	 * Sets the name of this object. This is usually inferred from the code, however it will still return a valid value
	 * that has been auto-set. Used mostly when adding documentation without code or stub libraries (like documentation
	 * from xml).
	 * 
	 * @param value
	 *            The name of this object.
	 */
	void setName(String value);

	// /**
	// * Gets the 'returnType' on functions, and the 'type' on properties. JS objects can be/return multiple types.
	// * @return Returns the 'returnTypes' on functions, and the 'types' on properties.
	// */
	// String[] getTypes();
	//	
	// /**
	// * Adds a 'returnType' on functions, and a 'type' to properties. JS objects can be/return multiple types.
	// * @param value The 'returnType' on functions, or the 'type' on properties to add.
	// */
	// void addType(String value);

	/**
	 * Gets the description of this object (can be html).
	 * 
	 * @return Returns the description of this object (can be html).
	 */
	String getDescription();

	/**
	 * Sets the the description of this object (can be html).
	 * 
	 * @param value
	 *            The description of this object (can be html).
	 */
	void setDescription(String value);

	/**
	 * Gets the example of this object (can be html).
	 * 
	 * @return Returns the example of this object (can be html).
	 */
	String[] getExamples();

	/**
	 * Adds an example of this object (can be html).
	 * 
	 * @param value
	 *            The example of this object (can be html).
	 */
	void addExample(String value);

	/**
	 * Gets the remarks of this object (can be html).
	 * 
	 * @return Returns the remarks of this object (can be html).
	 */
	String getRemarks();

	/**
	 * Sets the remarks of this object (can be html).
	 * 
	 * @param value
	 *            The remarks of this object (can be html).
	 */
	void setRemarks(String value);

	/**
	 * Gets the author of this file.
	 * 
	 * @return Returns the author of this file.
	 */
	String getAuthor();

	/**
	 * Sets the the author of this file.
	 * 
	 * @param value
	 *            The the author of this file.
	 */
	void setAuthor(String value);

	/**
	 * Gets the the version of this file.
	 * 
	 * @return Returns the the version of this file.
	 */
	String getVersion();

	/**
	 * Sets the the version of this file.
	 * 
	 * @param value
	 *            The the version of this file.
	 */
	void setVersion(String value);

	/**
	 * Gets the list of 'see' links in this documentation object.
	 * 
	 * @return Returns the list of 'see' links in this documentation object.
	 */
	String[] getSees();

	/**
	 * Adds a '@see' link to this documentation object.
	 * 
	 * @param value
	 *            a '@see' link to add to this documentation object.
	 */
	void addSee(String value);

	/**
	 * Gets all the parse errors for this object.
	 * 
	 * @return Returns all the parse errors for this object.
	 */
//	ErrorMessage[] getErrors();

	/**
	 * Clears all the parse errors for this object.
	 */
	void clearErrors();

	/**
	 * Adds a parse error to this object.
	 * 
	 * @param e
	 *            The parse error to add
	 */
//	void addError(ErrorMessage e);

	/**
	 * Gets the type of documentation this is, based on the TYPE constants in IDocumentation (TYPE_FUNCTION,
	 * TYPE_PROPERTY, or TYPE_PROJECT)
	 * 
	 * @return Returns the type of documentation.
	 */
	int getDocumentType();

	/**
	 * Sets the type of documentation this is, this value is based on the TYPE constants in IDocumentation
	 * (YPE_FUNCTION, TYPE_PROPERTY, or TYPE_PROJECT)
	 * 
	 * @param type
	 *            The type of documentation.
	 */
	void setDocumentType(int type);

	/**
	 * @return Returns the userAgent.
	 */
	String getUserAgent();

	/**
	 * @param userAgent
	 *            The userAgent to set.
	 */
	void setUserAgent(String userAgent);

	/**
	 * Returns the CodeLocation of the given ID.
	 * 
	 * @param id
	 * @return Returns the CodeLocation of the given ID (from the id tag).
	 */
//	CodeLocation[] getID(String id);

	/**
	 * Sets the codeLocation of the given ID (from the id tag).
	 * 
	 * @param id
	 *            The ID to set.
	 * @param location
	 */
//	void setID(String id, CodeLocation location);

	/**
	 * Gets all the id string in the doc.
	 * 
	 * @return Returns all the
	 */
	String[] getIDs();

	/**
	 * Adds a location to look for external script-docs.
	 * 
	 * @param value
	 *            A relative, absolute path, or URL.
	 */
	void addSDocLocation(String value);

	/**
	 * Locations to look for external script-docs.
	 * 
	 * @return Locations to look for external script-docs.
	 */
	String[] getSDocLocations();
}
