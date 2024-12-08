pipeline {
    parameters {
        booleanParam(name:'autoApprove', defaultValue:false, description:'Automatically run apply after terraform plan?')
    }
    environment {
        AWS_ACCESS_KEY_ID = credentials('AWS_ACCESS_KEY_ID')
        AWS_SECRET_KEY    = credentials('AWS_SECRET_KEY')
    }

    agent any
        stages {
            stage('checkout'){          //Terraform code retrieval 
                steps{
                    script {
                        dir('Terraform')
                        {
                             git branch: 'main', url: "https://github.com/MariosGramm/MyFirstPipeline.git"
                        }
                    }
                }
            }

            stage('InitPlan'){           //Terraform init + plan + tfplan.txt file
                steps{
                    sh 'pwd; cd terraform/ ; terraform init'
                    sh 'pwd; cd terraform/ ; terraform plan - out tfplan'
                    sh 'pwd; cd terraform/ ; terraform show -no-color tfplan > tfplan.txt'
                }

            }
        
           stage('Approval'){           //Αν ο χρήστης δεν έχει επιλέξει autoApprove
                when {
                    not {
                        equals expected:true,actual:params.autoApprove
                    }
                }

                steps{
                    script {
                        def plan = readFile 'terraform/tfplan.txt'
                        input message : 'Do you want to apply the plan?'
                        parameters : [text(name:'Plan', description: 'Please review the plan' , defaultValue: plan)]
                    }
                }
            }

            stage('Apply'){         //Terraform apply  
                steps{
                    sh 'pwd;cd terraform/ ; terraform apply -input=false tfplan'
                }
            }
        }
}

