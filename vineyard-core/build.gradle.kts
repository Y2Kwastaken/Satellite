plugins {
    id("vineyard-java")
}

dependencies {
    compileOnly("org.jetbrains:annotations-java5:24.0.1")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("net.fabricmc:tiny-remapper:0.10.3")
    implementation("net.fabricmc:mapping-io:0.6.1")
}
