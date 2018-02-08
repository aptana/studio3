/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.help.IContext;
import org.eclipse.help.IContext2;
import org.eclipse.help.IContextProvider;
import org.eclipse.help.IHelpResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import com.aptana.core.IMap;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.core.util.replace.RegexPatternReplacer;
import com.aptana.core.util.replace.SimpleTextPatternReplacer;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.js.actions.IJSActions;
import com.aptana.editor.js.actions.OpenDeclarationAction;
import com.aptana.editor.js.contentassist.JSModelFormatter;
import com.aptana.editor.js.internal.JSModelUtil;
import com.aptana.editor.js.internal.text.JSFoldingComputer;
import com.aptana.editor.js.outline.JSOutlineContentProvider;
import com.aptana.editor.js.outline.JSOutlineLabelProvider;
import com.aptana.js.core.IJSConstants;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.SinceElement;
import com.aptana.parsing.ast.INameNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;

@SuppressWarnings("restriction")
public class JSSourceEditor extends AbstractThemeableEditor
{
	private static final class JSContextProvider implements IContextProvider
	{
		@SuppressWarnings("nls")
		private static final class JSHelpContext implements IContext2
		{
			private static SimpleTextPatternReplacer TAG_MAPPER;
			static
			{
				TAG_MAPPER = new SimpleTextPatternReplacer();

				// tag mapping
				TAG_MAPPER.addPattern("<h2>", "<b>");
				TAG_MAPPER.addPattern("</h2>", "</b>");
				TAG_MAPPER.addPattern("<h3>", "<b>");
				TAG_MAPPER.addPattern("</h3>", "</b>");
				TAG_MAPPER.addPattern("<pre>", "<code>");
				TAG_MAPPER.addPattern("</pre>", "</code>");
				TAG_MAPPER.addPattern("<p>", "<br><br>");
				TAG_MAPPER.addPattern("</p>", "<br><br>");

				// tag removal
				TAG_MAPPER.addPattern("<hr>");

				for (String tag : CollectionsUtil.newList("warning", "tip", "glossary", "method", "varname",
						"specification"))
				{
					TAG_MAPPER.addPattern(StringUtil.concat("<", tag, ">"));
					TAG_MAPPER.addPattern(StringUtil.concat("</", tag, ">"));
				}
			}

			private static RegexPatternReplacer CODE_CLEANER;
			static
			{
				CODE_CLEANER = new RegexPatternReplacer();
				CODE_CLEANER.addPattern("<code>[^<]+</code>", new IMap<String, String>()
				{
					public String map(String item)
					{
						return StringUtil.join("</code><br><code>", StringUtil.LINE_SPLITTER.split(item));
					}
				});
			}

			private AbstractThemeableEditor editor;
			private final IParseNode node;
			private Collection<PropertyElement> fProperties;

			private JSHelpContext(AbstractThemeableEditor editor, IParseNode node)
			{
				this.editor = editor;
				this.node = node;
			}

			public IHelpResource[] getRelatedTopics()
			{
				Collection<PropertyElement> properties = getActiveProperty();
				if (!CollectionsUtil.isEmpty(properties))
				{
					Set<IHelpResource> refs = new HashSet<IHelpResource>();
					for (PropertyElement pe : properties)
					{
						for (SinceElement se : pe.getSinceList())
						{
							String version = se.getVersion();
							if ("DOM 0".equals(version))
							{
								refs.add(new JSDOMHelpResource(0));
							}
							else if ("HTML DOM Level 2".equals(version))
							{
								refs.add(new JSDOMHelpResource(2));
							}
							else if ("HTML DOM Level 3".equals(version))
							{
								refs.add(new JSDOMHelpResource(3));
							}
							else if ("DOM5 HTML".equals(version))
							{
								refs.add(new JSDOMHelpResource(5));
							}
						}
						return refs.toArray(new IHelpResource[refs.size()]);
					}
				}
				return null;
			}

			public String getText()
			{
				Collection<PropertyElement> properties = getActiveProperty();
				if (!CollectionsUtil.isEmpty(properties))
				{
					return stripUnusedTags(JSModelFormatter.DYNAMIC_HELP.getDocumentation(properties));
				}
				return null;
			}

			private synchronized Collection<PropertyElement> getActiveProperty()
			{
				if (node == null)
				{
					return Collections.emptyList();
				}
				if (fProperties == null)
				{
					fProperties = JSModelUtil.getProperties(editor, node);
				}
				return fProperties;
			}

			private String stripUnusedTags(String text)
			{
				if (text == null)
				{
					return null;
				}

				return CODE_CLEANER.searchAndReplace(TAG_MAPPER.searchAndReplace(text));
			}

