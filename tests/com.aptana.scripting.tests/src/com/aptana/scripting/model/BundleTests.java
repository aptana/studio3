/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.scripting.ScriptLogListener;
import com.aptana.scripting.model.filters.IModelFilter;

public class BundleTests extends BundleTestBase
{
	/**
	 * A runnable intended to stress test the bundle manager. We randomly add bundles (with a random precedence level
	 * (app/user) or snippets to bundles. This should help find deadlocks in the hierarchy of objects (manager -> bundle
	 * entries -> bundle elements -> children)
	 * 
	 * @author cwilliams
	 */
	private final class BundleLoadingRunnable implements Runnable
	{

		private int iterations;
		private Random r = new Random();
		private BundleManager manager = getBundleManagerInstance();

		BundleLoadingRunnable(int iterations)
		{
			this.iterations = iterations;
		}

		public void run()
		{
			manager.addBundle(new BundleElement(getRandomBundlePath() + "/bundle" + System.nanoTime()));
			for (int i = 0; i < iterations; i++)
			{
				switch (r.nextInt(2))
				{
					case 0:
						// Pick a random bundle
						List<String> names = manager.getBundleNames();
						String name = names.get(r.nextInt(names.size()));
						BundleEntry entry = manager.getBundleEntry(name);
						// Pick a random element of the bundle (basically app, user or project level bundle)
						List<BundleElement> elements = entry.getBundles();
						BundleElement be = elements.get(r.nextInt(elements.size()));
						// Add a snippet to it
						be.addChild(new SnippetElement(be.getPath() + "/snippets/" + System.nanoTime() + ".rb"));
						break;
					case 1:
						manager.addBundle(new BundleElement(getRandomBundlePath() + "/bundle" + System.nanoTime()));
						break;
				}
				Thread.yield();
			}
		}

		private String getRandomBundlePath()
		{
			switch (r.nextInt(2))
			{
				case 0:
					List<String> paths = manager.getApplicationBundlesPaths();
					return paths.get(r.nextInt(paths.size()));
				case 1:
					return manager.getUserBundlesPath();
			}
			return "/fake/path";
		}
	}

	public class LogListener implements ScriptLogListener
	{
		List<String> errors = new LinkedList<String>();
		List<String> infos = new LinkedList<String>();
		List<String> warnings = new LinkedList<String>();
		List<String> prints = new LinkedList<String>();
		List<String> printErrors = new LinkedList<String>();
		List<String> traces = new LinkedList<String>();

		public void logError(String error)
		{
			this.errors.add(error);
		}

		public void logInfo(String info)
		{
			this.infos.add(info);
		}

		public void logWarning(String warning)
		{
			this.warnings.add(warning);
		}

		public void print(String message)
		{
			this.prints.add(message);
		}

		public void printError(String message)
		{
			this.printErrors.add(message);
		}

		public void trace(String message)
		{
			this.traces.add(message);
		}

		public void reset()
		{
			this.errors.clear();
			this.infos.clear();
			this.warnings.clear();
			this.prints.clear();
			this.printErrors.clear();
			this.traces.clear();
		}
	}

	private boolean deadlocked;

	/**
	 * compareScopedBundles
	 * 
	 * @param bundleName
	 * @param prec1
	 * @param prec2
	 * @param command1
	 * @param command2
	 */
	private void compareScopedBundles(String bundleName, BundlePrecedence prec1, BundlePrecedence prec2,
			String command1, String command2)
	{
		// confirm first bundle loaded properly
		BundleEntry entry = this.getBundleEntry(bundleName, prec1);
		List<CommandElement> commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.size());
		assertEquals(command1, commands.get(0).getInvoke());

