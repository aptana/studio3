/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.preview.ui.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionInfo;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.AbstractEvaluationHandler;
import org.eclipse.ui.internal.InternalHandlerUtil;

import com.aptana.preview.PreviewManager;

/**
 * @author Max Stepanov
 * 
 */
@SuppressWarnings("restriction")
public class ShowPreviewHandler extends AbstractEvaluationHandler {

	private Expression enabledWhen;
	
	public ShowPreviewHandler() {
		registerEnablement();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editorPart = null;
		if (workbenchPage != null) {
			editorPart = workbenchPage.getActiveEditor();
		}
		if (editorPart != null) {
			PreviewManager.getInstance().openPreviewForEditor(editorPart);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.AbstractEvaluationHandler#getEnabledWhenExpression()
	 */
	@Override
	protected Expression getEnabledWhenExpression() {
		if (enabledWhen == null) {
			enabledWhen = new Expression() {
				public EvaluationResult evaluate(IEvaluationContext context) throws CoreException {
					IEditorPart editorPart = InternalHandlerUtil.getActiveEditor(context);
					if (editorPart != null) {
						if (PreviewManager.getInstance().testEditorInputForPreview(editorPart.getEditorInput())) {
							return EvaluationResult.TRUE;
						}
					}
					return EvaluationResult.FALSE;
				}

				@Override
				public void collectExpressionInfo(ExpressionInfo info) {
					info.addVariableNameAccess(ISources.ACTIVE_EDITOR_NAME);
				}
			};
		}
		return enabledWhen;
	}

}
