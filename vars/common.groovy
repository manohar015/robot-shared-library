def sonarCheck() {
   stage('Sonar Checks') {
    sh ''' 
        # sonar-scanner -Dsonar.host.url=http://172.31.0.99:9000 -Dsonar.projectKey=${COMPONENT} -Dsonar.login=${SONAR_USR} -Dsonar.password=${SONAR_PSW} ${ARGS}
        # sonar-quality-gate.sh ${SONAR_USR} ${SONAR_PSW} 172.31.0.99 ${COMPONENT} || true
        echo sonarchecks for ${COMPONENT}
        
      '''
   } 
}


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
            # mvn checkstyle:check || true                        # lint checks
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


def artifact() {
      stage('Check the release') {
           script {
                env.UPLOAD_STATUS=sh(returnStdout: true, script: 'curl -L -s http://172.31.0.75:8081/service/rest/repository/browse/${COMPONENT} | grep ${COMPONENT}-${TAG_NAME}.zip || true')
                print UPLOAD_STATUS       
             }
        }
    if(env.UPLOAD_STATUS == "") {
       stage('Prepare Artifacts') {
             if(env.APPTYPE == "nodejs") {
                sh ''' 
                    ls -ltr 
                    npm install 
                    ls -ltr 
                    zip -r ${COMPONENT}-${TAG_NAME}.zip node_modules server.js
                   
                   ''' 
            }
             else if(env.APPTYPE == "maven") {
                sh ''' 
                    mvn clean package 
                    mv target/${COMPONENT}-1.0.jar ${COMPONENT}.jar
                    zip -r ${COMPONENT}-${TAG_NAME}.zip ${COMPONENT}.jar

                   ''' 
            }

             else if(env.APPTYPE == "angularjs") {
                sh ''' 
                    cd static
                    zip -r ../${COMPONENT}-${TAG_NAME}.zip *                                 
                   ''' 
            }

             else if(env.APPTYPE == "python") {
                sh ''' 
                   zip -r ${COMPONENT}-${TAG_NAME}.zip *.py *.ini requirements.txt                              
                   ''' 
            }
             else {
                sh '''                
                    echo "This is an assignment for go"                                   
                   ''' 
            }
       } 
      stage('Upload Artifacts') {
         withCredentials([usernamePassword(credentialsId: 'NEXUS', usernameVariable: 'NEXUS_USR', passwordVariable: 'NEXUS_PSW')]) {
            sh ''' 
             curl -f -v -u ${NEXUS_USR}:${NEXUS_PSW} --upload-file ${COMPONENT}-${TAG_NAME}.zip http://172.31.0.75:8081/repository/${COMPONENT}/${COMPONENT}-${TAG_NAME}.zip
                  
             '''
              }
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



// For non-Java, code source partamter is  -Dsonar.sources=. 
// For Java, code source partamter is  -Dsonar.projectKey=target/ 
// Reference for paralle in scriptd pipeline : https://stackoverflow.com/questions/46834998/scripted-jenkinsfile-parallel-stage