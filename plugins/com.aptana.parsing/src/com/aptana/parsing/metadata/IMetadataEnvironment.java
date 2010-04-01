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

import java.util.Hashtable;

import com.aptana.parsing.lexer.ILexeme;

/**
 * A metadata environment
 * @author Ingo Muschenetz
 *
 */
public interface IMetadataEnvironment {

	/**
	 * Returns all the elements
	 */
	String[] getAllElements();

	/**
	 * Return the set of all fields
	 * 
	 * @return the Hashtable of all "global" fields;
	 */
	Hashtable<String, FieldMetadata> getGlobalFields();

	/**
	 * Return the set of all events
	 * 
	 * @return the Hashtable of all "global" events;
	 */
	Hashtable<String, EventMetadata> getGlobalEvents();

	/**
	 * Gets an element from the environment based upon a lexeme
	 * 
	 * @param lexeme
	 *            The environment item
	 * @return An element, or null if not found.
	 */
	ElementMetadata getElement(ILexeme unclosed);

	/**
	 * returns the specified element
	 * 
	 * @param name
	 *            The element name to get
	 * @return The specified element
	 */
	ElementMetadata getElement(String tagNameLower);

	/**
	 * Given a metadata element, returns the documentation for it
	 * 
	 * @param element
	 * @return A string containing documentation
	 */
	String getElementDocumentation(String e);

	/**
	 * Returns a list of all the platforms this item is supported by
	 * @return Returns a list of all the platforms this item is supported by
	 */
	String[] getUserAgentPlatformNames(String e);

	/**
	 * Given a metadata element, returns the documentation for it
	 * 
	 * @param element
	 *            The element to look at
	 * @return A string containing documentation
	 */
	String getFieldDocumentation(FieldMetadata fm);

	/**
	 * Given a metadata element, returns the documentation for it
	 * 
	 * @param element
	 *            The element to look at
	 * @return A string containing documentation
	 */
	String getEventDocumentation(EventMetadata fm);
}
