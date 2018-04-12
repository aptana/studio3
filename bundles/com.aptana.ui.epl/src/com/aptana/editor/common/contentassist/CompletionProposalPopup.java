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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.contentassist.IContentAssistSubjectControl;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IEditingSupport;
import org.eclipse.jface.text.IEditingSupportRegistry;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension4;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.epl.UIEplPlugin;

/**
 * This class is used to present proposals to the user. If additional information exists for a proposal, then selecting
 * that proposal will result in the information being displayed in a secondary window.
 * 
 * @see org.eclipse.jface.text.contentassist.ICompletionProposal
 */
public class CompletionProposalPopup implements IContentAssistListener
{
	private static final String COM_APTANA_EDITOR_COMMON = "com.aptana.editor.common"; //$NON-NLS-1$

	/**
	 * Set to <code>true</code> to use a Table with SWT.VIRTUAL. XXX: This is a workaround for:
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=90321 More details see also:
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=98585#c36
	 * 
	 * @since 3.1
	 */
	private static final boolean USE_VIRTUAL = !"motif".equals(SWT.getPlatform()); //$NON-NLS-1$

	/**
	 * PROPOSAL_ITEM_HEIGHT
	 */
	public static final int PROPOSAL_ITEMS_VISIBLE = 7;

	/**
	 * MAX_PROPOSAL_COLUMN_WIDTH
	 */
	public static final int MAX_PROPOSAL_COLUMN_WIDTH = 500;

	/**
	 * MAX_LOCATION_COLUMN_WIDTH
	 */
	public static final int MAX_LOCATION_COLUMN_WIDTH = 200;

	/**
	 * MIN_PROPOSAL_COLUMN_WIDTH
	 */
	public static final int MIN_PROPOSAL_COLUMN_WIDTH = 100;

	/**
	 * ProposalSelectionListener
	 * 
	 * @author Ingo Muschenetz
	 */
	private final class ProposalSelectionListener implements KeyListener
	{
		/**
		 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
		 */
		public void keyPressed(KeyEvent e)
		{
			if (!Helper.okToUse(fProposalShell))
			{
				return;
			}

			if (e.character == 0 && e.keyCode == SWT.MOD1)
			{
				// http://dev.eclipse.org/bugs/show_bug.cgi?id=34754
				int index = fProposalTable.getSelectionIndex();
				if (index >= 0)
				{
					selectProposal(index, true, true);
				}
				// else
				// {
				// fProposalTable.setTopIndex(0);
				// }
			}
		}

		/**
		 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
		 */
		public void keyReleased(KeyEvent e)
		{
			if (!Helper.okToUse(fProposalShell))
			{
				return;
			}

			if (e.character == 0 && e.keyCode == SWT.MOD1)
			{
				// http://dev.eclipse.org/bugs/show_bug.cgi?id=34754
				int index = fProposalTable.getSelectionIndex();
				if (index >= 0)
				{
					selectProposal(index, false, true);
				}
				// else
				// {
				// fProposalTable.setTopIndex(0);
				// }
			}
		}
	}

	/** The associated text viewer. */
	private ITextViewer fViewer;
	/** The associated code assistant. */
	private ContentAssistant fContentAssistant;
	/** The used additional info controller. */
	private AdditionalInfoController fAdditionalInfoController;
	/** The closing strategy for this completion proposal popup. */
	private PopupCloser fPopupCloser = new PopupCloser();
	/** The popup shell. */
	private Shell fProposalShell;
	/** The proposal table. */
	private Table fProposalTable;
	/** Indicates whether a completion proposal is being inserted. */
	private boolean fInserting = false;
	/** The key listener to control navigation. */
	private ProposalSelectionListener fKeyListener;
	/** List of document events used for filtering proposals. */
	private List<DocumentEvent> fDocumentEvents = new ArrayList<DocumentEvent>();
	/** Listener filling the document event queue. */
	private IDocumentListener fDocumentListener;
	/** Reentrance count for filtered proposals. */
	private long fInvocationCounter = 0;
	/** The filter list of proposals. */
	private ICompletionProposal[] fFilteredProposals;
	/** The computed list of proposals. */
	private ICompletionProposal[] fComputedProposals;
	/** The offset for which the proposals have been computed. */
	private int fInvocationOffset;
	/** The offset for which the computed proposals have been filtered. */
	private int fFilterOffset;
	/** The key last pressed to trigger activation * */
	private char fActivationKey;

	/** Do we insert the selected proposal on tab? * */
	private boolean _insertOnTab;

	/**
	 * The most recently selected proposal.
	 * 
	 * @since 3.0
	 */
	private ICompletionProposal fLastProposal;
	/**
	 * The code assist subject control. This replaces <code>fViewer</code>
	 * 
	 * @since 3.0
	 */
	private IContentAssistSubjectControl fContentAssistSubjectControl;
	/**
	 * The code assist subject control adapter. This replaces <code>fViewer</code>
	 * 
	 * @since 3.0
	 */
	private ContentAssistSubjectControlAdapter fContentAssistSubjectControlAdapter;
	/**
	 * Remembers the size for this completion proposal popup.
	 * 
	 * @since 3.0
	 */
	private Point fSize;
	/**
	 * Editor helper that communicates that the completion proposal popup may have focus while the 'logical focus' is
	 * still with the editor.
	 * 
	 * @since 3.1
	 */
	private IEditingSupport fFocusHelper;
	/**
	 * Set to true by {@link #computeFilteredProposals(int, DocumentEvent)} if the returned proposals are a subset of
	 * {@link #fFilteredProposals}, <code>false</code> if not.
	 * 
	 * @since 3.1
	 */
	private boolean fIsFilteredSubset;

	private IEclipsePreferences projectScopeNode;

	private IEclipsePreferences instanceScopeNode;

	private IPreferenceChangeListener prefListener;

	/**
	 * Creates a new completion proposal popup for the given elements.
	 * 
	 * @param contentAssistant
	 *            the code assistant feeding this popup
	 * @param viewer
	 *            the viewer on top of which this popup appears
	 * @param infoController
	 *            the information control collaborating with this popup
	 * @since 2.0
	 */
	public CompletionProposalPopup(ContentAssistant contentAssistant, ITextViewer viewer,
			AdditionalInfoController infoController)
	{
		fContentAssistant = contentAssistant;
		fViewer = viewer;
		fAdditionalInfoController = infoController;
		fContentAssistSubjectControlAdapter = new ContentAssistSubjectControlAdapter(fViewer);
	}

	/**
	 * Creates a new completion proposal popup for the given elements.
	 * 
	 * @param contentAssistant
	 *            the code assistant feeding this popup
	 * @param contentAssistSubjectControl
	 *            the code assist subject control on top of which this popup appears
	 * @param infoController
	 *            the information control collaborating with this popup
	 * @since 3.0
	 */
	public CompletionProposalPopup(ContentAssistant contentAssistant,
			IContentAssistSubjectControl contentAssistSubjectControl, AdditionalInfoController infoController)
	{
		fContentAssistant = contentAssistant;
		fContentAssistSubjectControl = contentAssistSubjectControl;
		fAdditionalInfoController = infoController;
		fContentAssistSubjectControlAdapter = new ContentAssistSubjectControlAdapter(fContentAssistSubjectControl);
	}

