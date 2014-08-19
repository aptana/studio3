/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.epl.UIEplPlugin;

/**
 * The generic root preferences page is designed to create simple preferences links that represents all the pages that
 * were registered under that page and displayed in the preferences tree as its children. This page is useful where
 * there is not much content to put into a root node in the preferences tree, but one still want to display something
 * and not leave it empty.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public abstract class GenericRootPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	protected static HashMap<String, String> pageNameToId;
	protected static final String DEFAULT_MESSAGE = EplMessages.GenericRootPage_genericPerferencesPageMessage;

	/**
	 * Constructs a new GenericRootPreferencePage.
	 */
	public GenericRootPreferencePage()
	{
		noDefaultAndApplyButton();
	}

	/**
	 * Creates the links.
	 */
	@SuppressWarnings({ "unchecked" })
	protected Control createContents(Composite parent)
	{
		// pageNameToId = null
		if (pageNameToId == null)
		{
			pageNameToId = new HashMap<String, String>();
			String pageId = getPageId();
			// Locate all the pages that are defined as this page children
			PreferenceManager manager = PlatformUI.getWorkbench().getPreferenceManager();
			List<IPreferenceNode> nodes = manager.getElements(PreferenceManager.POST_ORDER);
			for (Iterator<IPreferenceNode> i = nodes.iterator(); i.hasNext();)
			{
				IPreferenceNode node = i.next();
				if (node.getId().equals(pageId))
				{
					// we found the node, so take its child nodes and add them to the cache
					IPreferenceNode[] subNodes = node.getSubNodes();
					for (IPreferenceNode child : subNodes)
					{
						pageNameToId.put(child.getLabelText(), child.getId());
					}
					break;
				}
			}
		}
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		String contentsMessage = getContentsMessage();
		if (contentsMessage != null)
		{
			Label message = new Label(composite, SWT.WRAP);
			message.setText(contentsMessage);
		}
		Group group = new Group(composite, SWT.NONE);
		group.setLayout(new GridLayout(1, false));
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);
		group.setText(EplMessages.GenericRootPage_preferences);
		List<String> pagesNames = new ArrayList<String>(pageNameToId.keySet());
		// In case there are no pages to link to, add a label that will indicate that there are no settings
		if (pagesNames.isEmpty())
		{
			Label label = new Label(group, SWT.NONE);
			label.setText(EplMessages.GenericRootPage_noAvailablePages);
			label.setLayoutData(new GridData());
		}
		else
		{
			Collections.sort(pagesNames);
			for (String pageName : pagesNames)
			{
				String id = pageNameToId.get(pageName);
				final Link link = new Link(group, SWT.NONE);
				link.setText("<a>" + pageName + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
				link.addSelectionListener(new LinkSelectionListener(id));
				gd = new GridData();
				gd.horizontalIndent = 30;
				link.setLayoutData(gd);
			}
		}

		Group dialogsResetGroup = new Group(composite, SWT.NONE);
		dialogsResetGroup.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
		dialogsResetGroup.setLayoutData(GridDataFactory.fillDefaults().create());
		dialogsResetGroup.setText(EplMessages.GenericRootPreferencePage_dialogsGroup);

		Label label = new Label(dialogsResetGroup, SWT.WRAP);
		label.setText(EplMessages.GenericRootPreferencePage_clearMessagesLabelText);
		label.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER)
				.hint(convertVerticalDLUsToPixels(50), SWT.DEFAULT).create());

		final Button clearBt = new Button(dialogsResetGroup, SWT.PUSH);
		clearBt.setText(EplMessages.GenericRootPreferencePage_clearMessagesButtonLabel);
		clearBt.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false));
		// enable the 'reset' button only if there are dialogs to reset.
		final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(UIEplPlugin.PLUGIN_ID);
		String messages = prefs.get(IEplPreferenceConstants.HIDDEN_MESSAGES, null);
		clearBt.setEnabled(!StringUtil.isEmpty(messages));
		clearBt.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				try
				{
					prefs.remove(IEplPreferenceConstants.HIDDEN_MESSAGES);
					prefs.flush();
					clearBt.setEnabled(false);
				}
				catch (Exception ex)
				{
					IdeLog.logError(UIEplPlugin.getDefault(), ex);
				}
			}
		});

		Point point = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		gd = new GridData();
		composite.setLayoutData(gd);
		gd.heightHint = point.y;
		return composite;
	}

	public void init(IWorkbench workbench)
	{
		// Do nothing here
	}

	/**
	 * Returns the page id.
	 * 
	 * @return The page id, as defined in its contribution definition.
	 */
	protected abstract String getPageId();

	/**
	 * Returns the contents message that will appear on top of the preferences pages links. In case <code>null</code> is
	 * returned, no message will appear.
	 * 
	 * @return The message to display above the preferences links group (or null)
	 * @see GenericRootPreferencePage#DEFAULT_MESSAGE
	 */
	protected String getContentsMessage()
	{
		return DEFAULT_MESSAGE;
	}

	private class LinkSelectionListener extends SelectionAdapter
	{
		private String pageId;

		public LinkSelectionListener(String pageId)
		{
			this.pageId = pageId;
		}

		public void widgetSelected(SelectionEvent e)
		{
			((IWorkbenchPreferenceContainer) getContainer()).openPage(pageId, null);
		}
	}
}
