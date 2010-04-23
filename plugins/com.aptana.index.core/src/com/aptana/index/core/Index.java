package com.aptana.index.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.runtime.IPath;

import com.aptana.internal.index.core.DiskIndex;
import com.aptana.internal.index.core.MemoryIndex;
import com.aptana.internal.index.core.ReadWriteMonitor;

public class Index
{
	private static final int MATCH_RULE_INDEX_MASK = SearchPattern.EXACT_MATCH | SearchPattern.PREFIX_MATCH
			| SearchPattern.PATTERN_MATCH | SearchPattern.CASE_SENSITIVE | SearchPattern.REGEX_MATCH;
	
	private static final Map<String,Pattern> PATTERNS = new HashMap<String,Pattern>();
	
	// Separator to use after the container path
	public static final char DEFAULT_SEPARATOR = '/';
	public char separator = DEFAULT_SEPARATOR;

	private MemoryIndex memoryIndex;
	private DiskIndex diskIndex;
	public ReadWriteMonitor monitor;

	/**
	 * Index
	 * 
	 * @param path
	 * @throws IOException
	 */
	public Index(String path) throws IOException
	{
		IPath containerPath = IndexManager.getInstance().computeIndexLocation(path);
		String containerPathString = containerPath.getDevice() == null ? containerPath.toString() : containerPath
				.toOSString();
		this.memoryIndex = new MemoryIndex();
		this.monitor = new ReadWriteMonitor();

		this.diskIndex = new DiskIndex(containerPathString);
		this.diskIndex.initialize();
	}

	/**
	 * getIndexFile
	 * 
	 * @return
	 */
	public File getIndexFile()
	{
		return this.diskIndex == null ? null : this.diskIndex.indexFile;
	}

	/**
	 * addEntry
	 * 
	 * @param category
	 * @param key
	 * @param documentPath
	 */
	public void addEntry(String category, String key, String documentPath)
	{
		this.memoryIndex.addEntry(category, key, documentPath);
	}

	/**
	 * query
	 * 
	 * @param categories
	 * @param key
	 * @param matchRule
	 * @return
	 * @throws IOException
	 */
	public List<QueryResult> query(String[] categories, String key, int matchRule) throws IOException
	{
		if (this.memoryIndex.shouldMerge() && monitor.exitReadEnterWrite())
		{
			try
			{
				save();
			}
			finally
			{
				monitor.exitWriteEnterRead();
			}
		}

		Map<String, QueryResult> results;
		int rule = matchRule & MATCH_RULE_INDEX_MASK;
		
		if (this.memoryIndex.hasChanged())
		{
			results = this.diskIndex.addQueryResults(categories, key, rule, this.memoryIndex);
			results = this.memoryIndex.addQueryResults(categories, key, rule, results);
		}
		else
		{
			results = this.diskIndex.addQueryResults(categories, key, rule, null);
		}
		
		if ((matchRule & SearchPattern.REGEX_MATCH) == SearchPattern.REGEX_MATCH)
		{
			PATTERNS.clear();
		}

		return (results == null) ? null : new ArrayList<QueryResult>(results.values());
	}

	/**
	 * isMatch
	 * 
	 * @param pattern
	 * @param word
	 * @param matchRule
	 * @return
	 */
	public static boolean isMatch(String pattern, String word, int matchRule)
	{
		if (pattern == null)
			return true;
		int patternLength = pattern.length();
		int wordLength = word.length();
		if (patternLength == 0)
			return matchRule != SearchPattern.EXACT_MATCH;
		switch (matchRule)
		{
			case SearchPattern.EXACT_MATCH:
				return patternLength == wordLength && pattern.equalsIgnoreCase(word);
			case SearchPattern.PREFIX_MATCH:
				return patternLength <= wordLength && word.toLowerCase().startsWith(pattern.toLowerCase());
			case SearchPattern.PATTERN_MATCH:
				return patternMatch(pattern.toLowerCase(), word.toLowerCase());
			case SearchPattern.REGEX_MATCH:
				return regexPatternMatch(pattern, word, false);
			case SearchPattern.EXACT_MATCH | SearchPattern.CASE_SENSITIVE:
				return patternLength == wordLength && pattern.equals(word);
			case SearchPattern.PREFIX_MATCH | SearchPattern.CASE_SENSITIVE:
				return patternLength <= wordLength && word.startsWith(pattern);
			case SearchPattern.PATTERN_MATCH | SearchPattern.CASE_SENSITIVE:
				return patternMatch(pattern, word);
			case SearchPattern.REGEX_MATCH | SearchPattern.CASE_SENSITIVE:
				return regexPatternMatch(pattern, word, true);
		}
		return false;
	}

	/**
	 * patternMatch
	 * 
	 * @param pattern
	 * @param word
	 * @return
	 */
	private static boolean patternMatch(String pattern, String word)
	{
		// TODO Sort of like a regexp match, just handle * and ? wildcards in the pattern
		if (pattern.equals("*")) //$NON-NLS-1$
			return true;
		return false;
	}
	
	/**
	 * regexPatternMatch
	 * 
	 * @param regex
	 * @param word
	 * @return
	 */
	private static boolean regexPatternMatch(String regex, String word, boolean caseSensitive)
	{
		Pattern pattern = PATTERNS.get(regex);
		
		if (pattern == null)
		{
			try
			{
				// compile the pattern
				pattern = (caseSensitive) ? Pattern.compile(regex) : Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
				
				// cache for later
				PATTERNS.put(regex, pattern);
			}
			catch (PatternSyntaxException e)
			{
				e.printStackTrace();
			}
		}
		
		return (pattern != null) ? pattern.matcher(word).find() : false;
	}

	/**
	 * save
	 * 
	 * @throws IOException
	 */
	public void save() throws IOException
	{
		// must own the write lock of the monitor
		if (!hasChanged())
			return;

		int numberOfChanges = this.memoryIndex.numberOfChanges();
		this.diskIndex = this.diskIndex.mergeWith(this.memoryIndex);
		this.memoryIndex = new MemoryIndex();
		if (numberOfChanges > 1000)
			System.gc(); // reclaim space if the MemoryIndex was very BIG
	}

	/**
	 * hasChanged
	 * 
	 * @return
	 */
	private boolean hasChanged()
	{
		return memoryIndex.hasChanged();
	}

	/**
	 * Remove all indices for a given document
	 * 
	 * @param containerRelativePath
	 */
	public void remove(String containerRelativePath)
	{
		this.memoryIndex.remove(containerRelativePath);
	}

	/**
	 * removeCategories
	 * 
	 * @param categoryNames
	 */
	public void removeCategories(String... categoryNames)
	{
		try
		{
			this.memoryIndex.removeCategories(categoryNames);
			this.diskIndex = this.diskIndex.removeCategories(categoryNames, this.memoryIndex);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
