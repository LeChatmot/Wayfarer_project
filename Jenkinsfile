pipeline {
    agent any

    tools {
        jdk 'jdk21'
        maven 'maven3'
        nodejs 'node22'
    }

    environment {
        BACKEND_DIR = 'Wayfarer_backend'
        FRONTEND_DIR = 'Wayfarer_frontend'
        E2E_COMPOSE = 'docker-compose.e2e.yml'
        E2E_PROJECT = "wayfarer-e2e-${env.BUILD_NUMBER}"
        E2E_HTTP_PORT = '8090'
        E2E_HTTPS_PORT = '8443'
        E2E_BASE_URL = "http://localhost:8090"
        PROD_COMPOSE = 'docker-compose.yml'
        CI= true
        TESTCONTAINERS_RYUK_DISABLED = 'false'
    }

    options {
        timestamps()
        ansiColor('xterm')
        timeout(time: 45, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '10'))
        disableConcurrentBuilds()
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.GIT_SHORT_SHA = sh(
                        script: 'git rev-parse --short HEAD',
                        returnStdout: true
                    ).trim()
                }
                echo "Branche : ${env.BRANCH_NAME} | Commit : ${env.GIT_SHORT_SHA}"
            }
        }

        stage('Preparation des secrets') {
            steps {
                sh 'chmod +x scripts/generate-secrets.sh'
                sh './scripts/generate-secrets.sh'
            }
        }

        stage('Build') {
            parallel {
                stage('Backend - compile') {
                    steps {
                        dir("${BACKEND_DIR}") {
                            sh './mvnw -B -ntp verify'
                        }
                    }
                }
                stage('Frontend - install & build') {
                    steps {
                        dir("${FRONTEND_DIR}") {
                            sh 'npm ci'
                            sh 'npm run build'
                        }
                    }
                }
            }
        }

        stage('Tests unitaires') {
            parallel {
                stage('Backend - Surefire') {
                    steps {
                        dir("${BACKEND_DIR}") {
                            sh './mvnw -B -ntp test'
                        }
                    }
                    post {
                        always {
                            junit(
                                testResults: "${BACKEND_DIR}/target/surefire-reports/*.xml",
                                allowEmptyResults: true
                            )
                        }
                    }
                }
                stage('Frontend - Vitest') {
                    steps {
                        dir("${FRONTEND_DIR}") {
                            sh 'npm run test:ci'
                        }
                    }
                    post {
                        always {
                            junit(
                                testResults: "${FRONTEND_DIR}/reports/junit-vitest.xml",
                                allowEmptyResults: true
                            )
                            publishHTML(target: [
                                reportDir: "${FRONTEND_DIR}/coverage/lcov-report",
                                reportFiles: 'index.html',
                                reportName: 'Couverture Frontend',
                                keepAll: true,
                                alwaysLinkToLastBuild: true,
                                allowMissing: true
                            ])
                        }
                    }
                }
            }
        }

        stage('Tests integration - Testcontainers') {
            steps {
                dir("${BACKEND_DIR}") {
                    sh './mvnw -B -ntp verify -DskipTests=false -Dsurefire.skip=true'
                }
            }
            post {
                always {
                    junit(
                        testResults: "${BACKEND_DIR}/target/failsafe-reports/*.xml",
                        allowEmptyResults: true
                    )
                    publishHTML(target: [
                        reportDir: "${BACKEND_DIR}/target/site/jacoco-merged",
                        reportFiles: 'index.html',
                        reportName: 'Couverture JaCoCo Backend',
                        keepAll: true,
                        alwaysLinkToLastBuild: true,
                        allowMissing: true
                    ])
                }
            }
        }

        stage('Analyse SonarQube') {
            parallel {
                stage('Sonar Backend') {
                    steps {
                        withSonarQubeEnv('SonarQube') {
                            dir("${BACKEND_DIR}") {
                                sh './mvnw -B -ntp sonar:sonar'
                            }
                        }
                        script {
                            env.SONAR_TASK_BACKEND = readFile(
                                "${BACKEND_DIR}/target/sonar/report-task.txt"
                            ).trim()
                        }
                    }
                }
                stage('Sonar Frontend') {
                    steps {
                        withSonarQubeEnv('SonarQube') {
                            script {
                                def scannerHome = tool 'sonar-scanner'
                                dir("${FRONTEND_DIR}") {
                                    sh "${scannerHome}/bin/sonar-scanner"
                                }
                            }
                        }
                        script {
                            env.SONAR_TASK_FRONTEND = readFile(
                                "${FRONTEND_DIR}/.scannerwork/report-task.txt"
                            ).trim()
                        }
                    }
                }
            }
        }

        stage('Quality Gates') {
            steps {
                script {
                    def gates = [
                        'Backend' : env.SONAR_TASK_BACKEND,
                        'Frontend': env.SONAR_TASK_FRONTEND
                    ]
                    def failed = []

                    gates.each { name, taskContent ->
                        def props = [:]
                        taskContent.split('\n').each { line ->
                            def idx = line.indexOf('=')
                            if (idx > 0) {
                                props[line.substring(0, idx).trim()] = line.substring(idx + 1).trim()
                            }
                        }

                        echo "Verification du Quality Gate ${name} (task ${props['ceTaskId']})"

                        timeout(time: 10, unit: 'MINUTES') {
                            def result = waitForQualityGate(
                                abortPipeline: false,
                                taskId: props['ceTaskId'],
                                serverUrl: props['serverUrl']
                            )
                            if (result.status != 'OK') {
                                failed << "${name} (${result.status})"
                            } else {
                                echo "Quality Gate ${name} : OK"
                            }
                        }
                    }

                    if (failed) {
                        error "Quality Gate en echec : ${failed.join(', ')}"
                    }
                }
            }
        }

        stage('Tests E2E - environnement iso-prod') {
            steps {
                script {
                    try {
                        sh """
                            docker compose -f ${E2E_COMPOSE} -p ${E2E_PROJECT} build --pull
                            docker compose -f ${E2E_COMPOSE} -p ${E2E_PROJECT} up -d --wait --wait-timeout 240
                        """

                        sh """
                            echo "Attente de la disponibilite de ${E2E_BASE_URL}"
                            for i in \$(seq 1 60); do
                              if curl -sf -o /dev/null ${E2E_BASE_URL}; then
                                echo "Application disponible"
                                exit 0
                              fi
                              sleep 3
                            done
                            echo "Timeout : application indisponible"
                            exit 1
                        """

                        dir("${FRONTEND_DIR}") {
                            sh 'npx playwright install chromium'
                            sh 'npx playwright test'
                        }
                    } finally {
                        sh """
                            docker compose -f ${E2E_COMPOSE} -p ${E2E_PROJECT} logs --no-color > e2e-stack.log 2>&1 || true
                            docker compose -f ${E2E_COMPOSE} -p ${E2E_PROJECT} down -v --remove-orphans || true
                        """
                    }
                }
            }
            post {
                always {
                    junit(
                        testResults: "${FRONTEND_DIR}/playwright-report/junit-results.xml",
                        allowEmptyResults: true
                    )
                    publishHTML(target: [
                        reportDir: "${FRONTEND_DIR}/playwright-report",
                        reportFiles: 'index.html',
                        reportName: 'Rapport Playwright E2E',
                        keepAll: true,
                        alwaysLinkToLastBuild: true,
                        allowMissing: true
                    ])
                    archiveArtifacts(
                        artifacts: 'e2e-stack.log',
                        allowEmptyArchive: true
                    )
                    archiveArtifacts(
                        artifacts: "${FRONTEND_DIR}/test-results/**/*",
                        allowEmptyArchive: true
                    )
                }
            }
        }

        stage('Archivage artefacts') {
            steps {
                archiveArtifacts(
                    artifacts: "${BACKEND_DIR}/target/*.jar",
                    fingerprint: true,
                    allowEmptyArchive: false
                )
            }
        }

        stage('Deploiement production') {
            when {
                branch 'main'
            }
            steps {
                script {
                    sh 'chmod +x scripts/generate-grafana-env.sh'
                    sh './scripts/generate-grafana-env.sh'

                    sh """
                        docker compose -f ${PROD_COMPOSE} build --pull
                        docker compose -f ${PROD_COMPOSE} up -d --remove-orphans --wait --wait-timeout 300
                    """
                }
            }
            post {
                success {
                    echo "Deploiement reussi - commit ${env.GIT_SHORT_SHA}"
                }
                failure {
                    echo "Echec du deploiement - verification de l'etat de la stack"
                    sh "docker compose -f ${PROD_COMPOSE} ps || true"
                    sh "docker compose -f ${PROD_COMPOSE} logs --tail=200 --no-color || true"
                }
            }
        }

        stage('Verification post-deploiement') {
            when {
                branch 'main'
            }
            steps {
                sh '''
                    echo "Verification du healthcheck applicatif"
                    for i in $(seq 1 30); do
                      if curl -sfk -o /dev/null https://localhost/; then
                        echo "Frontend production accessible"
                        exit 0
                      fi
                      sleep 5
                    done
                    echo "Frontend production inaccessible"
                    exit 1
                '''
            }
        }
    }

    post {
        always {
            sh 'docker image prune -f --filter "until=72h" || true'
            cleanWs(
                deleteDirs: true,
                notFailBuild: true,
                patterns: [
                    [pattern: '**/node_modules/**', type: 'EXCLUDE'],
                    [pattern: '**/.m2/**', type: 'EXCLUDE']
                ]
            )
        }
        success {
            echo "Pipeline termine avec succes"
        }
        unstable {
            echo "Pipeline instable - des tests ont echoue"
        }
        failure {
            echo "Pipeline en echec"
        }
    }
}
