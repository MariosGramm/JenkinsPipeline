pipeline {
    parameters {
        booleanParam(name: 'autoApprove', defaultValue: false, description: 'Automatically run apply after generating plan?')
    }
    environment {
        AWS_ACCESS_KEY_ID     = credentials('AWS_ACCESS_KEY_ID')
        AWS_SECRET_ACCESS_KEY = credentials('AWS_SECRET_KEY') 
        AWS_REGION            = 'us-east-1' 
    }
    agent any
    stages {
        stage('Checkout') { // Terraform code retrieval
            steps {
                dir("terraform") {
                    git branch: 'main', url: "https://github.com/MariosGramm/MyFirstPipeline.git"
                }
            }
        }
        stage('InitPlan') { // Terraform init + plan + tfplan.txt file
            steps {
                dir('terraform') {
                    bat 'terraform init'
                    // Παράμετροι για AWS region και credentials
                    bat 'terraform plan -var="aws_region=%AWS_REGION%" -var="aws_access_key=%AWS_ACCESS_KEY_ID%" -var="aws_secret_key=%AWS_SECRET_ACCESS_KEY%" -out=tfplan'
                    bat 'terraform show -no-color tfplan > tfplan.txt'
                }
            }
        }
        stage('Approval') { // Manual approval if autoApprove is false
            when {
                not {
                    equals expected: true, actual: params.autoApprove
                }
            }
            steps {
                script {
                    def plan = readFile 'terraform/tfplan.txt'
                    input message: "Do you want to apply the plan?",
                    parameters: [text(name: 'Plan', description: 'Please review the plan', defaultValue: plan)]
                }
            }
        }
        stage('Apply') { // Terraform apply
            steps {
                dir('terraform') {
                    bat 'terraform apply -input=false tfplan'
                }
            }
        }
    }
}
