package com.aptana.deploy.preferences;

public interface IPreferenceConstants
{
	public enum DeployType
	{
		HEROKU, FTP, CAPISTRANO;
	}

	public static final String AUTO_SYNC = "AutoSyncChangesWithRemote"; //$NON-NLS-1$
	public static final String HEROKU_AUTO_PUBLISH = "HerokuAutoPublish"; //$NON-NLS-1$
	public static final String PROJECT_DEPLOY_TYPE = "ProjectDeployType"; //$NON-NLS-1$
	public static final String PROJECT_DEPLOY_ENDPOINT = "ProjectDeployEndpoint"; //$NON-NLS-1$
}
