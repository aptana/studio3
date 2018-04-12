package com.aptana.buildpath.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.FileUtil;
import com.aptana.testing.utils.TestProject;

public class BuildPathManagerTest
{

	private static IProject project;
	private static TestProject tp;

	@BeforeClass
	public static void setupProject() throws Exception
	{
		tp = new TestProject("bpm", new String[0]);
		project = tp.getInnerProject();
	}

	@AfterClass
	public static void tearDownProject() throws Exception
	{
		tp.delete();
	}

	@After
	public void tearDownBuildPaths() throws Exception
	{
		// Essentially we wipe the build paths for the project each time
		BuildPathManager.getInstance().setBuildPaths(project, new HashSet<IBuildPathEntry>());
	}

	@Test
	public void testManagingGlobalBuildPaths() throws IOException
	{
		BuildPathManager bpm = BuildPathManager.getInstance();

		File file = FileUtil.createTempFile("bpe", ".entry");
		IBuildPathEntry entry = new BuildPathEntry("build path entry", file.toURI());

		Set<IBuildPathEntry> entries = bpm.getBuildPaths();
		int initialSize = entries.size();

		assertFalse(bpm.hasBuildPath(entry));

		// Add it
		assertTrue(bpm.addBuildPath(entry));

		entries = bpm.getBuildPaths();
		assertEquals("Expected build path set to increase size by one, because of one we registered globally",
				initialSize + 1, entries.size());
		assertTrue("Expected build paths to include one we just added!", bpm.hasBuildPath(entry));

		// Now remove it
		assertTrue(bpm.removeBuildPath(entry));

		// check that it got removed
		entries = bpm.getBuildPaths();
		assertEquals(initialSize, entries.size());
		assertFalse("Expected build paths to no longer include one we just removed!", bpm.hasBuildPath(entry));
	}

	@Test
	public void testAddingNullEntryReturnsFalse() throws IOException
	{
		BuildPathManager bpm = BuildPathManager.getInstance();
		assertFalse(bpm.addBuildPath(null));
	}

	@Test
	public void testRemovingNullEntryReturnsFalse() throws IOException
	{
		BuildPathManager bpm = BuildPathManager.getInstance();
		assertFalse(bpm.removeBuildPath(null));
	}

	@Test
	public void testRemovingEntryThatDidntExistReturnsFalse() throws IOException
	{
		BuildPathManager bpm = BuildPathManager.getInstance();

		File file = FileUtil.createTempFile("bpe", ".entry");
		IBuildPathEntry entry = new BuildPathEntry("build path entry", file.toURI());
		assertFalse(bpm.hasBuildPath(entry));
		assertFalse(bpm.removeBuildPath(entry));
	}

	@Test
	public void testGetBuildPathsOfNullProject()
	{
		BuildPathManager bpm = BuildPathManager.getInstance();
		Set<IBuildPathEntry> entries = bpm.getBuildPaths(null);
		assertTrue("Expected empty build path set on null project", CollectionsUtil.isEmpty(entries));
	}

	@Test
	public void testOnlyRegisteredBuildPathsAreReturnedFromProjectBuildPathSet() throws Exception
	{
		BuildPathManager bpm = BuildPathManager.getInstance();

		File file = FileUtil.createTempFile("bpe", ".entry");
		IBuildPathEntry entry = new BuildPathEntry("build path entry", file.toURI());

		Set<IBuildPathEntry> entries = bpm.getBuildPaths(project);
		assertTrue("Expected empty build path set on project with none added", CollectionsUtil.isEmpty(entries));

		// Add an unregistered build path to the project
		assertTrue(bpm.addBuildPath(project, entry));

		entries = bpm.getBuildPaths(project);
		assertTrue("Expected empty build path set for project since single build path set was not globally registered",
				CollectionsUtil.isEmpty(entries));
	}

	@Test
	public void testSetAndGetBuildPathsOnProject() throws Exception
	{
		BuildPathManager bpm = BuildPathManager.getInstance();

		// Register the build path entry "globally" as a valid one.
		File file = FileUtil.createTempFile("bpe", ".entry");
		IBuildPathEntry entry = new BuildPathEntry("build path entry", file.toURI());
		bpm.addBuildPath(entry);

		// verify the build path is not attached to the project
		assertFalse(bpm.hasBuildPath(project, entry));
		assertTrue("Expected empty build path set on project with none added",
				CollectionsUtil.isEmpty(bpm.getBuildPaths(project)));

		// Now add the build path to the project
		assertTrue(bpm.addBuildPath(project, entry));

		// Verify it got added
		assertEquals(1, bpm.getBuildPaths(project).size());
		assertTrue(bpm.hasBuildPath(project, entry));

		// Remove it
		assertTrue(bpm.removeBuildPath(project, entry));

		// verify it got removed
		assertFalse(bpm.hasBuildPath(project, entry));
		assertTrue("Expected empty build path set on project with none added",
				CollectionsUtil.isEmpty(bpm.getBuildPaths(project)));

		// Now use setEntries to add
		Set<IBuildPathEntry> newEntries = CollectionsUtil.newSet(entry);
		assertTrue(bpm.setBuildPaths(project, newEntries));
		assertEquals(1, bpm.getBuildPaths(project).size());
		assertTrue(bpm.hasBuildPath(project, entry));
	}

	@Test
	public void testAddingNullEntryToProjectReturnsFalse() throws Exception
	{
		BuildPathManager bpm = BuildPathManager.getInstance();
		assertFalse(bpm.addBuildPath(project, null));
	}

	@Test
	public void testAddingEntryToNullProjectReturnsFalse() throws Exception
	{
		BuildPathManager bpm = BuildPathManager.getInstance();
		File file = FileUtil.createTempFile("bpe", ".entry");
		IBuildPathEntry entry = new BuildPathEntry("build path entry", file.toURI());
		assertFalse(bpm.addBuildPath(null, entry));
	}

	@Test
	public void testRemovingNullEntryFromProjectReturnsFalse() throws Exception
	{
		BuildPathManager bpm = BuildPathManager.getInstance();
		assertFalse(bpm.removeBuildPath(project, null));
	}

	@Test
	public void testRemovingEntryFromNullProjectReturnsFalse() throws Exception
	{
		BuildPathManager bpm = BuildPathManager.getInstance();
		File file = FileUtil.createTempFile("bpe", ".entry");
		IBuildPathEntry entry = new BuildPathEntry("build path entry", file.toURI());
		assertFalse(bpm.removeBuildPath(null, entry));
	}

	@Test
	public void testSetBuildPathsOnNullProjectReturnsFalse() throws Exception
	{
		BuildPathManager bpm = BuildPathManager.getInstance();
		File file = FileUtil.createTempFile("bpe", ".entry");
		IBuildPathEntry entry = new BuildPathEntry("build path entry", file.toURI());
		Set<IBuildPathEntry> newEntries = CollectionsUtil.newSet(entry);
		assertFalse(bpm.setBuildPaths(null, newEntries));
	}

	@Test
	public void testSetNullBuildPathsOnProjectReturnsFalse() throws Exception
	{
		BuildPathManager bpm = BuildPathManager.getInstance();
		assertFalse(bpm.setBuildPaths(project, null));
	}
}
