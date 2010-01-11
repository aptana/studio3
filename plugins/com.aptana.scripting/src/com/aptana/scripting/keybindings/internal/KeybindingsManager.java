package com.aptana.scripting.keybindings.internal;

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
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.bindings.Binding;
import org.eclipse.jface.bindings.BindingManager;
import org.eclipse.jface.bindings.BindingManagerEvent;
import org.eclipse.jface.bindings.IBindingManagerListener;
import org.eclipse.jface.bindings.Scheme;
import org.eclipse.jface.bindings.keys.KeyBinding;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.progress.WorkbenchJob;

import com.aptana.scripting.model.AbstractElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.ElementChangeListener;
import com.aptana.scripting.model.SnippetElement;

public class KeybindingsManager implements IBindingManagerListener, ElementChangeListener
{
	private static final AtomicBoolean installed = new AtomicBoolean(false);
	private BundleManager bundleManager;
	private IBindingService bindingService;
	private ICommandService commandService;
	private final IHandlerService handlerService;

	public static final void install()
	{
		if (installed.compareAndSet(false, true))
		{
			WorkbenchJob workbenchJob = new WorkbenchJob("Installing KeybindingsManager") //$NON-NLS-1$
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					IWorkbench workbench = PlatformUI.getWorkbench();
					IBindingService bindingService = (IBindingService) workbench.getService(IBindingService.class);
					ICommandService commandService = (ICommandService) workbench.getService(ICommandService.class);
					IHandlerService handlerService = (IHandlerService) workbench.getService(IHandlerService.class);
					KeybindingsManager keybindingsManager = new KeybindingsManager(bindingService, commandService,
							handlerService);
					// Load initial bindings
					keybindingsManager.loadbindings();
					return Status.OK_STATUS;
				}
			};
			workbenchJob.setSystem(true);
			workbenchJob.setPriority(Job.LONG);
			workbenchJob.schedule();
		}
	}

	private KeybindingsManager(IBindingService bindingService, ICommandService commandService,
			IHandlerService handlerService)
	{
		this.bindingService = bindingService;
		this.commandService = commandService;
		this.handlerService = handlerService;

		// Listen to the changes to bindings
		bindingService.addBindingManagerListener(this);

		bundleManager = BundleManager.getInstance();
		bundleManager.addElementChangeListener(this);
	}

	@SuppressWarnings("restriction")
	private void loadbindings()
	{
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
		Scheme activeScheme = bindingService.getActiveScheme();
		for (KeySequence keySequence : uniqueKeySequences)
		{
			ParameterizedCommand originalParameterizedCommand = null;
			// Is there a command that already has the same key sequence bounding ?
			Binding perfectMatch = bindingService.getPerfectMatch(keySequence);
			if (perfectMatch != null)
			{
				// TODO Improve the detection of collision considering the scheme hierarchy, context hierarchy and
				// type (USER vs. SYSTEM). For now we check for SYSTEM bindings in the current scheme that are defined
				// for base editor context.
				if (perfectMatch.getType() == Binding.SYSTEM &&
						activeScheme.getId().equals(perfectMatch.getSchemeId()) &&
						"org.eclipse.ui.textEditorScope".equals(perfectMatch.getContextId())) //$NON-NLS-1$
				{
					// Remove the bindings
					bindingsToRemove.add(perfectMatch);
					//
					originalParameterizedCommand = perfectMatch.getParameterizedCommand();
				}
			}

			CommandElementListHandler commandElementListHandler = new CommandElementListHandler(keySequence,
					originalParameterizedCommand);

			// id is a combination of the constant string and the string form of key sequence
			Command commandElementListCommand = commandService.getCommand("com.aptana.scripting.keybindings." //$NON-NLS-1$
					+ keySequence);
			String nameAndDescription = NLS.bind(Messages.KeybindingsManager_AptanaProxyCommand, new Object[] { keySequence });
			commandElementListCommand
					.define(nameAndDescription,
							nameAndDescription, commandService.getCategory("org.eclipse.ui.category.edit")); //$NON-NLS-1$

			handlerService.activateHandler(commandElementListCommand.getId(), commandElementListHandler);
			ParameterizedCommand parameterizedCommand = new ParameterizedCommand(commandElementListCommand, null);
			Binding commandElementBinding = new KeyBinding(keySequence, parameterizedCommand, activeScheme.getId(),
					"org.eclipse.ui.textEditorScope", // TODO Handle other scopes //$NON-NLS-1$
					null, null, null, Binding.SYSTEM);
			commandElementBindings.add(commandElementBinding);
		}

		List<Binding> bindingsList = new LinkedList<Binding>();

		// Original key bindings
		Binding[] bindings = bindingService.getBindings();
		bindingsList.addAll(Arrays.asList(bindings));

		// Remove proxied key bindings
		bindingsList.removeAll(bindingsToRemove);

		// Add key bindings defined in bundles
		bindingsList.addAll(commandElementBindings);

		// Set the new bindings
		BindingManager bindingManager = ((org.eclipse.ui.internal.keys.BindingService) bindingService)
				.getBindingManager();

		bindingManager.setBindings(bindingsList.toArray(new Binding[0]));
	}

	public void bindingManagerChanged(BindingManagerEvent event)
	{
		// TODO Deal with changes to binding
	}

	public void elementAdded(AbstractElement element)
	{
		// TODO Deal with changes to key bindings if the element was CommandElement
	}

	public void elementDeleted(AbstractElement element)
	{
		// TODO Deal with changes to key bindings if the element was CommandElement
	}
}
