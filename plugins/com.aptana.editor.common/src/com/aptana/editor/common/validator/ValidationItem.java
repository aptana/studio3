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
package com.aptana.editor.common.validator;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;

public class ValidationItem implements IValidationItem
{

	private final int severity;
	private final String message;
	private final int offset;
	private final int length;
	private final int lineNumber;
	private final String sourcePath;

	public ValidationItem(int severity, String message, int offset, int length, int lineNumber, String sourcePath)
	{
		this.severity = severity;
		this.message = message;
		this.offset = offset;
		this.length = length;
		this.lineNumber = lineNumber;
		this.sourcePath = sourcePath;
	}

	public int getOffset()
	{
		return offset;
	}

	public int getLength()
	{
		return length;
	}

	public int getLineNumber()
	{
		return lineNumber;
	}

	public String getMessage()
	{
		return message;
	}

	public int getSeverity()
	{
		return severity;
	}

	public String getSourcePath()
	{
		return sourcePath;
	}

	public Map<String, Object> createMarkerAttributes()
	{
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put(IMarker.SEVERITY, getSeverity());
		attributes.put(IMarker.CHAR_START, getOffset());
		attributes.put(IMarker.CHAR_END, getOffset() + getLength());
		attributes.put(IMarker.MESSAGE, getMessage());
		attributes.put(IMarker.LINE_NUMBER, getLineNumber());

		return attributes;
	}
}
