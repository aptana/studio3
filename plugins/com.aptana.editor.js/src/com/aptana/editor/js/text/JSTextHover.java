/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.internal.text.html.BrowserInformationControlInput;
import org.eclipse.jface.text.IInputChangedListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;

import com.aptana.core.IFilter;
import com.aptana.core.IMap;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.contentassist.CommonTextHover;
import com.aptana.editor.common.hover.CustomBrowserInformationControl;
import com.aptana.editor.common.hover.DocumentationBrowserInformationControlInput;
import com.aptana.editor.js.contentassist.JSIndexQueryHelper;
import com.aptana.editor.js.contentassist.JSLocationIdentifier;
import com.aptana.editor.js.contentassist.LocationType;
import com.aptana.editor.js.contentassist.ParseUtil;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.ParameterElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.SinceElement;
import com.aptana.editor.js.contentassist.model.UserAgentElement;
import com.aptana.editor.js.hyperlink.JSHyperlinkDetector;
import com.aptana.editor.js.parsing.ast.JSGetPropertyNode;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.ui.epl.UIEplPlugin;

@SuppressWarnings("restriction")
public class JSTextHover extends CommonTextHover implements ITextHover, ITextHoverExtension2
{

	private static final String PROPERTY_HEADER_TEMPLATE = "<b>{0}: {1} - {2}</b>"; //$NON-NLS-1$
	private static final String FUNCTION_HEADER_TEMPLATE = "<b>{0}({1}): {2} - {3}</b>"; //$NON-NLS-1$

	private String fDocs;
	private String fHeader;

	/**
	 * getActiveNode
	 * 
	 * @param textViewer
	 * @param offset
	 * @return
	 */
	protected IParseNode getActiveNode(ITextViewer textViewer, int offset)
	{
		IParseNode result = null;

		if (this.isHoverEnabled())
		{
			AbstractThemeableEditor editor = this.getEditor(textViewer);
			IParseNode ast = editor.getAST();

			if (ast != null)
			{
				result = ast.getNodeAtOffset(offset);

				// We won't get a current node if the cursor is outside of the positions
				// recorded by the AST
				if (result == null)
				{
					if (offset < ast.getStartingOffset())
					{
						result = ast.getNodeAtOffset(ast.getStartingOffset());
					}
					else if (ast.getEndingOffset() < offset)
					{
						result = ast.getNodeAtOffset(ast.getEndingOffset());
					}
				}
			}
		}

		return result;
	}

