/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.listeners;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import com.aptana.scripting.model.AbstractElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.ElementVisibilityListener;
import com.aptana.scripting.model.TriggerType;

/**
 * CommandListenerRegistrant
 */
public class ExecutionListenerRegistrant implements ElementVisibilityListener, IExecutionListener
{
	private static ExecutionListenerRegistrant INSTANCE;

	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static synchronized ExecutionListenerRegistrant getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new ExecutionListenerRegistrant();
			INSTANCE.setup();
		}

		return INSTANCE;
	}

	/**
	 * shutdown
	 */
	public static synchronized void shutdown()
	{
		if (INSTANCE != null)
		{
			INSTANCE.tearDown();
			INSTANCE = null;
		}
	}

	private Map<CommandElement, Set<String>> _commandToIdsMap = new HashMap<CommandElement, Set<String>>();
	private Map<String, Set<CommandElement>> _idToCommandsMap = new HashMap<String, Set<CommandElement>>();
	private Set<CommandElement> _nonlimitedCommands = new HashSet<CommandElement>();

	/**
	 * CommandListenerRegistrant
	 */
	public ExecutionListenerRegistrant()
	{
	}

	/**
	 * addCommand
	 * 
	 * @param command
	 */
	protected void addCommand(CommandElement command)
	{
		if (command.hasProperty(TriggerType.EXECUTION_LISTENER.getPropertyName()))
		{
			String[] ids = command.getTriggerTypeValues(TriggerType.EXECUTION_LISTENER);

			if (ids.length > 0)
			{
				Set<String> idSet = new HashSet<String>(Arrays.asList(ids));

				this._commandToIdsMap.put(command, idSet);

				for (String id : idSet)
				{
					Set<CommandElement> commandsUsingId = this._idToCommandsMap.get(id);

					if (commandsUsingId == null)
					{
						commandsUsingId = new HashSet<CommandElement>();

						this._idToCommandsMap.put(id, commandsUsingId);
					}

					commandsUsingId.add(command);
				}
			}
			else
			{
				this._nonlimitedCommands.add(command);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.scripting.model.ElementVisibilityListener#elementBecameHidden(com.aptana.scripting.model.AbstractElement
	 * )
	 */
	public void elementBecameHidden(AbstractElement element)
	{
		if (element instanceof CommandElement)
		{
			this.removeCommand((CommandElement) element);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.scripting.model.ElementVisibilityListener#elementBecameVisible(com.aptana.scripting.model.AbstractElement
	 * )
	 */
	public void elementBecameVisible(AbstractElement element)
	{
		if (element instanceof CommandElement)
		{
			this.addCommand((CommandElement) element);
		}
	}

	/**
	 * execute
	 * 
	 * @param id
	 * @param properties
	 */
	private void execute(String id, String... properties)
	{
		if (this._idToCommandsMap.containsKey(id) || this._nonlimitedCommands.isEmpty() == false)
		{
			// create property map
			Map<String, String> propertyMap = new HashMap<String, String>();

			// add type
			propertyMap.put("id", id);

			// add optional key/values
			int length = properties.length & ~0x01;

			for (int i = 0; i < length; i += 2)
			{
				String name = properties[i];
				String value = properties[i + 1];

				propertyMap.put(name, value);
			}

			Set<CommandElement> allCommands = new HashSet<CommandElement>();
			Set<CommandElement> commandsForId = this._idToCommandsMap.get(id);

			if (commandsForId != null)
			{
				allCommands.addAll(commandsForId);
			}

			allCommands.addAll(this._nonlimitedCommands);

			for (CommandElement command : allCommands)
			{
				CommandContext context = command.createCommandContext();

				context.put(TriggerType.EXECUTION_LISTENER.getName(), propertyMap);
				command.execute(context);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.IExecutionListener#notHandled(java.lang.String,
	 * org.eclipse.core.commands.NotHandledException)
	 */
	public void notHandled(String commandId, NotHandledException exception)
	{
		// ignore
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.IExecutionListener#postExecuteFailure(java.lang.String,
	 * org.eclipse.core.commands.ExecutionException)
	 */
	public void postExecuteFailure(String commandId, ExecutionException exception)
	{
		// ignore
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.IExecutionListener#postExecuteSuccess(java.lang.String, java.lang.Object)
	 */
	public void postExecuteSuccess(String commandId, Object returnValue)
	{
		this.execute( //
			commandId, //
			"type", "postExecuteSuccess" //
		);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.IExecutionListener#preExecute(java.lang.String,
	 * org.eclipse.core.commands.ExecutionEvent)
	 */
	public void preExecute(String commandId, ExecutionEvent event)
	{
		// ignore
	}

	/**
	 * removeCommand
	 * 
	 * @param command
	 */
	protected void removeCommand(CommandElement command)
	{
		Set<String> ids = this._commandToIdsMap.get(command);

		if (ids != null)
		{
			for (String id : ids)
			{
				Set<CommandElement> commandsUsingId = this._idToCommandsMap.get(id);

				if (commandsUsingId != null)
				{
					if (commandsUsingId.remove(command) && commandsUsingId.isEmpty())
					{
						this._idToCommandsMap.remove(id);
					}
				}
			}
		}
		else
		{
			this._nonlimitedCommands.remove(command);
		}
	}

	/**
	 * setup
	 */
	private void setup()
	{
		// listen for execution events
		Object service = PlatformUI.getWorkbench().getService(ICommandService.class);

		if (service instanceof ICommandService)
		{
			ICommandService commandService = (ICommandService) service;

			commandService.addExecutionListener(this);
		}

		// listen for element visibility events
		BundleManager manager = BundleManager.getInstance();

		manager.addElementVisibilityListener(this);
	}

	/**
	 * tearDown
	 */
	private void tearDown()
	{
		// stop listening for visibility events
		BundleManager manager = BundleManager.getInstance();

		manager.removeElementVisibilityListener(this);

		// stop listening for execution events
		Object service = PlatformUI.getWorkbench().getService(ICommandService.class);

		if (service instanceof ICommandService)
		{
			ICommandService commandService = (ICommandService) service;

			commandService.removeExecutionListener(this);
		}

		// drop all references
		this._commandToIdsMap.clear();
		this._idToCommandsMap.clear();
		this._nonlimitedCommands.clear();
	}
}
