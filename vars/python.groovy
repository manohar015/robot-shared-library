def call() {
    node {
        git branch: 'main', url: "https://github.com/b50-clouddevops/${COMPONENT}.git"
        env.APPTYPE="python"
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
//                         env.ARGS="-Dsonar.sources=."
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

// Reference for pylint 
// https://pypi.org/project/pylint/