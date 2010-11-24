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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

public class ValidationManager implements IValidationManager
{

	private IDocument fDocument;
	private List<IValidationItem> fItems;

	public ValidationManager()
	{
		fItems = new ArrayList<IValidationItem>();
	}

	public void clear()
	{
		fDocument = null;
		fItems.clear();
	}

	public void setDocument(IDocument document)
	{
		fDocument = document;
	}

	public void validate(String source, String language, URI path)
	{
		fItems.clear();

		ValidatorReference[] validatorRefs = ValidatorLoader.getInstance().getValidators(language);
		// using the first one for now
		// TODO: change to match the user selection in preferences
		if (validatorRefs.length > 0)
		{
			IValidator validator = validatorRefs[0].getValidator();
			validator.parseForErrors(source, path, this);
		}
	}

	public void addError(String message, int lineNumber, int lineOffset, int length, String sourcePath)
	{
		addItem(IMarker.SEVERITY_ERROR, message, lineNumber, lineOffset, length, sourcePath);
	}

	public void addWarning(String message, int lineNumber, int lineOffset, int length, String sourcePath)
	{
		addItem(IMarker.SEVERITY_WARNING, message, lineNumber, lineOffset, length, sourcePath);
	}

	public IValidationItem[] getItems()
	{
		return fItems.toArray(new IValidationItem[fItems.size()]);
	}

	private void addItem(int severity, String message, int lineNumber, int lineOffset, int length, String sourcePath)
	{
		int charLineOffset = 0;
		if (fDocument != null)
		{
			try
			{
				charLineOffset = fDocument.getLineOffset(lineNumber - 1);
			}
			catch (BadLocationException e)
			{
			}
		}
		int offset = charLineOffset + lineOffset;
		fItems.add(new ValidationItem(severity, message, offset, length, lineNumber, sourcePath));
	}
}
