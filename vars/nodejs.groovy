def call() {
    node {
        git branch: 'main', url: "https://github.com/b50-clouddevops/${COMPONENT}.git"
        env.APPTYPE="nodejs"
        common.sonarCheck()
        common.lintCheck()
        env.ARGS="-Dsonar.sources=."
        common.testCases()
        if (env.TAG_NAME != null) {
            common.artifact()
        }
    }
}

// def call() {
//     pipeline {
//         agent any 
//         environment {
//             SONAR      = credentials('SONAR')
//             NEXUS      = credentials('NEXUS')
//         }
 
//         stages {
//             stage('Downloading the dependencies') {
//                 steps {
//                     sh "npm install"
//                 }
//             }


//             stage('Sonar Check') {
//                 steps {
//                     script { 

//                     }
//                 }
//             }



//             stage('Check the release') {
//                 when {
//                     expression { env.TAG_NAME != null }   // Only runs when you run this against the TAG
//                 }
//                 steps {
//                     script {
//                         env.UPLOAD_STATUS=sh(returnStdout: true, script: 'curl -L -s http://172.31.0.75:8081/service/rest/repository/browse/${COMPONENT} | grep ${COMPONENT}-${TAG_NAME}.zip || true')
//                         print UPLOAD_STATUS
//                     }
//                 }
//             }

//             stage('Prepare Artifacts') {
//                 when {
//                     expression { env.TAG_NAME != null }   // Only runs when you run this against the TAG
//                     expression { env.UPLOAD_STATUS == "" }
//                 }
//                 steps {
//                     sh ''' 
//                         npm install 
//                         zip ${COMPONENT}-${TAG_NAME}.zip node_modules server.js

//                     ''' 
//                 }
//             }

//             stage('Upload Artifacts') {
//                 when {
//                     expression { env.TAG_NAME != null }   // Only runs when you run this against the TAG
//                     expression { env.UPLOAD_STATUS == "" }
//                 }
//                 steps {
//                     sh ''' 
//                         curl -f -v -u ${NEXUS_USR}:${NEXUS_PSW} --upload-file ${COMPONENT}-${TAG_NAME}.zip http://172.31.0.75:8081/repository/${COMPONENT}/${COMPONENT}-${TAG_NAME}.zip
//                     '''
//                 }
//             }
//         }    // end of statges 
//     }
// }

// call is the default function which will be called when you call the fileName 