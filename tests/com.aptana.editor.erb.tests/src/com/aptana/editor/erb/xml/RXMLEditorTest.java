package com.aptana.editor.erb.xml;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;

import com.aptana.editor.erb.common.ERBEditorTestCase;

public class RXMLEditorTest extends ERBEditorTestCase
{

	@Override
	protected IFileStore getFileStore() throws Exception
	{
		return EFS.getStore((new File("testxml.erb").toURI())); //$NON-NLS-1$
	}

	@Override
	protected String getClassName()
	{
		return RXMLEditor.class.getName();
	}

	@Override
	protected String getEditorId()
	{
		return "com.aptana.editor.erb.xml"; //$NON-NLS-1$
	}
}
