def  call() {

if(!env.TERRAFORM_DIR) {
    env.TERRAFORM_DIR = "./"
}

properties([
        parameters([
            choice(choices: 'dev\nprod', description: "Chose the Env", name: "ENV"),
            choice(choices: 'apply\ndestroy', description: "Choose apply or destroy", name: "ACTION"),
            string(choices: 'APP_VERSION', description: "Enter the version to deploy", name: "APP_VERSION"),
        ]) ,
    ])

    node {
      ansiColor('xterm') {
        sh "rm -rf *"
        git branch: 'main', url: "https://github.com/b50-clouddevops/${REPONAME}.git"  

        stage('Terraform Init'){
            sh ''' 
                cd ${TERRAFORM_DIR}
                terrafile -f env-${ENV}/Terrafile
                terraform init -backend-config=env-${ENV}/${ENV}-backend.tfvars\
            '''
        }  

        stage('Terraform Plan'){
            sh ''' 
                cd ${TERRAFORM_DIR}
                export TF_VAR_APP_VERSION=${APP_VERSION}
                terraform plan -var-file=env-${ENV}/${ENV}.tfvars
            '''
        }  

        stage('Terraform Apply'){
            sh ''' 
                cd ${TERRAFORM_DIR}
                export TF_VAR_APP_VERSION=${APP_VERSION}
                terraform ${ACTION} -var-file=env-${ENV}/${ENV}.tfvars -auto-approve -parallelism 1
            '''
            }            
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