plugins {
    id("java")
    id("maven-publish")
}

group = rootProject.group
version = rootProject.version

subprojects {
    version = rootProject.version.toString()
}