		// confirm second bundle overrides application
		entry = this.getBundleEntry(bundleName, prec2);
		commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.size());
		assertEquals(command2, commands.get(0).getInvoke());
	}

	/**
	 * compareScopedBundlesWithDelete
	 * 
	 * @param bundleName
	 * @param prec1
	 * @param prec2
	 * @param command1
	 * @param command2
	 */
	private void compareScopedBundlesWithDelete(String bundleName, BundlePrecedence prec1, BundlePrecedence prec2,
			String command1, String command2)
	{
		this.compareScopedBundles(bundleName, prec1, prec2, command1, command2);

		BundleEntry entry = getBundleManagerInstance().getBundleEntry(bundleName);
		List<BundleElement> bundles = entry.getBundles();
		assertEquals(2, bundles.size());
		entry.removeBundle(bundles.get(1));

		List<CommandElement> commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.size());
		assertEquals(command1, commands.get(0).getInvoke());
	}

	@Before
	public void setUp() throws Exception
	{
		deadlocked = false;
	}

	@Test
	public void testLoadLoneBundle()
	{
		String bundleName = "loneBundle";
		BundleElement bundle = this.loadBundle(bundleName, BundlePrecedence.APPLICATION);

		assertNotNull(bundle);
		assertEquals(bundleName, bundle.getDisplayName());
	}

	@Test
	public void testLoadBundleWithCommand()
	{
		String bundleName = "bundleWithCommand";
		BundleEntry entry = this.getBundleEntry(bundleName, BundlePrecedence.APPLICATION);
		List<CommandElement> commands = entry.getCommands();

		assertNotNull(commands);
		assertEquals(1, commands.size());
	}

	@Test
	public void testLoadBundleWithMenu()
	{
		String bundleName = "bundleWithMenu";
		BundleEntry entry = this.getBundleEntry(bundleName, BundlePrecedence.APPLICATION);
		List<MenuElement> menus = entry.getMenus();

		assertNotNull(menus);
		assertEquals(1, menus.size());
	}

	@Test
	public void testLoadBundleWithSnippet()
	{
		String bundleName = "bundleWithSnippet";
		BundleEntry entry = this.getBundleEntry(bundleName, BundlePrecedence.APPLICATION);
		List<CommandElement> snippets = entry.getCommands();

		assertNotNull(snippets);
		assertEquals(1, snippets.size());
		assertTrue(snippets.get(0) instanceof SnippetElement);

		List<SnippetElement> snippetElements = entry.getSnippets();
		assertNotNull(snippetElements);
		assertEquals(1, snippetElements.size());
	}

	@Test
	public void testLoadBundleWithSnippetUsingFilter()
	{
		String bundleName = "bundleWithSnippet";
		this.getBundleEntry(bundleName, BundlePrecedence.APPLICATION);
		List<SnippetElement> snippets = getBundleManagerInstance().getSnippets(new IModelFilter()
		{

			public boolean include(AbstractElement element)
			{
				return element instanceof SnippetElement;
			}
		});

		assertNotNull(snippets);
		assertEquals(1, snippets.size());
	}

	@Test
	public void testLoadBundleWithSnippetUsingNullFilter()
	{
		String bundleName = "bundleWithSnippet";
		int origSize = CollectionsUtil.size(getBundleManagerInstance().getSnippets(null));
		this.getBundleEntry(bundleName, BundlePrecedence.APPLICATION);
		List<SnippetElement> snippets = getBundleManagerInstance().getSnippets(null);

		assertNotNull(snippets);
		assertEquals(origSize + 1, snippets.size());
	}

	@Test
	public void testUserOverridesApplication()
	{
		compareScopedBundles("bundleWithCommand", BundlePrecedence.APPLICATION, BundlePrecedence.USER, "cd", "cd ..");
	}

	@Test
	public void testUserOverridesApplication2()
	{
		this.compareScopedBundles("bundleWithCommand", BundlePrecedence.USER, BundlePrecedence.APPLICATION, "cd ..",
				"cd ..");
	}

	@Test
	public void testProjectOverridesApplication()
	{
		this.compareScopedBundles("bundleWithCommand", BundlePrecedence.APPLICATION, BundlePrecedence.PROJECT, "cd",
				"cd /");
	}

	@Test
	public void testProjectOverridesApplication2()
	{
		this.compareScopedBundles("bundleWithCommand", BundlePrecedence.PROJECT, BundlePrecedence.APPLICATION, "cd /",
				"cd /");
	}

	@Test
	public void testProjectOverridesUser()
	{
		this.compareScopedBundles("bundleWithCommand", BundlePrecedence.USER, BundlePrecedence.PROJECT, "cd ..", "cd /");
	}

	@Test
	public void testProjectOverridesUser2()
	{
		this.compareScopedBundles("bundleWithCommand", BundlePrecedence.PROJECT, BundlePrecedence.USER, "cd /", "cd /");
	}

	@Test
	public void testApplicationOverrideAndDelete()
	{
		this.compareScopedBundlesWithDelete("bundleWithCommand", BundlePrecedence.APPLICATION, BundlePrecedence.USER,
				"cd", "cd ..");
	}

	@Test
	public void testApplicationOverrideAndDelete2()
	{
		this.compareScopedBundlesWithDelete("bundleWithCommand", BundlePrecedence.APPLICATION,
				BundlePrecedence.PROJECT, "cd", "cd /");
	}

	@Test
	public void testUserOverrideAndDelete()
	{
		this.compareScopedBundlesWithDelete("bundleWithCommand", BundlePrecedence.USER, BundlePrecedence.PROJECT,
				"cd ..", "cd /");
	}

	@Test
	public void testSamePrecedenceOverride()
	{
		// confirm first bundle loaded properly
		BundleEntry entry = this.getBundleEntry("bundleWithCommand", BundlePrecedence.USER);
		List<CommandElement> commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.size());
		assertEquals("cd ..", commands.get(0).getInvoke());

		// confirm second bundle overrides application
		this.loadBundleEntry("bundleWithSameCommand", BundlePrecedence.USER);
		commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.size());
		assertEquals("cd", commands.get(0).getInvoke());
	}

	@Test
	public void testSamePrecedenceOverride2()
	{
		// confirm first bundle loaded properly
		this.loadBundleEntry("bundleWithSameCommand", BundlePrecedence.USER);
		BundleEntry entry = getBundleManagerInstance().getBundleEntry("bundleWithCommand");
		assertNotNull(entry);
		List<CommandElement> commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.size());
		assertEquals("cd", commands.get(0).getInvoke());

		// confirm second bundle overrides application
		this.loadBundleEntry("bundleWithCommand", BundlePrecedence.USER);
		commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.size());
		assertEquals("cd", commands.get(0).getInvoke());
	}

	@Test
	public void testSamePrecedenceAugmentation()
	{
		// confirm first bundle loaded properly
		BundleEntry entry = this.getBundleEntry("bundleWithCommand", BundlePrecedence.USER);
		List<CommandElement> commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(1, commands.size());
		assertEquals("cd ..", commands.get(0).getInvoke());

		// confirm second bundle overrides application
		this.loadBundleEntry("bundleWithDifferentCommand", BundlePrecedence.USER);
		commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(2, commands.size());

		CommandElement command1 = commands.get(0);
		CommandElement command2 = commands.get(1);

		if (command1.getDisplayName().equals("MyCommand"))
		{
			assertEquals("cd ..", command1.getInvoke());
			assertEquals("cd", command2.getInvoke());
		}
		else
		{
			assertEquals("cd", command1.getInvoke());
			assertEquals("cd ..", command2.getInvoke());
		}
	}

	/**
	 * testBundleInCommandsDirectory
	 */
	// FIXME not working ATM
	// @Test public void testBundleInCommandsDirectory()
	// {
	// LogListener listener = new LogListener();
	// ScriptLogger.getInstance().addLogListener(listener);
	// this.loadBundleEntry("bundleInCommands", BundlePrecedence.PROJECT);
	//
	// assertEquals(1, listener.errors.size());
	// assertTrue(listener.errors.get(0).contains(
	// "Attempted to define a bundle in a file other than the bundle's bundle.rb file:"));
	// }

	/**
	 * testBundleFileInCommandsDirectory
	 */
	@Test
	public void testBundleFileInCommandsDirectory()
	{
		// LogListener listener = new LogListener();
		// ScriptLogger.getInstance().addLogListener(listener);
		// this.loadBundleEntry("bundleFileInCommands", BundlePrecedence.PROJECT);

		// assertEquals(1, listener.errors.size());
		// assertTrue(listener.errors.get(0).contains(
		// "Attempted to define a bundle in a file other than the bundle's bundle.rb file:"));
	}

	@Test
	public void testNameFromBundleDirectory()
	{
		// load bundle
		String bundleName = "bundleName";
		this.loadBundleEntry(bundleName, BundlePrecedence.PROJECT);

		// get bundle entry
		BundleEntry entry = getBundleManagerInstance().getBundleEntry(bundleName);
		assertNotNull(entry);
	}

	@Test
	public void testNameFromBundleDirectoryWithExtension()
	{
		// load bundle
		this.loadBundleEntry("bundleNameWithExtension.ruble", BundlePrecedence.PROJECT);

		// get bundle entry
		BundleEntry entry = getBundleManagerInstance().getBundleEntry("bundleNameWithExtension");
		assertNotNull(entry);
	}

	@Test
	public void testBundleIsBundleDeclaration()
	{
		String bundleName = "bundleDefinition";
		this.loadBundleEntry(bundleName, BundlePrecedence.PROJECT);

		BundleEntry entry = getBundleManagerInstance().getBundleEntry(bundleName);
		assertNotNull(entry);

		List<BundleElement> bundles = entry.getBundles();
		assertNotNull(bundles);
		assertEquals(1, bundles.size());
		assertFalse(bundles.get(0).isReference());
	}

	@Test
	public void testBundleIsBundleDeclaration2()
	{
		String bundleName = "bundleDefinition2";
		this.loadBundleEntry(bundleName, BundlePrecedence.PROJECT);

		BundleEntry entry = getBundleManagerInstance().getBundleEntry(bundleName);
		assertNotNull(entry);

		List<BundleElement> bundles = entry.getBundles();
		assertNotNull(bundles);
		assertEquals(1, bundles.size());
		assertFalse(bundles.get(0).isReference());
	}

	@Test
	public void testBundleIsBundleReference()
	{
		this.loadBundleEntry("bundleReference", BundlePrecedence.PROJECT);

		BundleEntry entry = getBundleManagerInstance().getBundleEntry("MyBundle");
		assertNotNull(entry);

		List<BundleElement> bundles = entry.getBundles();
		assertNotNull(bundles);
		assertEquals(1, bundles.size());
		assertTrue(bundles.get(0).isReference());
	}

	@Test
	public void testReferenceLoadingAcrossPrecendenceBounds()
	{
		this.loadBundleEntry("bundleWithCommand", BundlePrecedence.APPLICATION);
		this.loadBundleEntry("bundleWithCommandReference", BundlePrecedence.APPLICATION);
		this.loadBundleEntry("bundleWithCommandReference", BundlePrecedence.PROJECT);

		BundleEntry entry = getBundleManagerInstance().getBundleEntry("bundleWithCommand");
		assertNotNull(entry);

		List<BundleElement> bundles = entry.getBundles();
		assertNotNull(bundles);
		assertEquals(3, bundles.size());

		List<CommandElement> commands = entry.getCommands();
		assertNotNull(commands);
		assertEquals(3, commands.size());
	}

	@Test
	public void testDeadlock() throws InterruptedException
	{
		// Launch a lot of threads contending for bundle manager collections
		int numThreads = 10;
		int iterations = 100;
		long maxWait = (iterations * 100) + 1000;
		List<Callable<Object>> jobs = new ArrayList<Callable<Object>>(numThreads);
		for (int i = 0; i < numThreads; i++)
		{
			jobs.add(Executors.callable(new BundleLoadingRunnable(iterations)));
		}
		ExecutorService service = Executors.newFixedThreadPool(numThreads);

		List<Future<Object>> result = service.invokeAll(jobs, maxWait, TimeUnit.MILLISECONDS);

		for (Future<Object> f : result)
		{
			if (f.isCancelled())
			{
				deadlocked = true; // necessary to avoid normal teardown which would actually cause us to block forever.
				fail("One thread failed to finish, assuming deadlock.");
			}
		}
	}

	@Override
	public void tearDown() throws Exception
	{
		if (deadlocked)
		{
			return;
		}
		super.tearDown();
	}
}
