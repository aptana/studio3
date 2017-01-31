#!groovy

def checkCoverageDrop() {
	if (manager.logContains('Build step \'Record JaCoCo coverage report\' changed build result to FAILURE')) {
		manager.addErrorBadge('Code Coverage dropped.')
		manager.createSummary('error.gif').appendText('<h1>Code Coverage dropped</h1>', false, false, false, 'red')
	}
}

def ratchetCoverageThresholds() {
	// Grab the API for Jacoco
	def b = manager.build
	def actions = b.getActions()

	// Can't use find unless I move it out to @NonCPS method
	def action = null
	for (int i =0; i < actions.size(); i++) {
		if (actions[i].getUrlName() == 'jacoco') {
			action = actions[i]
			break
		}
	}
	// def action = actions.find { it.getUrlName() == 'jacoco' }
	if (action == null) {
		// no Jacoco coverage, so nothing to do!
		echo 'Unable to get Jacoco Build Action on build, so bailing out'
		return
	}

	boolean changed = false;
	// Now let's set the new threshold minimums to last value!
	// Only set these values if the observed percent is _higher_ than the existing minimum threshold!
	def thresholds = action.getThresholds();

	def metrics = ['Line', 'Class', 'Method', 'Instruction', 'Branch'] // TODO Add 'Complexity'?
	for (int i = 0; i < metrics.size(); i++) {
		def m = metrics[i]
		int percent = action."get${m}Coverage"().getPercentage();
		echo "Observed ${m} coverage: ${percent}%"
		if (percent > thresholds."getMin${m}"()) {
			echo "Increasing minimum threshold to observed value for ${m} coverage"
			manager.addInfoBadge("Increased ${m} coverage threshold to ${percent}%")
			thresholds."setMin${m}"(percent);
			changed = true;
		}
	}

	// Only if we have updated a value should we do anything...
	if (changed) {
		def publishers = b.getProject().publishersList;
		def jacocoPublisher = null
		// Can't use find unless I move it out to @NonCPS method
		for (int i = 0; i < publishers.size(); i++) {
			if (publishers[i].getDescriptor().getId() == 'hudson.plugins.jacoco.JacocoPublisher') {
				jacocoPublisher = publishers[i]
				break
			}
		}
		// publishers.find { it.getDescriptor().getId() == 'hudson.plugins.jacoco.JacocoPublisher' }

		// We need to replace the publisher with a new instance that has updated minimums
		// HACK to work around bug in plugin where we don't have access to plugin classes.
		def c = Class.forName('hudson.plugins.jacoco.JacocoPublisher', true, manager.hudson.getPluginManager().uberClassLoader)
		def constructor = c.getConstructor( [ String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, boolean ] as Class[] )
		// make a new version of the coverage publisher with updated minimums
		def newPub = constructor.newInstance(jacocoPublisher.getExecPattern(), jacocoPublisher.getClassPattern(), jacocoPublisher.getSourcePattern(),
				jacocoPublisher.getInclusionPattern(), jacocoPublisher.getExclusionPattern(), Integer.toString(thresholds.getMinInstruction()),
				Integer.toString(thresholds.getMinBranch()), Integer.toString(thresholds.getMinComplexity()), Integer.toString(thresholds.getMinLine()),
				Integer.toString(thresholds.getMinMethod()), Integer.toString(thresholds.getMinClass()), Integer.toString(thresholds.getMinInstruction()), Integer.toString(thresholds.getMinBranch()),
				Integer.toString(thresholds.getMinComplexity()), Integer.toString(thresholds.getMinLine()), Integer.toString(thresholds.getMinMethod()), Integer.toString(thresholds.getMinClass()),
				jacocoPublisher.isChangeBuildStatus())
		// Now remove the old version and replace with the new publisher with updated minimums
		publishers.replace(jacocoPublisher, newPub); // remove old and replace with new
		b.getProject().save(); // persist the change
	}
}

