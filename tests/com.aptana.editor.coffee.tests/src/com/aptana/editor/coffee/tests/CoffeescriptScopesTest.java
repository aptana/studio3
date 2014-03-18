/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.After;
import org.junit.Test;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.scripting.commands.TextEditorUtils;
import com.aptana.editor.epl.tests.EditorTestHelper;
import com.aptana.ui.util.UIUtils;

@SuppressWarnings({ "nls" })
public class CoffeescriptScopesTest
{

	private ITextEditor editor;
	private File file;

	@After
	public void tearDown() throws Exception
	{
		try
		{
			if (editor != null)
			{
				EditorTestHelper.closeEditor(editor);
			}
			if (file != null)
			{
				if (!file.delete())
				{
					file.deleteOnExit();
				}
			}
		}
		finally
		{
			this.file = null;
			this.editor = null;
		}
	}

	@Test
	public void testSomeBasicScopes() throws Exception
	{
		String source = "# Assignment:\n" + //
				"number   = 42\n" + //
				"opposite = true\n" + //
				"\n" + //
				"# Conditions:\n" + //
				"number = -42 if opposite\n" + //
				"\n" + //
				"# Functions:\n" + //
				"square = (x) -> x * x\n" + //
				"\n" + //
				"# Arrays:\n" + //
				"list = [1, 2, 3, 4, 5]\n";

		createFile(source);
		openEditor();

		// #
		// FIXME Handle giving special scope for the # punctuation!
		// assertScope("source.coffee comment.line.coffee punctuation.definition.comment.coffee", 0);
		assertScope("source.coffee comment.line.coffee", 1);
		// number
		assertScope("source.coffee variable.assignment.coffee", 14);
		assertScope("source.coffee variable.assignment.coffee", 19);
		// =
		assertScope("source.coffee keyword.operator.coffee", 23);
		// 42
		assertScope("source.coffee constant.numeric.coffee", 25);

		// opposite
		assertScope("source.coffee variable.assignment.coffee", 28);
		assertScope("source.coffee variable.assignment.coffee", 35);
		// =
		assertScope("source.coffee keyword.operator.coffee", 37);
		// true
		assertScope("source.coffee constant.language.boolean.true.coffee", 39);
		// ...
		// if
		assertScope("source.coffee keyword.control.coffee", 72);
		// ...
		// square
		assertScope("source.coffee meta.function.coffee entity.name.function.coffee", 98);
		assertScope("source.coffee meta.function.coffee entity.name.function.coffee", 103);
		// (
		assertScope("source.coffee meta.inline.function.coffee variable.parameter.function.coffee", 107);
		// x
		assertScope("source.coffee meta.inline.function.coffee variable.parameter.function.coffee", 108);
		// )
		assertScope("source.coffee meta.inline.function.coffee variable.parameter.function.coffee", 109);
		// ->
		assertScope("source.coffee meta.inline.function.coffee storage.type.function.coffee", 111);
		assertScope("source.coffee meta.inline.function.coffee storage.type.function.coffee", 112);
		// *
		assertScope("source.coffee keyword.operator.coffee", 116);
		// ...
		// [
		assertScope("source.coffee meta.brace.square.coffee", 138);
		// 1
		assertScope("source.coffee constant.numeric.coffee", 139);
		// ,
		assertScope("source.coffee meta.delimiter.object.comma.coffee", 140);
		// ...
		// ]
		assertScope("source.coffee meta.brace.square.coffee", 152);
	}

