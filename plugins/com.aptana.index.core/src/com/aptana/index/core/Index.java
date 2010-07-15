package com.aptana.index.core;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.CRC32;

import org.eclipse.core.runtime.IPath;

import com.aptana.internal.index.core.DiskIndex;
import com.aptana.internal.index.core.MemoryIndex;
import com.aptana.internal.index.core.ReadWriteMonitor;

public class Index
{
	private static final int MATCH_RULE_INDEX_MASK = SearchPattern.EXACT_MATCH | SearchPattern.PREFIX_MATCH
			| SearchPattern.PATTERN_MATCH | SearchPattern.CASE_SENSITIVE | SearchPattern.REGEX_MATCH;

	private static final Map<String, Pattern> PATTERNS = new HashMap<String, Pattern>();

	// Separator to use after the container path
	public static final char DEFAULT_SEPARATOR = '/';

	/**
	 * appendAsRegEx
	 * 
	 * @param pattern
	 * @param buffer
	 * @return
	 */
	private static StringBuffer appendAsRegEx(String pattern, StringBuffer buffer)
	{
		boolean isEscaped = false;

		for (int i = 0; i < pattern.length(); i++)
		{
			char c = pattern.charAt(i);

			switch (c)
			{
				// the backslash
				case '\\':
					// the backslash is escape char in string matcher
					if (!isEscaped)
					{
						isEscaped = true;
					}
					else
					{
						buffer.append("\\\\"); //$NON-NLS-1$
						isEscaped = false;
					}
					break;
				// characters that need to be escaped in the regex.
				case '(':
				case ')':
				case '{':
				case '}':
				case '.':
				case '[':
				case ']':
				case '$':
				case '^':
				case '+':
				case '|':
					if (isEscaped)
					{
						buffer.append("\\\\"); //$NON-NLS-1$
						isEscaped = false;
					}
					buffer.append('\\');
					buffer.append(c);
					break;
				case '?':
					if (!isEscaped)
					{
						buffer.append('.');
					}
					else
					{
						buffer.append('\\');
						buffer.append(c);
						isEscaped = false;
					}
					break;
				case '*':
					if (!isEscaped)
					{
						buffer.append(".*"); //$NON-NLS-1$
					}
					else
					{
						buffer.append('\\');
						buffer.append(c);
						isEscaped = false;
					}
					break;
				default:
					if (isEscaped)
					{
						buffer.append("\\\\"); //$NON-NLS-1$
						isEscaped = false;
					}
					buffer.append(c);
					break;
			}
		}

		if (isEscaped)
		{
			buffer.append("\\\\"); //$NON-NLS-1$
			isEscaped = false;
		}

		return buffer;
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
	 * isWordChar
	 * 
	 * @param c
	 * @return
	 */
	private static boolean isWordChar(char c)
	{
		return Character.isLetterOrDigit(c);
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
		if (pattern.equals("*")) //$NON-NLS-1$
			return true;

		// see if we've cached a regex for this pattern already
		Pattern p = PATTERNS.get(pattern);

		// nope, so try and create one
		if (p == null)
		{
			int len = pattern.length();
			StringBuffer buffer = new StringBuffer(len + 10);

			if (len > 0 && isWordChar(pattern.charAt(0)))
			{
				buffer.append("\\b"); //$NON-NLS-1$
			}

			appendAsRegEx(pattern, buffer);

			if (len > 0 && isWordChar(pattern.charAt(len - 1)))
			{
				buffer.append("\\b"); //$NON-NLS-1$
			}

			String regex = buffer.toString();

			p = Pattern.compile(regex);

			PATTERNS.put(pattern, p);
		}

		return (p != null) ? p.matcher(word).find() : false;
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
			}
		}

		return (pattern != null) ? pattern.matcher(word).find() : false;
	}

	public char separator = DEFAULT_SEPARATOR;

	private MemoryIndex memoryIndex;

	private DiskIndex diskIndex;

	public ReadWriteMonitor monitor;
	private URI containerURI;

	/**
	 * Index
	 * 
	 * @param containerURI
	 * @throws IOException
	 */
	public Index(URI containerURI) throws IOException
	{
		this.containerURI = containerURI;

		this.memoryIndex = new MemoryIndex();
		this.monitor = new ReadWriteMonitor();

		// Convert to a filename we can use for the actual index on disk
		IPath diskIndexPath = computeIndexLocation(containerURI);
		String diskIndexPathString = diskIndexPath.getDevice() == null ? diskIndexPath.toString() : diskIndexPath
				.toOSString();
		this.diskIndex = new DiskIndex(diskIndexPathString);
		this.diskIndex.initialize();
	}

	/**
	 * computeIndexLocation
	 * 
	 * @param containerPath
	 * @return
	 */
	private static IPath computeIndexLocation(URI containerPath)
	{
		CRC32 crc = new CRC32();
		crc.reset();
		crc.update(containerPath.toString().getBytes());
		String fileName = Long.toString(crc.getValue()) + ".index"; //$NON-NLS-1$
		return IndexActivator.getDefault().getStateLocation().append(fileName);
	}

	/**
	 * @deprecated
	 * @return
	 */
	public URI getRoot()
	{
		// TODO Remove this! JDT doesn't need it!
		return containerURI;
	}

	/**
	 * addEntry
	 * 
	 * @param category
	 * @param key
	 * @param containerRelativeURI
	 */
	public void addEntry(String category, String key, URI containerRelativeURI)
	{
		this.memoryIndex.addEntry(category, key, containerRelativeURI.toString());
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
	 * hasChanged
	 * 
	 * @return
	 */
	private boolean hasChanged()
	{
		return memoryIndex.hasChanged();
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

		// clear any cached regexes or patterns we might have used during the query
		PATTERNS.clear();

		return (results == null) ? null : new ArrayList<QueryResult>(results.values());
	}

	/**
	 * Remove all indices for a given document
	 * 
	 * @param containerRelativeURI
	 */
	public void remove(URI containerRelativeURI)
	{
		this.memoryIndex.remove(containerRelativeURI.toString());
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
			IndexActivator.logError("An error occurred while remove categories from the index", e);
		}
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
	 * Returns the document names that contain the given substring, if null then returns all of them.
	 */
	public Set<String> queryDocumentNames(String substring) throws IOException
	{
		Set<String> results;
		if (this.memoryIndex.hasChanged())
		{
			results = this.diskIndex.addDocumentNames(substring, this.memoryIndex);
			results.addAll(this.memoryIndex.addDocumentNames(substring));
		}
		else
		{
			results = this.diskIndex.addDocumentNames(substring, null);
		}
		return results;
	}

	public String toString()
	{
		return "Index for " + this.containerURI; //$NON-NLS-1$
	}
}
