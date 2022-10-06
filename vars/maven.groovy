def lintCheck() {
    sh ''' 
         echo Starting lint checks ${COMPONENT}
         mvn checkstyle:check || true 
         echo Lint Checks Completed for ${COMPONENT}
    ''' 
}

def call() {
    pipeline {
        agent any 
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
        }    // end of statges 
    }
}

// call is the default function which will be called when you call the fileName 