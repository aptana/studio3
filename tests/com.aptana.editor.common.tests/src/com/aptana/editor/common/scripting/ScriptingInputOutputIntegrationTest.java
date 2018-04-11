/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting;

import org.eclipse.ui.PartInitException;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.aptana.editor.common.scripting.commands.CommandExecutionUtils;
import com.aptana.editor.common.tests.SingleEditorTestCase;
import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.CommandResult;
import com.aptana.scripting.model.InputType;
import com.aptana.scripting.model.OutputType;
import com.aptana.testing.categories.IntegrationTests;

/**
 * This "unit" test is really a higher-level integration tests for checking how command output is applied to a text
 * editor given various combinations of input and output types.
 * 
 * @author cwilliams
 */
@Category({ IntegrationTests.class })
public class ScriptingInputOutputIntegrationTest extends SingleEditorTestCase
{

	private static final String PROJECT_NAME = "scripting_io";

	@Test
	public void testReplaceSelection() throws Exception
	{
		createAndOpenFile("replace_selection.txt", "Hello world!");
		select(6, 5); // Select 'world'
		applyCommandResult(InputType.SELECTION, OutputType.REPLACE_SELECTION, "output");
		assertContents("Hello output!");
	}

	@Test
	public void testReplaceSelectionWithInputLine() throws Exception
	{
		createAndOpenFile("replace_selection_line_input.txt", "Hello world!");
		select(6, 5); // Select 'world'
		applyCommandResult(InputType.LINE, OutputType.REPLACE_SELECTION, "output");
		assertContents("output");
	}

	@Test
	public void testReplaceSelectionWithInputWord() throws Exception
	{
		createAndOpenFile("replace_selection_word_input.txt", "Hello world!");
		setCaretOffset(1);
		applyCommandResult(InputType.WORD, OutputType.REPLACE_SELECTION, "output");
		assertContents("output world!");
	}

	@Test
	public void testReplaceSelectionWithInputLeftChar() throws Exception
	{
		createAndOpenFile("replace_selection_lchar_input.txt", "Hello world!");
		setCaretOffset(1);
		applyCommandResult(InputType.LEFT_CHAR, OutputType.REPLACE_SELECTION, "output");
		assertContents("outputello world!");
	}

	@Test
	public void testReplaceSelectionWithInputRightChar() throws Exception
	{
		createAndOpenFile("replace_selection_rchar_input.txt", "Hello world!");
		setCaretOffset(1);
		applyCommandResult(InputType.RIGHT_CHAR, OutputType.REPLACE_SELECTION, "output");
		assertContents("Houtputllo world!");
	}

	@Test
	public void testReplaceSelectionWithInputNone() throws Exception
	{
		createAndOpenFile("replace_selection_no_input.txt", "Hello world!");
		select(6, 5); // Select 'world'
		applyCommandResult(InputType.NONE, OutputType.REPLACE_SELECTION, "output");
		assertContents("Hello output!");
	}

	@Test
	public void testReplaceSelectionWithInputDocument() throws Exception
	{
		createAndOpenFile("replace_selection_doc_input.txt", "Hello world!\nSecond line!");
		applyCommandResult(InputType.DOCUMENT, OutputType.REPLACE_SELECTION, "output");
		assertContents("output");
	}

	@Test
	public void testReplaceSelectionWithInputSelectedLines() throws Exception
	{
		createAndOpenFile("replace_selection_selected_lines_input.txt", "Hello world!\nSecond line!");
		select(6, 13);
		applyCommandResult(InputType.SELECTED_LINES, OutputType.REPLACE_SELECTION, "output");
		assertContents("output");
	}

	@Test
	public void testReplaceSelectionWithInputSelectedLines2() throws Exception
	{
		createAndOpenFile("replace_selection_selected_lines2_input.txt", "Hello world!\nSecond line!");
		select(6, 5);
		applyCommandResult(InputType.SELECTED_LINES, OutputType.REPLACE_SELECTION, "output");
		assertContents("output\nSecond line!");
	}

