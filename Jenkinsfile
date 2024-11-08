pipeline {
    agent {
        label 'quality-check'
    }

    stages {
        stage('checkout') {
            steps {
                //git installation not configured
                // checkout([
                //     $class: 'GitSCM',
                //     branches: [[name: 'main']], // Specify 'dev' branch
                //     userRemoteConfigs: [[
                //         url: 'git@github.com:PhongPhamj/CICD_Lab.git', // SSH URL of the repository
                //         credentialsId: 'github-ssh-key' // Jenkins credential ID for SSH key
                //     ]]
                // ])

                //git installation configured
                git branch: 'main',
                    credentialsId: 'github-ssh-key',
                    url: 'git@github.com:PhongPhamj/CICD_Lab.git'
            }
        }

        stage('build & UT') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean package'
            }
        }

        // stage('Quality Analysis') {
        //     parallel {
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
        //     }
        // }

        stage('Quality Gate') {
            steps {
                script {
                    def qualityGate = waitForQualityGate()
                    if (qualityGate.status != 'OK') {
                        error 'Pipeline aborted due to quality gate failure'
                    }
                }
            }
        }
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