	/**
	 * getEditor
	 * 
	 * @param textViewer
	 * @return
	 */
	protected AbstractThemeableEditor getEditor(ITextViewer textViewer)
	{
		AbstractThemeableEditor result = null;

		if (textViewer instanceof IAdaptable)
		{
			result = (AbstractThemeableEditor) ((IAdaptable) textViewer).getAdapter(AbstractThemeableEditor.class);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHoverExtension2#getHoverInfo2(org.eclipse.jface.text.ITextViewer,
	 * org.eclipse.jface.text.IRegion)
	 */
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion)
	{
		try
		{
			IParseNode activeNode = getActiveNode(textViewer, hoverRegion.getOffset());
			if (activeNode == null)
			{
				return null;
			}

			// To avoid duplicating work, we generate the header and documentation together here
			// and then getHeader and getDocumentation just return the values.
			JSLocationIdentifier identifier = new JSLocationIdentifier(hoverRegion.getOffset(), activeNode);
			LocationType type = identifier.getType();
			IEditorPart editorPart = getEditor();

			switch (type)
			{
				case IN_CONSTRUCTOR:
				case IN_GLOBAL:
				case IN_VARIABLE_NAME:
				{
					JSIndexQueryHelper queryHelper = new JSIndexQueryHelper();
					final Index index = getIndex(editorPart);
					List<PropertyElement> properties = queryHelper.getGlobals(index, activeNode.getText());
					if (!CollectionsUtil.isEmpty(properties))
					{
						PropertyElement first = properties.get(0);
						fHeader = MessageFormat.format(PROPERTY_HEADER_TEMPLATE, first.getName(),
								first.getOwningType(), getIndexRelativePaths(index, properties));
						fDocs = getDocumentation(properties);
					}
					break;
				}

				case IN_PROPERTY_NAME:
					JSIndexQueryHelper queryHelper = new JSIndexQueryHelper();
					final Index index = this.getIndex(editorPart);
					JSGetPropertyNode propertyNode = ParseUtil.getGetPropertyNode(identifier.getTargetNode(),
							identifier.getStatementNode());

					List<String> types = ParseUtil.getParentObjectTypes(index, this.getEditorURI(editorPart),
							identifier.getTargetNode(), propertyNode, hoverRegion.getOffset());
					String typeName = null;
					String methodName = null;

					if (!CollectionsUtil.isEmpty(types))
					{
						typeName = types.get(0);
						methodName = propertyNode.getLastChild().getText();
					}

					if (typeName != null && methodName != null)
					{
						List<PropertyElement> properties = queryHelper.getTypeMembers(index, typeName, methodName);
						// filter to only functions
						properties = CollectionsUtil.filter(properties, new IFilter<PropertyElement>()
						{
							public boolean include(PropertyElement item)
							{
								return (item instanceof FunctionElement);
							}
						});

						if (!CollectionsUtil.isEmpty(properties))
						{
							FunctionElement first = (FunctionElement) properties.get(0);

							fHeader = MessageFormat.format(FUNCTION_HEADER_TEMPLATE, methodName,
									parameters(first.getParameters()), typeName,
									getIndexRelativePaths(index, properties));
							fDocs = getDocumentation(properties);
						}
					}
					break;

				case IN_OBJECT_LITERAL_PROPERTY:
				case IN_PARAMETERS:
				case IN_LABEL:
				case UNKNOWN:
				case NONE:
				default:
					return null;
			}
			return getHoverInfo(activeNode, isBrowserControlAvailable(textViewer), null, editorPart, hoverRegion);
		}
		finally
		{
			fHeader = null;
			fDocs = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.AbstractDocumentationHover#getHeader(java.lang.Object,
	 * org.eclipse.ui.IEditorPart, org.eclipse.jface.text.IRegion)
	 */
	@Override
	protected String getHeader(Object element, IEditorPart editorPart, IRegion hoverRegion)
	{
		return fHeader;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.AbstractDocumentationHover#getDocumentation(java.lang.Object,
	 * org.eclipse.ui.IEditorPart, org.eclipse.jface.text.IRegion)
	 */
	@Override
	protected String getDocumentation(Object element, IEditorPart editorPart, IRegion hoverRegion)
	{
		return fDocs;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.AbstractDocumentationHover#populateToolbarActions(org.eclipse.jface.action.
	 * ToolBarManager, com.aptana.editor.common.hover.CustomBrowserInformationControl)
	 */
	@Override
	public void populateToolbarActions(ToolBarManager tbm, CustomBrowserInformationControl iControl)
	{
		final OpenDeclarationAction openDeclarationAction = new OpenDeclarationAction(iControl);
		tbm.add(openDeclarationAction);
		IInputChangedListener inputChangeListener = new IInputChangedListener()
		{
			public void inputChanged(Object newInput)
			{
				if (newInput instanceof BrowserInformationControlInput)
				{
					openDeclarationAction.update();
				}
			}
		};
		iControl.addInputChangeListener(inputChangeListener);
	}

	private String getDocumentation(List<PropertyElement> properties)
	{
		Set<UserAgentElement> userAgents = new HashSet<UserAgentElement>();
		Set<SinceElement> sinceElements = new HashSet<SinceElement>();
		Set<String> descriptions = new HashSet<String>();
		String example = StringUtil.EMPTY;

		for (PropertyElement property : properties)
		{
			userAgents.addAll(property.getUserAgents());
			String desc = property.getDescription();
			if (!StringUtil.isEmpty(desc))
			{
				descriptions.add(desc);
			}
			sinceElements.addAll(property.getSinceList());
			if (StringUtil.isEmpty(example) && !CollectionsUtil.isEmpty(property.getExamples()))
			{
				example = property.getExamples().get(0);
			}
		}

		StringBuilder builder = new StringBuilder();
		String description = Messages.JSTextHover_NoDescription;
		if (!CollectionsUtil.isEmpty(descriptions))
		{
			description = StringUtil.join(", ", descriptions); //$NON-NLS-1$
		}
		builder.append(description);
		addSection(builder, Messages.JSTextHover_SupportedPlatforms, getPlatforms(userAgents));
		addSection(builder, Messages.JSTextHover_Example, example);
		addSection(builder, Messages.JSTextHover_Specification, getSpecificationsString(sinceElements));
		return builder.toString();
	}

	private void addSection(StringBuilder builder, String title, String value)
	{
		if (!StringUtil.isEmpty(value))
		{
			builder.append("<br /><br />"); //$NON-NLS-1$
			builder.append("<b>").append(title).append("</b><br />"); //$NON-NLS-1$ //$NON-NLS-2$
			builder.append(value);
		}
	}

	private String parameters(List<ParameterElement> parameters)
	{
		List<String> strings = CollectionsUtil.map(parameters, new IMap<ParameterElement, String>()
		{
			public String map(ParameterElement item)
			{
				StringBuilder b = new StringBuilder();
				b.append(item.getName());
				List<String> types = item.getTypes();
				if (!CollectionsUtil.isEmpty(types))
				{
					b.append(": ").append(StringUtil.join("|", types)); //$NON-NLS-1$ //$NON-NLS-2$
				}
				return b.toString();
			}
		});
		return StringUtil.join(", ", strings); //$NON-NLS-1$
	}

	private String getSpecificationsString(Collection<SinceElement> sinceElements)
	{
		List<String> strings = CollectionsUtil.map(sinceElements, new IMap<SinceElement, String>()
		{
			public String map(SinceElement item)
			{
				StringBuilder b = new StringBuilder();
				b.append(item.getName());
				String version = item.getVersion();
				if (!StringUtil.isEmpty(version))
				{
					b.append(": ").append(version); //$NON-NLS-1$
				}
				return b.toString();
			}
		});
		return StringUtil.join(", ", strings); //$NON-NLS-1$
	}

	private String getPlatforms(Collection<UserAgentElement> userAgents)
	{
		List<String> strings = CollectionsUtil.map(userAgents, new IMap<UserAgentElement, String>()
		{
			public String map(UserAgentElement item)
			{
				StringBuilder b = new StringBuilder();
				b.append(item.getPlatform());
				String version = item.getVersion();
				if (!StringUtil.isEmpty(version))
				{
					b.append(": ").append(version); //$NON-NLS-1$
				}
				return b.toString();
			}
		});
		return StringUtil.join(", ", strings); //$NON-NLS-1$
	}

	/**
	 * Given a Collection of propertyElements, we generate the unique set of documents and truncate the paths to use the
	 * relative path to the index.
	 * 
	 * @param index
	 * @param properties
	 * @return
	 */
	private String getIndexRelativePaths(final Index index, Collection<PropertyElement> properties)
	{
		Collection<String> documents = new HashSet<String>();
		for (PropertyElement property : properties)
		{
			documents.addAll(property.getDocuments());
		}
		documents = CollectionsUtil.map(documents, new IMap<String, String>()
		{
			public String map(String item)
			{
				try
				{
					return index.getRelativeDocumentPath(new URI(item)).getPath();
				}
				catch (URISyntaxException e)
				{
					return item;
				}
			}
		});

		return StringUtil.join(", ", documents); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset)
	{
		IParseNode activeNode = this.getActiveNode(textViewer, offset);
		IRegion result = null;

		if (activeNode != null)
		{
			JSLocationIdentifier identifier = new JSLocationIdentifier(offset, activeNode);
			LocationType type = identifier.getType();

			switch (type)
			{
				case UNKNOWN:
				case NONE:
					break;

				default:
					IParseNode targetNode = identifier.getTargetNode();

					if (targetNode != null)
					{
						result = new Region(targetNode.getStartingOffset(), targetNode.getLength());
					}
			}
		}

		if (result == null)
		{
			result = new Region(offset, 0);
		}

		return result;
	}

	/**
	 * getEditorURI
	 * 
	 * @param textViewer
	 * @return
	 */
	protected URI getEditorURI(IEditorPart editorPart)
	{
		AbstractThemeableEditor editor = (AbstractThemeableEditor) editorPart;
		URI result = null;

		if (editor != null)
		{
			IEditorInput editorInput = editor.getEditorInput();

			if (editorInput instanceof IURIEditorInput)
			{
				IURIEditorInput fileEditorInput = (IURIEditorInput) editorInput;

				result = fileEditorInput.getURI();
			}
		}

		return result;
	}

	/**
	 * getIndex
	 * 
	 * @param editorPart
	 * @return
	 */
	protected Index getIndex(IEditorPart editorPart)
	{
		AbstractThemeableEditor editor = (AbstractThemeableEditor) editorPart;
		Index result = null;

		if (editor != null)
		{
			IEditorInput input = editor.getEditorInput();

			if (input instanceof IFileEditorInput)
			{
				IFile file = ((IFileEditorInput) input).getFile();
				IProject project = file.getProject();

				result = IndexManager.getInstance().getIndex(project.getLocationURI());
			}
		}

		return result;
	}

	/**
	 * Open declaration action.
	 */
	public class OpenDeclarationAction extends Action
	{
		private static final String IMG_OPEN_DECLARATION = "icons/full/elcl16/goto_input.gif"; //$NON-NLS-1$
		private static final String IMG_OPEN_DECLARATION_DISABLED = "icons/full/dlcl16/goto_input.gif"; //$NON-NLS-1$
		private CustomBrowserInformationControl iControl;
		private IHyperlink[] hyperlinks;

		/**
		 * @param iControl
		 */
		public OpenDeclarationAction(CustomBrowserInformationControl iControl)
		{
			setText(Messages.JSTextHover_openDeclarationTooltip);
			setImageDescriptor(UIEplPlugin.imageDescriptorFromPlugin(UIEplPlugin.PLUGIN_ID, IMG_OPEN_DECLARATION));
			setDisabledImageDescriptor(UIEplPlugin.imageDescriptorFromPlugin(UIEplPlugin.PLUGIN_ID,
					IMG_OPEN_DECLARATION_DISABLED));
			this.iControl = iControl;
		}

		/**
		 * Update the action
		 */
		void update()
		{
			BrowserInformationControlInput input = iControl.getInput();
			if (input instanceof DocumentationBrowserInformationControlInput)
			{
				JSHyperlinkDetector detector = new JSHyperlinkDetector();
				IRegion hoverRegion = ((DocumentationBrowserInformationControlInput) input).getHoverRegion();
				if (hoverRegion != null)
				{
					hyperlinks = detector.detectHyperlinks((AbstractThemeableEditor) getEditor(), hoverRegion, false);
					setEnabled(hyperlinks != null && hyperlinks.length > 0 && hyperlinks[0] != null);
					return;
				}

			}
			setEnabled(false);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#run()
		 */
		@Override
		public void run()
		{
			// We already know that this hyperlink is valid. A check was made at the update call.
			iControl.dispose();
			hyperlinks[0].open();
		}
	}
}
