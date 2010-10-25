package com.aptana.editor.erb.common;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;

import org.eclipse.core.runtime.content.ITextContentDescriber;

public abstract class ERBContentDescriberTestCase extends TestCase
{

	private ERBContentDescriber describer;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		describer = createDescriber();
	}

	protected abstract ERBContentDescriber createDescriber();

	@Override
	protected void tearDown() throws Exception
	{
		describer = null;
		super.tearDown();
	}

	public void testDescribeEmptyContent() throws Exception
	{
		Reader contents = new StringReader("");
		assertEquals(ITextContentDescriber.INDETERMINATE, describer.describe(contents, null));
	}

	public void testDescribeWithPrefix() throws Exception
	{
		Reader contents = new StringReader(describer.getPrefix());
		assertEquals(ITextContentDescriber.VALID, describer.describe(contents, null));
	}

	public void testDescribeWithGarbage() throws Exception
	{
		Reader contents = new StringReader("gjfhjdhj");
		assertEquals(ITextContentDescriber.INDETERMINATE, describer.describe(contents, null));
	}

	// TODO Call describe(InputStream, IConentDescription)

	public void testDescribeInputStreamWithEmptyContent() throws Exception
	{
		InputStream stream = new ByteArrayInputStream("".getBytes());
		assertEquals(ITextContentDescriber.INDETERMINATE, describer.describe(stream, null));
	}

	public void testDescribeInputStreamWithPrefix() throws Exception
	{
		InputStream stream = new ByteArrayInputStream(describer.getPrefix().getBytes());
		assertEquals(ITextContentDescriber.VALID, describer.describe(stream, null));
	}

	public void testDescribeInputStreamWithGarbage() throws Exception
	{
		InputStream stream = new ByteArrayInputStream("gjfhjdhj".getBytes());
		assertEquals(ITextContentDescriber.INDETERMINATE, describer.describe(stream, null));
	}
}
