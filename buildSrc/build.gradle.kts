import org.gradle.kotlin.dsl.support.expectedKotlinDslPluginsVersion

plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.gradle.shadow.plugin)
    implementation(libs.gradle.kotlin.dsl.plugin.withVersion(expectedKotlinDslPluginsVersion))
    implementation(libs.gradle.kotlin.plugin.withVersion(embeddedKotlinVersion))
    implementation(libs.gradle.plugin.publish.plugin)
}

fun Provider<MinimalExternalModuleDependency>.withVersion(version: String): Provider<String> {
    return map { "${it.module.group}:${it.module.name}:$version" }
}
