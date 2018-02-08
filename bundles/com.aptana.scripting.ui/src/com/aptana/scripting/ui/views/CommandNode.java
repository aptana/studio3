/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui.views;

import java.util.EnumSet;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.InputType;
import com.aptana.scripting.model.TriggerType;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class CommandNode extends BaseNode<CommandNode.Property>
{
	enum Property implements IPropertyInformation<CommandNode>
	{
		NAME(Messages.CommandNode_Command_Name)
		{
			public Object getPropertyValue(CommandNode node)
			{
				return node.command.getDisplayName();
			}
		},
		PATH(Messages.CommandNode_Command_Path)
		{
			public Object getPropertyValue(CommandNode node)
			{
				return node.command.getPath();
			}
		},
		SCOPE(Messages.CommandNode_Command_Scope)
		{
			public Object getPropertyValue(CommandNode node)
			{
				String scope = node.command.getScope();

				return (scope != null && scope.length() > 0) ? scope : Messages.CommandNode_All_Scopes;
			}
		},
		EXECUTABLE(Messages.CommandNode_Command_Executable)
		{
			public Object getPropertyValue(CommandNode node)
			{
				return node.command.isExecutable();
			}
		},
		TYPE(Messages.CommandNode_Command_Type)
		{
			public Object getPropertyValue(CommandNode node)
			{
				String result;

				if (node.command.isBlockCommand())
				{
					result = Messages.CommandNode_Command_Ruby_Block;
				}
				else if (node.command.isShellCommand())
				{
					result = Messages.CommandNode_Command_Shell_Script;
				}
				else
				{
					result = Messages.CommandNode_Unknown_Execution_Type;
				}
				return result;
			}
		},
		INPUTS(Messages.CommandNode_Command_Inputs)
		{
			public Object getPropertyValue(CommandNode node)
			{
				InputType[] inputs = node.command.getInputTypes();
				StringBuilder buffer = new StringBuilder();

				for (int i = 0; i < inputs.length; i++)
				{
					if (i > 0)
					{
						buffer.append(", "); //$NON-NLS-1$
					}

					buffer.append(inputs[i].getName());
				}

				return buffer.toString();
			}
		},
		OUTPUT(Messages.CommandNode_Command_Output)
		{
			public Object getPropertyValue(CommandNode node)
			{
				return node.command.getOutputType();
			}
		},
		TRIGGERS(Messages.CommandNode_Command_Triggers)
		{
			public Object getPropertyValue(CommandNode node)
			{
				String[] triggers = node.command.getTriggerTypeValues(TriggerType.PREFIX);

				if (triggers != null && triggers.length > 0)
				{
					StringBuilder buffer = new StringBuilder();

					for (int i = 0; i < triggers.length; i++)
					{
						if (i > 0)
						{
							buffer.append(", "); //$NON-NLS-1$
						}

						buffer.append(triggers[i]);
					}

					return buffer.toString();
				}

				return null;
			}
		};

		private String header;

		private Property(String header) // $codepro.audit.disable unusedMethod
		{
			this.header = header;
		}

		public String getHeader()
		{
			return header;
		}
	}

	private static final Image COMMAND_ICON = ScriptingUIPlugin.getImage("icons/command.png"); //$NON-NLS-1$
	private CommandElement command;

	/**
	 * CommandNode
	 * 
	 * @param command
	 */
	CommandNode(CommandElement command)
	{
		this.command = command;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return COMMAND_ICON;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getPropertyInfoSet()
	 */
	@Override
	protected Set<Property> getPropertyInfoSet()
	{
		return EnumSet.allOf(Property.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getLabel()
	 */
	public String getLabel()
	{
		return command.getDisplayName();
	}
}
