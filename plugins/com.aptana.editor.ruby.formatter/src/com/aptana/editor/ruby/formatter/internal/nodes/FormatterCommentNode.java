package com.aptana.editor.ruby.formatter.internal.nodes;

import com.aptana.editor.ruby.formatter.RubyFormatterConstants;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;
import com.aptana.formatter.nodes.FormatterTextNode;

public class FormatterCommentNode extends FormatterTextNode {

	public FormatterCommentNode(IFormatterDocument document, int startOffset,
			int endOffset) {
		super(document, startOffset, endOffset);
	}

	public void accept(IFormatterContext context, IFormatterWriter visitor)
			throws Exception {
		if (getDocument().getBoolean(RubyFormatterConstants.WRAP_COMMENTS)) {
			final boolean savedWrapping = context.isWrapping();
			context.setWrapping(true);
			visitor.write(context, getStartOffset(), getEndOffset());
			context.setWrapping(savedWrapping);
		} else {
			visitor.write(context, getStartOffset(), getEndOffset());
		}
	}

}
