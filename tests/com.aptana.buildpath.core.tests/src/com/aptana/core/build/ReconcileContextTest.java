package com.aptana.core.build;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;

public class ReconcileContextTest
{

	private ReconcileContext context;

	@Before
	public void setUp() throws Exception
	{
//		super.setUp();
		context = new ReconcileContext(null, (IFile) null, null);
	}

	@After
	public void tearDown() throws Exception
	{
		context = null;
//		super.tearDown();
	}

	@Test
	public void testNullFileNullContents() throws Exception
	{
		assertEquals(StringUtil.EMPTY, context.getContents());
		assertNull(context.getContentType());
	}

	@Test
	public void testOpenInputStreamWithNullFile() throws Exception
	{
		String content = "this is some test content.";
		context = new ReconcileContext(null, (IFile) null, content);
		InputStream stream = context.openInputStream(new NullProgressMonitor());
		assertEquals(content, IOUtil.read(stream));
	}

	@Test
	public void testOpenInputStreamWithUnsupportedEncoding() throws Exception
	{
		String content = "this is some test content.";
		context = new ReconcileContext(null, (IFile) null, content)
		{
			@Override
			public String getCharset() throws CoreException
			{
				return "abjfjhytfj";
			}
		};
		try
		{
			context.openInputStream(new NullProgressMonitor());
			fail("Expected a CoreException to be thrown for unsupported encoding");
		}
		catch (CoreException ce)
		{
			assertTrue(true);
		}
	}
}
