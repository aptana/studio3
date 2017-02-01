#!groovy

// take a target job to lookup the coverage values for
// Then record the values and set those as minimums for our Jacoco step!
def getCoverageMinimums(jobName, isPR) {
	// Grab the target build
	// if it doesn't exist, return all zeros as minimums
	def targetJob = manager.hudson.getItemByFullName(jobName)
	if (targetJob == null) {
		echo "Unable to get find target job ${jobName} to compare against, bailing out"
		return [0, 0, 0, 0, 0]
	}
	// Now get the latest build for that job
	// Result.SUCCESS
	def lastBuild = targetJob.getLastBuild()
	if (lastBuild == null) {
		echo "No build yet for ${jobName} to compare against, bailing out"
		return [0, 0, 0, 0, 0]
	}

	// If we're comparing a non-PR, we need to not count the active build!
	if (!isPR) {
		lastBuild = lastBuild.getPreviousSuccessfulBuild()
	} else if (lastBuild.getResult() != Result.SUCCESS) {
		 // if it is a PR, and target job's last build wasn't successful
		 // get the last successful one
		 lastBuild = lastBuild.getPreviousSuccessfulBuild()
	}
	if (lastBuild == null) {
		echo "No successful build yet for ${jobName} to compare against, bailing out"
		return [0, 0, 0, 0, 0]
	}
	def actions = lastBuild.getActions()
	def action = null
	for (int i = 0; i < actions.size(); i++) {
		if (actions[i].getUrlName() == 'jacoco') {
			action = actions[i]
			break
		}
	}
	if (action == null) {
		// no Jacoco coverage, so nothing to do!
		echo 'Unable to get Jacoco Build Action on build, so bailing out'
		return [0, 0, 0, 0, 0]
	}

	def values = []
	def metrics = ['Branch', 'Class', 'Instruction', 'Line', 'Method'] // TODO Add 'Complexity'?
	for (int i = 0; i < metrics.size(); i++) {
		def m = metrics[i]
		float percent = action."get${m}Coverage"().getPercentageFloat();
		echo "Observed ${m} coverage: ${percent}%"
		values << percent
	}

	return values
}

// Manually check if
def checkCoverageDrop(coverageFloats) {
	def actions = manager.build.getActions()
	def action = null
	for (int i = 0; i < actions.size(); i++) {
		if (actions[i].getUrlName() == 'jacoco') {
			action = actions[i]
			break
		}
	}
	if (action == null) {
		// no Jacoco coverage, so nothing to do!
		echo 'Unable to get Jacoco Build Action on build, so bailing out'
		return
	}

	def values = []
	def metrics = ['Branch', 'Class', 'Instruction', 'Line', 'Method'] // TODO Add 'Complexity'?
	for (int i = 0; i < metrics.size(); i++) {
		def m = metrics[i]
		float percent = action."get${m}Coverage"().getPercentageFloat();
		echo "Comparing coverage for ${m} - Target: ${coverageFloats[i]}%, Recorded: ${percent}%"
		if (Float.compare(percent, coverageFloats[i]) < 0) {
			manager.addErrorBadge("${m} Code Coverage dropped.")
			manager.createSummary('error.gif').appendText("<h3>${m} Code Coverage dropped - Target: ${coverageFloats[i]}%, Recorded: ${percent}%</h3>", false, false, false, 'red')
			manager.buildUnstable()
		}
	}
}

