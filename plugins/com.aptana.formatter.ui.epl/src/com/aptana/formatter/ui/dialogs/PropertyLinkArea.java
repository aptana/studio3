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

import java.util.Iterator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.dialogs.PropertyPageContributorManager;
import org.eclipse.ui.internal.dialogs.PropertyPageManager;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

@SuppressWarnings("restriction")
public class PropertyLinkArea
{

	private Link pageLink;
	private IAdaptable element;

	public PropertyLinkArea(Composite parent, int style, final String pageId, IAdaptable element, String message,
			final IWorkbenchPreferenceContainer pageContainer)
	{
		this.element = element;
		pageLink = new Link(parent, style);

		IPreferenceNode node = getPreferenceNode(pageId);
		String text = null;
		if (node == null)
		{
			text = NLS.bind(WorkbenchMessages.PreferenceNode_NotFound, pageId);
		}
		else
		{
			text = NLS.bind(message, node.getLabelText());
		}

		pageLink.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				pageContainer.openPage(pageId, null);
			}
		});

		pageLink.setText(text);
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
		/*
		 * code pulled from org.eclipse.ui.internal.dialogs.PropertyDialog - i'm not sure why this type of class doesn't
		 * already exist for property pages like it does for preference pages since it seems it would be very useful -
		 * guess we're breaking new ground :)
		 */
		PropertyPageManager pageManager = new PropertyPageManager();
		PropertyPageContributorManager.getManager().contribute(pageManager, element);

		Iterator pages = pageManager.getElements(PreferenceManager.PRE_ORDER).iterator();

		while (pages.hasNext())
		{
			IPreferenceNode node = (IPreferenceNode) pages.next();
			if (node.getId().equals(pageId))
			{
				return node;
			}
		}

		return null;
	}
}