// Compare code coverage results to the target branch and ensure we didn't drop any
def compareCoverage(jobName) {
	// Grab the API for Jacoco
	def b = manager.build
	def actions = b.getActions()

	// Can't use #find unless I move it out to @NonCPS method
	// def action = null
	for (int i =0; i < actions.size(); i++) {
		if (actions[i].getUrlName() == 'jacoco') {
			action = actions[i]
			break
		}
	}
	// def action = actions.find { it.getUrlName() == 'jacoco' }
	if (action == null) {
		// no Jacoco coverage, so nothing to do!
		echo 'Unable to get Jacoco Build Action on build, so bailing out'
		return
	}

	// Now we need to grab the last values from the target job
	echo "Grabbing configuration of job: ${jobName}"
	def targetJob = manager.hudson.getItemByFullName(jobName)
	if (targetJob == null) {
		echo "Unable to get find target job ${jobName} to compare against, bailing out"
		return
	}

	def jacocoPublisher = null
	for (int i =0; i < targetJob.publishersList.size(); i++) {
		if (targetJob.publishersList[i].getDescriptor().getId() == 'hudson.plugins.jacoco.JacocoPublisher') {
			jacocoPublisher = targetJob.publishersList[i]
			break
		}
	}
	// def jacocoPublisher = targetJob.publishersList.find { it.getDescriptor().getId() == 'hudson.plugins.jacoco.JacocoPublisher' }
	if (jacocoPublisher == null) {
		echo 'Unable to get Jacoco Publisher on target merge build to grab thresholds, so bailing out'
		return
	}

	def summary = manager.createSummary('error.gif')
	boolean changed = false

	def metrics = ['Line', 'Class', 'Method', 'Instruction', 'Branch'] // TODO Add 'Complexity'?
	for (int i = 0; i < metrics.size(); i++) {
		def m = metrics[i]
		int percent = action."get${m}Coverage"().getPercentage();
		echo "Observed ${m} coverage: ${percent}%"
		int min = Integer.parseInt(jacocoPublisher."getMinimum${m}Coverage"())
		if (percent < min) {
			String msg = "${m} coverage (${percent}%) below target branch's threshold (${min}%)"
			echo msg
			summary.appendText("<p>${msg}</p>", false)
			manager.addErrorBadge(msg)
			manager.buildFailure()
			changed = true;
		}
	}

	if (!changed) {
		manager.removeSummaries()
	}
}

node('linux && ant && eclipse && jdk && vncserver') {
	try {
		def targetBranch = 'development'
		timestamps() {
			stage('Checkout') {
				checkout scm
				if (!env.BRANCH_NAME.startsWith('PR-')) {
					targetBranch = env.BRANCH_NAME
				} else {
					targetBranch = env.CHANGE_TARGET // should be the target branch for a PR!
				}
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
				step([$class: 'JacocoPublisher',
					changeBuildStatus: true,
					classPattern: 'eclipse/plugins/com.aptana.parsing_*,eclipse/plugins/com.aptana.terminal_*,eclipse/plugins/com.aptana.git.core_*,eclipse/plugins',
					exclusionPattern: 'lib.org.chromium*.jar,**/tests/**/*.class,**/*Test*.class,**/Messages.class,com.aptana.*.tests*.jar,com.aptana.commandline.launcher_*.jar,com.aptana.documentation_*.jar,com.aptana.org.eclipse.tm.terminal_*.jar,com.aptana.swt.webkitbrowser*.jar,com.aptana.testing.*.jar,com.aptana.libraries_*.jar,com.aptana.jetty.*.jar,com.aptana.portablegit.win32_*.jar,com.aptana.scripting_*.jar',
					execPattern: 'coverage-results/*.exec',
					inclusionPattern: 'com.aptana.*.core_*.jar, com.aptana.browser_*.jar, com.aptana.build*.jar, com.aptana.configurations_*.jar, com.aptana.console_*.jar, com.aptana.core*.jar, com.aptana.debug*.jar, com.aptana.debug.*.jar, com.aptana.deploy*.jar, com.aptana.editor.*.jar, com.aptana.explorer_*.jar com.aptana.file*.jar, com.aptana.formatter.*.jar, com.aptana.git.*.jar, com.aptana.index.*.jar, com.aptana.jira.*.jar, com.aptana.js*.jar, com.aptana.parsing*.jar, com.aptana.portal.*.jar, com.aptana.preview*.jar, com.aptana.projects*.jar, com.aptana.samples.*.jar, com.aptana.scripting*.jar, com.aptana.syncing.*.jar, com.aptana.theme*.jar, com.aptana.ui*.jar, com.aptana.usage_*.jar, com.aptana.webserver.*.jar, com.aptana.workbench_*.jar',
					maximumBranchCoverage: '0',
					maximumClassCoverage: '0',
					maximumInstructionCoverage: '0',
					maximumLineCoverage: '0',
					maximumMethodCoverage: '0',
					minimumBranchCoverage: '0',
					minimumClassCoverage: '0',
					minimumInstructionCoverage: '0',
					minimumLineCoverage: '0',
					minimumMethodCoverage: '0',
					sourcePattern: 'src_plugins/*/src'])
			} // end Test stage

			stage('Cleanup') {
				checkCoverageDrop()
				// if (!env.BRANCH_NAME.startsWith('PR-')) {
					ratchetCoverageThresholds()
				// } else {
					// compareCoverage("Studio/studio3/${env.CHANGE_TARGET}")
				// }
				// TODO Clean up after Jacoco?
			}

			// If not a PR, trigger downstream builds for same branch
			if (!env.BRANCH_NAME.startsWith('PR-')) {
				build job: "titanium-core-${env.BRANCH_NAME}", wait: false
				build job: "studio3-php-${env.BRANCH_NAME}", wait: false
				build job: "studio3-ruby-${env.BRANCH_NAME}", wait: false
				build job: "studio3-pydev-${env.BRANCH_NAME}", wait: false
				build job: "titanium-openshift-${env.BRANCH_NAME}", wait: false
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
