package com.aptana.editor.scripting.actions;

import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.swt.widgets.Shell;

public class SnippetsContentAssistant extends ContentAssistant {
	
	private IContentAssistProcessor contentAssistProcessor;
	private ExpandSnippetAction expandSnippetAction;
	
	protected static class DefaultInformationControlCreator extends AbstractReusableInformationControlCreator {
		public IInformationControl doCreateInformationControl(Shell shell) {
			DefaultInformationControl defaultInformationControl = new DefaultInformationControl(shell, true);
			return defaultInformationControl;
		}
	}

	public SnippetsContentAssistant(ExpandSnippetAction expandSnippet) {
		super();
		this.expandSnippetAction = expandSnippet;
		enableAutoActivation(false);
		enablePrefixCompletion(true);
		enableColoredLabels(true);
		setStatusLineVisible(true);
		setStatusMessage("Type 1..9 to select nth snippet");
		setInformationControlCreator(new DefaultInformationControlCreator());
	}
	
	@Override
	public IContentAssistProcessor getContentAssistProcessor(
			String contentType) {
		if (contentAssistProcessor == null) {
			contentAssistProcessor = new SnippetsCompletionProcessor(expandSnippetAction);
		}
		return contentAssistProcessor;
	}
	
}
