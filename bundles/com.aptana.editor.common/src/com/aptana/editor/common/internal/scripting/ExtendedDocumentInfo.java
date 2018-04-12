/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.internal.scripting;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;

import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.scripting.model.BundleManager;

/**
 * This class stores the association between an IDocument in an editor and the filename of teh file it represents. This
 * is used to do dynamic top-level scope associations, since bundles can define filename patterns to override top level
 * scopes returned for scripting purposes.
 * 
 * @author Max Stepanov
 */
/* package */class ExtendedDocumentInfo
{

	private Map<String, String> contentTypesAssociation = new HashMap<String, String>();
	private String filename;
	private String defaultContentType;

	// Cached copy of the top level scope
	private String fTopLevelScope;
	// Boolean flag to tell if we calculated top level scope already. It may have been null, so we can't just check for
	// null on memoized field.
	private boolean calculatedTopLevelScope = false;

	/**
	 * @param filename
	 */
	protected ExtendedDocumentInfo(String defaultContentType, String filename)
	{
		this.defaultContentType = defaultContentType;
		this.filename = filename;
	}

	public QualifiedContentType getContentType(IDocument document, int offset) throws BadLocationException
	{
		QualifiedContentType result = new QualifiedContentType(defaultContentType);
		try
		{
			// If we're at the end of the document, back up one char to grab partition when we have bogus zero length
			// partition at EOF.
			if (offset == document.getLength())
			{
				ITypedRegion region = document.getPartition(offset);
				if (region.getLength() == 0 && offset > 0)
				{
					offset = offset - 1;
				}
			}
			// get partition at offset
			String contentType = document.getContentType(offset);
			// grab the top level document type that this partition is a subtype of
			String subdocContentType = contentTypesAssociation.get(contentType);
			if (subdocContentType != null && !subdocContentType.equals(result.getLastPart()))
			{
				// if our content type/scope doesn't have this language level scope at the end, add it to the end
				result = result.subtype(subdocContentType);
			}
			// add partition to end
			return result.subtype(contentType);
		}
		catch (Exception e)
		{
			return result;
		}
	}

	/**
	 * Associate the partition with it's implied top level document Content Type/scope
	 * 
	 * @param contentType
	 * @param documentContentType
	 */
	public void associateContentType(String contentType, String documentContentType)
	{
		contentTypesAssociation.put(contentType, documentContentType);
	}

	/**
	 * Takes a fully converted/translated scope and then forcibly replaces the wrapping scope!
	 * 
	 * @param translation
	 * @return
	 */
	public QualifiedContentType modify(QualifiedContentType translation)
	{
		if (filename == null)
		{
			return translation;
		}

		// memoize since it's expensive (we repeatedly ask for scopes with our new coloring!)
		if (!calculatedTopLevelScope)
		{
			fTopLevelScope = BundleManager.getInstance().getTopLevelScope(filename);
			calculatedTopLevelScope = true;
		}

		if (fTopLevelScope == null)
		{
			return translation;
		}

		// TODO If scope ends with generic DEFAULT PARTITION, cut that off

		String[] parts = translation.getParts();
		String[] newParts = new String[parts.length];
		newParts[0] = fTopLevelScope;
		System.arraycopy(parts, 1, newParts, 1, parts.length - 1);
		return new QualifiedContentType(newParts);
	}

}
