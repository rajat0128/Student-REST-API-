// ─────────────────────────────────────────────────────────────────────────────
// Jenkinsfile — Declarative Pipeline
//
// This file tells Jenkins exactly what to do when code is pushed to GitHub.
// Stages run in order; if any stage fails, the pipeline stops and marks the
// build as FAILED (red ball in Jenkins UI).
// ─────────────────────────────────────────────────────────────────────────────

pipeline {

    // 'any' means Jenkins can run this pipeline on any available agent/node
    agent any

    // ── Environment Variables ────────────────────────────────────────────────
    // Change DOCKER_USERNAME to your own Docker Hub username
    environment {
        DOCKER_USERNAME  = 'your-dockerhub-username'   // ← CHANGE THIS
        IMAGE_NAME       = 'student-api'
        IMAGE_TAG        = "${BUILD_NUMBER}"             // e.g. "42"
        FULL_IMAGE       = "${DOCKER_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}"
        CONTAINER_NAME   = 'student-api-container'
        APP_PORT         = '8080'

        // 'dockerhub-credentials' must be added in:
        //   Jenkins → Manage Jenkins → Credentials → Add Credentials
        //   Kind: Username with password | ID: dockerhub-credentials
        DOCKER_CREDS     = credentials('dockerhub-credentials')
    }

    // ── Tools ────────────────────────────────────────────────────────────────
    // These must be configured in Jenkins → Manage Jenkins → Global Tool Configuration
    tools {
        maven 'Maven-3.9'     // Name given when configuring Maven in Jenkins
        jdk   'JDK-17'        // Name given when configuring JDK in Jenkins
    }

    // ── Pipeline Stages ──────────────────────────────────────────────────────
    stages {

        // Stage 1: Pull the latest code from GitHub
        stage('Checkout') {
            steps {
                echo '=== Checking out source code from GitHub ==='
                // Jenkins automatically checks out the branch that triggered the build
                checkout scm
                echo "Building branch: ${env.BRANCH_NAME}"
                echo "Build number:    ${env.BUILD_NUMBER}"
            }
        }

        // Stage 2: Run unit tests with Maven
        stage('Test') {
            steps {
                echo '=== Running unit tests ==='
                sh 'mvn test -B'
                // -B = batch mode (no interactive prompts, cleaner logs)
            }
            post {
                // Always publish test results even if tests fail
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        // Stage 3: Package the application into a JAR
        stage('Build JAR') {
            steps {
                echo '=== Packaging application with Maven ==='
                sh 'mvn clean package -DskipTests -B'
                // -DskipTests: tests already ran in Stage 2, skip them here
                echo "JAR created at: target/student-api-1.0.0.jar"
            }
            post {
                success {
                    // Archive the JAR so you can download it from Jenkins UI
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        // Stage 4: Build the Docker image
        stage('Docker Build') {
            steps {
                echo "=== Building Docker image: ${FULL_IMAGE} ==="
                sh """
                    docker build -t ${FULL_IMAGE} .
                    docker tag ${FULL_IMAGE} ${DOCKER_USERNAME}/${IMAGE_NAME}:latest
                """
                // Also tag as 'latest' so it's easy to find the newest build
            }
        }

        // Stage 5: Push the image to Docker Hub
        stage('Docker Push') {
            steps {
                echo '=== Pushing image to Docker Hub ==='
                sh """
                    echo ${DOCKER_CREDS_PSW} | docker login -u ${DOCKER_CREDS_USR} --password-stdin
                    docker push ${FULL_IMAGE}
                    docker push ${DOCKER_USERNAME}/${IMAGE_NAME}:latest
                """
            }
        }

        // Stage 6: Deploy — stop old container, start new one
        stage('Deploy') {
            steps {
                echo '=== Deploying container ==='
                sh """
                    # Stop and remove old container if it exists (ignore errors if not running)
                    docker stop  ${CONTAINER_NAME} || true
                    docker rm    ${CONTAINER_NAME} || true

                    # Pull the freshly built image
                    docker pull ${FULL_IMAGE}

                    # Run the new container
                    docker run -d \\
                        --name ${CONTAINER_NAME} \\
                        -p ${APP_PORT}:8080 \\
                        --restart unless-stopped \\
                        ${FULL_IMAGE}

                    echo "Container started. App is live at http://localhost:${APP_PORT}/api/students"
                """
            }
        }
    }

    // ── Post-pipeline Actions ────────────────────────────────────────────────
    post {
        success {
            echo """
            ✅ BUILD SUCCESSFUL
            Image : ${FULL_IMAGE}
            App   : http://localhost:${APP_PORT}/api/students
            Health: http://localhost:${APP_PORT}/actuator/health
            """
        }
        failure {
            echo '❌ BUILD FAILED — Check the stage logs above for details.'
            // In a real project you would send an email or Slack notification here
        }
        always {
            echo 'Pipeline finished.'
            // Clean up local dangling Docker images to save disk space
            sh 'docker image prune -f || true'
        }
    }
}
