/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
