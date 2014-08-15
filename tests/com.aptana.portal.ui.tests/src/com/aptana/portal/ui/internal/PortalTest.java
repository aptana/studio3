package com.aptana.portal.ui.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.tests.TestProject;
import com.aptana.core.util.StringUtil;
import com.aptana.portal.ui.IPortalPreferences;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.theme.IThemeManager;
import com.aptana.usage.UsagePlugin;

/**
 */
public class PortalTest
{
	/**
	 * testGetDefaultURLEmpty
	 */
	@Test
	public void testGetDefaultURLEmpty() throws Exception
	{
		Portal fixture = Portal.getInstance();
		URL result = fixture.getDefaultURL();
		assertEquals(Portal.BASE_REMOTE_URL, result.toString());
	}

	/**
	 * testGetDefaultURLEmpty
	 */
	@Test
	public void testGetDefaultURLNull() throws Exception
	{
		Portal fixture = Portal.getInstance();
		try
		{
			fixture.getDefaultURL(null, null);
			fail("Should throw a IllegalArgumentException"); //$NON-NLS-1$
		}
		catch (Exception ex)
		{
		}
	}

	/**
	 * testGetDefaultURLOffline
	 */
	@Test
	public void testGetDefaultURLOffline() throws Exception
	{
		URL localFileURL = new File("").toURI().toURL(); //$NON-NLS-1$
		URL googleServer = new URL("http://www.google.com"); //$NON-NLS-1$
		URL offlineServer = new URL("http://fff.appceler.abc"); //$NON-NLS-1$
		URL baseServer = new URL("http://www.msn.com"); //$NON-NLS-1$

		Portal fixture = Portal.getInstance();

		URL result = fixture.getDefaultURL(localFileURL, baseServer);
		assertEquals(localFileURL, result);

		result = fixture.getDefaultURL(googleServer, baseServer);
		assertEquals(googleServer, result);

		result = fixture.getDefaultURL(offlineServer, baseServer);
		assertEquals(baseServer, result);
	}

	/**
	 * testGetDeployParam
	 */
	@Test
	public void testGetDeployParamNullProject() throws Exception
	{
		Portal fixture = Portal.getInstance();

		TestProject project = new TestProject("deploy_param", new String[] { "com.aptana.projects.webnature" }); //$NON-NLS-1$ //$NON-NLS-2$
		try
		{
			Map<String, String> result = fixture.getDeployParam(project.getInnerProject());
			assertNotNull(result);
			assertEquals(0, result.size());
		}
		finally
		{
			project.delete();
		}
	}

	/**
	 * testGetGUID
	 */
	@Test
	public void testGetGUID() throws Exception
	{
		Portal fixture = Portal.getInstance();
		String result = fixture.getGUID();
		assertEquals(UsagePlugin.getApplicationId(), result);
	}

	/**
	 * testGetInstance
	 */
	@Test
	public void testGetInstance() throws Exception
	{
		Portal result = Portal.getInstance();
		assertNotNull(result);
	}

	/**
	 * testGetProjectTypeNull
	 */
	@Test
	public void testGetProjectTypeNull() throws Exception
	{
		Portal fixture = Portal.getInstance();
		IProject selectedProject = null;

		char result = fixture.getProjectType(selectedProject);
		assertEquals('O', result);
	}

	/**
	 * testGetProjectTypeWeb
	 */
	@Test
	public void testGetProjectTypeWeb() throws Exception
	{
		Portal fixture = Portal.getInstance();
		TestProject project = new TestProject("deploy_param", new String[] { Portal.WEB_NATURE }); //$NON-NLS-1$
		try
		{
			char result = fixture.getProjectType(project.getInnerProject());
			assertEquals('W', result);
		}
		finally
		{
			project.delete();
		}
	}

	/**
	 * testGetProjectTypeRuby
	 */
	@Test
	public void testGetProjectTypeRuby() throws Exception
	{
		Portal fixture = Portal.getInstance();
		TestProject project = new TestProject("deploy_param", new String[] { Portal.RAILS_NATURE }); //$NON-NLS-1$
		try
		{
			char result = fixture.getProjectType(project.getInnerProject());
			assertEquals('R', result);
		}
		finally
		{
			project.delete();
		}
	}

