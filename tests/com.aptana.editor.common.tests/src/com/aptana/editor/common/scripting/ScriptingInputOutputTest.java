package com.aptana.editor.common.scripting;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.scripting.commands.CommandExecutionUtils;
import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.CommandResult;
import com.aptana.scripting.model.InputType;
import com.aptana.scripting.model.OutputType;

/**
 * This "unit" test is really a higher-level integration tests for checking how command output is applied to a text
 * editor given various combinations of input and output types.
 * 
 * @author cwilliams
 */
public class ScriptingInputOutputTest extends TestCase
{

	private static final String PROJECT_NAME = "scripting_io";
	
	private IProject project;
	private IFile file;
	private ITextEditor editor;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		project = createProject();
	}

	@Override
	protected void tearDown() throws Exception
	{
		try
		{
			// Need to force the editor shut!
			editor.close(false);
			// Delete the generated file
			file.delete(true, new NullProgressMonitor());
			// Delete the generated project
			project.delete(true, new NullProgressMonitor());
		}
		finally
		{
			editor = null;
			file = null;
			project = null;
			super.tearDown();
		}
	}

	public void testReplaceSelection() throws Exception
	{
		createAndOpenFile("replace_selection.txt", "Hello world!");
		select(6, 5); // Select 'world'
		applyCommandResult(InputType.SELECTION, OutputType.REPLACE_SELECTION, "output");
		assertContents("Hello output!");
	}

	public void testReplaceSelectionWithInputLine() throws Exception
	{
		createAndOpenFile("replace_selection_line_input.txt", "Hello world!");
		select(6, 5); // Select 'world'
		applyCommandResult(InputType.LINE, OutputType.REPLACE_SELECTION, "output");
		assertContents("output");
	}

	public void testReplaceSelectionWithInputWord() throws Exception
	{
		createAndOpenFile("replace_selection_word_input.txt", "Hello world!");
		setCaretOffset(1);
		applyCommandResult(InputType.WORD, OutputType.REPLACE_SELECTION, "output");
		assertContents("output world!");
	}

	public void testReplaceSelectionWithInputLeftChar() throws Exception
	{
		createAndOpenFile("replace_selection_lchar_input.txt", "Hello world!");
		setCaretOffset(1);
		applyCommandResult(InputType.LEFT_CHAR, OutputType.REPLACE_SELECTION, "output");
		assertContents("outputello world!");
	}

	public void testReplaceSelectionWithInputRightChar() throws Exception
	{
		createAndOpenFile("replace_selection_rchar_input.txt", "Hello world!");
		setCaretOffset(1);
		applyCommandResult(InputType.RIGHT_CHAR, OutputType.REPLACE_SELECTION, "output");
		assertContents("Houtputllo world!");
	}

	public void testReplaceSelectionWithInputNone() throws Exception
	{
		createAndOpenFile("replace_selection_no_input.txt", "Hello world!");
		select(6, 5); // Select 'world'
		applyCommandResult(InputType.NONE, OutputType.REPLACE_SELECTION, "output");
		assertContents("Hello output!");
	}

	public void testReplaceSelectionWithInputDocument() throws Exception
	{
		createAndOpenFile("replace_selection_doc_input.txt", "Hello world!\nSecond line!");
		applyCommandResult(InputType.DOCUMENT, OutputType.REPLACE_SELECTION, "output");
		assertContents("output");
	}

	public void testReplaceSelectionWithInputSelectedLines() throws Exception
	{
		createAndOpenFile("replace_selection_selected_lines_input.txt", "Hello world!\nSecond line!");
		select(6, 13);
		applyCommandResult(InputType.SELECTED_LINES, OutputType.REPLACE_SELECTION, "output");
		assertContents("output");
	}

	public void testReplaceSelectionWithInputSelectedLines2() throws Exception
	{
		createAndOpenFile("replace_selection_selected_lines2_input.txt", "Hello world!\nSecond line!");
		select(6, 5);
		applyCommandResult(InputType.SELECTED_LINES, OutputType.REPLACE_SELECTION, "output");
		assertContents("output\nSecond line!");
	}

	public void testDiscard() throws Exception
	{
		createAndOpenFile("chris.txt", "Hello world!");
		select(6, 5);
		applyCommandResult(InputType.SELECTION, OutputType.DISCARD, "output");
		assertContents("Hello world!");
		// TODO Test with all the input types?
	}

	public void testInsertTextWithLineInputInsertsAtEndOfLine() throws Exception
	{
		createAndOpenFile("insert_text_line_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(1);
		applyCommandResult(InputType.LINE, OutputType.INSERT_AS_TEXT, "output");
		// Inserts at end of line
		assertContents("Hello world!output\nSecond line!");
	}

	public void testInsertTextWithSelectionInputInsertsAfterSelection() throws Exception
	{
		createAndOpenFile("insert_text_selection_input.txt", "Hello world!\nSecond line!");
		select(6, 5);
		applyCommandResult(InputType.SELECTION, OutputType.INSERT_AS_TEXT, "output");
		// Inserts at end of selection
		assertContents("Hello worldoutput!\nSecond line!");
	}

	public void testInsertTextWithWordInputInsertsAfterCurrentWord() throws Exception
	{
		createAndOpenFile("insert_text_word_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(1);
		applyCommandResult(InputType.WORD, OutputType.INSERT_AS_TEXT, "output");
		// Inserts at end of current word
		assertContents("Hellooutput world!\nSecond line!");
	}

	public void testInsertTextWithLeftCharInputInsertsAfterLeftChar() throws Exception
	{
		createAndOpenFile("insert_text_lchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(1);
		applyCommandResult(InputType.LEFT_CHAR, OutputType.INSERT_AS_TEXT, "output");
		// Inserts after left char
		assertContents("Houtputello world!\nSecond line!");
	}

	public void testInsertTextWithRightCharInputInsertsAfterRightChar() throws Exception
	{
		createAndOpenFile("insert_text_rchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(1);
		applyCommandResult(InputType.RIGHT_CHAR, OutputType.INSERT_AS_TEXT, "output");
		// Inserts after right char
		assertContents("Heoutputllo world!\nSecond line!");
	}

	public void testInsertTextWithDocInputInsertsAtEndOfDocument() throws Exception
	{
		createAndOpenFile("insert_text_doc_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(1);
		applyCommandResult(InputType.DOCUMENT, OutputType.INSERT_AS_TEXT, "output");
		// Inserts at end of doc
		assertContents("Hello world!\nSecond line!output");
	}

	public void testInsertTextWithNoInputInsertsAtCaretOffset() throws Exception
	{
		createAndOpenFile("insert_text_no_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(15);
		applyCommandResult(InputType.NONE, OutputType.INSERT_AS_TEXT, "output");
		// Inserts at caret offset
		assertContents("Hello world!\nSeoutputcond line!");
	}

	public void testInsertTextWithSelectedLinesInputInsertsAtEndOfSelectionEndLine() throws Exception
	{
		createAndOpenFile("insert_text_selected_lines_input.txt", "Hello world!\nSecond line!");
		select(6, 13);
		applyCommandResult(InputType.SELECTED_LINES, OutputType.INSERT_AS_TEXT, "output");
		// Inserts at end of selection's end line
		assertContents("Hello world!\nSecond line!output");
	}

	public void testReplaceLineWithSelectionInputReplacesCaretLine() throws Exception
	{
		createAndOpenFile("replace_line.txt", "Hello world!");
		select(6, 5);
		applyCommandResult(InputType.SELECTION, OutputType.REPLACE_LINE, "output");
		assertContents("output");
	}

	public void testReplaceLineWithDocumentInputReplacesCaretLine() throws Exception
	{
		createAndOpenFile("replace_line_doc_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(1);
		applyCommandResult(InputType.DOCUMENT, OutputType.REPLACE_LINE, "output");
		assertContents("output\nSecond line!");
	}

	public void testReplaceLineWithNoInputReplacesCaretLine() throws Exception
	{
		createAndOpenFile("replace_line_no_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(1);
		applyCommandResult(InputType.NONE, OutputType.REPLACE_LINE, "output");
		assertContents("output\nSecond line!");
	}

	public void testReplaceLineWithWordInputReplacesCaretLine() throws Exception
	{
		createAndOpenFile("replace_line_word_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.NONE, OutputType.REPLACE_LINE, "output");
		assertContents("Hello world!\noutput");
	}

	public void testReplaceLineWithLineInputReplacesCaretLine() throws Exception
	{
		createAndOpenFile("replace_line_line_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.LINE, OutputType.REPLACE_LINE, "output");
		assertContents("Hello world!\noutput");
	}

	public void testReplaceLineWithLeftCharInputReplacesCaretLine() throws Exception
	{
		createAndOpenFile("replace_line_lchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.LEFT_CHAR, OutputType.REPLACE_LINE, "output");
		assertContents("Hello world!\noutput");
	}

	public void testReplaceLineWithRightCharInputReplacesCaretLine() throws Exception
	{
		createAndOpenFile("replace_line_rchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.RIGHT_CHAR, OutputType.REPLACE_LINE, "output");
		assertContents("Hello world!\noutput");
		// TODO Test replace line with inputs of left and right char when they hit line boundary!
	}

	public void testReplaceLineWithSelectedLinesInputReplacesCaretLine() throws Exception
	{
		createAndOpenFile("replace_line_selected_lines_input.txt", "Hello world!\nSecond line!");
		select(6, 5);
		applyCommandResult(InputType.SELECTED_LINES, OutputType.REPLACE_LINE, "output");
		assertContents("output\nSecond line!");
	}

	public void testReplaceLineWithSelectedLinesInputReplacesCaretLine2() throws Exception
	{
		createAndOpenFile("replace_line_selected_lines_input2.txt", "Hello world!\nSecond line!");
		select(6, 13);
		applyCommandResult(InputType.SELECTED_LINES, OutputType.REPLACE_LINE, "output");
		assertContents("Hello world!\noutput");
	}

	public void testReplaceWordWithNoInput() throws Exception
	{
		createAndOpenFile("replace_word_no_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.NONE, OutputType.REPLACE_WORD, "output");
		assertContents("Hello world!\nSecond output!");
	}

	public void testReplaceWordWithDocumentInput() throws Exception
	{
		createAndOpenFile("replace_word_doc_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.DOCUMENT, OutputType.REPLACE_WORD, "output");
		assertContents("Hello world!\nSecond output!");
	}

	public void testReplaceWordWithWordInput() throws Exception
	{
		createAndOpenFile("replace_word_word_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.WORD, OutputType.REPLACE_WORD, "output");
		assertContents("Hello world!\nSecond output!");
	}

	public void testReplaceWordWithLeftCharInput() throws Exception
	{
		createAndOpenFile("replace_word_lchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.LEFT_CHAR, OutputType.REPLACE_WORD, "output");
		assertContents("Hello world!\nSecond output!");
	}

	public void testReplaceWordWithRightCharInput() throws Exception
	{
		createAndOpenFile("replace_word_rchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.RIGHT_CHAR, OutputType.REPLACE_WORD, "output");
		assertContents("Hello world!\nSecond output!");
	}

	public void testReplaceWordWithSelectionInput() throws Exception
	{
		createAndOpenFile("replace_word_selection_input.txt", "Hello world!\nSecond line!");
		select(6, 5);
		applyCommandResult(InputType.SELECTION, OutputType.REPLACE_WORD, "output");
		assertContents("Hello output!\nSecond line!");
	}

	public void testReplaceWordWithSelectedLinesInput() throws Exception
	{
		createAndOpenFile("replace_word_selected_lines_input.txt", "Hello world!\nSecond line!");
		select(6, 13);
		applyCommandResult(InputType.SELECTED_LINES, OutputType.REPLACE_WORD, "output");
		assertContents("Hello world!\noutput line!");
	}

	public void testReplaceWordWithLineInput() throws Exception
	{
		createAndOpenFile("replace_word_line_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(4);
		applyCommandResult(InputType.LINE, OutputType.REPLACE_WORD, "output");
		assertContents("output world!\nSecond line!");
	}

	public void testReplaceDocumentWithNoInput() throws Exception
	{
		createAndOpenFile("replace_doc_no_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.NONE, OutputType.REPLACE_DOCUMENT, "output");
		assertContents("output");
	}

	public void testReplaceDocumentWithDocumentInput() throws Exception
	{
		createAndOpenFile("replace_doc_doc_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.DOCUMENT, OutputType.REPLACE_DOCUMENT, "output");
		assertContents("output");
	}

	public void testReplaceDocumentWithWordInput() throws Exception
	{
		createAndOpenFile("replace_doc_word_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.WORD, OutputType.REPLACE_DOCUMENT, "output");
		assertContents("output");
	}

	public void testReplaceDocumentWithLeftCharInput() throws Exception
	{
		createAndOpenFile("replace_doc_lchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.LEFT_CHAR, OutputType.REPLACE_DOCUMENT, "output");
		assertContents("output");
	}

	public void testReplaceDocumentWithRightCharInput() throws Exception
	{
		createAndOpenFile("replace_doc_rchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.RIGHT_CHAR, OutputType.REPLACE_DOCUMENT, "output");
		assertContents("output");
	}

	public void testReplaceDocumentWithSelectionInput() throws Exception
	{
		createAndOpenFile("replace_doc_selection_input.txt", "Hello world!\nSecond line!");
		select(6, 5);
		applyCommandResult(InputType.SELECTION, OutputType.REPLACE_DOCUMENT, "output");
		assertContents("output");
	}

	public void testReplaceDocumentWithSelectedLinesInput() throws Exception
	{
		createAndOpenFile("replace_doc_selected_lines_input.txt", "Hello world!\nSecond line!");
		select(6, 13);
		applyCommandResult(InputType.SELECTED_LINES, OutputType.REPLACE_DOCUMENT, "output");
		assertContents("output");
	}

	public void testReplaceDocumentWithLineInput() throws Exception
	{
		createAndOpenFile("replace_doc_line_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(4);
		applyCommandResult(InputType.LINE, OutputType.REPLACE_DOCUMENT, "output");
		assertContents("output");
	}

	public void testReplaceSelectedLinesWithNoInput() throws Exception
	{
		createAndOpenFile("replace_selected_lines_no_input.txt", "Hello world!\nSecond line!");
		select(6, 13);
		applyCommandResult(InputType.NONE, OutputType.REPLACE_SELECTED_LINES, "output");
		assertContents("output");
	}

	public void testReplaceSelectedLinesWithDocumentInput() throws Exception
	{
		createAndOpenFile("replace_selected_lines_doc_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.DOCUMENT, OutputType.REPLACE_SELECTED_LINES, "output");
		assertContents("Hello world!\noutput");
	}

	public void testReplaceSelectedLinesWithWordInput() throws Exception
	{
		createAndOpenFile("replace_selected_lines_word_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.WORD, OutputType.REPLACE_SELECTED_LINES, "output");
		assertContents("Hello world!\noutput");
	}

	public void testReplaceSelectedLinesWithLeftCharInput() throws Exception
	{
		createAndOpenFile("replace_selected_lines_lchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.LEFT_CHAR, OutputType.REPLACE_SELECTED_LINES, "output");
		assertContents("Hello world!\noutput");
	}

	public void testReplaceSelectedLinesWithRightCharInput() throws Exception
	{
		createAndOpenFile("replace_selected_lines_rchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.RIGHT_CHAR, OutputType.REPLACE_SELECTED_LINES, "output");
		assertContents("Hello world!\noutput");
	}

	public void testReplaceSelectedLinesWithSelectionInput() throws Exception
	{
		createAndOpenFile("replace_selected_lines_selection_input.txt", "Hello world!\nSecond line!");
		select(6, 5);
		applyCommandResult(InputType.SELECTION, OutputType.REPLACE_SELECTED_LINES, "output");
		assertContents("output\nSecond line!");
	}

	public void testReplaceSelectedLinesWithSelectedLinesInput() throws Exception
	{
		createAndOpenFile("replace_selected_lines_selected_lines_input.txt", "Hello world!\nSecond line!");
		select(6, 13);
		applyCommandResult(InputType.SELECTED_LINES, OutputType.REPLACE_SELECTED_LINES, "output");
		assertContents("output");
	}

	public void testReplaceSelectedLinesWithLineInput() throws Exception
	{
		createAndOpenFile("replace_selected_lines_line_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(4);
		applyCommandResult(InputType.LINE, OutputType.REPLACE_SELECTED_LINES, "output");
		assertContents("output\nSecond line!");
	}

	public void testInsertAsSnippetWithNoInputInsertsAtCaret() throws Exception
	{
		createAndOpenFile("insert_as_snippet_no_input.txt", "Hello world!\nSecond line!");
		select(6, 13);
		applyCommandResult(InputType.NONE, OutputType.INSERT_AS_SNIPPET, "output");
		assertContents("Hello world!\nSecondoutput line!");
		// TODO Test tab stop variables/cursor position after
	}

	public void testInsertAsSnippetWithDocumentInputReplacesDocumentWithSnippet() throws Exception
	{
		createAndOpenFile("insert_as_snippet_doc_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.DOCUMENT, OutputType.INSERT_AS_SNIPPET, "output");
		assertContents("output");
	}

	public void testInsertAsSnippetWithWordInputReplacesWordWithSnippet() throws Exception
	{
		createAndOpenFile("insert_as_snippet_word_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.WORD, OutputType.INSERT_AS_SNIPPET, "output");
		assertContents("Hello world!\nSecond output!");
	}

	public void testInsertAsSnippetWithLeftCharInputReplacesCharWithSnippet() throws Exception
	{
		createAndOpenFile("insert_as_snippet_lchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.LEFT_CHAR, OutputType.INSERT_AS_SNIPPET, "output");
		assertContents("Hello world!\nSecond outputine!");
	}

	public void testInsertAsSnippetWithRightCharInputReplacesCharWithSnippet() throws Exception
	{
		createAndOpenFile("insert_as_snippet_rchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.RIGHT_CHAR, OutputType.INSERT_AS_SNIPPET, "output");
		assertContents("Hello world!\nSecond loutputne!");
	}

	public void testInsertAsSnippetWithSelectionInputReplacesSelectionWithSnippet() throws Exception
	{
		createAndOpenFile("insert_as_snippet_selection_input.txt", "Hello world!\nSecond line!");
		select(6, 5);
		applyCommandResult(InputType.SELECTION, OutputType.INSERT_AS_SNIPPET, "output");
		assertContents("Hello output!\nSecond line!");
	}

	public void testInsertAsSnippetWithSelectedLinesInputReplacesSelectedLinesWithSnippet() throws Exception
	{
		createAndOpenFile("insert_as_snippet_selected_lines_input.txt", "Hello world!\nSecond line!");
		select(6, 13);
		applyCommandResult(InputType.SELECTED_LINES, OutputType.INSERT_AS_SNIPPET, "output");
		assertContents("output");
	}

	public void testInsertAsSnippetWithLineInputReplacesLineWithSnippet() throws Exception
	{
		createAndOpenFile("insert_as_snippet_line_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(4);
		applyCommandResult(InputType.LINE, OutputType.INSERT_AS_SNIPPET, "output");
		assertContents("output\nSecond line!");
	}

	protected IFile createAndOpenFile(String fileName, String contents) throws CoreException, PartInitException
	{
		if (file == null)
		{
			file = createFile(project, fileName, contents);
			getEditor();
		}
		return file;
	}

	protected void select(int offset, int length) throws PartInitException
	{
		setCaretOffset(offset);
		getEditor().selectAndReveal(offset, length);
	}

	protected void setCaretOffset(int offset) throws PartInitException
	{
		getTextWidget().setCaretOffset(offset);
	}

	protected StyledText getTextWidget() throws PartInitException
	{
		ITextViewer adapter = (ITextViewer) getEditor().getAdapter(ITextOperationTarget.class);
		return adapter.getTextWidget();
	}

	private ITextEditor getEditor() throws PartInitException
	{
		if (editor == null)
		{
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			editor = (ITextEditor) IDE.openEditor(page, file);
		}
		return editor;
	}

	protected void assertContents(String expected) throws PartInitException
	{
		assertEquals(expected, getTextWidget().getText());
	}

	protected void applyCommandResult(InputType inputType, OutputType outputType, String output)
	{
		CommandElement commandElement = new CommandElement("fake/path.rb");
		CommandResult commandResult = createCommandResult(commandElement, inputType, outputType, "output");
		CommandExecutionUtils.processCommandResult(commandElement, commandResult, editor);
	}

	protected CommandResult createCommandResult(CommandElement commandElement, final InputType inputType,
			final OutputType outputType, final String output)
	{
		CommandContext context = commandElement.createCommandContext();
		CommandResult commandResult = new CommandResult(null, context)
		{
			@Override
			public String getOutputString()
			{
				return output;
			}

			@Override
			public boolean executedSuccessfully()
			{
				return true;
			}

			@Override
			public OutputType getOutputType()
			{
				return outputType;
			}

			@Override
			public InputType getInputType()
			{
				return inputType;
			}
		};
		return commandResult;
	}

	protected IFile createFile(IProject project, String fileName, String contents) throws CoreException
	{
		IFile file = project.getFile(fileName);
		ByteArrayInputStream source = new ByteArrayInputStream(contents.getBytes());
		file.create(source, true, new NullProgressMonitor());
		return file;
	}

	protected IProject createProject() throws CoreException
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject(PROJECT_NAME);
		if (!project.exists())
			project.create(new NullProgressMonitor());
		if (!project.isOpen())
			project.open(new NullProgressMonitor());
		return project;
	}

}
