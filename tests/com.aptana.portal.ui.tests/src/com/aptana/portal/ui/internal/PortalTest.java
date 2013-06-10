package com.aptana.portal.ui.internal;

import java.io.File;
import java.net.URL;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.core.tests.TestProject;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.portal.ui.IPortalPreferences;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.theme.IThemeManager;
import com.aptana.usage.UsagePlugin;

/**
 */
public class PortalTest extends TestCase
{
	/**
	 * testGetDefaultURLEmpty
	 */
	public void testGetDefaultURLEmpty() throws Exception
	{
		Portal fixture = Portal.getInstance();
		URL result = fixture.getDefaultURL();
		assertEquals(Portal.BASE_REMOTE_URL, result.toString());
	}

	/**
	 * testGetDefaultURLEmpty
	 */
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
	public void testGetGUID() throws Exception
	{
		Portal fixture = Portal.getInstance();
		String result = fixture.getGUID();
		assertEquals(UsagePlugin.getApplicationId(), result);
	}

	/**
	 * testGetInstance
	 */
	public void testGetInstance() throws Exception
	{
		Portal result = Portal.getInstance();
		assertNotNull(result);
	}

	/**
	 * testGetProjectTypeNull
	 */
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
	public void testGetThemeManager() throws Exception
	{
		Portal fixture = Portal.getInstance();

		IThemeManager result = fixture.getThemeManager();
		assertNotNull(result);
	}

	/**
	 * testGetURLParametersForProjectNull
	 */
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
	public void testOpenPortal() throws Exception
	{
		Portal fixture = Portal.getInstance();
		String browserEditorId = StringUtil.EMPTY;
		fixture.openPortal(null, browserEditorId);
	}

	/**
	 * testOpenPortalNull
	 */
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
	public void testShouldOpenPortal() throws Exception
	{
		Portal fixture = Portal.getInstance();

		IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode(PortalUIPlugin.PLUGIN_ID);
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
	public void tearDown() throws Exception
	{
		// Add additional tear down code here
	}
}