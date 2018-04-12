/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.FileEditorInput;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.aptana.core.util.FileUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.tests.util.AssertUtil;
import com.aptana.editor.html.HTMLMetadataLoader;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.HTMLTestUtil;
import com.aptana.editor.html.core.preferences.IPreferenceConstants;
import com.aptana.editor.html.tests.HTMLEditorBasedTests;
import com.aptana.projects.WebProjectNature;
import com.aptana.testing.categories.IntegrationTests;
import com.aptana.testing.utils.TestProject;
import com.aptana.webserver.core.SimpleWebServer;
import com.aptana.webserver.core.WebServerCorePlugin;

@Category({ IntegrationTests.class })
public class HTMLContentAssistProcessorIntegrationTest extends HTMLEditorBasedTests
{

	private HTMLContentAssistProcessor fProcessor;
	private IDocument fDocument;

	@BeforeClass
	public static void loadMetadata() throws Exception
	{
		HTMLMetadataLoader loader = new HTMLMetadataLoader();
		loader.schedule();
		loader.join();
	}

	@Before
	public void setUp() throws Exception
	{
		fProcessor = new HTMLContentAssistProcessor(null);
	}

	@Override
	public void tearDown() throws Exception
	{
		HTMLPlugin.getDefault().getPreferenceStore().setValue(IPreferenceConstants.HTML_REMOTE_HREF_PROPOSALS, true);
		fProcessor = null;
		fDocument = null;

		super.tearDown();
	}

	@Test
	public void testLinkHREFFolderProposal() throws Exception
	{
		assertHREFProposal("<link rel=\"stylesheet\" href=\"|\" />", "<link rel=\"stylesheet\" href=\"folder\" />",
				"folder");
	}

	@Test
	public void testLinkHREFFileProposal() throws Exception
	{
		assertHREFProposal("<link rel='stylesheet' href='|' />", "<link rel='stylesheet' href='root.css' />",
				"root.css");

	}

	@Test
	public void testLinkHREFFileUnderFolderProposal() throws Exception
	{
		assertHREFProposal("<link rel='stylesheet' href='folder/|' />",
				"<link rel='stylesheet' href='folder/inside_folder.css' />", "inside_folder.css");

	}

	@Test
	public void testLinkHREFFileProposalWithPrefix() throws Exception
	{
		assertHREFProposal("<link rel='stylesheet' href='roo|' />", "<link rel='stylesheet' href='root.css' />",
				"root.css");

	}

	@Test
	public void testLinkHREFFolderProposalWithPrefix() throws Exception
	{
		assertHREFProposal("<link rel='stylesheet' href='fo|' />", "<link rel='stylesheet' href='folder' />", "folder");

	}

	@Test
	public void testLinkHREFFileInsideFolderProposalWithPrefix() throws Exception
	{
		assertHREFProposal("<link rel='stylesheet' href='folder/in|' />",
				"<link rel='stylesheet' href='folder/inside_folder.css' />", "inside_folder.css");
	}

	@Test
	public void testApstud2959() throws Exception
	{
		String document = "<a href='http://|' />";
		int offset = HTMLTestUtil.findCursorOffset(document);
		fDocument = HTMLTestUtil.createDocument(document, true);
		ITextViewer viewer = createTextViewer(fDocument);

		TestProject project = new TestProject("Apstud2959", new String[] { WebProjectNature.ID });
		final IFile file = project.createFile("testApstud2959", "");
		project.createFolder("email");

		fProcessor = new HTMLContentAssistProcessor(null)
		{
			@Override
			protected URI getURI()
			{
				return file.getRawLocationURI();
			}
		};

		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, '\t', false);
		assertEquals(0, proposals.length);

