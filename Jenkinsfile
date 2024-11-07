pipeline {
    agent any

    stages {
        stage('checkout') {
            steps {
                // Checkout the 'dev' branch from the private GitHub repository
                checkout([
                    $class: 'GitSCM', 
                    branches: [[name: 'main']], // Specify 'dev' branch
                    userRemoteConfigs: [[
                        url: 'git@github.com:PhongPhamj/CICD_Lab.git', // SSH URL of the repository
                        credentialsId: 'github-ssh-key' // Jenkins credential ID for SSH key
                    ]]
                ])
            }
        }

        stage('build') {
            steps {
                // dir('CICD_Lab') {
                    sh 'sudo -i'
                    sh 'sudo chmod +x mvnw'
                    sh 'ls -la'
                    sh 'mvnw install -DskipTests=true'
                 // }
            }
        }

        // Optional capture stage
        // stage('capture') {
        //     steps {
        //         archiveArtifacts '**/build/libs/*.jar'
        //         junit '**/build/test-results/test/*.xml'
        //         jacoco(execPattern: '**/build/jacoco/*.exec')
        //     }
        // }
    }

    post {
        // always {
        //     // Clean up workspace
        //     cleanWs()
        // }

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
