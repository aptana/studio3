package com.aptana.preview.internal;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ui.IEditorPart;

import com.aptana.preview.PreviewManager;

public class HasPreviewPropertyTester extends PropertyTester
{

	private static final String HAS_PREVIEW_HANDLER = "hasPreviewHandler"; //$NON-NLS-1$

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
	{
		if (HAS_PREVIEW_HANDLER.equals(property))
		{
			IEditorPart editorPart = (IEditorPart) receiver;
			if (editorPart != null)
			{
				if (PreviewManager.getInstance().testEditorInputForPreview(editorPart.getEditorInput()))
				{
					return true;
				}
			}
		}
		return false;
	}
}
