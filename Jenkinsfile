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
        REPO_NAME = 'cicd-lab'
        USER_REPO = 'phonqpham/cicd-lab'
        SONARQUBE_ANALYSIS_URL = 'http://13.210.206.84:9000/dashboard?id=com.example%3AbackendCICD'
        S3_BASE_URL = 'https://ap-southeast-2.console.aws.amazon.com/s3/buckets/jenkins-analysis-reports?region=ap-southeast-2&bucketType=general&prefix=jenkins/'
    }

    stages {
/******************************************************************************
 *CHECKOUT AND BUILD*
 ******************************************************************************/
        stage('Checkout') {
            steps {
                script {
                    startTime = new Date(currentBuild.startTimeInMillis).format('yyyy-MM-dd HH:mm:ss', TimeZone.getTimeZone('UTC'))
                }
                git branch: 'main',
                credentialsId: 'github-ssh-key',
                url: 'git@github.com:PhongPhamj/CICD_Lab.git'
            }
        }
        stage('Build & UT') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean package'
            // sh './mvnw install -DskipTests=true'
            }
        }
/******************************************************************************
 *CODE ANALYSIS*
 ******************************************************************************/
        // stage('Code Analysis') {
        //     parallel {
        //         stage('Dependency Check') {
        //             steps {
        //                 dependencyCheck additionalArguments: '--scan ./target/', odcInstallation: 'owasp'
        //                 dependencyCheckPublisher pattern: '**/dependency-check-report.xml'

        //                 script {
        //                     xmlReport = readFile('dependency-check-report.xml')
        //                     xml = new XmlParser().parseText(xmlReport)

        //                     vulnerabilities = xml.depthFirst().findAll { it.name() == 'vulnerability' }
        //                     vulnerabilities.each { vulnerability ->
        //                         echo "Vulnerability ID: ${vulnerability.'@id'}"
        //                     }
        //                 }

        //                 withAWS(credentials: 'AWS-user', region: 'ap-southeast-2') {
        //                     s3Upload(bucket: 'jenkins-analysis-reports', path:"jenkins/${GIT_COMMIT}/dependency-check.xml",  file: 'dependency-check-report.xml')
        //                 }

        //             }
        //         }
        //         stage('Code Scan') {
        //             steps {
        //                 script {
        //                     withSonarQubeEnv('EC2SonarQube') {
        //                         sh ' ./mvnw sonar:sonar '
        //                     }
        //                 }

        //                 script {
        //                     // waitForQualityGate abortPipeline: true
        //                     qualityGate = waitForQualityGate()
        //                     currentBuild.result = qualityGate.status == 'OK' ? 'SUCCESS' : 'FAILURE'
        //                     sonarStatus = qualityGate.status
        //                 }
        //             }
        //         }
        //     }
        // }

/******************************************************************************
 *CREATE DOCKER IMAGE*
 ******************************************************************************/
        stage('Build Image') {
            steps {
                script {
                    withDockerRegistry(credentialsId: 'docker-credentials', toolName: 'jenkins-docker', url: 'https://index.docker.io/v1/') {
                        sh 'docker build -t local-image .'
                        sh "docker tag local-image ${DOCKER_HUB_USERNAME}/${REPO_NAME}:latest"
                        sh "docker tag local-image ${DOCKER_HUB_USERNAME}/${REPO_NAME}:${GIT_COMMIT}"
                    }
                }
            }
        }
        stage('Scan Image') {
            steps {
                withCredentials([string(credentialsId: 'github-classic-token', variable: 'TRIVY_TOKEN')]) {
                sh '''
                    export TRIVY_AUTH_URL="https://ghcr.io"
                    export TRIVY_TOKEN=$TRIVY_TOKEN
                    echo "TRIVY_TOKEN"
                    trivy image --no-progress --exit-code 1 --severity HIGH,CRITICAL -f json -o trivy-report.json $DOCKER_HUB_USERNAME/$REPO_NAME:latest
                '''
                }
                withAWS(credentials: 'AWS-user', region: 'ap-southeast-2') {
                    s3Upload(bucket: 'jenkins-analysis-reports', path:"jenkins/${GIT_COMMIT}/image-scan.json",  file: 'trivy-report.json')
                }
            }
            post {
                failure {
                    sh 'docker system prune --force --all --volumes'
                }
            }
        }

        stage('Create Docker Hub Repo If not existed') {
            steps {
                script {
                    // Check if the repository exists
                    checkRepo = sh(
                    script: '''
                        curl -s -o /dev/null -w "%{http_code}" \
                        -H "Authorization: Bearer $DOCKER_HUB_TOKEN" \
                        https://hub.docker.com/v2/repositories/$DOCKER_HUB_USERNAME/$REPO_NAME/
                    ''',
                    returnStdout: true
                    ).trim()
                    if (checkRepo == '404') {
                        // Repository does not exist, so create it
                        createRepo = sh(
                            script: '''
                                curl -X POST https://hub.docker.com/v2/repositories/$DOCKER_HUB_USERNAME/$REPO_NAME/ \
                                -H "Authorization: Bearer $DOCKER_HUB_TOKEN" \
                                -H "Content-Type: application/json" \
                                -d '{ "name": "$REPO_NAME", "is_private": true }'
                            ''',
                            returnStatus: true
                        )

                        if (createRepo != 0) {
                            error('Failed to create Docker Hub private repository.')
                        } else {
                            echo "Created repository ${DOCKER_HUB_USERNAME}/${REPO_NAME} on Docker Hub."
                        }
                    }else {
                        echo 'Repository found'
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
                        sh 'docker system prune --all'
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }

        // success {
        //     slackSend(channel: '#cicd', color: 'good'
        //     , message: "Job '${REPO_NAME} [${GIT_COMMIT}]' succeeded. Started at: ${startTime}." +
        //     "\nCode Quality Analysis can be found at: ${SONARQUBE_ANALYSIS_URL}"+
        //     "\nDependency Check Analysis can be found at: ${S3_BASE_URL}${GIT_COMMIT}/"+
        //     "\nImage Scan Analysis can be found at: ${S3_BASE_URL}${GIT_COMMIT}/")
        // }

        // failure {
        //     slackSend(channel: '#cicd', color: 'danger'
        //     , message: "Job '${REPO_NAME} [${GIT_COMMIT}]' failed. Started at: ${startTime}.\n" +
        //     "\nCode Quality Analysis can be found at: ${SONARQUBE_ANALYSIS_URL}."+
        //     "\nDependency Cheeck Analysis can be found at: ${S3_BASE_URL}${GIT_COMMIT}/"+
        //     "\nImage Scan Analysis can be found at: ${S3_BASE_URL}${GIT_COMMIT}/")
        // }
    }
}