	/**
	 * testGetProjectTypePHP
	 */
	@Test
	public void testGetProjectTypePHP() throws Exception
	{
		Portal fixture = Portal.getInstance();
		TestProject project = new TestProject("deploy_param", new String[] { Portal.PHP_NATURE }); //$NON-NLS-1$
		try
		{
			char result = fixture.getProjectType(project.getInnerProject());
			assertEquals('P', result);
		}
		finally
		{
			project.delete();
		}
	}

	/**
	 * testGetProjectTypeWeb
	 */
	@Test
	public void testGetProjectTypePydev() throws Exception
	{
		Portal fixture = Portal.getInstance();
		TestProject project = new TestProject("deploy_param", new String[] { Portal.PYDEV_NATURE }); //$NON-NLS-1$
		try
		{
			char result = fixture.getProjectType(project.getInnerProject());
			assertEquals('D', result);
		}
		finally
		{
			project.delete();
		}
	}

	/**
	 * testGetProjectTypeNull
	 */
	@Test
	public void testGetThemeManager() throws Exception
	{
		Portal fixture = Portal.getInstance();

		IThemeManager result = fixture.getThemeManager();
		assertNotNull(result);
	}

	/**
	 * testGetURLParametersForProjectNull
	 */
	@Test
	public void testGetURLParametersForProjectNull() throws Exception
	{
		Portal fixture = Portal.getInstance();
		IProject activeProject = null;

		Map<String, String> result = fixture.getURLParametersForProject(activeProject);
		assertNotNull(result);
	}

	/**
	 * testOpenPortal
	 */
	@Test
	public void testOpenPortal() throws Exception
	{
		Portal fixture = Portal.getInstance();
		String browserEditorId = StringUtil.EMPTY;
		fixture.openPortal(null, browserEditorId);
	}

	/**
	 * testOpenPortalNull
	 */
	@Test
	public void testOpenPortalNull() throws Exception
	{
		Portal fixture = Portal.getInstance();
		URL url = null;
		String browserEditorId = StringUtil.EMPTY;
		boolean bringToTop = true;

		fixture.openPortal(url, browserEditorId, bringToTop, null);
	}

	/**
	 * testOpenPortalBringToTop
	 */
	@Test
	public void testOpenPortalBringToTop() throws Exception
	{
		Portal fixture = Portal.getInstance();
		String browserEditorId = StringUtil.EMPTY;
		boolean bringToTop = true;

		fixture.openPortal(null, browserEditorId, bringToTop, null);
	}

	/**
	 * testShouldOpenPortal
	 */
	@Test
	public void testShouldOpenPortal() throws Exception
	{
		Portal fixture = Portal.getInstance();

		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(PortalUIPlugin.PLUGIN_ID);
		boolean open = prefs.getBoolean(IPortalPreferences.SHOULD_OPEN_DEV_TOOLBOX, false);

		prefs.putBoolean(IPortalPreferences.SHOULD_OPEN_DEV_TOOLBOX, true);
		assertTrue(fixture.shouldOpenPortal());

		prefs.putBoolean(IPortalPreferences.SHOULD_OPEN_DEV_TOOLBOX, false);
		assertFalse(fixture.shouldOpenPortal());

		prefs.putBoolean(IPortalPreferences.SHOULD_OPEN_DEV_TOOLBOX, open);

	}

	/**
	 * Perform pre-test initialization.
	 * 
	 * @throws Exception
	 *             if the initialization fails for some reason
	 * @generatedBy CodePro at 8/31/11 2:51 PM
	 */
	@Before
	public void setUp() throws Exception
	{
		// add additional set up code here
	}

	/**
	 * Perform post-test clean-up.
	 * 
	 * @throws Exception
	 *             if the clean-up fails for some reason
	 * @generatedBy CodePro at 8/31/11 2:51 PM
	 */
	@After
	public void tearDown() throws Exception
	{
		// Add additional tear down code here
	}
}
