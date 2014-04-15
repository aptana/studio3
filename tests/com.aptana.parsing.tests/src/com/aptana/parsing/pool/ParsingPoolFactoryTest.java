/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.pool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Before;
import org.junit.Test;

import beaver.Symbol;

import com.aptana.core.epl.util.LRUCacheWithSoftPrunedValues;
import com.aptana.parsing.AbstractParser;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParseStateCacheKey;
import com.aptana.parsing.IParser;
import com.aptana.parsing.IParserPool;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ParseStateCacheKey;
import com.aptana.parsing.ParseStateCacheKeyWithComments;
import com.aptana.parsing.ParsingEngine;
import com.aptana.parsing.WorkingParseResult;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.ast.ParseRootNode;

/**
 * @author Fabio
 */
public class ParsingPoolFactoryTest
{

	private final int PARSE_TIMEOUT = 20;

	/**
	 * @author Fabio
	 */
	private static final class ParseStateCollectingComments extends ParseState
	{
		public ParseStateCollectingComments(String source, int startingOffset)
		{
			super(source, startingOffset);
		}

		@Override
		public IParseStateCacheKey getCacheKey(String contentTypeId)
		{
			return new ParseStateCacheKeyWithComments(true, true, super.getCacheKey(contentTypeId));
		}
	}

	/**
	 * @author Fabio
	 */
	private static final class ParseStateNotCollectingComments extends ParseState
	{
		public ParseStateNotCollectingComments(String source, int startingOffset)
		{
			super(source, startingOffset);
		}

		@Override
		public IParseStateCacheKey getCacheKey(String contentTypeId)
		{
			return new ParseStateCacheKeyWithComments(false, false, super.getCacheKey(contentTypeId));
		}
	}

	/**
	 * @author Fabio
	 */
	private static final class ParserPoolProvider implements ParsingEngine.IParserPoolProvider
	{
		/**
		 * 
		 */
		private final Map<String, IParserPool> parserPool;

		/**
		 * @param parserPool
		 */
		private ParserPoolProvider(IParserPool parserPool)
		{
			this.parserPool = new HashMap<String, IParserPool>();
			this.parserPool.put("test", parserPool);
		}

		public ParserPoolProvider(HashMap<String, IParserPool> contentTypeToPool)
		{
			this.parserPool = contentTypeToPool;
		}

		public IParserPool getParserPool(String contentTypeId)
		{
			IParserPool pool = this.parserPool.get(contentTypeId);
			if (pool == null)
			{
				fail("Expected content type to be one of: " + this.parserPool.keySet());
			}
			return pool;
		}
	}

	/**
	 * @author Fabio
	 */
	private static final class ParserPool implements IParserPool
	{
		/**
		 * 
		 */
		private final IParser parser;

		/**
		 * @param parser
		 */
		private ParserPool(IParser parser)
		{
			this.parser = parser;
		}

		public boolean validate(IParser o)
		{
			throw new RuntimeException("Not implemented");
		}

		public void expire(IParser o)
		{
			throw new RuntimeException("Not implemented");
		}

		public void dispose()
		{
			throw new RuntimeException("Not implemented");
		}

		public IParser create()
		{
			throw new RuntimeException("Not implemented");
		}

		public IParser checkOut()
		{
			return parser;
		}

		public void checkIn(IParser t)
		{
		}
	}

	/**
	 * @author Fabio
	 */
	private static final class Parser extends AbstractParser
	{
		/**
		 * 
		 */
		private final Queue<ParseRootNode> queue;

		public int parseTimeout;

		public int parses;

		/**
		 * @param queue
		 */
		private Parser(Queue<ParseRootNode> queue)
		{
			this.queue = queue;
		}

		protected synchronized void parse(IParseState parseState, WorkingParseResult working) throws Exception
		{
			Thread.sleep(parseTimeout);
			parses += 1;
			ParseRootNode ast = queue.remove();
			working.setParseResult(ast);
		}
	}

	Queue<ParseRootNode> queue;

	Parser parser;

	IParserPool parserPool;

	ParsingEngine parsingEngine;

	ParseRootNode parseRootNode;

	@Before
	public void setUp() throws Exception
	{

		queue = new ConcurrentLinkedQueue<ParseRootNode>();

		parser = new Parser(queue);

		parserPool = new ParserPool(parser);

		parsingEngine = new ParsingEngine(new ParserPoolProvider(parserPool), 200, 0)
		{
			// Note: empty body (class just created to access protected constructor).
		};

		parseRootNode = new ParseRootNode(new Symbol[0], 0, 0)
		{
			public String getLanguage()
			{
				return "test";
			}
		};
	};

	@Test
	public void testParserPoolFactory() throws Exception
	{
		queue.add(parseRootNode);
		IParseRootNode ast = parsingEngine.parse("test", new ParseState("", 0)).getRootNode();
		assertEquals(parseRootNode, ast);
		assertEquals(0, queue.size());
		assertEquals(1, parser.parses);

		// Second parse: ast should be cached.
		ast = parsingEngine.parse("test", new ParseState("", 0)).getRootNode();
		assertEquals(parseRootNode, ast);
		assertEquals(0, queue.size());
		assertEquals(1, parser.parses);
	}

