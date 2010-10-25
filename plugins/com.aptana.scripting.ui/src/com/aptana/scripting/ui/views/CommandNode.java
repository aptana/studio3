package com.aptana.scripting.ui.views;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.InputType;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class CommandNode extends BaseNode
{
	private enum Property
	{
		NAME, PATH, SCOPE, EXECUTABLE, TYPE, INPUTS, OUTPUT, TRIGGERS
	}

	private static final Image COMMAND_ICON = ScriptingUIPlugin.getImage("icons/command.png"); //$NON-NLS-1$
	private CommandElement _command;

	/**
	 * CommandNode
	 * 
	 * @param command
	 */
	public CommandNode(CommandElement command)
	{
		this._command = command;
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
	 * @see com.aptana.scripting.ui.views.BaseNode#getLabel()
	 */
	public String getLabel()
	{
		return this._command.getDisplayName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor nameProperty = new PropertyDescriptor(Property.NAME, "Name");
		PropertyDescriptor pathProperty = new PropertyDescriptor(Property.PATH, "Path");
		PropertyDescriptor scopeProperty = new PropertyDescriptor(Property.SCOPE, "Scope");
		PropertyDescriptor executableProperty = new PropertyDescriptor(Property.EXECUTABLE, "Executable");
		PropertyDescriptor executionTypeProperty = new PropertyDescriptor(Property.TYPE, "Execution Type");
		PropertyDescriptor inputsProperty = new PropertyDescriptor(Property.INPUTS, "Inputs");
		PropertyDescriptor outputProperty = new PropertyDescriptor(Property.OUTPUT, "Output");
		PropertyDescriptor triggersProperty = new PropertyDescriptor(Property.TRIGGERS, "Triggers");

		return new IPropertyDescriptor[] { nameProperty, pathProperty, scopeProperty, executableProperty,
				executionTypeProperty, inputsProperty, outputProperty, triggersProperty };
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id)
	{
		Object result = null;

		if (id instanceof Property)
		{
			StringBuilder buffer;

			switch ((Property) id)
			{
				case NAME:
					result = this._command.getDisplayName();
					break;

				case PATH:
					result = this._command.getPath();
					break;

				case SCOPE:
					String scope = this._command.getScope();
					
					result = (scope != null && scope.length() > 0) ? scope : "all";
					break;

				case EXECUTABLE:
					result = this._command.isExecutable();
					break;

				case TYPE:
					result = this._command.isBlockCommand() ? "Ruby block" : this._command.isShellCommand() ? "Shell script"
							: "unknown";
					break;

				case INPUTS:
					InputType[] inputs = this._command.getInputTypes();

					buffer = new StringBuilder();

					for (int i = 0; i < inputs.length; i++)
					{
						if (i > 0)
						{
							buffer.append(", ");
						}

						buffer.append(inputs[i].getName());
					}

					result = buffer.toString();
					break;

				case OUTPUT:
					result = this._command.getOutputType();
					break;

				case TRIGGERS:
					String[] triggers = this._command.getTriggers();

					if (triggers != null && triggers.length > 0)
					{
						buffer = new StringBuilder();

						for (int i = 0; i < triggers.length; i++)
						{
							if (i > 0)
							{
								buffer.append(", ");
							}

							buffer.append(triggers[i]);
						}

						result = buffer.toString();
					}
					else
					{
						result = "none";
					}
					break;

				default:
					break;
			}
		}

		return result;
	}
}
