plugins {
    `vineyard-kotlin`
    `vineyard-kotlin-publish`
}

dependencies {
    shade(project(":vineyard-core"))
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
