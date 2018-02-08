package com.aptana.core.build;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;

public class ReconcileContextTest
{

	private ReconcileContext context;

	@Before
	public void setUp() throws Exception
	{
		context = new ReconcileContext(null, (IFile) null, null);
	}

	@After
	public void tearDown() throws Exception
	{
		context = null;
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
