/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.internal.index.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UTFDataFormatException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexPlugin;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

/**
 * Yuck this needs to be a random access file that stores the index of documents, category names, and the relationship
 * between them. We need to be careful to allow for quick access into the categories when reading, and to keep filesize
 * down if possible.
 * 
 * @author cwilliams
 */
public class DiskIndex
{
	private static final String SIGNATURE = "INDEX VERSION 0.1"; //$NON-NLS-1$
	private static final int CHUNK_SIZE = 100;
	private static final int RE_INDEXED = -1;
	private static final int DELETED = -2;
	private static final boolean DEBUG = true;

	public File indexFile;
	private int headerInfoOffset;
	private int streamRead;
	private int numberOfChunks;
	private int sizeOfLastChunk;
	private int documentReferenceSize;
	private char separator = Index.DEFAULT_SEPARATOR;
	private int[] chunkOffsets;
	private int startOfCategoryTables;
	private Map<String, Integer> categoryOffsets;
	// Usually a map from string to map from string to list of integer. But may also be a single integer (to represent a
	// pointer to long array)
	// FIXME YUCK!! This "usually a list of integers, sometimes one integer that acts as a pointer" stuff is killing me!
	private Map<String, Map<String, Object>> categoryTables;

	private int streamEnd;
	private String[][] cachedChunks;
	private String[] categoriesToDiscard;

	/**
	 * DiskIndex
	 * 
	 * @param fileName
	 */
	public DiskIndex(String fileName)
	{
		this.indexFile = new File(fileName);

		// clear cached items
		this.headerInfoOffset = -1;
		this.numberOfChunks = -1;
		this.sizeOfLastChunk = -1;
		this.chunkOffsets = null;
		this.documentReferenceSize = -1;
		this.categoryTables = null;
		this.categoryOffsets = null;
		this.categoriesToDiscard = null;
	}

	/**
	 * addDocumentNames
	 * 
	 * @param substring
	 * @param memoryIndex
	 * @return
	 * @throws IOException
	 */
	public Set<String> addDocumentNames(String substring, MemoryIndex memoryIndex) throws IOException
	{
		// must skip over documents which have been added/changed/deleted in the memory index
		List<String> docNames = readAllDocumentNames();
		Set<String> results = new HashSet<String>(docNames.size());

		if (substring == null)
		{
			if (memoryIndex == null)
			{
				return new HashSet<String>(docNames);
			}

			Map<String, Map<String, Set<String>>> docsToRefs = memoryIndex.getDocumentsToReferences();

			for (String docName : docNames)
			{
				if (!docsToRefs.containsKey(docName))
				{
					results.add(docName);
				}
			}
		}
		else
		{
			if (memoryIndex == null)
			{
				for (String docName : docNames)
				{
					if (docName.startsWith(substring, 0))
					{
						results.add(docName);
					}
				}
			}
			else
			{
				Map<String, Map<String, Set<String>>> docsToRefs = memoryIndex.getDocumentsToReferences();

				for (String docName : docNames)
				{
					if (docName.startsWith(substring, 0) && !docsToRefs.containsKey(docName))
					{
						results.add(docName);
					}
				}
			}
		}

		return results;
	}

	/**
	 * addQueryResult
	 * 
	 * @param results
	 * @param word
	 * @param wordsToDocNumbers
	 * @param memoryIndex
	 * @return
	 * @throws IOException
	 */
	private Map<String, QueryResult> addQueryResult(Map<String, QueryResult> results, String word,
			Map<String, Object> wordsToDocNumbers, MemoryIndex memoryIndex) throws IOException
	{
		// must skip over documents which have been added/changed/deleted in the memory index
		if (results == null)
		{
			results = new HashMap<String, QueryResult>(13);
		}

		QueryResult result = results.get(word);

		if (memoryIndex == null)
		{
			if (result == null)
			{
				result = new QueryResult(word, null);
				results.put(word, result);
			}

			result.addDocumentTable(wordsToDocNumbers);

			List<Integer> docNumbers = readDocumentNumbers(wordsToDocNumbers.get(word));

			for (Integer docNumber : docNumbers)
			{
				result.addDocumentName(readDocumentName(docNumber));
			}
		}
		else
		{
			Map<String, Map<String, Set<String>>> docsToRefs = memoryIndex.getDocumentsToReferences();

			if (result == null)
			{
				result = new QueryResult(word, null);
			}

			List<Integer> docNumbers = readDocumentNumbers(wordsToDocNumbers.get(word));

			for (Integer docNumber : docNumbers)
			{
				String docName = readDocumentName(docNumber);

				if (!docsToRefs.containsKey(docName))
				{
					result.addDocumentName(docName);
				}
			}

			if (!result.isEmpty())
			{
				results.put(word, result);
			}
		}

		return results;
	}

