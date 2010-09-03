package com.aptana.editor.js.contentassist;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

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

	private JSParser fParser;

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
	 * getSource
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 *
	private String getSource(File file) throws IOException
	{
		InputStream stream = new FileInputStream(file);

		return getSource(stream);
	}
	*/

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
	 * main
	 * 
	 * @param args
	 * @return
	 *
	public void testFiles()
	{
		File file = new File("/Users/klindsey/Documents/Projects/Jaxer");

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

			long start = System.nanoTime();
			long characterCount = 0;

			for (File f : files)
			{
				try
				{
					tester.setUp();
					
					String source = this.getSource(f);
					
					characterCount += source.length();
					
					tester.timeIndex(f.getAbsolutePath(), source);
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

			long diff = System.nanoTime() - start;
			double seconds = diff / 1e9;

			String message = String.format("processed %d files (%d characters) in %f s", files.size(), characterCount, seconds);
			System.out.println(message);
		}
	}
	*/

	/**
	 * testRegress155081_2
	 * 
	 * @throws Exception
	 */
	public void testRegress155081_2() throws Exception
	{
		timeIndex("performance/regress-155081-2.js");
	}

	/**
	 * testRegress98901
	 * 
	 * @throws Exception
	 */
	public void testRegress98901() throws Exception
	{
		timeIndex("performance/regress-98901.js");
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
		int numRuns = 5;
		
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
				long start = System.nanoTime();

				for (int i = 0; i < numRuns; i++)
				{
					URI indexURI = this.getIndexURI();
					
					if (indexURI != null)
					{
						IndexManager.getInstance().removeIndex(indexURI);
					}
					
					indexer.indexTree(this.getIndex(), (JSParseRootNode) root, this.getLocation());
				}

				// get time difference
				long diff = System.nanoTime() - start;
				double seconds = ((double) diff / numRuns) / 1e9;

				// show results
				System.out.println(String.format("index: %12.9fs: %s", seconds, resourceName));
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
