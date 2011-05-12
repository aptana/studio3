/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.hovers;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;

import com.aptana.js.debug.core.IJSDebugConstants;
import com.aptana.js.debug.core.model.IJSStackFrame;

/**
 * @author Max Stepanov
 */
public class JSDebugHover implements ITextHover, ITextHoverExtension2 {
	
	private static IDebugModelPresentation modelPresentation;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		Object info = getHoverInfo2(textViewer, hoverRegion);
		return info != null ? info.toString() : null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return null; // JavaWordFinder.findWord(textViewer.getDocument(), offset);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHoverExtension2#getHoverInfo2(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
	 */
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		IJSStackFrame frame = getFrame();
		if (frame != null) {
			IDocument document = textViewer.getDocument();
			if (document != null) {
				try {
					String variableName = document.get(hoverRegion.getOffset(), hoverRegion.getLength());
					try {
						IVariable variable = frame.findVariable(variableName);
						if (variable != null) {
							return getVariableText(variable);
						}
					} catch (DebugException e) {
						return null;
					}
				} catch (BadLocationException e) {
					return null;
				}
			}
		}
		return null;
	}

	private IJSStackFrame getFrame() {
		IAdaptable adaptable = DebugUITools.getDebugContext();
		if (adaptable != null) {
			return (IJSStackFrame) adaptable.getAdapter(IJSStackFrame.class);
		}
		return null;
	}

	/*
	 * Returns HTML text for the given variable
	 */
	private String getVariableText(IVariable variable) {
		StringBuilder sb = new StringBuilder();
		IDebugModelPresentation modelPresentation = getModelPresentation();
		sb.append("<p><pre>"); //$NON-NLS-1$
		String variableText = modelPresentation.getText(variable);
		sb.append(replaceHTMLChars(variableText));
		sb.append("</pre></p>"); //$NON-NLS-1$
		if (sb.length() > 0) {
			return sb.toString();
		}
		return null;
	}

	/*
	 * Replaces reserved HTML characters in the given string with their escaped
	 * equivalents. This is to ensure that variable values containing reserved
	 * characters are correctly displayed.
	 */
	private static String replaceHTMLChars(String variableText) {
		StringBuilder sb = new StringBuilder(variableText.length());
		char[] characters = variableText.toCharArray();
		for (int i = 0; i < characters.length; i++) {
			char character = characters[i];
			switch (character) {
			case '<':
				sb.append("&lt;"); //$NON-NLS-1$
				break;
			case '>':
				sb.append("&gt;"); //$NON-NLS-1$
				break;
			case '&':
				sb.append("&amp;"); //$NON-NLS-1$
				break;
			case '"':
				sb.append("&quot;"); //$NON-NLS-1$
				break;
			default:
				sb.append(character);
			}
		}
		return sb.toString();
	}

	private static IDebugModelPresentation getModelPresentation() {
		if (modelPresentation == null) {
			modelPresentation = DebugUITools.newDebugModelPresentation(IJSDebugConstants.ID_DEBUG_MODEL);
			modelPresentation.setAttribute(IDebugModelPresentation.DISPLAY_VARIABLE_TYPE_NAMES, Boolean.TRUE);
		}
		return modelPresentation;
	}

}
