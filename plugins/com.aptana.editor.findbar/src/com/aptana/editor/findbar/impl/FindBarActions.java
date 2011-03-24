/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar.impl;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.jface.bindings.Binding;
import org.eclipse.jface.bindings.BindingManagerEvent;
import org.eclipse.jface.bindings.IBindingManagerListener;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.findbar.FindBarPlugin;
import com.aptana.editor.findbar.preferences.IPreferencesConstants;

/**
 * Helper to manage the activation of actions. When some control of the find bar receives the focus, the binding service
 * is disabled so that we no longer have the default actions from the text editor, while handling internally the actions
 * we want so that the actions related to the find bar still get executed. When focus is lost, the find bar actions get
 * disabled and the binding service is restored again.
 * 
 * @author Fabio Zadrozny
 */
public class FindBarActions
{

	public static final String HIDE_FIND_BAR_COMMAND_ID = "org.eclipse.ui.edit.findbar.hide"; //$NON-NLS-1$
	public static final String FIND_PREVIOUS_COMMAND_ID = "org.eclipse.ui.edit.findbar.findPrevious"; //$NON-NLS-1$
	public static final String FIND_NEXT_COMMAND_ID = "org.eclipse.ui.edit.findbar.findNext"; //$NON-NLS-1$
	public static final String FIND_NEXT_OR_PREV_COMMAND_ID = "org.eclipse.ui.edit.findbar.findNextOrPrev"; //$NON-NLS-1$
	public static final String FOCUS_REPLACE_COMMAND_ID = "org.eclipse.ui.edit.findbar.focusReplace"; //$NON-NLS-1$
	public static final String FOCUS_FIND_COMMAND_ID = "org.eclipse.ui.edit.findbar.focusFind"; //$NON-NLS-1$
	public static final String FOCUS_FIND_OR_OPEN_ECLIPSE_SEARCH_COMMAND_ID = "org.eclipse.ui.edit.findbar.focusFindOrOpenEclipseSearch"; //$NON-NLS-1$
	public static final String TOGGLE_CASE_MATCHING_COMMAND_ID = "org.eclipse.ui.edit.findbar.toggleCaseMatching"; //$NON-NLS-1$
	public static final String TOGGLE_REGEXP_MATCHING_COMMAND_ID = "org.eclipse.ui.edit.findbar.toggleRegexpMatching"; //$NON-NLS-1$
	public static final String TOGGLE_SEARCH_BACKWARD_COMMAND_ID = "org.eclipse.ui.edit.findbar.toggleSearchBackward"; //$NON-NLS-1$
	public static final String TOGGLE_WORD_MATCHING_COMMAND_ID = "org.eclipse.ui.edit.findbar.toggleWordMatching"; //$NON-NLS-1$
	public static final String SEARCH_IN_OPEN_FILES_COMMAND_ID = "org.eclipse.ui.edit.findbar.searchInOpenFiles"; //$NON-NLS-1$
	public static final String SHOW_OPTIONS_COMMAND_ID = "org.eclipse.ui.edit.findbar.showOptions"; //$NON-NLS-1$

	private boolean fActivated;
	private IContextActivation findBarContextActivation;

	private final Map<String, AbstractHandler> fCommandToHandler = new HashMap<String, AbstractHandler>();
	private final List<IHandlerActivation> fHandlerActivations = new ArrayList<IHandlerActivation>();

	private final IBindingManagerListener fClearCommandToBindingOnChangesListener = new IBindingManagerListener()
	{

		public void bindingManagerChanged(BindingManagerEvent event)
		{
			if (event.isActiveBindingsChanged())
			{
				fCommandToBinding = null;
			}
		}
	};

	/**
	 * May be null.
	 */
	private HashMap<String, List<TriggerSequence>> fCommandToBinding;
	private ITextEditor textEditor;
	private WeakReference<FindBarDecorator> findBarDecorator;

