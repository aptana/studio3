/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.outline;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.resolver.IPathResolver;
import com.aptana.parsing.ast.IParseNode;

public class CommonOutlineContentProvider implements ITreeContentProvider
{

	protected static final Object[] EMPTY = new Object[0];
	private IParseListener fListener;
	protected IPathResolver resolver;

	private IParseNode fRootNode;

	public CommonOutlineItem getOutlineItem(IParseNode node)
	{
		if (node == null)
		{
			return null;
		}
		return new CommonOutlineItem(node.getNameNode().getNameRange(), node);
	}

	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof AbstractThemeableEditor)
		{
			fRootNode = ((AbstractThemeableEditor) parentElement).getFileService().getParseResult();
			if (fRootNode != null)
			{
				return filter(fRootNode.getChildren());
			}
		}
		else if (parentElement instanceof IParseNode)
		{
			return filter(((IParseNode) parentElement).getChildren());
		}
		else if (parentElement instanceof CommonOutlineItem)
		{
			// delegates to the parse node it references to
			return getChildren(((CommonOutlineItem) parentElement).getReferenceNode());
		}
		return EMPTY;
	}

	public Object getParent(Object element)
	{
		if (element instanceof IParseNode)
		{
			return ((IParseNode) element).getParent();
		}
		if (element instanceof CommonOutlineItem)
		{
			IParseNode node = ((CommonOutlineItem) element).getReferenceNode();
			return getOutlineItem(node.getParent());
		}
		return null;
	}

	public boolean hasChildren(Object element)
	{
		return getChildren(element).length > 0;
	}

	public Object[] getElements(Object inputElement)
	{
		return getChildren(inputElement);
	}

	public void dispose()
	{
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		boolean isCU = (newInput instanceof AbstractThemeableEditor);

		if (isCU && fListener == null)
		{
			final AbstractThemeableEditor editor = (AbstractThemeableEditor) newInput;
			fListener = new IParseListener()
			{
				public void parseFinished()
				{
					Display.getDefault().asyncExec(new Runnable()
					{

						public void run()
						{
							if (!editor.hasOutlinePageCreated())
							{
								return;
							}

							// FIXME What if the parse failed! We don't really want to wipe the existing results! This
							// is just a hack!
							IParseNode node = editor.getFileService().getParseResult();
							if (node != null)
							{
								boolean shouldAutoExpand = (fRootNode == null);
								CommonOutlinePage page = editor.getOutlinePage();
								page.refresh();
								if (shouldAutoExpand)
								{
									page.expandToLevel(2);
								}
							}
						}
					});
				}
			};
			editor.getFileService().addListener(fListener);
			this.resolver = PathResolverProvider.getResolver(editor.getEditorInput());
		}
		else if (!isCU && fListener != null)
		{
			AbstractThemeableEditor editor = (AbstractThemeableEditor) oldInput;
			editor.getFileService().removeListener(fListener);
			fListener = null;
			this.resolver = PathResolverProvider.getResolver(editor.getEditorInput());
		}
	}

	/**
	 * Subclass could override to return a specific list from the result.
	 * 
	 * @param nodes
	 *            the array containing the parse result
	 * @return the specific top level objects to display
	 */
	protected Object[] filter(IParseNode[] nodes)
	{
		return nodes;
	}
}
