def  call() {

properties([
        parameters([
            choice(name: 'ENV',choices:"dev\nprod", description: "Choose Environment to build!")
            choice(name: 'ACTION',choices:"apply\ndestroy", description: "Choose apply or destroy")

                choice(defaultValue: '/data', name: 'Directory'),
                string(defaultValue: 'Dev', name: 'DEPLOY_ENV')

        ])   
    ])

    node {
        sh "rm -rf *"
        git branch: 'main', url: "https://github.com/b50-clouddevops/${REPONAME}.git"  

        stage('Terraform Init'){
            sh ''' 
                terrafile -f env-${ENV}/Terrafile
                terraform init -backend-config=env-${ENV}/${ENV}-backend.tfvars
            '''
        }  

        stage('Terraform Plan'){
            sh ''' 
                terraform plan -var-file=env-${ENV}/${ENV}.tfvars
            '''
        }  

        stage('Terraform Apply'){
            sh ''' 
                terraform ${ACTION} -var-file=env-${ENV}/${ENV}.tfvars -auto-approve
            '''
        }
        
    }

}

// Ref : https://stackoverflow.com/questions/53747772/is-it-possible-to-make-a-parameterized-scripted-pipeline-in-jenkins-by-listing-t
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