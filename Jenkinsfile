#! groovy
node('linux && ant && eclipse && jdk && vncserver') {
	try {
		def librariesComRepo = "file://${env.WORKSPACE}/libraries-com/dist/"
		def studio3Repo = "file://${env.WORKSPACE}/dist/"

		buildPlugin {
			dependencies = ['libraries-com': 'Studio/libraries_com']
			builder = 'com.aptana.feature.build'
			properties = ['libraries-com.p2.repo': librariesComRepo]
		}

		testPlugin {
			builder = 'com.aptana.studio.tests.build'
			properties = [
				'studio3.p2.repo': studio3Repo,
				'libraries-com.p2.repo': librariesComRepo,
				's3.accessKey': '${S3_ACCESS_KEY}',
				's3.secretAccessKey': '${S3_SECRET_ACCESS_KEY}',
				'ftp.host': '10.0.1.52',
				'ftp.username': 'ftp_test',
				'ftp.password': '${FTP_PASSWORD}',
				'ftp.path': '/home/ftp_test',
				'sftp.host': '10.0.1.52',
				'sftp.username': 'ftp_test',
				'sftp.password': '${FTP_PASSWORD}',
				'sftp.path': '/home/ftp_test',
				'ftps.host': 'app.brickftp.com',
				'ftps.username': 'ftp_test',
				'ftps.password': '${FTP_PASSWORD}',
				'ftps.path': '/',
				'ftp.supports.setmodtime': 'true',
				'ftp.supports.foldersetmodtime': 'false',
				'ftp.supports.permissions': 'false',
				'ftp.supports.changegroup': 'false',
				'ftps.supports.setmodtime': 'true',
				'ftps.supports.foldersetmodtime': 'false',
				'ftps.supports.changegroup': 'false',
				'ftps.supports.permissions': 'false'
			]
			classPattern = 'eclipse/plugins/com.aptana.parsing_*,eclipse/plugins/com.aptana.terminal_*,eclipse/plugins/com.aptana.git.core_*,eclipse/plugins'
			inclusionPattern = 'com.aptana.*.core_*.jar, com.aptana.browser_*.jar, com.aptana.build*.jar, com.aptana.configurations_*.jar, com.aptana.console_*.jar, com.aptana.core*.jar, com.aptana.debug*.jar, com.aptana.debug.*.jar, com.aptana.deploy*.jar, com.aptana.editor.*.jar, com.aptana.explorer_*.jar com.aptana.file*.jar, com.aptana.formatter.*.jar, com.aptana.git.*.jar, com.aptana.index.*.jar, com.aptana.jira.*.jar, com.aptana.js*.jar, com.aptana.parsing*.jar, com.aptana.portal.*.jar, com.aptana.preview*.jar, com.aptana.projects*.jar, com.aptana.samples.*.jar, com.aptana.scripting*.jar, com.aptana.syncing.*.jar, com.aptana.theme*.jar, com.aptana.ui*.jar, com.aptana.usage_*.jar, com.aptana.webserver.*.jar, com.aptana.workbench_*.jar'
			exclusionPattern = 'lib.org.chromium*.jar,**/tests/**/*.class,**/*Test*.class,**/Messages.class,com.aptana.*.tests*.jar,com.aptana.commandline.launcher_*.jar,com.aptana.documentation_*.jar,com.aptana.org.eclipse.tm.terminal_*.jar,com.aptana.swt.webkitbrowser*.jar,com.aptana.testing.*.jar,com.aptana.libraries_*.jar,com.aptana.jetty.*.jar,com.aptana.portablegit.win32_*.jar,com.aptana.scripting_*.jar'
		}

		// If not a PR, trigger downstream builds for same branch
		if (!env.BRANCH_NAME.startsWith('PR-')) {
			build job: "titanium-core-${env.BRANCH_NAME}", wait: false
			build job: "Studio/studio3-php/${env.BRANCH_NAME}", wait: false
			build job: "Studio/studio3-ruby/${env.BRANCH_NAME}", wait: false
			build job: "Studio/Pydev/${env.BRANCH_NAME}", wait: false
		}
	} catch (e) {
		// if any exception occurs, mark the build as failed
		currentBuild.result = 'FAILURE'
		//office365ConnectorSend(message: 'Build failed', status: currentBuild.result, webhookUrl: 'https://outlook.office.com/webhook/ba1960f7-fcca-4b2c-a5f3-095ff9c87b22@300f59df-78e6-436f-9b27-b64973e34f7d/JenkinsCI/5dcba6d96f54460d9264e690b26b663e/72931ee3-e99d-4daf-84d2-1427168af2d9')
		throw e
	} finally {
		step([$class: 'WsCleanup', notFailBuild: true])
	}
} // end node
