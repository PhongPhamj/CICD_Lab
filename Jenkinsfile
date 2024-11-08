pipeline {
    agent {
        label 'quality-check'
    }

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
                sh './mvnw install -DskipTests=true'
            }
        }

        // stage('Quality Analysis') {
        //     parallel {
        //         stage('Dependency Check') {
        //             steps {
        //                 dependencyCheck additionalArguments: '--scan ./target/', odcInstallation: 'owasp'
        //                 dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
        //             }
        //         }

        //         stage('Code Scan') {
        //             steps {
        //                 sh ''' mvn sonar:sonar \
        //                     -Dsonar.host.url=http://localhost:9000/ \
        //                     -Dsonar.login=squ_9bd7c664e4941bd4e7670a88ed93d68af40b42a3 '''
        //             }
        //         }
        //     }
        // }

        stage('Dependency Check') {
                steps {
                    dependencyCheck additionalArguments: '--scan ./target/', odcInstallation: 'owasp'
                    dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
                    }
        }

        stage('Code Scan') {
            steps {
                    sh ''' ./mvnw sonar:sonar \
                    -Dsonar.host.url=http://3.27.32.114:9000/ \
                    -Dsonar.login=squ_326f9df19a5fa0d11644bc817357b918a969a230 \
                    -Dsonar.java.binaries=target/ \
                    -Dsonar.projectName=CICD-Lab \
                    -Dsonar.projectKey=CICD-Lab '''
                    }
                }

        stage('UT + package') {
            steps {
                sh './mvnw clean package'
            }
        }
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
