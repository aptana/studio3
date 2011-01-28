/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting;


public interface IContentTypeTranslator
{

	/**
	 * Allows plugins to contribute a scope translation. This is used to give different scope names to various
	 * partitions based on their nesting within other languages.
	 * 
	 * @param left
	 * @param right
	 */
	public void addTranslation(QualifiedContentType left, QualifiedContentType right);

	public QualifiedContentType translate(QualifiedContentType contentType);
}
