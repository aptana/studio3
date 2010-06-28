package com.aptana.editor.html.outline;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages"; //$NON-NLS-1$
	
	public static String HTMLOutlineContentProvider_PlaceholderItemLabel;
	public static String HTMLOutlineContentProvider_FetchingExternalFilesJobName;
	public static String HTMLOutlineContentProvider_UnableToResolveFile_Error;
	public static String HTMLOutlineContentProvider_UnableToFindParser_Error;
	public static String HTMLOutlineContentProvider_FileNotFound_Error;
	
	static
	{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