	/**
	 * addQueryResults
	 * 
	 * @param categories
	 * @param key
	 * @param matchRule
	 * @param memoryIndex
	 * @return
	 * @throws IOException
	 */
	public Map<String, QueryResult> addQueryResults(String[] categories, String key, int matchRule,
			MemoryIndex memoryIndex) throws IOException
	{
		// assumes sender has called startQuery() & will call stopQuery() when finished
		if (this.categoryOffsets == null)
		{
			return null; // file is empty
		}

		Map<String, QueryResult> results = null; // initialized if needed

		// Add perf fixes for common ways of searching for everything:
		// PREFIX_MATCH with an empty key
		if ((matchRule == SearchPattern.PREFIX_MATCH || matchRule == (SearchPattern.PREFIX_MATCH | SearchPattern.CASE_SENSITIVE))
				&& StringUtil.EMPTY.equals(key))
		{
			key = null;
		}
		// PATTERN_MATCH with a key of "*"
		else if ((matchRule == SearchPattern.PATTERN_MATCH || matchRule == (SearchPattern.PATTERN_MATCH | SearchPattern.CASE_SENSITIVE))
				&& "*".equals(key)) //$NON-NLS-1$
		{
			key = null;
		}

		if (key == null)
		{
			for (int i = 0, l = categories.length; i < l; i++)
			{
				Map<String, Object> wordsToDocNumbers = readCategoryTable(categories[i], true); // cache if key
				// is null
				// since its a definite
				// match
				if (wordsToDocNumbers != null)
				{
					if (results == null)
					{
						results = new HashMap<String, QueryResult>(wordsToDocNumbers.size());
					}

					for (String word : wordsToDocNumbers.keySet())
					{
						if (word != null)
						{
							results = addQueryResult(results, word, wordsToDocNumbers, memoryIndex);
						}
					}
				}
			}

			if (results != null && this.cachedChunks == null)
			{
				cacheDocumentNames();
			}
		}
		else
		{
			switch (matchRule)
			{
				case SearchPattern.EXACT_MATCH | SearchPattern.CASE_SENSITIVE:
					for (int i = 0, l = categories.length; i < l; i++)
					{
						Map<String, Object> wordsToDocNumbers = readCategoryTable(categories[i], false);

						if (wordsToDocNumbers != null && wordsToDocNumbers.containsKey(key))
						{
							results = addQueryResult(results, key, wordsToDocNumbers, memoryIndex);
						}
					}
					break;

				case SearchPattern.PREFIX_MATCH | SearchPattern.CASE_SENSITIVE:
					for (int i = 0, l = categories.length; i < l; i++)
					{
						Map<String, Object> wordsToDocNumbers = readCategoryTable(categories[i], false);

						if (wordsToDocNumbers != null)
						{
							for (String word : wordsToDocNumbers.keySet())
							{
								if (word != null && word.startsWith(key))
								{
									results = addQueryResult(results, word, wordsToDocNumbers, memoryIndex);
								}
							}
						}
					}
					break;

				default:
					for (int i = 0, l = categories.length; i < l; i++)
					{
						Map<String, Object> wordsToDocNumbers = readCategoryTable(categories[i], false);

						if (wordsToDocNumbers != null)
						{
							for (String word : wordsToDocNumbers.keySet())
							{
								if (word != null && Index.isMatch(key, word, matchRule))
								{
									results = addQueryResult(results, word, wordsToDocNumbers, memoryIndex);
								}
							}
						}
					}
			}
		}

		return results;
	}

	/**
	 * cacheDocumentNames
	 * 
	 * @throws IOException
	 */
	private void cacheDocumentNames() throws IOException
	{
		// will need all document names so get them now
		this.cachedChunks = new String[this.numberOfChunks][];

		InputStream stream = new BufferedInputStream(new FileInputStream(this.indexFile));

		try
		{
			// if (this.numberOfChunks > 5)
			// BUFFER_READ_SIZE <<= 1;
			int offset = this.chunkOffsets[0];

			skip(stream, offset);

			for (int i = 0; i < this.numberOfChunks; i++)
			{
				int size = i == this.numberOfChunks - 1 ? this.sizeOfLastChunk : CHUNK_SIZE;

				readChunk(this.cachedChunks[i] = new String[size], stream, 0, size);
			}
		}
		catch (IOException e)
		{
			this.cachedChunks = null;
			throw e;
		}
		finally
		{
			stream.close();
		}
	}

	/**
	 * computeDocumentNames
	 * 
	 * @param onDiskNames
	 * @param positions
	 * @param indexedDocuments
	 * @param memoryIndex
	 * @return
	 */
	private List<String> computeDocumentNames(List<String> onDiskNames, int[] positions,
			Map<String, Integer> indexedDocuments, MemoryIndex memoryIndex)
	{
		int onDiskLength = onDiskNames.size();
		Map<String, Map<String, Set<String>>> memIndexDocs = memoryIndex.getDocumentsToReferences();

		if (onDiskLength == 0)
		{
			// disk index was empty, so add every indexed document
			for (Map.Entry<String, Map<String, Set<String>>> entry : memIndexDocs.entrySet())
			{
				Map<String, Set<String>> refTable = entry.getValue();

				if (refTable != null)
				{
					indexedDocuments.put(entry.getKey(), null); // remember each new document
				}
			}

			List<String> newDocNames = new ArrayList<String>(indexedDocuments.size());
			Set<String> added = indexedDocuments.keySet();

			for (String adddedString : added)
			{
				if (adddedString != null)
				{
					newDocNames.add(adddedString);
				}
			}

			Collections.sort(newDocNames);

			for (int i = 0, l = newDocNames.size(); i < l; i++)
			{
				indexedDocuments.put(newDocNames.get(i), new Integer(i));
			}

			return newDocNames;
		}

		// initialize positions as if each document will remain in the same position
		for (int i = 0; i < onDiskLength; i++)
		{
			positions[i] = i;
		}

		// find out if the memory index has any new or deleted documents, if not then the names & positions are the same
		int numDeletedDocNames = 0;
		int numReindexedDocNames = 0;

		nextPath: for (Map.Entry<String, Map<String, Set<String>>> entry : memIndexDocs.entrySet())
		{
			String docName = entry.getKey();

			if (docName == null)
			{
				continue;
			}

			for (int j = 0; j < onDiskLength; j++)
			{
				if (docName.equals(onDiskNames.get(j)))
				{
					if (entry.getValue() == null)
					{
						positions[j] = DELETED;
						numDeletedDocNames++;
					}
					else
					{
						positions[j] = RE_INDEXED;
						numReindexedDocNames++;
					}

					continue nextPath;
				}
			}

			if (entry.getValue() != null)
			{
				indexedDocuments.put(docName, null); // remember each new document, skip deleted documents which were
			}
			// never saved
		}

		List<String> newDocNames = onDiskNames;

		if (numDeletedDocNames > 0 || indexedDocuments.size() > 0)
		{
			// some new documents have been added or some old ones deleted
			newDocNames = new ArrayList<String>(onDiskLength + indexedDocuments.size() - numDeletedDocNames);

			for (int i = 0; i < onDiskLength; i++)
			{
				if (positions[i] >= RE_INDEXED)
				{
					newDocNames.add(onDiskNames.get(i)); // keep each unchanged document
				}
			}

			Set<String> added = indexedDocuments.keySet();

			for (String addedString : added)
			{
				if (addedString != null)
				{
					newDocNames.add(addedString); // add each new document
				}
			}

			Collections.sort(newDocNames);

			for (int i = 0, l = newDocNames.size(); i < l; i++)
			{
				if (indexedDocuments.containsKey(newDocNames.get(i)))
				{
					indexedDocuments.put(newDocNames.get(i), new Integer(i)); // remember the position for each new
				}
			}
			// document
		}

		// need to be able to look up an old position (ref# from a ref[]) and map it to its new position
		// if its old position == DELETED then its forgotton
		// if its old position == ReINDEXED then its also forgotten but its new position is needed to map references
		int count = -1;

		for (int i = 0; i < onDiskLength;)
		{
			switch (positions[i])
			{
				case DELETED:
					i++; // skip over deleted... references are forgotten
					break;

				case RE_INDEXED:
					String newName = newDocNames.get(++count);
					if (newName.equals(onDiskNames.get(i)))
					{
						indexedDocuments.put(newName, new Integer(count)); // the reindexed docName that was at position
						// i is now at position count
						i++;
					}
					break;

				default:
					if (newDocNames.get(++count).equals(onDiskNames.get(i)))
					{
						positions[i++] = count; // the unchanged docName that was at position i is now at position count
					}
			}
		}

		return newDocNames;
	}