	public FindBarActions(ITextEditor textEditor, FindBarDecorator findBarDecorator)
	{
		this.textEditor = textEditor;
		// Don't create cycles...
		this.findBarDecorator = new WeakReference<FindBarDecorator>(findBarDecorator);

		fCommandToHandler.put(HIDE_FIND_BAR_COMMAND_ID, new HideFindBarHandler());
		fCommandToHandler.put(FIND_PREVIOUS_COMMAND_ID, new FindPreviousHandler());
		fCommandToHandler.put(FIND_NEXT_COMMAND_ID, new FindNextHandler());
		fCommandToHandler.put(FIND_NEXT_OR_PREV_COMMAND_ID, new FindNextOrPrevHandler());
		fCommandToHandler.put(FOCUS_FIND_COMMAND_ID, new FocusFindFindBarHandler());
		fCommandToHandler.put(FOCUS_FIND_OR_OPEN_ECLIPSE_SEARCH_COMMAND_ID,
				new FocusFindOrOpenEclipseSearchFindBarHandler());
		fCommandToHandler.put(FOCUS_REPLACE_COMMAND_ID, new FocusReplaceFindBarHandler());
		fCommandToHandler.put(TOGGLE_CASE_MATCHING_COMMAND_ID, new ToggleCaseFindBarHandler());
		fCommandToHandler.put(TOGGLE_WORD_MATCHING_COMMAND_ID, new ToggleWordFindBarHandler());
		fCommandToHandler.put(TOGGLE_REGEXP_MATCHING_COMMAND_ID, new ToggleRegexpFindBarHandler());
		fCommandToHandler.put(TOGGLE_SEARCH_BACKWARD_COMMAND_ID, new ToggleSearchBackwardFindBarHandler());
		fCommandToHandler.put(SEARCH_IN_OPEN_FILES_COMMAND_ID, new SearchInOpenFilesFindBarHandler());
		fCommandToHandler.put(SHOW_OPTIONS_COMMAND_ID, new ShowOptionsFindBarHandler());

		// Now, aside from the find bar commands, there are some other commands that it's nice to have available too,
		// even if the editor does not have focus.
		fCommandToHandler.put("org.eclipse.ui.edit.undo", null); //$NON-NLS-1$
		fCommandToHandler.put("org.eclipse.ui.edit.redo", null); //$NON-NLS-1$
	}

	/**
	 * Focus listener that adds itself as a key adapter to a given control so that given some key sequence we activate
	 * the commands related to the find bar.
	 */
	private class FindBarControlFocusListener implements FocusListener, Listener
	{

		private Control control;
		private boolean listening = false;
		private Display display;
		private boolean firstActivation = true;

		public FindBarControlFocusListener(Control control)
		{
			this.control = control;
		}

		public void focusLost(FocusEvent e)
		{
			if (!listening)
			{
				return;
			}
			listening = false;
			setFindBarContextActive(false);
			display.removeFilter(SWT.KeyDown, this);
			display.removeFilter(SWT.Traverse, this);
		}

		public void focusGained(FocusEvent e)
		{
			if (listening)
			{
				return;
			}
			if (firstActivation)
			{
				firstActivation = false;
				// On the first activation we update the commands to bindings because we want its side-effect
				// which is updating the tooltips to match the command.
				updateCommandToBinding();
			}
			listening = true;
			display = PlatformUI.getWorkbench().getDisplay();
			display.addFilter(SWT.KeyDown, this);
			display.addFilter(SWT.Traverse, this);

			setFindBarContextActive(true);
		}

		/**
		 * Modeled as com.aptana.scripting.keybindings.internal.KeybindingsManager, which handles not only the KeyDown,
		 * but also the Traverse event so that it can consume events that would end up being treated in the mnemonics
		 * before actually issuing a KeyDown event.
		 */
		public void handleEvent(Event event)
		{

			// If this is not a keyboard event, then there are no key strokes. This can happen if we are listening to
			// focus traversal events.
			if ((event.stateMask == 0) && (event.keyCode == 0) && (event.character == 0))
			{
				return;
			}

			// We only want to handle events in the control that got the focus (the traverse will happen first at it,
			// so, if it can't consume this event, there's no point in checking bindings after that).
			if (event.widget != control)
			{
				return;
			}
			boolean consumed = processEvent(event.keyCode, event.stateMask);
			if (consumed)
			{
				consumeEvent(event);
			}
		}

