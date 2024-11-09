/* groovylint-disable NestedBlockDepth */
pipeline {
    // agent {
    //     label 'quality-check'
    // }

    agent any

    environment {
        GIT_COMMIT = sh(returnStdout: true, script: 'git rev-parse --short=10 HEAD').trim()
        DOCKER_HUB_USERNAME = 'phonqpham'
        DOCKER_HUB_TOKEN = credentials('dockerhub-accesstoken')
        REPO_NAME = 'CICD-Lab'
        USER_REPO = 'phonqpham/CICD-Lab'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                credentialsId: 'github-ssh-key',
                url: 'git@github.com:PhongPhamj/CICD_Lab.git'
            }
        }

        stage('Build & UT') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean package'
            }
        }

        // stage('Code Analysis') {
        //     parallel {
        //         stage('Dependency Check') {
        //             steps {
        //                 dependencyCheck additionalArguments: '--scan ./target/', odcInstallation: 'owasp'
        //                 dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
        //             }
        //         }
        //         stage('Code Scan') {
        //             steps {
        //                 script {
        //                     withSonarQubeEnv('EC2SonarQube') {
        //                         sh ' ./mvnw sonar:sonar '
        //                     }
        //                 }
        //             }
        //         }
        //     }
        // }

        // stage('Quality Gate') {
        //     steps {
        //         script {
        //             waitForQualityGate abortPipeline: true
        //         }
        //     }
        // }

        stage('Build Image') {
            steps {
                sh 'echo "start building"'
                sh '''
                if ! getent group docker > /dev/null 2>&1; then
                    sudo groupadd docker
                fi
                '''
                sh 'sudo usermod -aG docker ubuntu'
                script {
                    withDockerRegistry(credentialsId: 'docker-credentials', toolName: 'jenkins-docker', url: 'https://index.docker.io/v1/') {
                        sh 'docker build -t local-image .'
                        sh "docker tag local-image ${DOCKER_HUB_USERNAME}/${REPO_NAME}:latest ${DOCKER_HUB_USERNAME}/${REPO_NAME}:${GIT_COMMIT}"
                    }
                }
            }
        }

        // stage('Scan Image') {
        // }

            stage('Create Docker Hub Repo') {
                steps {
                    script {
                        // Check if the repository exists
                        checkRepo = sh(
                        script: """
                            curl -s -o /dev/null -w "%{http_code}" \
                            -H "Authorization: Bearer ${DOCKER_HUB_TOKEN}" \
                            https://hub.docker.com/v2/repositories/${DOCKER_HUB_USERNAME}/${REPO_NAME}/
                        """,
                        returnStdout: true
                    ).trim()
                        if (checkRepo == '404') {
                            // Repository does not exist, so create it
                            createRepo = sh(
                            script: """
                                curl -X POST https://hub.docker.com/v2/repositories/${DOCKER_HUB_USERNAME}/${REPO_NAME}/ \
                                -H "Authorization: Bearer ${DOCKER_HUB_TOKEN}" \
                                -H "Content-Type: application/json" \
                                -d '{ "name": "${REPO_NAME}", "is_private": true }'
                            """,
                            returnStatus: true
                        )

                            if (createRepo != 0) {
                                error('Failed to create Docker Hub private repository.')
                        } else {
                                echo "Created repository ${DOCKER_HUB_USERNAME}/${REPO_NAME} on Docker Hub."
                            }
                    } else {
                            error("Unexpected response: ${checkRepo}. Failed to verify repository existence.")
                        }
                    }
                }
            }

        stage('Push Image') {
            steps {
                script {
                    withDockerRegistry(credentialsId: 'docker-credentials', toolName: 'jenkins-docker') {
                        sh "docker push ${DOCKER_HUB_USERNAME}/${REPO_NAME}:latest"
                        sh "docker push ${DOCKER_HUB_USERNAME}/${REPO_NAME}:${GIT_COMMIT}"
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