	/**
	 * copyQueryResults
	 * 
	 * @param categoryToWords
	 * @param newPosition
	 */
	@SuppressWarnings("unchecked")
	private void copyQueryResults(Map<String, Set<String>> categoryToWords, int newPosition)
	{
		for (Map.Entry<String, Set<String>> entry : categoryToWords.entrySet())
		{
			String categoryName = entry.getKey();

			if (categoryName == null)
			{
				continue;
			}

			Map<String, Object> wordsToDocs = this.categoryTables.get(categoryName);

			if (wordsToDocs == null)
			{
				this.categoryTables.put(categoryName, wordsToDocs = new HashMap<String, Object>());
			}

			for (String word : entry.getValue())
			{
				if (word == null)
				{
					continue;
				}

				Object positions = wordsToDocs.get(word);

				if (positions == null)
				{
					wordsToDocs.put(word, positions = new ArrayList<Integer>());
				}

				((List<Integer>) positions).add(newPosition);
			}
		}
	}

	/**
	 * getCategories
	 * 
	 * @return
	 */
	public List<String> getCategories()
	{
		List<String> result;

		if (categoryOffsets == null)
		{
			result = Collections.emptyList();
		}
		else
		{
			result = new ArrayList<String>(categoryOffsets.keySet());
		}

		return result;
	}

	/**
	 * getDocuments
	 * 
	 * @return
	 */
	public List<String> getDocuments()
	{
		List<String> result = Collections.emptyList();

		try
		{
			result = this.readAllDocumentNames();
		}
		catch (IOException e)
		{
		}

		return result;
	}

	/**
	 * initialize
	 * 
	 * @throws IOException
	 */
	public void initialize(boolean reuseExistingFile) throws IOException
	{
		if (this.indexFile.exists())
		{
			if (reuseExistingFile)
			{
				// read it in!
				InputStream stream = new BufferedInputStream(new FileInputStream(this.indexFile));

				try
				{
					streamRead = 0;

					String signature = readString(stream);

					if (!signature.equals(SIGNATURE))
					{
						throw new IOException(Messages.DiskIndex_Wrong_Format);
					}

					this.headerInfoOffset = readStreamInt(stream);

					if (this.headerInfoOffset > 0)
					{ // file is empty if its not set
						skip(stream, this.headerInfoOffset - this.streamRead); // assume that the header info offset is
						// over
						// current buffer end
						readHeaderInfo(stream);
					}
				}
				finally
				{
					stream.close();
				}
				return;
			}

			if (!this.indexFile.delete())
			{
				if (DEBUG)
				{
					System.out.println("initialize - Failed to delete index " + this.indexFile); //$NON-NLS-1$
				}

				throw new IOException("Failed to delete index " + this.indexFile); //$NON-NLS-1$
			}
		}

		// create a new empty one!
		if (indexFile.createNewFile())
		{
			FileOutputStream stream = new FileOutputStream(this.indexFile, false);

			try
			{
				writeString(stream, SIGNATURE);
				stream.write(-1);
			}
			finally
			{
				stream.close();
			}
		}
		else
		{
			if (DEBUG)
			{
				System.out.println("initialize - Failed to create new index " + this.indexFile); //$NON-NLS-1$
			}

			throw new IOException(Messages.DiskIndex_Unable_To_Create_Index_File + indexFile);
		}
	}

	/**
	 * initializeFrom
	 * 
	 * @param diskIndex
	 * @param newIndexFile
	 * @throws IOException
	 */
	private void initializeFrom(DiskIndex diskIndex, File newIndexFile) throws IOException
	{
		if (newIndexFile.exists() && !newIndexFile.delete())
		{ // delete the temporary index file
			if (DEBUG)
			{
				System.out.println("initializeFrom - Failed to delete temp index " + this.indexFile); //$NON-NLS-1$
			}
		}
		else if (!newIndexFile.createNewFile())
		{
			if (DEBUG)
			{
				System.out.println("initializeFrom - Failed to create temp index " + this.indexFile); //$NON-NLS-1$
			}

			throw new IOException("Failed to create temp index " + this.indexFile); //$NON-NLS-1$
		}

		int size = diskIndex.categoryOffsets == null ? 8 : diskIndex.categoryOffsets.size();
		this.categoryOffsets = new HashMap<String, Integer>(size);
		this.categoryTables = new HashMap<String, Map<String, Object>>(size);
		this.separator = diskIndex.separator;
		this.categoriesToDiscard = diskIndex.categoriesToDiscard;
	}

	/**
	 * mergeCategories
	 * 
	 * @param onDisk
	 * @param positions
	 * @param stream
	 * @throws IOException
	 */
	private void mergeCategories(DiskIndex onDisk, int[] positions, OutputStream stream) throws IOException
	{
		// at this point, this.categoryTables contains the names -> wordsToDocs added in copyQueryResults()
		Set<String> oldNames = onDisk.categoryOffsets.keySet();

		for (String oldName : oldNames)
		{
			if (oldName != null && !this.categoryTables.containsKey(oldName))
			{
				this.categoryTables.put(oldName, null);
			}
		}

		Set<String> categoryNames = this.categoryTables.keySet();

		for (String categoryName : categoryNames)
		{
			if (categoryName != null)
			{
				mergeCategory(categoryName, onDisk, positions, stream);
			}
		}

		this.categoryTables = null;
	}

