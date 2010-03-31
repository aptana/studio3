package com.aptana.terminal.server;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.terminal.Activator;
import com.aptana.util.ExecutableUtil;
import com.aptana.util.FileUtils;
import com.aptana.util.ResourceUtils;

/**
 * BuiltinCygwinConfiguration
 */
public class WindowsMingwConfiguration implements ProcessConfiguration
{
	private static final String REDTTY_EXE = "/redttyw.exe"; //$NON-NLS-1$
	
	private String _sh;

	/**
	 * BuiltinCygwinConfiguration
	 */
	public WindowsMingwConfiguration()
	{
		this._sh = ExecutableUtil.find("sh", true, null, getPossibleMysgitBinDirectories());
	}
	
	private List<String> getPossibleMysgitBinDirectories()
	{
		List<String> result = new LinkedList<String>();
		
		for (File root : File.listRoots())
		{
			result.add(FileUtils.buildPath(root, "Program Files", "Git", "bin", "sh.exe").getAbsolutePath());
			result.add(FileUtils.buildPath(root, "Program Files (x86)", "Git", "bin", "sh.exe").getAbsolutePath());
		}
		
		return result;
	}

	/*
	 * @see com.aptana.terminal.server.ProcessConfiguration#afterStart(com.aptana.terminal.server.ProcessWrapper)
	 */
	@Override
	public void afterStart(ProcessWrapper wrapper)
	{
	}

	/*
	 * @see com.aptana.terminal.server.ProcessConfiguration#beforeStart(com.aptana.terminal.server.ProcessWrapper, java.lang.ProcessBuilder)
	 */
	@Override
	public void beforeStart(ProcessWrapper wrapper, ProcessBuilder builder)
	{
	}

	/*
	 * @see com.aptana.terminal.server.ProcessConfiguration#getCommandLineArguments()
	 */
	@Override
	public List<String> getCommandLineArguments()
	{
		List<String> list = new ArrayList<String>();
		String command = (this._sh != null) ? this._sh : "sh";
		
		list.add("\"\\\"" + command + "\\\"  --login -i\""); //$NON-NLS-1$
		list.add("80x40"); //$NON-NLS-1$
		if (Platform.inDevelopmentMode() || Platform.inDebugMode()) {
			list.add("-show"); //$NON-NLS-1$
		}
		return list;
	}

	/*
	 * @see com.aptana.terminal.server.ProcessConfiguration#getPlatform()
	 */
	@Override
	public String getPlatform()
	{
		return Platform.OS_WIN32;
	}

	/*
	 * @see com.aptana.terminal.server.ProcessConfiguration#getProcessName()
	 */
	@Override
	public String getProcessName()
	{
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(REDTTY_EXE), null);
		
		return ResourceUtils.resourcePathToString(url);
	}

	/**
	 * @see com.aptana.terminal.server.ProcessConfiguration#isValid()
	 */
	@Override
	public boolean isValid()
	{
		return true;
	}

	/*
	 * @see com.aptana.terminal.server.ProcessConfiguration#setupEnvironment(java.util.Map)
	 */
	@Override
	public void setupEnvironment(Map<String, String> env)
	{
	}
}
