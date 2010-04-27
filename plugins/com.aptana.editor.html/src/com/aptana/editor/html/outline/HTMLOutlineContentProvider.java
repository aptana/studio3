package com.aptana.editor.html.outline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.common.outline.CompositeOutlineContentProvider;
import com.aptana.editor.css.outline.CSSOutlineContentProvider;
import com.aptana.editor.css.parsing.CSSParserFactory;
import com.aptana.editor.css.parsing.ICSSParserConstants;
import com.aptana.editor.html.Activator;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.editor.html.parsing.ast.HTMLSpecialNode;
import com.aptana.editor.js.outline.JSOutlineContentProvider;
import com.aptana.editor.js.parsing.IJSParserConstants;
import com.aptana.editor.js.parsing.JSParserFactory;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class HTMLOutlineContentProvider extends CompositeOutlineContentProvider
{

	private Map<String, Object[]> cache = new HashMap<String, Object[]>();

	public HTMLOutlineContentProvider()
	{
		addSubLanguage(ICSSParserConstants.LANGUAGE, new CSSOutlineContentProvider());
		addSubLanguage(IJSParserConstants.LANGUAGE, new JSOutlineContentProvider());
	}

	// TODO Expand the external items lazily/asynch somehow
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
			if (item.getName().equalsIgnoreCase("link")) //$NON-NLS-1$
			{
				String rel = item.getAttributeValue("rel"); //$NON-NLS-1$
				if (rel.equals("stylesheet")) //$NON-NLS-1$
				{
					String attribute = item.getAttributeValue("href"); //$NON-NLS-1$
					if (attribute.length() > 0)
					{
						return getExternalChildren(attribute, ICSSParserConstants.LANGUAGE);
					}
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
			if (item.getName().equalsIgnoreCase("script")) { //$NON-NLS-1$
				String attribute = item.getAttributeValue("src"); //$NON-NLS-1$
				if (attribute.length() > 0)
				{
					return getExternalChildren(attribute, IJSParserConstants.LANGUAGE);
				}
			}

			return getChildren(item.getChild(0));
		}
		return super.getChildren(parentElement);
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
			if (item.getName().equalsIgnoreCase("link")) //$NON-NLS-1$
			{
				String rel = item.getAttributeValue("rel"); //$NON-NLS-1$
				if (rel.equals("stylesheet")) //$NON-NLS-1$
				{
					String attribute = item.getAttributeValue("href"); //$NON-NLS-1$
					if (attribute.length() > 0)
					{
						return true;
					}
				}
			}
		}
		// Handle embedded languages (JS and CSS)
		if (element instanceof HTMLSpecialNode)
		{
			// HTMLSpecialNode always has the root node of the nested language as its child; we want to skip that and
			// get the content below
			HTMLSpecialNode item = (HTMLSpecialNode) element;

			// Special case of external JS file
			if (item.getName().equalsIgnoreCase("script")) { //$NON-NLS-1$
				String attribute = item.getAttributeValue("src"); //$NON-NLS-1$
				if (attribute.length() > 0)
				{
					return true;
				}
			}
		}
		return super.hasChildren(element);
	}

	private Object[] parse(String source, String language) throws Exception
	{
		if (source == null)
		{
			return EMPTY;
		}
		IParser parser = getParser(language);
		if (parser == null)
		{
			return EMPTY;
		}
		IParseState pState = new ParseState();
		pState.setEditState(source, source, 0, 0);
		IParseNode parse = parser.parse(pState);
		return getChildren(parse);
	}

	private IParser getParser(String language)
	{
		if (language.equals(IJSParserConstants.LANGUAGE))
			return JSParserFactory.getInstance().getParser();
		if (language.equals(ICSSParserConstants.LANGUAGE))
			return CSSParserFactory.getInstance().getParser();
		return null;
	}

	private Object[] getExternalChildren(final String srcPathOrURL, String language)
	{
		Object[] cached = cache.get(srcPathOrURL);
		if (cached != null)
		{
			// we have a cached result
			return cached;
		}

		if (resolver == null)
		{
			return EMPTY;
		}
		try
		{
			// resolving source and editor input
			String source = resolver.resolveSource(srcPathOrURL);
			Object[] elements = parse(source, language);

			// caching result
			cache.put(srcPathOrURL, elements);
			return elements;
		}
		catch (Exception e)
		{
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			return EMPTY;
		}

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
}
