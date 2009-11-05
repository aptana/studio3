package tabsnippetexpansion;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class ExpandSnippet extends AbstractHandler
{
	private static class SnippetTemplateContextType extends TemplateContextType {
		SnippetTemplateContextType() {
			super("snippets");
			addGlobalResolvers();
		}

		private void addGlobalResolvers() {
			addResolver(new GlobalTemplateVariables.Cursor());
			addResolver(new GlobalTemplateVariables.WordSelection());
			addResolver(new GlobalTemplateVariables.LineSelection());
			addResolver(new GlobalTemplateVariables.Dollar());
			addResolver(new GlobalTemplateVariables.Date());
			addResolver(new GlobalTemplateVariables.Year());
			addResolver(new GlobalTemplateVariables.Time());
			addResolver(new GlobalTemplateVariables.User());
		}
	}
	
	private static class SnippetsCompletionProcessor extends TemplateCompletionProcessor {

		private static final SnippetTemplateContextType SNIPPET_TEMPLATE_CONTEXT_TYPE = new SnippetTemplateContextType();

		@Override
		protected TemplateContextType getContextType(ITextViewer viewer,
				IRegion region) {
			return SNIPPET_TEMPLATE_CONTEXT_TYPE;
		}

		@Override
		protected Image getImage(Template template) {
			return Activator.getDefault().getImage(Activator.SNIPPET);
		}

		@Override
		protected Template[] getTemplates(String contextTypeId) {
			return new Template[] {
					new Template("a", "Insert a s", "snippets", "${cursor}name: ${selection} address: ${address:2}", true)
					,new Template("aaaab", "Insert ab s and 10 bs", "snippets", "aabbbbbbb${1:hello}bbbbbbbbbb${2:hello}bbbbb", false)
					,new Template("aaaabc", "Insert abc s", "snippets", "aabbcc", false)
			};
		}
		
	}
	private ContentAssistant contentAssistant;
	
	public IContentAssistant getContentAssistant() {
		if (contentAssistant == null) {
			contentAssistant = new ContentAssistant() {
				private IContentAssistProcessor contentAssistProcessor = new SnippetsCompletionProcessor();
				@Override
				public IContentAssistProcessor getContentAssistProcessor(
						String contentType) {
					return contentAssistProcessor;
				}
			};
			contentAssistant.setAutoActivationDelay(10000);
			contentAssistant.enableAutoActivation(true);
			contentAssistant.enableAutoInsert(true);
			contentAssistant.enablePrefixCompletion(true);
		}
		return contentAssistant;
	}
	
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchWindow activeWorkbenchWindow = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = activeWorkbenchWindow.getActivePage();
		if (page == null)
		{
			return null;
		}
		IEditorPart editor = page.getActiveEditor();
		if (editor == null)
		{
			return null;
		}

		IWorkbenchPartSite site = editor.getSite();
		if (editor instanceof AbstractTextEditor)
		{
			AbstractTextEditor abstractTextEditor = (AbstractTextEditor) editor;
			if (abstractTextEditor.isEditable())
			{
				Object adapter = (Control) abstractTextEditor.getAdapter(Control.class);
				if (adapter instanceof Control)
				{
					Control control = (Control) adapter;
					if (control instanceof StyledText)
					{
						StyledText styledText = (StyledText) control;
						if (styledText.getSelectionCount() > 0)
						{
							return null;
						}

						int caretOffset = styledText.getCaretOffset();
						int lineAtOffset = styledText.getLineAtOffset(caretOffset);
						int offsetAtLine = styledText.getOffsetAtLine(lineAtOffset);
						if (offsetAtLine == caretOffset)
						{
							return null;
						}

						if (Character.isJavaIdentifierStart(styledText.getLine(lineAtOffset).charAt(
								caretOffset - offsetAtLine - 1)))
						{
							final ITextOperationTarget textOperationTarget = (ITextOperationTarget) abstractTextEditor
							.getAdapter(ITextOperationTarget.class);
							// Get the word at offset
							if (textOperationTarget instanceof ITextViewer)
							{
								final ITextViewer textViewer = (ITextViewer) textOperationTarget;
								BusyIndicator.showWhile(textViewer.getTextWidget().getDisplay(), new Runnable() {
									public void run() {
										IContentAssistant contentAssistant = getContentAssistant();
										contentAssistant.install(textViewer);
										contentAssistant.showPossibleCompletions();
									}
								});
								
							}
						}
					}
				}
			}
		}
		return null;
	}

	private boolean enabled = true;

	@Override
	public void setEnabled(Object object)
	{
		if (object instanceof IEvaluationContext)
		{
			IEvaluationContext evaluationContext = (IEvaluationContext) object;
			IEditorPart editor = (IEditorPart) evaluationContext.getVariable(ISources.ACTIVE_EDITOR_NAME);
			if (editor instanceof AbstractTextEditor)
			{
				AbstractTextEditor abstractTextEditor = (AbstractTextEditor) editor;
				if (!abstractTextEditor.isEditable())
				{
					enabled = false;
					return;
				}
				Object adapter = (Control) abstractTextEditor.getAdapter(Control.class);
				if (adapter instanceof Control)
				{
					Control control = (Control) adapter;
					if (control instanceof StyledText)
					{
						StyledText styledText = (StyledText) control;
						if (styledText.getSelectionCount() > 0)
						{
							enabled = false;
							return;
						}
						int caretOffset = styledText.getCaretOffset();
						int lineAtOffset = styledText.getLineAtOffset(caretOffset);
						int offsetAtLine = styledText.getOffsetAtLine(lineAtOffset);
						if (offsetAtLine == caretOffset)
						{
							enabled = false;
							return;
						}
					}
				}
				if (editor instanceof ITextEditor)
				{
					ITextEditor textEditor = (ITextEditor) editor;
					IDocumentProvider provider = textEditor.getDocumentProvider();
					if (provider != null)
					{
						IDocument document = provider.getDocument(editor.getEditorInput());
						if (document != null)
						{
							enabled = (!LinkedModeModel.hasInstalledModel(document));
							return;
						}
					}
				}
			}
		}
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public boolean isHandled()
	{
		return isEnabled();
	}

	public void removeHandlerListener(IHandlerListener handlerListener)
	{
	}

}