	/**
	 * mergeCategory
	 * 
	 * @param categoryName
	 * @param onDisk
	 * @param positions
	 * @param stream
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void mergeCategory(String categoryName, DiskIndex onDisk, int[] positions, OutputStream stream)
			throws IOException
	{
		Map<String, Object> wordsToDocs = this.categoryTables.get(categoryName);

		if (wordsToDocs == null)
		{
			wordsToDocs = new HashMap<String, Object>(3);
		}

		Map<String, Object> oldWordsToDocs = onDisk.readCategoryTable(categoryName, true);

		if (oldWordsToDocs != null)
		{
			nextWord: for (Map.Entry<String, Object> entry : oldWordsToDocs.entrySet())
			{
				String oldWord = entry.getKey();

				if (oldWord == null)
				{
					continue;
				}

				List<Integer> oldDocNumbers = (List<Integer>) entry.getValue();
				List<Integer> mappedNumbers = new ArrayList<Integer>(oldDocNumbers.size());

				for (Integer oldDocNumber : oldDocNumbers)
				{
					int pos = positions[oldDocNumber];

					if (pos > RE_INDEXED) // forget any reference to a document which was deleted or re_indexed
					{
						mappedNumbers.add(pos);
					}
				}

				if (mappedNumbers.isEmpty())
				{
					continue nextWord; // skip words which no longer have any references
				}

				Object o = wordsToDocs.get(oldWord);

				if (o == null)
				{
					wordsToDocs.put(oldWord, mappedNumbers);
				}
				else
				{
					List<Integer> list = null;

					if (o instanceof List<?>)
					{
						list = (List<Integer>) o;
					}
					else
					{
						list = new ArrayList<Integer>();
						wordsToDocs.put(oldWord, list);
					}

					list.addAll(mappedNumbers);
				}
			}

			onDisk.categoryTables.put(categoryName, null); // flush cached table
		}

		writeCategoryTable(categoryName, wordsToDocs, stream);
	}

	/**
	 * mergeWith
	 * 
	 * @param memoryIndex
	 * @return
	 * @throws IOException
	 */
	public DiskIndex mergeWith(MemoryIndex memoryIndex) throws IOException
	{
		// assume write lock is held
		// compute & write out new docNames
		List<String> names = readAllDocumentNames();
		int previousLength = names.size();
		int[] positions = new int[previousLength]; // keeps track of the position of each document in the new sorted
		// docNames
		Map<String, Integer> indexedDocuments = new HashMap<String, Integer>(3); // for each new/changed document in the
		// memoryIndex

		names = computeDocumentNames(names, positions, indexedDocuments, memoryIndex);

		if (names.isEmpty())
		{
			if (previousLength == 0)
			{
				return this; // nothing to do... memory index contained deleted documents that had never been saved
			}

			// index is now empty since all the saved documents were removed
			DiskIndex newDiskIndex = new DiskIndex(this.indexFile.getPath());
			newDiskIndex.initialize(false);

			return newDiskIndex;
		}

		this.streamEnd = 0;

		DiskIndex newDiskIndex = new DiskIndex(this.indexFile.getPath() + ".tmp"); //$NON-NLS-1$

		try
		{
			newDiskIndex.initializeFrom(this, newDiskIndex.indexFile);
			OutputStream stream = new BufferedOutputStream(new FileOutputStream(newDiskIndex.indexFile, false));
			int offsetToHeader = -1;

			try
			{
				newDiskIndex.writeDocumentNames(stream, names);
				names = null;

				// add each new/changed doc to empty category tables using its new position #
				if (!indexedDocuments.isEmpty())
				{
					for (Map.Entry<String, Integer> entry : indexedDocuments.entrySet())
					{
						if (entry.getKey() != null)
						{
							newDiskIndex.copyQueryResults(memoryIndex.getCategoriesForDocument(entry.getKey()),
									entry.getValue());
						}
					}
				}

				indexedDocuments = null; // free up the space

				// TODO Check list of categories we wanted removed and wipe them out of "categoryTables"?

				// merge each category table with the new ones & write them out
				if (previousLength == 0)
				{
					newDiskIndex.writeCategories(stream);
				}
				else
				{
					newDiskIndex.mergeCategories(this, positions, stream);
				}
				// write header
				offsetToHeader = newDiskIndex.streamEnd;
				newDiskIndex.writeHeaderInfo(stream);
				positions = null; // free up the space
			}
			finally
			{
				stream.close();
			}

			newDiskIndex.writeOffsetToHeader(offsetToHeader);

			// rename file by deleting previous index file & renaming temp one
			if (this.indexFile.exists() && !this.indexFile.delete())
			{
				throw new IOException("Failed to delete index file " + this.indexFile); //$NON-NLS-1$
			}

			if (!newDiskIndex.indexFile.renameTo(this.indexFile))
			{
				throw new IOException("Failed to rename index file " + this.indexFile); //$NON-NLS-1$
			}
		}
		catch (IOException e)
		{
			if (newDiskIndex.indexFile.exists() && !newDiskIndex.indexFile.delete())
			{
				if (DEBUG)
				{
					System.out.println("mergeWith - Failed to delete temp index " + newDiskIndex.indexFile); //$NON-NLS-1$
				}
			}

			throw e;
		}

		newDiskIndex.indexFile = this.indexFile;

		return newDiskIndex;
	}

	/**
	 * read
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	private int read(InputStream stream) throws IOException
	{
		int val = stream.read();

		streamRead++;

		return val;
	}

	/**
	 * readAllDocumentNames
	 * 
	 * @return
	 * @throws IOException
	 */
	private synchronized List<String> readAllDocumentNames() throws IOException
	{
		if (this.numberOfChunks <= 0)
		{
			return Collections.emptyList();
		}

		InputStream stream = new BufferedInputStream(new FileInputStream(this.indexFile));

		try
		{
			int offset = this.chunkOffsets[0];

			skip(stream, offset);

			int lastIndex = this.numberOfChunks - 1;

			String[] docNames = new String[lastIndex * CHUNK_SIZE + sizeOfLastChunk];

			for (int i = 0; i < this.numberOfChunks; i++)
			{
				readChunk(docNames, stream, i * CHUNK_SIZE, i < lastIndex ? CHUNK_SIZE : sizeOfLastChunk);
			}

			return Arrays.asList(docNames);
		}
		finally
		{
			stream.close();
		}
	}

