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
import com.aptana.editor.css.parsing.ICSSParserConstants;
import com.aptana.editor.html.Activator;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.editor.html.parsing.ast.HTMLSpecialNode;
import com.aptana.editor.js.outline.JSOutlineContentProvider;
import com.aptana.editor.js.parsing.IJSParserConstants;
import com.aptana.editor.js.parsing.JSParser;
import com.aptana.parsing.IParseState;
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

	@Override
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof CommonOutlineItem)
		{
			// delegates to the parse node it references to
			return getChildren(((CommonOutlineItem) parentElement).getReferenceNode());
		}
		if (parentElement instanceof HTMLSpecialNode)
		{
			// HTMLSpecialNode always has the root node of the nested language as its child; we want to skip that and
			// get the content below
			HTMLSpecialNode item = (HTMLSpecialNode) parentElement;

			// addition to allow expansion of script tags with src attribute;
			if (item.getName().equals("script")) { //$NON-NLS-1$
				String attribute = item.getAttributeValue("src"); //$NON-NLS-1$
				if (attribute.length() > 0)
				{
					return getExternalScriptChildren(attribute,item);
				}
			}			
			
			return getChildren(item.getChild(0));
		}
		return super.getChildren(parentElement);
	}
	
	private Object[] getExternalScriptChildren(final String attribute, HTMLSpecialNode parent)
	{
		Object[] cached = cache.get(attribute);
		if (cached != null)
		{
			//we have a cached result
			return cached;
		}

		if (resolver == null)
		{
			return EMPTY;
		}
		try
		{
			//registering listener
//			IPropertyChangeListener propertyChangeListener = listeners.get(attribute);
//			if (propertyChangeListener == null)
//			{
//				propertyChangeListener = new IPropertyChangeListener()
//				{
//
//					public void propertyChange(PropertyChangeEvent event)
//					{
//						cache.remove(attribute);
//					}
//
//				};
//				resolver.addChangeListener(attribute, propertyChangeListener);
//			}
//			listeners.put(attribute, propertyChangeListener);
			
			//resolving source and editor input
			String source = resolver.resolveSource(attribute);
//			IEditorInput input = resolver.resolveEditorInput(attribute);
			if (source == null)
			{
				return EMPTY;
//				return new Object[] { new WarningItem(StringUtils.format(
//						HTMLContentProviderMessages.HTMLContentProvider_NOT_RESOLVABLE, attribute)) };
			}
			JSParser ps;
//			try
//			{
				//parsing
				ps = new JSParser();
				IParseState pState = new ParseState();
				pState.setEditState(source, source, 0, 0);
				IParseNode parse = ps.parse(pState);
//				JSOutlineContentProvider provider = new JSOutlineContentProvider();
				
				Object[] elements = new Object[] { parse.getChild(0) };
				//acquiring items
//				Object[] elements = provider.getElements(parse.getChildren());
//				for (int a = 0; a < elements.length; a++)
//				{
//					if (elements[a] instanceof JSOutlineItem)
//					{
//						JSOutlineItem item = (JSOutlineItem) elements[a];
//						item.setResolveInformation(input);
//						item.setParent(parent);
//						parent.addChild(item);
//					}
//				}
				
				//caching result
				cache.put(attribute, elements);
				return elements;
//			}
//			catch (ParserInitializationException e)
//			{
//				IdeLog.logError(HTMLPlugin.getDefault(), e.getMessage());
//				return NO_OBJECTS;
//			}
//			catch (LexerException e)
//			{
//				IdeLog.logError(HTMLPlugin.getDefault(), e.getMessage());
//				return NO_OBJECTS;
//			}

		}
		catch (Exception e)
		{
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
//			WarningItem warningItem = new WarningItem(e.getMessage());
//			warningItem.setError(true);
//			return new Object[] { warningItem };
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
