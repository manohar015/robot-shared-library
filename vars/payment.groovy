def lintCheck() {
    sh ''' 
         echo Starting lint checks ${COMPONENT}
         # pylint *.py           # lint checks
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
        }    // end of statges 
    }
}

// Reference for pylint 
// https://pypi.org/project/pylint/