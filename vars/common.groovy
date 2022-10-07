def sonarCheck() {
    sh ''' 
        sonar-scanner -Dsonar.host.url=http://172.31.0.99:9000 -Dsonar.projectKey=${COMPONENT} -Dsonar.login=${SONAR_USR} -Dsonar.password=${SONAR_PSW} ${ARGS}
        sonar-quality-gate ${SONAR_USR} ${SONAR_PSW} 172.31.0.99 ${COMPONENT} // This gives the result of the scan and based on that either it will abort the pipeline or will proceed further.
        echo sonarchecks for ${COMPONENT}
      ''' 
}


// For non-Java, code source partamter is  -Dsonar.sources=. 
// For Java, code source partamter is  -Dsonar.projectKey=target/ 