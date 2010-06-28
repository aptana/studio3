package com.aptana.internal.index.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.aptana.index.core.Index;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class MemoryIndex
{

	private static final int MERGE_THRESHOLD = 100;
	private HashMap<String, Map<String, Set<String>>> documentsToTable;

	public MemoryIndex()
	{
		documentsToTable = new HashMap<String, Map<String, Set<String>>>();
	}

	public void addEntry(String category, String key, String filePath)
	{
		Map<String, Set<String>> categoriesToWords = documentsToTable.get(filePath);
		if (categoriesToWords == null)
		{
			categoriesToWords = new HashMap<String, Set<String>>();
			documentsToTable.put(filePath, categoriesToWords);
		}
		Set<String> words = categoriesToWords.get(category);
		if (words == null)
		{
			words = new HashSet<String>();
			categoriesToWords.put(category, words);
		}
		words.add(key);
	}

	public Set<String> addDocumentNames(String substring)
	{
		// assumed the disk index already skipped over documents which have been added/changed/deleted
		Set<String> results = new HashSet<String>();
		for (Map.Entry<String, Map<String, Set<String>>> entry : documentsToTable.entrySet())
		{
			if (substring == null)
			{ // add all new/changed documents
				if (entry.getValue() != null)
				{
					results.add(entry.getKey());
				}
			}
			else
			{
				if (entry.getValue() != null && (entry.getKey()).startsWith(substring, 0))
				{
					results.add(entry.getKey());
				}
			}
		}
		return results;
	}

	Set<String> getDocumentNames()
	{
		return documentsToTable.keySet();
	}

	public int numberOfChanges()
	{
		return documentsToTable.size();
	}

	public boolean hasChanged()
	{
		return numberOfChanges() > 0;
	}

	Map<String, Set<String>> getCategoriesForDocument(String docname)
	{
		return documentsToTable.get(docname);
	}

	public Map<String, QueryResult> addQueryResults(String[] categories, String key, int matchRules,
			Map<String, QueryResult> results)
	{
		if (results == null)
			results = new HashMap<String, QueryResult>();

		for (Map.Entry<String, Map<String, Set<String>>> entry : documentsToTable.entrySet())
		{
			Map<String, Set<String>> categoriesToWords = entry.getValue();
			if (categoriesToWords == null)
				continue;
			for (String category : categories)
			{
				Set<String> words = categoriesToWords.get(category);
				// When we're looking for exact matches, case sensitive, just ask wordset if it contains key!
				if (matchRules == (SearchPattern.EXACT_MATCH | SearchPattern.CASE_SENSITIVE))
				{
					if (words.contains(key))
					{
						QueryResult result = results.get(key);
						if (result == null)
							result = new QueryResult(key);
						result.addDocumentName(entry.getKey());
						results.put(key, result);
					}
				}
				else
				{
					if (words != null)
					{
						// Otherwise we need to check each word individually
						for (String word : words)
						{
							if (Index.isMatch(key, word, matchRules))
							{
								QueryResult result = results.get(word);
								if (result == null)
									result = new QueryResult(word);
								result.addDocumentName(entry.getKey());
								results.put(word, result);
							}
						}
					}
				}
			}
		}
		return results;
	}

	public boolean shouldMerge()
	{
		return numberOfChanges() >= MERGE_THRESHOLD;
	}

	Map<String, Map<String, Set<String>>> getDocumentsToReferences()
	{
		return Collections.unmodifiableMap(documentsToTable);
	}

	public void remove(String documentName)
	{
		this.documentsToTable.put(documentName, null);
	}

	public void removeCategories(String[] categoryNames)
	{
		for (Map<String, Set<String>> categoriesToWords : documentsToTable.values())
		{
			if (categoriesToWords != null)
			{
				for (String category : categoryNames)
					categoriesToWords.remove(category);
			}
		}
	}

}
