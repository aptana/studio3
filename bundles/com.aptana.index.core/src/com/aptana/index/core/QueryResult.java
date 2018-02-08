/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.core.util.StringUtil;

public class QueryResult
{

	private String word;
	private HashSet<String> documentNames;
	private List<Map<String, Object>> documentTables;

	public QueryResult(String word)
	{
		this(word, null);
	}

	public QueryResult(String word, Map<String, Object> table)
	{
		this.word = word;
		this.documentNames = new HashSet<String>();
		this.documentTables = new ArrayList<Map<String, Object>>();
		if (table != null)
			documentTables.add(table);
	}

	public void addDocumentName(String path)
	{
		this.documentNames.add(path);
	}

	public String getWord()
	{
		return word;
	}

	public Set<String> getDocuments()
	{
		return Collections.unmodifiableSet(documentNames);
	}

	public boolean isEmpty()
	{
		return this.documentTables.isEmpty() && this.documentNames.isEmpty();
	}

	public void addDocumentTable(Map<String, Object> wordsToDocNumbers)
	{
		documentTables.add(wordsToDocNumbers);
	}

	@Override
	public String toString()
	{
		return MessageFormat
				.format("[word: ''{0}'', documents: {1}]", getWord(), StringUtil.join(", ", getDocuments())); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
