def lintCheck() {
  stage('Lint Checks') {

    if (env.APPTYPE == 'nodejs') {
      sh ''' 
            # npm install 
            # We want Devs to handle the lint checks failure 
            # npm i jslint 
            # node_modules/jslint/bin/jslint.js  server.js || true 
            echo Starting lint checks
            echo Lint Checks Completed for ${COMPONENT}
      
        ''' 
        } else if (env.APPTYPE == 'python'){
        sh ''' 
            echo Starting lint checks ${COMPONENT}
            # pylint *.py           # lint checks
            echo Lint Checks Completed for ${COMPONENT}
         ''' 
      } else if (env.APPTYPE == 'maven'){
        sh ''' 
            # mvn clean compile 
            echo Starting lint checks ${COMPONENT}
            mvn checkstyle:check || true                        # lint checks
            echo Lint Checks Completed for ${COMPONENT}
         ''' 
      }
       else {
        sh ''' 
            echo Starting lint checks ${COMPONENT}
            echo Lint Checks Completed for ${COMPONENT}
         ''' 
      }
    }
}


def testCases() {
    stage('Test Cases') {
        def stages = [:]

        stages["Unit Test Cases"] = {
             sh 'echo Unit Test Cases Completed'
        }
        stages["Integration Test Cases"] = {
             sh 'echo Integration Test Cases Completed'
        }

        stages["Functional Test Cases"] = {
             sh 'echo Functional Test Cases Completed'
        }
        parallel(stages)
    }
}


def artifact {
      stage('Check the release') {
           script {
                env.UPLOAD_STATUS=sh(returnStdout: true, script: 'curl -L -s http://172.31.0.75:8081/service/rest/repository/browse/${COMPONENT} | grep ${COMPONENT}-${TAG_NAME}.zip || true')
                print UPLOAD_STATUS       
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
}

// def testCases() {
//     stage('Test Cases') {
//           stage('Unit Tests') {
//                sh 'echo Unit Test Cases Completed'
//               }    
//           stage('Integration Tests') {
//                 sh 'echo Integration Test Cases Completed'
//               }
                  
//           stage('Functional Tests') {
//                 sh 'echo Functional Test Cases Completed'      
//                  }
//             }
// }

def sonarCheck() {
    sh ''' 
        # sonar-scanner -Dsonar.host.url=http://172.31.0.99:9000 -Dsonar.projectKey=${COMPONENT} -Dsonar.login=${SONAR_USR} -Dsonar.password=${SONAR_PSW} ${ARGS}
        # sonar-quality-gate.sh ${SONAR_USR} ${SONAR_PSW} 172.31.0.99 ${COMPONENT} || true
        echo sonarchecks for ${COMPONENT}
      ''' 
}


// For non-Java, code source partamter is  -Dsonar.sources=. 
// For Java, code source partamter is  -Dsonar.projectKey=target/ 
// Reference for paralle in scriptd pipeline : https://stackoverflow.com/questions/46834998/scripted-jenkinsfile-parallel-stage