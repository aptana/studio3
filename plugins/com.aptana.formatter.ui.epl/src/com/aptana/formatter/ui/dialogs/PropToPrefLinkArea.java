/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.formatter.ui.dialogs;

import java.text.MessageFormat;
import java.util.Iterator;

import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.internal.WorkbenchMessages;

/**
 * Creates a link between a property page and preference page
 */
@SuppressWarnings("restriction")
public class PropToPrefLinkArea
{

	private Link pageLink;

	public PropToPrefLinkArea(Composite parent, int style, final String pageId, String message, final Shell shell,
			final Object pageData)
	{
		/*
		 * breaking new ground yet again - want to link between property and preference paes. ie: project specific debug
		 * engine options to general debugging options
		 */
		pageLink = new Link(parent, style);

		IPreferenceNode node = getPreferenceNode(pageId);
		String result;
		if (node == null)
		{
			result = NLS.bind(WorkbenchMessages.PreferenceNode_NotFound, pageId);
		}
		else
		{
			result = MessageFormat.format(message, node.getLabelText());

			// only add the selection listener if the node is found
			pageLink.addSelectionListener(new SelectionAdapter()
			{

				public void widgetSelected(SelectionEvent e)
				{
					PreferencesUtil.createPreferenceDialogOn(shell, pageId, new String[] { pageId }, pageData).open();
				}

			});
		}
		pageLink.setText(result);

	}

	/**
	 * Returns the property link control
	 */
	public Control getControl()
	{
		return pageLink;
	}

	@SuppressWarnings("rawtypes")
	private IPreferenceNode getPreferenceNode(String pageId)
	{
		Iterator iterator = PlatformUI.getWorkbench().getPreferenceManager().getElements(PreferenceManager.PRE_ORDER)
				.iterator();
		while (iterator.hasNext())
		{
			IPreferenceNode next = (IPreferenceNode) iterator.next();
			if (next.getId().equals(pageId))
			{
				return next;
			}
		}
		return null;
	}
}