	/**
	 * readCategoryTable
	 * 
	 * @param categoryName
	 * @param readDocNumbers
	 * @return
	 * @throws IOException
	 */
	private synchronized Map<String, Object> readCategoryTable(String categoryName, boolean readDocNumbers)
			throws IOException
	{
		// result will be null if categoryName is unknown
		Integer offset = this.categoryOffsets.get(categoryName);

		if (offset == null)
		{
			return null;
		}

		if (this.categoryTables == null)
		{
			this.categoryTables = new HashMap<String, Map<String, Object>>(3);
		}
		else
		{
			Map<String, Object> cachedTable = this.categoryTables.get(categoryName);

			if (cachedTable != null)
			{
				if (readDocNumbers)
				{ // must cache remaining document number arrays
					Map<String, Object> copy = new HashMap<String, Object>(cachedTable);

					for (Map.Entry<String, Object> entry : cachedTable.entrySet())
					{
						Object arrayOffset = entry.getValue();

						if (arrayOffset instanceof Integer)
						{
							copy.put(entry.getKey(), readDocumentNumbers(arrayOffset));
						}
					}

					cachedTable = copy;
				}

				return cachedTable;
			}
		}

		InputStream stream = new BufferedInputStream(new FileInputStream(this.indexFile));
		Map<String, Object> categoryTable = null;
		String[] matchingWords = null;
		int count = 0;
		int firstOffset = -1;

		try
		{
			// skip to start of category
			skip(stream, offset);

			// Read the number of words in the category
			int size = readStreamInt(stream);

			try
			{
				if (size < 0)
				{
					// DEBUG
					StringBuilder debug = new StringBuilder();
					debug.append("-------------------- DEBUG --------------------\n"); //$NON-NLS-1$
					debug.append("file = ").append(this.indexFile).append('\n'); //$NON-NLS-1$
					debug.append("offset = ").append(offset).append('\n'); //$NON-NLS-1$
					debug.append("size = ").append(size).append('\n'); //$NON-NLS-1$
					debug.append("--------------------   END   --------------------\n"); //$NON-NLS-1$
					System.err.print(debug.toString());
					IdeLog.logError(
							IndexPlugin.getDefault(),
							MessageFormat.format(
									"Size of category ''{0}'' negative:\n{1}", categoryName, debug.toString())); //$NON-NLS-1$
					size = 0;
				}

				categoryTable = new HashMap<String, Object>(size);
			}
			catch (OutOfMemoryError oom)
			{
				// Here we hope that those string allocation will not throw another OutOfMemoryError...
				// DEBUG
				oom.printStackTrace();
				String error = MessageFormat
						.format("Index error in readCategoryTable. \nfile = {0} \noffset = {1} \n size = {2}", this.indexFile, offset, size); //$NON-NLS-1$
				System.err.println("-------------------- DEBUG --------------------\n"); //$NON-NLS-1$
				System.err.println(error);
				System.err.println("--------------------   END   --------------------\n"); //$NON-NLS-1$
				IdeLog.logError(IndexPlugin.getDefault(), error, oom);
				throw oom;
			}

			int largeArraySize = 256;

			for (int i = 0; i < size; i++)
			{
				String word = readString(stream);
				int arrayOffset = readStreamInt(stream);

				// if arrayOffset is:
				// <= 0 then the array size == 1 with the value -> -arrayOffset
				// > 1 & < 256 then the size of the array is > 1 & < 256, the document array follows immediately
				// 256 if the array size >= 256 followed by another int which is the offset to the array (written prior
				// to the table)
				if (arrayOffset <= 0)
				{
					categoryTable.put(word, CollectionsUtil.newList(-arrayOffset)); // store 1 element array by negating
					// documentNumber
				}
				else if (arrayOffset < largeArraySize)
				{
					categoryTable.put(word, readStreamDocumentArray(stream, arrayOffset)); // read in-lined array
					// providing size
				}
				else
				{
					arrayOffset = readStreamInt(stream); // read actual offset

					if (readDocNumbers)
					{
						if (matchingWords == null)
						{
							matchingWords = new String[size];
						}

						if (count == 0)
						{
							firstOffset = arrayOffset;
						}

						matchingWords[count++] = word;
					}

					categoryTable.put(word, new Integer(arrayOffset)); // offset to array in the file
				}
			}

			this.categoryTables.put(categoryName, categoryTable);
		}
		catch (IOException ioe)
		{
			throw ioe;
		}
		finally
		{
			stream.close();
		}

		if (matchingWords != null && count > 0)
		{
			stream = new BufferedInputStream(new FileInputStream(this.indexFile));

			try
			{
				skip(stream, firstOffset);

				for (int i = 0; i < count; i++)
				{ // each array follows the previous one
					categoryTable.put(matchingWords[i], readStreamDocumentArray(stream, readStreamInt(stream)));
				}
			}
			catch (IOException ioe)
			{
				throw ioe;
			}
			finally
			{
				stream.close();
			}
		}

		return categoryTable;
	}

	/**
	 * readChunk
	 * 
	 * @param docNames
	 * @param stream
	 * @param index
	 * @param size
	 * @throws IOException
	 */
	private void readChunk(String[] docNames, InputStream stream, int index, int size) throws IOException
	{
		String current = readString(stream);

		docNames[index++] = current;

		for (int i = 1; i < size; i++)
		{
			int start = read(stream) & 0xFF;
			int end = read(stream) & 0xFF;
			String next = readString(stream);

			if (start > 0)
			{
				if (end > 0)
				{
					int length = current.length();

					next = current.substring(0, start) + next + current.substring(length - end, length);
				}
				else
				{
					next = current.substring(0, start) + next;
				}
			}
			else if (end > 0)
			{
				int length = current.length();

				next = next + current.substring(length - end, length);
			}

			docNames[index++] = next;
			current = next;
		}
	}

