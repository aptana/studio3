/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core;

import java.util.List;

/**
 * IndexDocument
 */
public interface IndexDocument
{
	/**
	 * Add a new document to the list of documents
	 * 
	 * @param document
	 */
	void addDocument(String document);
	
	/**
	 * Retrieve the list of documents
	 * 
	 * @return
	 */
	List<String> getDocuments();
}
