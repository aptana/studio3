/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import org.eclipse.osgi.util.NLS;

/**
 * Messages
 * @author Ingo Muschenetz
 *
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.common.contentassist.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	public static String MetadataLoader_Error_Loading_Metadata;

	/**
	 * MetadataObjectsReader_UnableToLocateDocumentationXML
	 */
	public static String MetadataObjectsReader_UnableToLocateDocumentationXML;
	
	/**
	 * MetadataObjectsReader_IOErrorOccurredProcessingDocumentationXML
	 */
	public static String MetadataObjectsReader_IOErrorOccurredProcessingDocumentationXML;
	
	/**
	 * MetadataObjectsReader_IOErrorOccurredProcessingDocumentationBinary
	 */
	public static String MetadataObjectsReader_IOErrorOccurredProcessingDocumentationBinary;
	
	/**
	 * MetadataReader_ErrorLoadingDocumentationXML
	 */
	public static String MetadataReader_ErrorLoadingDocumentationXML;
	
	/**
	 * MetadataReader_IOErrorProcessingDocumentationXML
	 */
	public static String MetadataReader_IOErrorProcessingDocumentationXML;
	
	/**
	 * MetadataReader_SAXParserConfiguredIncorrectly
	 */
	public static String MetadataReader_SAXParserConfiguredIncorrectly;
	
	/**
	 * MetadataReader_ErrorParsingDocumentationXML
	 */
	public static String MetadataReader_ErrorParsingDocumentationXML;
}