	/**
	 * readDocumentName
	 * 
	 * @param docNumber
	 * @return
	 * @throws IOException
	 */
	private synchronized String readDocumentName(int docNumber) throws IOException
	{
		if (this.cachedChunks == null)
		{
			this.cachedChunks = new String[this.numberOfChunks][];
		}

		int chunkNumber = docNumber / CHUNK_SIZE;
		String[] chunk = this.cachedChunks[chunkNumber];

		if (chunk == null)
		{
			boolean isLastChunk = chunkNumber == this.numberOfChunks - 1;
			int start = this.chunkOffsets[chunkNumber];
			int numberOfBytes = (isLastChunk ? this.startOfCategoryTables : this.chunkOffsets[chunkNumber + 1]) - start;

			if (numberOfBytes < 0)
			{
				throw new IllegalArgumentException();
			}

			InputStream file = new BufferedInputStream(new FileInputStream(this.indexFile));

			try
			{
				skip(file, start);

				int numberOfNames = isLastChunk ? this.sizeOfLastChunk : CHUNK_SIZE;

				chunk = new String[numberOfNames];
				readChunk(chunk, file, 0, numberOfNames);
			}
			catch (IOException ioe)
			{
				throw ioe;
			}
			finally
			{
				file.close();
			}

			this.cachedChunks[chunkNumber] = chunk;
		}

		return chunk[docNumber - (chunkNumber * CHUNK_SIZE)];
	}

	/**
	 * readDocumentNumbers
	 * 
	 * @param arrayOffset
	 * @return
	 * @throws IOException
	 */
	private @SuppressWarnings("unchecked")
	synchronized List<Integer> readDocumentNumbers(Object arrayOffset) throws IOException
	{
		// arrayOffset is either a cached array of docNumbers or an Integer offset in the file
		if (arrayOffset instanceof List<?>)
		{
			return (List<Integer>) arrayOffset;
		}

		InputStream stream = new BufferedInputStream(new FileInputStream(this.indexFile));

		try
		{
			int offset = ((Integer) arrayOffset).intValue();

			skip(stream, offset);

			return readStreamDocumentArray(stream, readStreamInt(stream));
		}
		finally
		{
			stream.close();
		}
	}

	/**
	 * readHeaderInfo
	 * 
	 * @param stream
	 * @throws IOException
	 */
	private void readHeaderInfo(InputStream stream) throws IOException
	{
		// must be same order as writeHeaderInfo()
		this.numberOfChunks = readStreamInt(stream);
		if (this.numberOfChunks < 0)
		{
			throw new IOException(MessageFormat.format("Corrupt index file, reported {0} chunks", numberOfChunks)); //$NON-NLS-1$
		}
		this.sizeOfLastChunk = read(stream) & 0xFF;
		this.documentReferenceSize = read(stream) & 0xFF;
		this.separator = (char) (read(stream) & 0xFF);

		this.chunkOffsets = new int[this.numberOfChunks];
		for (int i = 0; i < this.numberOfChunks; i++)
		{
			this.chunkOffsets[i] = readStreamInt(stream);
		}

		this.startOfCategoryTables = readStreamInt(stream);

		// Build the table of categories to offsets where they start
		int categoryCount = readStreamInt(stream);
		this.categoryOffsets = new HashMap<String, Integer>(categoryCount);
		for (int i = 0; i < categoryCount; i++)
		{
			String categoryName = readString(stream);
			int offset = readStreamInt(stream);
			this.categoryOffsets.put(categoryName, offset); // cache offset to category table
		}

		this.categoryTables = new HashMap<String, Map<String, Object>>(3);
	}

	/**
	 * readStreamDocumentArray
	 * 
	 * @param stream
	 * @param arraySize
	 * @return
	 * @throws IOException
	 */
	private List<Integer> readStreamDocumentArray(InputStream stream, int arraySize) throws IOException
	{
		if (arraySize == 0)
		{
			return Collections.emptyList();
		}

		List<Integer> indexes = new ArrayList<Integer>();

		for (int i = 0; i < arraySize; i++)
		{
			int value = 0;

			switch (this.documentReferenceSize)
			{
				case 1:
					value = read(stream) & 0xFF;
					break;

				case 2:
					value = (read(stream) & 0xFF) << 8;
					value = value + (read(stream) & 0xFF);
					break;

				default:
					value = readStreamInt(stream);
					break;
			}

			indexes.add(value);
		}

		return indexes;
	}

	/**
	 * readStreamInt
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	private int readStreamInt(InputStream stream) throws IOException
	{
		int val = (read(stream) & 0xFF) << 24;

		val += (read(stream) & 0xFF) << 16;
		val += (read(stream) & 0xFF) << 8;

		return val + (read(stream) & 0xFF);
	}

	/**
	 * readString
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	private String readString(InputStream stream) throws IOException
	{
		int length = (read(stream) & 0xFF) << 8;
		length += read(stream) & 0xFF;

		// fill the chars from bytes buffer
		char[] word = new char[length];
		int i = 0;

		while (i < length)
		{
			byte b = (byte) read(stream);

			switch (b & 0xF0)
			{
				case 0x00:
				case 0x10:
				case 0x20:
				case 0x30:
				case 0x40:
				case 0x50:
				case 0x60:
				case 0x70:
					word[i++] = (char) b;
					break;

				case 0xC0:
				case 0xD0:
					char next = (char) read(stream);

					if ((next & 0xC0) != 0x80)
					{
						throw new UTFDataFormatException();
					}

					char ch = (char) ((b & 0x1F) << 6);

					ch |= next & 0x3F;
					word[i++] = ch;
					break;

				case 0xE0:
					char first = (char) read(stream);
					char second = (char) read(stream);

					if ((first & second & 0xC0) != 0x80)
					{
						throw new UTFDataFormatException();
					}

					ch = (char) ((b & 0x0F) << 12);
					ch |= ((first & 0x3F) << 6);
					ch |= second & 0x3F;
					word[i++] = ch;
					break;

				default:
					throw new UTFDataFormatException(
							MessageFormat
									.format("Unexpected byte value ''{0}'' at index {1}, reading string of length {2}. Read so far: ''{3}''. Possibly corrupt index file: ''{4}''", //$NON-NLS-1$
											b, i, length, new String(word), indexFile.getAbsolutePath()));
			}
		}

		return new String(word);
	}

	/**
	 * removeCategories
	 * 
	 * @param categoryNames
	 * @param memoryIndex
	 * @return
	 * @throws IOException
	 */
	public DiskIndex removeCategories(String[] categoryNames, MemoryIndex memoryIndex) throws IOException
	{
		// FIXME We need to wipe out the category from the file somehow! The problem is that we can drop a document/file
		// fairly easily by setting it's mapping to null in memory index and merging, but the way the thing is set up we
		// don't really have an easy way of wiping a category out from memory and disk right now.

		// int offset = this.categoryOffsets.get(categoryName);
		// // "Wipe out" from the category offset to next category offset. However we need to handle the "long arrays"
		// written just before offsets!
		// RandomAccessFile file = new RandomAccessFile(this.indexFile, "rw");
		// file.seek(offset);

		this.categoriesToDiscard = categoryNames;

		DiskIndex newIndex = mergeWith(memoryIndex);

		newIndex.categoriesToDiscard = null;

		return newIndex;
	}

