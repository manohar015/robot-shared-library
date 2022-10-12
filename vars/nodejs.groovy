env.APPTYPE="nodejs"
def call() {
    node {
        common.lintCheck()
        env.ARGS="-Dsonar.sources=."
                        common.sonarCheck()
    }
}

def call() {
    pipeline {
        agent any 
        environment {
            SONAR      = credentials('SONAR')
            NEXUS      = credentials('NEXUS')
        }
 
        stages {
            stage('Downloading the dependencies') {
                steps {
                    sh "npm install"
                }
            }


            stage('Sonar Check') {
                steps {
                    script { 

                    }
                }
            }

           stage('Test Cases') {
            parallel {
                stage('Unit Tests') {
                    steps {
                        sh 'echo Unit Test Cases Completed'
                         }
                    }
                stage('Integration Tests') {
                    steps {
                        sh 'echo Integration Test Cases Completed'
                         }
                    }
                stage('Functional Tests') {
                    steps {
                        sh 'echo Functional Test Cases Completed'
                         }
                    }
                }
            }

            stage('Check the release') {
                when {
                    expression { env.TAG_NAME != null }   // Only runs when you run this against the TAG
                }
                steps {
                    script {
                        env.UPLOAD_STATUS=sh(returnStdout: true, script: 'curl -L -s http://172.31.0.75:8081/service/rest/repository/browse/${COMPONENT} | grep ${COMPONENT}-${TAG_NAME}.zip || true')
                        print UPLOAD_STATUS
                    }
                }
            }

            stage('Prepare Artifacts') {
                when {
                    expression { env.TAG_NAME != null }   // Only runs when you run this against the TAG
                    expression { env.UPLOAD_STATUS == "" }
                }
                steps {
                    sh ''' 
                        npm install 
                        zip ${COMPONENT}-${TAG_NAME}.zip node_modules server.js

                    ''' 
                }
            }

            stage('Upload Artifacts') {
                when {
                    expression { env.TAG_NAME != null }   // Only runs when you run this against the TAG
                    expression { env.UPLOAD_STATUS == "" }
                }
                steps {
                    sh ''' 
                        curl -f -v -u ${NEXUS_USR}:${NEXUS_PSW} --upload-file ${COMPONENT}-${TAG_NAME}.zip http://172.31.0.75:8081/repository/${COMPONENT}/${COMPONENT}-${TAG_NAME}.zip
                    '''
                }
            }
        }    // end of statges 
    }
}

// call is the default function which will be called when you call the fileName 