			public String getTitle()
			{
				if (node == null)
				{
					return null;
				}
				INameNode nameNode = node.getNameNode();
				if (nameNode == null)
				{
					return null;
				}
				return nameNode.getName();
			}

			public String getStyledText()
			{
				return null;
			}

			public String getCategory(IHelpResource topic)
			{
				// TODO Auto-generated method stub
				return null;
			}
		}

		private final JSSourceEditor editorPart;

		private JSContextProvider(JSSourceEditor editorPart)
		{
			this.editorPart = editorPart;
		}

		public String getSearchExpression(Object target)
		{
			return null;
		}

		public int getContextChangeMask()
		{
			return SELECTION;
		}

		public IContext getContext(Object target)
		{
			if (target instanceof IParseNode)
			{
				return new JSHelpContext(editorPart, (IParseNode) target);
			}
			ISelection selection = editorPart.getSelectionProvider().getSelection();
			if (selection.isEmpty())
			{
				return null;
			}
			ITextSelection textSelection = (ITextSelection) selection;
			int offset = textSelection.getOffset();
			return new JSHelpContext(editorPart, editorPart.getASTNodeAt(offset, editorPart.getAST()));
		}
	}

	private static final class JSDOMHelpResource implements IHelpResource
	{
		private int domNumber;

		JSDOMHelpResource(int number)
		{
			this.domNumber = number;
		}

		public String getLabel()
		{
			return MessageFormat.format("DOM {0}", domNumber); //$NON-NLS-1$
		}

		public String getHref()
		{
			return MessageFormat.format("http://aptana.com/reference/html/api/HTMLDOM{0}.index.html", domNumber); //$NON-NLS-1$
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof JSDOMHelpResource)
			{
				JSDOMHelpResource other = (JSDOMHelpResource) obj;
				return other.domNumber == domNumber;
			}
			return false;
		}

		@Override
		public int hashCode()
		{
			return 31 * domNumber;
		}
	}

	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();

		setPreferenceStore(getChainedPreferenceStore());

		setSourceViewerConfiguration(new JSSourceViewerConfiguration(getPreferenceStore(), this));
		setDocumentProvider(JSPlugin.getDefault().getJSDocumentProvider());
	}

	public JSSourceViewerConfiguration getJSSourceViewerConfiguration()
	{
		return (JSSourceViewerConfiguration) super.getSourceViewerConfiguration();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#createActions()
	 */
	@Override
	protected void createActions()
	{
		super.createActions();
		IAction action = new OpenDeclarationAction(Messages.getResourceBundle(), this);
		action.setActionDefinitionId(IJSActions.OPEN_DECLARATION);
		setAction(IJSActions.OPEN_DECLARATION, action);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#editorContextMenuAboutToShow(org.eclipse.jface.action.
	 * IMenuManager)
	 */
	@Override
	protected void editorContextMenuAboutToShow(IMenuManager menu)
	{
		super.editorContextMenuAboutToShow(menu);

		IAction action = getAction(IJSActions.OPEN_DECLARATION);

		if (action != null)
		{
			menu.appendToGroup("group.open", action); //$NON-NLS-1$
		}
	}

	public static IPreferenceStore getChainedPreferenceStore()
	{
		return new ChainedPreferenceStore(new IPreferenceStore[] { JSPlugin.getDefault().getPreferenceStore(),
				CommonEditorPlugin.getDefault().getPreferenceStore(), EditorsPlugin.getDefault().getPreferenceStore() });
	}

	@Override
	public ITreeContentProvider getOutlineContentProvider()
	{
		return new JSOutlineContentProvider();
	}

	@Override
	public ILabelProvider getOutlineLabelProvider()
	{
		return new JSOutlineLabelProvider();
	}

	@Override
	protected IPreferenceStore getOutlinePreferenceStore()
	{
		return JSPlugin.getDefault().getPreferenceStore();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#getPluginPreferenceStore()
	 */
	@Override
	protected IPreferenceStore getPluginPreferenceStore()
	{
		return JSPlugin.getDefault().getPreferenceStore();
	}

	@Override
	public IFoldingComputer createFoldingComputer(IDocument document)
	{
		return new JSFoldingComputer(this, document);
	}

	@Override
	public String getContentType()
	{
		return IJSConstants.CONTENT_TYPE_JS;
	}

	@Override
	public Object getAdapter(Class adapter)
	{
		if (IContextProvider.class == adapter)
		{
			return new JSContextProvider(this);
		}
		return super.getAdapter(adapter);
	}

	@Override
	public void refreshOutline(final IParseRootNode ast)
	{
		outlineAutoExpanded = true; // Don't auto-expand it here.
		super.refreshOutline(ast);
	}
}
