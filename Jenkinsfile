#! groovy
// Keep logs/reports/etc of last 15 builds, only keep build artifacts of last build
properties([
	buildDiscarder(logRotator(numToKeepStr: '15', artifactNumToKeepStr: '1')),
	// specify projects to allow to copy artifacts with a comma-separated list.
	copyArtifactPermission("/appcelerator-studio/titanium_studio/${env.BRANCH_NAME},../Pydev/${env.BRANCH_NAME},../studio3-ruby/${env.BRANCH_NAME},../studio3-php/${env.BRANCH_NAME},../studio3-rcp/${env.BRANCH_NAME}"),
])
// Set after copying upstream artifacts
// Tells maven where to assume a p2 repo holding the sftp libraries would be
def sftpURL = ''
def targetBranch = 'development'
def isPR = false

timestamps {
	// node('((linux && vncserver) || osx) && jdk') { // TODO: Re-enable choosin osx box?
	node('linux && vncserver && jdk') {
		stage('Checkout') {
			// checkout scm
			// Hack for JENKINS-37658 - see https://support.cloudbees.com/hc/en-us/articles/226122247-How-to-Customize-Checkout-for-Pipeline-Multibranch
			checkout([
				$class: 'GitSCM',
				branches: scm.branches,
				extensions: scm.extensions + [[$class: 'CleanCheckout'], [$class: 'CloneOption', honorRefspec: true, noTags: true, reference: '', shallow: true, depth: 30, timeout: 30]],
				userRemoteConfigs: scm.userRemoteConfigs
			])
			isPR = env.BRANCH_NAME.startsWith('PR-')
			if (isPR) {
				targetBranch = env.CHANGE_TARGET
			} else {
				targetBranch = env.BRANCH_NAME
			}
		}

		stage('Dependencies') {
			step([$class: 'CopyArtifact',
				filter: 'repository/',
				fingerprintArtifacts: true,
				projectName: "../libraries_com/${targetBranch}",
				target: 'libraries_com'])
			sftpURL = "file:${pwd()}/libraries_com/repository"
		} // stage('Dependencies')

		stage('Build') {
			withEnv(["PATH+MAVEN=${tool name: 'Maven 3.5.0', type: 'maven'}/bin"]) {
				// https://stackoverflow.com/questions/10463077/how-to-refer-environment-variable-in-pom-xml
				withCredentials([usernamePassword(credentialsId: 'aca99bee-0f1e-4fc5-a3da-3dfd73f66432', passwordVariable: 'STOREPASS', usernameVariable: 'ALIAS')]) {
					wrap([$class: 'Xvnc', takeScreenshot: false, useXauthority: true]) {
						try {
							timeout(30) {
								sh "mvn -Dsftp.p2.repo.url=${sftpURL} -Dmaven.test.failure.ignore=true -Djarsigner.keypass=${env.STOREPASS} -Djarsigner.storepass=${env.STOREPASS} -Djarsigner.keystore=${env.KEYSTORE} clean verify"
							}
						} finally {
							// record tests even if we failed
							junit 'tests/*/target/surefire-reports/TEST-*.xml'
						}
					} // xvnc
				} // withCredentials
			} // withEnv(maven)
			dir('releng/com.aptana.studio.update/target') {
				// To keep backwards compatability with existing build pipeline, rename to "dist"
				sh 'mv repository dist'
				archiveArtifacts artifacts: 'dist/**/*'
				def jarName = sh(returnStdout: true, script: 'ls dist/features/com.aptana.feature_*.jar').trim()
				def version = (jarName =~ /.*?_(.+)\.jar/)[0][1]
				currentBuild.displayName = "#${version}-${currentBuild.number}"
			}
			dir('releng/com.aptana.studio.test.update/target') {
				sh 'mv repository dist-tests'
				archiveArtifacts artifacts: 'dist-tests/**/*'
			}
		} // stage('Build')

		// If not a PR, trigger downstream builds for same branch
		if (!isPR) {
			build job: "appcelerator-studio/titanium_studio/${env.BRANCH_NAME}", wait: false
			build job: "../studio3-php/${env.BRANCH_NAME}", wait: false
			build job: "../studio3-ruby/${env.BRANCH_NAME}", wait: false
			build job: "../Pydev/${env.BRANCH_NAME}", wait: false
		}
	} // node
} // timestamps
