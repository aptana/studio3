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
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

public class SnippetTemplateProposal extends TemplateProposal implements ICompletionProposalExtension6 {

	private ICompletionProposal[] templateProposals;
	private char triggerChar;
	private char[] triggerChars;
	private StyledString styledDisplayString;

	public SnippetTemplateProposal(Template template, TemplateContext context,
			IRegion region, Image image, int relevance) {
		super(template, context, region, image, relevance);
	}

	@Override
	public void apply(final ITextViewer viewer, char trigger, int stateMask, final int offset) {
		final IDocument document = viewer.getDocument();
		final StyledText textWidget = viewer.getTextWidget();

		IDocumentListener documentListener = new IDocumentListener() {
			public void documentChanged(DocumentEvent event) {
				Display.getCurrent().asyncExec(new Runnable() {
					public void run() {
						if (LinkedModeModel.hasInstalledModel(document)) {
							final LinkedModeModel linkedModeModel = LinkedModeModel.getModel(document, offset);
							final VerifyKeyListener keyListener = new VerifyKeyListener() {
								public void verifyKey(VerifyEvent event) {
									Point selection= viewer.getSelectedRange();
									int offset= selection.x;
									int length= selection.y;
									LinkedPosition findPosition = linkedModeModel.findPosition(new LinkedPosition(document, offset, length, LinkedPositionGroup.NO_STOP));
									if (findPosition == null) {
										linkedModeModel.exit(ILinkedModeListener.EXIT_ALL);
									}
								}
							};
							final MouseListener mouseListener = new MouseListener() {
								public void mouseUp(MouseEvent e) {
								}
								
								public void mouseDown(MouseEvent e) {
									Point selection= viewer.getSelectedRange();
									int offset= selection.x;
									int length= selection.y;
									LinkedPosition findPosition = linkedModeModel.findPosition(new LinkedPosition(document, offset, length, LinkedPositionGroup.NO_STOP));
									if (findPosition == null) {
										linkedModeModel.exit(ILinkedModeListener.EXIT_ALL);
									}
								}
								
								public void mouseDoubleClick(MouseEvent e) {
									
								}
							};
							textWidget.addVerifyKeyListener(keyListener);
							textWidget.addMouseListener(mouseListener);
							linkedModeModel.addLinkingListener(new ILinkedModeListener() {								
								public void suspend(LinkedModeModel model) {
								}
								public void resume(LinkedModeModel model, int flags) {
								}
								public void left(LinkedModeModel model, int flags) {
									textWidget.removeVerifyKeyListener(keyListener);
									textWidget.removeMouseListener(mouseListener);
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
		return template.getDescription() + " [ " + template.getName() + "\u21E5 ]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public StyledString getStyledDisplayString() {
		return styledDisplayString;
	}
	
	Template getTemplateSuper() {
		return super.getTemplate();
	}
	
	void setStyledDisplayString(StyledString styledDisplayString) {
		this.styledDisplayString = styledDisplayString;
	}

	void setTriggerChar(char triggerChar) {
		this.triggerChar = triggerChar;
	}
	
	private static final String TRIGGER_CHARS = "123456789"; //$NON-NLS-1$
	
	void setTemplateProposals(ICompletionProposal[] templateProposals) {
		this.templateProposals = templateProposals;
		triggerChars = new char[Math.min(templateProposals.length, TRIGGER_CHARS.length())];
		TRIGGER_CHARS.getChars(0, triggerChars.length, triggerChars, 0);
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
