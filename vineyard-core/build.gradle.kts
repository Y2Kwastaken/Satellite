plugins {
    id("vineyard-java")
}

dependencies {
    compileOnly(libs.jetbrains.annotations)
    api(libs.gson)
    api(libs.tiny.remapper)
    api(libs.mapping.io)
}
