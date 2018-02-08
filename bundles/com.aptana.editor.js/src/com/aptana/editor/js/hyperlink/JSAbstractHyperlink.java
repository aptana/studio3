/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.hyperlink;

import java.io.File;
import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ObjectUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.util.EditorUtil;
import com.aptana.editor.js.JSPlugin;

/**
 * JSAbstractHyperlink
 */
public abstract class JSAbstractHyperlink implements IJSHyperlink
{
	public static final String INVOCATION_TYPE = "invocation"; //$NON-NLS-1$
	public static final String VARIABLE_TYPE = "variable"; //$NON-NLS-1$
	public static final String PARAMETER_TYPE = "parameter"; //$NON-NLS-1$
	public static final String LOCAL_DECLARTION_TYPE = "local declaration"; //$NON-NLS-1$
	public static final String LOCAL_ASSIGNMENT_TYPE = "local assignment"; //$NON-NLS-1$

	private IRegion hyperlinkRegion;
	private String typeLabel;
	private String hyperlinkText;
	private String targetFilePath;

	protected JSAbstractHyperlink(IRegion hyperlinkRegion, String typeLabel, String hyperlinkText, String targetFilePath)
	{
		this.hyperlinkRegion = hyperlinkRegion;
		this.typeLabel = typeLabel;
		this.hyperlinkText = hyperlinkText;
		this.targetFilePath = targetFilePath;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof JSAbstractHyperlink)
		{
			JSAbstractHyperlink that = (JSAbstractHyperlink) obj;

			return ObjectUtil.areEqual(getHyperlinkRegion(), that.getHyperlinkRegion())
					&& ObjectUtil.areEqual(getTypeLabel(), that.getTypeLabel())
					&& ObjectUtil.areEqual(getHyperlinkText(), that.getHyperlinkText())
					&& ObjectUtil.areEqual(getTargetFilePath(), that.getTargetFilePath());
		}
		else
		{
			return super.equals(obj);
		}
	}

	protected AbstractThemeableEditor getEditor()
	{
		IEditorPart part = null;

		if (targetFilePath != null)
		{
			try
			{
				File file = new File(new URI(targetFilePath));

				if (file.exists())
				{
					part = EditorUtil.openInEditor(file);
				}
				else
				{
					IResource findMember = ResourcesPlugin.getWorkspace().getRoot().findMember(targetFilePath);

					if (findMember instanceof IFile && findMember.exists())
					{
						part = EditorUtil.openInEditor(new File(((IFile) findMember).getLocationURI()));
					}
				}
			}
			catch (Exception e)
			{
				String message = "An error occurred while trying to retrieve the current editor: " + e.getMessage(); //$NON-NLS-1$

				IdeLog.logWarning(JSPlugin.getDefault(), message);
			}
		}

		return (part instanceof AbstractThemeableEditor) ? (AbstractThemeableEditor) part : null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getHyperlinkRegion()
	 */
	public IRegion getHyperlinkRegion()
	{
		return hyperlinkRegion;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText()
	{
		return hyperlinkText;
	}

	/**
	 * getTargetFilePath
	 * 
	 * @return
	 */
	public String getTargetFilePath()
	{
		return targetFilePath;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getTypeLabel()
	 */
	public String getTypeLabel()
	{
		return typeLabel;
	}
}
