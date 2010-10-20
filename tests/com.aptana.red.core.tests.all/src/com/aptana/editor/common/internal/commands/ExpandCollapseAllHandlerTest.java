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
package com.aptana.editor.common.internal.commands;

import com.aptana.editor.common.tests.SingleEditorTestCase;
import com.aptana.editor.html.Activator;

public class ExpandCollapseAllHandlerTest extends SingleEditorTestCase
{

//	private static final String EXPAND_ALL_COMMAND_ID = "com.aptana.editor.commands.ExpandAll";
//	private static final String COLLAPSE_ALL_COMMAND_ID = "com.aptana.editor.commands.CollapseAll";
	private static final String PROJECT_NAME = "expand_collapse";

	@Override
	protected void setUp() throws Exception
	{
		Activator.getDefault();
		super.setUp();
	}

	@Override
	protected String getProjectName()
	{
		return PROJECT_NAME;
	}

	public void testExecute() throws Exception
	{
		// FIXME This assumes that the bundles are loaded and the folding is set up
		/*
		// Create an open an HTML file
		createAndOpenFile("example" + System.currentTimeMillis() + ".html",
				"<html>\n<head>\n<title>Title goes here</title>\n</head>\n<body>\n<div>\n<p>Hi</p>\n</div>\n</body>");
		// Open the Outline view
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		IViewPart outline = page.showView(IPageLayout.ID_OUTLINE);

		// Grab tree viewer for outline contents
		IPage curPage = ((ContentOutline) outline).getCurrentPage();
		CommonOutlinePage outlinePage = (CommonOutlinePage) curPage;
		Method m = ContentOutlinePage.class.getDeclaredMethod("getTreeViewer");
		m.setAccessible(true);
		TreeViewer treeViewer = (TreeViewer) m.invoke(outlinePage);
		Thread.sleep(500);

		// Grab the handler service to execute our command
		IHandlerService service = (IHandlerService) outline.getSite().getService(IHandlerService.class);
		service.executeCommand(EXPAND_ALL_COMMAND_ID, null);

		// check expansion state, should be expanded
		Object[] expanded = treeViewer.getExpandedElements();
		assertEquals(4, expanded.length); // html, head, body, div

		// toggle expansion
		service.executeCommand(COLLAPSE_ALL_COMMAND_ID, null);

		// check expansion state
		expanded = treeViewer.getExpandedElements();
		assertEquals(0, expanded.length); // collapsed

		// toggle expansion
		service.executeCommand(EXPAND_ALL_COMMAND_ID, null);

		// check expansion state
		expanded = treeViewer.getExpandedElements();
		assertEquals(4, expanded.length); // html, head, body, div
		*/
	}

}
