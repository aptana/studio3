package com.aptana.editor.scripting.actions;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.DefaultInformationControl.IInformationPresenter;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SnippetsContentAssistant extends ContentAssistant {
	
	private IContentAssistProcessor contentAssistProcessor;
	private ExpandSnippetAction expandSnippetAction;
	
	private static class StringInformationPresenter implements IInformationPresenter {
		public String updatePresentation(Display display, String hoverInfo,
				TextPresentation presentation, int maxWidth, int maxHeight) {
			return hoverInfo;
		}
	}
	
	private static class DefaultInformationControlCreator extends AbstractReusableInformationControlCreator {
		public IInformationControl doCreateInformationControl(Shell shell) {
			DefaultInformationControl defaultInformationControl = new DefaultInformationControl(shell, new StringInformationPresenter()) {
				@Override
				protected void createContent(Composite parent) {
					super.createContent(parent);
					Control[] children = parent.getChildren();
					for (Control control : children) {
						if (control instanceof StyledText) {
							StyledText styledText = (StyledText) control;
							styledText.setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));
						}
					}
				}
			};
			return defaultInformationControl;
		}
	}

	public SnippetsContentAssistant(ExpandSnippetAction expandSnippet) {
		super();
		this.expandSnippetAction = expandSnippet;
		enableAutoActivation(false);
		enablePrefixCompletion(true);
		enableAutoInsert(true);
		enableColoredLabels(true);
		setStatusLineVisible(true);
		setStatusMessage("Type 1..9 to select nth snippet"); // TODO
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
