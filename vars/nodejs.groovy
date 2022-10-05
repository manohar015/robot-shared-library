def lintChecks() {
    sh ''' 
         # We want Devs to handle the lint checks failure 
         # npm i jslint 
         # node_modules/jslint/bin/jslint.js  server.js || true 
         echo Starting lint checks
         echo Lint Checks Completed
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
            stage('Lint Checks') {
                steps {
                    script { 
                        nodejs.lintChecks()
                    }
                }
            }
        }    // end of statges 
    }
}

// call is the default function which will be called when you call the fileName 