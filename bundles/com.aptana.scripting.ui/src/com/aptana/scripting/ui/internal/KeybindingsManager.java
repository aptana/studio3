/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui.internal;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.commands.contexts.ContextManagerEvent;
import org.eclipse.core.commands.contexts.IContextManagerListener;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.bindings.Binding;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.keys.BindingService;
import org.eclipse.ui.internal.keys.WorkbenchKeyboard;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.ui.services.IEvaluationService;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.scripting.ScriptingActivator;
import com.aptana.scripting.model.AbstractElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.LoadCycleListener;
import com.aptana.scripting.model.filters.IModelFilter;
import com.aptana.scripting.ui.ICommandElementsProvider;
import com.aptana.scripting.ui.KeyBindingUtil;
import com.aptana.scripting.ui.ScriptingUIPlugin;
import com.aptana.ui.keybinding.KeyBindingHelper;

@SuppressWarnings("restriction")
public class KeybindingsManager implements LoadCycleListener
{

	private static final String MNEMONICS = "123456789"; //$NON-NLS-1$

	private static final AtomicBoolean installed = new AtomicBoolean(false);

	private static KeybindingsManager INSTANCE;

	/**
	 * The workbench on which this KeybindingsManager should act.
	 */
	private final IWorkbench workbench;

	private final Listener listener = new Listener()
	{
		@SuppressWarnings("rawtypes")
		public void handleEvent(Event event)
		{
			// Not enabled - simply return
			if (!enabled)
			{
				return;
			}

			// Showing commands menu - simply return
			if (showingCommandsMenu)
			{
				return;
			}

			/*
			 * If this is not a keyboard event, then there are no key strokes. This can happen if we are listening to
			 * focus traversal events.
			 */
			if ((event.stateMask == 0) && (event.keyCode == 0) && (event.character == 0))
			{
				return;
			}

			if (!KeyBindingHelper.isKeyEventComplete(event))
			{
				return;
			}

			// Generate possible key strokes - we only handle the first one right now
			List possibleKeyStrokes = WorkbenchKeyboard.generatePossibleKeyStrokes(event);
			if (possibleKeyStrokes.size() > 0)
			{
				// Process the key strokes
				processKeyStrokes(event, possibleKeyStrokes);
			}
		}
	};

	/**
	 * This is used to store prefix of the multi-stroke key sequence.
	 */
	private final KeyBindingState state;