	@Test
	public void testParserPoolFactoryThreaded() throws Exception
	{

		parser.parseTimeout = PARSE_TIMEOUT;
		queue.add(parseRootNode);
		final Queue<Exception> exceptions = new ConcurrentLinkedQueue<Exception>();
		final Queue<IParseRootNode> asts = new ConcurrentLinkedQueue<IParseRootNode>();

		Runnable r = new Runnable()
		{

			public void run()
			{
				ParseState parseState = new ParseState("", 0);
				try
				{
					IParseRootNode node = parsingEngine.parse("test", parseState).getRootNode();
					if (node == null)
					{
						return;
					}
					asts.add(node);
				}
				catch (Exception e)
				{
					exceptions.add(e);
				}

			}
		};
		parsingEngine.clearCache();

		int expectedParses = 100;

		for (int j = 0; j < expectedParses; j++)
		{
			new Thread(r).start();
		}
		for (int i = 0; i < 100; i++)
		{
			if (exceptions.size() > 0)
			{
				throw exceptions.poll();
			}
			if (asts.size() == expectedParses)
			{
				break;
			}
			Thread.sleep(PARSE_TIMEOUT);
		}
		// Check if we really got to the proper state
		assertEquals(1, parser.parses);
		assertEquals(expectedParses, asts.size());
		if (exceptions.size() > 0)
		{
			throw exceptions.poll();
		}
		r.run();
		assertEquals(1, parser.parses);
		assertEquals(expectedParses + 1, asts.size());
		if (exceptions.size() > 0)
		{
			throw exceptions.poll();
		}

		parsingEngine.dispose();
		r.run();
		assertEquals(1, parser.parses);
		assertEquals(expectedParses + 1, asts.size());
		if (exceptions.size() > 0)
		{
			throw exceptions.poll();
		}
	}

	@Test
	public void testParseStateWithComments() throws Exception
	{
		queue.add(parseRootNode);

		IParseRootNode ast = parsingEngine.parse("test", new ParseStateCollectingComments("", 0)).getRootNode();
		assertEquals(parseRootNode, ast);
		assertEquals(0, queue.size());
		assertEquals(1, parser.parses);

		// Second parse: ast should be cached as the first has the comments.
		ast = parsingEngine.parse("test", new ParseStateNotCollectingComments("", 0)).getRootNode();
		assertEquals(parseRootNode, ast);
		assertEquals(0, queue.size());
		assertEquals(1, parser.parses);

		parsingEngine.clearCache();

		queue.add(parseRootNode);

		ast = parsingEngine.parse("test", new ParseStateNotCollectingComments("", 0)).getRootNode();
		assertEquals(parseRootNode, ast);
		assertEquals(0, queue.size());
		assertEquals(2, parser.parses);

		queue.add(parseRootNode);
		// Second parse: this time as it was cached without comments, it should be reparsed.
		ast = parsingEngine.parse("test", new ParseStateCollectingComments("", 0)).getRootNode();
		assertEquals(parseRootNode, ast);
		assertEquals(0, queue.size());
		assertEquals(3, parser.parses);
	}

	private class ParserWithSubParse extends AbstractParser
	{

		private ParsingEngine parsingEngine;

		public void parse(IParseState parseState, WorkingParseResult working) throws Exception
		{
			this.parsingEngine.parse("subContent", new ParseState("sub1"));
			this.parsingEngine.parse("subContent", new ParseState("sub2"));
			this.parsingEngine.parse("subContent", new ParseState("sub3"));
			working.setParseResult(new ParseRootNode(new Symbol[0], 0, 0)
			{
				public String getLanguage()
				{
					return "main";
				}
			});
		}

		public void setParsingEngine(ParsingEngine parsingEngine)
		{
			this.parsingEngine = parsingEngine;
		}

	}

	private class SubParser extends AbstractParser
	{

		protected void parse(IParseState parseState, WorkingParseResult working) throws Exception
		{
			working.setParseResult(new ParseRootNode(new Symbol[0], 0, 0)
			{
				public String getLanguage()
				{
					return "sub";
				}
			});
		}

	}

	@SuppressWarnings({ "rawtypes" })
	@Test
	public void testParseWithSubParses() throws Exception
	{
		ParserWithSubParse mainParser = new ParserWithSubParse();
		SubParser subParser = new SubParser();

		HashMap<String, IParserPool> contentTypeToPool = new HashMap<String, IParserPool>();
		contentTypeToPool.put("mainContent", new ParserPool(mainParser));
		contentTypeToPool.put("subContent", new ParserPool(subParser));

		parsingEngine = new ParsingEngine(new ParserPoolProvider(contentTypeToPool), 4, 0)
		{
			// Empty body just to access protected constructor.
		};

		// Change the cache for a cache with an auxiliary cache that's predictable.
		Field declaredField = ParsingEngine.class.getDeclaredField("fParseCache");
		declaredField.setAccessible(true);
		Map auxiliaryCache = new HashMap();
		Constructor<LRUCacheWithSoftPrunedValues> constructor = LRUCacheWithSoftPrunedValues.class
				.getDeclaredConstructor(int.class, Map.class);
		constructor.setAccessible(true);
		LRUCacheWithSoftPrunedValues cache = constructor.newInstance(4, auxiliaryCache);
		declaredField.set(parsingEngine, cache);

		mainParser.setParsingEngine(parsingEngine);

		// In the end, the mainContent should be in the LRU, while the subContent should be in the auxiliary cache.
		parsingEngine.parse("mainContent", new ParseState("main"));

		assertEquals(1, cache.keys().size());
		for (Object key : cache.keys())
		{
			ParseStateCacheKey cacheKey = (ParseStateCacheKey) key;
			Object content = cacheKey.getAt(0);
			if (!"mainContent".equals(content))
			{
				fail("Could not find mainContent in LRU main memory. Found: " + content);
			}
		}
		assertEquals(3, auxiliaryCache.size());
		for (Object key : auxiliaryCache.keySet())
		{
			ParseStateCacheKey cacheKey = (ParseStateCacheKey) key;
			Object content = cacheKey.getAt(0);
			if (!"subContent".equals(content))
			{
				fail("Could not find subContent in LRU main memory. Found: " + content);
			}
		}

	}

}