	/**
	 * skip
	 * 
	 * @param stream
	 * @param n
	 * @return
	 * @throws IOException
	 */
	private long skip(InputStream stream, long n) throws IOException
	{
		final long wantToSkip = n;
		long actuallySkipped = 0;

		while (n > 0)
		{
			long skipped = stream.skip(n);

			actuallySkipped += skipped;
			n = n - skipped;

			if (actuallySkipped == wantToSkip)
			{
				return actuallySkipped;
			}

			int read = stream.read();

			if (read == -1)
			{
				return actuallySkipped;
			}

			n--;
			actuallySkipped++;
		}

		return actuallySkipped;
	}

	/**
	 * writeCategories
	 * 
	 * @param stream
	 * @throws IOException
	 */
	private void writeCategories(OutputStream stream) throws IOException
	{
		for (Map.Entry<String, Map<String, Object>> entry : categoryTables.entrySet())
		{
			String categoryName = entry.getKey();

			if (categoryName != null)
			{
				writeCategoryTable(categoryName, entry.getValue(), stream);
			}
		}

		this.categoryTables = null;
	}

	/**
	 * writeCategoryTable
	 * 
	 * @param categoryName
	 * @param wordsToDocs
	 * @param stream
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void writeCategoryTable(String categoryName, Map<String, Object> wordsToDocs, OutputStream stream)
			throws IOException
	{
		if (this.categoriesToDiscard != null)
		{
			for (String categoryToDiscard : categoriesToDiscard)
			{
				if (categoryName.equals(categoryToDiscard))
				{
					return;
				}
			}
		}

		// the format of a category table is as follows:
		// any document number arrays with >= 256 elements are written before the table (the offset to each array is
		// remembered)
		// then the number of word->int[] pairs in the table is written
		// for each word -> int[] pair, the word is written followed by:
		// an int <= 0 if the array size == 1
		// an int > 1 & < 256 for the size of the array if its > 1 & < 256, the document array follows immediately
		// 256 if the array size >= 256 followed by another int which is the offset to the array (written prior to the
		// table)

		Map<String, Integer> longArrays = new HashMap<String, Integer>();
		int largeArraySize = 256;

		for (Map.Entry<String, Object> entry : wordsToDocs.entrySet())
		{
			List<Integer> docNumbers = (List<Integer>) entry.getValue();

			if (docNumbers.size() >= largeArraySize)
			{
				longArrays.put(entry.getKey(), new Integer(this.streamEnd));
				writeDocumentNumbers(docNumbers, stream);
			}
		}

		this.categoryOffsets.put(categoryName, this.streamEnd); // remember the offset to the start of the table
		this.categoryTables.put(categoryName, null); // flush cached table
		writeStreamInt(stream, wordsToDocs.size());

		for (Map.Entry<String, Object> entry : wordsToDocs.entrySet())
		{
			try
			{
				writeString(stream, entry.getKey());

				if (longArrays.containsKey(entry.getKey()))
				{
					writeStreamInt(stream, largeArraySize); // mark to identify that an offset follows
					writeStreamInt(stream, longArrays.get(entry.getKey()).intValue()); // offset in the file of the
																						// array of
					// document numbers
				}
				else
				{
					List<Integer> documentNumbers = (List<Integer>) entry.getValue();

					if (documentNumbers.size() == 1)
					{
						writeStreamInt(stream, -documentNumbers.get(0));
					}
					else
					{
						writeDocumentNumbers(documentNumbers, stream);
					}
				}
			}
			catch (IOException ioe)
			{
				// We may have run into the JS indexing issue: https://jira.appcelerator.org/browse/APSTUD-7366
				IdeLog.logError(IndexPlugin.getDefault(), ioe);
				// To limit the damage, we're going to effectively skip writing one entry into the index. This will
				// break our knowledge of some property/type in JS but will allow indexing to continue.
			}
		}
	}

	/**
	 * writeDocumentNames
	 * 
	 * @param stream
	 * @param sortedDocNames
	 * @throws IOException
	 */
	private void writeDocumentNames(OutputStream stream, List<String> sortedDocNames) throws IOException
	{
		writeString(stream, SIGNATURE);
		this.headerInfoOffset = this.streamEnd;
		writeStreamInt(stream, -1);

		int size = sortedDocNames.size();
		this.numberOfChunks = (size / CHUNK_SIZE) + 1;
		this.sizeOfLastChunk = size % CHUNK_SIZE;

		if (this.sizeOfLastChunk == 0)
		{
			this.numberOfChunks--;
			this.sizeOfLastChunk = CHUNK_SIZE;
		}

		this.documentReferenceSize = size <= 0x7F ? 1 : (size <= 0x7FFF ? 2 : 4); // number of bytes used to encode a
		// reference

		this.chunkOffsets = new int[this.numberOfChunks];
		int lastIndex = this.numberOfChunks - 1;

		for (int i = 0; i < this.numberOfChunks; i++)
		{
			this.chunkOffsets[i] = this.streamEnd;

			int chunkSize = i == lastIndex ? this.sizeOfLastChunk : CHUNK_SIZE;
			int chunkIndex = i * CHUNK_SIZE;

			String current = sortedDocNames.get(chunkIndex);
			writeString(stream, current);

			for (int j = 1; j < chunkSize; j++)
			{
				String next = sortedDocNames.get(chunkIndex + j);
				int len1 = current.length();
				int len2 = next.length();
				int max = len1 < len2 ? len1 : len2;
				int start = 0; // number of identical characters at the beginning (also the index of first character
				// that is different)

				while (current.charAt(start) == next.charAt(start))
				{
					start++;

					if (max == start)
					{
						break; // current is 'abba', next is 'abbab'
					}
				}

				if (start > 255)
				{
					start = 255;
				}

				int end = 0; // number of identical characters at the end

				while (current.charAt(--len1) == next.charAt(--len2))
				{
					end++;

					if (len2 == start)
					{
						break; // current is 'abbba', next is 'abba'
					}

					if (len1 == 0)
					{
						break; // current is 'xabc', next is 'xyabc'
					}
				}

				if (end > 255)
				{
					end = 255;
				}

				stream.write((byte) start);
				stream.write((byte) end);
				this.streamEnd += 2;

				int last = next.length() - end;

				writeString(stream, (start < last ? next.substring(start, last) : "")); //$NON-NLS-1$
				current = next;
			}
		}

		this.startOfCategoryTables = this.streamEnd + 1;
	}