	/**
	 * The window listener responsible for maintaining internal state as the focus moves between windows on the desktop.
	 */
	private final IWindowListener windowListener = new IWindowListener()
	{

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.ui.IWindowListener#windowActivated(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowActivated(IWorkbenchWindow window)
		{
			checkActiveWindow(window);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.ui.IWindowListener#windowClosed(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowClosed(IWorkbenchWindow window)
		{
			// Do nothing.
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.ui.IWindowListener#windowDeactivated(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowDeactivated(IWorkbenchWindow window)
		{
			// Do nothing
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.ui.IWindowListener#windowOpened(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowOpened(IWorkbenchWindow window)
		{
			// Do nothing.
		}
	};

	private boolean enabled;
	private boolean showingCommandsMenu;

	private final IContextManagerListener contextManagerListener = new IContextManagerListener()
	{
		public void contextManagerChanged(ContextManagerEvent contextManagerEvent)
		{
			setEnabled(contextManagerEvent.getContextManager().getActiveContextIds()
					.contains(ScriptingUIPlugin.SCRIPTING_CONTEXT_ID));
		}
	};

	private static class MutexRule implements ISchedulingRule
	{
		public boolean isConflicting(ISchedulingRule rule)
		{
			return rule == this;
		}

		public boolean contains(ISchedulingRule rule)
		{
			return rule == this;
		}
	};

	private static final MutexRule MUTEX_RULE = new MutexRule();

	private BundleManager bundleManager;

	private final Set<KeySequence> uniqueKeySequences;
	private final Set<KeySequence> uniqueKeySequencesPrefixes;

	private Job workbenchJob;

	/**
	 * Install the KeybindingsManager.
	 */
	public static void install()
	{
		if (installed.compareAndSet(false, true))
		{
			IWorkbench workbench = null;
			try
			{
				workbench = PlatformUI.getWorkbench();
			}
			catch (Exception e)
			{
				// ignore, may be running headless, like in tests
			}
			if (workbench != null)
			{
				INSTANCE = new KeybindingsManager(workbench);

				// Load initial bindings
				INSTANCE.initBindings();
			}
		}
	}

	/**
	 * Uninstall the KeybindingsManager.
	 */
	public static void uninstall()
	{
		if (installed.compareAndSet(true, false))
		{
			if (INSTANCE != null)
			{
				INSTANCE.dispose();
			}
			INSTANCE = null;
		}
	}

	private KeybindingsManager(IWorkbench workbench)
	{
		this.workbench = workbench;
		state = new KeyBindingState(workbench);
		workbench.addWindowListener(windowListener);

		uniqueKeySequences = new HashSet<KeySequence>();
		uniqueKeySequencesPrefixes = new HashSet<KeySequence>();

		bundleManager = BundleManager.getInstance();
		bundleManager.addLoadCycleListener(this);
	}

	private void dispose()
	{
		WorkbenchJob job = new WorkbenchJob("Disposing KeybindingsManager") //$NON-NLS-1$
		{
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				workbench.getDisplay().removeFilter(SWT.KeyDown, listener);
				workbench.getDisplay().removeFilter(SWT.Traverse, listener);
				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(job);
		job.schedule();
	}

	private void initBindings()
	{
		WorkbenchJob workbenchJob = new WorkbenchJob("Installing KeybindingsManager") //$NON-NLS-1$
		{
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{

				loadbindings();

				// Insert our key listener before the Eclipse's key listeners
				IBindingService bindingService = (IBindingService) workbench.getService(IBindingService.class);
				if (bindingService instanceof BindingService)
				{
					final BindingService theBindingService = (BindingService) bindingService;
					Display display = PlatformUI.getWorkbench().getDisplay();
					final WorkbenchKeyboard keyboard = theBindingService.getKeyboard();
					Listener keyDownFilter = keyboard.getKeyDownFilter();
					try
					{
						if (keyDownFilter != null)
						{
							display.removeFilter(SWT.KeyDown, keyDownFilter);
							display.removeFilter(SWT.Traverse, keyDownFilter);
						}
						display.addFilter(SWT.KeyDown, listener);
						display.addFilter(SWT.Traverse, listener);
					}
					finally
					{
						if (keyDownFilter != null)
						{
							display.addFilter(SWT.KeyDown, keyDownFilter);
							display.addFilter(SWT.Traverse, keyDownFilter);
						}
					}
				}

				// Set the initial enabled state of KeybindingsManager
				IContextService contextService = (IContextService) workbench.getService(IContextService.class);
				contextService.addContextManagerListener(contextManagerListener);
				setEnabled(contextService.getActiveContextIds().contains(ScriptingUIPlugin.SCRIPTING_CONTEXT_ID));

				return Status.OK_STATUS;
			}
		};
		workbenchJob.setRule(MUTEX_RULE);
		EclipseUtil.setSystemForJob(workbenchJob);
		workbenchJob.setPriority(Job.LONG);
		workbenchJob.schedule();
	}

	private void reloadbindings()
	{
		if (workbenchJob == null)
		{
			workbenchJob = new Job("Reloading KeybindingsManager") //$NON-NLS-1$
			{
				@Override
				public IStatus run(IProgressMonitor monitor)
				{
					if (monitor != null && monitor.isCanceled())
					{
						return Status.CANCEL_STATUS;
					}
					try
					{
						loadbindings();
					}
					finally
					{
					}
					return Status.OK_STATUS;
				}

				public boolean shouldRun()
				{
					return PlatformUI.isWorkbenchRunning();
				};

			};
			workbenchJob.setRule(MUTEX_RULE);
			EclipseUtil.setSystemForJob(workbenchJob);
			workbenchJob.setPriority(Job.LONG);
		}
		else
		{
			workbenchJob.cancel();
		}
		workbenchJob.schedule(100);
	}

	private void loadbindings()
	{
		resetState();

		// Collect unique key sequences
		uniqueKeySequences.clear();
		uniqueKeySequencesPrefixes.clear();

		// Filter to commands with bindings
		IModelFilter filter = new IModelFilter()
		{

			public boolean include(AbstractElement element)
			{
				boolean result = false;

				if (element instanceof CommandElement)
				{
					CommandElement node = (CommandElement) element;
					String[] bindings = node.getKeyBindings();
					result = (bindings != null && bindings.length > 0);
				}

				return result;
			}
		};

		// Get all commands with bindings
		List<CommandElement> commands = bundleManager.getExecutableCommands(filter);
		for (CommandElement commandElement : commands)
		{
			// Get key sequences
			KeySequence[] keySequences = KeyBindingUtil.getKeySequences(commandElement);
			if (keySequences != null && keySequences.length > 0)
			{
				// Add to the set
				for (KeySequence keySequence : keySequences)
				{
					boolean added = uniqueKeySequences.add(keySequence);
					if (added && keySequence.getKeyStrokes().length > 1)
					{
						// Collect prefixes - used to determine prefix matching of key sequences
						TriggerSequence[] prefixes = keySequence.getPrefixes();
						for (TriggerSequence triggerSequence : prefixes)
						{
							KeySequence prefixKeySequence = (KeySequence) triggerSequence;
							if (prefixKeySequence.getKeyStrokes().length > 0)
							{
								uniqueKeySequencesPrefixes.add(prefixKeySequence);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Set the enabled state of KeybindingsManager.
	 * 
	 * @param enabled
	 */
	private void setEnabled(boolean enabled)
	{
		if (this.enabled == enabled)
		{
			return;
		}
		this.enabled = enabled;
		if (!enabled)
		{
			resetState();
		}
	}

	@SuppressWarnings("rawtypes")
	private void processKeyStrokes(Event event, List keyStrokes)
	{
		boolean consume = false;
		if (keyStrokes.size() > 0)
		{
			consume = processKeyStroke(event, (KeyStroke) keyStrokes.get(0));
		}
		if (consume)
		{
			// Consume the event if it was handled
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

	private boolean processKeyStroke(Event event, KeyStroke keyStroke)
	{
		IBindingService bindingService = (IBindingService) workbench.getService(IBindingService.class);
		KeySequence sequenceBeforeKeyStroke = state.getCurrentSequence();
		KeySequence sequenceAfterKeyStroke = KeySequence.getInstance(sequenceBeforeKeyStroke, keyStroke);

		if (uniqueKeySequences.contains(sequenceAfterKeyStroke))
		{
			IEvaluationService evaluationService = (IEvaluationService) workbench.getService(IEvaluationService.class);
			IEvaluationContext evaluationContext = evaluationService.getCurrentState();
			IWorkbenchPart workbenchPart = (IWorkbenchPart) evaluationContext.getVariable(ISources.ACTIVE_PART_NAME);
			ICommandElementsProvider commandElementsProvider = (ICommandElementsProvider) workbenchPart
					.getAdapter(ICommandElementsProvider.class);
			if (commandElementsProvider != null)
			{
				// Is there a Eclipse binding that matches the key sequence?
				Binding binding = null;
				if (bindingService.isPerfectMatch(sequenceAfterKeyStroke))
				{
					// Record it
					binding = bindingService.getPerfectMatch(sequenceAfterKeyStroke);
				}

				List<CommandElement> commandElements = commandElementsProvider
						.getCommandElements(sequenceAfterKeyStroke);
				if (commandElements.size() == 0)
				{
					if (binding == null)
					{
						// Remember the prefix
						incrementState(sequenceAfterKeyStroke);
					}
					else
					{
						// Reset our state
						resetState();
					}

					// Do not consume the event. Let Eclipse handle it.
					return false;
				}
				else
				{
					if (binding == null && commandElements.size() == 1)
					{
						// We have a unique scripting command to execute
						executeCommandElement(commandElementsProvider, commandElements.get(0));

						// Reset our state
						resetState();

						// The event should be consumed
						return true;
					}
					else
					{
						// We need to show commands menu to the user
						IContextService contextService = (IContextService) workbench.getService(IContextService.class);
						popup(workbenchPart.getSite().getShell(), bindingService, contextService,
								commandElementsProvider, commandElements, event, binding,
								getInitialLocation(commandElementsProvider));

						// Reset our state
						resetState();

						// The event should be consumed
						return true;
					}
				}
			}
		}
		else if (uniqueKeySequencesPrefixes.contains(sequenceAfterKeyStroke))
		{
			// Prefix match

			// Is there a Eclipse command with a perfect match
			if (bindingService.isPerfectMatch(sequenceAfterKeyStroke))
			{
				// Reset our state
				resetState();
			}
			else
			{
				// Remember the prefix
				incrementState(sequenceAfterKeyStroke);
			}
		}
		else
		{
			// Reset our state
			resetState();
		}

		// We did not handle the event. Do not consume the event. Let Eclipse handle it.
		return false;
	}

	/**
	 * Reset the state if the active window associated with the state changes.
	 * 
	 * @param window
	 */
	private void checkActiveWindow(IWorkbenchWindow window)
	{
		if (!window.equals(state.getAssociatedWindow()))
		{
			resetState();
			state.setAssociatedWindow(window);
		}
	}

	/**
	 * Changes the key binding state to the given key sequence.
	 * 
	 * @param sequence
	 *            The new key sequence for the state; should not be <code>null</code>.
	 */
	private void incrementState(KeySequence sequence)
	{
		// Update the state.
		state.setCurrentSequence(sequence);
		state.setAssociatedWindow(workbench.getActiveWorkbenchWindow());
	}

	/**
	 * Resets the state.
	 */
	private void resetState()
	{
		state.reset();
	}

	// FIXME Combine with MenuDialog found in com.aptana.scripting.ui!
	// Shows the commands menu.
	private void popup(final Shell shell, final IBindingService bindingService, final IContextService contextService,
			final ICommandElementsProvider commandElementsProvider, final List<CommandElement> commandElements,
			final Event event, final Binding binding, final Point initialLocation)
	{
		PopupDialog popupDialog = new PopupDialog(shell, PopupDialog.INFOPOPUP_SHELLSTYLE, true, false, false, false,
				false, null, null)
		{

			@Override
			protected Point getInitialLocation(Point initialSize)
			{
				Display display = shell.getDisplay();
				Point cursorLocation = display.getCursorLocation();
				if (initialLocation != null)
				{
					// Warp the cursor ?
					// if (!cursorLocation.equals(initialLocation))
					// {
					// display.setCursorLocation(initialLocation);
					// }
					return initialLocation;
				}
				return cursorLocation;
			}

			protected Control createDialogArea(Composite parent)
			{

				registerShellType();

				// Create a composite for the dialog area.
				final Composite composite = new Composite(parent, SWT.NONE);
				final GridLayout compositeLayout = new GridLayout();
				compositeLayout.marginHeight = 1;
				compositeLayout.marginWidth = 1;
				composite.setLayout(compositeLayout);
				composite.setLayoutData(new GridData(GridData.FILL_BOTH));

				// Layout the table.
				final Table commandElementTable = new Table(composite, SWT.FULL_SELECTION | SWT.SINGLE | SWT.NO_SCROLL);
				final GridData gridData = new GridData(GridData.FILL_BOTH);
				commandElementTable.setLayoutData(gridData);
				commandElementTable.setLinesVisible(true);

				// Initialize the columns and rows.
				final TableColumn columnCommandName = new TableColumn(commandElementTable, SWT.LEFT, 0);
				final TableColumn columnAccelerator = new TableColumn(commandElementTable, SWT.CENTER, 1);

				int mnemonic = 0;
				for (CommandElement commandElement : commandElements)
				{
					final String[] text = { commandElement.getDisplayName(),
							(mnemonic < MNEMONICS.length() ? String.valueOf(MNEMONICS.charAt(mnemonic++)) : "") }; //$NON-NLS-1$
					final TableItem item = new TableItem(commandElementTable, SWT.NULL);
					item.setText(text);
					item.setData(CommandElement.class.getName(), commandElement);
				}

				if (binding != null)
				{
					ParameterizedCommand originalParameterizedCommand = binding.getParameterizedCommand();
					// Add original command
					if (originalParameterizedCommand != null)
					{
						try
						{
							String name = originalParameterizedCommand.getName();
							final TableItem item = new TableItem(commandElementTable, SWT.NULL);
							item.setText(new String[] { name,
									(mnemonic < MNEMONICS.length() ? String.valueOf(MNEMONICS.charAt(mnemonic++)) : "") }); //$NON-NLS-1$
							item.setData(ParameterizedCommand.class.getName(), originalParameterizedCommand);
						}
						catch (NotDefinedException nde)
						{
							IdeLog.logError(ScriptingActivator.getDefault(), nde.getMessage(), nde);
						}
					}
				}

				Dialog.applyDialogFont(parent);
				columnAccelerator.pack();
				columnCommandName.pack();
				columnAccelerator.setWidth(columnAccelerator.getWidth() * 4);

				/*
				 * If the user double-clicks on the table row, it should execute the selected command.
				 */
				commandElementTable.addListener(SWT.DefaultSelection, new Listener()
				{
					public final void handleEvent(final Event event)
					{
						// Try to execute the corresponding command.
						Object commandElement = null;
						Object parameterizedCommand = null;
						final TableItem[] selection = commandElementTable.getSelection();
						if (selection.length > 0)
						{
							commandElement = selection[0].getData(CommandElement.class.getName());
							parameterizedCommand = selection[0].getData(ParameterizedCommand.class.getName());
						}
						close();
						if (commandElement instanceof CommandElement)
						{
							executeCommandElement(commandElementsProvider, (CommandElement) commandElement);
						}
						else if (parameterizedCommand instanceof ParameterizedCommand)
						{
							try
							{
								executeCommand(binding, event);
							}
							catch (CommandException e)
							{
								IdeLog.logError(ScriptingActivator.getDefault(), e.getMessage(), e);
							}
						}
					}
				});

				commandElementTable.addKeyListener(new KeyListener()
				{
					public void keyReleased(KeyEvent e)
					{
					}

					public void keyPressed(KeyEvent e)
					{
						if (!e.doit)
						{
							return;
						}
						int index = MNEMONICS.indexOf(e.character);
						if (index != -1)
						{
							if (index < commandElementTable.getItemCount())
							{
								e.doit = false;
								TableItem tableItem = commandElementTable.getItem(index);
								Object commandElement = tableItem.getData(CommandElement.class.getName());
								Object parameterizedCommand = tableItem.getData(ParameterizedCommand.class.getName());
								close();
								if (commandElement instanceof CommandElement)
								{
									executeCommandElement(commandElementsProvider, (CommandElement) commandElement);
								}
								else if (parameterizedCommand instanceof ParameterizedCommand)
								{
									try
									{
										executeCommand(binding, event);
									}
									catch (CommandException ex)
									{
										IdeLog.logError(ScriptingActivator.getDefault(), ex.getMessage(), ex);
									}
								}
							}
						}
					}
				});
				return composite;
			}

			protected Color getBackground()
			{
				return getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
			}

			@Override
			protected Control createContents(Composite parent)
			{
				return super.createContents(parent);
			}

			@Override
			public int open()
			{
				showingCommandsMenu = true;
				bindingService.setKeyFilterEnabled(false);
				return super.open();
			}

			@Override
			public boolean close()
			{
				boolean closed = super.close();
				if (closed)
				{
					showingCommandsMenu = false;
					bindingService.setKeyFilterEnabled(true);
				}
				return closed;
			}

			/**
			 * Registers the shell as the same type as its parent with the context support. This ensures that it does
			 * not modify the current state of the application.
			 */
			private final void registerShellType()
			{
				final Shell shell = getShell();
				contextService.registerShell(shell, contextService.getShellType((Shell) shell.getParent()));
			}
		};

		popupDialog.open();
	}

	private Point getInitialLocation(ICommandElementsProvider commandElementsProvider)
	{
		// Ask the commandElementsProvider to provide the location
		Point popupLocation = commandElementsProvider.getCommandElementsPopupLocation();
		if (popupLocation != null)
		{
			return popupLocation;
		}

		Display display = workbench.getDisplay();
		if (display != null)
		{
			return display.getCursorLocation();
		}

		return null;
	}

	private void executeCommandElement(ICommandElementsProvider commandElementsProvider, CommandElement commandElement)
	{
		commandElementsProvider.execute(commandElement);
	}

	/**
	 * Performs the actual execution of the command by looking up the current handler from the command manager. If there
	 * is a handler and it is enabled, then it tries the actual execution. Execution failures are logged.
	 * 
	 * @param binding
	 *            The binding that should be executed; should not be <code>null</code>.
	 * @param trigger
	 *            The triggering event; may be <code>null</code>.
	 * @return <code>true</code> if there was a handler; <code>false</code> otherwise.
	 * @throws CommandException
	 *             if the handler does not complete execution for some reason. It is up to the caller of this method to
	 *             decide whether to log the message, display a dialog, or ignore this exception entirely.
	 */
	final void executeCommand(final Binding binding, final Event trigger) throws CommandException
	{
		final ParameterizedCommand parameterizedCommand = binding.getParameterizedCommand();

		// Dispatch to the handler.
		final IHandlerService handlerService = (IHandlerService) workbench.getService(IHandlerService.class);
		final Command command = parameterizedCommand.getCommand();
		command.setEnabled(handlerService.getCurrentState());

		try
		{
			handlerService.executeCommand(parameterizedCommand, trigger);
		}
		catch (final NotDefinedException e)
		{
			// The command is not defined. Forwarded to the IExecutionListener.
		}
		catch (final NotEnabledException e)
		{
			// The command is not enabled. Forwarded to the IExecutionListener.
		}
		catch (final NotHandledException e)
		{
			// There is no handler. Forwarded to the IExecutionListener.
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.LoadCycleListener#scriptLoaded(java.io.File)
	 */
	public void scriptLoaded(File script)
	{
		reloadbindings();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.LoadCycleListener#scriptReloaded(java.io.File)
	 */
	public void scriptReloaded(File script)
	{
		reloadbindings();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.LoadCycleListener#scriptUnloaded(java.io.File)
	 */
	public void scriptUnloaded(File script)
	{
		reloadbindings();
	}

}
