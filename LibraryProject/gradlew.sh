#!/usr/bin/env bash
# Gradle Wrapper generator

GRADLE_VERSION="8.5"
PROJECT_DIR="/home/atambek/LibraryProject/LibraryProject"

# Download gradle wrapper
mkdir -p "$PROJECT_DIR/gradle/wrapper"

# Download gradle wrapper jar
WRAPPER_JAR_URL="https://github.com/gradle/gradle/releases/download/v${GRADLE_VERSION}/gradle-${GRADLE_VERSION}-bin.zip"

# For simplicity, let's try using sdk if available
which sdk || echo "SDKMAN not available"

# Alternative: use gradle from snap
which gradle || echo "Gradle not in PATH"

# Try to install gradle via snap
snap install gradle --classic 2>/dev/null && echo "Gradle installed via snap" || echo "Snap install failed"