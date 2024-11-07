pipeline {
    agent any

    stages {

        stage('checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/PhongPhamj/CICD_Lab.git'
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
        //         jacoco(execPattern: '**/build/jacoco/*.exec')
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