node('linux && ant && eclipse && jdk && vncserver') {
	try {
		def targetBranch = 'development'
		def minimums = []
		def isPR = false
		timestamps() {
			stage('Checkout') {
				checkout scm
				isPR = env.BRANCH_NAME.startsWith('PR-')
				if (!isPR) {
					targetBranch = env.BRANCH_NAME
				} else {
					targetBranch = env.CHANGE_TARGET
				}
				minimums = getCoverageMinimums("Studio/studio3/${targetBranch}", isPR)
			}

			// Copy over dependencies
			stage('Dependencies') {
				step([$class: 'CopyArtifact',
					filter: 'dist/',
					fingerprintArtifacts: true,
					projectName: "Studio/libraries_com/${targetBranch}",
					target: 'libraries-com'])
			}

			stage('Build') {
				env.PATH = "${tool name: 'Ant 1.9.2', type: 'ant'}/bin:${env.PATH}"
				writeFile file: 'override.properties', text: """scs.branch.name=${targetBranch}
workspace=${env.WORKSPACE}
deploy.dir=${env.WORKSPACE}/dist
buildDirectory=${env.WORKSPACE}
vanilla.eclipse=${env.ECLIPSE_4_4_HOME}
launcher.plugin=${env.ECLIPSE_4_4_LAUNCHER}
builder.plugin=${env.ECLIPSE_4_4_BUILDER}
configs=win32, win32, x86 & win32, win32, x86_64 & linux, gtk, x86 & linux, gtk, x86_64 & macosx, cocoa, x86 & macosx, cocoa, x86_64
libraries-com.p2.repo=file://${env.WORKSPACE}/libraries-com/dist/
"""
				timeout(10) {
					sh 'ant -propertyfile override.properties -f builders/com.aptana.feature.build/build.xml build'
				}

				dir('dist') {
					def zipName = sh(returnStdout: true, script: 'ls *.zip').trim()
					def version = (zipName =~ /^.*?\-(.+)\.zip/)[0][1]
					currentBuild.displayName = "#${version}-${currentBuild.number}"
				}
				archiveArtifacts artifacts: 'dist/**/*', excludes: 'dist/*.zip'

				// Try and clean up files left from first build step
				sh '''rm -rf $WORKSPACE/I.*
rm -rf 	$WORKSPACE/eclipse
rm -rf 	$WORKSPACE/assemble.*.xml
rm -rf 	$WORKSPACE/compile.*.xml
rm -rf 	$WORKSPACE/final*.properties
rm -rf 	$WORKSPACE/package.*.xml
rm -rf 	$WORKSPACE/transformedRepos
rm -rf 	$WORKSPACE/nestedJars
rm -rf 	$WORKSPACE/buildRepo
rm -rf 	$WORKSPACE/repoBase
rm -rf 	$WORKSPACE/src_plugins
mv $WORKSPACE/plugins $WORKSPACE/src_plugins
mkdir $WORKSPACE/plugins'''
			}

			stage('Test') {
				wrap([$class: 'Xvnc', takeScreenshot: false, useXauthority: true]) {
					wrap([$class: 'MaskPasswordsBuildWrapper']) {
						writeFile file: 'test-override.properties', text: """scs.branch.name=${targetBranch}
workspace=${env.WORKSPACE}
deploy.dir=${env.WORKSPACE}/dist-tests
buildDirectory=${env.WORKSPACE}
vanilla.eclipse=${env.ECLIPSE_4_4_HOME}
launcher.plugin=${env.ECLIPSE_4_4_LAUNCHER}
builder.plugin=${env.ECLIPSE_4_4_BUILDER}
test.timeout=1800000
useEclipseExe=true
instrument.code=true
studio3.p2.repo=file://${env.WORKSPACE}/dist
libraries-com.p2.repo=file://${env.WORKSPACE}/libraries-com/dist/
s3.accessKey=\${S3_ACCESS_KEY}
s3.secretAccessKey=\${S3_SECRET_ACCESS_KEY}
ftp.host=10.0.1.52
ftp.username=ftp_test
ftp.password=\${FTP_PASSWORD}
ftp.path=/home/ftp_test
sftp.host=10.0.1.52
sftp.username=ftp_test
sftp.password=\${FTP_PASSWORD}
sftp.path=/home/ftp_test
ftps.host=app.brickftp.com
ftps.username=ftp_test
ftps.password=\${FTP_PASSWORD}
ftps.path=/
ftp.supports.setmodtime=true
ftp.supports.foldersetmodtime=false
ftp.supports.permissions=false
ftp.supports.changegroup=false
ftps.supports.setmodtime=true
ftps.supports.foldersetmodtime=false
ftps.supports.changegroup=false
ftps.supports.permissions=false"""
						timeout(10) {
							sh 'ant -propertyfile test-override.properties -f builders/com.aptana.studio.tests.build/build.xml build test'
						} // end timeout
					} // end mask passwords
				} // end xvnc

				junit 'test-results/*.xml'
				archiveArtifacts artifacts: 'dist-tests/**/*,test-results/**/*,coverage-results/**/*,eclipse/junit-workspace/.metadata/.log,eclipse/npm-debug.log', excludes: 'dist-tests/*.zip'

				// FIXME Don't set max/min here and do compariosn ourselves?
				// Seems like sometimes when the value is the same (as an integer) it still gets marked
				// ass below threshold (likely due to decimal precision it doesn't expose?)
				step([$class: 'JacocoPublisher',
					changeBuildStatus: false,
					classPattern: 'eclipse/plugins/com.aptana.parsing_*,eclipse/plugins/com.aptana.terminal_*,eclipse/plugins/com.aptana.git.core_*,eclipse/plugins',
					exclusionPattern: 'lib.org.chromium*.jar,**/tests/**/*.class,**/*Test*.class,**/Messages.class,com.aptana.*.tests*.jar,com.aptana.commandline.launcher_*.jar,com.aptana.documentation_*.jar,com.aptana.org.eclipse.tm.terminal_*.jar,com.aptana.swt.webkitbrowser*.jar,com.aptana.testing.*.jar,com.aptana.libraries_*.jar,com.aptana.jetty.*.jar,com.aptana.portablegit.win32_*.jar,com.aptana.scripting_*.jar',
					execPattern: 'coverage-results/*.exec',
					inclusionPattern: 'com.aptana.*.core_*.jar, com.aptana.browser_*.jar, com.aptana.build*.jar, com.aptana.configurations_*.jar, com.aptana.console_*.jar, com.aptana.core*.jar, com.aptana.debug*.jar, com.aptana.debug.*.jar, com.aptana.deploy*.jar, com.aptana.editor.*.jar, com.aptana.explorer_*.jar com.aptana.file*.jar, com.aptana.formatter.*.jar, com.aptana.git.*.jar, com.aptana.index.*.jar, com.aptana.jira.*.jar, com.aptana.js*.jar, com.aptana.parsing*.jar, com.aptana.portal.*.jar, com.aptana.preview*.jar, com.aptana.projects*.jar, com.aptana.samples.*.jar, com.aptana.scripting*.jar, com.aptana.syncing.*.jar, com.aptana.theme*.jar, com.aptana.ui*.jar, com.aptana.usage_*.jar, com.aptana.webserver.*.jar, com.aptana.workbench_*.jar',
					maximumBranchCoverage: Integer.toString(Math.round(minimums[0])),
					maximumClassCoverage: Integer.toString(Math.round(minimums[1])),
					maximumInstructionCoverage: Integer.toString(Math.round(minimums[2])),
					maximumLineCoverage: Integer.toString(Math.round(minimums[3])),
					maximumMethodCoverage: Integer.toString(Math.round(minimums[4])),
					minimumBranchCoverage: Integer.toString(Math.round(minimums[0])),
					minimumClassCoverage: Integer.toString(Math.round(minimums[1])),
					minimumInstructionCoverage: Integer.toString(Math.round(minimums[2])),
					minimumLineCoverage: Integer.toString(Math.round(minimums[3])),
					minimumMethodCoverage: Integer.toString(Math.round(minimums[4])),
					sourcePattern: 'src_plugins/*/src'])
			} // end Test stage

			stage('Cleanup') {
				checkCoverageDrop(minimums)
			}

			// If not a PR, trigger downstream builds for same branch
			if (!env.BRANCH_NAME.startsWith('PR-')) {
				build job: "titanium-core-${env.BRANCH_NAME}", wait: false
				build job: "Studio/studio3-php/${env.BRANCH_NAME}", wait: false
				build job: "Studio/studio3-ruby/${env.BRANCH_NAME}", wait: false
				build job: "Studio/Pydev/${env.BRANCH_NAME}", wait: false
			}
		} // end timestamps
	} catch (e) {
		// if any exception occurs, mark the build as failed
		currentBuild.result = 'FAILURE'
		office365ConnectorSend(message: 'Build failed', status: currentBuild.result, webhookUrl: 'https://outlook.office.com/webhook/ba1960f7-fcca-4b2c-a5f3-095ff9c87b22@300f59df-78e6-436f-9b27-b64973e34f7d/JenkinsCI/5dcba6d96f54460d9264e690b26b663e/72931ee3-e99d-4daf-84d2-1427168af2d9')
		throw e
	} finally {
		step([$class: 'WsCleanup', notFailBuild: true])
	}
} // end node
