package com.aptana.editor.erb.common;

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
}
