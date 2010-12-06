/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting;

import org.eclipse.ui.PartInitException;

import com.aptana.editor.common.scripting.commands.CommandExecutionUtils;
import com.aptana.editor.common.tests.SingleEditorTestCase;
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
public class ScriptingInputOutputTest extends SingleEditorTestCase
{

	private static final String PROJECT_NAME = "scripting_io";

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