	@Test
	public void testDiscard() throws Exception
	{
		createAndOpenFile("chris.txt", "Hello world!");
		select(6, 5);
		applyCommandResult(InputType.SELECTION, OutputType.DISCARD, "output");
		assertContents("Hello world!");
		// TODO Test with all the input types?
	}

	@Test
	public void testInsertTextWithLineInputInsertsAtEndOfLine() throws Exception
	{
		createAndOpenFile("insert_text_line_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(1);
		applyCommandResult(InputType.LINE, OutputType.INSERT_AS_TEXT, "output");
		// Inserts at end of line
		assertContents("Hello world!output\nSecond line!");
	}

	@Test
	public void testInsertTextWithSelectionInputInsertsAfterSelection() throws Exception
	{
		createAndOpenFile("insert_text_selection_input.txt", "Hello world!\nSecond line!");
		select(6, 5);
		applyCommandResult(InputType.SELECTION, OutputType.INSERT_AS_TEXT, "output");
		// Inserts at end of selection
		assertContents("Hello worldoutput!\nSecond line!");
	}

	@Test
	public void testInsertTextWithWordInputInsertsAfterCurrentWord() throws Exception
	{
		createAndOpenFile("insert_text_word_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(1);
		applyCommandResult(InputType.WORD, OutputType.INSERT_AS_TEXT, "output");
		// Inserts at end of current word
		assertContents("Hellooutput world!\nSecond line!");
	}

	@Test
	public void testInsertTextWithLeftCharInputInsertsAfterLeftChar() throws Exception
	{
		createAndOpenFile("insert_text_lchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(1);
		applyCommandResult(InputType.LEFT_CHAR, OutputType.INSERT_AS_TEXT, "output");
		// Inserts after left char
		assertContents("Houtputello world!\nSecond line!");
	}

	@Test
	public void testInsertTextWithRightCharInputInsertsAfterRightChar() throws Exception
	{
		createAndOpenFile("insert_text_rchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(1);
		applyCommandResult(InputType.RIGHT_CHAR, OutputType.INSERT_AS_TEXT, "output");
		// Inserts after right char
		assertContents("Heoutputllo world!\nSecond line!");
	}

	@Test
	public void testInsertTextWithDocInputInsertsAtEndOfDocument() throws Exception
	{
		createAndOpenFile("insert_text_doc_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(1);
		applyCommandResult(InputType.DOCUMENT, OutputType.INSERT_AS_TEXT, "output");
		// Inserts at end of doc
		assertContents("Hello world!\nSecond line!output");
	}

	@Test
	public void testInsertTextWithNoInputInsertsAtCaretOffset() throws Exception
	{
		createAndOpenFile("insert_text_no_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(15);
		applyCommandResult(InputType.NONE, OutputType.INSERT_AS_TEXT, "output");
		// Inserts at caret offset
		assertContents("Hello world!\nSeoutputcond line!");
	}

	@Test
	public void testInsertTextWithSelectedLinesInputInsertsAtEndOfSelectionEndLine() throws Exception
	{
		createAndOpenFile("insert_text_selected_lines_input.txt", "Hello world!\nSecond line!");
		select(6, 13);
		applyCommandResult(InputType.SELECTED_LINES, OutputType.INSERT_AS_TEXT, "output");
		// Inserts at end of selection's end line
		assertContents("Hello world!\nSecond line!output");
	}

	@Test
	public void testReplaceLineWithSelectionInputReplacesCaretLine() throws Exception
	{
		createAndOpenFile("replace_line.txt", "Hello world!");
		select(6, 5);
		applyCommandResult(InputType.SELECTION, OutputType.REPLACE_LINE, "output");
		assertContents("output");
	}

	@Test
	public void testReplaceLineWithDocumentInputReplacesCaretLine() throws Exception
	{
		createAndOpenFile("replace_line_doc_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(1);
		applyCommandResult(InputType.DOCUMENT, OutputType.REPLACE_LINE, "output");
		assertContents("output\nSecond line!");
	}

	@Test
	public void testReplaceLineWithNoInputReplacesCaretLine() throws Exception
	{
		createAndOpenFile("replace_line_no_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(1);
		applyCommandResult(InputType.NONE, OutputType.REPLACE_LINE, "output");
		assertContents("output\nSecond line!");
	}

	@Test
	public void testReplaceLineWithWordInputReplacesCaretLine() throws Exception
	{
		createAndOpenFile("replace_line_word_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.NONE, OutputType.REPLACE_LINE, "output");
		assertContents("Hello world!\noutput");
	}

	@Test
	public void testReplaceLineWithLineInputReplacesCaretLine() throws Exception
	{
		createAndOpenFile("replace_line_line_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.LINE, OutputType.REPLACE_LINE, "output");
		assertContents("Hello world!\noutput");
	}

	@Test
	public void testReplaceLineWithLeftCharInputReplacesCaretLine() throws Exception
	{
		createAndOpenFile("replace_line_lchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.LEFT_CHAR, OutputType.REPLACE_LINE, "output");
		assertContents("Hello world!\noutput");
	}

	@Test
	public void testReplaceLineWithRightCharInputReplacesCaretLine() throws Exception
	{
		createAndOpenFile("replace_line_rchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.RIGHT_CHAR, OutputType.REPLACE_LINE, "output");
		assertContents("Hello world!\noutput");
		// TODO Test replace line with inputs of left and right char when they hit line boundary!
	}

	@Test
	public void testReplaceLineWithSelectedLinesInputReplacesCaretLine() throws Exception
	{
		createAndOpenFile("replace_line_selected_lines_input.txt", "Hello world!\nSecond line!");
		select(6, 5);
		applyCommandResult(InputType.SELECTED_LINES, OutputType.REPLACE_LINE, "output");
		assertContents("output\nSecond line!");
	}

	@Test
	public void testReplaceLineWithSelectedLinesInputReplacesCaretLine2() throws Exception
	{
		createAndOpenFile("replace_line_selected_lines_input2.txt", "Hello world!\nSecond line!");
		select(6, 13);
		applyCommandResult(InputType.SELECTED_LINES, OutputType.REPLACE_LINE, "output");
		assertContents("Hello world!\noutput");
	}

	@Test
	public void testReplaceWordWithNoInput() throws Exception
	{
		createAndOpenFile("replace_word_no_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.NONE, OutputType.REPLACE_WORD, "output");
		assertContents("Hello world!\nSecond output!");
	}

	@Test
	public void testReplaceWordWithDocumentInput() throws Exception
	{
		createAndOpenFile("replace_word_doc_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.DOCUMENT, OutputType.REPLACE_WORD, "output");
		assertContents("Hello world!\nSecond output!");
	}

	@Test
	public void testReplaceWordWithWordInput() throws Exception
	{
		createAndOpenFile("replace_word_word_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.WORD, OutputType.REPLACE_WORD, "output");
		assertContents("Hello world!\nSecond output!");
	}

	@Test
	public void testReplaceWordWithLeftCharInput() throws Exception
	{
		createAndOpenFile("replace_word_lchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.LEFT_CHAR, OutputType.REPLACE_WORD, "output");
		assertContents("Hello world!\nSecond output!");
	}

	@Test
	public void testReplaceWordWithRightCharInput() throws Exception
	{
		createAndOpenFile("replace_word_rchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.RIGHT_CHAR, OutputType.REPLACE_WORD, "output");
		assertContents("Hello world!\nSecond output!");
	}

	@Test
	public void testReplaceWordWithSelectionInput() throws Exception
	{
		createAndOpenFile("replace_word_selection_input.txt", "Hello world!\nSecond line!");
		select(6, 5);
		applyCommandResult(InputType.SELECTION, OutputType.REPLACE_WORD, "output");
		assertContents("Hello output!\nSecond line!");
	}

	@Test
	public void testReplaceWordWithSelectedLinesInput() throws Exception
	{
		createAndOpenFile("replace_word_selected_lines_input.txt", "Hello world!\nSecond line!");
		select(6, 13);
		applyCommandResult(InputType.SELECTED_LINES, OutputType.REPLACE_WORD, "output");
		assertContents("Hello world!\noutput line!");
	}

	@Test
	public void testReplaceWordWithLineInput() throws Exception
	{
		createAndOpenFile("replace_word_line_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(4);
		applyCommandResult(InputType.LINE, OutputType.REPLACE_WORD, "output");
		assertContents("output world!\nSecond line!");
	}

	@Test
	public void testReplaceDocumentWithNoInput() throws Exception
	{
		createAndOpenFile("replace_doc_no_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.NONE, OutputType.REPLACE_DOCUMENT, "output");
		assertContents("output");
	}

	@Test
	public void testReplaceDocumentWithDocumentInput() throws Exception
	{
		createAndOpenFile("replace_doc_doc_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.DOCUMENT, OutputType.REPLACE_DOCUMENT, "output");
		assertContents("output");
	}

	@Test
	public void testReplaceDocumentWithWordInput() throws Exception
	{
		createAndOpenFile("replace_doc_word_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.WORD, OutputType.REPLACE_DOCUMENT, "output");
		assertContents("output");
	}

	@Test
	public void testReplaceDocumentWithLeftCharInput() throws Exception
	{
		createAndOpenFile("replace_doc_lchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.LEFT_CHAR, OutputType.REPLACE_DOCUMENT, "output");
		assertContents("output");
	}

	@Test
	public void testReplaceDocumentWithRightCharInput() throws Exception
	{
		createAndOpenFile("replace_doc_rchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.RIGHT_CHAR, OutputType.REPLACE_DOCUMENT, "output");
		assertContents("output");
	}

	@Test
	public void testReplaceDocumentWithSelectionInput() throws Exception
	{
		createAndOpenFile("replace_doc_selection_input.txt", "Hello world!\nSecond line!");
		select(6, 5);
		applyCommandResult(InputType.SELECTION, OutputType.REPLACE_DOCUMENT, "output");
		assertContents("output");
	}

	@Test
	public void testReplaceDocumentWithSelectedLinesInput() throws Exception
	{
		createAndOpenFile("replace_doc_selected_lines_input.txt", "Hello world!\nSecond line!");
		select(6, 13);
		applyCommandResult(InputType.SELECTED_LINES, OutputType.REPLACE_DOCUMENT, "output");
		assertContents("output");
	}

	@Test
	public void testReplaceDocumentWithLineInput() throws Exception
	{
		createAndOpenFile("replace_doc_line_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(4);
		applyCommandResult(InputType.LINE, OutputType.REPLACE_DOCUMENT, "output");
		assertContents("output");
	}

	@Test
	public void testReplaceSelectedLinesWithNoInput() throws Exception
	{
		createAndOpenFile("replace_selected_lines_no_input.txt", "Hello world!\nSecond line!");
		select(6, 13);
		applyCommandResult(InputType.NONE, OutputType.REPLACE_SELECTED_LINES, "output");
		assertContents("output");
	}

	@Test
	public void testReplaceSelectedLinesWithDocumentInput() throws Exception
	{
		createAndOpenFile("replace_selected_lines_doc_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.DOCUMENT, OutputType.REPLACE_SELECTED_LINES, "output");
		assertContents("Hello world!\noutput");
	}

	@Test
	public void testReplaceSelectedLinesWithWordInput() throws Exception
	{
		createAndOpenFile("replace_selected_lines_word_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.WORD, OutputType.REPLACE_SELECTED_LINES, "output");
		assertContents("Hello world!\noutput");
	}

	@Test
	public void testReplaceSelectedLinesWithLeftCharInput() throws Exception
	{
		createAndOpenFile("replace_selected_lines_lchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.LEFT_CHAR, OutputType.REPLACE_SELECTED_LINES, "output");
		assertContents("Hello world!\noutput");
	}

	@Test
	public void testReplaceSelectedLinesWithRightCharInput() throws Exception
	{
		createAndOpenFile("replace_selected_lines_rchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.RIGHT_CHAR, OutputType.REPLACE_SELECTED_LINES, "output");
		assertContents("Hello world!\noutput");
	}

	@Test
	public void testReplaceSelectedLinesWithSelectionInput() throws Exception
	{
		createAndOpenFile("replace_selected_lines_selection_input.txt", "Hello world!\nSecond line!");
		select(6, 5);
		applyCommandResult(InputType.SELECTION, OutputType.REPLACE_SELECTED_LINES, "output");
		assertContents("output\nSecond line!");
	}

	@Test
	public void testReplaceSelectedLinesWithSelectedLinesInput() throws Exception
	{
		createAndOpenFile("replace_selected_lines_selected_lines_input.txt", "Hello world!\nSecond line!");
		select(6, 13);
		applyCommandResult(InputType.SELECTED_LINES, OutputType.REPLACE_SELECTED_LINES, "output");
		assertContents("output");
	}

	@Test
	public void testReplaceSelectedLinesWithLineInput() throws Exception
	{
		createAndOpenFile("replace_selected_lines_line_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(4);
		applyCommandResult(InputType.LINE, OutputType.REPLACE_SELECTED_LINES, "output");
		assertContents("output\nSecond line!");
	}

	@Test
	public void testInsertAsSnippetWithNoInputInsertsAtCaret() throws Exception
	{
		createAndOpenFile("insert_as_snippet_no_input.txt", "Hello world!\nSecond line!");
		select(6, 13);
		applyCommandResult(InputType.NONE, OutputType.INSERT_AS_SNIPPET, "output");
		assertContents("Hello world!\nSecondoutput line!");
		// TODO Test tab stop variables/cursor position after
	}

	@Test
	public void testInsertAsSnippetWithDocumentInputReplacesDocumentWithSnippet() throws Exception
	{
		createAndOpenFile("insert_as_snippet_doc_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.DOCUMENT, OutputType.INSERT_AS_SNIPPET, "output");
		assertContents("output");
	}

	@Test
	public void testInsertAsSnippetWithWordInputReplacesWordWithSnippet() throws Exception
	{
		createAndOpenFile("insert_as_snippet_word_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.WORD, OutputType.INSERT_AS_SNIPPET, "output");
		assertContents("Hello world!\nSecond output!");
	}

	@Test
	public void testInsertAsSnippetWithLeftCharInputReplacesCharWithSnippet() throws Exception
	{
		createAndOpenFile("insert_as_snippet_lchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.LEFT_CHAR, OutputType.INSERT_AS_SNIPPET, "output");
		assertContents("Hello world!\nSecond outputine!");
	}

	@Test
	public void testInsertAsSnippetWithRightCharInputReplacesCharWithSnippet() throws Exception
	{
		createAndOpenFile("insert_as_snippet_rchar_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(21);
		applyCommandResult(InputType.RIGHT_CHAR, OutputType.INSERT_AS_SNIPPET, "output");
		assertContents("Hello world!\nSecond loutputne!");
	}

	@Test
	public void testInsertAsSnippetWithSelectionInputReplacesSelectionWithSnippet() throws Exception
	{
		createAndOpenFile("insert_as_snippet_selection_input.txt", "Hello world!\nSecond line!");
		select(6, 5);
		applyCommandResult(InputType.SELECTION, OutputType.INSERT_AS_SNIPPET, "output");
		assertContents("Hello output!\nSecond line!");
	}

	@Test
	public void testInsertAsSnippetWithSelectedLinesInputReplacesSelectedLinesWithSnippet() throws Exception
	{
		createAndOpenFile("insert_as_snippet_selected_lines_input.txt", "Hello world!\nSecond line!");
		select(6, 13);
		applyCommandResult(InputType.SELECTED_LINES, OutputType.INSERT_AS_SNIPPET, "output");
		assertContents("output");
	}

	@Test
	public void testInsertAsSnippetWithLineInputReplacesLineWithSnippet() throws Exception
	{
		createAndOpenFile("insert_as_snippet_line_input.txt", "Hello world!\nSecond line!");
		setCaretOffset(4);
		applyCommandResult(InputType.LINE, OutputType.INSERT_AS_SNIPPET, "output");
		assertContents("output\nSecond line!");
	}

	protected void applyCommandResult(InputType inputType, OutputType outputType, String output)
			throws PartInitException
	{
		CommandElement commandElement = new CommandElement("fake/path.rb");
		CommandResult commandResult = createCommandResult(commandElement, inputType, outputType, "output");
		CommandExecutionUtils.processCommandResult(commandElement, commandResult, getEditor());
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

	@Override
	protected String getProjectName()
	{
		return PROJECT_NAME;
	}

}