	@Test
	public void testClasses() throws Exception
	{
		String source = "class Animal\n" + //
				"  constructor: (@name) ->\n" + //
				"  \n" + //
				"  move: (meters) ->\n" + //
				"    alert @name + \" moved \" + meters + \"m.\"\n" + //
				"\n" + //
				"class Snake extends Animal\n" + //
				"  move: ->\n" + //
				"    alert \"Slithering...\"\n" + //
				"    super 5\n" + //
				"    \n" + //
				"class Horse extends Animal\n" + //
				"  move: ->\n" + //
				"    alert \"Galloping...\"\n" + //
				"    super 45\n" + //
				"    \n" + //
				"sam = new Snake \"Sammy the Python\"\n" + //
				"tom = new Horse \"Tommy the Palomino\"\n" + //
				"\n" + //
				"sam.move()\n" + //
				"tom.move()\n";

		createFile(source);
		openEditor();

		// class
		assertScope("source.coffee meta.class.coffee storage.type.class.coffee", 0);
		assertScope("source.coffee meta.class.coffee storage.type.class.coffee", 4);
		// Animal
		assertScope("source.coffee meta.class.coffee entity.name.type.class.coffee", 6);
		assertScope("source.coffee meta.class.coffee entity.name.type.class.coffee", 11);
		// constructor
		assertScope("source.coffee meta.function.coffee entity.name.function.coffee", 15);
		assertScope("source.coffee meta.function.coffee entity.name.function.coffee", 25);
		// :
		assertScope("source.coffee keyword.operator.coffee", 26);
		// (
		assertScope("source.coffee meta.inline.function.coffee variable.parameter.function.coffee", 28);
		// @name
		assertScope("source.coffee meta.inline.function.coffee variable.other.readwrite.instance.coffee", 29);
		assertScope("source.coffee meta.inline.function.coffee variable.other.readwrite.instance.coffee", 33);
		// )
		assertScope("source.coffee meta.inline.function.coffee variable.parameter.function.coffee", 34);
		// ->
		assertScope("source.coffee meta.inline.function.coffee storage.type.function.coffee", 36);
		assertScope("source.coffee meta.inline.function.coffee storage.type.function.coffee", 37);
		// move
		assertScope("source.coffee meta.function.coffee entity.name.function.coffee", 44);
		assertScope("source.coffee meta.function.coffee entity.name.function.coffee", 47);
		// :
		assertScope("source.coffee keyword.operator.coffee", 48);
		// (
		assertScope("source.coffee meta.inline.function.coffee variable.parameter.function.coffee", 50);
		// meters
		assertScope("source.coffee meta.inline.function.coffee variable.parameter.function.coffee", 51);
		assertScope("source.coffee meta.inline.function.coffee variable.parameter.function.coffee", 56);
		// )
		assertScope("source.coffee meta.inline.function.coffee variable.parameter.function.coffee", 57);
		// ->
		assertScope("source.coffee meta.inline.function.coffee storage.type.function.coffee", 59);
		assertScope("source.coffee meta.inline.function.coffee storage.type.function.coffee", 60);

		// alert @name + " moved " + meters + "m."\n

		// alert
		assertScope("source.coffee", 66);
		assertScope("source.coffee", 70);
		// @name
		assertScope("source.coffee variable.other.readwrite.instance.coffee", 72);
		assertScope("source.coffee variable.other.readwrite.instance.coffee", 76);
		// +
		assertScope("source.coffee keyword.operator.coffee", 78);
		// "
		assertScope("source.coffee string.quoted.double.coffee", 80);
		// moved
		assertScope("source.coffee string.quoted.double.coffee", 82);
		// "
		assertScope("source.coffee string.quoted.double.coffee", 88);
		// +
		assertScope("source.coffee keyword.operator.coffee", 90);
		// meters
		assertScope("source.coffee", 92);
		assertScope("source.coffee", 97);
		// +
		assertScope("source.coffee keyword.operator.coffee", 99);
		// "m."
		assertScope("source.coffee string.quoted.double.coffee", 101);
		assertScope("source.coffee string.quoted.double.coffee", 104);

		// class Snake extends Animal

		// class
		assertScope("source.coffee meta.class.coffee storage.type.class.coffee", 107);
		assertScope("source.coffee meta.class.coffee storage.type.class.coffee", 111);
		// Snake
		assertScope("source.coffee meta.class.coffee entity.name.type.class.coffee", 113);
		assertScope("source.coffee meta.class.coffee entity.name.type.class.coffee", 117);
		// extends
		assertScope("source.coffee meta.class.coffee keyword.control.inheritance.coffee", 119);
		assertScope("source.coffee meta.class.coffee keyword.control.inheritance.coffee", 125);
		// Animal
		assertScope("source.coffee meta.class.coffee entity.other.inherited-class.coffee", 127);
		assertScope("source.coffee meta.class.coffee entity.other.inherited-class.coffee", 132);
	}

	protected void assertScope(String scope, int offset) throws BadLocationException
	{
		char c = TextEditorUtils.getSourceViewer(editor).getDocument().getChar(offset);
		assertEquals("Scope doesn't match for char '" + c + "'", scope, getScopeAtOffset(offset));
	}

	protected void openEditor() throws PartInitException
	{
		IEditorPart part = IDE.openEditorOnFileStore(UIUtils.getActivePage(),
				EFS.getLocalFileSystem().fromLocalFile(file));
		editor = (ITextEditor) part;
	}

	protected void createFile(String contents) throws IOException
	{
		file = File.createTempFile("testing", ".coffee");
		FileWriter writer = new FileWriter(file);
		writer.write(contents);
		writer.close();
	}

	protected String getScopeAtOffset(int offset) throws BadLocationException
	{
		return getScopeAtOffset(TextEditorUtils.getSourceViewer(editor), offset);
	}

	protected String getScopeAtOffset(ISourceViewer viewer, int offset) throws BadLocationException
	{
		return CommonEditorPlugin.getDefault().getDocumentScopeManager().getScopeAtOffset(viewer, offset);
	}
}
