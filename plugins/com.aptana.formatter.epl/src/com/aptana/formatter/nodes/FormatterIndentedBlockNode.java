package com.aptana.formatter.nodes;

import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;

public class FormatterIndentedBlockNode extends FormatterBlockNode {

	private final boolean indenting;

	public FormatterIndentedBlockNode(IFormatterDocument document,
			boolean indenting) {
		super(document);
		this.indenting = indenting;
	}

	public void accept(IFormatterContext context, IFormatterWriter visitor)
			throws Exception {
		if (isIndenting()) {
			context.incIndent();
		}
		super.accept(context, visitor);
		if (isIndenting()) {
			context.decIndent();
		}
	}

	protected boolean isIndenting() {
		return indenting;
	}

}
