package com.aptana.editor.scripting.actions;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class SnippetTemplateProposal extends TemplateProposal implements ICompletionProposalExtension6 {

	private final ExpandSnippetAction expandSnippet;
	private ICompletionProposal[] templateProposals;
	private char triggerChar;
	private char[] triggerChars;
	private String triggerCharSuffix = "";

	public SnippetTemplateProposal(Template template, TemplateContext context,
			IRegion region, Image image, int relevance, ExpandSnippetAction expandSnippet) {
		super(template, context, region, image, relevance);
		this.expandSnippet = expandSnippet;
	}

	@Override
	public void apply(ITextViewer viewer, char trigger, int stateMask, final int offset) {
		final IDocument document = viewer.getDocument();
		IDocumentListener documentListener = new IDocumentListener() {
			public void documentChanged(DocumentEvent event) {
				Display.getCurrent().asyncExec(new Runnable() {
					public void run() {
						if (LinkedModeModel.hasInstalledModel(document)) {
							expandSnippet.setDeactivated(true);
							LinkedModeModel linkedModeModel = LinkedModeModel.getModel(document, offset);
							linkedModeModel.addLinkingListener(new ILinkedModeListener() {								
								public void suspend(LinkedModeModel model) {
								}
								public void resume(LinkedModeModel model, int flags) {
								}
								public void left(LinkedModeModel model, int flags) {
									expandSnippet.setDeactivated(false);
								}
							});
						}
					}
				});
			}

			public void documentAboutToBeChanged(DocumentEvent event) {
			}
		}; 

		try {
			document.addDocumentListener(documentListener);
			if (contains(triggerChars, trigger)) {
				if (triggerChar == trigger) {
					super.apply(viewer, trigger, stateMask, offset);
				} else {
					((ICompletionProposalExtension2)templateProposals[trigger - '1']).apply(viewer, trigger, stateMask, offset);
				}
			} else {
				super.apply(viewer, trigger, stateMask, offset);
			}
		} finally {
			document.removeDocumentListener(documentListener);			
		}
	}
	
	@Override
	public char[] getTriggerCharacters() {
		return triggerChars;
	}
	
	@Override
	public String getDisplayString() {
		Template template = getTemplate();
		return template.getDescription() + " [ " + template.getName() + "\u21E5 ]";
	}

	public StyledString getStyledDisplayString() {
		Template template = getTemplate();
		return new StyledString(template.getDescription())
					.append(new StyledString("[ " + template.getName() + "\u21E5 ]",
							StyledString.COUNTER_STYLER)).append(triggerCharSuffix);
	}

	void setTriggerChar(char triggerChar) {
		this.triggerChar = triggerChar;
		triggerCharSuffix = "    " + String.valueOf(triggerChar);
	}
	
	private static final String TRIGGER_CHARS = "123456789";
	
	void setTemplateProposals(ICompletionProposal[] templateProposals) {
		this.templateProposals = templateProposals;
		triggerChars = new char[Math.max(templateProposals.length, TRIGGER_CHARS.length())];
		TRIGGER_CHARS.getChars(0, triggerChars.length-1, triggerChars, 0);
	}
	
	private static boolean contains(char[] characters, char c) {
		if (characters == null)
			return false;

		for (int i= 0; i < characters.length; i++) {
			if (c == characters[i])
				return true;
		}

		return false;
	}
}
