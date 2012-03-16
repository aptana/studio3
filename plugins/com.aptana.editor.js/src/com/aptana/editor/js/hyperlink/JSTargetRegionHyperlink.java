/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.hyperlink;

import org.eclipse.jface.text.IRegion;

import com.aptana.editor.common.AbstractThemeableEditor;

/**
 * JSTargetRegionHyperlink
 */
public class JSTargetRegionHyperlink extends JSAbstractHyperlink
{
	private IRegion targetRegion;

	public JSTargetRegionHyperlink(IRegion hyperlinkRegion, String typeLabel, String hyperlinkText,
			String targetFilePath, IRegion targetRegion)
	{
		super(hyperlinkRegion, typeLabel, hyperlinkText, targetFilePath);

		this.targetRegion = targetRegion;
	}

	/**
	 * getTargetRegion
	 * 
	 * @return
	 */
	public IRegion getTargetRegion()
	{
		return targetRegion;
	}

	public void open()
	{
		if (targetRegion != null)
		{
			AbstractThemeableEditor editor = getEditor();

			if (editor != null)
			{
				editor.selectAndReveal(targetRegion.getOffset(), targetRegion.getLength());
			}
		}
	}
}
