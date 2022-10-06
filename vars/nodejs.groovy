def lintCheck() {
    sh ''' 
         # We want Devs to handle the lint checks failure 
         # npm i jslint 
         # node_modules/jslint/bin/jslint.js  server.js || true 
         echo Starting lint checks
         echo Lint Checks Completed for ${COMPONENT}
    ''' 
}

def sonarCheck() {
    sh ''' 
        sonar-scanner -Dsonar.host.url=http://172.31.0.99:9000 -Dsonar.sources=. -Dsonar.projectKey=${COMPONENT} -Dsonar.login=admin -Dsonar.password=password
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
            stage('Sonar Check') {
                steps {
                    script { 
                        sonarCheck()
                    }
                }
            }
        }    // end of statges 
    }
}

// call is the default function which will be called when you call the fileName 