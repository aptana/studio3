/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing;

import java.text.MessageFormat;

import org.eclipse.core.runtime.Assert;

import com.aptana.core.epl.util.LRUCacheWithSoftPrunnedValues;
import com.aptana.core.logging.IdeLog;

/**
 * This class is responsible for actually calling the parsing. It'll use the ParseState#getCacheKey() to know if an
 * ongoing parse can be used for a new requestor (and if so, that requestor will be blocked until the end of the parse
 * rather than doing the parse itself).
 * 
 * @author Fabio
 */
public class ParsingEngine
{

	public static interface IParserPoolProvider
	{
		IParserPool getParserPool(String contentTypeId);
	}

	/**
	 * Internal class to help in the synchronization of the parsing results.
	 */
	private static class CacheValue
	{
		/**
		 * Key for the state.
		 */
		private IParseStateCacheKey fCachedParseStateKey;

		/**
		 * This is the state that was used for the parsing.
		 */
		private ParseResult fCachedParseResult;

		/**
		 * Lock to help in synchronizing (threads should wait in it while a result is not available).
		 */
		private final Object fLock = new Object();

		/**
		 * Boolean determining if the results are available or not. Access should be synchronized with fLock.
		 */
		private volatile boolean fResultGotten = false;

		/**
		 * @param parseStateKey
		 *            the key for which the parse will be done.
		 * @param parseState
		 *            the state for which the parse will be done.
		 */
		public CacheValue(IParseStateCacheKey parseStateKey)
		{
			fCachedParseStateKey = parseStateKey;
		}

		/**
		 * @return true if the new cache key requires a reparse.
		 */
		public boolean requiresReparse(IParseStateCacheKey newCacheKey)
		{
			return fCachedParseStateKey.requiresReparse(newCacheKey);
		}

		/**
		 * @return the result from doing the parse. If it's still not available, blocks until it's provided.
		 */
		public ParseResult getResult()
		{
			while (!fResultGotten) // Double-check pattern for speed.
			{
				synchronized (fLock)
				{
					if (!fResultGotten)
					{
						try
						{
							fLock.wait();
						}
						catch (InterruptedException e)
						{
							// ignore
						}
					}
				}
			}
			return fCachedParseResult;
		}

		/**
		 * Sets the result of the parse. Notifies any waiting thread that it has become available.
		 */
		public void setResult(ParseResult parseResult)
		{
			Assert.isNotNull(parseResult); // A parse result must NOT be null (should be an empty parse result if
											// needed).
			fCachedParseResult = parseResult;
			synchronized (fLock)
			{
				fResultGotten = true;
				fLock.notifyAll();
			}
		}
	}

	/**
	 * A parse cache. Keyed by combo of content type and source hash, holds IParseRootNode result. Retains most recently
	 * used ASTs.
	 */
	private LRUCacheWithSoftPrunnedValues<IParseStateCacheKey, CacheValue> fParseCache;

	/**
	 * Object providing access to the pool provider.
	 */
	private IParserPoolProvider fParserPoolProvider;

	/**
	 * Any access to the fParseCache should have this lock in place.
	 */
	private final Object fParseCacheLock = new Object();

	public ParsingEngine(IParserPoolProvider parserPoolProvider)
	{
		// Create a cache with N 'strong' references but still keep prunned values as soft references.
		fParseCache = new LRUCacheWithSoftPrunnedValues<IParseStateCacheKey, CacheValue>(15);
		fParserPoolProvider = parserPoolProvider;
	}

	public void dispose()
	{
		fParseCache = null;
	}

	/**
	 * To be used for testing purposes only.
	 */
	public void clearCache()
	{
		if (fParseCache == null) // already disposed.
		{
			return;
		}
		fParseCache.flush();
	}

