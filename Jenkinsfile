#! groovy
@Library('pipeline-build') _

timestamps() {
	node('linux && ant && eclipse && jdk && vncserver') {
		stage('Checkout') {
			// checkout scm
			// Hack for JENKINS-37658 - see https://support.cloudbees.com/hc/en-us/articles/226122247-How-to-Customize-Checkout-for-Pipeline-Multibranch
			checkout([
				$class: 'GitSCM',
				branches: scm.branches,
				extensions: scm.extensions + [
					[$class: 'CleanBeforeCheckout'],
					[$class: 'CloneOption', depth: 30, honorRefspec: true, noTags: true, reference: '', shallow: true, timeout: 30]
				],
				userRemoteConfigs: scm.userRemoteConfigs
			])
		}

		def librariesComRepo = "file://${env.WORKSPACE}/libraries-com/dist/"
		def studio3Repo = "file://${env.WORKSPACE}/dist/"

		buildPlugin {
			dependencies = ['libraries-com': 'aptana-studio/libraries_com']
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
			exclusionPattern = 'lib.org.chromium*.jar,**/tests/**/*,**/*Test*.*,**/Messages.*,com.aptana.*.tests*.jar,com.aptana.commandline.launcher_*.jar,com.aptana.documentation_*.jar,com.aptana.org.eclipse.tm.terminal_*.jar,com.aptana.swt.webkitbrowser*.jar,com.aptana.testing.*.jar,com.aptana.libraries_*.jar,com.aptana.jetty.*.jar,com.aptana.portablegit.win32_*.jar,com.aptana.scripting_*.jar,org.*.jar,mestevens.xcode.parser_*.jar,com.fasterxml.jackson*.jar'
		}

		// If not a PR, trigger downstream builds for same branch
		if (!env.BRANCH_NAME.startsWith('PR-')) {
			build job: "Appcelerator Studio/titanium_studio/${env.BRANCH_NAME}", wait: false
			build job: "Studio/studio3-php/${env.BRANCH_NAME}", wait: false
			build job: "Studio/studio3-ruby/${env.BRANCH_NAME}", wait: false
			build job: "Studio/Pydev/${env.BRANCH_NAME}", wait: false
		}
	} // end node
} //end timestamps
