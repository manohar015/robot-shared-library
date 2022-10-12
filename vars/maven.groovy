def call() {
    node {
        git branch: 'main', url: "https://github.com/b50-clouddevops/${COMPONENT}.git"
        env.APPTYPE="maven"
        common.sonarCheck()
        common.lintCheck()
        sh 'mvn clean compile'
        env.ARGS="-Dsonar.java.binaries=target/"
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
//             stage('Lint Check') {
//                 steps {
//                     script { 
//                         lintCheck()
//                     }
//                 }
//             }
//             stage('Sonar Check') {
//                 steps {
//                     script { 
//                         sh "mvn clean compile"
//                         env.ARGS="-Dsonar.java.binaries=target/"
//                         common.sonarCheck()
//                     }
//                 }
//             }
//            stage('Test Cases') {
//             parallel {
//                 stage('Unit Tests') {
//                     steps {
//                         sh 'echo Unit Test Cases Completed'
//                          }
//                     }
//                 stage('Integration Tests') {
//                     steps {
//                         sh 'echo Integration Test Cases Completed'
//                          }
//                     }
//                 stage('Functional Tests') {
//                     steps {
//                         sh 'echo Functional Test Cases Completed'
//                          }
//                     }
//                 }
//             }
//         }    // end of statges 
//     }
// }

// call is the default function which will be called when you call the fileName 