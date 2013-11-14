/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.console;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.console.TextConsolePage;
import org.eclipse.ui.console.TextConsoleViewer;
import org.eclipse.ui.internal.console.IOConsoleViewer;
import org.eclipse.ui.internal.console.ScrollLockAction;
import org.eclipse.ui.part.IPageBookViewPage;

/**
 * @author Shalom
 */
@SuppressWarnings("restriction")
public class ConsoleAutoScrollPageParticipant extends PlatformObject implements IConsolePageParticipant
{
	private boolean scrollActionEnabled;
	private StyledText textWidget;
	private Listener listener;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.console.IConsolePageParticipant#init(org.eclipse.ui.part.IPageBookViewPage,
	 * org.eclipse.ui.console.IConsole)
	 */
	public void init(IPageBookViewPage page, IConsole console)
	{
		if (console.getType() != IDebugUIConstants.ID_PROCESS_CONSOLE_TYPE || !(page instanceof TextConsolePage))
		{
			return;
		}
		TextConsolePage consolePage = (TextConsolePage) page;
		TextConsoleViewer textViewer = consolePage.getViewer();
		if (!(textViewer instanceof IOConsoleViewer))
		{
			return;
		}
		final IOConsoleViewer viewer = (IOConsoleViewer) textViewer;
		scrollActionEnabled = viewer.isAutoScroll();
		final IToolBarManager toolBarManager = consolePage.getSite().getActionBars().getToolBarManager();
		IAction slAction = null;
		// Look for the ScrollLockAction
		for (IContributionItem item : toolBarManager.getItems())
		{
			if (item instanceof ActionContributionItem)
			{
				IAction action = ((ActionContributionItem) item).getAction();
				if (action instanceof ScrollLockAction)
				{
					slAction = action;
					break;
				}
			}
		}
		textWidget = viewer.getTextWidget();
		listener = new ConsoleListener(viewer, toolBarManager, slAction);

		// Based on Eclipse Snippet191 - Detects scrolling that were initiated by the user.
		textWidget.addListener(SWT.MouseDown, listener);
		textWidget.addListener(SWT.MouseMove, listener);
		textWidget.addListener(SWT.MouseUp, listener);
		textWidget.addListener(SWT.KeyDown, listener);
		textWidget.addListener(SWT.KeyUp, listener);
		ScrollBar vBar = textWidget.getVerticalBar();
		if (vBar != null)
		{
			vBar.addListener(SWT.Selection, listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.console.IConsolePageParticipant#dispose()
	 */
	public void dispose()
	{
		if (textWidget != null && !textWidget.isDisposed())
		{
			textWidget.removeListener(SWT.MouseDown, listener);
			textWidget.removeListener(SWT.MouseMove, listener);
			textWidget.removeListener(SWT.MouseUp, listener);
			textWidget.removeListener(SWT.KeyDown, listener);
			textWidget.removeListener(SWT.KeyUp, listener);
			textWidget.removeListener(SWT.Resize, listener);
			ScrollBar vBar = textWidget.getVerticalBar();
			if (vBar != null && !vBar.isDisposed())
			{
				vBar.removeListener(SWT.Selection, listener);
			}
		}
		textWidget = null;
		listener = null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.console.IConsolePageParticipant#activated()
	 */
	public void activated()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.console.IConsolePageParticipant#deactivated()
	 */
	public void deactivated()
	{
	}

	private boolean isLastLineVisible(int topLineIndex)
	{
		int visibleLines = textWidget.getBounds().height / textWidget.getLineHeight();
		return (textWidget.getLineCount() - topLineIndex <= visibleLines);
	}

	/**
	 * Console listener.
	 */
	private final class ConsoleListener implements Listener
	{
		private IOConsoleViewer viewer;
		private IToolBarManager toolBarManager;
		private IAction scrollLockAction;
		int lastIndex = textWidget.getTopIndex();

		/**
		 * @param viewer
		 * @param toolBarManager
		 * @param scrollLockAction
		 */
		private ConsoleListener(IOConsoleViewer viewer, IToolBarManager toolBarManager, IAction scrollLockAction)
		{
			this.viewer = viewer;
			this.toolBarManager = toolBarManager;
			this.scrollLockAction = scrollLockAction;
		}

		public void handleEvent(Event event)
		{
			if (textWidget.isDisposed())
			{
				return;
			}
			int index = textWidget.getTopIndex();
			if (index != lastIndex)
			{
				lastIndex = index;
				if (isLastLineVisible(index))
				{
					// The user scrolled to the bottom, so we re-set the auto-scroll to its original value.
					viewer.setAutoScroll(scrollActionEnabled);
					if (scrollLockAction != null)
					{
						scrollLockAction.setChecked(!scrollActionEnabled);
						toolBarManager.update(true);
					}
				}
				else
				{
					// The user scrolled up, so we turn off the auto-scrolling.
					viewer.setAutoScroll(false);
					if (scrollLockAction != null)
					{
						scrollLockAction.setChecked(true);
						toolBarManager.update(true);
					}
				}
			}
		}
	}
}
