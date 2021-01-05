pipeline {
    agent any

    // tools {
    //     // Install the Maven version configured as "M3" and add it to the path.
    //     maven "M3"
    // }
    triggers { pollSCM('* * * * *')}
    stages {
        stage('Checkout') {
            steps {
                // Get some code from a GitHub repository
                git branch: 'main', url: 'https://github.com/markp112/jgsu-spring-petclinic.git'
                
            }
        }
        stage('Build') {
            steps {

                // Run Maven on a Unix agent.
                // sh "mvn -Dmaven.test.failure.ignore=true clean package"

                // To run Maven on a Windows agent, use
                sh './mvnw clean package'
            }

            post {
                // If Maven was able to run the tests, even if some of the test
                // failed, record the test results and archive the jar file.
                always {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts 'target/*.jar'
                // }
                // changed {
                  emailext subject: "job \'${JOB_NAME}\' (${BUILD_NUMBER}) is waiting for input",
                    attachLog: 'true', 
                    body: 'Please go to ${BUILD_URL} and verify the build', 
                    compressLog: true, 
                    recipientProviders: [upstreamDevelopers()], 
                    to: 'test@jenkins'
                }
            }
        }
    }
}
