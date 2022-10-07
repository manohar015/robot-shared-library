def lintCheck() {
    sh ''' 
         echo Starting lint checks ${COMPONENT}
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
                        env.ARGS=-Dsonar.sources=. 
                        common.sonarCheck()
                    }
                }
            }
        }    // end of statges 
    }
}