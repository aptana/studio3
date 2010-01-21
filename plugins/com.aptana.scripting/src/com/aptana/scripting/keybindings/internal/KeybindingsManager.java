package com.aptana.scripting.keybindings.internal;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.bindings.Binding;
import org.eclipse.jface.bindings.BindingManager;
import org.eclipse.jface.bindings.keys.KeyBinding;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.IWorkbenchRegistryConstants;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.progress.WorkbenchJob;

import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.LoadCycleListener;
import com.aptana.scripting.model.SnippetElement;

@SuppressWarnings("restriction")
public class KeybindingsManager implements LoadCycleListener, IPropertyChangeListener
{
	private static final String SCRIPTING_COMMAND_ID_PREFIX = "com.aptana.scripting.keybindings."; //$NON-NLS-1$

	private static final AtomicBoolean installed = new AtomicBoolean(false);
	private BundleManager bundleManager;
	private IBindingService bindingService;
	private ICommandService commandService;
	private final IHandlerService handlerService;

	private static class MutexRule implements ISchedulingRule {
		public boolean isConflicting(ISchedulingRule rule) {
			return rule == this;
		}
		public boolean contains(ISchedulingRule rule) {
			return rule == this;
		}
	};

	private static final MutexRule MUTEX_RULE = new MutexRule();

	private static List<CommandElementListHandler> commandElementListHandlers = new LinkedList<CommandElementListHandler>();

	public static final void install()
	{
		if (installed.compareAndSet(false, true))
		{
			IWorkbench workbench = PlatformUI.getWorkbench();
			IBindingService bindingService = (IBindingService) workbench.getService(IBindingService.class);
			ICommandService commandService = (ICommandService) workbench.getService(ICommandService.class);
			IHandlerService handlerService = (IHandlerService) workbench.getService(IHandlerService.class);
			KeybindingsManager keybindingsManager = new KeybindingsManager(bindingService, commandService,
					handlerService);

			// Load initial bindings
			keybindingsManager.initBindings();
		}
	}

	private KeybindingsManager(IBindingService bindingService, ICommandService commandService,
			IHandlerService handlerService)
	{
		this.bindingService = bindingService;
		this.commandService = commandService;
		this.handlerService = handlerService;

		bundleManager = BundleManager.getInstance();
		bundleManager.addLoadCycleListener(this);

		WorkbenchPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
	}

