plugins {
    id("vineyard-java")
}

dependencies {
    compileOnly(libs.jetbrains.annotations)
    implementation(libs.gson)
    implementation(libs.tiny.remapper)
    implementation(libs.mapping.io)
}
