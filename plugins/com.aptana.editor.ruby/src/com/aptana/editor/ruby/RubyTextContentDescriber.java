package com.aptana.editor.ruby;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.core.internal.content.TextContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;

@SuppressWarnings("restriction")
public class RubyTextContentDescriber extends TextContentDescriber
{

	@Override
	public int describe(InputStream contents, IContentDescription description) throws IOException
	{
		return describe(new InputStreamReader(contents), description);
	}

	@Override
	public int describe(Reader contents, IContentDescription description) throws IOException
	{
		int result = super.describe(contents, description);
		String firstLine = new BufferedReader(contents).readLine();
		// Verify that a shebang line is there
		if (firstLine.contains("#!") && firstLine.contains("ruby")) //$NON-NLS-1$ //$NON-NLS-2$
			return VALID;
		// TODO Now try passing a syntax check?!
		return result;
	}
}
