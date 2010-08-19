package com.aptana.editor.common.internal.formatter;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.scripting.IDocumentScopeManager;
import com.aptana.editor.common.scripting.QualifiedContentType;

/**
 * A common multi-pass content formatter.<br>
 * This content formatter behaves like the {@link MultiPassContentFormatter}. The only change is in the way the slave
 * formatters are located. The slaves are searched by translating the given partition type to the conent-type of the
 * language that generates them.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class CommonMultiPassContentFormatter extends MultiPassContentFormatter
{

	private IDocumentScopeManager documentScopeManager;
	private final String fType;
	private final String fPartitioning;

	/**
	 * Constructs a new {@link CommonMultiPassContentFormatter}.
	 * 
	 * @param partitioning
	 * @param type
	 */
	public CommonMultiPassContentFormatter(String partitioning, String type)
	{
		super(partitioning, type);
		this.fPartitioning = partitioning;
		this.fType = type;
		documentScopeManager = CommonEditorPlugin.getDefault().getDocumentScopeManager();
	}

	/**
	 * Format the slaves.<br>
	 * This version of the formatSlaves tries to identify all the regions that belong to the same content-type and
	 * format them as a single unit.
	 * 
	 * @see org.eclipse.jface.text.formatter.MultiPassContentFormatter#formatSlaves(org.eclipse.jface.text.formatter.
	 *      IFormattingContext, org.eclipse.jface.text.IDocument, int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void formatSlaves(IFormattingContext context, IDocument document, int offset, int length)
	{
		Map partitioners = new HashMap(0);
		try
		{

			final ITypedRegion[] partitions = TextUtilities.computePartitioning(document, fPartitioning, offset,
					length, false);

			if (!fType.equals(partitions[0].getType()))
				partitions[0] = TextUtilities.getPartition(document, fPartitioning, partitions[0].getOffset(), false);

			if (partitions.length > 1)
			{

				if (!fType.equals(partitions[partitions.length - 1].getType()))
					partitions[partitions.length - 1] = TextUtilities.getPartition(document, fPartitioning,
							partitions[partitions.length - 1].getOffset(), false);
			}

			String type = null;
			ITypedRegion partition = null;

			partitioners = TextUtilities.removeDocumentPartitioners(document);

			// Instead of formatting each partition, we collect the start and end offset for a specific content type and
			// when it switches, we call a slave formatter to do the actual formatting.
			int start = -1;
			int contentLength = 0;
			QualifiedContentType lastQualifiedContentType = null;
			for (int index = partitions.length - 1; index >= 0; index--)
			{
				partition = partitions[index];
				type = partition.getType();
				boolean isDefaultType = fType.equals(type);
				QualifiedContentType qualifiedContentType = documentScopeManager.getContentType(document, partition
						.getOffset());
				if (!isDefaultType && qualifiedContentType != null)
				{
					if (lastQualifiedContentType == null || qualifiedContentType.equals(lastQualifiedContentType))
					{
						// We just need to set/expand the offset and length (note that the loop is moving backwards)
						start = partition.getOffset();
						contentLength += partition.getLength();
						lastQualifiedContentType = qualifiedContentType;
						continue;
					}
				}
				// if we got to this point, that means we have to check for a content-type switch and make a call to a
				// slave formatter.
				if (lastQualifiedContentType != null)
				{
					// take the last qualified content type and format it
					formatSlave(context, document, start, contentLength, lastQualifiedContentType.getLastPart());
					start = -1;
					contentLength = 0;
					lastQualifiedContentType = null;
				}
			}
			if (lastQualifiedContentType != null)
			{
				// take the last qualified content type and format it
				if (lastQualifiedContentType.getPartCount() > 0)
					formatSlave(context, document, start, contentLength, lastQualifiedContentType.getParts()[0]);
			}
		}
		catch (BadLocationException exception)
		{
			// Should not happen
		}
		finally
		{
			TextUtilities.addDocumentPartitioners(document, partitioners);
		}
	}
}
