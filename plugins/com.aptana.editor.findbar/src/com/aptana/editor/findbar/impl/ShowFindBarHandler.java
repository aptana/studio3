/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar.impl;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.ui.ISources;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.findbar.api.IFindBarDecorated;


/**
 * This takes care of the case when the active textEdior is an instance of <code>IFindBarDecorated</code> or
 * does adapt to <code>IFindBarDecorated</code>.
 * 
 * @author schitale
 *
 */
public class ShowFindBarHandler extends AbstractHandler {
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Object applicationContext = event.getApplicationContext();
		if (applicationContext instanceof IEvaluationContext) {
			IEvaluationContext evaluationContext = (IEvaluationContext) applicationContext;
			Object variable = evaluationContext.getVariable(ISources.ACTIVE_EDITOR_NAME);
			if (variable instanceof ITextEditor) {
				ITextEditor textEditor = (ITextEditor) variable;
				IFindBarDecorated findBarDecorated = null;
				if (textEditor instanceof IFindBarDecorated) {
					findBarDecorated = (IFindBarDecorated) textEditor;
				} else {
					findBarDecorated = (IFindBarDecorated) textEditor.getAdapter(IFindBarDecorated.class);
				}
				if (findBarDecorated != null) {
					findBarDecorated.getFindBarDecorator().setVisible(true);
				} else {
					IHandlerService handlerService = (IHandlerService) textEditor.getSite().getService(IHandlerService.class);
					try {
						handlerService.executeCommand(ActionFactory.FIND.create(textEditor.getSite().getWorkbenchWindow()).getActionDefinitionId(), null);
					} catch (NotDefinedException e) {
					} catch (NotEnabledException e) {
					} catch (NotHandledException e) {
					}
				}
			}
		}
		return null;
	}
}
