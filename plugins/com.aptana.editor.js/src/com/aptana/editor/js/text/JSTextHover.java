/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text;

import java.net.URI;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.contentassist.CommonTextHover;
import com.aptana.editor.js.contentassist.JSIndexQueryHelper;
import com.aptana.editor.js.contentassist.JSLocationIdentifier;
import com.aptana.editor.js.contentassist.LocationType;
import com.aptana.editor.js.contentassist.ParseUtil;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.parsing.ast.JSGetPropertyNode;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.parsing.ast.IParseNode;

public class JSTextHover extends CommonTextHover implements ITextHover, ITextHoverExtension2
{
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
	 * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer,
	 * org.eclipse.jface.text.IRegion)
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion)
	{
		// Not called
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHoverExtension2#getHoverInfo2(org.eclipse.jface.text.ITextViewer,
	 * org.eclipse.jface.text.IRegion)
	 */
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion)
	{
		int offset = hoverRegion.getOffset();
		Object result = null;

		IParseNode activeNode = this.getActiveNode(textViewer, offset);

		if (activeNode != null)
		{
			JSLocationIdentifier identifier = new JSLocationIdentifier(offset, activeNode);
			LocationType type = identifier.getType();

			switch (type)
			{
				case IN_CONSTRUCTOR:
				case IN_GLOBAL:
				case IN_VARIABLE_NAME:
				{
					JSIndexQueryHelper queryHelper = new JSIndexQueryHelper();
					Index index = this.getIndex(textViewer);
					PropertyElement property = queryHelper.getGlobal(index, activeNode.getText());

					if (property != null)
					{
						result = property.getDescription();
					}
					break;
				}

				case IN_PROPERTY_NAME:
					JSIndexQueryHelper queryHelper = new JSIndexQueryHelper();
					Index index = this.getIndex(textViewer);
					// @formatter:off
					JSGetPropertyNode propertyNode = ParseUtil.getGetPropertyNode(
						identifier.getTargetNode(),
						identifier.getStatementNode()
					);
					// @formatter:on
					List<String> types = ParseUtil.getParentObjectTypes(index, this.getEditorURI(textViewer),
							identifier.getTargetNode(), propertyNode, offset);
					String typeName = null;
					String methodName = null;

					if (types.size() > 0)
					{
						typeName = types.get(0);
						methodName = propertyNode.getLastChild().getText();
					}

					if (typeName != null && methodName != null)
					{
						PropertyElement property = queryHelper.getTypeMember(index, typeName, methodName);

						if (property instanceof FunctionElement)
						{
							result = ((FunctionElement) property).getDescription();
						}
					}
					break;

				case IN_OBJECT_LITERAL_PROPERTY:
					break;

				case IN_PARAMETERS:
					break;

				case IN_LABEL:
				case UNKNOWN:
				case NONE:
				default:
					break;
			}
		}

		return result;
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
	protected URI getEditorURI(ITextViewer textViewer)
	{
		AbstractThemeableEditor editor = this.getEditor(textViewer);
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
	 * @param textViewer
	 * @return
	 */
	protected Index getIndex(ITextViewer textViewer)
	{
		AbstractThemeableEditor editor = this.getEditor(textViewer);
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
}