	private void initBindings() {
		WorkbenchJob workbenchJob = new WorkbenchJob("Installing KeybindingsManager") //$NON-NLS-1$
		{
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				try {
					loadbindings();
				} finally {
				}
				return Status.OK_STATUS;
			}
		};
		workbenchJob.setRule(MUTEX_RULE);
		workbenchJob.setSystem(true);
		workbenchJob.setPriority(Job.LONG);
		workbenchJob.schedule();
	}

	private void reloadbindings() {
		WorkbenchJob workbenchJob = new WorkbenchJob("Reloading KeybindingsManager") //$NON-NLS-1$
		{
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				try {
					Binding[] bindings = bindingService.getBindings();

					List<Binding> bindingsList = new LinkedList<Binding>(Arrays.asList(bindings));

					// Remove any bindings we added
					for (Binding binding : bindings)
					{
						ParameterizedCommand parameterizedCommand = binding.getParameterizedCommand();
						if (parameterizedCommand != null)
						{
							Command command = parameterizedCommand.getCommand();
							if (command.isDefined())
							{
								String id = command.getId();
								if (id.startsWith(SCRIPTING_COMMAND_ID_PREFIX))
								{
									bindingsList.remove(binding);
								}
							}
						}
					}

					for (CommandElementListHandler commandElementListHandler : commandElementListHandlers)
					{
						// Deactivate the handler
						handlerService.deactivateHandler(commandElementListHandler.getActivateHandler());

						// Add back the original bindings that we were wrapping
						Binding originalBinding = commandElementListHandler.getOriginalBinding();
						if (originalBinding != null)
						{
							bindingsList.add(originalBinding);
						}
					}

					// Set the new bindings
					BindingManager bindingManager = ((org.eclipse.ui.internal.keys.BindingService) bindingService)
					.getBindingManager();

					bindingManager.setBindings(bindingsList.toArray(new Binding[0]));

					loadbindings();
				} finally {
				}
				return Status.OK_STATUS;
			}

		};
		workbenchJob.setRule(MUTEX_RULE);
		workbenchJob.setSystem(true);
		workbenchJob.setPriority(Job.LONG);
		workbenchJob.schedule();
	}

	private void loadbindings()
	{
		commandElementListHandlers.clear();

		// Collect unique key sequences
		Set<KeySequence> uniqueKeySequences = new LinkedHashSet<KeySequence>();

		// Get all commands
		CommandElement[] commands = bundleManager.getCommands();
		for (CommandElement commandElement : commands)
		{
			// Skip snippets
			if (commandElement instanceof SnippetElement)
			{
				continue;
			}

			// Get key sequences
			KeySequence[] keySequences = commandElement.getKeySequences();
			if (keySequences != null && keySequences.length > 0)
			{
				// Add to the set
				for (KeySequence keySequence : keySequences)
				{
					uniqueKeySequences.add(keySequence);
				}
			}
		}

		List<Binding> commandElementBindings = new LinkedList<Binding>();
		List<Binding> bindingsToRemove = new LinkedList<Binding>();

		String schemeId = PlatformUI.getPreferenceStore().getString(IWorkbenchPreferenceConstants.KEY_CONFIGURATION_ID);
		for (KeySequence keySequence : uniqueKeySequences)
		{
			ParameterizedCommand originalParameterizedCommand = null;
			Binding originalBinding = null;

			// id is a combination of the constant string and the string form of key sequence

			// Is there a command that already has the same key sequence bounding ?
			Binding perfectMatch = bindingService.getPerfectMatch(keySequence);
			if (perfectMatch != null)
			{
				// TODO Improve the detection of collision considering the scheme hierarchy, context hierarchy and
				// type (USER vs. SYSTEM). For now we check for SYSTEM bindings in the current scheme that are defined
				// for base editor context.
				if (perfectMatch.getType() == Binding.SYSTEM &&
						schemeId.equals(perfectMatch.getSchemeId()) &&
						"org.eclipse.ui.textEditorScope".equals(perfectMatch.getContextId())) //$NON-NLS-1$
				{
					// Remove the bindings
					bindingsToRemove.add(perfectMatch);
					//
					originalParameterizedCommand = perfectMatch.getParameterizedCommand();
					originalBinding = perfectMatch;
				}
			}

			String commandId = SCRIPTING_COMMAND_ID_PREFIX + keySequence;
			CommandElementListHandler commandElementListHandler = new CommandElementListHandler(keySequence,
					originalParameterizedCommand, originalBinding);

			commandElementListHandlers.add(commandElementListHandler);

			Command commandElementListCommand = commandService.getCommand(commandId);
			String nameAndDescription = NLS.bind(Messages.KeybindingsManager_AptanaProxyCommand, new Object[] { keySequence });
			commandElementListCommand
					.define(nameAndDescription,
							nameAndDescription, commandService.getCategory("org.eclipse.ui.category.edit")); //$NON-NLS-1$

			IHandlerActivation activateHandler = handlerService.activateHandler(commandElementListCommand.getId(), commandElementListHandler);
			commandElementListHandler.setActivationHandler(activateHandler);
			ParameterizedCommand parameterizedCommand = new ParameterizedCommand(commandElementListCommand, null);
			Binding commandElementBinding = new KeyBinding(keySequence, parameterizedCommand, schemeId,
					"org.eclipse.ui.textEditorScope", // TODO Handle other scopes //$NON-NLS-1$
					null, null, null, Binding.SYSTEM);
			commandElementBindings.add(commandElementBinding);
		}

		// Original key bindings
		List<Binding> bindingsList = new LinkedList<Binding>(Arrays.asList(bindingService.getBindings()));

		// Remove the original key bindings that we are wrapping
		bindingsList.removeAll(bindingsToRemove);

		// Add key bindings defined in bundles
		bindingsList.addAll(commandElementBindings);

		// Set the new bindings
		BindingManager bindingManager = ((org.eclipse.ui.internal.keys.BindingService) bindingService)
				.getBindingManager();

		bindingManager.setBindings(bindingsList.toArray(new Binding[0]));
	}

	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		// Apparently this preference property name is fired when the user makes changes to
		// the General > Keys preferences page
		if (IWorkbenchRegistryConstants.EXTENSION_COMMANDS.equals(event.getProperty()))
		{
			// Not working yet
			// reloadbindings();
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.scripting.model.LoadCycleListener#scriptLoaded(java.io.File)
	 */
	@Override
	public void scriptLoaded(File script)
	{
		reloadbindings();
	}

	/* (non-Javadoc)
	 * @see com.aptana.scripting.model.LoadCycleListener#scriptReloaded(java.io.File)
	 */
	@Override
	public void scriptReloaded(File script)
	{
		reloadbindings();
	}

	/* (non-Javadoc)
	 * @see com.aptana.scripting.model.LoadCycleListener#scriptUnloaded(java.io.File)
	 */
	@Override
	public void scriptUnloaded(File script)
	{
		reloadbindings();
	}

}
