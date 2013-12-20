package com.aptana.index.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Test;

import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;

public class FileStoreBuildContextTest
{

	private FileStoreBuildContext context;

	@After
	public void tearDown() throws Exception
	{
		context = null;
	}

	@Test
	public void testWithEmptyJSFile() throws Exception
	{
		File file = File.createTempFile("fileStoreBuildContext", ".js");
		file.deleteOnExit();
		URI uri = file.toURI();
		IFileStore fileStore = EFS.getStore(uri);
		context = new FileStoreBuildContext(fileStore);
		assertEquals(StringUtil.EMPTY, context.getContents());
		assertEquals("com.aptana.contenttype.js", context.getContentType());
		assertNull(context.getFile());
		assertNull(context.getCharset());
		assertEquals(file.getName(), context.getName());
		assertNull(context.getProject());
		assertEquals(uri, context.getURI());
		InputStream stream = context.openInputStream(new NullProgressMonitor());
		assertNotNull(stream);
		assertEquals(StringUtil.EMPTY, IOUtil.read(stream));
		assertNotNull(context.getAST());
	}
}
