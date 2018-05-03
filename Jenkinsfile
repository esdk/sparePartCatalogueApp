@Library('esdk-jenkins-lib@master') _

node {
	timestamps {
		ansiColor('xterm') {
			try {
				properties([parameters([
						string(name: 'ESDK_VERSION', defaultValue: '', description: 'Version of ESDK to use (if not same as project version, project version will be updated as well)'),
						string(name: 'BUILD_USER_PARAM', defaultValue: 'anonymous', description: 'User who triggered the build implicitly (through a commit in another project)'),
					])
				])
				stage('Setup') {
					checkout scm
					sh "git reset --hard origin/$BRANCH_NAME"
					sh 'git clean -fdx'
					prepareEnv()
					rmDirInMavenLocal '​de/abas/esdk'
					initGradleProps()
				}
				stage('Set version') {
					updateEssentialsAppVersion(params.ESDK_VERSION, 'gradle.properties.template', params.BUILD_USER_PARAM, 'github.com/Tschasmine/sparePartCatalogueApp.git')
					initGradleProps()
				}
				stage('Preparation') { // for display purposes
					withCredentials([usernamePassword(credentialsId: '82305355-11d8-400f-93ce-a33beb534089',
							passwordVariable: 'MAVENPASSWORD', usernameVariable: 'MAVENUSER')]) {
						shDocker('login partner.registry.abas.sh -u $MAVENUSER -p $MAVENPASSWORD')
					}
					shDockerComposeUp()
					installLanguages()
				}
				stage('Installation') {
					shGradle("checkPreconditions")
					shGradle("fullInstall")
				}
				stage('Build') {
					shGradle("verify")
				}
				onMaster {
					stage('Publish') {
						shGradle("createAppJar")
						shGradle("publish")
					}
				}
			} catch (any) {
				any.printStackTrace()
				currentBuild.result = 'FAILURE'
				throw any
			} finally {
				shDockerComposeCleanUp()

				junit allowEmptyResults: true, testResults: 'build/test-results/**/*.xml'
				archiveArtifacts 'build/reports/**'

				slackNotify(currentBuild.result)
			}
		}
	}
}

def installLanguages() {
	sh "docker exec -t erp sh -c 'cd /abas/erp && eval \$(sh denv.sh) && echo \"KONFIG;y;y\" | edpimport.sh -psy -u -b 12:18 -f such,bse,bsa'"
}
