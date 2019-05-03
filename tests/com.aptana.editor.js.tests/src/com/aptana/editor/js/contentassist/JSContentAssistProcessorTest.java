package com.aptana.editor.js.contentassist;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.buildpath.core.BuildPathManager;
import com.aptana.buildpath.core.IBuildPathEntry;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.editor.common.tests.EditorContentAssistBasedTests;
import com.aptana.editor.common.tests.TextViewer;
import com.aptana.editor.js.tests.JSEditorBasedTestCase;
import com.aptana.index.core.FileStoreBuildContext;
import com.aptana.index.core.Index;
import com.aptana.js.core.JSTypeConstants;
import com.aptana.js.core.index.JSCAFileIndexingParticipant;
import com.aptana.js.core.index.JSIndexQueryHelper;
import com.aptana.js.core.model.FunctionElement;
import com.aptana.js.core.model.ParameterElement;

public class JSContentAssistProcessorTest extends JSEditorBasedTestCase
{

	protected boolean LESS_THAN_3_4_SDK = false;
	private JSContentAssistProcessor processor;
	private IDocument document;
	private Mockery context;
	private JSIndexQueryHelper helper;

	@Before
	public void setUp() throws Exception
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		helper = context.mock(JSIndexQueryHelper.class);
		LESS_THAN_3_4_SDK = false;
		processor = new JSContentAssistProcessor(null)
		{
			@Override
			protected IDocument getDocument()
			{
				return document;
			}

			@Override
			protected String getFilename()
			{
				return "something.js";
			}

			@Override
			protected JSIndexQueryHelper getQueryHelper()
			{
				return helper;
			}

			@Override
			protected boolean hasSDKLessThanOrEqualToVersion(IProject project, String sdk)
			{
				return LESS_THAN_3_4_SDK;
			}
		};
	}

	@After
	public void tearDown() throws Exception
	{
		if (context != null)
		{
			context.assertIsSatisfied();
			context = null;
		}
		processor = null;
		document = null;
		helper = null;
	}

	@Test
	public void testSuggestsConstantsForFunctionArgumentsWithThemDefined()
	{
		int offset = 4; // between parens
		document = new Document("foo();");

		ParameterElement param = new ParameterElement();
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("constants", CollectionsUtil.newList("BAR"));
		param.fromJSON(json);
		param.setName("param1");

		final FunctionElement function = new FunctionElement();
		function.setName("foo");
		function.addParameter(param);

		context.checking(new Expectations()
		{
			{
				oneOf(helper).findFunctionInHierarchy(JSTypeConstants.WINDOW_TYPE, "foo");
				will(returnValue(function));

				allowing(helper).getGlobals("something.js");
				will(returnValue(Collections.emptyList()));
			}
		});

		ICompletionProposal[] proposals = processor.computeCompletionProposals(new TextViewer(document), offset, '\t',
				false);
		EditorContentAssistBasedTests.assertContains(proposals, "BAR");
	}

	private Index indexResource(String resource) throws CoreException
	{
		IFileStore fileToIndex = getFileStore(resource);
		URI uri = fileToIndex.toURI();
		Index index = getIndexManager().getIndex(uri);
		JSCAFileIndexingParticipant indexer = new JSCAFileIndexingParticipant();

		indexer.index(new FileStoreBuildContext(fileToIndex), index, new NullProgressMonitor());

		return index;
	}

	@Test
	public void test3_3SDKTiUIMethods() throws Exception
	{
		LESS_THAN_3_4_SDK = true;

		Index index = indexResource("metadata/sdk_3_3_api.jsca");
		helper = new JSIndexQueryHelper(index);
		Set<ICompletionProposal> proposals = new HashSet<ICompletionProposal>();

		// check for Ti.UI
		processor.addTypeProperties(proposals, "Titanium.UI", 10, false);
		ICompletionProposal[] actualProposals = proposals.toArray(new ICompletionProposal[proposals.size()]);
		assertContains(actualProposals, "createButton", "backgroundColor", "KEYBOARD_ASCII", "addEventListener",
				"getCurrentTab");
	}

	@Test
	public void testProjectHas3_4_0SDK() throws Exception
	{
		final BuildPathManager buildPathManager = context.mock(BuildPathManager.class);
		final IProject project = context.mock(IProject.class);
		final IBuildPathEntry projMetadata = context.mock(IBuildPathEntry.class, "projectMetadata");
		final IBuildPathEntry sdkJsca = context.mock(IBuildPathEntry.class, "sdkApiJSCA");

		processor = new JSContentAssistProcessor(null)
		{
			@Override
			protected BuildPathManager getBuildPathManager()
			{
				return buildPathManager;
			}
		};
		context.checking(new Expectations()
		{
			{
				oneOf(buildPathManager).getBuildPaths(project);
				will(returnValue(CollectionsUtil.newSet(projMetadata, sdkJsca)));

				allowing(projMetadata).getPath();
				will(returnValue(new URI("/path/to/project")));

				allowing(sdkJsca).getPath();
				will(returnValue(new URI("/path/to/sdk/mobilesdk/linux/3.4.0.GA/api.jsca")));
			}
		});
		assertTrue(processor.hasSDKLessThanOrEqualToVersion(project, "3.4.0.GA"));
	}

	@Test
	public void testProjectHas3_4_1SDK() throws Exception
	{
		final BuildPathManager buildPathManager = context.mock(BuildPathManager.class);
		final IProject project = context.mock(IProject.class);
		final IBuildPathEntry projMetadata = context.mock(IBuildPathEntry.class, "projectMetadata");
		final IBuildPathEntry sdkJsca = context.mock(IBuildPathEntry.class, "sdkApiJSCA");

		processor = new JSContentAssistProcessor(null)
		{
			@Override
			protected BuildPathManager getBuildPathManager()
			{
				return buildPathManager;
			}
		};
		context.checking(new Expectations()
		{
			{
				oneOf(buildPathManager).getBuildPaths(project);
				will(returnValue(CollectionsUtil.newSet(projMetadata, sdkJsca)));

				allowing(projMetadata).getPath();
				will(returnValue(new URI("/path/to/project")));

				allowing(sdkJsca).getPath();
				will(returnValue(new URI("/path/to/sdk/mobilesdk/linux/3.4.1.GA/api.jsca")));
			}
		});
		assertFalse(processor.hasSDKLessThanOrEqualToVersion(project, "3.4.0.GA"));
	}

	@Test
	public void test3_4SDKTiUIMethods() throws Exception
	{
		LESS_THAN_3_4_SDK = false;

		Index index = indexResource("metadata/sdk_3_4_1_api.jsca");
		helper = new JSIndexQueryHelper(index);
		Set<ICompletionProposal> proposals = new HashSet<ICompletionProposal>();

		// check for Ti.UI
		processor.addTypeProperties(proposals, "Titanium.UI", 10, false);
		ICompletionProposal[] actualProposals = proposals.toArray(new ICompletionProposal[proposals.size()]);
		assertContains(actualProposals, "createButton", "backgroundColor", "KEYBOARD_ASCII", "addEventListener",
				"getCurrentTab");
	}
}
