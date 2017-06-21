@Library('esdk-jenkins-lib@ESDK-198-refactor-jenkinsfiles-of-all-es') _
import de.abas.esdk.jenkins.Common

Common lib = new Common()

node {
	timestamps {
		ansiColor('xterm') {
			try {
				stage('Setup') {
					lib.prepareEnv()
					checkout scm
					lib.shInstallDockerCompose()
					lib.initGradleProps()
					lib.showGradleProps()
				}
				stage('Preparation') { // for display purposes
					withCredentials([usernamePassword(credentialsId: '82305355-11d8-400f-93ce-a33beb534089',
							passwordVariable: 'MAVENPASSWORD', usernameVariable: 'MAVENUSER')]) {
						lib.shDocker('login intra.registry.abas.sh -u $MAVENUSER -p $MAVENPASSWORD')
					}
					lib.shDockerComposeUp()
					sleep 30
				}
				stage('Installation') {
					lib.shGradle("publishHomeDirJars")
					lib.shGradle("fullInstall")
				}
				stage('Build') {
					lib.shGradle("verify")
				}
				lib.onMaster {
					stage('Publish') {
						lib.shGradle("publish")
					}
				}
			} catch (any) {
				currentBuild.result = 'FAILURE'
				throw any
			} finally {
				lib.shDockerComposeCleanUp()

				archiveArtifacts 'build/reports/**'

				lib.slackNotify(currentBuild.result)
			}
		}
	}
}
