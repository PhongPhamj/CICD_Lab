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
        
        stage('Code Analysis') {
            parallel {
                stage('Dependency Check') {
                    steps {
                        dependencyCheck additionalArguments: '--scan ./target/', odcInstallation: 'owasp'
                        dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
                    }
                }
                stage('Code Scan') {
                    steps {
                        script {
                            withSonarQubeEnv('SonarQube') {
                                sh ' ./mvnw sonar:sonar '
                            }
                        }
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                script {
                    qualityGate = waitForQualityGate()
                    if (qualityGate.status != 'OK') {
                        error 'Pipeline aborted due to quality gate failure'
                    } else {
                        echo 'Quality gate passed'
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
