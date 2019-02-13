@Library('esdk-jenkins-lib@master') _

/**
 * This is the Jenkinsfile used by the ESDK team to build, test and release this demo app.
 *
 * It makes use of an internal Jenkins Pipeline library (esdk-jenkins-lib) and is not intended to be part of the demo.
 * To get a more simple demo Jenkinsfile for building and testing your app against an abas ERP docker container,
 * please use the
 *
 *  ESDK Project Builder: https://dev.abas-essentials-sdk.com/#/project-builder
 *
 */

def version
def releaseVersion
def prefix = 'spare'

timestamps {
	ansiColor('xterm') {
		node {
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
					version = readVersion()
					releaseVersion = version.split("-")[0]
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
					parallel(
							languages: {
								installLanguages()
							},
							nexus: {
								waitForNexus()
							}
					)
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
						shGradle("publish -x fullInstall -x createApp -x jar -x copyIS -x createWorkdirs -x processResources -x importEnums -x importNamedTypes")
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

		onMaster {

			def readyForRelease = askForReadyForRelease(releaseVersion, 'ESDK', prefix)

			node {
				try {
					milestone label: "release-started"
					stage("Prepare Release") {
						checkout scm
						sh "git reset --hard origin/$BRANCH_NAME"
						sh 'git clean -fdx'
						prepareEnv()
						rmDirInMavenLocal '​de/abas/esdk'
						initGradleProps()
						shGitCheckoutReleaseBranch()
						withCredentials([usernamePassword(credentialsId: '82305355-11d8-400f-93ce-a33beb534089',
								passwordVariable: 'MAVENPASSWORD', usernameVariable: 'MAVENUSER')]) {
							shDocker('login partner.registry.abas.sh -u $MAVENUSER -p $MAVENPASSWORD')
						}
						shDockerComposeUp()
						parallel(
								languages: {
									installLanguages()
								},
								nexus: {
									waitForNexus()
								}
						)
					}
					stage("Release") {
						justReplace(version, releaseVersion, 'gradle.properties.template')
						justReplace(version, releaseVersion, 'gradle.properties')
						String devVersion = readyForRelease.devVersion + '-SNAPSHOT'
						printReleaseInfo(releaseVersion, devVersion, readyForRelease.answer)
						String releaseText = "Releasing ${releaseVersion}\nnew dev version ${devVersion}"
						currentBuild.description = releaseText + '\nrelease commit, publishing artifacts...'

						shGradle("publish")
						shGradle("packAbasApp -x createAppJar")
						def abasApp = sh returnStdout: true, script: "ls build/abas-app/ | grep 'abasApp-$releaseVersion'"
						abasApp = abasApp.trim()
						withAWS(credentials: 'e4ec24aa-35e1-4650-a4bd-6d9b06654e9b', region: "us-east-1") {
							s3Upload(
									bucket: "abas-apps",
									file: "build/abas-app/$abasApp",
									path: "sparePartCatalogueApp-abasApp-${releaseVersion}.zip",
									pathStyleAccessEnabled: true,
									cacheControl: 'max-age=0',
									acl: 'Private'
							)
						}
						build job: 'esdk/abasAppTestBucketScan', parameters: [string(name: 'INSTALLER_VERSION', value: "$version")], wait: false

						shGitCommitRelease("gradle.properties.template", releaseVersion, readyForRelease.answer, BUILD_ID)
						justReplace(releaseVersion, devVersion, 'gradle.properties.template')
						shGitCommitSnapshot("gradle.properties.template", devVersion, readyForRelease.answer)

						bitbucketSshAgent {
							shGitPushIntoMaster("git@bitbucket.org:abascloud/abas-essentials-sdk.git", releaseVersion)
						}

						keepThisBuildForever()
						markJiraVersionReleased(readyForRelease.releaseVersion)
						createJiraVersion(readyForRelease.devVersion, "ESDK", prefix)
						currentBuild.description = "Release: ${releaseVersion} \nDev Version: ${devVersion}"
					}
				} catch (any) {
					any.printStackTrace()
					currentBuild.result = 'FAILURE'
					throw any
				} finally {
					shDockerComposeCleanUp()

					slackNotify(currentBuild.result)
				}
			}

		}
	}
}

def installLanguages() {
	sh "docker exec -t erp sh -c 'cd /abas/erp && eval \$(sh denv.sh) && echo \"KONFIG;y;y\" | edpimport.sh -psy -u -b 12:18 -f such,bse,bsa'"
}
