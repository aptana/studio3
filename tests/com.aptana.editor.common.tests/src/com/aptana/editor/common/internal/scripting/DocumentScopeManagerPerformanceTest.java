package com.aptana.editor.common.internal.scripting;

import java.io.File;
import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.experimental.categories.Category;

import com.aptana.core.tests.GlobalTimePerformanceTestCase;
import com.aptana.core.util.ResourceUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.scripting.IDocumentScopeManager;
import com.aptana.editor.common.scripting.commands.TextEditorUtils;
import com.aptana.editor.epl.tests.EditorTestHelper;
import com.aptana.testing.categories.PerformanceTests;
import com.aptana.ui.util.UIUtils;

@Category({ PerformanceTests.class })
public class DocumentScopeManagerPerformanceTest extends GlobalTimePerformanceTestCase
{

	/**
	 * This version will return token level Scopes.
	 * 
	 * @throws Exception
	 */
	public void testGetScopeAtOffsetSourceViewer() throws Exception
	{
		IDocumentScopeManager manager = CommonEditorPlugin.getDefault().getDocumentScopeManager();
		ITextEditor editor = null;
		File file = null;
		try
		{
			IWorkbenchPage page = UIUtils.getActivePage();
			// Open a really big file, like DOJO!
			URL url = FileLocator.find(Platform.getBundle("com.aptana.js.core.tests"),
					Path.fromPortableString("performance/dojo.js.uncompressed.js"), null);
			file = ResourceUtil.resourcePathToFile(url);

			editor = (ITextEditor) IDE.openEditorOnFileStore(page, EFS.getLocalFileSystem().fromLocalFile(file));
			ISourceViewer viewer = TextEditorUtils.getSourceViewer(editor);
			IDocument doc = viewer.getDocument();
			int length = doc.getLength();
			EditorTestHelper.closeAllEditors();

			IEditorReference[] refs = page.getEditorReferences();
			assertEquals(0, refs.length);

			for (int i = 0; i < 280; i++)
			{
				startMeasuring();
				editor = (ITextEditor) IDE.openEditorOnFileStore(page, EFS.getLocalFileSystem().fromLocalFile(file));
				viewer = TextEditorUtils.getSourceViewer(editor);
				for (int x = 0; x < length; x += 100)
				{
					manager.getScopeAtOffset(viewer, x);
				}
				stopMeasuring();
				EditorTestHelper.closeEditor(editor);
				refs = page.getEditorReferences();
				assertEquals(0, refs.length);
			}
			commitMeasurements();
			assertPerformance();
		}
		finally
		{
			if (editor != null)
			{
				EditorTestHelper.closeEditor(editor);
			}
		}
	}
}
