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

public class SnippetElement extends CommandElement
{
	private String _expansion;
	
	/**
	 * Snippet
	 * 
	 * @param name
	 */
	public SnippetElement(String path)
	{
		super(path);
		
		this.setInputType(InputType.NONE);
		this.setOutputType(OutputType.INSERT_AS_SNIPPET);
	}

	/**
	 * execute
	 */
	public CommandResult execute()
	{
		return execute(null);
	}

	/**
	 * execute
	 */
	public CommandResult execute(CommandContext context)
	{
		// set output type
		context.setOutputType(getOutputType());

		CommandResult result = new CommandResult(this, context);

		// set result
		result.setOutputString(this.getExpansion());

		// indicate successful execution so that command result processing will work
		result.setExecutedSuccessfully(true);

		// grab input type so we can report back which input was used
		String inputTypeString = (String) context.get(CommandContext.INPUT_TYPE);
		InputType inputType = InputType.get(inputTypeString);
		
		result.setInputType(inputType);
		
		return result;
	}

	/**
	 * getElementName
	 */
	protected String getElementName()
	{
		return "snippet"; //$NON-NLS-1$
	}
	
	/**
	 * getExpansion
	 * 
	 * @return
	 */
	public String getExpansion()
	{
		return this._expansion;
	}

	/**
	 * setExpansion
	 * 
	 * @param expansion
	 */
	public void setExpansion(String expansion)
	{
		this._expansion = expansion;
	}

	/**
	 * Always executable.
	 */
	@Override
	public boolean isExecutable()
	{
		return true;
	}

}
