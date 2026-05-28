// ─────────────────────────────────────────────────────────────────────────────
// Jenkinsfile — Windows Compatible Declarative Pipeline
// ─────────────────────────────────────────────────────────────────────────────

pipeline {

    agent any

    environment {
        DOCKER_USERNAME  = 'rajat54321'
        IMAGE_NAME       = 'student-api'
        IMAGE_TAG        = "${BUILD_NUMBER}"
        FULL_IMAGE       = "${DOCKER_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}"
        CONTAINER_NAME   = 'student-api-container'
        APP_PORT         = '8080'
        DOCKER_CREDS     = credentials('dockerhub-credentials')
    }

    tools {
        maven 'Maven-3.9'
        jdk   'JDK-17'
    }

    stages {

        stage('Checkout') {
            steps {
                echo '=== Checking out source code from GitHub ==='
                checkout scm
                echo "Build number: ${env.BUILD_NUMBER}"
            }
        }

        stage('Test') {
            steps {
                echo '=== Running unit tests ==='
                bat 'mvn test -B'
            }
            post {
                always {
                    junit testResults: 'target/surefire-reports/*.xml',
                        allowEmptyResults: true
                }
            }
        }

        stage('Build JAR') {
            steps {
                echo '=== Packaging application with Maven ==='
                bat 'mvn clean package -DskipTests -B'
                echo 'JAR created at: target/student-api-1.0.0.jar'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('Docker Build') {
            steps {
                echo "=== Building Docker image: ${FULL_IMAGE} ==="
                bat "docker build -t ${FULL_IMAGE} ."
                bat "docker tag ${FULL_IMAGE} ${DOCKER_USERNAME}/${IMAGE_NAME}:latest"
            }
        }

        stage('Docker Push') {
            steps {
                echo '=== Pushing image to Docker Hub ==='
                bat "echo %DOCKER_CREDS_PSW% | docker login -u %DOCKER_CREDS_USR% --password-stdin"
                bat "docker push ${FULL_IMAGE}"
                bat "docker push ${DOCKER_USERNAME}/${IMAGE_NAME}:latest"
            }
        }

        stage('Deploy') {
            steps {
                echo '=== Deploying container ==='
                bat "docker stop ${CONTAINER_NAME} || exit 0"
                bat "docker rm   ${CONTAINER_NAME} || exit 0"
                bat "docker pull ${FULL_IMAGE}"
                bat "docker run -d --name ${CONTAINER_NAME} -p ${APP_PORT}:8080 --restart unless-stopped ${FULL_IMAGE}"
                echo "Container started. App live at http://localhost:${APP_PORT}/api/students"
            }
        }
    }

    post {
        success {
            echo "BUILD SUCCESSFUL — http://localhost:${APP_PORT}/api/students"
        }
        failure {
            echo 'BUILD FAILED — Check the stage logs above for details.'
        }
        always {
            echo 'Pipeline finished.'
            bat 'docker image prune -f || exit 0'
        }
    }
}