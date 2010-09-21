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
package com.aptana.scripting.model;

import java.io.OutputStream;

public class CommandResult
{
	private String _outputString;
	private OutputStream _outputStream;
	private String _errorString;
	private OutputStream _errorStream;
	private InputType _inputType;
	private OutputType _outputType;
	private int _returnValue;
	private boolean _executedSuccessfully;
	private CommandContext _context;
	private CommandElement _command;

	/**
	 * CommandResult
	 */
	public CommandResult(CommandElement command, CommandContext context)
	{
		this._command = command;
		this._context = context;
		this._outputType = (context != null) ? context.getOutputType() : OutputType.UNDEFINED;
	}

	/**
	 * executedSuccessfully
	 * 
	 * @return
	 */
	public boolean executedSuccessfully()
	{
		return this._executedSuccessfully;
	}

	/**
	 * getCommand
	 * 
	 * @return
	 */
	public CommandElement getCommand()
	{
		return this._command;
	}

	/**
	 * getContext
	 * 
	 * @return
	 */
	public CommandContext getContext()
	{
		return this._context;
	}

	/**
	 * getOutputStream
	 * 
	 * @return
	 */
	public OutputStream getErrorStream()
	{
		return this._errorStream;
	}

	/**
	 * getErrorString
	 * 
	 * @return
	 */
	public String getErrorString()
	{
		return this._errorString;
	}
	
	/**
	 * getInputType
	 * 
	 * @return
	 */
	public InputType getInputType()
	{
		return this._inputType;
	}

	/**
	 * getOutputStream
	 * 
	 * @return
	 */
	public OutputStream getOutputStream()
	{
		return this._outputStream;
	}

	/**
	 * getOutputString
	 * 
	 * @return
	 */
	public String getOutputString()
	{
		String result = this._outputString;
		
		if (result == null && this._outputStream != null)
		{
			result = this._outputStream.toString();
		}
		
		return result;
	}

	/**
	 * getOutputType
	 * 
	 * @return
	 */
	public OutputType getOutputType()
	{
		return (this._outputType != null) ? this._outputType : OutputType.UNDEFINED;
	}
	
	/**
	 * getReturnValue
	 * 
	 * @return
	 */
	public int getReturnValue()
	{
		return this._returnValue;
	}

	/**
	 * setExecutedSuccessfully
	 * 
	 * @param value
	 */
	void setExecutedSuccessfully(boolean value)
	{
		this._executedSuccessfully = value;
	}

	/**
	 * setInputType
	 * 
	 * @param inputType
	 */
	void setInputType(InputType inputType)
	{
		this._inputType = inputType;
	}

	/**
	 * setOutputStream
	 * 
	 * @param output
	 */
	void setOutputStream(OutputStream output)
	{
		this._outputStream = output;
	}
	
	/**
	 * setOutputString
	 * 
	 * @param output
	 */
	void setOutputString(String output)
	{
		this._outputString = output;
	}

	/**
	 * Set the error string
	 * 
	 * @param err
	 */
	void setErrorString(String err)
	{
		this._errorString = err;
	}
	
	/**
	 * setReturnValue
	 * 
	 * @param value
	 */
	void setReturnValue(int value)
	{
		this._returnValue = value;
	}
}