	public ParseResult parse(String contentTypeId, IParseState parseState) throws Exception // $codepro.audit.disable
																							// declaredExceptions
	{
		try
		{
			if (contentTypeId == null)
			{
				return ParseResult.EMPTY;
			}

			String source = parseState.getSource();
			if (source == null)
			{
				// If we don't have the source, we're not able to do a parse in the first place.
				IdeLog.logError(ParsingPlugin.getDefault(), Messages.ParserPoolFactory_Expecting_Source,
						IDebugScopes.PARSING);
				return ParseResult.EMPTY;
			}
			IParseStateCacheKey newParseStateKey = parseState.getCacheKey(contentTypeId);
			CacheValue cacheValue = null;
			LRUCacheWithSoftPrunnedValues<IParseStateCacheKey, CacheValue> parseCache = fParseCache;
			if (parseCache == null)
			{
				return ParseResult.EMPTY; // already disposed.
			}

			IParserPool pool = null;
			IParser parser = null;
			boolean getResultFromCache = false;

			try
			{
				synchronized (fParseCacheLock)
				{
					cacheValue = parseCache.get(newParseStateKey);
					if (cacheValue != null && !cacheValue.requiresReparse(newParseStateKey))
					{
						// Cache hit... it may still be in progress, but the cacheValue.getResult should handle that
						// (but we'll get out of the synchronized block to actually do that).
						getResultFromCache = true;
					}
					else
					{
						// No cache-hit, we'll do the parsing here.
						pool = fParserPoolProvider.getParserPool(contentTypeId);

						// If we won't be able to do the parsing because we're unable to get the pool or the
						// parser, don't even register the cache value (so that no one listens for something thot
						// won't yield a correct return anyways).
						if (pool == null)
						{
							if (IdeLog.isInfoEnabled(ParsingPlugin.getDefault(), null))
							{
								String message = MessageFormat.format(
										Messages.ParserPoolFactory_Cannot_Acquire_Parser_Pool, contentTypeId);
								IdeLog.logInfo(ParsingPlugin.getDefault(), message, IDebugScopes.PARSING);
							}
							return ParseResult.EMPTY;
						}
						parser = pool.checkOut();
						if (parser == null)
						{
							String message = MessageFormat.format(Messages.ParserPoolFactory_Cannot_Acquire_Parser,
									contentTypeId);
							IdeLog.logError(ParsingPlugin.getDefault(), message, IDebugScopes.PARSING);
							return ParseResult.EMPTY;
						}

						// Ok, we're in a state where either there's no one parsing or the currently cached value does
						// not match the one in the cache for this key (i.e.: parse without comments and later with
						// comments).
						cacheValue = new CacheValue(newParseStateKey);
						parseCache.put(newParseStateKey, cacheValue);
						// Important: after we put it here (in the situation getResultFromCache), we MUST have a result
						// cacheValue.setResult(), otherwise we may end up with a listener waiting eternally for a
						// result.
					}
				}
			}
			catch (Throwable e)
			{
				if (!getResultFromCache)
				{
					// Clean up if something bad happened at somewhere there (to avoid any possible deadlock).
					if (pool != null && parser != null)
					{
						try
						{
							pool.checkIn(parser);
						}
						catch (Throwable e1)
						{
							// Don't even log this one (we're already in a bad state if something happened and we'll
							// throw the original exception).
						}
					}
					if (cacheValue != null)
					{
						// We really HAVE to call this one to avoid possible deadlocks.
						cacheValue.setResult(ParseResult.EMPTY);
					}
				}
				throw new RuntimeException(e);

			}

			if (getResultFromCache)
			{
				return cacheValue.getResult();
			}
			else
			{
				ParseResult result = ParseResult.EMPTY;
				try
				{
					try
					{
						result = parser.parse(parseState);
					}
					finally
					{
						pool.checkIn(parser);
					}
					synchronized (fParseCacheLock)
					{
						// Make a get just to update time stamp or change it from the soft map back into the main LRU.
						// Done because we may have the situation where the a main parse has multiple sub-parses, and
						// it's more important to persist the main parse than the sub-parses.
						parseCache.get(newParseStateKey);
					}
				}
				finally
				{
					// Set the result even if this means setting null (otherwise it's possible that some listener
					// deadlocks because of that).
					cacheValue.setResult(result);
				}
				return result;
			}
		}
		finally
		{
			// Clean up source inside parse state to help reduce RAM usage...
			parseState.clearEditState();
		}

	}

}
