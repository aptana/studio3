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
package com.aptana.editor.html.outline;

import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.PlatformUI;

import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.common.outline.CompositeOutlineContentProvider;
import com.aptana.editor.css.outline.CSSOutlineContentProvider;
import com.aptana.editor.css.parsing.ICSSParserConstants;
import com.aptana.editor.html.Activator;
import com.aptana.editor.html.parsing.ast.HTMLCommentNode;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.editor.html.parsing.ast.HTMLSpecialNode;
import com.aptana.editor.js.outline.JSOutlineContentProvider;
import com.aptana.editor.js.parsing.IJSParserConstants;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.IParserPool;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class HTMLOutlineContentProvider extends CompositeOutlineContentProvider
{

	private Map<String, Object[]> cache = new HashMap<String, Object[]>();
	private TreeViewer treeViewer;

	public HTMLOutlineContentProvider()
	{
		addSubLanguage(ICSSParserConstants.LANGUAGE, new CSSOutlineContentProvider());
		addSubLanguage(IJSParserConstants.LANGUAGE, new JSOutlineContentProvider());
	}

	@Override
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof CommonOutlineItem)
		{
			// delegates to the parse node it references to
			return getChildren(((CommonOutlineItem) parentElement).getReferenceNode());
		}
		// Handle expansion of link tags pointing to stylesheets
		if (parentElement instanceof HTMLElementNode)
		{
			HTMLElementNode item = (HTMLElementNode) parentElement;

			if (isStylesheetLinkTag(item))
			{
				String attribute = getExternalCSSReference(item);
				if (attribute != null && attribute.length() > 0)
				{
					return getExternalChildren(parentElement, attribute, ICSSParserConstants.LANGUAGE);
				}
			}
		}
		// Handle embedded languages (JS and CSS)
		if (parentElement instanceof HTMLSpecialNode)
		{
			// HTMLSpecialNode always has the root node of the nested language as its child; we want to skip that and
			// get the content below
			HTMLSpecialNode item = (HTMLSpecialNode) parentElement;

			// Special case of external JS file
			String attribute = getExternalJSReference(item);
			if (attribute != null && attribute.length() > 0)
			{
				return getExternalChildren(parentElement, attribute, IJSParserConstants.LANGUAGE);
			}
			return getChildren(item.getChild(0));
		}
		return super.getChildren(parentElement);
	}

	private String getExternalJSReference(HTMLSpecialNode item)
	{
		if (!isJavascriptTag(item))
			return null;

		return item.getAttributeValue("src"); //$NON-NLS-1$
	}

	private boolean isJavascriptTag(HTMLSpecialNode item)
	{
		if (item == null)
			return false;
		if (!item.getName().equalsIgnoreCase("script")) //$NON-NLS-1$
			return false;
		if (item.getChild(0) == null || !item.getChild(0).getLanguage().equals(IJSParserConstants.LANGUAGE))
			return false;

		return true;
	}

	private boolean isStylesheetLinkTag(HTMLElementNode item)
	{
		if (item == null)
			return false;

		if (!item.getName().equalsIgnoreCase("link")) //$NON-NLS-1$
			return false;

		String rel = item.getAttributeValue("rel"); //$NON-NLS-1$
		if (rel == null || !rel.equals("stylesheet")) //$NON-NLS-1$
			return false;

		return true;
	}

	private String getExternalCSSReference(HTMLElementNode item)
	{
		if (!isStylesheetLinkTag(item))
			return null;

		return item.getAttributeValue("href"); //$NON-NLS-1$
	}

	/**
	 * Override hasChildren so for external stylesheets and JS we just assume there's content and don't fetch it one
	 * layer too early (on expansion of the tag's parent).
	 */
	@Override
	public boolean hasChildren(Object element)
	{
		if (element instanceof CommonOutlineItem)
		{
			// delegates to the parse node it references to
			return hasChildren(((CommonOutlineItem) element).getReferenceNode());
		}

		// Handle expansion of link tags pointing to stylesheets
		if (element instanceof HTMLElementNode)
		{
			HTMLElementNode item = (HTMLElementNode) element;
			if (isStylesheetLinkTag(item))
			{
				String attribute = getExternalCSSReference(item);
				return attribute != null && attribute.length() > 0;
			}
		}
		// Handle embedded languages (JS and CSS)
		if (element instanceof HTMLSpecialNode)
		{
			// HTMLSpecialNode always has the root node of the nested language as its child; we want to skip that and
			// get the content below
			HTMLSpecialNode item = (HTMLSpecialNode) element;

			// Special case of external JS file
			if (isJavascriptTag(item))
			{
				String attribute = getExternalJSReference(item);
				
				if (attribute != null && attribute.length() > 0)
				{
					return true;
				}
			}
		}
		return super.hasChildren(element);
	}

	private IParseNode parse(IParser parser, String source) throws Exception
	{
		IParseState pState = new ParseState();
		pState.setEditState(source, source, 0, 0);
		return parser.parse(pState);
	}

	private Object[] getExternalChildren(final Object parent, final String srcPathOrURL, final String language)
	{
		Object[] cached;
		synchronized (cache)
		{
			cached = cache.get(srcPathOrURL);
		}
		if (cached != null)
		{
			// we have a cached result
			return cached;
		}

		if (resolver == null)
		{
			return EMPTY;
		}

		// schedule job to get file, parse and get children and then add to parent. In the meantime return a
		// placeholder.
		Job job = new Job(Messages.HTMLOutlineContentProvider_FetchingExternalFilesJobName)
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				SubMonitor sub = SubMonitor.convert(monitor, 100);
				Object[] elements;
				try
				{
					// resolving source and editor input
					String source = resolver.resolveSource(srcPathOrURL, sub.newChild(50));
					if (source == null)
					{
						throw new Exception(Messages.HTMLOutlineContentProvider_UnableToResolveFile_Error);
					}

					IParserPool pool = ParserPoolFactory.getInstance().getParserPool(language);
					if (pool == null)
					{
						throw new Exception(MessageFormat.format(
								Messages.HTMLOutlineContentProvider_UnableToFindParser_Error, language));
					}
					IParser parser = pool.checkOut();
					if (parser == null)
					{
						throw new Exception(MessageFormat.format(
								Messages.HTMLOutlineContentProvider_UnableToFindParser_Error, language));
					}
					IParseNode node = parse(parser, source);
					pool.checkIn(parser);
					sub.worked(90);
					elements = getChildren(node);

					// caching result
					synchronized (cache)
					{
						cache.put(srcPathOrURL, elements);
					}
				}
				catch (FileNotFoundException e)
				{
					Activator.getDefault().getLog()
							.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
					elements = new Object[] { new OutlinePlaceholderItem(IStatus.ERROR, MessageFormat.format(
							Messages.HTMLOutlineContentProvider_FileNotFound_Error, e.getMessage())) };
				}
				catch (Exception e)
				{
					Activator.getDefault().getLog()
							.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
					elements = new Object[] { new OutlinePlaceholderItem(IStatus.ERROR, e.getMessage()) };
				}
				final Object[] finalElements = elements;
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{
						treeViewer.add(getOutlineItem((IParseNode) parent), finalElements);
					}
				});
				sub.done();
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.LONG);
		job.schedule();
		final OutlinePlaceholderItem placeholder = new OutlinePlaceholderItem(IStatus.INFO,
				Messages.HTMLOutlineContentProvider_PlaceholderItemLabel);
		// Listen for update, when we have it, remove the placeholder
		job.addJobChangeListener(new JobChangeAdapter()
		{

			@Override
			public void done(IJobChangeEvent event)
			{
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{
						treeViewer.remove(placeholder);
					}
				});
			}
		});

		return new Object[] { placeholder };
	}

	@Override
	public Object getParent(Object element)
	{
		if (element instanceof CommonOutlineItem)
		{
			IParseNode node = ((CommonOutlineItem) element).getReferenceNode();
			IParseNode parent = node.getParent();
			if (parent instanceof ParseRootNode)
			{
				// we're at the root of the nested language, which is not displayed; go one level up
				parent = parent.getParent();
			}
			return getOutlineItem(parent);
		}
		return super.getParent(element);
	}

	@Override
	protected Object[] filter(IParseNode[] nodes)
	{
		List<CommonOutlineItem> items = new ArrayList<CommonOutlineItem>();
		HTMLElementNode element;
		for (IParseNode node : nodes)
		{
			if (node instanceof HTMLCommentNode)
			{
				// ignores comment nodes in outline
				continue;
			}
			if (node instanceof HTMLElementNode)
			{
				// for HTML node, only takes the element node
				element = (HTMLElementNode) node;
				if (element.getName().length() > 0)
				{
					items.add(getOutlineItem(element));
				}
			}
			else
			{
				// includes all non-HTML nodes and let the nested language handle its own filtering
				items.add(getOutlineItem(node));
			}
		}
		return items.toArray(new CommonOutlineItem[items.size()]);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		this.treeViewer = (TreeViewer) viewer;
		super.inputChanged(viewer, oldInput, newInput);
	}
}
