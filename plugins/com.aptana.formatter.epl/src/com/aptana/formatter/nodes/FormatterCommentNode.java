package com.aptana.formatter.nodes;

import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;
import com.aptana.formatter.nodes.FormatterTextNode;

/**
 * Base class for formatter comment nodes.
 */
public abstract class FormatterCommentNode extends FormatterTextNode
{

	public FormatterCommentNode(IFormatterDocument document, int startOffset, int endOffset)
	{
		super(document, startOffset, endOffset);
	}

	public void accept(IFormatterContext context, IFormatterWriter visitor) throws Exception
	{
		if (getDocument().getBoolean(getWrappingKey()))
		{
			final boolean savedWrapping = context.isWrapping();
			context.setWrapping(true);
			visitor.write(context, getStartOffset(), getEndOffset());
			context.setWrapping(savedWrapping);
		}
		else
		{
			visitor.write(context, getStartOffset(), getEndOffset());
		}
	}

	/**
	 * Returns the key for the boolean 'Wrapping' value that is stored in the document.
	 * 
	 * @return A key that will be used to retrieve the boolean value for the wrapping
	 */
	public abstract String getWrappingKey();
}
