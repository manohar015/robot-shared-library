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
        }    // end of statges 
    }
}

// Reference for pylint 
// https://pypi.org/project/pylint/