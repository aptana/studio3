/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal.formatter;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.text.edits.TextEdit;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.ExtendedFastPartitioner;
import com.aptana.editor.common.IExtendedPartitioner;
import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.common.NullPartitionerSwitchStrategy;
import com.aptana.editor.common.text.rules.CompositePartitionScanner;
import com.aptana.editor.common.text.rules.NullSubPartitionScanner;
import com.aptana.formatter.IScriptFormatterFactory;
import com.aptana.formatter.ScriptFormatterManager;
import com.aptana.formatter.preferences.PreferencesLookupDelegate;

/**
 * A common formatter utilities class.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class CommonFormatterUtils
{

	/**
	 * Format a given input string that is in the given file path (or should be injected to).<br>
	 * This method will try to determine the content type and the project's formatter settings by the file path and run
	 * the right formatter in order to generate a formatted output.
	 * 
	 * @param filePath
	 * @param input
	 * @return The formatted output, or the given input in case the input could not be formatted.
	 */
	public static String format(IPath filePath, String input)
	{
		if (filePath == null || input == null || input.trim().length() == 0)
		{
			return input;
		}
		IContentType contentType = Platform.getContentTypeManager().findContentTypeFor(filePath.lastSegment());
		if (contentType != null)
		{
			// Format the string before returning it
			IScriptFormatterFactory factory = ScriptFormatterManager.getSelected(contentType.getId());
			if (factory != null)
			{
				IDocument document = new Document(input);
				IPartitioningConfiguration partitioningConfiguration = (IPartitioningConfiguration) factory
						.getPartitioningConfiguration();
				CompositePartitionScanner partitionScanner = new CompositePartitionScanner(
						partitioningConfiguration.createSubPartitionScanner(), new NullSubPartitionScanner(),
						new NullPartitionerSwitchStrategy());
				IDocumentPartitioner partitioner = new ExtendedFastPartitioner(partitionScanner,
						partitioningConfiguration.getContentTypes());
				partitionScanner.setPartitioner((IExtendedPartitioner) partitioner);
				partitioner.connect(document);
				document.setDocumentPartitioner(partitioner);

				final String lineDelimiter = TextUtilities.getDefaultLineDelimiter(document);
				IResource parentResource = ResourcesPlugin.getWorkspace().getRoot()
						.findMember(filePath.removeLastSegments(1));
				if (parentResource != null)
				{
					IProject project = parentResource.getProject();
					Map<String, String> preferences = factory
							.retrievePreferences(new PreferencesLookupDelegate(project));
					TextEdit formattedTextEdit = factory.createFormatter(lineDelimiter, preferences).format(input, 0,
							input.length(), 0, false, null, StringUtil.EMPTY);
					try
					{
						formattedTextEdit.apply(document);
						input = document.get();
					}
					catch (Exception e)
					{
						IdeLog.logWarning(CommonEditorPlugin.getDefault(),
								"Error while formatting the file template code", e); //$NON-NLS-1$
					}
				}
			}
		}
		return input;
	}
}
