package com.aptana.editor.common.text.rules;

import java.net.URI;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.ide.IDE;
import org.junit.experimental.categories.Category;

import com.aptana.core.tests.GlobalTimePerformanceTestCase;
import com.aptana.core.util.ResourceUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.epl.tests.EditorTestHelper;
import com.aptana.testing.categories.PerformanceTests;
import com.aptana.ui.util.UIUtils;

@Category({ PerformanceTests.class })
public class ThemeingDamagerRepairerPerfTest extends GlobalTimePerformanceTestCase
{
	private AbstractThemeableEditor editor;

	@Override
	protected void tearDown() throws Exception
	{
		if (editor != null)
		{
			EditorTestHelper.closeEditor(editor);
			editor = null;
		}
		super.tearDown();
	}

	public void testForcedRecoloringOfEntireFile() throws Exception
	{
		URL url = FileLocator.find(Platform.getBundle("com.aptana.editor.common.tests"),
				Path.fromPortableString("performance/ext-all-debug.js"), null);
		URI uri = ResourceUtil.resourcePathToURI(url);
		editor = (AbstractThemeableEditor) IDE.openEditor(UIUtils.getActivePage(), uri, "com.aptana.editor.js", true);
		ISourceViewer fViewer = editor.getISourceViewer();
		for (int i = 0; i < 2000; i++)
		{
			startMeasuring();
			fViewer.invalidateTextPresentation();
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}

	// Add a test for edits in the file to make sure edits are as fast/faster...
	public void testEditingFileLive() throws Exception
	{
		URL url = FileLocator.find(Platform.getBundle("com.aptana.editor.common.tests"),
				Path.fromPortableString("performance/ext-all-debug.js"), null);
		URI uri = ResourceUtil.resourcePathToURI(url);
		editor = (AbstractThemeableEditor) IDE.openEditor(UIUtils.getActivePage(), uri, "com.aptana.editor.js", true);
		ISourceViewer fViewer = editor.getISourceViewer();
		IDocument document = fViewer.getDocument();
		for (int i = 0; i < 6000; i++)
		{
			startMeasuring();
			document.replace(756, 1, "");
			document.replace(756, 0, "o");
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}

}
