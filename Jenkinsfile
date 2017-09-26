@Library('esdk-jenkins-lib@master') _

node {
	timestamps {
		ansiColor('xterm') {
			try {
				stage('Setup') {
					checkout scm
					sh 'git clean -fdx'
					prepareEnv()
					rmDirInMavenLocal '​de/abas/esdk'
					shInstallDockerCompose()
					initGradleProps()
					showGradleProps()
				}
				stage('Preparation') { // for display purposes
					withCredentials([usernamePassword(credentialsId: '82305355-11d8-400f-93ce-a33beb534089',
							passwordVariable: 'MAVENPASSWORD', usernameVariable: 'MAVENUSER')]) {
						shDocker('login intra.registry.abas.sh -u $MAVENUSER -p $MAVENPASSWORD')
					}
					shDockerComposeUp()
					sleep 30
				}
				stage('Installation') {
					shGradle("checkPreconditions")
					shGradle("publishHomeDirJars")
					shGradle("fullInstall")
				}
				stage('Build') {
					shGradle("verify")
				}
				onMaster {
					stage('Publish') {
						shGradle("publish")
					}
				}
			} catch (any) {
				any.printStackTrace()
				currentBuild.result = 'FAILURE'
				throw any
			} finally {
				shDockerComposeCleanUp()

				archiveArtifacts 'build/reports/**'

				slackNotify(currentBuild.result)
			}
		}
	}
}
