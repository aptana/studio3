package com.aptana.editor.scripting.actions;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class SnippetTemplateProposal extends TemplateProposal {

	private final ExpandSnippetAction expandSnippet;

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
			super.apply(viewer, trigger, stateMask, offset);
		} finally {
			document.removeDocumentListener(documentListener);			
		}
	}
	
	@Override
	public String getDisplayString() {
		Template template = getTemplate();
		return template.getDescription() + " (" + template.getName() + "\u21E5)";
	}
	
}