	/**
	 * Computes and presents completion proposals. The flag indicates whether this call has be made out of an auto
	 * activation context.
	 * 
	 * @param autoActivated
	 *            <code>true</code> if auto activation context
	 * @return an error message or <code>null</code> in case of no error
	 */
	public String showProposals(final boolean autoActivated)
	{
		if (fKeyListener == null)
		{
			fKeyListener = new ProposalSelectionListener();
		}

		final Control control = fContentAssistSubjectControlAdapter.getControl();

		if (control != null && !control.isDisposed())
		{

			// add the listener before computing the proposals so we don't move the caret
			// when the user types fast.
			fContentAssistSubjectControlAdapter.addKeyListener(fKeyListener);

			BusyIndicator.showWhile(control.getDisplay(), new Runnable()
			{
				public void run()
				{
					fInvocationOffset = fContentAssistSubjectControlAdapter.getSelectedRange().x;
					fFilterOffset = fInvocationOffset;
					fComputedProposals = computeProposals(fInvocationOffset, autoActivated);
					IDocument doc = fContentAssistSubjectControlAdapter.getDocument();
					DocumentEvent initial = new DocumentEvent(doc, fInvocationOffset, 0, StringUtil.EMPTY);
					fComputedProposals = filterProposals(fComputedProposals, doc, fInvocationOffset, initial);

					int count = (fComputedProposals == null ? 0 : fComputedProposals.length);

					// If we don't have any proposals, and we've manually asked for proposals, show "no proposals"
					if (!autoActivated && count == 0)
					{
						fComputedProposals = createNoProposal();
						count = fComputedProposals.length;
					}

					if (count == 0)
					{
						hide();
					}
					else if (count == 1 && !autoActivated && canAutoInsert(fComputedProposals[0]))
					{
						insertProposal(fComputedProposals[0], (char) 0, 0, fInvocationOffset);
						hide();
					}
					else
					{
						createPopup();
					}
				}
			});
		}

		return getErrorMessage();
	}

	/**
	 * Create the "no proposals" proposal
	 * 
	 * @return
	 */
	private ICompletionProposal[] createNoProposal()
	{
		fEmptyProposal.fOffset = fFilterOffset;
		fEmptyProposal.fDisplayString = fEmptyMessage != null ? fEmptyMessage : JFaceTextMessages
				.getString("CompletionProposalPopup.no_proposals"); //$NON-NLS-1$
		modifySelection(-1, -1); // deselect everything
		return new ICompletionProposal[] { fEmptyProposal };
	}

	/**
	 * Returns the completion proposal available at the given offset of the viewer's document. Delegates the work to the
	 * code assistant.
	 * 
	 * @param offset
	 *            the offset
	 * @param autoActivated
	 * @return the completion proposals available at this offset
	 */
	private ICompletionProposal[] computeProposals(int offset, boolean autoActivated)
	{
		if (fContentAssistSubjectControl != null)
		{
			return fContentAssistant.computeCompletionProposals(fContentAssistSubjectControl, offset, fActivationKey);
		}
		return fContentAssistant.computeCompletionProposals(fViewer, offset, fActivationKey, autoActivated);
	}

	/**
	 * Returns the error message.
	 * 
	 * @return the error message
	 */
	private String getErrorMessage()
	{
		return fContentAssistant.getErrorMessage();
	}

