package com.aptana.editor.common.internal.formatter;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.scripting.IDocumentScopeManager;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.formatter.ui.IScriptFormatterFactory;
import com.aptana.formatter.ui.ScriptFormatterManager;
import com.aptana.formatter.ui.ScriptFormattingContextProperties;
import com.aptana.formatter.ui.ScriptFormattingStrategy;

/**
 * A common multi-pass content formatter.<br>
 * This content formatter behaves like the {@link MultiPassContentFormatter}. The only change is in the way the slave
 * formatters are located. The slaves are searched by translating the given partition type to the conent-type of the
 * language that generates them.<br>
 * Note that when using this class, call the {@link #setMasterStrategy(String)} and the
 * {@link #setSlaveStrategy(String)} of the {@link CommonMultiPassContentFormatter}, instead of its superclass
 * {@link MultiPassContentFormatter}.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class CommonMultiPassContentFormatter extends MultiPassContentFormatter
{

	private IDocumentScopeManager documentScopeManager;
	private final String fType;
	private final String fPartitioning;
	private String masterContentType;
	private Set<String> slaveContentTypes;

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
		slaveContentTypes = new HashSet<String>();
		masterContentType = StringUtil.EMPTY;
	}

	/**
	 * Set the master strategy.
	 * 
	 * @param contentType
	 */
	public void setMasterStrategy(String contentType)
	{
		this.masterContentType = contentType;
		super.setMasterStrategy(new ScriptFormattingStrategy(contentType));
	}

	/**
	 * Set a slave strategy.
	 * 
	 * @param contentType
	 */
	public void setSlaveStrategy(String contentType)
	{
		this.slaveContentTypes.add(contentType);
		super.setSlaveStrategy(new ScriptFormattingStrategy(contentType), contentType);
	}

	/**
	 * Format the slaves.<br>
	 * This version of the formatSlaves will look into the content-types and will call a slave formatter for that
	 * content-type for the entire document (not only the partition area). This allows better formatting for nested
	 * languages like PHP and ERB (Ruby).
	 * 
	 * @see org.eclipse.jface.text.formatter.MultiPassContentFormatter#formatSlaves(org.eclipse.jface.text.formatter.
	 *      IFormattingContext, org.eclipse.jface.text.IDocument, int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void formatSlaves(IFormattingContext context, IDocument document, int offset, int length)
	{
		// Add a property to the context to notify the formatter it is formatting as a slave.
		// By doing so formatters for languages like ERB will do an extra step of collecting the code bits from the
		// content before formatting it.
		context.setProperty(ScriptFormattingContextProperties.CONTEXT_FORMATTER_IS_SLAVE, Boolean.TRUE);

		// For now, the partitioners addition and removal is commented out. This one causes a major slow down when
		// dealing with large files.
		// Map partitioners = new HashMap(0);
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

			// Instead of formatting each partition, we collect the start and end offset for a specific content type and
			// when it switches, we call a slave formatter to do the actual formatting.
			int start = -1;
			int contentLength = 0;
			String lastContentType = null;
			// Note: This loop is traversing backwards
			for (int index = partitions.length - 1; index >= 0; index--)
			{
				partition = partitions[index];
				type = partition.getType();
				boolean isDefaultType = fType.equals(type);
				QualifiedContentType qualifiedContentType = documentScopeManager.getContentType(document, partition
						.getOffset());
				String contentType = extractContentType(qualifiedContentType);
				if (!isDefaultType && contentType != null && !contentType.equals(masterContentType))
				{
					if (lastContentType == null || lastContentType.equals(contentType))
					{
						// We just need to set/expand the offset and length (note that the loop is moving backwards)
						start = partition.getOffset();
						contentLength += partition.getLength();
						lastContentType = contentType;
						continue;
					}
				}
				// if we got to this point, that means we have to check for a content-type switch and make a call to a
				// slave formatter.
				if (lastContentType != null)
				{
					// take the last qualified content type and format it
					// partitioners = TextUtilities.removeDocumentPartitioners(document);
					// System.out.println(lastContentType + "(" + document.get(start, contentLength) + ')');
					updateContex(context, lastContentType);
					formatSlave(context, document, start, contentLength, lastContentType);
					start = -1;
					contentLength = 0;
					lastContentType = null;
					// TextUtilities.addDocumentPartitioners(document, partitioners);
					// partitioners = null;
				}
			}
			if (lastContentType != null)
			{
				// take the last qualified content type and format it
				// partitioners = TextUtilities.removeDocumentPartitioners(document);
				updateContex(context, lastContentType);
				formatSlave(context, document, start, contentLength, lastContentType);
			}
		}
		catch (BadLocationException exception)
		{
			// Should never happen
			CommonEditorPlugin.logError(exception);
		}
		// finally
		// {
		// if (partitioners != null)
		// {
		// TextUtilities.addDocumentPartitioners(document, partitioners);
		// }
		// }
	}

	/**
	 * Update the fomatting context to reflect to the script formatter that should be used with the given content-type.
	 * 
	 * @param context
	 * @param lastContentType
	 */
	private void updateContex(IFormattingContext context, String contentType)
	{
		IScriptFormatterFactory factory = ScriptFormatterManager.getSelected(contentType);
		if (factory != null && context != null)
		{
			context.setProperty(ScriptFormattingContextProperties.CONTEXT_FORMATTER_ID, factory.getId());
		}
	}

	protected String extractContentType(QualifiedContentType qualifiedContentType)
	{
		if (qualifiedContentType == null)
		{
			return null;
		}
		int partCount = qualifiedContentType.getPartCount();
		if (partCount > 2)
		{
			return qualifiedContentType.getParts()[partCount - 2];
		}
		return null;
	}
}
