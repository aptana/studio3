/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.IUpdate;

import com.aptana.core.logging.IdeLog;
import com.aptana.js.debug.core.IJSDebugConstants;
import com.aptana.js.debug.ui.JSDebugUIPlugin;

/**
 * @author Max Stepanov
 */
public abstract class AbstractBreakpointRulerAction extends Action implements IUpdate {
	private IVerticalRulerInfo fInfo;
	private ITextEditor fTextEditor;
	private IBreakpoint fBreakpoint;

	/**
	 * determineBreakpoint
	 * 
	 * @return IBreakpoint
	 */
	protected IBreakpoint determineBreakpoint() {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager()
				.getBreakpoints(IJSDebugConstants.ID_DEBUG_MODEL);
		/*
		 * IBreakpoint[] phpBreakpoints = DebugPlugin.getDefault().getBreakpointManager()
		 * .getBreakpoints(IJSDebugConstants.PHP_DEBUG_MODEL); ArrayList<IBreakpoint> allBreakPoints = new
		 * ArrayList<IBreakpoint>(); allBreakPoints.addAll(Arrays.asList(breakpoints));
		 * allBreakPoints.addAll(Arrays.asList(phpBreakpoints)); breakpoints = new IBreakpoint[allBreakPoints.size()];
		 * allBreakPoints.toArray(breakpoints);
		 */
		for (IBreakpoint breakpoint : breakpoints) {
			if (breakpoint instanceof ILineBreakpoint) {
				ILineBreakpoint jBreakpoint = (ILineBreakpoint) breakpoint;
				try {
					if (breakpointAtRulerLine(jBreakpoint)) {
						return jBreakpoint;
					}
				} catch (CoreException ce) {
					IdeLog.logError(JSDebugUIPlugin.getDefault(), ce);
					continue;
				}
			}
		}
		return null;
	}

	/**
	 * getInfo
	 * 
	 * @return IVerticalRulerInfo
	 */
	protected IVerticalRulerInfo getInfo() {
		return fInfo;
	}

	/**
	 * setInfo
	 * 
	 * @param info
	 */
	protected void setInfo(IVerticalRulerInfo info) {
		fInfo = info;
	}

	/**
	 * getTextEditor
	 * 
	 * @return ITextEditor
	 */
	protected ITextEditor getTextEditor() {
		return fTextEditor;
	}

	/**
	 * setTextEditor
	 * 
	 * @param textEditor
	 */
	protected void setTextEditor(ITextEditor textEditor) {
		fTextEditor = textEditor;
	}

	/**
	 * Returns the resource for which to create the marker, or <code>null</code> if there is no applicable resource.
	 * 
	 * @return the resource for which to create the marker or <code>null</code>
	 */
	protected IResource getResource() {
		IEditorInput input = fTextEditor.getEditorInput();
		IResource resource = (IResource) input.getAdapter(IFile.class);
		if (resource == null) {
			resource = (IResource) input.getAdapter(IResource.class);
		}
		return resource;
	}

	/**
	 * breakpointAtRulerLine
	 * 
	 * @param jBreakpoint
	 * @return boolean
	 * @throws CoreException
	 */
	protected boolean breakpointAtRulerLine(ILineBreakpoint jBreakpoint) throws CoreException {
		AbstractMarkerAnnotationModel model = getAnnotationModel();
		if (model != null) {
			Position position = model.getMarkerPosition(jBreakpoint.getMarker());
			if (position != null) {
				IDocumentProvider provider = getTextEditor().getDocumentProvider();
				IDocument doc = provider.getDocument(getTextEditor().getEditorInput());
				try {
					int markerLineNumber = doc.getLineOfOffset(position.getOffset());
					int rulerLine = getInfo().getLineOfLastMouseButtonActivity();
					if (rulerLine == markerLineNumber) {
						if (getTextEditor().isDirty()) {
							return jBreakpoint.getLineNumber() == markerLineNumber + 1;
						}
						return true;
					}
				} catch (BadLocationException ignore) {
					ignore.getCause();
				}
			}
		}

		return false;
	}

	/**
	 * getBreakpoint
	 * 
	 * @return IBreakpoint
	 */
	protected IBreakpoint getBreakpoint() {
		return fBreakpoint;
	}

	/**
	 * setBreakpoint
	 * 
	 * @param breakpoint
	 */
	protected void setBreakpoint(IBreakpoint breakpoint) {
		fBreakpoint = breakpoint;
	}

	/**
	 * Returns the <code>AbstractMarkerAnnotationModel</code> of the editor's input.
	 * 
	 * @return the marker annotation model
	 */
	protected AbstractMarkerAnnotationModel getAnnotationModel() {
		IDocumentProvider provider = fTextEditor.getDocumentProvider();
		IAnnotationModel model = provider.getAnnotationModel(getTextEditor().getEditorInput());
		if (model instanceof AbstractMarkerAnnotationModel) {
			return (AbstractMarkerAnnotationModel) model;
		}
		return null;
	}
}
