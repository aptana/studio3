package com.aptana.git.ui.hyperlink;

import junit.framework.TestCase;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class HyperlinkDetectorTest extends TestCase
{

	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	// Auto-merging plugins/com.aptana.branding/window1616.png
	// Auto-merging plugins/com.aptana.core/src/com/aptana/core/IURIMapper.java
	// Removing plugins/com.aptana.explorer/icons/full/elcl16/command.png
	// Auto-merging plugins/com.aptana.ui/src/com/aptana/ui/actions/DefaultNavigatorActionProvider.java
	// Auto-merging plugins/com.aptana.webserver.core/src/com/aptana/webserver/core/WorkspaceResolvingURIMapper.java
	// Merge made by recursive.
	// plugins/com.aptana.branding/about.ini | 4 +-
	// plugins/com.aptana.branding/build.properties | 4 +-
	// .../window1616.png} | Bin 3303 -> 3492 bytes
	// plugins/com.aptana.branding/window3232.png | Bin 0 -> 5003 bytes
	// .../ide/core/io/downloader/DownloadManager.java | 21 +
	// plugins/com.aptana.core/plugin.xml | 2 +-
	// .../src/com/aptana/core/IURIMapper.java} | 12 +-
	// .../core/internal/PlatformPropertyTester.java | 14 +-
	// .../src/com/aptana/core/util/FileUtil.java | 29 +-
	// .../src/com/aptana/core/util/FirefoxUtil.java | 37 +-
	// .../src/com/aptana/core/util/IOUtil.java | 39 +-
	// .../src/com/aptana/core/util/ProcessUtil.java | 129 +++-
	// .../src/com/aptana/core/util/SocketUtil.java | 14 +
	// .../src/com/aptana/core/util/StringUtil.java | 11 +
	// .../src/com/aptana/core/util/ZipUtil.java | 88 ++
	// plugins/com.aptana.debug.core/META-INF/MANIFEST.MF | 2 +-
	// .../src/com/aptana/debug/core/internal/Util.java | 10 +
	// .../src/com/aptana/debug/core/util/DebugUtil.java | 28 +
	// .../contentassist/HTMLContentAssistProcessor.java | 10 +-
	// .../aptana/editor/js/sdoc/parsing/SDocParser.java | 2 +-
	// .../icons/full/elcl16/command.png | Bin 961 -> 0 bytes
	// .../icons/full/elcl16/config.png | Bin 0 -> 942 bytes
	// .../icons/full/elcl16/deploy_package.png | Bin 0 -> 669 bytes
	// .../com/aptana/explorer/IExplorerUIConstants.java | 4 +-
	// .../explorer/internal/ui/GitProjectView.java | 17 +-
	// .../com/aptana/explorer/internal/ui/Messages.java | 3 +
	// .../explorer/internal/ui/SingleProjectView.java | 116 +++-
	// .../explorer/internal/ui/messages.properties | 3 +
	// .../navigator/actions/CommandsActionProvider.java | 12 +-
	// .../navigator/actions/DeployActionProvider.java | 5 +-
	// .../internal/SFTPFileUploadOutputStream.java | 1 -
	// .../src/com/aptana/git/core/GitMoveDeleteHook.java | 37 +-
	// .../com/aptana/git/core/GitProjectRefresher.java | 8 +-
	// .../com/aptana/git/core/model/GitExecutable.java | 7 +-
	// .../src/com/aptana/git/core/model/GitIndex.java | 76 +-
	// .../com/aptana/git/core/model/GitRepository.java | 249 ++++--
	// .../git/core/model/GitRepositoryManager.java | 14 +-
	// .../git/ui/internal/actions/BlameHandler.java | 7 +-
	// .../com.aptana.js.debug.core/META-INF/MANIFEST.MF | 2 +-
	// .../debug/core/JSLaunchConfigurationDelegate.java | 31 +-
	// .../debug/core/internal/model/DebugConnection.java | 5 -
	// .../debug/core/internal/model/JSDebugTarget.java | 44 +-
	// .../com.aptana.js.debug.ui/META-INF/MANIFEST.MF | 3 +-
	// .../js/debug/ui/internal/JSLaunchShortcut.java | 1 +
	// .../ui/internal/LaunchConfigurationsHelper.java | 1 +
	// .../js/debug/ui/internal/StartPageManager.java | 1 +
	// plugins/com.aptana.portal.ui/plugin.xml | 5 +
	// .../actionControllers/ViewActionController.java | 93 +++
	// .../internal/impl/WebServerPreviewHandler.java | 32 +-
	// .../samples/ui/project/NewSampleProjectWizard.java | 810 +++++++++---------
	// .../samples/ui/project/SampleProjectCreator.java | 72 +-
	// .../com/aptana/samples/ui/views/SamplesView.java | 856 ++++++++++----------
	// .../ui/views/SamplesViewContentProvider.java | 190 +++---
	// .../samples/ui/views/SamplesViewLabelProvider.java | 240 +++---
	// .../samples/handlers/ISamplePreviewHandler.java | 44 +-
	// .../samples/handlers/ISampleProjectHandler.java | 46 +-
	// .../aptana/samples/internal/SamplesManager.java | 268 +++---
	// .../src/com/aptana/samples/model/SampleEntry.java | 154 ++--
	// .../scripting/model/CommandScriptRunner.java | 9 +-
	// .../com/aptana/ide/syncing/core/old/Messages.java | 4 +
	// .../aptana/ide/syncing/core/old/Synchronizer.java | 4 +-
	// .../ide/syncing/core/old/VirtualFileSyncPair.java | 11 +-
	// .../ide/syncing/core/old/messages.properties | 2 +
	// .../ide/syncing/ui/views/FTPManagerComposite.java | 3 -
	// plugins/com.aptana.terminal/plugin.xml | 4 -
	// .../theme/internal/InvasiveThemeHijacker.java | 12 +-
	// plugins/com.aptana.ui.epl/icons/close.gif | Bin 0 -> 73 bytes
	// plugins/com.aptana.ui.epl/icons/close_hot.gif | Bin 0 -> 852 bytes
	// .../src/com/aptana/ui/dialogs/Messages.java | 2 +
	// .../com/aptana/ui/dialogs/TitaniumUpdatePopup.java | 151 ++++
	// .../src/com/aptana/ui/dialogs/messages.properties | 2 +
	// .../src/com/aptana/ui/epl/UIEplPlugin.java | 39 +-
	// plugins/com.aptana.ui/META-INF/MANIFEST.MF | 3 +-
	// .../com.aptana.ui/OSGI-INF/l10n/bundle.properties | 2 +-
	// .../com.aptana.ui/src/com/aptana/ui/Messages.java | 4 +
	// .../com.aptana.ui/src/com/aptana/ui/UIPlugin.java | 176 ++++-
	// .../actions/DefaultNavigatorActionProvider.java} | 46 +-
	// .../aptana/ui/dialogs/ProjectSelectionDialog.java | 2 +-
	// .../aptana/ui/internal/WebPerspectiveFactory.java | 12 +-
	// .../ui/internal/commands/OpenInFinderHandler.java | 14 +-
	// .../src/com/aptana/ui/messages.properties | 3 +
	// .../ui/preferences/IPreferenceConstants.java | 9 +
	// .../com/aptana/ui/util/WorkbenchBrowserUtil.java | 69 +--
	// .../core/AbstractWebServerConfiguration.java | 8 +-
	// .../webserver/core/EFSWebServerConfiguration.java | 11 +-
	// ...apper.java => WorkspaceResolvingURIMapper.java} | 23 +-
	// plugins/com.aptana.workbench/build.properties | 2 +-
	// .../aptana/workbench/commands/EditBundleJob.java | 7 +-
	// .../.settings/org.eclipse.pde.core.prefs | 4 +
	// .../.settings/org.eclipse.pde.prefs | 33 +
	// .../com/aptana/editor/epl/tests/DisplayWaiter.java | 1 +
	// .../aptana/editor/epl/tests/EditorTestHelper.java | 1 +
	// .../core/tests/LargeSampleSyncingTests.java | 157 +++--
	// .../syncing/core/tests/SyncingErrorTests.java | 106 ++-
	// 94 files changed, 2919 insertions(+), 1930 deletions(-)
	// rename plugins/{com.aptana.explorer/icons/full/elcl16/network_arrow.png => com.aptana.branding/window1616.png}
	// (78%)
	// create mode 100644 plugins/com.aptana.branding/window3232.png
	// rename plugins/{com.aptana.webserver.core/src/com/aptana/webserver/core/IURLMapper.java =>
	// com.aptana.core/src/com/aptana/core/IURIMapper.java} (75%)
	// create mode 100644 plugins/com.aptana.core/src/com/aptana/core/util/ZipUtil.java
	// delete mode 100644 plugins/com.aptana.explorer/icons/full/elcl16/command.png
	// create mode 100644 plugins/com.aptana.explorer/icons/full/elcl16/config.png
	// create mode 100644 plugins/com.aptana.explorer/icons/full/elcl16/deploy_package.png
	// create mode 100644
	// plugins/com.aptana.portal.ui/src/com/aptana/portal/ui/dispatch/actionControllers/ViewActionController.java
	// create mode 100644 plugins/com.aptana.ui.epl/icons/close.gif
	// create mode 100644 plugins/com.aptana.ui.epl/icons/close_hot.gif
	// create mode 100644 plugins/com.aptana.ui.epl/src/com/aptana/ui/dialogs/TitaniumUpdatePopup.java
	// rename plugins/{com.aptana.explorer/src/com/aptana/explorer/navigator/actions/ExplorerActionProvider.java =>
	// com.aptana.ui/src/com/aptana/ui/actions/DefaultNavigatorActionProvider.java} (63%)
	// create mode 100644 plugins/com.aptana.ui/src/com/aptana/ui/preferences/IPreferenceConstants.java
	// rename plugins/com.aptana.webserver.core/src/com/aptana/webserver/core/{WorkspaceResolvingURLMapper.java =>
	// WorkspaceResolvingURIMapper.java} (85%)
	// create mode 100644 plugins/org.apache.httpcomponents.httpcore/.settings/org.eclipse.pde.core.prefs
	// create mode 100644 plugins/org.apache.httpcomponents.httpcore/.settings/org.eclipse.pde.prefs

	public void testDetectFileWithLinesAdded() throws Exception
	{
		HyperlinkDetector detector = new HyperlinkDetector();
		IHyperlink[] links = detector.detectHyperlinks("railties/test/application/rake_test.rb             |   17 ++");
		assertNotNull(links);
		assertEquals(1, links.length);
		IRegion region = links[0].getHyperlinkRegion();
		assertEquals(0, region.getOffset());
		assertEquals(38, region.getLength());
	}

	public void testDetectFileWithLinesRemoved() throws Exception
	{
		HyperlinkDetector detector = new HyperlinkDetector();
		IHyperlink[] links = detector.detectHyperlinks("railties/test/application/rake_test.rb             |    2 --");
		assertNotNull(links);
		assertEquals(1, links.length);
		IRegion region = links[0].getHyperlinkRegion();
		assertEquals(0, region.getOffset());
		assertEquals(38, region.getLength());
	}

	public void testDetectFileWithLinesAddedAndRemoved() throws Exception
	{
		HyperlinkDetector detector = new HyperlinkDetector();
		IHyperlink[] links = detector.detectHyperlinks("railties/test/application/rake_test.rb             |    2 +-");
		assertNotNull(links);
		assertEquals(1, links.length);
		IRegion region = links[0].getHyperlinkRegion();
		assertEquals(0, region.getOffset());
		assertEquals(38, region.getLength());
	}

	public void testDetectBinaryFileChanges() throws Exception
	{
		HyperlinkDetector detector = new HyperlinkDetector();
		IHyperlink[] links = detector
				.detectHyperlinks(".../icons/full/elcl16/deploy_package.png           |  Bin 0 -> 669 bytes");
		assertNotNull(links);
		assertEquals(1, links.length);
		IRegion region = links[0].getHyperlinkRegion();
		assertEquals(0, region.getOffset());
		assertEquals(40, region.getLength());
	}

	public void testAutoMergingLine() throws Exception
	{
		HyperlinkDetector detector = new HyperlinkDetector();
		IHyperlink[] links = detector.detectHyperlinks("Auto-merging plugins/com.aptana.branding/window1616.png");
		assertNotNull(links);
		assertEquals(1, links.length);
		IRegion region = links[0].getHyperlinkRegion();
		assertEquals(13, region.getOffset());
		assertEquals(42, region.getLength());
	}

	public void testRemovingLine() throws Exception
	{
		HyperlinkDetector detector = new HyperlinkDetector();
		IHyperlink[] links = detector
				.detectHyperlinks("Removing plugins/com.aptana.explorer/icons/full/elcl16/command.png");
		assertNotNull(links);
		assertEquals(1, links.length);
		IRegion region = links[0].getHyperlinkRegion();
		assertEquals(9, region.getOffset());
		assertEquals(57, region.getLength());
	}

	public void testCreateLine() throws Exception
	{
		HyperlinkDetector detector = new HyperlinkDetector();
		IHyperlink[] links = detector.detectHyperlinks("create mode 100644 plugins/com.aptana.branding/window3232.png");
		assertNotNull(links);
		assertEquals(1, links.length);
		IRegion region = links[0].getHyperlinkRegion();
		assertEquals(19, region.getOffset());
		assertEquals(42, region.getLength());
	}

	public void testDeleteLine() throws Exception
	{
		HyperlinkDetector detector = new HyperlinkDetector();
		IHyperlink[] links = detector
				.detectHyperlinks("delete mode 100644 plugins/com.aptana.explorer/icons/full/elcl16/command.png");
		assertNotNull(links);
		assertEquals(1, links.length);
		IRegion region = links[0].getHyperlinkRegion();
		assertEquals(19, region.getOffset());
		assertEquals(57, region.getLength());
	}
	
	public void testRenameLine() throws Exception
	{
		HyperlinkDetector detector = new HyperlinkDetector();
		IHyperlink[] links = detector
				.detectHyperlinks(" rename plugins/{com.aptana.webserver.core/src/com/aptana/webserver/core/IURLMapper.java => com.aptana.core/src/com/aptana/core/IURIMapper.java} (75%)");
		assertNotNull(links);
		assertEquals(1, links.length);
		IRegion region = links[0].getHyperlinkRegion();
		assertEquals(8, region.getOffset());
		assertEquals(136, region.getLength());
	}
}
