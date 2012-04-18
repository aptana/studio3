/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

/***********************************************************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html Contributors: IBM Corporation - initial API and implementation
 **********************************************************************************************************************/

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.contentassist.IContentAssistSubjectControl;
import org.eclipse.jface.contentassist.ISubjectControlContentAssistProcessor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IEventConsumer;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.IViewportListener;
import org.eclipse.jface.text.IWidgetTokenKeeper;
import org.eclipse.jface.text.IWidgetTokenKeeperExtension;
import org.eclipse.jface.text.IWidgetTokenOwner;
import org.eclipse.jface.text.IWidgetTokenOwnerExtension;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistantExtension;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * The standard implementation of the <code>IContentAssistant</code> interface. Usually, clients instantiate this class
 * and configure it before using it.
 */
@SuppressWarnings("deprecation")
public class ContentAssistant implements IContentAssistant, IContentAssistantExtension, IWidgetTokenKeeper,
		IWidgetTokenKeeperExtension// , IUnifiedContentAssistant
{

	/**
	 * A generic closer class used to monitor various interface events in order to determine whether content-assist
	 * should be terminated and all associated windows closed.
	 */
	class Closer implements ControlListener, MouseListener, FocusListener, DisposeListener, IViewportListener
	{

		/** The shell that a <code>ControlListener</code> is registered with. */
		private Shell fShell;
		/**
		 * The control that a <code>MouseListener</code>, a<code>FocusListener</code> and a <code>DisposeListener</code>
		 * are registered with.
		 */
		private Control fControl;

		/**
		 * Installs this closer on it's viewer's text widget.
		 */
		protected void install()
		{
			Control control = fContentAssistSubjectControlAdapter.getControl();
			fControl = control;
			if (Helper.okToUse(control))
			{

				Shell shell = control.getShell();
				fShell = shell;
				shell.addControlListener(this);

				control.addMouseListener(this);
				control.addFocusListener(this);

				/*
				 * 1GGYYWK: ITPJUI:ALL - Dismissing editor with code assist up causes lots of Internal Errors
				 */
				control.addDisposeListener(this);
			}
			if (fViewer != null)
			{
				fViewer.addViewportListener(this);
			}
		}

		/**
		 * Uninstalls this closer from the viewer's text widget.
		 */
		protected void uninstall()
		{
			Control shell = fShell;
			fShell = null;
			if (Helper.okToUse(shell))
			{
				shell.removeControlListener(this);
			}

			Control control = fControl;
			fControl = null;
			if (Helper.okToUse(control))
			{

				control.removeMouseListener(this);
				control.removeFocusListener(this);

				/*
				 * 1GGYYWK: ITPJUI:ALL - Dismissing editor with code assist up causes lots of Internal Errors
				 */
				control.removeDisposeListener(this);
			}

			if (fViewer != null)
			{
				fViewer.removeViewportListener(this);
			}
		}

		/**
		 * @see org.eclipse.swt.events.ControlListener#controlResized(org.eclipse.swt.events.ControlEvent)
		 */
		public void controlResized(ControlEvent e)
		{
			hide();
		}

		/**
		 * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
		 */
		public void controlMoved(ControlEvent e)
		{
			hide();
		}

		/**
		 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
		 */
		public void mouseDown(MouseEvent e)
		{
			hide();
		}

		/**
		 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
		 */
		public void mouseUp(MouseEvent e)
		{
		}

		/**
		 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
		 */
		public void mouseDoubleClick(MouseEvent e)
		{
			hide();
		}

		/**
		 * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
		 */
		public void focusGained(FocusEvent e)
		{
			focusChanged(e);
		}

		/**
		 * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
		 */
		public void focusLost(FocusEvent e)
		{
			focusChanged(e);

		}

		/*
		 * Called on focus gained or lost.
		 */
		private void focusChanged(FocusEvent e)
		{
			Control control = fControl;
			if (Helper.okToUse(control))
			{
				Display d = control.getDisplay();
				if (d != null)
				{
					d.asyncExec(new Runnable()
					{
						public void run()
						{
							if (!fProposalPopup.hasFocus()
									&& (fContextInfoPopup == null || !fContextInfoPopup.hasFocus()))
							{
								hide();
							}
						}
					});
				}
			}
		}

		/**
		 * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
		 */
		public void widgetDisposed(DisposeEvent e)
		{
			/*
			 * 1GGYYWK: ITPJUI:ALL - Dismissing editor with code assist up causes lots of Internal Errors
			 */
			hide();
		}

		/**
		 * @see org.eclipse.jface.text.IViewportListener#viewportChanged(int)
		 */
		public void viewportChanged(int topIndex)
		{
			hide();
		}
	}

	/**
	 * An implementation of <code>IContentAssistListener</code>, this class is used to monitor key events in support of
	 * automatic activation of the code assistant. If enabled, the implementation utilizes a thread to watch for input
	 * characters matching the activation characters specified by the code assist processor, and if detected, will wait
	 * the indicated delay interval before activating the code assistant.
	 */
	class AutoAssistListener extends KeyAdapter implements KeyListener, Runnable, VerifyKeyListener
	{
		private Thread fThread;
		private boolean fIsReset = false;
		private Object fMutex = new Object();
		private int fShowStyle;

		private static final int SHOW_PROPOSALS = 1;
		private static final int SHOW_CONTEXT_INFO = 2;

		/**
		 * AutoAssistListener
		 */
		protected AutoAssistListener()
		{
		}

		/**
		 * start
		 * 
		 * @param showStyle
		 */
		protected void start(int showStyle)
		{
			fShowStyle = showStyle;
			fThread = new Thread(this, "Aptana: ContentAssistant.assist_delay_timer_name"); //$NON-NLS-1$
			fThread.start();
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			try
			{
				while (true)
				{
					synchronized (fMutex)
					{
						if (fAutoActivationDelay != 0)
						{
							fMutex.wait(fAutoActivationDelay);
						}
						if (fIsReset)
						{
							fIsReset = false;
							continue;
						}
					}
					showAssist(fShowStyle);
					break;
				}
			}
			catch (InterruptedException e)
			{
			}
			fThread = null;
		}

		/**
		 * reset
		 * 
		 * @param showStyle
		 */
		protected void reset(int showStyle)
		{
			synchronized (fMutex)
			{
				fShowStyle = showStyle;
				fIsReset = true;
				fMutex.notifyAll();
			}
		}

		/**
		 * stop
		 */
		protected void stop()
		{
			Thread threadToStop = fThread;
			if (threadToStop != null && threadToStop.isAlive())
			{
				threadToStop.interrupt();
			}
		}

		/**
		 * contains
		 * 
		 * @param characters
		 * @param character
		 * @return boolean
		 */
		private boolean contains(char[] characters, char character)
		{
			if (characters != null)
			{
				for (int i = 0; i < characters.length; i++)
				{
					if (character == characters[i])
					{
						return true;
					}
				}
			}
			return false;
		}

		/**
		 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
		 */
		public void keyPressed(KeyEvent e)
		{
			// Only act on typed characters and ignore modifier-only events
			if (e.character == 0 && (e.keyCode & SWT.KEYCODE_BIT) == 0)
			{
				return;
			}

			if (e.character != 0 && (e.stateMask == SWT.ALT))
				return;

			// Only act on characters that are trigger candidates. This
			// avoids computing the model selection on every keystroke
			boolean validAssistLocation = false;

			if (computeAllAutoActivationTriggers().indexOf(e.character) < 0)
			{
				StyledText styledText = (StyledText) e.widget;
				validAssistLocation = isValidAutoAssistLocation(e, styledText);
				if (!validAssistLocation)
				{
					stop();
					return;
				}
			}

			int showStyle;
			int pos = fContentAssistSubjectControlAdapter.getSelectedRange().x;
			char[] activation;

			activation = fContentAssistSubjectControlAdapter.getCompletionProposalAutoActivationCharacters(
					ContentAssistant.this, pos);

			if ((contains(activation, e.character) || validAssistLocation) && !isProposalPopupActive())
			{
				showStyle = SHOW_PROPOSALS;
				fProposalPopup.setActivationKey(e.character);
			}
			else
			{
				activation = fContentAssistSubjectControlAdapter.getContextInformationAutoActivationCharacters(
						ContentAssistant.this, pos);
				if ((contains(activation, e.character) || validAssistLocation) && !isContextInfoPopupActive())
				{
					showStyle = SHOW_CONTEXT_INFO;
				}
				else
				{
					stop();
					return;
				}
			}

			if (fThread != null && fThread.isAlive())
			{
				reset(showStyle);
			}
			else
			{
				start(showStyle);
			}
		}

		/**
		 * @see org.eclipse.swt.custom.VerifyKeyListener#verifyKey(org.eclipse.swt.events.VerifyEvent)
		 */
		public void verifyKey(VerifyEvent event)
		{
			keyPressed(event);
		}

		/**
		 * showAssist
		 * 
		 * @param showStyle
		 */
		protected void showAssist(final int showStyle)
		{
			final Display d = fContentAssistSubjectControlAdapter.getControl().getDisplay();
			if (d != null)
			{
				try
				{
					d.syncExec(new Runnable()
					{
						public void run()
						{
							Control c = d.getFocusControl();
							if (c == null)
							{
								return;
							}

							if (showStyle == SHOW_PROPOSALS)
							{
								fProposalPopup.showProposals(true);
							}
							else if (showStyle == SHOW_CONTEXT_INFO && fContextInfoPopup != null)
							{
								fContextInfoPopup.showContextProposals(true);
							}
							// }
						}
					});
				}
				catch (SWTError e)
				{
				}
			}
		}
	}

	/**
	 * The layout manager layouts the various windows associated with the code assistant based on the settings of the
	 * code assistant.
	 */
	class LayoutManager implements Listener
	{

		// Presentation types.
		/** The presentation type for the proposal selection popup. */
		public static final int LAYOUT_PROPOSAL_SELECTOR = 0;
		/** The presentation type for the context selection popup. */
		public static final int LAYOUT_CONTEXT_SELECTOR = 1;
		/** The presentation type for the context information hover . */
		public static final int LAYOUT_CONTEXT_INFO_POPUP = 2;

		int fContextType = LAYOUT_CONTEXT_SELECTOR;
		Shell[] fShells = new Shell[3];
		Object[] fPopups = new Object[3];

		/**
		 * add
		 * 
		 * @param popup
		 * @param shell
		 * @param type
		 * @param offset
		 */
		protected void add(Object popup, Shell shell, int type, int offset)
		{
			checkType(type);

			if (fShells[type] != shell)
			{
				if (fShells[type] != null)
				{
					fShells[type].removeListener(SWT.Dispose, this);
				}
				shell.addListener(SWT.Dispose, this);
				fShells[type] = shell;
			}

			fPopups[type] = popup;
			if (type == LAYOUT_CONTEXT_SELECTOR || type == LAYOUT_CONTEXT_INFO_POPUP)
			{
				fContextType = type;
			}

			layout(type, offset);
			adjustListeners(type);
		}

		/**
		 * checkType
		 * 
		 * @param type
		 */
		protected void checkType(int type)
		{
			// Assert.isTrue(type == LAYOUT_PROPOSAL_SELECTOR || type == LAYOUT_CONTEXT_SELECTOR || type ==
			// LAYOUT_CONTEXT_INFO_POPUP);
		}

		/**
		 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
		 */
		public void handleEvent(Event event)
		{
			Widget source = event.widget;
			source.removeListener(SWT.Dispose, this);

			int type = getShellType(source);
			checkType(type);
			fShells[type] = null;

			switch (type)
			{
				case LAYOUT_PROPOSAL_SELECTOR:
					if (fContextType == LAYOUT_CONTEXT_SELECTOR && Helper.okToUse(fShells[LAYOUT_CONTEXT_SELECTOR]))
					{
						// Restore event notification to the tip popup.
						addContentAssistListener((IContentAssistListener) fPopups[LAYOUT_CONTEXT_SELECTOR],
								CONTEXT_SELECTOR);
					}
					break;

				case LAYOUT_CONTEXT_SELECTOR:
					if (Helper.okToUse(fShells[LAYOUT_PROPOSAL_SELECTOR]))
					{
						if (fProposalPopupOrientation == PROPOSAL_STACKED)
						{
							layout(LAYOUT_PROPOSAL_SELECTOR, getSelectionOffset());
						}
						// Restore event notification to the proposal popup.
						addContentAssistListener((IContentAssistListener) fPopups[LAYOUT_PROPOSAL_SELECTOR],
								PROPOSAL_SELECTOR);
					}
					fContextType = LAYOUT_CONTEXT_INFO_POPUP;
					break;

				case LAYOUT_CONTEXT_INFO_POPUP:
					if (Helper.okToUse(fShells[LAYOUT_PROPOSAL_SELECTOR]))
					{
						if (fContextInfoPopupOrientation == CONTEXT_INFO_BELOW)
						{
							layout(LAYOUT_PROPOSAL_SELECTOR, getSelectionOffset());
						}
					}
					fContextType = LAYOUT_CONTEXT_SELECTOR;
					break;

				default:
					break;
			}
		}

		/**
		 * getShellType
		 * 
		 * @param shell
		 * @return int
		 */
		protected int getShellType(Widget shell)
		{
			for (int i = 0; i < fShells.length; i++)
			{
				if (fShells[i] == shell)
				{
					return i;
				}
			}
			return -1;
		}

		/**
		 * layout
		 * 
		 * @param type
		 * @param offset
		 */
		protected void layout(int type, int offset)
		{
			switch (type)
			{
				case LAYOUT_PROPOSAL_SELECTOR:
					layoutProposalSelector(offset);
					break;
				case LAYOUT_CONTEXT_SELECTOR:
					layoutContextSelector(offset);
					break;
				case LAYOUT_CONTEXT_INFO_POPUP:
					layoutContextInfoPopup(offset);
					break;
				default:
					break;
			}
		}

		/**
		 * layoutProposalSelector
		 * 
		 * @param offset
		 */
		protected void layoutProposalSelector(int offset)
		{
			if (fContextType == LAYOUT_CONTEXT_INFO_POPUP && fContextInfoPopupOrientation == CONTEXT_INFO_BELOW
					&& Helper.okToUse(fShells[LAYOUT_CONTEXT_INFO_POPUP]))
			{
				// Stack proposal selector beneath the tip box.
				Shell shell = fShells[LAYOUT_PROPOSAL_SELECTOR];
				Shell parent = fShells[LAYOUT_CONTEXT_INFO_POPUP];

				Point p = getAboveLocation(shell, offset);
				if (p.y <= 0)
				{
					p = getStackedLocation(shell, parent);
				}

				shell.setLocation(p);
			}
			else if (fContextType != LAYOUT_CONTEXT_SELECTOR || !Helper.okToUse(fShells[LAYOUT_CONTEXT_SELECTOR]))
			{
				// There are no other presentations to be concerned with,
				// so place the proposal selector beneath the cursor line.
				Shell shell = fShells[LAYOUT_PROPOSAL_SELECTOR];

				offset = findOffsetOfFirstCharacter(fContentAssistSubjectControlAdapter, offset);

				Point proposedLocation = getBelowLocation(shell, offset, true);

				// NOTE: this gives the position at the top of the line at the current offset
				Point cursorLocation = fContentAssistSubjectControlAdapter.getLocationAtOffset(offset);

				// convert to screen coordinates
				cursorLocation = fContentAssistSubjectControlAdapter.getControl().toDisplay(cursorLocation);

				int lineHeight = fContentAssistSubjectControlAdapter.getLineHeight();

				// bounding box of the proposal selector
				Rectangle shellBounds = shell.getBounds();
				int shellBoundsBottom = proposedLocation.y + shellBounds.height;

				if (proposedLocation.y <= (cursorLocation.y + lineHeight) && cursorLocation.y < shellBoundsBottom)
				{
					proposedLocation = getAboveLocation(shell, offset);
				}

				proposedLocation.x -= 32;

				shell.setLocation(proposedLocation);

				// clip right side of popup to right side of screen, if necessary
				Rectangle bounds = Display.getCurrent().getBounds();
				int screenRight = bounds.x + bounds.width;
				int proposalRight = proposedLocation.x + shell.getSize().x;

				if (proposalRight > screenRight)
				{
					int newWidth = shell.getSize().x - (proposalRight - screenRight);

					shell.setSize(newWidth, shell.getSize().y);
				}
			}
			else
			{
				switch (fProposalPopupOrientation)
				{
					case PROPOSAL_REMOVE:
					{
						// Remove the tip selector and place the
						// proposal selector beneath the cursor line.
						fShells[LAYOUT_CONTEXT_SELECTOR].dispose();
						Shell shell = fShells[LAYOUT_PROPOSAL_SELECTOR];
						shell.setLocation(getBelowLocation(shell, offset, true));
						break;
					}
					case PROPOSAL_OVERLAY:
					{
						// Overlay the tip selector with the proposal selector.
						Shell shell = fShells[LAYOUT_PROPOSAL_SELECTOR];
						shell.setLocation(getBelowLocation(shell, offset, true));
						break;
					}
					case PROPOSAL_STACKED:
					{
						// Stack the proposal selector beneath the tip selector.
						Shell shell = fShells[LAYOUT_PROPOSAL_SELECTOR];
						Shell parent = fShells[LAYOUT_CONTEXT_SELECTOR];
						shell.setLocation(getStackedLocation(shell, parent));
						break;
					}
					default:
						break;
				}
			}
		}

		private int findOffsetOfFirstCharacter(ContentAssistSubjectControlAdapter adapter, int offset)
		{
			int origOffset = offset;

			offset--;

			while (offset >= 0)
			{
				char c;

				try
				{
					c = adapter.getDocument().getChar(offset);
				}
				catch (BadLocationException e)
				{
					return origOffset;
				}

				if (Character.isWhitespace(c) || c == '.' || c == ',' || c == '(' || c == ':' || c == '#')
				{
					return offset + 1;
				}
				else
				{
					offset--;
				}
			}

			return offset;
		}

		/**
		 * layoutContextSelector
		 * 
		 * @param offset
		 */
		protected void layoutContextSelector(int offset)
		{
			// Always place the context selector beneath the cursor line.
			Shell shell = fShells[LAYOUT_CONTEXT_SELECTOR];
			shell.setLocation(getBelowLocation(shell, offset, true));

			if (Helper.okToUse(fShells[LAYOUT_PROPOSAL_SELECTOR]))
			{
				switch (fProposalPopupOrientation)
				{
					case PROPOSAL_REMOVE:
						// Remove the proposal selector.
						fShells[LAYOUT_PROPOSAL_SELECTOR].dispose();
						break;

					case PROPOSAL_OVERLAY:
						// The proposal selector has been overlaid by the tip selector.
						break;

					case PROPOSAL_STACKED:
					{
						// Stack the proposal selector beneath the tip selector.
						shell = fShells[LAYOUT_PROPOSAL_SELECTOR];
						Shell parent = fShells[LAYOUT_CONTEXT_SELECTOR];

						Point p = getAboveLocation(shell, offset);
						if (p.y <= 0)
						{
							p = getStackedLocation(shell, parent);
						}
						shell.setLocation(p);

						break;
					}

					default:
						break;
				}
			}
		}

		/**
		 * layoutContextInfoPopup
		 * 
		 * @param offset
		 */
		protected void layoutContextInfoPopup(int offset)
		{
			switch (fContextInfoPopupOrientation)
			{
				case CONTEXT_INFO_ABOVE:
				{
					// Place the popup above the cursor line.
					Shell shell = fShells[LAYOUT_CONTEXT_INFO_POPUP];
					shell.setLocation(getAboveLocation(shell, offset));
					break;
				}
				case CONTEXT_INFO_BELOW:
				{
					// Place the popup beneath the cursor line.
					Shell parent = fShells[LAYOUT_CONTEXT_INFO_POPUP];
					parent.setLocation(getBelowLocation(parent, offset, false));

					if (Helper.okToUse(fShells[LAYOUT_PROPOSAL_SELECTOR]))
					{
						// Stack the proposal selector beneath the context info popup.
						Shell shell = fShells[LAYOUT_PROPOSAL_SELECTOR];

						Point p = getAboveLocation(shell, offset);
						if (p.y <= 0)
						{
							p = getStackedLocation(shell, parent);
						}

						shell.setLocation(p);
					}
					break;
				}
				default:
					break;
			}
		}

		/**
		 * shiftHorizontalLocation
		 * 
		 * @param location
		 * @param shellBounds
		 * @param displayBounds
		 */
		protected void shiftHorizontalLocation(Point location, Rectangle shellBounds, Rectangle displayBounds)
		{
			if (location.x + shellBounds.width > displayBounds.width)
			{
				location.x = displayBounds.width - shellBounds.width;
			}

			if (location.x < displayBounds.x)
			{
				location.x = displayBounds.x;
			}
		}

		/**
		 * shiftVerticalLocation
		 * 
		 * @param location
		 * @param shellBounds
		 * @param displayBounds
		 * @param keepOnScreen
		 */
		protected void shiftVerticalLocation(Point location, Rectangle shellBounds, Rectangle displayBounds,
				boolean keepOnScreen)
		{
			if (keepOnScreen)
			{
				// Move to the bottom of the screen if we hang over it
				if (location.y + shellBounds.height > displayBounds.height)
				{
					location.y = displayBounds.height - shellBounds.height;
				}
			}

			// Move to the top of the screen if we're above it
			if (location.y < displayBounds.y)
			{
				location.y = displayBounds.y;
			}
		}

		/**
		 * getAboveLocation
		 * 
		 * @param shell
		 * @param offset
		 * @return Point
		 */
		protected Point getAboveLocation(Shell shell, int offset)
		{
			Point location = fContentAssistSubjectControlAdapter.getLocationAtOffset(offset);
			location = fContentAssistSubjectControlAdapter.getControl().toDisplay(location);

			Rectangle shellBounds = shell.getBounds();
			Rectangle displayBounds = shell.getDisplay().getClientArea();

			location.y = location.y - (shellBounds.height + fContentAssistSubjectControlAdapter.getLineHeight());

			shiftHorizontalLocation(location, shellBounds, displayBounds);
			shiftVerticalLocation(location, shellBounds, displayBounds, true);

			return location;
		}

		/**
		 * getBelowLocation
		 * 
		 * @param shell
		 * @param offset
		 * @param keepOnScreen
		 * @return Point
		 */
		protected Point getBelowLocation(Shell shell, int offset, boolean keepOnScreen)
		{
			// NOTE: this gives the position at the top of the line at the current offset
			Point location = fContentAssistSubjectControlAdapter.getLocationAtOffset(offset);

			// make sure we're not off the left or top of the screen
			if (location.x < 0)
			{
				location.x = 0;
			}
			if (location.y < 0)
			{
				location.y = 0;
			}

			// convert to screen coordinates
			location = fContentAssistSubjectControlAdapter.getControl().toDisplay(location);

			// bounding box of the proposal selector
			Rectangle shellBounds = shell.getBounds();

			// bounding box of the display (screen)
			Rectangle displayBounds = shell.getDisplay().getClientArea();

			// Move down one line and add padding
			location.y = location.y + fContentAssistSubjectControlAdapter.getLineHeight() + 5;

			// shiftHorizontalLocation(location, shellBounds, displayBounds);
			shiftVerticalLocation(location, shellBounds, displayBounds, keepOnScreen);

			return location;
		}

		/**
		 * getStackedLocation
		 * 
		 * @param shell
		 * @param parent
		 * @return Point
		 */
		protected Point getStackedLocation(Shell shell, Shell parent)
		{
			Point p = parent.getLocation();
			Point parentSize = parent.getSize();
			// p.x += parentSize.x / 4;
			p.y += parentSize.y + 5;

			// p= parent.toDisplay(p);

			Rectangle shellBounds = shell.getBounds();
			Rectangle displayBounds = shell.getDisplay().getClientArea();
			shiftHorizontalLocation(p, shellBounds, displayBounds);
			shiftVerticalLocation(p, shellBounds, displayBounds, true);

			return p;
		}

		/**
		 * adjustListeners
		 * 
		 * @param type
		 */
		protected void adjustListeners(int type)
		{
			switch (type)
			{
				case LAYOUT_PROPOSAL_SELECTOR:
					if (fContextType == LAYOUT_CONTEXT_SELECTOR && Helper.okToUse(fShells[LAYOUT_CONTEXT_SELECTOR]))
					{
						// Disable event notification to the tip selector.
						removeContentAssistListener((IContentAssistListener) fPopups[LAYOUT_CONTEXT_SELECTOR],
								CONTEXT_SELECTOR);
					}
					break;
				case LAYOUT_CONTEXT_SELECTOR:
					if (Helper.okToUse(fShells[LAYOUT_PROPOSAL_SELECTOR]))
					{
						// Disable event notification to the proposal selector.
						removeContentAssistListener((IContentAssistListener) fPopups[LAYOUT_PROPOSAL_SELECTOR],
								PROPOSAL_SELECTOR);
					}
					break;
				case LAYOUT_CONTEXT_INFO_POPUP:
					break;

				default:
					break;
			}
		}
	}

	/**
	 * Internal key listener and event consumer.
	 */
	class InternalListener implements VerifyKeyListener, IEventConsumer
	{

		/**
		 * Verifies key events by notifying the registered listeners. Each listener is allowed to indicate that the
		 * event has been handled and should not be further processed.
		 * 
		 * @param e
		 *            the verify event
		 * @see VerifyKeyListener#verifyKey(org.eclipse.swt.events.VerifyEvent)
		 */
		public void verifyKey(VerifyEvent e)
		{
			IContentAssistListener[] listeners = fListeners.clone();
			for (int i = 0; i < listeners.length; i++)
			{
				if (listeners[i] != null)
				{
					if (!listeners[i].verifyKey(e) || !e.doit)
					{
						break;
					}
				}
			}
			if (fAutoAssistListener != null)
			{
				fAutoAssistListener.keyPressed(e);
			}
		}

		/**
		 * @see org.eclipse.jface.text.IEventConsumer#processEvent(org.eclipse.swt.events.VerifyEvent)
		 */
		public void processEvent(VerifyEvent event)
		{

			installKeyListener();

			IContentAssistListener[] listeners = fListeners.clone();
			for (int i = 0; i < listeners.length; i++)
			{
				if (listeners[i] != null)
				{
					listeners[i].processEvent(event);
					if (!event.doit)
					{
						return;
					}
				}
			}
		}
	}

	/**
	 * Dialog store constants.
	 * 
	 * @since 3.0
	 */
	public static final String STORE_SIZE_X = "size.x"; //$NON-NLS-1$

	/**
	 * STORE_SIZE_Y
	 */
	public static final String STORE_SIZE_Y = "size.y"; //$NON-NLS-1$

	// Content-Assist Listener types
	static final int CONTEXT_SELECTOR = 0;
	static final int PROPOSAL_SELECTOR = 1;
	static final int CONTEXT_INFO_POPUP = 2;

	/**
	 * The popup priority: &gt; linked position proposals and hover pop-ups. Default value: <code>20</code>;
	 * 
	 * @since 3.0
	 */
	public static final int WIDGET_PRIORITY = 20;

	private static final int DEFAULT_AUTO_ACTIVATION_DELAY = 0;
	private static final int DEFAULT_INFO_POPUP_DELAY = 200;

	private IInformationControlCreator fInformationControlCreator;
	private int fAutoActivationDelay = DEFAULT_AUTO_ACTIVATION_DELAY;
	private boolean fIsAutoActivated = false;
	private boolean fIsAutoInserting = false;
	private int fProposalPopupOrientation = PROPOSAL_OVERLAY;
	private int fContextInfoPopupOrientation = CONTEXT_INFO_ABOVE;
	private Map<String, IContentAssistProcessor> fProcessors;

	/**
	 * The partitioning.
	 * 
	 * @since 3.0
	 */
	private String fPartitioning;

	private Color fContextInfoPopupBackground;
	private Color fContextInfoPopupForeground;
	private Color fContextSelectorBackground;
	private Color fContextSelectorForeground;
	private Color fProposalSelectorBackground;
	private Color fProposalSelectorForeground;

	private ITextViewer fViewer;
	private String fLastErrorMessage;

	private Closer fCloser;
	private LayoutManager fLayoutManager;
	private AutoAssistListener fAutoAssistListener;
	private InternalListener fInternalListener;
	private CompletionProposalPopup fProposalPopup;
	private ContextInformationPopup fContextInfoPopup;

	private int fUserAgentColumnCount;

	// private IUnifiedEditor editor;

	/**
	 * Flag which tells whether a verify key listener is hooked.
	 * 
	 * @since 3.0
	 */
	private boolean fVerifyKeyListenerHooked = false;
	private IContentAssistListener[] fListeners = new IContentAssistListener[4];
	/**
	 * The code assist subject control.
	 * 
	 * @since 3.0
	 */
	private IContentAssistSubjectControl fContentAssistSubjectControl;
	/**
	 * The code assist subject control adapter.
	 * 
	 * @since 3.0
	 */
	private ContentAssistSubjectControlAdapter fContentAssistSubjectControlAdapter;
	/**
	 * The dialog settings for the control's bounds.
	 * 
	 * @since 3.0
	 */
	private IDialogSettings fDialogSettings;
	/**
	 * Prefix completion setting.
	 * 
	 * @since 3.0
	 */
	private boolean fIsPrefixCompletionEnabled = false;

	private Color fProposalSelectorSelectionColor;

	/**
	 * Creates a new code assistant. The code assistant is not automatically activated, overlays the completion
	 * proposals with context information list if necessary, and shows the context information above the location at
	 * which it was activated. If auto activation will be enabled, without further configuration steps, this code
	 * assistant is activated after a 500 milli-seconds delay. It uses the default partitioning.
	 */
	public ContentAssistant()
	{
		fPartitioning = IDocumentExtension3.DEFAULT_PARTITIONING;
	}

	/**
	 * Sets the document partitioning this code assistant is using.
	 * 
	 * @param partitioning
	 *            the document partitioning for this code assistant
	 * @since 3.0
	 */
	public void setDocumentPartitioning(String partitioning)
	{
		fPartitioning = partitioning;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistantExtension#getDocumentPartitioning()
	 */
	public String getDocumentPartitioning()
	{
		return fPartitioning;
	}

	/**
	 * Registers a given code assist processor for a particular content type. If there is already a processor registered
	 * for this type, the new processor is registered instead of the old one.
	 * 
	 * @param processor
	 *            the code assist processor to register, or <code>null</code> to remove an existing one
	 * @param contentType
	 *            the content type under which to register
	 */
	public void setContentAssistProcessor(IContentAssistProcessor processor, String contentType)
	{
		if (fProcessors == null)
		{
			fProcessors = new HashMap<String, IContentAssistProcessor>();
		}

		if (processor == null)
		{
			fProcessors.remove(contentType);
		}
		else
		{
			fProcessors.put(contentType, processor);
		}
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistant#getContentAssistProcessor(java.lang.String)
	 */
	public IContentAssistProcessor getContentAssistProcessor(String contentType)
	{
		if (fProcessors == null)
		{
			return null;
		}

		return fProcessors.get(contentType);
	}

	/**
	 * Computes the sorted set of all auto activation trigger characters.
	 * 
	 * @return the sorted set of all auto activation trigger characters
	 * @since 3.1
	 */
	private String computeAllAutoActivationTriggers()
	{
		if (fProcessors == null)
		{
			return ""; //$NON-NLS-1$
		}

		StringBuffer buf = new StringBuffer(5);
		Iterator<Entry<String, IContentAssistProcessor>> iter = fProcessors.entrySet().iterator();

		while (iter.hasNext())
		{
			Entry<String, IContentAssistProcessor> entry = iter.next();
			IContentAssistProcessor processor = entry.getValue();
			char[] triggers = processor.getCompletionProposalAutoActivationCharacters();

			if (triggers != null)
			{
				buf.append(triggers);
			}

			triggers = processor.getContextInformationAutoActivationCharacters();

			if (triggers != null)
			{
				buf.append(triggers);
			}
		}

		return buf.toString();
	}

	/**
	 * @param offset
	 * @param document
	 */
	private boolean isValidAutoAssistLocation(KeyEvent e, StyledText styledText)
	{
		// Don't pop up CA if we pressed a Ctrl or Command character. On Linux, Unicode characters can be inserted with
		// Ctrl + Shift + u + key sequence, but at this point, all we get is the character, no modifiers.
		if (e.stateMask == SWT.MOD1)
		{
			return false;
		}

		int keyCode = e.keyCode;
		if (keyCode == SWT.ESC || keyCode == SWT.BS || keyCode == SWT.DEL || keyCode == SWT.ARROW
				|| (keyCode & SWT.KEYCODE_BIT) != 0)
		{
			return false;
		}

		int offset = styledText.getCaretOffset();
		IContentAssistProcessor processor = getProcessor(fContentAssistSubjectControlAdapter, offset);
		if (processor instanceof ICommonContentAssistProcessor)
		{
			ICommonContentAssistProcessor cp = (ICommonContentAssistProcessor) processor;
			// are we typing a valid identifier, and the previous "location" (character or lexeme) should pop up CA
			return cp.isValidIdentifier(e.character, keyCode)
					&& isAutoActivationLocation(cp, styledText, e.character, keyCode);
		}
		else
		{
			return false;
		}
	}

	/**
	 * @param cp
	 * @param styledText
	 * @param c
	 * @param keyCode
	 * @return
	 */
	private boolean isAutoActivationLocation(ICommonContentAssistProcessor cp, StyledText styledText, char c,
			int keyCode)
	{

		int offset = styledText.getCaretOffset();
		Point selection = styledText.getSelection();
		if (offset >= selection.x && offset <= selection.y)
		{
			offset = selection.x;
		}

		// Are we at beginning of file?
		if (offset == 0)
		{
			return true;
		}

		String line = styledText.getText(offset - 1, offset - 1);

		if (line.length() > 0)
		{
			return cp.isValidActivationCharacter(line.charAt(0), keyCode)
					|| cp.isValidAutoActivationLocation(c, keyCode, fContentAssistSubjectControlAdapter.getDocument(),
							offset);
		}
		else
		{
			return false;
		}
	}

	/**
	 * Enables the code assistant's auto activation mode.
	 * 
	 * @param enabled
	 *            indicates whether auto activation is enabled or not
	 */
	public void enableAutoActivation(boolean enabled)
	{
		fIsAutoActivated = enabled;
		manageAutoActivation(fIsAutoActivated);
	}

	/**
	 * Enables the code assistant's auto insertion mode. If enabled, the code assistant inserts a proposal automatically
	 * if it is the only proposal. In the case of ambiguities, the user must make the choice.
	 * 
	 * @param enabled
	 *            indicates whether auto insertion is enabled or not
	 * @since 2.0
	 */
	public void enableAutoInsert(boolean enabled)
	{
		fIsAutoInserting = enabled;
	}

	/**
	 * Returns whether this code assistant is in the auto insertion mode or not.
	 * 
	 * @return <code>true</code> if in auto insertion mode
	 * @since 2.0
	 */
	boolean isAutoInserting()
	{
		return fIsAutoInserting;
	}

	/**
	 * Installs and uninstall the listeners needed for auto-activation.
	 * 
	 * @param start
	 *            <code>true</code> if listeners must be installed, <code>false</code> if they must be removed
	 * @since 2.0
	 */
	private void manageAutoActivation(boolean start)
	{
		if (start)
		{

			if ((fContentAssistSubjectControlAdapter != null) && fAutoAssistListener == null)
			{
				fAutoAssistListener = new AutoAssistListener();
				// For details see https://bugs.eclipse.org/bugs/show_bug.cgi?id=49212
				if (fContentAssistSubjectControlAdapter.supportsVerifyKeyListener())
				{
					fContentAssistSubjectControlAdapter.appendVerifyKeyListener(fAutoAssistListener);
				}
				else
				{
					fContentAssistSubjectControlAdapter.addKeyListener(fAutoAssistListener);
				}
			}

		}
		else if (fAutoAssistListener != null)
		{
			// For details see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=49212
			if (fContentAssistSubjectControlAdapter.supportsVerifyKeyListener())
			{
				fContentAssistSubjectControlAdapter.removeVerifyKeyListener(fAutoAssistListener);
			}
			else
			{
				fContentAssistSubjectControlAdapter.removeKeyListener(fAutoAssistListener);
			}
			fAutoAssistListener = null;
		}
	}

	/**
	 * Sets the delay after which the code assistant is automatically invoked if the cursor is behind an auto activation
	 * character.
	 * 
	 * @param delay
	 *            the auto activation delay
	 */
	public void setAutoActivationDelay(int delay)
	{
		fAutoActivationDelay = delay;
	}

	/**
	 * Sets the proposal pop-ups' orientation. The following values may be used:
	 * <ul>
	 * <li>PROPOSAL_OVERLAY
	 * <p>
	 * proposal popup windows should overlay each other</li>
	 * <li>PROPOSAL_REMOVE
	 * <p>
	 * any currently shown proposal popup should be closed</li>
	 * <li>PROPOSAL_STACKED
	 * <p>
	 * proposal popup windows should be vertical stacked, with no overlap, beneath the line containing the current
	 * cursor location</li>
	 * </ul>
	 * 
	 * @param orientation
	 *            the popup's orientation
	 */
	public void setProposalPopupOrientation(int orientation)
	{
		fProposalPopupOrientation = orientation;
	}

	/**
	 * Sets the context information popup's orientation. The following values may be used:
	 * <ul>
	 * <li>CONTEXT_ABOVE
	 * <p>
	 * context information popup should always appear above the line containing the current cursor location</li>
	 * <li>CONTEXT_BELOW
	 * <p>
	 * context information popup should always appear below the line containing the current cursor location</li>
	 * </ul>
	 * 
	 * @param orientation
	 *            the popup's orientation
	 */
	public void setContextInformationPopupOrientation(int orientation)
	{
		fContextInfoPopupOrientation = orientation;
	}

	/**
	 * Sets the context information popup's background color.
	 * 
	 * @param background
	 *            the background color
	 */
	public void setContextInformationPopupBackground(Color background)
	{
		fContextInfoPopupBackground = background;
	}

	/**
	 * Returns the background of the context information popup.
	 * 
	 * @return the background of the context information popup
	 * @since 2.0
	 */
	Color getContextInformationPopupBackground()
	{
		return fContextInfoPopupBackground;
	}

	/**
	 * Sets the context information popup's foreground color.
	 * 
	 * @param foreground
	 *            the foreground color
	 * @since 2.0
	 */
	public void setContextInformationPopupForeground(Color foreground)
	{
		fContextInfoPopupForeground = foreground;
	}

	/**
	 * Returns the foreground of the context information popup.
	 * 
	 * @return the foreground of the context information popup
	 * @since 2.0
	 */
	Color getContextInformationPopupForeground()
	{
		return fContextInfoPopupForeground;
	}

	/**
	 * Sets the proposal selector's background color.
	 * 
	 * @param background
	 *            the background color
	 * @since 2.0
	 */
	public void setProposalSelectorBackground(Color background)
	{
		fProposalSelectorBackground = background;
	}

	/**
	 * Returns the background of the proposal selector.
	 * 
	 * @return the background of the proposal selector
	 * @since 2.0
	 */
	Color getProposalSelectorBackground()
	{
		return fProposalSelectorBackground;
	}

	/**
	 * Sets the proposal's foreground color.
	 * 
	 * @param foreground
	 *            the foreground color
	 * @since 2.0
	 */
	public void setProposalSelectorForeground(Color foreground)
	{
		fProposalSelectorForeground = foreground;
	}

	/**
	 * Returns the foreground of the proposal selector.
	 * 
	 * @return the foreground of the proposal selector
	 * @since 2.0
	 */
	Color getProposalSelectorForeground()
	{
		return fProposalSelectorForeground;
	}

	/**
	 * Sets the context selector's background color.
	 * 
	 * @param background
	 *            the background color
	 * @since 2.0
	 */
	public void setContextSelectorBackground(Color background)
	{
		fContextSelectorBackground = background;
	}

	/**
	 * Returns the background of the context selector.
	 * 
	 * @return the background of the context selector
	 * @since 2.0
	 */
	Color getContextSelectorBackground()
	{
		return fContextSelectorBackground;
	}

	/**
	 * Sets the context selector's foreground color.
	 * 
	 * @param foreground
	 *            the foreground color
	 * @since 2.0
	 */
	public void setContextSelectorForeground(Color foreground)
	{
		fContextSelectorForeground = foreground;
	}

	/**
	 * Returns the foreground of the context selector.
	 * 
	 * @return the foreground of the context selector
	 * @since 2.0
	 */
	Color getContextSelectorForeground()
	{
		return fContextSelectorForeground;
	}

	/**
	 * Sets the information control creator for the additional information control.
	 * 
	 * @param creator
	 *            the information control creator for the additional information control
	 * @since 2.0
	 */
	public void setInformationControlCreator(IInformationControlCreator creator)
	{
		fInformationControlCreator = creator;
	}

	// /**
	// * @see
	// com.aptana.ide.editors.unified.contentassist.IUnifiedContentAssistant#setEditor(com.aptana.ide.editors.unified.IUnifiedEditor)
	// */
	// public void setEditor(IUnifiedEditor editor)
	// {
	// this.editor = editor;
	// }

	/**
	 * install
	 * 
	 * @param contentAssistSubjectControl
	 */
	protected void install(IContentAssistSubjectControl contentAssistSubjectControl)
	{
		fContentAssistSubjectControl = contentAssistSubjectControl;
		fContentAssistSubjectControlAdapter = new ContentAssistSubjectControlAdapter(fContentAssistSubjectControl);
		install();
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistant#install(org.eclipse.jface.text.ITextViewer)
	 */
	public void install(ITextViewer textViewer)
	{
		fViewer = textViewer;
		fContentAssistSubjectControlAdapter = new ContentAssistSubjectControlAdapter(fViewer);
		install();
	}

	/**
	 * install
	 */
	protected void install()
	{

		fLayoutManager = new LayoutManager();
		fInternalListener = new InternalListener();

		AdditionalInfoController controller = null;
		if (fInformationControlCreator != null)
		{
			int delay = DEFAULT_INFO_POPUP_DELAY; // default delay for information popups to the sidepopup
			controller = new AdditionalInfoController(fInformationControlCreator, delay);
		}

		fContextInfoPopup = fContentAssistSubjectControlAdapter.createContextInfoPopup(this);
		fProposalPopup = fContentAssistSubjectControlAdapter.createCompletionProposalPopup(this, controller);

		manageAutoActivation(fIsAutoActivated);
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistant#uninstall()
	 */
	public void uninstall()
	{
		hide();
		if (fProposalPopup != null)
		{
			fProposalPopup.disposePopup();
		}
		manageAutoActivation(false);

		if (fCloser != null)
		{
			fCloser.uninstall();
			fCloser = null;
		}

		fViewer = null;
		fContentAssistSubjectControl = null;
		fContentAssistSubjectControlAdapter = null;
	}

	/**
	 * Adds the given shell of the specified type to the layout. Valid types are defined by <code>LayoutManager</code>.
	 * 
	 * @param popup
	 *            a code assist popup
	 * @param shell
	 *            the shell of the content-assist popup
	 * @param type
	 *            the type of popup
	 * @param visibleOffset
	 *            the offset at which to layout the popup relative to the offset of the viewer's visible region
	 * @since 2.0
	 */
	void addToLayout(Object popup, Shell shell, int type, int visibleOffset)
	{
		fLayoutManager.add(popup, shell, type, visibleOffset);
	}

	/**
	 * Layouts the registered popup of the given type relative to the given offset. The offset is relative to the offset
	 * of the viewer's visible region. Valid types are defined by <code>LayoutManager</code>.
	 * 
	 * @param type
	 *            the type of popup to layout
	 * @param visibleOffset
	 *            the offset at which to layout relative to the offset of the viewer's visible region
	 * @since 2.0
	 */
	void layout(int type, int visibleOffset)
	{
		fLayoutManager.layout(type, visibleOffset);
	}

	/**
	 * Notifies the controller that a popup has lost focus.
	 * 
	 * @param e
	 *            the focus event
	 */
	void popupFocusLost(FocusEvent e)
	{
		if (fCloser != null)
		{
			fCloser.focusLost(e);
		}
	}

	/**
	 * Returns the offset of the selection relative to the offset of the visible region.
	 * 
	 * @return the offset of the selection relative to the offset of the visible region
	 * @since 2.0
	 */
	int getSelectionOffset()
	{
		return fContentAssistSubjectControlAdapter.getWidgetSelectionRange().x;
	}

	/**
	 * Returns whether the widget token could be acquired. The following are valid listener types:
	 * <ul>
	 * <li>AUTO_ASSIST</li>
	 * <li>CONTEXT_SELECTOR</li>
	 * <li>PROPOSAL_SELECTOR</li>
	 * <li>CONTEXT_INFO_POPUP</li>
	 * </ul>
	 * 
	 * @param type
	 *            the listener type for which to acquire
	 * @return <code>true</code> if the widget token could be acquired
	 * @since 2.0
	 */
	private boolean acquireWidgetToken(int type)
	{
		switch (type)
		{
			case CONTEXT_SELECTOR:
			case PROPOSAL_SELECTOR:
				if (fContentAssistSubjectControl instanceof IWidgetTokenOwnerExtension)
				{
					IWidgetTokenOwnerExtension extension = (IWidgetTokenOwnerExtension) fContentAssistSubjectControl;
					return extension.requestWidgetToken(this, WIDGET_PRIORITY);
				}
				else if (fContentAssistSubjectControl instanceof IWidgetTokenOwner)
				{
					IWidgetTokenOwner owner = (IWidgetTokenOwner) fContentAssistSubjectControl;
					return owner.requestWidgetToken(this);
				}
				else if (fViewer instanceof IWidgetTokenOwnerExtension)
				{
					IWidgetTokenOwnerExtension extension = (IWidgetTokenOwnerExtension) fViewer;
					return extension.requestWidgetToken(this, WIDGET_PRIORITY);
				}
				else if (fViewer instanceof IWidgetTokenOwner)
				{
					IWidgetTokenOwner owner = (IWidgetTokenOwner) fViewer;
					return owner.requestWidgetToken(this);
				}

			default:
				break;
		}
		return true;
	}

	/**
	 * Registers a code assist listener. The following are valid listener types:
	 * <ul>
	 * <li>AUTO_ASSIST</li>
	 * <li>CONTEXT_SELECTOR</li>
	 * <li>PROPOSAL_SELECTOR</li>
	 * <li>CONTEXT_INFO_POPUP</li>
	 * </ul>
	 * Returns whether the listener could be added successfully. A listener can not be added if the widget token could
	 * not be acquired.
	 * 
	 * @param listener
	 *            the listener to register
	 * @param type
	 *            the type of listener
	 * @return <code>true</code> if the listener could be added
	 */
	boolean addContentAssistListener(IContentAssistListener listener, int type)
	{

		if (acquireWidgetToken(type))
		{

			fListeners[type] = listener;

			if (fCloser == null && getNumberOfListeners() == 1)
			{
				fCloser = new Closer();
				fCloser.install();
				fContentAssistSubjectControlAdapter.setEventConsumer(fInternalListener);
				installKeyListener();
			}
			else
			{
				promoteKeyListener();
			}
			return true;
		}

		return false;
	}

	/**
	 * Re-promotes the key listener to the first position, using prependVerifyKeyListener. This ensures no other
	 * instance is filtering away the keystrokes underneath, if we've been up for a while (e.g. when the context info is
	 * showing.
	 * 
	 * @since 3.0
	 */
	public void promoteKeyListener()
	{
		uninstallVerifyKeyListener();
		installKeyListener();
	}

	/**
	 * Installs a key listener on the text viewer's widget.
	 */
	private void installKeyListener()
	{
		if (!fVerifyKeyListenerHooked)
		{
			if (Helper.okToUse(fContentAssistSubjectControlAdapter.getControl()))
			{
				fVerifyKeyListenerHooked = fContentAssistSubjectControlAdapter
						.prependVerifyKeyListener(fInternalListener);
			}
		}
	}

	/**
	 * Releases the previously acquired widget token if the token is no longer necessary. The following are valid
	 * listener types:
	 * <ul>
	 * <li>AUTO_ASSIST</li>
	 * <li>CONTEXT_SELECTOR</li>
	 * <li>PROPOSAL_SELECTOR</li>
	 * <li>CONTEXT_INFO_POPUP</li>
	 * </ul>
	 * 
	 * @param type
	 *            the listener type
	 * @since 2.0
	 */
	private void releaseWidgetToken(int type)
	{
		if (fListeners[CONTEXT_SELECTOR] == null && fListeners[PROPOSAL_SELECTOR] == null)
		{
			IWidgetTokenOwner owner = null;
			if (fContentAssistSubjectControl instanceof IWidgetTokenOwner)
			{
				owner = (IWidgetTokenOwner) fContentAssistSubjectControl;
			}
			else if (fViewer instanceof IWidgetTokenOwner)
			{
				owner = (IWidgetTokenOwner) fViewer;
			}
			if (owner != null)
			{
				owner.releaseWidgetToken(this);
			}
		}
	}

	/**
	 * Unregisters a code assist listener.
	 * 
	 * @param listener
	 *            the listener to unregister
	 * @param type
	 *            the type of listener
	 * @see #addContentAssistListener(IContentAssistListener, int)
	 */
	void removeContentAssistListener(IContentAssistListener listener, int type)
	{
		fListeners[type] = null;

		if (getNumberOfListeners() == 0)
		{

			if (fCloser != null)
			{
				fCloser.uninstall();
				fCloser = null;
			}

			uninstallVerifyKeyListener();
			if (fContentAssistSubjectControlAdapter != null)
			{
				fContentAssistSubjectControlAdapter.setEventConsumer(null);
			}
		}

		releaseWidgetToken(type);
	}

	/**
	 * Uninstall the key listener from the text viewer's widget.
	 * 
	 * @since 3.0
	 */
	private void uninstallVerifyKeyListener()
	{
		if (fVerifyKeyListenerHooked)
		{
			if (Helper.okToUse(fContentAssistSubjectControlAdapter.getControl()))
			{
				fContentAssistSubjectControlAdapter.removeVerifyKeyListener(fInternalListener);
			}
			fVerifyKeyListenerHooked = false;
		}
	}

	/**
	 * Returns the number of listeners.
	 * 
	 * @return the number of listeners
	 * @since 2.0
	 */
	private int getNumberOfListeners()
	{
		int count = 0;
		for (int i = 0; i <= CONTEXT_INFO_POPUP; i++)
		{
			if (fListeners[i] != null)
			{
				++count;
			}
		}
		return count;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistant#showPossibleCompletions()
	 */
	public String showPossibleCompletions()
	{
		promoteKeyListener();
		if (fIsPrefixCompletionEnabled)
		{
			return fProposalPopup.incrementalComplete();
		}
		return fProposalPopup.showProposals(false);
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistantExtension#completePrefix()
	 */
	public String completePrefix()
	{
		promoteKeyListener();
		return fProposalPopup.incrementalComplete();
	}

	/**
	 * Callback to signal this code assistant that the presentation of the possible completions has been stopped.
	 * 
	 * @since 2.1
	 */
	protected void possibleCompletionsClosed()
	{
		storeCompletionProposalPopupSize();
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistant#showContextInformation()
	 */
	public String showContextInformation()
	{
		promoteKeyListener();
		if (fContextInfoPopup != null)
		{
			return fContextInfoPopup.showContextProposals(false);
		}
		return null;
	}

	/**
	 * Callback to signal this code assistant that the presentation of the context information has been stopped.
	 * 
	 * @since 2.1
	 */
	protected void contextInformationClosed()
	{
	}

	/**
	 * Requests that the specified context information to be shown.
	 * 
	 * @param contextInformation
	 *            the context information to be shown
	 * @param offset
	 *            the offset to which the context information refers to
	 * @since 2.0
	 */
	void showContextInformation(IContextInformation contextInformation, int offset)
	{
		if (fContextInfoPopup != null)
		{
			fContextInfoPopup.showContextInformation(contextInformation, offset);
		}
	}

	/**
	 * Returns the current code assist error message.
	 * 
	 * @return an error message or <code>null</code> if no error has occurred
	 */
	String getErrorMessage()
	{
		return fLastErrorMessage;
	}

	/**
	 * Returns the code assist processor for the content type of the specified document position.
	 * 
	 * @param viewer
	 *            the text viewer
	 * @param offset
	 *            a offset within the document
	 * @return a content-assist processor or <code>null</code> if none exists
	 * @since 3.0
	 */
	private IContentAssistProcessor getProcessor(ITextViewer viewer, int offset)
	{
		try
		{

			IDocument document = viewer.getDocument();
			String type = TextUtilities.getContentType(document, getDocumentPartitioning(), offset, true);

			return getContentAssistProcessor(type);

		}
		catch (BadLocationException x)
		{
		}

		return null;
	}

	/**
	 * Returns the code assist processor for the content type of the specified document position.
	 * 
	 * @param contentAssistSubjectControl
	 *            the code assist subject control
	 * @param offset
	 *            a offset within the document
	 * @return a content-assist processor or <code>null</code> if none exists
	 * @since 3.0
	 */
	private IContentAssistProcessor getProcessor(IContentAssistSubjectControl contentAssistSubjectControl, int offset)
	{
		try
		{

			IDocument document = contentAssistSubjectControl.getDocument();
			String type;
			if (document != null)
			{
				type = TextUtilities.getContentType(document, getDocumentPartitioning(), offset, true);
			}
			else
			{
				type = IDocument.DEFAULT_CONTENT_TYPE;
			}

			return getContentAssistProcessor(type);

		}
		catch (BadLocationException x)
		{
		}

		return null;
	}

	/**
	 * Returns an array of completion proposals computed based on the specified document position. The position is used
	 * to determine the appropriate code assist processor to invoke.
	 * 
	 * @param contentAssistSubjectControl
	 *            the code assist subject control
	 * @param offset
	 *            a document offset
	 * @return an array of completion proposals
	 * @see IContentAssistProcessor#computeCompletionProposals(ITextViewer, int)
	 * @since 3.0
	 */
	ICompletionProposal[] computeCompletionProposals(IContentAssistSubjectControl contentAssistSubjectControl,
			int offset, char activationChar)
	{
		fLastErrorMessage = null;
		fUserAgentColumnCount = 0;

		ICompletionProposal[] result = null;
		IContentAssistProcessor processor = getProcessor(contentAssistSubjectControl, offset);

		if (processor != null)
		{
			if (processor instanceof ISubjectControlContentAssistProcessor)
			{
				result = ((ISubjectControlContentAssistProcessor) processor).computeCompletionProposals(
						contentAssistSubjectControl, offset);
				fLastErrorMessage = processor.getErrorMessage();
			}
			if (processor instanceof ICommonContentAssistProcessor)
			{
				String[] ids = ((ICommonContentAssistProcessor) processor).getActiveUserAgentIds();

				fUserAgentColumnCount = (ids != null) ? ids.length : 0;
			}
		}

		return result;
	}

	/**
	 * Return the number of columns needed to display all user agents
	 * 
	 * @return Returns the column count
	 */
	public int getUserAgentColumnCount()
	{
		return fUserAgentColumnCount;
	}

	/**
	 * Returns an array of completion proposals computed based on the specified document position. The position is used
	 * to determine the appropriate code assist processor to invoke.
	 * 
	 * @param viewer
	 *            the viewer for which to compute the proposals
	 * @param offset
	 *            a document offset
	 * @param autoActivated
	 *            determines whether we were autoActivated or not
	 * @return an array of completion proposals
	 * @see IContentAssistProcessor#computeCompletionProposals(ITextViewer, int)
	 */
	ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset, char activationChar,
			boolean autoActivated)
	{
		fLastErrorMessage = null;
		fUserAgentColumnCount = 0;

		ICompletionProposal[] result = null;
		IContentAssistProcessor processor = this.getProcessor(viewer, offset);

		if (processor != null)
		{
			if (processor instanceof ICommonContentAssistProcessor)
			{
				ICommonContentAssistProcessor commonProcessor = (ICommonContentAssistProcessor) processor;

				result = commonProcessor.computeCompletionProposals(viewer, offset, activationChar, autoActivated);

				String[] ids = ((ICommonContentAssistProcessor) processor).getActiveUserAgentIds();

				fUserAgentColumnCount = (ids != null) ? ids.length : 0;
			}
			else
			{
				result = processor.computeCompletionProposals(viewer, offset);
			}

			fLastErrorMessage = processor.getErrorMessage();
		}

		return result;
	}

	/**
	 * Returns an array of context information objects computed based on the specified document position. The position
	 * is used to determine the appropriate code assist processor to invoke.
	 * 
	 * @param viewer
	 *            the viewer for which to compute the context information
	 * @param offset
	 *            a document offset
	 * @return an array of context information objects
	 * @see IContentAssistProcessor#computeContextInformation(ITextViewer, int)
	 */
	IContextInformation[] computeContextInformation(ITextViewer viewer, int offset)
	{
		fLastErrorMessage = null;

		IContextInformation[] result = null;

		IContentAssistProcessor p = getProcessor(viewer, offset);
		if (p != null)
		{
			result = p.computeContextInformation(viewer, offset);
			fLastErrorMessage = p.getErrorMessage();
		}

		return result;
	}

	/**
	 * Returns an array of context information objects computed based on the specified document position. The position
	 * is used to determine the appropriate code assist processor to invoke.
	 * 
	 * @param contentAssistSubjectControl
	 *            the code assist subject control
	 * @param offset
	 *            a document offset
	 * @return an array of context information objects
	 * @see IContentAssistProcessor#computeContextInformation(ITextViewer, int)
	 * @since 3.0
	 */
	IContextInformation[] computeContextInformation(IContentAssistSubjectControl contentAssistSubjectControl, int offset)
	{
		fLastErrorMessage = null;

		IContextInformation[] result = null;

		IContentAssistProcessor p = getProcessor(contentAssistSubjectControl, offset);
		if (p instanceof ISubjectControlContentAssistProcessor)
		{
			result = ((ISubjectControlContentAssistProcessor) p).computeContextInformation(contentAssistSubjectControl,
					offset);
			fLastErrorMessage = p.getErrorMessage();
		}

		return result;
	}

	/**
	 * Returns the context information validator that should be used to determine when the currently displayed context
	 * information should be dismissed. The position is used to determine the appropriate code assist processor to
	 * invoke.
	 * 
	 * @param viewer
	 *            the text viewer
	 * @param offset
	 *            a document offset
	 * @return an validator
	 * @see IContentAssistProcessor#getContextInformationValidator()
	 * @since 3.0
	 */
	IContextInformationValidator getContextInformationValidator(ITextViewer viewer, int offset)
	{
		IContentAssistProcessor p = getProcessor(viewer, offset);
		return p != null ? p.getContextInformationValidator() : null;
	}

	/**
	 * Returns the context information validator that should be used to determine when the currently displayed context
	 * information should be dismissed. The position is used to determine the appropriate code assist processor to
	 * invoke.
	 * 
	 * @param contentAssistSubjectControl
	 *            the code assist subject control
	 * @param offset
	 *            a document offset
	 * @return an validator
	 * @see IContentAssistProcessor#getContextInformationValidator()
	 * @since 3.0
	 */
	IContextInformationValidator getContextInformationValidator(
			IContentAssistSubjectControl contentAssistSubjectControl, int offset)
	{
		IContentAssistProcessor p = getProcessor(contentAssistSubjectControl, offset);
		return p != null ? p.getContextInformationValidator() : null;
	}

	/**
	 * Returns the context information presenter that should be used to display context information. The position is
	 * used to determine the appropriate code assist processor to invoke.
	 * 
	 * @param viewer
	 *            the text viewer
	 * @param offset
	 *            a document offset
	 * @return a presenter
	 * @since 2.0
	 */
	IContextInformationPresenter getContextInformationPresenter(ITextViewer viewer, int offset)
	{
		IContextInformationValidator validator = getContextInformationValidator(viewer, offset);
		if (validator instanceof IContextInformationPresenter)
		{
			return (IContextInformationPresenter) validator;
		}
		return null;
	}

	/**
	 * Returns the context information presenter that should be used to display context information. The position is
	 * used to determine the appropriate code assist processor to invoke.
	 * 
	 * @param contentAssistSubjectControl
	 *            the code assist subject control
	 * @param offset
	 *            a document offset
	 * @return a presenter
	 * @since 3.0
	 */
	IContextInformationPresenter getContextInformationPresenter(
			IContentAssistSubjectControl contentAssistSubjectControl, int offset)
	{
		IContextInformationValidator validator = getContextInformationValidator(contentAssistSubjectControl, offset);
		if (validator instanceof IContextInformationPresenter)
		{
			return (IContextInformationPresenter) validator;
		}
		return null;
	}

	/**
	 * Returns the characters which when typed by the user should automatically initiate proposing completions. The
	 * position is used to determine the appropriate code assist processor to invoke.
	 * 
	 * @param contentAssistSubjectControl
	 *            the code assist subject control
	 * @param offset
	 *            a document offset
	 * @return the auto activation characters
	 * @see IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 * @since 3.0
	 */
	char[] getCompletionProposalAutoActivationCharacters(IContentAssistSubjectControl contentAssistSubjectControl,
			int offset)
	{
		IContentAssistProcessor p = getProcessor(contentAssistSubjectControl, offset);
		return p != null ? p.getCompletionProposalAutoActivationCharacters() : null;
	}

	/**
	 * Returns the characters which when typed by the user should automatically initiate proposing completions. The
	 * position is used to determine the appropriate code assist processor to invoke.
	 * 
	 * @param viewer
	 *            the text viewer
	 * @param offset
	 *            a document offset
	 * @return the auto activation characters
	 * @see IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	char[] getCompletionProposalAutoActivationCharacters(ITextViewer viewer, int offset)
	{
		IContentAssistProcessor p = getProcessor(viewer, offset);
		return p != null ? p.getCompletionProposalAutoActivationCharacters() : null;
	}

	/**
	 * Returns the characters which when typed by the user should automatically initiate the presentation of context
	 * information. The position is used to determine the appropriate code assist processor to invoke.
	 * 
	 * @param viewer
	 *            the text viewer
	 * @param offset
	 *            a document offset
	 * @return the auto activation characters
	 * @see IContentAssistProcessor#getContextInformationAutoActivationCharacters()
	 * @since 3.0
	 */
	char[] getContextInformationAutoActivationCharacters(ITextViewer viewer, int offset)
	{
		IContentAssistProcessor p = getProcessor(viewer, offset);
		return p != null ? p.getContextInformationAutoActivationCharacters() : null;
	}

	/**
	 * Returns the characters which when typed by the user should automatically initiate the presentation of context
	 * information. The position is used to determine the appropriate code assist processor to invoke.
	 * 
	 * @param contentAssistSubjectControl
	 *            the code assist subject control
	 * @param offset
	 *            a document offset
	 * @return the auto activation characters
	 * @see IContentAssistProcessor#getContextInformationAutoActivationCharacters()
	 * @since 3.0
	 */
	char[] getContextInformationAutoActivationCharacters(IContentAssistSubjectControl contentAssistSubjectControl,
			int offset)
	{
		IContentAssistProcessor p = getProcessor(contentAssistSubjectControl, offset);
		return p != null ? p.getContextInformationAutoActivationCharacters() : null;
	}

	/**
	 * @see org.eclipse.jface.text.IWidgetTokenKeeper#requestWidgetToken(org.eclipse.jface.text.IWidgetTokenOwner)
	 */
	public boolean requestWidgetToken(IWidgetTokenOwner owner)
	{
		return false;
	}

	/**
	 * @see org.eclipse.jface.text.IWidgetTokenKeeperExtension#requestWidgetToken(org.eclipse.jface.text.IWidgetTokenOwner,
	 *      int)
	 */
	public boolean requestWidgetToken(IWidgetTokenOwner owner, int priority)
	{
		if (priority > WIDGET_PRIORITY)
		{
			hide();
			return true;
		}
		return false;
	}

	/**
	 * @see org.eclipse.jface.text.IWidgetTokenKeeperExtension#setFocus(org.eclipse.jface.text.IWidgetTokenOwner)
	 */
	public boolean setFocus(IWidgetTokenOwner owner)
	{
		if (fProposalPopup != null)
		{
			fProposalPopup.setFocus();
			return fProposalPopup.hasFocus();
		}
		return false;
	}

	/**
	 * Hides any open pop-ups.
	 * 
	 * @since 3.0
	 */
	public void hide()
	{
		if (fProposalPopup != null)
		{
			fProposalPopup.hide();
		}

		if (fContextInfoPopup != null)
		{
			fContextInfoPopup.hide();
		}
	}

	// ------ control's size handling dialog settings ------

	/**
	 * Tells this information control manager to open the information control with the values contained in the given
	 * dialog settings and to store the control's last valid size in the given dialog settings.
	 * <p>
	 * Note: This API is only valid if the information control implements
	 * {@link org.eclipse.jface.text.IInformationControlExtension3}. Not following this restriction will later result in
	 * an {@link UnsupportedOperationException}.
	 * </p>
	 * <p>
	 * The constants used to store the values are:
	 * <ul>
	 * <li>{@link ContentAssistant#STORE_SIZE_X}</li>
	 * <li>{@link ContentAssistant#STORE_SIZE_Y}</li>
	 * </ul>
	 * </p>
	 * 
	 * @param dialogSettings
	 * @since 3.0
	 */
	public void setRestoreCompletionProposalSize(IDialogSettings dialogSettings)
	{
		fDialogSettings = dialogSettings;
	}

	/**
	 * Stores the code assist pop-up's size.
	 */
	protected void storeCompletionProposalPopupSize()
	{
		if (fDialogSettings == null || fProposalPopup == null)
		{
			return;
		}

		Point size = fProposalPopup.getSize();
		if (size == null)
		{
			return;
		}

		fDialogSettings.put(STORE_SIZE_X, size.x);
		fDialogSettings.put(STORE_SIZE_Y, size.y);
	}

	/**
	 * Restores the code assist pop-up's size.
	 * 
	 * @return the stored size
	 * @since 3.0
	 */
	protected Point restoreCompletionProposalPopupSize()
	{
		if (fDialogSettings == null)
		{
			return null;
		}

		Point size = new Point(-1, -1);

		try
		{
			size.x = fDialogSettings.getInt(STORE_SIZE_X);
			size.y = fDialogSettings.getInt(STORE_SIZE_Y);
		}
		catch (NumberFormatException ex)
		{
			size.x = -1;
			size.y = -1;
		}

		// sanity check
		if (size.x == -1 && size.y == -1)
		{
			return null;
		}

		Rectangle maxBounds = null;
		if (fContentAssistSubjectControl != null && !fContentAssistSubjectControl.getControl().isDisposed())
		{
			maxBounds = fContentAssistSubjectControl.getControl().getDisplay().getBounds();
		}
		else
		{
			// fallback
			Display display = Display.getCurrent();
			if (display == null)
			{
				display = Display.getDefault();
			}
			if (display != null && !display.isDisposed())
			{
				maxBounds = display.getBounds();
			}
		}

		if (size.x > -1 && size.y > -1)
		{
			if (maxBounds != null)
			{
				size.x = Math.min(size.x, maxBounds.width);
				size.y = Math.min(size.y, maxBounds.height);
			}

			// Enforce an absolute minimal size
			size.x = Math.max(size.x, 30);
			size.y = Math.max(size.y, 30);
		}

		return size;
	}

	/**
	 * Sets the prefix completion property. If enabled, code assist delegates completion to prefix completion.
	 * 
	 * @param enabled
	 *            <code>true</code> to enable prefix completion, <code>false</code> to disable
	 */
	public void enablePrefixCompletion(boolean enabled)
	{
		fIsPrefixCompletionEnabled = enabled;
	}

	/**
	 * Returns whether the code assistant proposal popup has the focus.
	 * 
	 * @return <code>true</code> if the proposal popup has the focus
	 * @since 3.0
	 */
	public boolean hasProposalPopupFocus()
	{
		return fProposalPopup.hasFocus();
	}

	/**
	 * Returns whether proposal popup is active.
	 * 
	 * @return <code>true</code> if the proposal popup is active, <code>false</code> otherwise
	 * @since 3.4
	 */
	protected boolean isProposalPopupActive()
	{
		return fProposalPopup != null && fProposalPopup.isActive();
	}

	/**
	 * Returns whether the context information popup is active.
	 * 
	 * @return <code>true</code> if the context information popup is active, <code>false</code> otherwise
	 * @since 3.4
	 */
	protected boolean isContextInfoPopupActive()
	{
		return fContextInfoPopup != null && fContextInfoPopup.isActive();
	}

	/**
	 * Set the color of the proposal selector
	 * 
	 * @param color
	 */
	public void setProposalSelectorSelectionColor(Color color)
	{
		fProposalSelectorSelectionColor = color;
	}

	/**
	 * Get the color of the proposal selector
	 * 
	 * @return
	 */
	Color getProposalSelectorSelectionColor()
	{
		return fProposalSelectorSelectionColor;
	}
}
