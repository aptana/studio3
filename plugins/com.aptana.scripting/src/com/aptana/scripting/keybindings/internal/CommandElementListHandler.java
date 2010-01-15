package com.aptana.scripting.keybindings.internal;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.bindings.Binding;
import org.eclipse.jface.bindings.keys.KeySequence;
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
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;

import com.aptana.scripting.Activator;
import com.aptana.scripting.keybindings.ICommandElementsProvider;
import com.aptana.scripting.model.CommandElement;

/**
 * This proxies the CommandElements and possibly a ParameterizedCommand that is bound to the given key sequence.
 *
 * @author schitale
 */
class CommandElementListHandler extends AbstractHandler
{
	private static final String MNEMONICS = "123456789"; //$NON-NLS-1$

	private final KeySequence keySequence;
	private final ParameterizedCommand originalParameterizedCommand;
	private final Binding originalBinding;
	private IHandlerActivation activateHandler;

	CommandElementListHandler(KeySequence keySequence, ParameterizedCommand originalParameterizedCommand, Binding originalBinding)
	{
		this.keySequence = keySequence;
		this.originalParameterizedCommand = originalParameterizedCommand;
		this.originalBinding = originalBinding;
	}

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		Object applicationContext = event.getApplicationContext();
		if (applicationContext instanceof IEvaluationContext)
		{
			IEvaluationContext evaluationContext = (IEvaluationContext) applicationContext;
			IWorkbenchPart workbenchPart = (IWorkbenchPart) evaluationContext.getVariable(ISources.ACTIVE_PART_NAME);
			IHandlerService handlerService = (IHandlerService) workbenchPart.getSite()
					.getService(IHandlerService.class);
			ICommandElementsProvider commandElementsProvider = (ICommandElementsProvider) workbenchPart
					.getAdapter(ICommandElementsProvider.class);
			if (commandElementsProvider != null)
			{
				List<CommandElement> commandElements = commandElementsProvider.getCommandElements(keySequence);
				if (commandElements == null || commandElements.size() == 0)
				{
					if (originalParameterizedCommand != null)
					{
						executeParameterizedCommand(handlerService, originalParameterizedCommand);
					}
					return null;
				}
				// Is there only one command
				if (commandElements.size() == 1 && originalParameterizedCommand == null)
				{
					// Execute right away!
					executeCommandElement(commandElementsProvider, commandElements.get(0));
				}
				else
				{
					// Show a pop-up
					IContextService contextService = (IContextService) workbenchPart.getSite().getService(
							IContextService.class);
					Shell shell = workbenchPart.getSite().getShell();
					popup(shell, contextService, handlerService, commandElementsProvider, commandElements, getInitialLocation(commandElementsProvider));
				}
			}
			else
			{
				if (originalParameterizedCommand != null)
				{
					executeParameterizedCommand(handlerService, originalParameterizedCommand);
				}
			}
		}
		return null;
	}

	private void executeCommandElement(ICommandElementsProvider commandElementsProvider, CommandElement commandElement)
	{
		commandElementsProvider.execute(commandElement);
	}

	private void executeParameterizedCommand(IHandlerService handlerService, ParameterizedCommand parameterizedCommand)
	{
		try
		{
			handlerService.executeCommand(parameterizedCommand, null);
		}
		catch (ExecutionException ee)
		{
			Activator.logError(ee.getMessage(), ee);
		}
		catch (NotDefinedException nde)
		{
			// ignore
		}
		catch (NotEnabledException e1)
		{
			// ignore
		}
		catch (NotHandledException e1)
		{
			// ignore
		}
	}

	KeySequence getKeySequence()
	{
		return keySequence;
	}

	Binding getOriginalBinding()
	{
		return originalBinding;
	}

	IHandlerActivation getActivateHandler()
	{
		return activateHandler;
	}

	void setActivationHandler(IHandlerActivation activateHandler)
	{
		this.activateHandler = activateHandler;
	}

	private void popup(final Shell shell, final IContextService contextService, final IHandlerService handlerService,
			final ICommandElementsProvider commandElementsProvider, final List<CommandElement> commandElements,
			final Point initialLocation)
	{
		PopupDialog popupDialog = new PopupDialog(shell, PopupDialog.INFOPOPUP_SHELLSTYLE, true, false, false, false,
				false, null, null)
		{

			@Override
			protected Point getInitialLocation(Point initialSize)
			{
				if (initialLocation != null) {
					return initialLocation;
				}
				return shell.getDisplay().getCursorLocation();
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
						// ignore
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
							;
						}
						close();
						if (commandElement instanceof CommandElement)
						{
							executeCommandElement(commandElementsProvider, (CommandElement) commandElement);
						}
						else if (parameterizedCommand instanceof ParameterizedCommand)
						{
							executeParameterizedCommand(handlerService, (ParameterizedCommand) parameterizedCommand);
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
									executeParameterizedCommand(handlerService,
											(ParameterizedCommand) parameterizedCommand);
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
		// TODO Ask the commandElementsProvider to provide the location
		Display display = PlatformUI.getWorkbench().getDisplay();
		if (display != null)
		{
			return display.getCursorLocation();
		}

		return null;
	}
}
