package com.aptana.git.core.model;

interface GitEnv
{
	/**
	 * Environment variable we check for possible location of git binary (when we have no pref value or it's pointing to
	 * a bad version).
	 */
	static final String GIT_PATH = "GIT_PATH";

	/**
	 * Env var to hold the author name of the original commit when amending.
	 */
	static final String GIT_AUTHOR_NAME = "GIT_AUTHOR_NAME";

	/**
	 * Env var to hold the author email of the original commit when amending.
	 */
	static final String GIT_AUTHOR_EMAIL = "GIT_AUTHOR_EMAIL";

	/**
	 * Env var to hold the timestamp of the original commit when amending.
	 */
	static final String GIT_AUTHOR_DATE = "GIT_AUTHOR_DATE";

	/**
	 * Location of .git for repo. Used for launches of hooks.
	 */
	static final String GIT_DIR = "GIT_DIR";

	/**
	 * Location of .git/index file for repo. Used for launches of hooks.
	 */
	static final String GIT_INDEX_FILE = "GIT_INDEX_FILE";
}
