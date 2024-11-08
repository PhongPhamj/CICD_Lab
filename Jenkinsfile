pipeline {
    agent any

    stages {
        stage('checkout') {
            steps {
                // checkout([
                //     $class: 'GitSCM', 
                //     branches: [[name: 'main']], // Specify 'dev' branch
                //     userRemoteConfigs: [[
                //         url: 'git@github.com:PhongPhamj/CICD_Lab.git', // SSH URL of the repository
                //         credentialsId: 'github-ssh-key' // Jenkins credential ID for SSH key
                //     ]]
                // ])
                git branch: 'main', 
                    credentialsId: 'github-ssh-key', 
                    url: 'git@github.com:PhongPhamj/CICD_Lab.git'
            }
        }

        stage('build') {
            steps {
                sh 'chmod +x mvnw'
                sh 'pwd'
                sh 'ls -la'
                // dir('CICD_Lab') {
                sh './mvnw install -DskipTests=true'
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
