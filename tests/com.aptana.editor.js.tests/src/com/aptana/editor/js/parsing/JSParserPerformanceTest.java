package com.aptana.editor.js.parsing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import junit.framework.TestCase;

import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseState;

public class JSParserPerformanceTest extends TestCase
{
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
				JSParserPerformanceTest tester = new JSParserPerformanceTest();

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
					try
					{
						tester.setUp();
						tester.timeParse(f);
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
		InputStream stream = getClass().getResourceAsStream(resourceName);

		return getSource(stream);
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
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
	}

	/**
	 * testDojo
	 * 
	 * @throws Exception
	 */
	public void testDojo() throws Exception
	{
		timeParse("dojo.js.uncompressed.js");
	}

	/**
	 * testExt
	 * 
	 * @throws Exception
	 */
	public void testExt() throws Exception
	{
		timeParse("ext-core.js");
	}

	/**
	 * testTiMobile
	 * 
	 * @throws Exception
	 */
	public void testTiMobile() throws Exception
	{
		timeParse("timobile.js");
	}

	/**
	 * testTinyMce
	 * 
	 * @throws Exception
	 */
	public void testTinyMce() throws Exception
	{
		timeParse("tiny_mce.js");
	}

	/**
	 * testRegress
	 * 
	 * @throws Exception
	 */
	public void testRegress() throws Exception
	{
		timeParse("regress-155081-2.js");
	}
	
	/**
	 * timeParse
	 * 
	 * @param file
	 * @throws Exception
	 */
	protected void timeParse(File file) throws Exception
	{
		this.timeParse(file.getAbsolutePath(), getSource(file));
	}

	/**
	 * time
	 * 
	 * @param resourceName
	 * @throws Exception
	 */
	protected void timeParse(String resourceName) throws Exception
	{
		this.timeParse(resourceName, getSource(resourceName));
	}

	/**
	 * time
	 * 
	 * @param name
	 * @param source
	 */
	protected void timeParse(String name, String source) throws Exception
	{
		this.timeParse(name, source, 5);
	}

	/**
	 * time
	 * 
	 * @param resourceName
	 * @throws Exception
	 */
	protected void timeParse(String resourceName, String src, int numRuns) throws Exception
	{
		// apply to parse state
		IParseState parseState = new ParseState();
		parseState.setEditState(src, src, 0, 0);

		// start timing
		long start = System.currentTimeMillis();

		for (int i = 0; i < numRuns; i++)
		{
			try
			{
				fParser.parse(parseState);
			}
			catch (Exception e)
			{
				fail(e.getMessage());
			}
		}

		// get time difference
		long diff = System.currentTimeMillis() - start;

		if (diff > numRuns*100)
		{
			// show results
			System.out.println(String.format("parse: %5dms: %s", (diff / numRuns), resourceName));
		}
	}
}
