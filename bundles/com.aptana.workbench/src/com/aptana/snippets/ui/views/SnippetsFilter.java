/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.snippets.ui.views;

import java.util.regex.Pattern;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.aptana.scripting.model.SnippetElement;

/**
 * A Viewer Filter that filters SnippetElements based on display name, description and tags
 * 
 * @author nle
 */
public class SnippetsFilter extends ViewerFilter
{
	private Pattern pattern;

	public void setPattern(Pattern pattern)
	{
		this.pattern = pattern;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		if (pattern != null && element instanceof SnippetElement)
		{
			SnippetElement snippetElement = (SnippetElement) element;
			if (snippetElement.getDisplayName() != null && pattern.matcher(snippetElement.getDisplayName()).find())
			{
				return true;
			}
			else if (snippetElement.getDescription() != null && pattern.matcher(snippetElement.getDescription()).find())
			{
				return true;
			}
			else if (snippetElement.getTags() != null)
			{
				for (String tag : snippetElement.getTags())
				{
					if (pattern.matcher(tag).find())
					{
						return true;
					}
				}
			}
		}

		return false;
	}
}