		private void consumeEvent(Event event)
		{
			switch (event.type)
			{
				case SWT.KeyDown:
					event.doit = false;
					break;
				case SWT.Traverse:
					event.detail = SWT.TRAVERSE_NONE;
					event.doit = true;
					break;
				default:
			}
			event.type = SWT.NONE;
		}

		/**
		 * Matches key events against the keybindings for the find bar actions.
		 */
		private boolean processEvent(int keyCode, int stateMask)
		{
			boolean consumed = false;
			if (fCommandToBinding == null)
			{
				// May be changed to null if the list of bindings change.
				updateCommandToBinding();
			}

			HashMap<String, List<TriggerSequence>> commandToBinding = FindBarActions.this.fCommandToBinding;
			if (commandToBinding != null)
			{
				for (Map.Entry<String, List<TriggerSequence>> entry : commandToBinding.entrySet())
				{

					List<TriggerSequence> value = entry.getValue();
					for (TriggerSequence seq : value)
					{
						if (seq instanceof KeySequence)
						{
							KeySequence keySequence = (KeySequence) seq;
							if (KeyBindingHelper.matchesKeybinding(keyCode, stateMask, keySequence))
							{
								consumed = true;
								IHandlerService handlerService = (IHandlerService) textEditor.getSite().getService(
										IHandlerService.class);
								try
								{
									handlerService.executeCommand(entry.getKey(), null);
								}
								catch (NotEnabledException e1)
								{
									// Ignore it in this case (i.e.: undo will only be enabled if there's something
									// to be undone).
								}
								catch (Exception e1)
								{
									FindBarPlugin.log(e1);
								}

								return consumed;
							}
						}
					}
				}
			}
			return consumed;
		}

	}

