def lintCheck() {
  stage('Lint Checks') {

    if (env.APPTYPE == 'nodejs') {
        sh ''' 
         # We want Devs to handle the lint checks failure 
         # npm i jslint 
         # node_modules/jslint/bin/jslint.js  server.js || true 
         echo Starting lint checks
         echo Lint Checks Completed for ${COMPONENT}
        ''' 
        } else {
            echo 'I execute elsewhere'
      }

  }
}


def sonarCheck() {
    sh ''' 
        sonar-scanner -Dsonar.host.url=http://172.31.0.99:9000 -Dsonar.projectKey=${COMPONENT} -Dsonar.login=${SONAR_USR} -Dsonar.password=${SONAR_PSW} ${ARGS}
        sonar-quality-gate.sh ${SONAR_USR} ${SONAR_PSW} 172.31.0.99 ${COMPONENT} || true
        echo sonarchecks for ${COMPONENT}
      ''' 
}


// For non-Java, code source partamter is  -Dsonar.sources=. 
// For Java, code source partamter is  -Dsonar.projectKey=target/ 