	/**
	 * writeDocumentNumbers
	 * 
	 * @param documentNumbers
	 * @param stream
	 * @throws IOException
	 */
	private void writeDocumentNumbers(List<Integer> documentNumbers, OutputStream stream) throws IOException
	{
		// must store length as a positive int to detect in-lined array of 1 element
		int length = documentNumbers.size();

		writeStreamInt(stream, length);
		Collections.sort(documentNumbers);

		for (Integer docNumber : documentNumbers)
		{
			int value = docNumber.intValue();

			switch (this.documentReferenceSize)
			{
				case 1:
					stream.write((byte) value);
					this.streamEnd++;
					break;

				case 2:
					stream.write((byte) (value >> 8));
					stream.write((byte) value);
					this.streamEnd += 2;
					break;

				default:
					writeStreamInt(stream, value);
					break;
			}
		}

		stream.flush();
	}

	/**
	 * writeHeaderInfo
	 * 
	 * @param stream
	 * @throws IOException
	 */
	private void writeHeaderInfo(OutputStream stream) throws IOException
	{
		writeStreamInt(stream, this.numberOfChunks);

		stream.write((byte) this.sizeOfLastChunk);
		stream.write((byte) this.documentReferenceSize);
		stream.write((byte) this.separator);
		this.streamEnd += 3;

		// apend the file with chunk offsets
		for (int i = 0; i < this.numberOfChunks; i++)
		{
			writeStreamInt(stream, this.chunkOffsets[i]);
		}

		writeStreamInt(stream, this.startOfCategoryTables);

		// append the file with the category offsets... # of name -> offset pairs, followed by each name & an offset to
		// its word->doc# table
		writeStreamInt(stream, this.categoryOffsets.size());

		for (Map.Entry<String, Integer> entry : categoryOffsets.entrySet())
		{
			writeString(stream, entry.getKey());
			writeStreamInt(stream, entry.getValue());
		}

		stream.flush();
	}

	/**
	 * writeOffsetToHeader
	 * 
	 * @param offsetToHeader
	 * @throws IOException
	 */
	private void writeOffsetToHeader(int offsetToHeader) throws IOException
	{
		if (offsetToHeader > 0)
		{
			RandomAccessFile file = new RandomAccessFile(this.indexFile, "rw"); //$NON-NLS-1$

			try
			{
				file.seek(this.headerInfoOffset); // offset to position in header
				file.writeInt(offsetToHeader);
				this.headerInfoOffset = offsetToHeader; // update to reflect the correct offset
			}
			finally
			{
				file.close();
			}
		}
	}

	/**
	 * writeStreamInt
	 * 
	 * @param stream
	 * @param val
	 * @throws IOException
	 */
	private void writeStreamInt(OutputStream stream, int val) throws IOException
	{
		stream.write((byte) (val >> 24));
		stream.write((byte) (val >> 16));
		stream.write((byte) (val >> 8));
		stream.write((byte) val);
		this.streamEnd += 4;
		stream.flush();
	}

	/**
	 * writeString
	 * 
	 * @param stream
	 * @param signature
	 * @throws IOException
	 */
	private void writeString(OutputStream stream, String signature) throws IOException
	{
		char[] array = signature.toCharArray();

		// If this string is longer than we can store in 2 bytes (16 bits, which is 65535), we'll overflow/break!
		int length = array.length;
		if (length >= 65535)
		{
			throw new IOException(
					MessageFormat
							.format("Trying to write a string that is too long and will overflow the recorded length maximum. index file: {0},  length: {1}, string: ''{2}''", //$NON-NLS-1$
									indexFile.getAbsolutePath(), length, signature));
		}

		stream.write((byte) ((length >>> 8) & 0xFF)); // store chars array length instead of bytes
		stream.write((byte) (length & 0xFF)); // this will allow to read it faster
		this.streamEnd += 2;

		// we're assuming that very few char[] are so large that we need to flush the buffer more than once, if at all
		for (char ch : array)
		{
			if ((ch & 0x007F) == ch)
			{
				stream.write((byte) ch);
				this.streamEnd++;
			}
			else if ((ch & 0x07FF) == ch)
			{
				// first two bits are stored in first byte
				byte b = (byte) (ch >> 6);
				b &= 0x1F;
				b |= 0xC0;
				stream.write(b);
				streamEnd++;
				// last six bits are stored in second byte
				b = (byte) (ch & 0x3F);
				b |= 0x80;
				stream.write(b);
				streamEnd++;
			}
			else
			{
				// first four bits are stored in first byte
				byte b = (byte) (ch >> 12);
				b &= 0x0F;
				b |= 0xE0;
				stream.write(b);
				streamEnd++;
				// six following bits are stored in second byte
				b = (byte) (ch >> 6);
				b &= 0x3F;
				b |= 0x80;
				stream.write(b);
				streamEnd++;
				// last six bits are stored in third byte
				b = (byte) (ch & 0x3F);
				b |= 0x80;
				stream.write(b);
				streamEnd++;
			}
		}

		stream.flush();
	}
}
