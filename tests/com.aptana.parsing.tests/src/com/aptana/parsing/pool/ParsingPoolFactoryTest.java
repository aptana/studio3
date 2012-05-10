/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.pool;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import junit.framework.TestCase;
import beaver.Symbol;

import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParseStateCacheKey;
import com.aptana.parsing.IParser;
import com.aptana.parsing.IParserPool;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ParseStateCacheKeyWithComments;
import com.aptana.parsing.ParsingEngine;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.ast.ParseRootNode;

/**
 * @author Fabio
 */
public class ParsingPoolFactoryTest extends TestCase
{

	private final int PARSE_TIMEOUT = 20;

	/**
	 * @author Fabio
	 */
	private static final class ParseStateCollectingComments extends ParseState
	{
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
		private final IParserPool parserPool;

		/**
		 * @param parserPool
		 */
		private ParserPoolProvider(IParserPool parserPool)
		{
			this.parserPool = parserPool;
		}

		public IParserPool getParserPool(String contentTypeId)
		{
			if (!contentTypeId.equals("test"))
			{
				fail("Expected content type to be 'test'");
			}
			return parserPool;
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
		private final Parser parser;

		/**
		 * @param parser
		 */
		private ParserPool(Parser parser)
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
	private static final class Parser implements IParser
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

		public synchronized IParseRootNode parse(IParseState parseState) throws Exception
		{
			Thread.sleep(parseTimeout);
			parses += 1;
			return queue.remove();
		}
	}

	Queue<ParseRootNode> queue;

	Parser parser;

	IParserPool parserPool;

	ParsingEngine parsingEngine;

	ParseRootNode parseRootNode;

	protected void setUp() throws Exception
	{

		queue = new ConcurrentLinkedQueue<ParseRootNode>();

		parser = new Parser(queue);

		parserPool = new ParserPool(parser);

		parsingEngine = new ParsingEngine(new ParserPoolProvider(parserPool));

		parseRootNode = new ParseRootNode("test", new Symbol[0], 0, 0);
	};

	public void testParserPoolFactory() throws Exception
	{
		queue.add(parseRootNode);
		IParseRootNode ast = parsingEngine.parse("test", new ParseState());
		assertEquals(parseRootNode, ast);
		assertEquals(0, queue.size());
		assertEquals(1, parser.parses);

		// Second parse: ast should be cached.
		ast = parsingEngine.parse("test", new ParseState());
		assertEquals(parseRootNode, ast);
		assertEquals(0, queue.size());
		assertEquals(1, parser.parses);
	}

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
				ParseState parseState = new ParseState();
				try
				{
					IParseRootNode node = parsingEngine.parse("test", parseState);
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

	public void testParseStateWithComments() throws Exception
	{
		queue.add(parseRootNode);

		IParseRootNode ast = parsingEngine.parse("test", new ParseStateCollectingComments());
		assertEquals(parseRootNode, ast);
		assertEquals(0, queue.size());
		assertEquals(1, parser.parses);

		// Second parse: ast should be cached as the first has the comments.
		ast = parsingEngine.parse("test", new ParseStateNotCollectingComments());
		assertEquals(parseRootNode, ast);
		assertEquals(0, queue.size());
		assertEquals(1, parser.parses);

		parsingEngine.clearCache();

		queue.add(parseRootNode);

		ast = parsingEngine.parse("test", new ParseStateNotCollectingComments());
		assertEquals(parseRootNode, ast);
		assertEquals(0, queue.size());
		assertEquals(2, parser.parses);

		queue.add(parseRootNode);
		// Second parse: this time as it was cached without comments, it should be reparsed.
		ast = parsingEngine.parse("test", new ParseStateCollectingComments());
		assertEquals(parseRootNode, ast);
		assertEquals(0, queue.size());
		assertEquals(3, parser.parses);
	}

}
