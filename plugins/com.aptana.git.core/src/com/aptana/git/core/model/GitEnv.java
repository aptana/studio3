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
package com.aptana.git.core.model;

interface GitEnv
{
	/**
	 * Environment variable we check for possible location of git binary (when we have no pref value or it's pointing to
	 * a bad version).
	 */
	static final String GIT_PATH = "GIT_PATH"; //$NON-NLS-1$

	/**
	 * Env var to hold the author name of the original commit when amending.
	 */
	static final String GIT_AUTHOR_NAME = "GIT_AUTHOR_NAME"; //$NON-NLS-1$

	/**
	 * Env var to hold the author email of the original commit when amending.
	 */
	static final String GIT_AUTHOR_EMAIL = "GIT_AUTHOR_EMAIL"; //$NON-NLS-1$

	/**
	 * Env var to hold the timestamp of the original commit when amending.
	 */
	static final String GIT_AUTHOR_DATE = "GIT_AUTHOR_DATE"; //$NON-NLS-1$

	/**
	 * Location of .git for repo. Used for launches of hooks.
	 */
	static final String GIT_DIR = "GIT_DIR"; //$NON-NLS-1$

	/**
	 * Location of .git/index file for repo. Used for launches of hooks.
	 */
	static final String GIT_INDEX_FILE = "GIT_INDEX_FILE"; //$NON-NLS-1$
}
