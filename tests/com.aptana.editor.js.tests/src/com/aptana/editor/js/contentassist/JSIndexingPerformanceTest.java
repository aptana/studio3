package com.aptana.editor.js.contentassist;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import junit.framework.TestCase;

import com.aptana.editor.js.Activator;
import com.aptana.editor.js.contentassist.index.JSFileIndexingParticipant;
import com.aptana.editor.js.parsing.JSParser;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;

public class JSIndexingPerformanceTest extends TestCase
{
	public class Indexer extends JSFileIndexingParticipant
	{
		public void indexTree(Index index, JSParseRootNode root, URI location)
		{
			this.processParseResults(index, root, location);
		}
	}

	/**
	 * main
	 * 
	 * @param args
	 * @return
	 */
	public static void main(String[] args)
	{
		if (args != null && args.length > 0)
		{
			File file = new File(args[0]);

			if (file.canRead())
			{
				List<File> files = new ArrayList<File>();
				JSIndexingPerformanceTest tester = new JSIndexingPerformanceTest();

				if (file.isDirectory())
				{
					final Queue<File> directories = new ArrayDeque<File>();

					directories.offer(file);

					while (directories.isEmpty() == false)
					{
						File directory = directories.poll();
						File[] jsFiles = directory.listFiles(new FileFilter()
						{

							@Override
							public boolean accept(File pathname)
							{
								boolean result = false;

								if (pathname.isDirectory())
								{
									directories.offer(pathname);
								}
								else
								{
									result = (pathname.getName().toLowerCase().endsWith(".js"));
								}

								return result;
							}

						});

						files.addAll(Arrays.asList(jsFiles));
					}
				}
				else if (file.isFile())
				{
					files.add(file);
				}

				long start = System.currentTimeMillis();

				for (File f : files)
				{
					System.out.println("Processing " + f.getName());
					
					try
					{
						tester.setUp();
						tester.timeIndex(f);
					}
					catch (Throwable e)
					{
					}
					finally
					{
						try
						{
							tester.tearDown();
						}
						catch (Exception e)
						{
						}
					}
				}

				long diff = System.currentTimeMillis() - start;

				System.out.println("processed " + files.size() + " files in " + diff + " milliseconds");
			}
		}
		else
		{
			System.out.println("Expected a list of JS files and/or directories to search");
		}
	}

	private JSParser fParser;

	/**
	 * getSource
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private String getSource(File file) throws IOException
	{
		InputStream stream = new FileInputStream(file);

		return getSource(stream);
	}

	/**
	 * getSource
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	private String getSource(InputStream stream) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		int read = -1;

		while ((read = stream.read()) != -1)
		{
			out.write(read);
		}

		stream.close();

		String src = new String(out.toByteArray());
		return src;
	}

	/**
	 * getSource
	 * 
	 * @param resourceName
	 * @return
	 * @throws IOException
	 */
	private String getSource(String resourceName) throws IOException
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle(Activator.PLUGIN_ID), new Path(resourceName), false);

		return getSource(stream);
	}

	/**
	 * getURI
	 * 
	 * @return
	 */
	protected URI getLocation()
	{
		return URI.create("inference_file.js");
	}

	/**
	 * getIndex
	 * 
	 * @return
	 */
	protected Index getIndex()
	{
		URI indexURI = this.getIndexURI();
		Index result = null;

		if (indexURI != null)
		{
			result = IndexManager.getInstance().getIndex(indexURI);
		}

		return result;
	}

	/**
	 * getIndexURI
	 * 
	 * @return
	 */
	protected URI getIndexURI()
	{
		return URI.create("inference.testing");
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		
		fParser = new JSParser();
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		fParser = null;
		
		URI indexURI = this.getIndexURI();

		if (indexURI != null)
		{
			IndexManager.getInstance().removeIndex(indexURI);
		}

		super.tearDown();
	}

	/**
	 * testDojo
	 * 
	 * @throws Exception
	 */
	public void testDojo() throws Exception
	{
		timeIndex("performance/dojo.js.uncompressed.js");
	}

	/**
	 * testExt
	 * 
	 * @throws Exception
	 */
	public void testExt() throws Exception
	{
		timeIndex("performance/ext-core.js");
	}

	/**
	 * testTiMobile
	 * 
	 * @throws Exception
	 */
	public void testTiMobile() throws Exception
	{
		timeIndex("performance/timobile.js");
	}

	/**
	 * testTinyMce
	 * 
	 * @throws Exception
	 */
	public void testTinyMce() throws Exception
	{
		timeIndex("performance/tiny_mce.js");
	}

	/**
	 * testRegress
	 * 
	 * @throws Exception
	 */
	public void testRegress() throws Exception
	{
		timeIndex("performance/regress-155081-2.js");
	}

	/**
	 * timeParse
	 * 
	 * @param file
	 * @throws Exception
	 */
	protected void timeIndex(File file) throws Exception
	{
		this.timeIndex(file.getAbsolutePath(), getSource(file));
	}

	/**
	 * time
	 * 
	 * @param resourceName
	 * @throws Exception
	 */
	protected void timeIndex(String resourceName) throws Exception
	{
		this.timeIndex(resourceName, getSource(resourceName));
	}

	/**
	 * time
	 * 
	 * @param resourceName
	 * @throws Exception
	 */
	protected void timeIndex(String resourceName, String src) throws Exception
	{
		// apply to parse state
		IParseState parseState = new ParseState();
		parseState.setEditState(src, src, 0, 0);

		try
		{
			fParser.parse(parseState);

			IParseNode root = parseState.getParseResult();

			if (root instanceof JSParseRootNode)
			{
				Indexer indexer = new Indexer();

				// start timing
				long start = System.currentTimeMillis();

				indexer.indexTree(this.getIndex(), (JSParseRootNode) root, this.getLocation());

				// get time difference
				long diff = System.currentTimeMillis() - start;

				// show results
				System.out.println(String.format("index: %5dms: %s", diff, resourceName));
			}
			else
			{
				fail("No parse root node for " + resourceName);
			}
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
}
