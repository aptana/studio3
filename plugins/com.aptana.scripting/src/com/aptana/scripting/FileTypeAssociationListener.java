/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;

import com.aptana.scripting.model.BundleVisibilityListener;
import com.aptana.scripting.model.BundleEntry;

/**
 * This listener dynamically does the activation and removal of filetype associations between our editor and filename
 * patterns supplied by bundles.
 * 
 * @author cwilliams
 */
class FileTypeAssociationListener implements BundleVisibilityListener
{
	private static final String GENERIC_CONTENT_TYPE_ID = "com.aptana.editor.text.content-type.generic"; //$NON-NLS-1$

	public void bundlesBecameVisible(BundleEntry entry)
	{
		// Activate the file type associations
		for (String fileType : getFileTypes(entry))
		{
			associateFiletype(fileType);
		}
	}

	protected void associateFiletype(String fileType)
	{
		IContentType type = Platform.getContentTypeManager().findContentTypeFor(fileType.replaceAll("\\*", "star")); //$NON-NLS-1$ //$NON-NLS-2$
		// TODO Make this much more intelligent! If we're associating a scope that is more specific than an
		// existing scope that is associated with a non-generic content type, we should associate with that
		// parent content type!
		// i.e. 'source.ruby.rspec' => '*.spec' should get associated to same content type that
		// 'source.ruby' did (the ruby content type).
		if (type != null)
			return;
		type = Platform.getContentTypeManager().getContentType(GENERIC_CONTENT_TYPE_ID);
		if (type == null)
		{
			ScriptingActivator.logError("Unable to get reference to generic content type for dynamic filetype associations!", null); //$NON-NLS-1$
			return;
		}
		try
		{
			int assocType = IContentType.FILE_NAME_SPEC;

			if (fileType.contains("*") && fileType.indexOf('.') != -1) //$NON-NLS-1$
			{
				assocType = IContentType.FILE_EXTENSION_SPEC;
				fileType = fileType.substring(fileType.indexOf('.') + 1);
			}

			type.addFileSpec(fileType, assocType);
		}
		catch (CoreException e)
		{
			ScriptingActivator.logError(e.getMessage(), e);
		}
	}

	public void bundlesBecameHidden(BundleEntry entry)
	{
		// remove the file type associations
		for (String fileType : getFileTypes(entry))
		{
			disassociateFiletype(fileType);
		}
	}

	protected void disassociateFiletype(String fileType)
	{
		IContentType type = Platform.getContentTypeManager().getContentType(GENERIC_CONTENT_TYPE_ID);
		try
		{
			int assocType = IContentType.FILE_NAME_SPEC;
			if (fileType.contains("*") && fileType.indexOf('.') != -1) //$NON-NLS-1$
			{
				assocType = IContentType.FILE_EXTENSION_SPEC;
				fileType = fileType.substring(fileType.indexOf('.') + 1);
			}
			type.removeFileSpec(fileType, assocType);
		}
		catch (CoreException e)
		{
			ScriptingActivator.logError(e.getMessage(), e);
		}
	}

	protected List<String> getFileTypes(BundleEntry entry)
	{
		return entry.getFileTypes();
	}

	public void cleanup()
	{
		// Clean up the generic content type in bundle
		IContentType type = Platform.getContentTypeManager().getContentType(GENERIC_CONTENT_TYPE_ID);
		int[] specTypes = new int[] { IContentType.FILE_EXTENSION_SPEC, IContentType.FILE_NAME_SPEC };
		for (int specType : specTypes)
		{
			String[] specs = type.getFileSpecs(specType);
			for (String spec : specs)
			{
				try
				{
					type.removeFileSpec(spec, specType);
				}
				catch (CoreException e)
				{
					ScriptingActivator.logError(e.getMessage(), e);
				}
			}
		}
	}
}
