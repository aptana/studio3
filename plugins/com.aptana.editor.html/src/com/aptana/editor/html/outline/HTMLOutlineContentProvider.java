/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.outline;

import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.PlatformUI;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.css.core.ICSSConstants;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.common.outline.CompositeOutlineContentProvider;
import com.aptana.editor.common.outline.PathResolverProvider;
import com.aptana.editor.common.resolver.IPathResolver;
import com.aptana.editor.css.outline.CSSOutlineContentProvider;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.IDebugScopes;
import com.aptana.editor.html.core.preferences.IPreferenceConstants;
import com.aptana.editor.html.parsing.HTMLParser;
import com.aptana.editor.html.parsing.ast.HTMLCommentNode;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.editor.html.parsing.ast.HTMLNode;
import com.aptana.editor.html.parsing.ast.HTMLSpecialNode;
import com.aptana.editor.html.parsing.ast.HTMLTextNode;
import com.aptana.editor.html.preferences.HTMLPreferenceUtil;
import com.aptana.editor.js.outline.JSOutlineContentProvider;
import com.aptana.js.core.IJSConstants;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseRootNode;
import com.aptana.parsing.lexer.Range;

public class HTMLOutlineContentProvider extends CompositeOutlineContentProvider
{

	private Map<String, Object[]> cache = new HashMap<String, Object[]>();
	private TreeViewer treeViewer;

	private boolean showTextNode;
	private IPathResolver resolver;
	private AbstractThemeableEditor fEditor;

	private IPreferenceChangeListener preferenceListener = new IPreferenceChangeListener()
	{

		public void preferenceChange(PreferenceChangeEvent event)
		{
			if (IPreferenceConstants.HTML_OUTLINE_SHOW_TEXT_NODES.equals(event.getKey()))
			{
				showTextNode = HTMLPreferenceUtil.getShowTextNodesInOutline();
			}
		}
	};

	public HTMLOutlineContentProvider(AbstractThemeableEditor editor)
	{
		addSubLanguage(ICSSConstants.CONTENT_TYPE_CSS, new CSSOutlineContentProvider());
		addSubLanguage(IJSConstants.CONTENT_TYPE_JS, new JSOutlineContentProvider());

		showTextNode = HTMLPreferenceUtil.getShowTextNodesInOutline();
		InstanceScope.INSTANCE.getNode(HTMLPlugin.PLUGIN_ID).addPreferenceChangeListener(preferenceListener);
		fEditor = editor;
	}

	@Override
	public void dispose()
	{
		try
		{
			InstanceScope.INSTANCE.getNode(HTMLPlugin.PLUGIN_ID).removePreferenceChangeListener(preferenceListener);
		}
		finally
		{
			super.dispose();
		}
	}

	@Override
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof HTMLOutlineItem)
		{
			// delegates to the parse node it references to
			return getChildren(((HTMLOutlineItem) parentElement).getReferenceNode());
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
					return getExternalChildren(item, attribute, ICSSConstants.CONTENT_TYPE_CSS);
				}
			}
			else
			{
				IParseNode[] styleNodes = item.getCSSStyleNodes();
				IParseNode[] jsAttrNodes = item.getJSAttributeNodes();
				if (styleNodes.length > 0 || jsAttrNodes.length > 0)
				{
					List<IParseNode> children = new ArrayList<IParseNode>();
					children.addAll(Arrays.asList(styleNodes));
					children.addAll(Arrays.asList(jsAttrNodes));
					children.addAll(Arrays.asList(item.getChildren()));
					return filter(children.toArray(new IParseNode[children.size()]));
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
				return getExternalChildren(item, attribute, IJSConstants.CONTENT_TYPE_JS);
			}
			return getChildren(item.getChild(0));
		}
		return super.getChildren(parentElement);
	}

	private String getExternalJSReference(HTMLSpecialNode item)
	{
		if (!isJavascriptTag(item))
		{
			return null;
		}
		return item.getAttributeValue("src"); //$NON-NLS-1$
	}

	private boolean isJavascriptTag(HTMLSpecialNode item)
	{
		if (item == null)
		{
			return false;
		}
		if (!item.getName().equalsIgnoreCase("script")) //$NON-NLS-1$
		{
			return false;
		}
		String type = item.getAttributeValue("type"); //$NON-NLS-1$
		if (type != null && !HTMLParser.isJavaScript(item))
		{
			return false;
		}
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
		if (element instanceof HTMLOutlineItem)
		{
			// delegates to the parse node it references to
			return hasChildren(((HTMLOutlineItem) element).getReferenceNode());
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
			String attribute = getExternalJSReference(item);
			if (attribute != null && attribute.length() > 0)
			{
				return true;
			}
		}
		return super.hasChildren(element);
	}

	private Object[] getExternalChildren(final IParseNode parent, final String srcPathOrURL, final String language)
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

					IParseNode node = ParserPoolFactory.parse(language, source, 0, sub.newChild(90)).getRootNode();
					elements = getChildren(node);
					// adjusts the offsets to match the parent node since the children belong to an external file
					for (Object element : elements)
					{
						if (element instanceof CommonOutlineItem)
						{
							((CommonOutlineItem) element).setRange(new Range(parent.getStartingOffset(), parent
									.getEndingOffset()));
						}
					}

					// caching result
					synchronized (cache)
					{
						cache.put(srcPathOrURL, elements);
					}
				}
				catch (FileNotFoundException e)
				{
					IdeLog.logTrace(HTMLPlugin.getDefault(), e.getMessage(), e, IDebugScopes.OUTLINE);
					elements = new Object[] { new OutlinePlaceholderItem(IStatus.ERROR, MessageFormat.format(
							Messages.HTMLOutlineContentProvider_FileNotFound_Error, e.getMessage())) };
				}
				catch (beaver.Parser.Exception e)
				{
					IdeLog.logTrace(HTMLPlugin.getDefault(),
							MessageFormat.format("Unable to parse the content in {0}", srcPathOrURL), e, //$NON-NLS-1$
							IDebugScopes.OUTLINE);
					elements = new Object[] { new OutlinePlaceholderItem(IStatus.ERROR,
							Messages.HTMLOutlineContentProvider_ERR_ParseContent) };
				}
				catch (Exception e)
				{
					IdeLog.logTrace(HTMLPlugin.getDefault(),
							MessageFormat.format("{0} ''{1}''", e.getMessage(), srcPathOrURL), e, IDebugScopes.OUTLINE); //$NON-NLS-1$
					elements = new Object[] { new OutlinePlaceholderItem(IStatus.ERROR, e.getMessage()) };
				}
				final Object[] finalElements = elements;
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{
						treeViewer.add(getOutlineItem(parent), finalElements);
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
						if (treeViewer != null && treeViewer.getTree() != null && !treeViewer.getTree().isDisposed())
						{
							treeViewer.remove(placeholder);
						}
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
			if (node instanceof HTMLTextNode)
			{
				if (!showTextNode || StringUtil.isEmpty(((HTMLTextNode) node).getText()))
				{
					continue;
				}
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
		if (fEditor != null)
		{
			this.resolver = PathResolverProvider.getResolver(fEditor.getEditorInput());
		}
		super.inputChanged(viewer, oldInput, newInput);
	}

	@Override
	public CommonOutlineItem getOutlineItem(IParseNode node)
	{
		if (node instanceof HTMLNode)
		{
			return new HTMLOutlineItem(node.getNameNode().getNameRange(), node);
		}
		return super.getOutlineItem(node);
	}
}
