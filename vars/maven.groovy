def lintCheck() {
    sh ''' 
         echo Starting lint checks ${COMPONENT}
         mvn checkstyle:check || true                        # lint checks
         echo Lint Checks Completed for ${COMPONENT}
       
       ''' 
}

def call() {
    pipeline {
        agent any 
        stages {
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
                        mvn clean compile 
                        env.ARGS="-Dsonar.projectKey=target/"
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
        }    // end of statges 
    }
}

// call is the default function which will be called when you call the fileName 