package com.aptana.editor.ruby;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.ruby.messages"; //$NON-NLS-1$
	public static String CoreStubber_IndexingRuby;
	public static String CoreStubber_IndexingRubyCore;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
