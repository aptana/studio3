package com.aptana.index.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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

	public String[] getDocuments()
	{
		return documentNames.toArray(new String[documentNames.size()]);
	}

	public boolean isEmpty()
	{
		return this.documentTables.isEmpty() && this.documentNames.isEmpty();
	}

	public void addDocumentTable(Map<String, Object> wordsToDocNumbers)
	{
		documentTables.add(wordsToDocNumbers);
	}

}
