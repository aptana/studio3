package com.aptana.radrails.editor.common.actions;

import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import com.aptana.radrails.editor.common.DocumentContentTypeManager;
import com.aptana.radrails.editor.common.QualifiedContentType;
import com.aptana.radrails.editor.common.tmp.ContentTypeTranslation;

public class ShowScopesAction extends TextEditorAction {
	
	public static IAction create(ITextEditor textEditor, ITextViewer textViewer) {
		return new ShowScopesAction(ResourceBundle.getBundle(ShowScopesAction.class.getName()), "ShowScopesAction.", textEditor, textViewer);
	}
	
	public static final String COMMAND_ID = "com.aptana.radrails.editor.common.showscopes";
	
	protected static class DefaultInformationControlCreator extends AbstractReusableInformationControlCreator {
		public IInformationControl doCreateInformationControl(Shell shell) {
			return new DefaultInformationControl(shell, true);
		}
	}
	
	private final ITextViewer textViewer;

	public ShowScopesAction(ResourceBundle bundle, String prefix,
			ITextEditor editor, ITextViewer textViewer) {
		super(bundle, prefix, editor);
		this.textViewer = textViewer;
		setActionDefinitionId(COMMAND_ID);
	}
	
	@Override
	public void run() {
        ITextEditor textEditor = getTextEditor();
        try {
        	Shell shell = textEditor.getEditorSite().getShell();
			StyledText textWidget = textViewer.getTextWidget();
			int caretOffset = textWidget.getCaretOffset();
			String contentType = getContentTypeAtOffset(textEditor, caretOffset);
			System.out.println(contentType);
			Point locationAtOffset = textWidget.getLocationAtOffset(caretOffset);
			locationAtOffset.y += textWidget.getLineHeight(caretOffset) + 2;
			locationAtOffset = shell.getDisplay().map(textWidget, null, locationAtOffset);
			popup(shell, contentType+"\n", locationAtOffset);
        } catch (BadLocationException e) {
        	System.err.println(e.getMessage());
        }
	}
	
	private String getContentTypeAtOffset(ITextEditor textEditor, int offset) throws BadLocationException {
		IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		QualifiedContentType contentType = DocumentContentTypeManager.getInstance().getContentType(document, offset);
		if (contentType != null) {
			return ContentTypeTranslation.getDefault().translate(contentType).toString();
		}
		return document.getContentType(offset);
	}
	
	private void popup(Shell shell, final String description, final Point location) {
		PopupDialog popupDialog = new PopupDialog(shell, PopupDialog.HOVER_SHELLSTYLE,
				true, false, false, false, false, null, "Type Escape to dismiss.") {
			
			@Override
			protected Point getInitialLocation(Point initialSize) {
				return location;
			}

			protected Control createDialogArea(Composite parent) {
				Label label = new Label(parent, SWT.WRAP);
				label.setText(description);
				label.addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent event) {
						close();
					}
				});
				// Use the compact margins employed by PopupDialog.
				GridData gd = new GridData(GridData.BEGINNING
						| GridData.FILL_BOTH);
				gd.horizontalIndent = PopupDialog.POPUP_HORIZONTALSPACING;
				gd.verticalIndent = PopupDialog.POPUP_VERTICALSPACING;
				label.setLayoutData(gd);
				return label;
			}
		};
		popupDialog.open();
	}

}