	/**
	 * Creates the proposal selector.
	 */
	private void createProposalSelector()
	{
		Control control = fContentAssistSubjectControlAdapter.getControl();
		if (Helper.okToUse(fProposalShell))
		{
			// Custom code to force colors again in case theme changed...
			// Not sure why we don't set background for all WS here
			if (!"carbon".equals(SWT.getPlatform())) //$NON-NLS-1$
			{
				fProposalShell.setBackground(getForegroundColor(control));
			}

			Color c = getBackgroundColor(control);
			fProposalTable.setBackground(c);

			c = getForegroundColor(control);
			fProposalTable.setForeground(c);
			return;
		}

		fProposalShell = new Shell(control.getShell(), SWT.ON_TOP | SWT.RESIZE);
		fProposalShell.setFont(JFaceResources.getDefaultFont());
		if (USE_VIRTUAL)
		{
			fProposalTable = new Table(fProposalShell, SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);

			Listener listener = new Listener()
			{
				public void handleEvent(Event event)
				{
					handleSetData(event);
				}
			};
			fProposalTable.addListener(SWT.SetData, listener);
		}
		else
		{
			fProposalTable = new Table(fProposalShell, SWT.H_SCROLL | SWT.V_SCROLL);
		}

		fProposalShell.addControlListener(new ControlAdapter()
		{
			public void controlResized(ControlEvent e)
			{
				TableColumn[] columns = fProposalTable.getColumns();
				int currentSize = 0;
				for (int i = 1; i < columns.length; i++)
				{
					currentSize += columns[i].getWidth();
				}

				Rectangle area = fProposalShell.getClientArea();
				int width = getTableWidth();
				TableColumn column1 = fProposalTable.getColumn(0);

				// take up any remaining space for first column
				fProposalTable.setSize(area.width, area.height);

				int col1Width = width - currentSize;

				// 1st column can't be smaller than a default size;
				col1Width = Math.max(col1Width, MIN_PROPOSAL_COLUMN_WIDTH);
				column1.setWidth(col1Width);
			}
		});

		_insertOnTab = true; // store.getBoolean(IPreferenceConstants.INSERT_ON_TAB);

		// Here we add custom columns
		new TableColumn(fProposalTable, SWT.LEFT);

		for (int i = 0; i < fContentAssistant.getUserAgentColumnCount(); i++)
		{
			TableColumn tc = new TableColumn(fProposalTable, SWT.LEFT);
			tc.setWidth(20);
		}

		new TableColumn(fProposalTable, SWT.LEFT);
		// end custom columns

		fProposalTable.setLocation(0, 0);
		if (fAdditionalInfoController != null)
		{
			fAdditionalInfoController.setSizeConstraints(40, 20, true, false);
		}

		// Custom code: We set margins to 1 so we get a border
		GridLayout layout = new GridLayout();
		layout.marginWidth = 1;
		layout.marginHeight = 1;
		fProposalShell.setLayout(layout);

		GridData data = new GridData(GridData.FILL_BOTH);

		Point size = fContentAssistant.restoreCompletionProposalPopupSize();
		if (size != null)
		{
			fProposalTable.setLayoutData(data);
			fProposalShell.setSize(size);
		}
		else
		{
			int height = fProposalTable.getItemHeight() * CompletionProposalPopup.PROPOSAL_ITEMS_VISIBLE;
			// use golden ratio as default aspect ratio
			final double aspectRatio = (1 + Math.sqrt(5)) / 2;
			int width = (int) (height * aspectRatio);
			Rectangle trim = fProposalTable.computeTrim(0, 0, width, height);
			data.heightHint = trim.height;
			data.widthHint = trim.width;
			fProposalTable.setLayoutData(data);
			fProposalShell.pack();
		}

		fProposalShell.addControlListener(new ControlListener()
		{

			public void controlMoved(ControlEvent e)
			{
			}

			public void controlResized(ControlEvent e)
			{
				if (fAdditionalInfoController != null)
				{
					// reset the cached resize constraints
					fAdditionalInfoController.setSizeConstraints(40, 20, true, false);
					fAdditionalInfoController.hideInformationControl();
					fAdditionalInfoController.handleTableSelectionChanged();
				}

				fSize = fProposalShell.getSize();
			}
		});

		// Custom code: not sure why we don't set background for all WS here
		if (!"carbon".equals(SWT.getPlatform())) //$NON-NLS-1$
		{
			fProposalShell.setBackground(getForegroundColor(control));
		}

		Color c = getBackgroundColor(control);
		fProposalTable.setBackground(c);

		c = getForegroundColor(control);
		fProposalTable.setForeground(c);

		// Custom code for overriding selection color
		Listener selectionOverride = new Listener()
		{
			public void handleEvent(Event event)
			{
				if ((event.detail & SWT.SELECTED) != 0)
				{
					GC gc = event.gc;
					Color oldBackground = gc.getBackground();

					Color sc = fContentAssistant.getProposalSelectorSelectionColor();
					if (sc == null)
						return;
					gc.setBackground(sc);
					gc.fillRectangle(event.x, event.y, event.width, event.height);
					gc.setBackground(oldBackground);

					event.detail &= ~SWT.SELECTED;
					event.detail &= ~SWT.BACKGROUND;

					gc.setForeground(getForegroundColor(fContentAssistSubjectControlAdapter.getControl()));
				}
			}
		};
		fProposalTable.addListener(SWT.EraseItem, selectionOverride);

		fProposalTable.addSelectionListener(new SelectionListener()
		{

			public void widgetSelected(SelectionEvent e)
			{
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
				selectProposalWithMask(e.stateMask);
				// This disposal on windows it because of focus issue when a select is manually select and the next time
				// the popup is show it has focus and so editor typing focus is lost
				if (Platform.OS_WIN32.equals(Platform.getOS()))
				{
					disposePopup();
				}
			}
		});
		fPopupCloser.install(fContentAssistant, fProposalTable, fAdditionalInfoController);
		// TISTUD-913: changed to the line above from 'fPopupCloser.install(fContentAssistant, fProposalTable);'

		installPreferenceListener();

		fProposalShell.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				unregister(); // but don't dispose the shell, since we're being called from its disposal event!
			}
		});

		fProposalTable.setHeaderVisible(false);

		// addCommandSupport(fProposalTable);
	}

	/**
	 * Set up the preference listener
	 */
	private void installPreferenceListener()
	{
		prefListener = new IPreferenceChangeListener()
		{
			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (event.getKey().equals(IPreferenceConstants.USER_AGENT_PREFERENCE))
				{
					if (Helper.okToUse(fProposalShell))
					{
						fProposalShell.dispose();
					}
					else
					{
						if (projectScopeNode != null)
						{
							projectScopeNode.removePreferenceChangeListener(this);
						}
						if (instanceScopeNode != null)
						{
							instanceScopeNode.removePreferenceChangeListener(this);
						}
					}
				}
			}
		};

		projectScopeNode = getProjectScopeNode();
		if (projectScopeNode != null)
		{
			projectScopeNode.addPreferenceChangeListener(prefListener);
		}
		instanceScopeNode = InstanceScope.INSTANCE.getNode(COM_APTANA_EDITOR_COMMON);
		if (instanceScopeNode != null)
		{
			instanceScopeNode.addPreferenceChangeListener(prefListener);
		}
	}

	/**
	 * Returns a project scope node, or null.
	 */
	private IEclipsePreferences getProjectScopeNode()
	{
		// Locate the project. What a joy!...
		if (fViewer instanceof IAdaptable)
		{
			ITextEditor editor = (ITextEditor) ((IAdaptable) fViewer).getAdapter(ITextEditor.class);
			if (editor != null)
			{
				IEditorInput editorInput = editor.getEditorInput();
				if (editorInput != null)
				{
					IResource resource = (IResource) editorInput.getAdapter(IResource.class);
					if (resource != null)
					{
						IProject project = resource.getProject();
						return new ProjectScope(project).getNode(COM_APTANA_EDITOR_COMMON);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns the background color to use.
	 * 
	 * @param control
	 *            the control to get the display from
	 * @return the background color
	 * @since 3.2
	 */
	private Color getBackgroundColor(Control control)
	{
		Color c = fContentAssistant.getProposalSelectorBackground();
		if (c == null)
			c = JFaceResources.getColorRegistry().get(JFacePreferences.CONTENT_ASSIST_BACKGROUND_COLOR);
		return c;
	}

	/**
	 * Returns the foreground color to use.
	 * 
	 * @param control
	 *            the control to get the display from
	 * @return the foreground color
	 * @since 3.2
	 */
	private Color getForegroundColor(Control control)
	{
		Color c = fContentAssistant.getProposalSelectorForeground();
		if (c == null)
			c = JFaceResources.getColorRegistry().get(JFacePreferences.CONTENT_ASSIST_FOREGROUND_COLOR);
		return c;
	}

	/**
	 * @since 3.1
	 */
	int defaultIndex = -1;
	private char fLastKeyPressed;

	/**
	 * The (reusable) empty proposal.
	 * 
	 * @since 3.2
	 */
	private final EmptyProposal fEmptyProposal = new EmptyProposal();
	/**
	 * The text for the empty proposal, or <code>null</code> to use the default text.
	 * 
	 * @since 3.2
	 */
	private String fEmptyMessage = null;

	private void handleSetData(Event event)
	{
		TableItem item = (TableItem) event.item;
		int index = fProposalTable.indexOf(item);

		boolean outputRelevance = IdeLog.isInfoEnabled(UIEplPlugin.getDefault(), IUiEplScopes.RELEVANCE);

		if (0 <= index && index < fFilteredProposals.length)
		{
			ICompletionProposal current = fFilteredProposals[index];

			String entry = current.getDisplayString().trim();

			if (outputRelevance)
			{
				int relevance = 0;
				if (current instanceof ICommonCompletionProposal)
				{
					relevance = ((ICommonCompletionProposal) current).getRelevance();
				}
				entry += MessageFormat.format(
						JFaceTextMessages.getString("CompletionProposalPopup.RelevancePercentage"), relevance); //$NON-NLS-1$
			}

			item.setImage(current.getImage());
			item.setText(0, entry);

			item.setData(current);

			if (current instanceof ICommonCompletionProposal)
			{
				ICommonCompletionProposal proposal = (ICommonCompletionProposal) current;
				String location = proposal.getFileLocation();
				Image[] images = proposal.getUserAgentImages();
				int userAgentCount = fContentAssistant.getUserAgentColumnCount();

				if (images != null)
				{
					for (int j = 0; j < images.length; j++)
					{
						Image image = images[j];
						item.setImage(j + 1, image);
					}
				}
				else
				{
					for (int j = 0; j < userAgentCount; j++)
					{
						item.setImage(j + 1, null);
					}
				}

				if (userAgentCount > 0)
				{
					item.setText(userAgentCount + 1, " " + location); //$NON-NLS-1$
				}
				else
				{
					item.setText(userAgentCount + 1, location);
				}
			}
		}
		else
		{
			// this should not happen, but does on win32
		}
	}

	/**
	 * Resizes the table to match the internal items
	 */
	private void resizeTable(int objectColumn, int locationColumn)
	{
		// Try/catch is fix for LH where we are strangely getting an ArrayIndexOutOfBounds exception
		// Not entirely sure how it's happening: https://aptana.lighthouseapp.com/projects/35272/tickets/2017
		try
		{
			fProposalTable.setRedraw(false);
			int height = (fProposalTable.getItemHeight() * Math.min(fFilteredProposals.length, PROPOSAL_ITEMS_VISIBLE));

			if (fProposalTable.getHorizontalBar() != null)
			{
				height += fProposalTable.getHorizontalBar().getSize().y;
			}

			fProposalTable.setLayoutData(GridDataFactory.fillDefaults().hint(SWT.DEFAULT, height).grab(true, true)
					.create());
			for (int j = 1; j < fProposalTable.getColumnCount() - 1; j++)
			{
				// User agent images are 16px. Adding a few px for padding
				fProposalTable.getColumn(j).setWidth(22);
			}
			TableColumn lastColumn = fProposalTable.getColumn(fProposalTable.getColumnCount() - 1);
			lastColumn.setWidth(locationColumn);

			fProposalTable.getColumn(0).setWidth(objectColumn);
			fProposalTable.setRedraw(true);
			fProposalShell.pack(true);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			IdeLog.logError(UIEplPlugin.getDefault(),
					JFaceTextMessages.getString("CompletionProposalPopup.Error_Resizing_Popup"), e); //$NON-NLS-1$
		}
	}

	/**
	 * Gets the interior width of the CA proposal table
	 * 
	 * @return
	 */
	private int getTableWidth()
	{
		Rectangle area = fProposalShell.getClientArea();
		Point preferredSize = fProposalTable.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		int width = area.width - 2 * fProposalTable.getBorderWidth();
		if (preferredSize.y > area.height + fProposalTable.getHeaderHeight())
		{
			// Subtract the scrollbar width from the total column width
			// if a vertical scrollbar will be required
			if (fProposalTable.getVerticalBar() != null)
			{
				Point vBarSize = fProposalTable.getVerticalBar().getSize();
				width -= vBarSize.x;
			}
		}

		// We subtract an extra 2 pixels because it seems this calculation overestimates
		// the table width a tiny bit. This might be a calculatable value.
		width -= 2;

		return width;
	}

	/**
	 * Returns the proposal selected in the proposal selector.
	 * 
	 * @return the selected proposal
	 * @since 2.0
	 */
	private ICompletionProposal getSelectedProposal()
	{
		if (fProposalTable == null || fProposalTable.isDisposed())
		{
			return null;
		}
		int i = fProposalTable.getSelectionIndex();
		if (fFilteredProposals == null || i < 0 || i >= fFilteredProposals.length)
		{
			return null;
		}
		return fFilteredProposals[i];
	}

	/**
	 * Takes the selected proposal and applies it.
	 * 
	 * @param stateMask
	 *            the state mask
	 * @since 2.1
	 */
	private boolean selectProposalWithMask(int stateMask)
	{
		ICompletionProposal p = getSelectedProposal();
		hide();
		if (p == null)
		{
			return false;
		}

		insertProposal(p, (char) 0, stateMask, fContentAssistSubjectControlAdapter.getSelectedRange().x);
		return true;
	}

	/**
	 * Applies the given proposal at the given offset. The given character is the one that triggered the insertion of
	 * this proposal.
	 * 
	 * @param p
	 *            the completion proposal
	 * @param trigger
	 *            the trigger character
	 * @param stateMask
	 *            the state mask
	 * @param offset
	 *            the offset
	 * @since 2.1
	 */
	private void insertProposal(ICompletionProposal p, char trigger, int stateMask, final int offset)
	{

		fInserting = true;
		IRewriteTarget target = null;
		IEditingSupport helper = new IEditingSupport()
		{

			public boolean isOriginator(DocumentEvent event, IRegion focus)
			{
				return focus.getOffset() <= offset && focus.getOffset() + focus.getLength() >= offset;
			}

			public boolean ownsFocusShell()
			{
				return false;
			}

		};

		try
		{

			IDocument document = fContentAssistSubjectControlAdapter.getDocument();

			if (fViewer instanceof ITextViewerExtension)
			{
				ITextViewerExtension extension = (ITextViewerExtension) fViewer;
				target = extension.getRewriteTarget();
			}

			if (target != null)
			{
				target.beginCompoundChange();
			}

			if (fViewer instanceof IEditingSupportRegistry)
			{
				IEditingSupportRegistry registry = (IEditingSupportRegistry) fViewer;
				registry.register(helper);
			}

			if (p instanceof ICompletionProposalExtension2 && fViewer != null)
			{
				ICompletionProposalExtension2 e = (ICompletionProposalExtension2) p;
				e.apply(fViewer, trigger, stateMask, offset);
			}
			else if (p instanceof ICompletionProposalExtension)
			{
				ICompletionProposalExtension e = (ICompletionProposalExtension) p;
				e.apply(document, trigger, offset);
			}
			else
			{
				p.apply(document);
			}

			Point selection = p.getSelection(document);
			if (selection != null)
			{
				fContentAssistSubjectControlAdapter.setSelectedRange(selection.x, selection.y);
				fContentAssistSubjectControlAdapter.revealRange(selection.x, selection.y);
			}

			IContextInformation info = p.getContextInformation();
			if (info != null)
			{

				int contextInformationOffset;
				if (p instanceof ICompletionProposalExtension)
				{
					ICompletionProposalExtension e = (ICompletionProposalExtension) p;
					contextInformationOffset = e.getContextInformationPosition();
				}
				else
				{
					if (selection == null)
					{
						selection = fContentAssistSubjectControlAdapter.getSelectedRange();
					}
					contextInformationOffset = selection.x + selection.y;
				}

				fContentAssistant.showContextInformation(info, contextInformationOffset);
			}
			else
			{
				fContentAssistant.showContextInformation(null, -1);
			}

		}
		finally
		{
			if (target != null)
			{
				target.endCompoundChange();
			}

			if (fViewer instanceof IEditingSupportRegistry)
			{
				IEditingSupportRegistry registry = (IEditingSupportRegistry) fViewer;
				registry.unregister(helper);
			}
			fInserting = false;
		}
	}

	/**
	 * Returns whether this popup has the focus.
	 * 
	 * @return <code>true</code> if the popup has the focus
	 */
	public boolean hasFocus()
	{
		if (fPopupCloser != null && fPopupCloser.isAdditionalInfoInFocus())
		{
			// TISTUD-913
			return true;
		}
		if (Helper.okToUse(fProposalShell))
		{
			return (fProposalShell.isFocusControl() || fProposalTable.isFocusControl());
		}

		return false;
	}

	/**
	 * Hides this popup.
	 */
	public void hide()
	{
		fLastKeyPressed = '\0';
		unregister();

		if (fViewer instanceof IEditingSupportRegistry)
		{
			IEditingSupportRegistry registry = (IEditingSupportRegistry) fViewer;
			registry.unregister(fFocusHelper);
		}

		if (Helper.okToUse(fProposalShell))
		{
			fContentAssistant.removeContentAssistListener(this, ContentAssistant.PROPOSAL_SELECTOR);
			// TISTUD-913: moved the 'fPopupCloser.uninstall();' to disposePopup()
			if (fAdditionalInfoController != null)
			{
				fAdditionalInfoController.disposeInformationControl();
			}
			// TISTUD-1550: Call to dispose, instead of fProposalShell.setVisible(false);
			fProposalShell.dispose();
		}
	}

	/**
	 * Disposes the popup
	 */
	public void disposePopup()
	{
		if (fProposalShell != null && !fProposalShell.isDisposed())
		{
			fProposalShell.dispose();
		}

		try
		{
			if (projectScopeNode != null)
			{
				projectScopeNode.removePreferenceChangeListener(prefListener);
				projectScopeNode = null;
			}
			if (instanceScopeNode != null)
			{
				instanceScopeNode.removePreferenceChangeListener(prefListener);
				instanceScopeNode = null;
			}
		}
		catch (IllegalStateException e)
		{
			// ignores
		}
		if (fPopupCloser != null)
		{
			// TISTUD-913
			fPopupCloser.uninstall();
		}
		prefListener = null;
	}

	/**
	 * Unregister this completion proposal popup.
	 * 
	 * @since 3.0
	 */
	private void unregister()
	{
		if (fDocumentListener != null)
		{
			IDocument document = fContentAssistSubjectControlAdapter.getDocument();
			if (document != null)
			{
				document.removeDocumentListener(fDocumentListener);
			}
			fDocumentListener = null;
		}
		fDocumentEvents.clear();

		if (fKeyListener != null && Helper.okToUse(fContentAssistSubjectControlAdapter.getControl()))
		{
			fContentAssistSubjectControlAdapter.removeKeyListener(fKeyListener);
			fKeyListener = null;
		}

		if (fLastProposal != null)
		{
			if (fLastProposal instanceof ICompletionProposalExtension2 && fViewer != null)
			{
				ICompletionProposalExtension2 extension = (ICompletionProposalExtension2) fLastProposal;
				extension.unselected(fViewer);
			}
			fLastProposal = null;
		}

		fFilteredProposals = null;
		fComputedProposals = null;

		fContentAssistant.possibleCompletionsClosed();
	}

	/**
	 * Returns whether this popup is active. It is active if the proposal selector is visible.
	 * 
	 * @return <code>true</code> if this popup is active
	 */
	public boolean isActive()
	{
		return Helper.okToUse(fProposalShell) && fProposalShell.isVisible();
	}

	/**
	 * Initializes the proposal selector with these given proposals.
	 * 
	 * @param proposals
	 *            the proposals
	 * @param isFilteredSubset
	 *            if <code>true</code>, the proposal table is not cleared, but the proposals that are not in the passed
	 *            array are removed from the displayed set
	 */
	private void setProposals(ICompletionProposal[] proposals, boolean isFilteredSubset)
	{
		ICompletionProposal[] oldProposals = fFilteredProposals;
		ICompletionProposal oldProposal = getSelectedProposal(); // may trigger filtering and a reentrant call to
																	// setProposals()
		if (oldProposals != fFilteredProposals) // reentrant call was first - abort
			return;

		if (Helper.okToUse(fProposalTable))
		{
			if (oldProposal instanceof ICompletionProposalExtension2 && fViewer != null)
				((ICompletionProposalExtension2) oldProposal).unselected(fViewer);

			if (proposals == null)
			{
				proposals = new ICompletionProposal[] {};
			}

			fFilteredProposals = proposals;
			final int newLen = proposals.length;

			if (USE_VIRTUAL)
			{
				fProposalTable.setItemCount(newLen);
				fProposalTable.clearAll();
			}
			else
			{
				fProposalTable.setRedraw(false);
				fProposalTable.setItemCount(newLen);
				TableItem[] items = fProposalTable.getItems();
				for (int i = 0; i < items.length; i++)
				{
					TableItem item = items[i];
					ICompletionProposal proposal = proposals[i];
					item.setText(proposal.getDisplayString());
					item.setImage(proposal.getImage());
					item.setData(proposal);
				}
				fProposalTable.setRedraw(true);
			}

			// Custom code for modifying selection/size
			int defaultIndex = -1;
			int suggestedIndex = -1;

			// select the first proposal
			if (proposals.length > 0)
			{
				defaultIndex = 0;
				suggestedIndex = 0;
			}

			String longestString = StringUtil.EMPTY;
			String longestLoc = StringUtil.EMPTY;

			for (int i = 0; i < proposals.length; i++)
			{
				ICompletionProposal proposal = proposals[i];
				String entry = proposal.getDisplayString().trim();
				if (entry.length() > longestString.length())
				{
					longestString = entry;
				}
				if (proposal instanceof ICommonCompletionProposal)
				{

					ICommonCompletionProposal prop = (ICommonCompletionProposal) proposal;
					String loc = prop.getFileLocation();
					if (loc.length() > longestLoc.length())
					{
						longestLoc = loc;
					}
				}
			}

			int objWidth = getStringWidth(longestString);

			int locWidth = getStringWidth(longestLoc);

			objWidth = Math.min(objWidth, MAX_PROPOSAL_COLUMN_WIDTH);
			locWidth = Math.min(locWidth, MAX_LOCATION_COLUMN_WIDTH);

			if (!isFilteredSubset)
			{
				resizeTable(objWidth, locWidth);
			}
			modifySelection(defaultIndex, suggestedIndex);
		}
	}

	/**
	 * Returns the width of the string in pixels
	 * 
	 * @param string
	 * @return
	 */
	protected int getStringWidth(String string)
	{
		String measureString = "M" + string + "MM"; //$NON-NLS-1$ //$NON-NLS-2$
		GC gc = new GC(fProposalTable.getShell());
		Point extent = gc.stringExtent(measureString);
		int width = extent.x;
		gc.dispose();
		return width;
	}

	protected void modifySelection(int defaultIndex, int suggestedIndex)
	{
		// IM changed this to deselect the table if there is no default selection
		if (defaultIndex != -1)
		{
			this.selectProposal(defaultIndex, false, true);
		}
		else if (suggestedIndex != -1)
		{
			this.fProposalTable.deselectAll();
			this.setScroll(suggestedIndex);
		}
		else if (Helper.okToUse(fProposalTable))
		{
			if (fLastKeyPressed == '\b' && defaultIndex == -1)
			{
				hide();
			}
			else
			{
				this.fProposalTable.setTopIndex(0);
				this.fProposalTable.deselectAll();
			}
		}
	}

	/**
	 * Returns the graphical location at which this popup should be made visible.
	 * 
	 * @param width
	 * @param height
	 * @return the location of this popup
	 */
	/*
	 * private Point getLocation(int width, int height) { // get character index into the source for the current caret
	 * position int caret = fContentAssistSubjectControlAdapter.getCaretOffset(); // get coordinate of caret position
	 * Point clientPoint = fContentAssistSubjectControlAdapter.getLocationAtOffset(caret); // make sure clientPoint is
	 * within the visible client area if (clientPoint.x < 0) clientPoint.x = 0; if (clientPoint.y < 0) clientPoint.y =
	 * 0; // convert coordinate to screen coordinates Point screenPoint =
	 * fContentAssistSubjectControlAdapter.getControl().toDisplay(clientPoint); Rectangle screenRect =
	 * Display.getCurrent().getClientArea(); int lineHeight = fContentAssistSubjectControlAdapter.getLineHeight(); int x
	 * = screenPoint.x; int y = screenPoint.y + lineHeight + 2; if (x + width > screenRect.width) { // hangs over the
	 * right side of the screen if (screenPoint.x - width > 0) { x = screenPoint.x - width; } else { // doesn't fit on
	 * the left either // This should only happen on a really small screen or with a large popup } } if (y + height >
	 * screenRect.height) { // doesn't fit below current line // TODO: '7' is a magic number. We really need a reliable
	 * way to get the true height/width of this widget int yOffset = height + lineHeight + 7; if (screenPoint.y -
	 * yOffset > 0) { y = screenPoint.y - yOffset; } else { // doesn't fit above current line either // This should only
	 * happen on a really small screen or with a large popup } } return new Point(x, y); }
	 */

	/**
	 * Returns the graphical location at which this popup should be made visible.
	 * 
	 * @param proposalBox
	 * @param displayBounds
	 * @param caretLocation
	 * @param lineHeight
	 * @return the location of this popup
	 */
	public static Point computeLocation(Rectangle proposalBox, Rectangle displayBounds, Point caretLocation,
			int lineHeight)
	{

		if (caretLocation.y + proposalBox.height > displayBounds.height + displayBounds.y)
		{
			return new Point(caretLocation.x, caretLocation.y - (lineHeight + proposalBox.height));
		}
		else
		{
			return new Point(caretLocation.x, caretLocation.y + lineHeight);
		}
	}

	/**
	 * Returns the size of this completion proposal popup.
	 * 
	 * @return a Point containing the size
	 * @since 3.0
	 */
	Point getSize()
	{
		return fSize;
	}

	/**
	 * Displays this popup and install the additional info controller, so that additional info is displayed when a
	 * proposal is selected and additional info is available.
	 */
	private void displayProposals()
	{

		if (!Helper.okToUse(fProposalShell) || !Helper.okToUse(fProposalTable))
		{
			return;
		}

		if (fContentAssistant.addContentAssistListener(this, ContentAssistant.PROPOSAL_SELECTOR))
		{

			if (fDocumentListener == null)
			{
				fDocumentListener = new IDocumentListener()
				{
					public void documentAboutToBeChanged(DocumentEvent event)
					{
						if (!fInserting)
						{
							fDocumentEvents.add(event);
						}
					}

					public void documentChanged(DocumentEvent event)
					{
						if (!fInserting)
						{
							filterProposals();
						}
					}
				};
			}
			IDocument document = fContentAssistSubjectControlAdapter.getDocument();
			if (document != null)
			{
				document.addDocumentListener(fDocumentListener);
			}

			if (fFocusHelper == null)
			{
				fFocusHelper = new IEditingSupport()
				{

					public boolean isOriginator(DocumentEvent event, IRegion focus)
					{
						return false; // this helper just covers the focus change to the proposal
						// shell, no remote
						// editions
					}

					public boolean ownsFocusShell()
					{
						return true;
					}

				};
			}
			if (fViewer instanceof IEditingSupportRegistry)
			{
				IEditingSupportRegistry registry = (IEditingSupportRegistry) fViewer;
				registry.register(fFocusHelper);
			}

			/*
			 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=52646 on GTK, setVisible and such may run the event loop
			 * (see also https://bugs.eclipse.org/bugs/show_bug.cgi?id=47511) Since the user may have already canceled
			 * the popup or selected an entry (ESC or RETURN), we have to double check whether the table is still
			 * okToUse. See comments below
			 */
			fProposalShell.setVisible(true); // may run event loop on GTK
			// transfer focus since no verify key listener can be attached
			if (!fContentAssistSubjectControlAdapter.supportsVerifyKeyListener() && Helper.okToUse(fProposalShell))
			{
				fProposalShell.setFocus(); // may run event loop on GTK ??
			}

			if (fAdditionalInfoController != null && Helper.okToUse(fProposalTable))
			{
				fAdditionalInfoController.install(fProposalTable);
				fAdditionalInfoController.handleTableSelectionChanged();
			}
		}
		else
		{
			hide();
		}
	}

	/**
	 * @see IContentAssistListener#verifyKey(VerifyEvent)
	 */
	public boolean verifyKey(VerifyEvent e)
	{
		if (!Helper.okToUse(fProposalShell))
		{
			return true;
		}

		char key = e.character;
		fLastKeyPressed = e.character;

		if (key == 0)
		{
			int newSelection = fProposalTable.getSelectionIndex();
			int visibleRows = (fProposalTable.getSize().y / fProposalTable.getItemHeight()) - 1;
			boolean smartToggle = false;
			switch (e.keyCode)
			{

				case SWT.ARROW_LEFT:
				case SWT.ARROW_RIGHT:
					// filterProposals();
					hide();
					return true;

				case SWT.ARROW_UP:
					newSelection -= 1;
					if (newSelection < 0)
					{
						newSelection = fProposalTable.getItemCount() - 1;
					}
					break;

				case SWT.ARROW_DOWN:
					newSelection += 1;
					if (newSelection > fProposalTable.getItemCount() - 1)
					{
						newSelection = 0;
					}
					break;

				case SWT.PAGE_DOWN:
					newSelection += visibleRows;
					if (newSelection >= fProposalTable.getItemCount())
					{
						newSelection = fProposalTable.getItemCount() - 1;
					}
					break;

				case SWT.PAGE_UP:
					newSelection -= visibleRows;
					if (newSelection < 0)
					{
						newSelection = 0;
					}
					break;

				case SWT.HOME:
					hide();
					return true;
					// newSelection= 0;
					// break;
					//
				case SWT.END:
					hide();
					return true;
					// newSelection= fProposalTable.getItemCount() - 1;
					// break;

				default:
					if (e.keyCode != SWT.CAPS_LOCK && e.keyCode != SWT.MOD1 && e.keyCode != SWT.MOD2
							&& e.keyCode != SWT.MOD3 && e.keyCode != SWT.MOD4)
					{
						hide();
					}
					return true;
			}

			selectProposal(newSelection, smartToggle, false);

			e.doit = false;
			return false;

		}

		// key != 0
		switch (key)
		{
			case '\t':
				if (!_insertOnTab)
				{
					e.doit = true;
					hide();
					break;
				}
				else
				{
					if (selectProposalWithMask(e.stateMask))
					{
						e.doit = false;
						break;
					}
					else
					{
						return true;
					}
				}

			case 0x1B: // Esc
				e.doit = false;
				hide();
				break;

			case '\n': // Ctrl-Enter on w2k
			case '\r': // Enter
				if (selectProposalWithMask(e.stateMask))
				{
					e.doit = false;
					break;
				}
				else
				{
					return true;
				}
			default:

				ICompletionProposal p = getSelectedProposal();
				if (p instanceof ICompletionProposalExtension)
				{
					ICompletionProposalExtension t = (ICompletionProposalExtension) p;
					char[] triggers = t.getTriggerCharacters();
					if (contains(triggers, key))
					{
						// we do this inside the 'if' for performance reasons
						boolean triggerEnabled = true;
						if (p instanceof ICommonCompletionProposal)
						{
							triggerEnabled = ((ICommonCompletionProposal) p).validateTrigger(
									fContentAssistSubjectControlAdapter.getDocument(), fFilterOffset, e);
						}
						if (triggerEnabled)
						{
							e.doit = false;
							hide();
							insertProposal(p, key, e.stateMask,
									fContentAssistSubjectControlAdapter.getSelectedRange().x);
						}
					}
				}
		}

		return true;
	}

	/**
	 * Selects the entry with the given index in the proposal selector and feeds the selection to the additional info
	 * controller.
	 * 
	 * @param index
	 *            the index in the list
	 * @param smartToggle
	 *            <code>true</code> if the smart toggle key has been pressed
	 * @param autoScroll
	 *            Do we scroll the item into view at the middle of the list
	 * @since 2.1
	 */
	private void selectProposal(int index, boolean smartToggle, boolean autoScroll)
	{
		if (fFilteredProposals == null)
		{
			return;
		}

		ICompletionProposal oldProposal = getSelectedProposal();
		if (oldProposal instanceof ICompletionProposalExtension2 && fViewer != null)
		{
			((ICompletionProposalExtension2) oldProposal).unselected(fViewer);
		}

		ICompletionProposal proposal = fFilteredProposals[index];
		if (proposal instanceof ICompletionProposalExtension2 && fViewer != null)
		{
			((ICompletionProposalExtension2) proposal).selected(fViewer, smartToggle);
		}

		fLastProposal = proposal;
		fProposalTable.setSelection(index);

		if (autoScroll)
		{
			setScroll(index);
		}
		fProposalTable.showSelection();

		if (fAdditionalInfoController != null)
		{
			fAdditionalInfoController.handleTableSelectionChanged();
		}
	}

	/**
	 * Sets the postition of the table
	 * 
	 * @param index
	 */
	private void setScroll(int index)
	{
		int topIndex = index - 4 > 0 ? index - 4 : 0;
		fProposalTable.setTopIndex(topIndex);
	}

	/**
	 * Returns whether the given character is contained in the given array of characters.
	 * 
	 * @param characters
	 *            the list of characters
	 * @param c
	 *            the character to look for in the list
	 * @return <code>true</code> if character belongs to the list
	 * @since 2.0
	 */
	private boolean contains(char[] characters, char c)
	{

		if (characters == null)
		{
			return false;
		}

		for (int i = 0; i < characters.length; i++)
		{
			if (c == characters[i])
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * @see org.eclipse.jface.text.IEventConsumer#processEvent(org.eclipse.swt.events.VerifyEvent)
	 */
	public void processEvent(VerifyEvent e)
	{
	}

	/**
	 * Filters the displayed proposal based on the given cursor position and the offset of the original invocation of
	 * the code assistant.
	 */
	private void filterProposals()
	{
		++fInvocationCounter;
		final Control control = fContentAssistSubjectControlAdapter.getControl();
		control.getDisplay().asyncExec(new Runnable()
		{
			final long fCounter = fInvocationCounter;

			public void run()
			{

				if (fCounter != fInvocationCounter)
				{
					return;
				}

				if (control.isDisposed())
				{
					return;
				}

				int offset = fContentAssistSubjectControlAdapter.getSelectedRange().x;
				ICompletionProposal[] proposals = null;
				try
				{
					if (offset > -1)
					{
						DocumentEvent event = TextUtilities.mergeProcessedDocumentEvents(fDocumentEvents);
						proposals = computeFilteredProposals(offset, event);
					}
				}
				catch (BadLocationException x)
				{
				}
				finally
				{
					fDocumentEvents.clear();
				}
				fFilterOffset = offset;

				if (proposals != null && proposals.length > 0)
				{
					fContentAssistant.promoteKeyListener();
					setProposals(proposals, fIsFilteredSubset);
				}
				else
				{
					hide();
				}
			}
		});
	}

	/**
	 * Computes the subset of already computed proposals that are still valid for the given offset.
	 * 
	 * @param offset
	 *            the offset
	 * @param event
	 *            the merged document event
	 * @return the set of filtered proposals
	 * @since 3.0
	 */
	private ICompletionProposal[] computeFilteredProposals(int offset, DocumentEvent event)
	{

		if (offset == fInvocationOffset && event == null)
		{
			fIsFilteredSubset = false;
			return fComputedProposals;
		}

		if (offset < fInvocationOffset)
		{
			fIsFilteredSubset = false;
			fInvocationOffset = offset;
			fComputedProposals = computeProposals(fInvocationOffset, false);
			return fComputedProposals;
		}

		ICompletionProposal[] proposals;
		if (offset < fFilterOffset)
		{
			proposals = fComputedProposals;
			fIsFilteredSubset = false;
		}
		else
		{
			proposals = fFilteredProposals;
			fIsFilteredSubset = true;
		}

		if (proposals == null)
		{
			fIsFilteredSubset = false;
			return null;
		}

		IDocument document = fContentAssistSubjectControlAdapter.getDocument();
		// this does go through the array twice (once to figure out if it's okay to use the else case, and the second
		// time to actual filter the proposals, but it is what the original logic suggests
		for (int i = 0; i < proposals.length; i++)
		{
			ICompletionProposal proposal = proposals[i];
			if (!(proposal instanceof ICompletionProposalExtension2)
					&& !(proposal instanceof ICompletionProposalExtension))
			{
				// restore original behavior
				fIsFilteredSubset = false;
				fInvocationOffset = offset;
				fComputedProposals = computeProposals(fInvocationOffset, false);

				return fComputedProposals;
			}
		}

		ICompletionProposal[] filtered = filterProposals(proposals, document, offset, event);
		return filtered;
	}

	/**
	 * Filters the list of proposals to only those that are valid in the current context of the document event
	 * 
	 * @param proposals
	 * @param document
	 * @param offset
	 * @param event
	 * @return
	 */
	private ICompletionProposal[] filterProposals(ICompletionProposal[] proposals, IDocument document, int offset,
			DocumentEvent event)
	{
		int length = proposals == null ? 0 : proposals.length;
		List<ICompletionProposal> filtered = new ArrayList<ICompletionProposal>(length);
		for (int i = 0; i < length; i++)
		{
			ICompletionProposal proposal = proposals[i];

			if (proposal instanceof ICompletionProposalExtension2)
			{
				ICompletionProposalExtension2 p = (ICompletionProposalExtension2) proposal;

				if (p.validate(document, offset, event))
				{
					filtered.add(proposal);
				}

			}
			else if (proposal instanceof ICompletionProposalExtension)
			{
				ICompletionProposalExtension p = (ICompletionProposalExtension) proposal;

				if (p.isValidFor(document, offset))
				{
					filtered.add(proposal);
				}
			}
		}

		IdeLog.logInfo(UIEplPlugin.getDefault(),
				MessageFormat.format("Filtered list to {0} proposals", filtered.size()), IUiEplScopes.CONTENT_ASSIST); //$NON-NLS-1$

		return filtered.toArray(new ICompletionProposal[filtered.size()]);
	}

	/**
	 * Requests the proposal shell to take focus.
	 * 
	 * @since 3.0
	 */
	public void setFocus()
	{
		if (Helper.okToUse(fProposalShell))
		{
			fProposalShell.setFocus();
		}
	}

	/**
	 * Returns <code>true</code> if <code>proposal</code> should be auto-inserted, <code>false</code> otherwise.
	 * 
	 * @param proposal
	 *            the single proposal that might be automatically inserted
	 * @return <code>true</code> if <code>proposal</code> can be inserted automatically, <code>false</code> otherwise
	 * @since 3.1
	 */
	private boolean canAutoInsert(ICompletionProposal proposal)
	{
		if (fContentAssistant.isAutoInserting())
		{
			if (proposal instanceof ICompletionProposalExtension4)
			{
				ICompletionProposalExtension4 ext = (ICompletionProposalExtension4) proposal;
				return ext.isAutoInsertable();
			}
			return true; // default behavior before ICompletionProposalExtension4 was introduced
		}
		return false;
	}

	/**
	 * Completes the common prefix of all proposals directly in the code. If no common prefix can be found, the proposal
	 * popup is shown.
	 * 
	 * @return an error message if completion failed.
	 * @since 3.0
	 */
	public String incrementalComplete()
	{
		if (Helper.okToUse(fProposalShell) && fFilteredProposals != null)
		{
			completeCommonPrefix();
		}
		else
		{
			final Control control = fContentAssistSubjectControlAdapter.getControl();

			if (fKeyListener == null)
			{
				fKeyListener = new ProposalSelectionListener();
			}

			if (!Helper.okToUse(fProposalShell) && !control.isDisposed())
			{
				fContentAssistSubjectControlAdapter.addKeyListener(fKeyListener);
			}

			BusyIndicator.showWhile(control.getDisplay(), new Runnable()
			{
				public void run()
				{

					fInvocationOffset = fContentAssistSubjectControlAdapter.getSelectedRange().x;
					fFilterOffset = fInvocationOffset;
					fFilteredProposals = computeProposals(fInvocationOffset, false);

					int count = (fFilteredProposals == null ? 0 : fFilteredProposals.length);
					if (count == 0)
					{
						// IM turned off for the moment, as it's annoying more than helpful to beep.
						// control.getDisplay().beep();
						hide();
					}
					else if (count == 1 && canAutoInsert(fFilteredProposals[0]))
					{
						insertProposal(fFilteredProposals[0], (char) 0, 0, fInvocationOffset);
						hide();
					}
					else
					{
						if (completeCommonPrefix())
						{
							hide();
						}
						else
						{
							fComputedProposals = fFilteredProposals;
							createPopup();
						}
					}
				}
			});
		}
		return getErrorMessage();
	}

	/**
	 * Calls the necessary methods to popup the content assist method. This was moved to one single method to unify the
	 * path to show the window and to provide an easy way to time the operations needed to populate and create the
	 * widget.
	 */
	private void createPopup()
	{
		createProposalSelector();
		setProposals(fComputedProposals, false);
		fContentAssistant.addToLayout(this, fProposalShell, ContentAssistant.LayoutManager.LAYOUT_PROPOSAL_SELECTOR,
				fContentAssistant.getSelectionOffset());
		displayProposals();
	}

	/**
	 * Acts upon <code>fFilteredProposals</code>: if there is just one valid proposal, it is inserted, otherwise, the
	 * common prefix of all proposals is inserted into the document. If there is no common prefix, nothing happens and
	 * <code>false</code> is returned.
	 * 
	 * @return <code>true</code> if a single proposal was inserted and the selector can be closed, <code>false</code> if
	 *         more than once choice remain
	 * @since 3.0
	 */
	private boolean completeCommonPrefix()
	{

		// 0: insert single proposals
		if (fFilteredProposals.length == 1)
		{
			if (canAutoInsert(fFilteredProposals[0]))
			{
				insertProposal(fFilteredProposals[0], (char) 0, 0, fFilterOffset);
				hide();
				return true;
			}
			return false;
		}

		// 1: extract pre- and postfix from all remaining proposals
		IDocument document = fContentAssistSubjectControlAdapter.getDocument();

		// contains the common postfix in the case that there are any proposals matching our LHS
		StringBuffer rightCasePostfix = null;
		List<ICompletionProposal> rightCase = new ArrayList<ICompletionProposal>();

		// whether to check for non-case compatible matches. This is initially true, and stays so
		// as long as there are i) no case-sensitive matches and ii) all proposals share the same
		// (although not corresponding with the document contents) common prefix.
		boolean checkWrongCase = true;
		// the prefix of all case insensitive matches. This differs from the document
		// contents and will be replaced.
		CharSequence wrongCasePrefix = null;
		int wrongCasePrefixStart = 0;
		// contains the common postfix of all case-insensitive matches
		StringBuffer wrongCasePostfix = null;
		List<ICompletionProposal> wrongCase = new ArrayList<ICompletionProposal>();

		for (int i = 0; i < fFilteredProposals.length; i++)
		{
			ICompletionProposal proposal = fFilteredProposals[i];
			CharSequence insertion = getPrefixCompletion(proposal);
			int start = getPrefixCompletionOffset(proposal);
			try
			{
				int prefixLength = fFilterOffset - start;
				int relativeCompletionOffset = Math.min(insertion.length(), prefixLength);
				String prefix = document.get(start, prefixLength);
				if (insertion.toString().startsWith(prefix))
				{
					checkWrongCase = false;
					rightCase.add(proposal);
					CharSequence newPostfix = insertion.subSequence(relativeCompletionOffset, insertion.length());
					if (rightCasePostfix == null)
					{
						rightCasePostfix = new StringBuffer(newPostfix.toString());
					}
					else
					{
						truncatePostfix(rightCasePostfix, newPostfix);
					}
				}
				else if (checkWrongCase)
				{
					CharSequence newPrefix = insertion.subSequence(0, relativeCompletionOffset);
					if (isPrefixCompatible(wrongCasePrefix, wrongCasePrefixStart, newPrefix, start, document))
					{
						wrongCasePrefix = newPrefix;
						wrongCasePrefixStart = start;
						CharSequence newPostfix = insertion.subSequence(relativeCompletionOffset, insertion.length());
						if (wrongCasePostfix == null)
						{
							wrongCasePostfix = new StringBuffer(newPostfix.toString());
						}
						else
						{
							truncatePostfix(wrongCasePostfix, newPostfix);
						}
						wrongCase.add(proposal);
					}
					else
					{
						checkWrongCase = false;
					}
				}
			}
			catch (BadLocationException e2)
			{
				// bail out silently
				return false;
			}

			if (rightCasePostfix != null && rightCasePostfix.length() == 0 && rightCase.size() > 1)
			{
				return false;
			}
		}

		// 2: replace single proposals

		if (rightCase.size() == 1)
		{
			ICompletionProposal proposal = rightCase.get(0);
			if (canAutoInsert(proposal))
			{
				insertProposal(proposal, (char) 0, 0, fInvocationOffset);
				hide();
				return true;
			}
			return false;
		}
		else if (checkWrongCase && wrongCase.size() == 1)
		{
			ICompletionProposal proposal = wrongCase.get(0);
			if (canAutoInsert(proposal))
			{
				insertProposal(proposal, (char) 0, 0, fInvocationOffset);
				hide();
				return true;
			}
			return false;
		}

		// 3: replace post- / prefixes

		CharSequence prefix;
		if (checkWrongCase)
		{
			prefix = wrongCasePrefix;
		}
		else
		{
			prefix = ""; //$NON-NLS-1$
		}

		CharSequence postfix;
		if (checkWrongCase)
		{
			postfix = wrongCasePostfix;
		}
		else
		{
			postfix = rightCasePostfix;
		}

		if (prefix == null || postfix == null)
		{
			return false;
		}

		try
		{
			// 4: check if parts of the postfix are already in the document
			int to = Math.min(document.getLength(), fFilterOffset + postfix.length());
			StringBuffer inDocument = new StringBuffer(document.get(fFilterOffset, to - fFilterOffset));
			truncatePostfix(inDocument, postfix);

			// 5: replace and reveal
			document.replace(fFilterOffset - prefix.length(), prefix.length() + inDocument.length(), prefix.toString()
					+ postfix.toString());

			fContentAssistSubjectControlAdapter.setSelectedRange(fFilterOffset + postfix.length(), 0);
			fContentAssistSubjectControlAdapter.revealRange(fFilterOffset + postfix.length(), 0);

			return false;
		}
		catch (BadLocationException e)
		{
			// ignore and return false
			return false;
		}
	}

	/**
	 * @since 3.1
	 */
	private boolean isPrefixCompatible(CharSequence oneSequence, int oneOffset, CharSequence twoSequence,
			int twoOffset, IDocument document) throws BadLocationException
	{
		if (oneSequence == null || twoSequence == null)
		{
			return true;
		}

		int min = Math.min(oneOffset, twoOffset);
		int oneEnd = oneOffset + oneSequence.length();
		int twoEnd = twoOffset + twoSequence.length();

		String one = document.get(oneOffset, min - oneOffset) + oneSequence
				+ document.get(oneEnd, Math.min(fFilterOffset, fFilterOffset - oneEnd));
		String two = document.get(twoOffset, min - twoOffset) + twoSequence
				+ document.get(twoEnd, Math.min(fFilterOffset, fFilterOffset - twoEnd));

		return one.equals(two);
	}

	/**
	 * Truncates <code>buffer</code> to the common prefix of <code>buffer</code> and <code>sequence</code>.
	 * 
	 * @param buffer
	 *            the common postfix to truncate
	 * @param sequence
	 *            the characters to truncate with
	 */
	private void truncatePostfix(StringBuffer buffer, CharSequence sequence)
	{
		// find common prefix
		int min = Math.min(buffer.length(), sequence.length());
		for (int c = 0; c < min; c++)
		{
			if (sequence.charAt(c) != buffer.charAt(c))
			{
				buffer.delete(c, buffer.length());
				return;
			}
		}

		// all equal up to minimum
		buffer.delete(min, buffer.length());
	}

	/**
	 * Extracts the completion offset of an <code>ICompletionProposal</code>. If <code>proposal</code> is a
	 * <code>ICompletionProposalExtension3</code>, its <code>getCompletionOffset</code> method is called, otherwise, the
	 * invocation offset of this popup is shown.
	 * 
	 * @param proposal
	 *            the proposal to extract the offset from
	 * @return the proposals completion offset, or <code>fInvocationOffset</code>
	 * @since 3.1
	 */
	private int getPrefixCompletionOffset(ICompletionProposal proposal)
	{
		if (proposal instanceof ICompletionProposalExtension3)
		{
			return ((ICompletionProposalExtension3) proposal).getPrefixCompletionStart(
					fContentAssistSubjectControlAdapter.getDocument(), fFilterOffset);
		}
		return fInvocationOffset;
	}

	/**
	 * Extracts the replacement string from an <code>ICompletionProposal</code>. If <code>proposal</code> is a
	 * <code>ICompletionProposalExtension3</code>, its <code>getCompletionText</code> method is called, otherwise, the
	 * display string is used.
	 * 
	 * @param proposal
	 *            the proposal to extract the text from
	 * @return the proposals completion text
	 * @since 3.1
	 */
	private CharSequence getPrefixCompletion(ICompletionProposal proposal)
	{
		CharSequence insertion = null;
		if (proposal instanceof ICompletionProposalExtension3)
		{
			insertion = ((ICompletionProposalExtension3) proposal).getPrefixCompletionText(
					fContentAssistSubjectControlAdapter.getDocument(), fFilterOffset);
		}

		if (insertion == null)
		{
			insertion = proposal.getDisplayString();
		}

		return insertion;
	}

	/**
	 * Gets the activation key
	 * 
	 * @return char
	 */
	public char getActivationKey()
	{
		return fActivationKey;
	}

	/**
	 * Sets the activation key
	 * 
	 * @param activationKey
	 */
	public void setActivationKey(char activationKey)
	{
		fActivationKey = activationKey;
	}

	/**
	 * The empty proposal displayed if there is nothing else to show.
	 * 
	 * @since 3.2
	 */
	private static final class EmptyProposal implements ICompletionProposal, ICompletionProposalExtension,
			ICompletionProposalExtension4
	{

		String fDisplayString;
		int fOffset;

		/*
		 * @see ICompletionProposal#apply(IDocument)
		 */
		public void apply(IDocument document)
		{
		}

		/*
		 * @see ICompletionProposal#getSelection(IDocument)
		 */
		public Point getSelection(IDocument document)
		{
			return new Point(fOffset, 0);
		}

		/*
		 * @see ICompletionProposal#getContextInformation()
		 */
		public IContextInformation getContextInformation()
		{
			return null;
		}

		/*
		 * @see ICompletionProposal#getImage()
		 */
		public Image getImage()
		{
			return null;
		}

		/*
		 * @see ICompletionProposal#getDisplayString()
		 */
		public String getDisplayString()
		{
			return fDisplayString;
		}

		/*
		 * @see ICompletionProposal#getAdditionalProposalInfo()
		 */
		public String getAdditionalProposalInfo()
		{
			return null;
		}

		/*
		 * @see
		 * org.eclipse.jface.text.contentassist.ICompletionProposalExtension#apply(org.eclipse.jface.text.IDocument,
		 * char, int)
		 */
		public void apply(IDocument document, char trigger, int offset)
		{
		}

		/*
		 * @see
		 * org.eclipse.jface.text.contentassist.ICompletionProposalExtension#isValidFor(org.eclipse.jface.text.IDocument
		 * , int)
		 */
		public boolean isValidFor(IDocument document, int offset)
		{
			return false;
		}

		/*
		 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension#getTriggerCharacters()
		 */
		public char[] getTriggerCharacters()
		{
			return null;
		}

		/*
		 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension#getContextInformationPosition()
		 */
		public int getContextInformationPosition()
		{
			return -1;
		}

		/*
		 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension#isAutoInsertable()
		 */
		public boolean isAutoInsertable()
		{
			return false;
		}
	}

}
