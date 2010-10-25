package com.aptana.editor.erb.html;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;

import com.aptana.editor.erb.common.ERBEditorTestCase;

public class RHTMLEditorTest extends ERBEditorTestCase
{

	@Override
	protected IFileStore getFileStore() throws Exception
	{
		return EFS.getStore((new File("testhtml.erb").toURI())); //$NON-NLS-1$
	}

	@Override
	protected String getClassName()
	{
		return RHTMLEditor.class.getName();
	}

	@Override
	protected String getEditorId()
	{
		return "com.aptana.editor.erb.html"; //$NON-NLS-1$
	}
}
