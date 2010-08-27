package com.aptana.editor.common.text.reconciler;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.common.text.reconciler.messages"; //$NON-NLS-1$
	public static String CommonReconcilingStrategy_FoldingTaskName;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
