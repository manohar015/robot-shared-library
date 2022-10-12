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
          stage('Unit Tests') {
               sh 'echo Unit Test Cases Completed'
              }    
          stage('Integration Tests') {
                sh 'echo Integration Test Cases Completed'
              }
                  
          stage('Functional Tests') {
                sh 'echo Functional Test Cases Completed'      
                 }
            }
}

def sonarCheck() {
    sh ''' 
        # sonar-scanner -Dsonar.host.url=http://172.31.0.99:9000 -Dsonar.projectKey=${COMPONENT} -Dsonar.login=${SONAR_USR} -Dsonar.password=${SONAR_PSW} ${ARGS}
        # sonar-quality-gate.sh ${SONAR_USR} ${SONAR_PSW} 172.31.0.99 ${COMPONENT} || true
        echo sonarchecks for ${COMPONENT}
      ''' 
}


// For non-Java, code source partamter is  -Dsonar.sources=. 
// For Java, code source partamter is  -Dsonar.projectKey=target/ 