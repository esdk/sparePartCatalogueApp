def prepareEnv() {
    def jdkHome = tool name: 'java8', type: 'jdk'
    env.PATH = "${jdkHome}/bin:${env.PATH}"
    env.JAVA_HOME = "${jdkHome}"
    jdkHome = null
}

def notify(String buildStatus) {
    // defaults
    def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
    def summary = "${subject} (${env.BUILD_URL})"
    def colorCode

    // build status evaluation
    if (buildStatus == 'SUCCESSFUL') {
        color = 'GREEN'
        colorCode = '#00FF00'
    } else {
        color = 'RED'
        colorCode = '#FF0000'
    }

    // send notification
    slackSend(channel: 'esdk-bot', color: colorCode, message: summary)
}

def onMaster(Closure run) {
    echo("Branch: $env.BRANCH_NAME")
    if ("master" == env.BRANCH_NAME) {
        run()
    } else {
        echo("only runs on master branch")
    }
}

node {
    timestamps {
        ansiColor('xterm') {
            prepareEnv()
            try {
                stage('Setup') {
                    checkout scm
                    sh '''
                        curl -sS -L "https://github.com/docker/compose/releases/download/1.9.0/docker-compose-$(uname -s)-$(uname -m)" -o docker-compose
                        chmod +x docker-compose 
                    '''
                    sh './initGradleProperties.sh'
                    withCredentials([usernamePassword(credentialsId: '82305355-11d8-400f-93ce-a33beb534089',
                            passwordVariable: 'MAVENPASSWORD', usernameVariable: 'MAVENUSER')]) {
                        sh '''
                            echo esdkSnapshotURL=https://registry.abas.sh/repository/abas.esdk.snapshots/ >> gradle.properties
                            echo esdkReleaseURL=https://registry.abas.sh/repository/abas.esdk.releases/ >> gradle.properties
                            echo nexusUser=$MAVENUSER >> gradle.properties
                            echo nexusPassword=$MAVENPASSWORD >> gradle.properties
                            echo "org.gradle.java.home=$JAVA_HOME" >> ../gradle.properties
                        '''
                    }
                    sh 'cat gradle.properties'
                }
                stage('Preparation') { // for display purposes
                    withCredentials([usernamePassword(credentialsId: '82305355-11d8-400f-93ce-a33beb534089',
                            passwordVariable: 'MAVENPASSWORD', usernameVariable: 'MAVENUSER')]) {
                        sh 'docker login intra.registry.abas.sh -u $MAVENUSER -p $MAVENPASSWORD'
                    }
                    sh "./docker-compose up -d"
                    sleep 30
                }
                stage('Installation') {
                    sh "./gradlew publishHomeDirJars --stacktrace"
                    sh "./gradlew fullInstall --stacktrace"
                }
                stage('Build') {
                    sh "./gradlew verify --stacktrace"
                }
                onMaster {
                    stage('Publish') {
                        sh "./gradlew publish --stacktrace"
                    }
                }
            } catch (any) {
                currentBuild.result = 'FAILURE'
                throw any
            } finally {
                sh '''
                    ./docker-compose stop || true
                    ./docker-compose rm -fv || true
                '''

                archiveArtifacts 'build/reports/**'

                def result = currentBuild.result ?: 'SUCCESSFUL'
                notify(result)
            }
        }
    }
}
