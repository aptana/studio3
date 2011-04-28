/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.internal;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

public class EditorPropertyTester extends ProjectPropertyTester
{

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
	{
		if (receiver instanceof IEditorPart)
		{
			IEditorInput editorInput = ((IEditorPart) receiver).getEditorInput();
			if (editorInput instanceof IFileEditorInput)
			{
				return super.test(((IFileEditorInput) editorInput).getFile(), property, args, expectedValue);
			}
		}
		return false;
	}
}
