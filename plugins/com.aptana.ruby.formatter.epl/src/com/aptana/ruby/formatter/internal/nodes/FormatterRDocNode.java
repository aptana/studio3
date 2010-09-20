package com.aptana.ruby.formatter.internal.nodes;

import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;
import com.aptana.formatter.nodes.FormatterTextNode;


public class FormatterRDocNode extends FormatterTextNode {

	public FormatterRDocNode(IFormatterDocument document, int startOffset,
			int endOffset) {
		super(document, startOffset, endOffset);
	}

	public void accept(IFormatterContext context, IFormatterWriter visitor)
			throws Exception {
		IFormatterContext commentContext = context.copy();
		commentContext.setIndenting(false);
		visitor.write(commentContext, getStartOffset(), getEndOffset());
	}

}
