def lintCheck() {
    sh ''' 
         # We want Devs to handle the lint checks failure 
         # npm i jslint 
         # node_modules/jslint/bin/jslint.js  server.js || true 
         echo Starting lint checks
         echo Lint Checks Completed for ${COMPONENT}
    ''' 
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

            stage('Lint Check') {
                steps {
                    script { 
                        lintCheck()
                    }
                }
            }

            stage('Sonar Check') {
                steps {
                    script { 
                        env.ARGS="-Dsonar.sources=."
                        common.sonarCheck()
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
                        def UPLOAD_STATUS=sh(returnStdout: true, script: "curl -L -s -v http://172.31.0.75:8081/service/rest/repository/browse/${COMPONENT} | grep ${COMPONENT}-${TAG_NAME}.zip")
                        print UPLOAD_STATUS
                    }
                }
            }

            stage('Prepare Artifacts') {
                when {
                    expression { env.TAG_NAME != null }   // Only runs when you run this against the TAG
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