/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2020-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
 
String getDiscordMessage() {
    def msg = "**Status:** " + currentBuild.currentResult.toLowerCase() + "\n**Branch:** ${BRANCH_NAME}\n**Changes:**\n"
    if (!currentBuild.changeSets.isEmpty()) {
        currentBuild.changeSets.first().getLogs().each {
            msg += "- `" + it.getCommitId().substring(0, 8) + "` *" + it.getComment().substring(0, Math.min(64, it.getComment().length() - 1)) + (it.getComment().length() - 1 > 64 ? "..." : "") + "*\n"
        }
    } else {
        msg += "- no changes\n"
    }

    msg += "\n**Artifacts:**\n"
    currentBuild.rawBuild.getArtifacts().each {
        msg += "- [" + it.getDisplayPath() + "](" + env.BUILD_URL + "artifact/" + it.getHref() + ")\n"
    }

    return msg.length() > 2048 ? msg.substring(0, 2045) + "..." : msg
}

pipeline {
    agent any
    tools {
        git "Default"
        jdk "jdk8"
    }
    options {
        buildDiscarder(logRotator(artifactNumToKeepStr: '5'))
    }
    stages {
        stage("Prepare Workspace") {
            steps {
                sh "./gradlew setupCiWorkspace"
            }
        }
        stage("Build") {
            steps {
                sh "./gradlew build"
            }
            post {
                success {
                    archiveArtifacts artifacts: "build/libs/*.jar", fingerprint: true
                }
            }
        }
        stage("Deploy") {
            when {
                branch "master"
            }
            steps {
                sh "./gradlew publish"
            }
        }
    }

    post {
        always {
            sh "./gradlew --stop"
            deleteDir()

            withCredentials([string(credentialsId: "valkyrien_skies_discord_webhook", variable: "discordWebhook")]) {
                discordSend thumbnail: "https://static.miraheze.org/valkyrienskieswiki/6/63/Logo_128.png",
                        result: currentBuild.currentResult,
                        description: getDiscordMessage(),
                        link: env.BUILD_URL,
                        title: "vw-world:${BRANCH_NAME} #${BUILD_NUMBER}",
                        webhookURL: "${discordWebhook}"
            }
        }
    }
}
