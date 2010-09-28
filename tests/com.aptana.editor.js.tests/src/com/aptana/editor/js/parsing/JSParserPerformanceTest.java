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

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.editor.js.Activator;
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
		timeParse("performance/dojo.js.uncompressed.js");
	}

	/**
	 * testExt
	 * 
	 * @throws Exception
	 */
	public void testExt() throws Exception
	{
		timeParse("performance/ext-core.js");
	}

	/**
	 * testTiMobile
	 * 
	 * @throws Exception
	 */
	public void testTiMobile() throws Exception
	{
		timeParse("performance/timobile.js");
	}

	/**
	 * testTinyMce
	 * 
	 * @throws Exception
	 */
	public void testTinyMce() throws Exception
	{
		timeParse("performance/tiny_mce.js");
	}

	/**
	 * testJaxerFiles
	 * 
	 * @throws Exception
	 */
	public void testJaxerFiles() throws Exception
	{
		timeParse("performance/jaxer/11.2.2-1-n.js");
		timeParse("performance/jaxer/15.10.6.2-2.js");
		timeParse("performance/jaxer/15.5.4.7-2.js");
		timeParse("performance/jaxer/15.9.5.21-3.js");
		timeParse("performance/jaxer/ComposerCommands.js");
		timeParse("performance/jaxer/DBAPI.js");
		timeParse("performance/jaxer/DOMTestCase.js");
		timeParse("performance/jaxer/Microformats.js");
		timeParse("performance/jaxer/MochiKit_packed.js");
		timeParse("performance/jaxer/SimpleTest.js");
		timeParse("performance/jaxer/TestCachePerformance.js");
		timeParse("performance/jaxer/UDDITypes.js");
		timeParse("performance/jaxer/browser_bug_411172.js");
		timeParse("performance/jaxer/clientBothProperty.js");
		timeParse("performance/jaxer/commands.js");
		timeParse("performance/jaxer/crlManager.js");
		timeParse("performance/jaxer/dojo.js");
		timeParse("performance/jaxer/dom.js");
		timeParse("performance/jaxer/editor.js");
		timeParse("performance/jaxer/effects.js");
		timeParse("performance/jaxer/file-utils.js");
		timeParse("performance/jaxer/head_urlformatter.js");
		timeParse("performance/jaxer/httpd.js");
		timeParse("performance/jaxer/ifaceinfotest.js");
		timeParse("performance/jaxer/irc.js");
		timeParse("performance/jaxer/jquery-1.2.1.js");
		timeParse("performance/jaxer/jquery-1.2.6.min.js");
		timeParse("performance/jaxer/jquery-stable.js");
		timeParse("performance/jaxer/jquery.js");
		timeParse("performance/jaxer/lexical-008.js");
		timeParse("performance/jaxer/messages.js");
		timeParse("performance/jaxer/narcissus-exec.js");
		timeParse("performance/jaxer/nsDragAndDrop.js");
		timeParse("performance/jaxer/packed.js");
		timeParse("performance/jaxer/perlstress-001.js");
		timeParse("performance/jaxer/perlstress-002.js");
		timeParse("performance/jaxer/property_database.js");
		timeParse("performance/jaxer/prototype.js");
		timeParse("performance/jaxer/publishprefs.js");
		timeParse("performance/jaxer/regress-100199.js");
		timeParse("performance/jaxer/regress-111557.js");
		timeParse("performance/jaxer/regress-155081-2.js");
		timeParse("performance/jaxer/regress-192226.js");
		timeParse("performance/jaxer/regress-244470.js");
		timeParse("performance/jaxer/regress-309925-02.js");
		timeParse("performance/jaxer/regress-76054.js");
		timeParse("performance/jaxer/regress-98901.js");
		timeParse("performance/jaxer/scriptaculous.js");
		timeParse("performance/jaxer/split-002.js");
		timeParse("performance/jaxer/test_413784.js");
		timeParse("performance/jaxer/test_423515_forceCopyShortcuts.js");
		timeParse("performance/jaxer/test_bug364285-1.js");
		timeParse("performance/jaxer/test_bug374754.js");
		timeParse("performance/jaxer/test_multi_statements.js");
		timeParse("performance/jaxer/test_prepare_insert_update.js");
		timeParse("performance/jaxer/tip_followscroll.js");
		timeParse("performance/jaxer/tree-utils.js");
		timeParse("performance/jaxer/utils.js");
		timeParse("performance/jaxer/xpath.js");
		timeParse("performance/jaxer/xslt_script.js");
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

		// show results
		System.out.println(String.format("parse: %5dms: %s", (diff / numRuns), resourceName));
	}
}
