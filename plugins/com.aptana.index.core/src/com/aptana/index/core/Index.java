/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.CRC32;

import org.eclipse.core.runtime.IPath;

import com.aptana.core.logging.IdeLog;
import com.aptana.internal.index.core.DiskIndex;
import com.aptana.internal.index.core.MemoryIndex;

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

		IndexPlugin plugin = IndexPlugin.getDefault();
		if (plugin != null)
		{
			IPath path = plugin.getStateLocation();
			if (path != null)
			{
				return path.append(fileName);
			}
		}
		return null;
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
		{
			return true;
		}

		int patternLength = pattern.length();
		int wordLength = word.length();

		if (patternLength == 0)
		{
			return matchRule != SearchPattern.EXACT_MATCH;
		}

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
		{
			return true;
		}

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

	private MemoryIndex memoryIndex;
	private DiskIndex diskIndex;
	ReadWriteLock monitor;
	private URI containerURI;

	/**
	 * Index
	 * 
	 * @param containerURI
	 * @param reuseExistingFile
	 * @throws IOException
	 */
	protected Index(URI containerURI, boolean reuseExistingFile) throws IOException
	{
		this.containerURI = containerURI;

		this.memoryIndex = new MemoryIndex();
		this.monitor = new ReentrantReadWriteLock();

		// Convert to a filename we can use for the actual index on disk
		IPath diskIndexPath = computeIndexLocation(containerURI);
		if (diskIndexPath == null)
		{
			return;
		}
		String diskIndexPathString = (diskIndexPath.getDevice() == null) ? diskIndexPath.toString() : diskIndexPath
				.toOSString();
		this.enterWrite();
		try
		{
			this.diskIndex = new DiskIndex(diskIndexPathString);
			this.diskIndex.initialize(reuseExistingFile);
		}
		finally
		{
			this.exitWrite();
		}
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
		this.enterWrite();
		try
		{
			this.memoryIndex.addEntry(category, key, containerRelativeURI.toString());
		}
		finally
		{
			this.exitWrite();
		}
	}

	/**
	 * deleteIndexFile
	 */
	void deleteIndexFile()
	{
		if (isTraceEnabled())
		{
			logTrace(MessageFormat.format("Deleting index ''{0}''", this)); //$NON-NLS-1$
		}

		// TODO Enter write?

		File indexFile = this.getIndexFile();
		if (indexFile != null && indexFile.exists())
		{
			indexFile.delete();
		}
	}

	protected static void logTrace(String msg)
	{
		IdeLog.logTrace(IndexPlugin.getDefault(), msg, IDebugScopes.INDEXER);
	}

	protected static boolean isTraceEnabled()
	{
		return IdeLog.isTraceEnabled(IndexPlugin.getDefault(), IDebugScopes.INDEXER);
	}

	/**
	 * Blocks to acquire the read lock.
	 */
	private void enterRead()
	{
		if (this.monitor != null)
		{
			this.monitor.readLock().lock();
		}
	}

	/**
	 * Blocks to acquire the write lock
	 */
	private void enterWrite()
	{
		if (this.monitor != null)
		{
			this.monitor.writeLock().lock();
		}
	}

	private void exitRead()
	{
		if (this.monitor != null)
		{
			this.monitor.readLock().unlock();
		}
	}

	/**
	 * This method does not guarantee that it will acquire the write lock. A boolean is returned indicating success.
	 * 
	 * @return
	 */
	private boolean exitReadEnterWrite()
	{
		boolean result = false;

		if (this.monitor != null)
		{
			monitor.readLock().unlock();
			result = monitor.writeLock().tryLock();
		}

		return result;
	}

	private void exitWrite()
	{
		if (this.monitor != null)
		{
			this.monitor.writeLock().unlock();
		}
	}

	/**
	 * This operation is _not_ atomic. This method will block until the read lock is acquired.
	 */
	private void exitWriteEnterRead()
	{
		if (this.monitor != null)
		{
			exitWrite();
			enterRead();
		}
	}

	/**
	 * getCategories
	 * 
	 * @return
	 */
	public List<String> getCategories()
	{
		Set<String> categories = new HashSet<String>();
		this.enterRead();
		try
		{
			categories.addAll(this.memoryIndex.getCategories());
			categories.addAll(this.diskIndex.getCategories());
		}
		finally
		{
			this.exitRead();
		}
		return new ArrayList<String>(categories);
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
	 * @deprecated
	 * @return
	 */
	public URI getRoot()
	{
		// TODO Remove this! JDT doesn't need it!
		return containerURI;
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
	public List<QueryResult> query(String[] categories, String key, int matchRule)
	{
		Map<String, QueryResult> results = null;

		try
		{
			// NOTE: I'd like to lock later in the method, but it would contort
			// the IReadWriteMonitor interface, so we lock here and stick with
			// the call to exitReadEnterWrite below
			this.enterRead();

			if (this.memoryIndex.shouldMerge() && this.exitReadEnterWrite())
			{
				// in write...
				try
				{
					this.save(false);
				}
				finally
				{
					this.exitWriteEnterRead();
				}
			}
			// We're in read mode for monitor here now matter what...
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
		}
		catch (IOException e)
		{
			if (e instanceof java.io.EOFException)
			{
				e.printStackTrace();
			}
			IdeLog.logError(IndexPlugin.getDefault(), e);
		}
		finally
		{
			this.exitRead();

			// clear any cached regexes or patterns we might have used during the query
			PATTERNS.clear();
		}

		return (results == null) ? null : new ArrayList<QueryResult>(results.values());
	}

	/**
	 * Returns the document names that contain the given substring, if null then returns all of them.
	 */
	public Set<String> queryDocumentNames(String substring) throws IOException
	{
		Set<String> results;
		this.enterRead();
		try
		{
			if (this.memoryIndex.hasChanged())
			{
				results = this.diskIndex.addDocumentNames(substring, this.memoryIndex);
				results.addAll(this.memoryIndex.addDocumentNames(substring));
			}
			else
			{
				results = this.diskIndex.addDocumentNames(substring, null);
			}
		}
		finally
		{
			this.exitRead();
		}
		return results;
	}

	/**
	 * Remove all indices for a given document
	 * 
	 * @param containerRelativeURI
	 */
	public void remove(URI containerRelativeURI)
	{
		String documentName = containerRelativeURI.toString();

		this.enterRead();
		try
		{
			if (isTraceEnabled() && memoryIndex.hasDocument(documentName))
			{
				// @formatter:off
				String message = MessageFormat.format("Removing URI ''{0}'' from index ''{1}''", //$NON-NLS-1$
						containerRelativeURI, this);
				// @formatter:on
				logTrace(message);
			}
		}
		finally
		{
			this.exitRead();
			this.enterWrite(); // we must wait for write! DO NOT CALL exitReadEnterWrite!
		}
		try
		{
			this.memoryIndex.remove(documentName);
		}
		finally
		{
			this.exitWrite();
		}
	}

	/**
	 * removeCategories
	 * 
	 * @param categoryNames
	 */
	public void removeCategories(String... categoryNames)
	{
		this.enterWrite();
		try
		{
			this.memoryIndex.removeCategories(categoryNames);
			this.diskIndex = this.diskIndex.removeCategories(categoryNames, this.memoryIndex);
		}
		catch (IOException e)
		{
			IdeLog.logError(IndexPlugin.getDefault(), "An error occurred while removing categories from the index", e); //$NON-NLS-1$
		}
		finally
		{
			this.exitWrite();
		}
	}

	/**
	 * save
	 * 
	 * @throws IOException
	 */
	public void save() throws IOException
	{
		if (isTraceEnabled())
		{
			logTrace(MessageFormat.format("Saving index ''{0}''", this)); //$NON-NLS-1$
		}

		this.save(true);
	}

	/**
	 * save
	 * 
	 * @param lock
	 * @throws IOException
	 */
	private void save(boolean lock) throws IOException
	{
		// NOTE: Unfortunately we need the ugly "lock" flag hack in order to
		// prevent hanging when save is called from query
		try
		{
			if (lock)
			{
				this.enterWrite();
			}

			// no need to do anything if the memory index hasn't changed
			if (!hasChanged())
			{
				return;
			}

			int numberOfChanges = this.memoryIndex.numberOfChanges();
			this.diskIndex = this.diskIndex.mergeWith(this.memoryIndex);
			this.memoryIndex = new MemoryIndex();

			if (numberOfChanges > 1000)
			{
				System.gc(); // reclaim space if the MemoryIndex was very BIG
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(IndexPlugin.getDefault(), e);
		}
		finally
		{
			if (lock)
			{
				this.exitWrite();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "Index for " + this.containerURI; //$NON-NLS-1$
	}

	public URI getRelativeDocumentPath(URI path)
	{
		return containerURI.relativize(path);
	}

	/**
	 * Reset memory and disk indexes.
	 * 
	 * @throws IOException
	 */
	public void reset() throws IOException
	{
		this.memoryIndex = new MemoryIndex();
		this.diskIndex = new DiskIndex(this.diskIndex.indexFile.getCanonicalPath());
		this.diskIndex.initialize(false/* do not reuse the index file */);
	}
}