	private class HideFindBarHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			FindBarDecorator dec = findBarDecorator.get();
			if (dec != null)
			{
				dec.hideFindBar();
			}
			return null;
		}
	}

	private class FindPreviousHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			FindBarDecorator dec = findBarDecorator.get();
			if (dec != null)
			{
				dec.findPrevious();
			}
			return null;
		}
	}

	private class FindNextHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			FindBarDecorator dec = findBarDecorator.get();
			if (dec != null)
			{
				dec.findNext();
			}
			return null;
		}
	}

	private class FindNextOrPrevHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			FindBarDecorator dec = findBarDecorator.get();
			if (dec != null)
			{
				dec.findNextOrPrev();
			}
			return null;
		}
	}

	private class FocusFindFindBarHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			FindBarDecorator dec = findBarDecorator.get();
			if (dec != null)
			{
				dec.combo.setFocus();
				dec.combo.setSelection(new Point(0, dec.combo.getText().length()));
			}
			return null;
		}
	}

	private class FocusFindOrOpenEclipseSearchFindBarHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			FindBarDecorator dec = findBarDecorator.get();
			if (dec != null)
			{
				IPreferenceStore preferenceStore = FindBarPlugin.getDefault().getPreferenceStore();
				boolean openEclipseFindBar = preferenceStore
						.getBoolean(IPreferencesConstants.CTRL_F_TWICE_OPENS_ECLIPSE_FIND_BAR);

				if (openEclipseFindBar)
				{
					dec.showFindReplaceDialog();
				}
				else
				{
					dec.combo.setFocus();
					dec.combo.setSelection(new Point(0, dec.combo.getText().length()));
				}
			}
			return null;
		}
	}

	private class FocusReplaceFindBarHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			FindBarDecorator dec = findBarDecorator.get();
			if (dec != null)
			{
				dec.comboReplace.setFocus();
				dec.comboReplace.setSelection(new Point(0, dec.comboReplace.getText().length()));
			}
			return null;
		}
	}

	private class ToggleCaseFindBarHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			FindBarDecorator dec = findBarDecorator.get();
			if (dec != null)
			{
				FindBarConfiguration conf = dec.getConfiguration();
				conf.setCaseSensitive(!conf.getCaseSensitive());
			}
			return null;
		}
	}

	private class ToggleWordFindBarHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			FindBarDecorator dec = findBarDecorator.get();
			if (dec != null)
			{
				FindBarConfiguration conf = dec.getConfiguration();
				conf.setWholeWord(!conf.getWholeWord());
			}
			return null;
		}
	}

	private class ToggleRegexpFindBarHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			FindBarDecorator dec = findBarDecorator.get();
			if (dec != null)
			{
				FindBarConfiguration conf = dec.getConfiguration();
				conf.setRegularExpression(!conf.getRegularExpression());
			}
			return null;
		}
	}

	private class ToggleSearchBackwardFindBarHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			FindBarDecorator dec = findBarDecorator.get();
			if (dec != null)
			{
				FindBarConfiguration conf = dec.getConfiguration();
				conf.setSearchBackward(!conf.getSearchBackward());
			}
			return null;
		}
	}

	private class SearchInOpenFilesFindBarHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			FindBarDecorator dec = findBarDecorator.get();
			if (dec != null)
			{
				dec.searchInOpenFiles();
			}
			return null;
		}
	}

	private class ShowOptionsFindBarHandler extends AbstractHandler
	{
		public Object execute(ExecutionEvent event) throws ExecutionException
		{
			FindBarDecorator dec = findBarDecorator.get();
			if (dec != null)
			{
				dec.showOptions(false);
			}
			return null;
		}
	}

	private void setFindBarContextActive(boolean activate)
	{
		fActivated = activate;
		IWorkbenchPartSite site = textEditor.getSite();
		IContextService contextService = (IContextService) site.getService(IContextService.class);
		IHandlerService handlerService = (IHandlerService) site.getService(IHandlerService.class);
		IBindingService service = (IBindingService) site.getService(IBindingService.class);

		if (activate)
		{

			// These will be the only active commands (note that they may have multiple keybindings
			// defined in plugin.xml)
			for (Map.Entry<String, AbstractHandler> entry : fCommandToHandler.entrySet())
			{
				AbstractHandler handler = entry.getValue();
				if (handler != null)
				{
					fHandlerActivations.add(handlerService.activateHandler(entry.getKey(), handler));
				}
			}

			// Yes, no longer execute anything from the binding service (we'll do our own handling so that the commands
			// we need still get executed).
			service.setKeyFilterEnabled(false);

			service.addBindingManagerListener(fClearCommandToBindingOnChangesListener);
			findBarContextActivation = contextService.activateContext("org.eclipse.ui.textEditorScope.findbar"); //$NON-NLS-1$
		}
		else
		{
			fCommandToBinding = null;
			service.setKeyFilterEnabled(true);
			if (findBarContextActivation != null)
			{
				service.removeBindingManagerListener(fClearCommandToBindingOnChangesListener);
				handlerService.deactivateHandlers(fHandlerActivations);
				fHandlerActivations.clear();
				contextService.deactivateContext(findBarContextActivation);
				findBarContextActivation = null;
			}
		}
	}

	/**
	 * Updates the list of commands -> binding available.
	 */
	public void updateCommandToBinding()
	{
		fCommandToBinding = getCommandToBindings();
		FindBarDecorator dec = findBarDecorator.get();
		if (dec != null)
		{
			// Whenever we get the bindings, update the tooltips accordingly.
			updateTooltip(SEARCH_IN_OPEN_FILES_COMMAND_ID, Messages.FindBarDecorator_TOOLTIP_SearchInOpenFiles,
					dec.searchInOpenFiles);
			updateTooltip(TOGGLE_WORD_MATCHING_COMMAND_ID, Messages.FindBarDecorator_LABEL_WholeWord, dec.wholeWord);
			updateTooltip(TOGGLE_CASE_MATCHING_COMMAND_ID, Messages.FindBarDecorator_LABEL_CaseSensitive,
					dec.caseSensitive);
			if (dec.regularExpression != null)
			{
				updateTooltip(TOGGLE_REGEXP_MATCHING_COMMAND_ID, Messages.FindBarDecorator_LABEL_RegularExpression,
						dec.regularExpression);
			}
			updateTooltip(TOGGLE_SEARCH_BACKWARD_COMMAND_ID, Messages.FindBarDecorator_LABEL_SearchBackward,
					dec.searchBackward);
			updateTooltip(SHOW_OPTIONS_COMMAND_ID, Messages.FindBarDecorator_LABEL_ShowOptions, dec.options);

			List<TriggerSequence> bindings = fCommandToBinding.get(FOCUS_REPLACE_COMMAND_ID);
			if (bindings != null && bindings.size() > 0)
			{
				dec.comboReplace.setToolTipText(Messages.FindBarActions_TOOLTIP_FocusReplaceCombo + bindings.get(0));
			}

			bindings = fCommandToBinding.get(FOCUS_FIND_COMMAND_ID);
			if (bindings != null && bindings.size() > 0)
			{
				dec.combo.setToolTipText(Messages.FindBarActions_TOOLTIP_FocusFindCombo + bindings.get(0));
			}
		}

	}

	private void updateTooltip(String commandId, String tooltip, Button button)
	{
		List<TriggerSequence> bindings = fCommandToBinding.get(commandId);
		if (bindings != null && bindings.size() > 0)
		{
			button.setToolTipText(MessageFormat.format("{0} ({1})", tooltip, bindings.get(0))); //$NON-NLS-1$
		}
		else
		{
			button.setToolTipText(tooltip);
		}
	}

	private void updateTooltip(String commandId, String tooltip, ToolItem item)
	{
		List<TriggerSequence> bindings = fCommandToBinding.get(commandId);
		if (bindings != null && bindings.size() > 0)
		{
			item.setToolTipText(MessageFormat.format("{0} ({1})", tooltip, bindings.get(0))); //$NON-NLS-1$
		}
		else
		{
			item.setToolTipText(tooltip);
		}
	}

	/**
	 * @return a map with the commands -> bindings available.
	 */
	public HashMap<String, List<TriggerSequence>> getCommandToBindings()
	{
		HashMap<String, List<TriggerSequence>> commandToBinding = new HashMap<String, List<TriggerSequence>>();

		IWorkbenchPartSite site = textEditor.getSite();
		IBindingService service = (IBindingService) site.getService(IBindingService.class);
		Binding[] bindings = service.getBindings();

		for (Map.Entry<String, AbstractHandler> entry : fCommandToHandler.entrySet())
		{
			List<TriggerSequence> seq = new ArrayList<TriggerSequence>();
			commandToBinding.put(entry.getKey(), seq);
			for (Binding binding : bindings)
			{
				ParameterizedCommand command = binding.getParameterizedCommand();
				if (command != null)
				{
					if (entry.getKey().equals(command.getId()))
					{
						seq.add(binding.getTriggerSequence());
					}
				}
			}

		}
		return commandToBinding;
	}

	public boolean isActivated()
	{
		return fActivated;
	}

	/**
	 * Creates a listener to manage the focus on buttons.
	 */
	public FocusListener createFocusListener(Button button)
	{
		return new FindBarControlFocusListener(button);
	}

	/**
	 * Creates a listener to manage the focus on combos.
	 */
	public FocusListener createFocusListener(Combo combo)
	{
		final WeakReference<Combo> weakCombo = new WeakReference<Combo>(combo);
		return new FindBarControlFocusListener(combo)
		{
			public void focusGained(FocusEvent e)
			{
				Combo c = weakCombo.get();
				if (c != null)
				{
					c.setBackground(null);
				}
				super.focusGained(e);
			}
		};
	}
}
