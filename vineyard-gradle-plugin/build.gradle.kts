plugins {
    `vineyard-kotlin`
    `vineyard-kotlin-publish`
}

dependencies {
    compileOnly(gradleApi())
    implementation(project(":vineyard-core"))
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

gradlePlugin {
    plugins {
        create("vineyard") {
            id = "io.github.cabernetmc.vineyard"
            implementationClass = "io.github.cabernetmc.VineyardInternalsPlugin"
            displayName = "Vineyard"
            description = "Environment setup for CabernetMC"
        }
    }
}
