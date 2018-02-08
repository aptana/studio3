package com.aptana.index.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.index.core.messages"; //$NON-NLS-1$

	public static String AbstractFileIndexingParticipant_Indexing_Message;

	public static String IndexFilesOfProjectJob_Name;
	public static String IndexPlugin_IndexingFile;
	public static String IndexRequestJob_Name;

	public static String MetadataLoader_Error_Loading_Metadata;
	
	public static String MetadataObjectsReader_IOErrorOccurredProcessingDocumentationXML;

	public static String MetadataReader_ErrorLoadingDocumentationXML;
	public static String MetadataReader_IOErrorProcessingDocumentationXML;
	public static String MetadataReader_SAXParserConfiguredIncorrectly;
	public static String MetadataReader_ErrorParsingDocumentationXML;

	public static String RemoveIndexOfFilesOfProjectJob_Name;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