		project.delete();
	}

	@Test
	public void testRootFileSystemForHREF() throws Exception
	{
		String document = "<a href='file://|' />";
		int offset = HTMLTestUtil.findCursorOffset(document);
		fDocument = HTMLTestUtil.createDocument(document, true);
		ITextViewer viewer = createTextViewer(fDocument);

		IFileStore root = EFS.getLocalFileSystem().getStore(Path.ROOT);
		String[] names = root.childNames(EFS.NONE, null);
		int count = 0;
		for (String name : names)
		{
			if (name.startsWith("."))
			{
				continue;
			}
			count++;
		}
		fProcessor = new HTMLContentAssistProcessor(null)
		{
			@Override
			protected URI getURI()
			{
				// Shouldn't be necessary...
				return new File(FileUtil.getTempDirectory().toOSString(), "file.html").toURI();
			}
		};

		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, '\t', false);
		assertEquals(count, proposals.length);

	}

	@Test
	public void testRootFileSystemForHREFBadPrefix() throws Exception
	{
		// Busted scheme
		String document = "<a href='file:/|' />";
		int offset = HTMLTestUtil.findCursorOffset(document);
		fDocument = HTMLTestUtil.createDocument(document, true);
		ITextViewer viewer = createTextViewer(fDocument);

		fProcessor = new HTMLContentAssistProcessor(null)
		{
			@Override
			protected URI getURI()
			{
				// Shouldn't be necessary...
				return new File(FileUtil.getTempDirectory().toOSString(), "file.html").toURI();
			}
		};

		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, '\t', false);
		assertEquals(0, proposals.length);

	}

	@Test
	public void testServerHREF() throws Exception
	{
		// Generate some folders/files to use as proposals
		TestProject project = createWebProject("href_file_proposal");
		final IFile file = project.createFile("test.html", "<link rel='stylesheet' href='/|' />");
		this.setupTestContext(file);

		SimpleWebServer server = new SimpleWebServer();
		server.setDocumentRoot(project.getURI());
		server.setBaseURL(new URL("http://www.test.com/"));
		WebServerCorePlugin.getDefault().getServerManager().add(server);

		int offset = this.cursorOffsets.get(0);
		ITextViewer viewer = AssertUtil.createTextViewer(document);
		ICompletionProposal[] proposals = processor.doComputeCompletionProposals(viewer, offset, '\t', false);

		AssertUtil.assertProposalFound("file.html", proposals);
		AssertUtil.assertProposalFound("root.css", proposals);
		AssertUtil.assertProposalFound("folder", proposals);
		AssertUtil.assertProposalApplies("<link rel='stylesheet' href='/folder' />", document, "folder", proposals,
				offset, null);

		WebServerCorePlugin.getDefault().getServerManager().remove(server);
		project.delete();

	}

	@Test
	public void testRailsServerHREF() throws Exception
	{
		// Generate some folders/files to use as proposals
		TestProject project = createWebProject("href_file_proposal");
		project.createFolder("public");
		project.createFile("public/railsfile.html", "");
		final IFile file = project.createFile("test.html", "<link rel='stylesheet' href='/|' />");
		this.setupTestContext(file);

		int offset = this.cursorOffsets.get(0);
		ITextViewer viewer = AssertUtil.createTextViewer(document);
		ICompletionProposal[] proposals = processor.doComputeCompletionProposals(viewer, offset, '\t', false);

		AssertUtil.assertProposalFound("railsfile.html", proposals);
		AssertUtil.assertProposalApplies("<link rel='stylesheet' href='/railsfile.html' />", document, "railsfile.html",
				proposals, offset, null);

		project.delete();

	}

	@Test
	public void testRootFileSystemForHREFWithFolderPrefix() throws Exception
	{
		// Generate some folders/files to use as proposals
		TestProject project = new TestProject("href_file_proposal", new String[] { WebProjectNature.ID });
		final IFolder folder = project.createFolder("folder");

		String fileUri = folder.getRawLocationURI().toString();
		fileUri = fileUri.replace("file:/", "file://");
		String document = "<link rel='stylesheet' href='" + fileUri + "/|' />";
		int offset = HTMLTestUtil.findCursorOffset(document);
		fDocument = HTMLTestUtil.createDocument(document, true);
		ITextViewer viewer = createTextViewer(fDocument);

		project.createFile("folder/inside_folder.css", "");

		final IFile file = project.createFile("file.html", "");

		fProcessor = new HTMLContentAssistProcessor(null)
		{
			@Override
			protected URI getURI()
			{
				return file.getRawLocationURI();
			}
		};

		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, '\t', false);
		assertEquals(1, proposals.length);
		AssertUtil.assertProposalFound("inside_folder.css", proposals);
		AssertUtil.assertProposalApplies("<link rel='stylesheet' href='" + fileUri + "/inside_folder.css' />",
				fDocument, "inside_folder.css", proposals, offset, null);

		project.delete();
	}

	@Test
	public void testRootFileSystemForHREFWithFolderAndFilePrefix() throws Exception
	{
		// Generate some folders/files to use as proposals
		TestProject project = new TestProject("href_file_proposal", new String[] { WebProjectNature.ID });
		final IFolder folder = project.createFolder("folder");

		String fileUri = folder.getLocationURI().toString();
		fileUri = fileUri.replace("file:/", "file://");
		String document = "<link rel='stylesheet' href='" + fileUri + "/inside|' />";
		int offset = HTMLTestUtil.findCursorOffset(document);
		fDocument = HTMLTestUtil.createDocument(document, true);
		ITextViewer viewer = createTextViewer(fDocument);

		project.createFile("folder/inside_folder.css", "");

		final IFile file = project.createFile("file.html", "");

		fProcessor = new HTMLContentAssistProcessor(null)
		{
			@Override
			protected URI getURI()
			{
				return file.getLocationURI();
			}
		};

		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, '\t', false);
		assertEquals(1, proposals.length);

		AssertUtil.assertProposalFound("inside_folder.css", proposals);
		AssertUtil.assertProposalApplies("<link rel='stylesheet' href='" + fileUri + "/inside_folder.css' />",
				fDocument, "inside_folder.css", proposals, offset, null);

		project.delete();

	}

	@Test
	public void testNoProposalsForHTTPURIPath() throws Exception
	{
		TestProject project = null;
		try
		{
			// The "project" the file we're working on sits in.
			project = new TestProject("href_http_doesnt_fetch", new String[] { WebProjectNature.ID });
			final IFile file = project.createFile("file.html", "");

			// Set up document/file
			String document = "<link rel='stylesheet' href='http://www.aptana.com/|' />";
			int offset = HTMLTestUtil.findCursorOffset(document);
			fDocument = HTMLTestUtil.createDocument(document, true);
			ITextViewer viewer = createTextViewer(fDocument);
			fProcessor = new HTMLContentAssistProcessor(null)
			{
				@Override
				protected URI getURI()
				{
					return file.getLocationURI();
				}

				@Override
				protected boolean efsFileSystemCanGrabChildren(String scheme)
				{
					// Verify that we can't grab children as a means of making sure we don't call fetchInfo or hit
					// remote URI.
					boolean result = super.efsFileSystemCanGrabChildren(scheme);
					assertFalse(result);
					return result;
				}

				@Override
				protected List<ICompletionProposal> suggestChildrenOfFileStore(int offset, String valuePrefix,
						URI editorStoreURI, IFileStore parent) throws CoreException
				{
					fail("We asked for children of an HTTP URI!");
					return super.suggestChildrenOfFileStore(offset, valuePrefix, editorStoreURI, parent);
				}
			};

			// FIXME do a more solid check that we don't call fetchInfo on the filestore. We're hacking around it with
			// our assertion in efsFileSystemCanGrabChildren
			ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, '\t', false);
			assertEquals(0, proposals.length);
		}
		finally
		{
			if (project != null)
			{
				project.delete();
			}
		}
	}

	@Test
	public void testDontHitRemoteURIForChildrenIfPrefIsTurnedOff() throws Exception
	{
		TestProject project = null;
		try
		{
			// The "project" the file we're working on sits in.
			project = new TestProject("href_dont_hit_remote", new String[] { WebProjectNature.ID });
			final IFile file = project.createFile("file.html", "");

			// Set up document/file
			String document = "<link rel='stylesheet' href='ftp://ftp.cs.brown.edu/|' />";
			int offset = HTMLTestUtil.findCursorOffset(document);
			fDocument = HTMLTestUtil.createDocument(document, true);
			ITextViewer viewer = createTextViewer(fDocument);
			fProcessor = new HTMLContentAssistProcessor(null)
			{
				@Override
				protected URI getURI()
				{
					return file.getLocationURI();
				}

				@Override
				protected List<ICompletionProposal> suggestChildrenOfFileStore(int offset, String valuePrefix,
						URI editorStoreURI, IFileStore parent) throws CoreException
				{
					fail("We asked for children of a remote URI when we had that preference turned off!");
					return super.suggestChildrenOfFileStore(offset, valuePrefix, editorStoreURI, parent);
				}
			};

			// Turn off hitting remote
			HTMLPlugin.getDefault().getPreferenceStore().setValue(IPreferenceConstants.HTML_REMOTE_HREF_PROPOSALS,
					false);

			ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, '\t', false);
			assertEquals(0, proposals.length);
		}
		finally
		{
			if (project != null)
			{
				project.delete();
			}
		}
	}

	@Test
	public void testNoProposalsForURIEndingInColonSlash() throws Exception
	{
		TestProject project = null;
		try
		{
			// The "project" the file we're working on sits in.
			project = new TestProject("href_http_doesnt_fetch", new String[] { WebProjectNature.ID });
			final IFile file = project.createFile("file.html", "");
			project.createFolder("folder");

			// Set up document/file
			String document = "<link rel='stylesheet' href='file:/|' />";
			int offset = HTMLTestUtil.findCursorOffset(document);
			fDocument = HTMLTestUtil.createDocument(document, true);
			ITextViewer viewer = createTextViewer(fDocument);
			fProcessor = new HTMLContentAssistProcessor(null)
			{
				@Override
				protected URI getURI()
				{
					return file.getLocationURI();
				}
			};

			ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, '\t', false);
			assertEquals(0, proposals.length);
		}
		finally
		{
			if (project != null)
			{
				project.delete();
			}
		}
	}

	private void assertHREFProposal(String source, String expected, String proposalToChoose) throws Exception
	{
		// Generate some folders/files to use as proposals
		TestProject project = createWebProject("href_file_proposal");
		final IResource file = project.createFile("test.html", source);
		IFileStore fileStore = EFS.getStore(file.getRawLocationURI());
		this.setupTestContext(fileStore);

		int offset = this.cursorOffsets.get(0);
		ITextViewer viewer = AssertUtil.createTextViewer(document);
		ICompletionProposal[] proposals = processor.doComputeCompletionProposals(viewer, offset, '\t', false);

		AssertUtil.assertProposalFound(proposalToChoose, proposals);
		AssertUtil.assertProposalApplies(expected, document, proposalToChoose, proposals, offset, null);

		project.delete();
	}

	@Test
	public void testRelativeHREFFileProposals() throws Exception
	{
		// FIXME I need to set up files on the filesystem and relative to editor to test those!

		String document = "<a href=\"|\"></a>";
		int offset = HTMLTestUtil.findCursorOffset(document);
		fDocument = HTMLTestUtil.createDocument(document, true);
		ITextViewer viewer = createTextViewer(fDocument);

		final File tmpFile = File.createTempFile("test", ".html");
		tmpFile.deleteOnExit();

		File sibling = new File(tmpFile.getParentFile(), "sibling.html");
		sibling.createNewFile();
		sibling.deleteOnExit();

		fProcessor = new HTMLContentAssistProcessor(null)
		{
			@Override
			protected URI getURI()
			{
				return tmpFile.toURI();
			}
		};

		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, '\t', false);
		assertTrue(proposals.length > 0);
		AssertUtil.assertProposalFound("sibling.html", proposals);
	}

	@Test
	public void testIsValidAutoActivationLocationElement()
	{

		// need to close previous editor
		String source = "<a|>";

		IFileStore fileStore = createFileStore("proposal_tests", "html", source);
		this.setupTestContext(fileStore);
		int offset = this.cursorOffsets.get(0);

		// starting to type an attribute
		assertTrue(processor.isValidAutoActivationLocation(' ', ' ', document, offset));
		assertTrue(processor.isValidAutoActivationLocation('\t', '\t', document, offset));
		assertFalse(processor.isValidAutoActivationLocation('b', 'b', document, offset));
	}

	@Test
	public void testIsValidAutoActivationLocationAttribute()
	{
		String source = "<a |>";
		IFileStore fileStore = createFileStore("proposal_tests", "html", source);
		this.setupTestContext(fileStore);
		int offset = this.cursorOffsets.get(0);

		// starting to type an attribute
		assertTrue(processor.isValidAutoActivationLocation('b', 'b', document, offset));

	}

	@Test
	public void testIsValidAutoActivationLocationAttributeValue()
	{
		String source = "<a class=\"|\"|>";
		IFileStore fileStore = createFileStore("proposal_tests", "html", source);
		this.setupTestContext(fileStore);
		int offset = this.cursorOffsets.get(0);

		// starting to type an attribute value
		assertTrue(processor.isValidAutoActivationLocation('f', 'f', document, offset));
	}

	@Test
	public void testIsValidAutoActivationLocationText()
	{
		// need to close previous editor
		String source = "<a>|";
		IFileStore fileStore = createFileStore("proposal_tests", "html", source);
		this.setupTestContext(fileStore);
		int offset = this.cursorOffsets.get(0);

		assertFalse(processor.isValidAutoActivationLocation('t', 't', document, offset));
	}

	@Test
	public void testAPSTUD3862() throws Exception
	{
		TestProject project = createWebProject("3862_");
		try
		{
			project.createFolder("public");
			project.createFolder("public/css");
			project.createFolder("application");
			IFile file = project.createFile("index.html", "<img src=\"/img/\" />\n");

			AbstractThemeableEditor editor = (AbstractThemeableEditor) createEditor(new FileEditorInput(file));
			fProcessor = new HTMLContentAssistProcessor(editor);
			ISourceViewer viewer = editor.getISourceViewer();
			ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, 15, '\t', false);

			assertEquals(
					"src value prefix refers to non-existant subfolder, but we incorrectly suggested children anyways",
					0, proposals.length);
		}
		finally
		{
			project.delete();
		}
	}

	private ITextViewer createTextViewer(IDocument fDocument)
	{
		ITextViewer viewer = new TextViewer(new Shell(), SWT.NONE);
		viewer.setDocument(fDocument);
		return viewer;
	}
}
