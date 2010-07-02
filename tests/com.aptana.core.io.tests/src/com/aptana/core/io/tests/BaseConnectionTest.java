/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.io.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.ide.core.io.ConnectionContext;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;

/**
 * @author Max Stepanov
 */
public abstract class BaseConnectionTest extends TestCase
{
	protected boolean supportsSetModificationTime = false;
	protected boolean supportsChangeGroup = false;
	protected boolean supportsChangePermissions = false;

	protected static final String TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse nunc tellus, condimentum quis luctus fermentum, tincidunt eget dui. Sed bibendum iaculis ligula, fringilla ullamcorper justo ullamcorper non. Curabitur tristique mi a magna vestibulum fermentum. Praesent sed neque feugiat purus egestas tristique. Sed non nisi velit. Maecenas placerat, nisi quis iaculis porta, nisi mauris facilisis est, at rutrum lacus sem non ante. Morbi et cursus nibh. Aliquam tincidunt urna quis quam semper ut congue est auctor. Curabitur malesuada, diam ut congue elementum, orci eros rhoncus felis, vel elementum felis velit id eros. Quisque eros diam, malesuada nec tincidunt eget, gravida iaculis tortor. Donec sollicitudin ultricies ante ac facilisis. In egestas malesuada erat id vehicula.\n" + //$NON-NLS-1$
			"Integer non urna nunc, et rhoncus eros. Suspendisse tincidunt laoreet enim vel pretium. Nam bibendum sodales risus nec adipiscing. Pellentesque fringilla interdum odio posuere consectetur. Nullam venenatis augue sed felis tempus eu posuere quam facilisis. Pellentesque commodo rutrum bibendum. Ut sit amet sapien in purus vestibulum sodales. Integer pharetra mi in dui auctor in tristique erat malesuada. Integer nec ipsum quam. Quisque non enim et quam consequat mollis id ac sem. Nunc ut elit ac odio adipiscing pretium vel eget mauris. Aenean diam diam, porttitor sit amet lobortis a, accumsan at ante. Phasellus ut nulla enim. In nec diam magna. In molestie vulputate viverra. Etiam at justo tellus, sed rutrum erat.\r\n" //$NON-NLS-1$
			+ "Duis consectetur ornare ante, sit amet ultricies leo aliquam vitae. In fermentum nisi non dolor viverra non hendrerit nulla malesuada. Mauris adipiscing aliquet fringilla. Curabitur porttitor tristique massa, et semper nulla semper et. Phasellus a ipsum eu lectus pulvinar aliquam eget viverra velit. Sed commodo ultrices pulvinar. In at felis sollicitudin lorem semper scelerisque. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin vel purus id odio malesuada gravida. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Quisque metus mi, eleifend consectetur varius vitae, euismod eget nulla. Morbi justo felis, accumsan vel tempor non, rutrum at augue. Curabitur nulla lorem, ultricies a lobortis in, semper vitae diam. Pellentesque nec orci non turpis dignissim mollis. Quisque quis sapien vitae ligula iaculis dapibus sed at quam. Nullam ut nisl id eros sagittis rutrum a vitae risus. Suspendisse lacinia lacinia rutrum. Fusce molestie pellentesque dapibus. Quisque eu orci dolor, eget venenatis velit.\n" //$NON-NLS-1$
			+ "Nam rhoncus gravida ultrices. Maecenas hendrerit diam pharetra mauris commodo eleifend. Etiam ullamcorper aliquet arcu, sit amet luctus risus scelerisque at. Praesent nibh eros, rutrum in imperdiet eget, dignissim ornare nisl. Fusce sollicitudin, turpis id volutpat tincidunt, diam nibh euismod eros, eget tempor justo nulla ut magna. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Vivamus eu neque ac ante varius imperdiet. Vestibulum blandit neque lacus, a suscipit mi. Maecenas aliquet, lorem ut interdum bibendum, velit tellus feugiat quam, non posuere leo justo eget ante. Aliquam mattis augue est, et malesuada libero. Suspendisse nisl tellus, tempus sit amet luctus quis, vulputate eu turpis. Morbi lobortis vulputate odio at faucibus. Cras ut nisi ipsum."; //$NON-NLS-1$
	protected static final byte[] BYTES;

	static
	{
		BYTES = new byte[65536 + 20];
		for (int i = 0; i < BYTES.length; ++i)
		{
			BYTES[i] = (byte) i;
		}
	}

	protected IConnectionPoint cp;
	protected IPath testPath;
	private static Properties cachedProperties;

	protected static final Properties getConfig()
	{
		if (cachedProperties == null)
		{
			cachedProperties = new Properties();
			String propertiesFile = System.getenv("junit.properties");
			if (propertiesFile != null && new File(propertiesFile).length() > 0)
			{
				try
				{
					cachedProperties.load(new FileInputStream(propertiesFile));
				}
				catch (IOException ignore)
				{
				}
			}
		}
		return cachedProperties;
	}

	@Override
	protected void setUp() throws Exception
	{
		ConnectionContext context = new ConnectionContext();
		context.put(ConnectionContext.COMMAND_LOG, System.out);
		CoreIOPlugin.setConnectionContext(cp, context);

		testPath = Path.ROOT.append(getClass().getSimpleName() + System.currentTimeMillis());
		IFileStore fs = cp.getRoot().getFileStore(testPath);
		assertNotNull(fs);
		fs.mkdir(EFS.NONE, null);
		cp.disconnect(null);
		assertFalse(cp.isConnected());
	}

	@Override
	protected void tearDown() throws Exception
	{
		try
		{
			IFileStore fs = cp.getRoot().getFileStore(testPath);
			if (fs.fetchInfo().exists())
			{
				fs.delete(EFS.NONE, null);
				assertFalse(fs.fetchInfo().exists());
			}
		}
		finally
		{
			try
			{
				if (cp.isConnected())
				{
					cp.disconnect(null);
				}
			}
			finally
			{
				cp = null;
				testPath = null;
				super.tearDown();
			}
		}
	}
}
