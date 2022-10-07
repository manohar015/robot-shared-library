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
                        env.ARGS=-Dsonar.sources=. 
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

            stage('Prepare Artifacts') {
                steps {
                    echo 'echo'
                }
            }

        }    // end of statges 
    }
}

// call is the default function which will be called when you call the fileName 