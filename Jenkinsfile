pipeline {
    agent any

    stages {

        // stage('checkout') {
        //     steps {
        //         git branch: 'main', 
        //             credentialsId: 'github-ssh-key', 
        //             url: 'git@github.com:PhongPhamj/CICD_Lab.git'
        //     }
        // }

        stages {
        stage('checkout') {
            steps {
                // Checkout the 'dev' branch from the private GitHub repository
                checkout([
                    $class: 'GitSCM', 
                    branches: [[name: 'dev']], // Specify 'dev' branch
                    userRemoteConfigs: [[
                        url: 'git@github.com:PhongPhamj/CICD_Lab.git', // SSH URL of the repository
                        credentialsId: 'github-ssh-key' // Jenkins credential ID for SSH key
                    ]]
                ])
            }
        }

        stage('build') {
            steps {
                sh 'mvn install -DskipTests=true'
            }
        }

        // stage('capture') {
        //     steps {
        //         archiveArtifacts '**/build/libs/*.jar'
        //         junit '**/build/test-results/test/*.xml'
        //         // Configure Jacoco for code coverage
        //         jacoco(execPattern: '**/build/jacoco/*.exec'))
        //     }
        // }
    }

    post {
        always {
            // Clean up workspace
            cleanWs()
        }

        success {
            // Notify on success
            echo 'Build successful!'
        }

        unstable {
            // Notify on unstable build
            echo 'Build unstable.'
        }

        failure {
            // Notify on failure
            echo 'Build failed!'
        }
    }
}
