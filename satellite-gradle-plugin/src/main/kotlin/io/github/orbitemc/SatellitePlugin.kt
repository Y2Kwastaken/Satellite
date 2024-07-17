package io.github.orbitemc

import de.undercouch.gradle.tasks.download.Download
import de.undercouch.gradle.tasks.download.DownloadTaskPlugin
import io.github.orbitemc.extension.SatelliteExtension
import io.github.orbitemc.task.DownloadMinecraftMeta
import io.github.orbitemc.task.UnzipRemapJar
import io.github.orbitemc.task.build.GenerateLibrariesList
import io.github.orbitemc.task.build.GenerateVersionsList
import io.github.orbitemc.task.mapping.ConvertMinecraftMappings
import io.github.orbitemc.task.mapping.DownloadMinecraftMappings
import io.github.orbitemc.task.patch.ApplyPatches
import io.github.orbitemc.task.patch.BuildPatches
import io.github.orbitemc.task.server.DownloadMinecraftBootstrapJar
import io.github.orbitemc.task.server.ExtractMinecraftBootstrapJar
import io.github.orbitemc.task.server.RemapMinecraftServerJar
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.JavaExec
import org.gradle.crypto.checksum.Checksum
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.registering
import org.gradle.kotlin.dsl.repositories
import java.nio.file.Files
import kotlin.io.path.absolutePathString
import kotlin.io.path.notExists
import kotlin.math.min

class SatellitePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply("java")
        project.plugins.apply(DownloadTaskPlugin::class.java)

        val satellite = project.extensions.create(SATELLITE, SatelliteExtension::class.java, project, project.objects)

        val minecraft by project.configurations.registering
        val bundled by project.configurations.registering
        project.configurations.named("implementation") {
            extendsFrom(bundled.get())
        }

        val downloadMinecraftMeta by project.tasks.register(
            DOWNLOAD_MINECRAFT_META_TASK, DownloadMinecraftMeta::class.java
        ) {
            group = SATELLITE_SETUP_CATEGORY
            minecraftVersion.set(satellite.minecraftVersion)
            outputDirectory.set(satellite.workingDirectory)
        }

        val downloadMinecraftBootstrapJar by project.tasks.register(
            DOWNLOAD_MINECRAFT_BOOTSTRAP_JAR_TASK, DownloadMinecraftBootstrapJar::class
        ) {
            group = SATELLITE_SETUP_CATEGORY
            minecraftVersion.set(satellite.minecraftVersion)
            outputDirectory.set(satellite.workingDirectory)

            dependsOn(downloadMinecraftMeta)
        }

        val downloadMinecraftMappings by project.tasks.register(
            DOWNLOAD_MINECRAFT_MAPPINGS_TASK, DownloadMinecraftMappings::class.java
        ) {
            group = SATELLITE_SETUP_CATEGORY
            minecraftVersion.set(satellite.minecraftVersion)
            outputDirectory.set(satellite.workingDirectory)

            dependsOn(downloadMinecraftMeta)
        }

        val downloadVineFlower by project.tasks.register(DOWNLOAD_DECOMPILER_TASK, Download::class.java) {
            group = SATELLITE_SETUP_CATEGORY
            src(satellite.decompilerDownloadLink.get())
            dest(satellite.workingDirectory.asFile().resolve("decompiler.jar"))
            overwrite(false)

            onlyIf {
                return@onlyIf satellite.workingDirectory.asPath().resolve("decompiler.jar").notExists()
            }
        }

        val convertMinecraftMappings by project.tasks.register(
            CONVERT_MINECRAFT_MAPPINGS_TASK, ConvertMinecraftMappings::class.java
        ) {
            group = SATELLITE_SETUP_CATEGORY
            minecraftVersion.set(satellite.minecraftVersion)
            outputDirectory.set(satellite.workingDirectory)

            dependsOn(downloadMinecraftMappings)
        }

        val extractMinecraftBootstrapJar by project.tasks.register(
            EXTRACT_MINECRAFT_BOOTSTRAP_JAR_TASK, ExtractMinecraftBootstrapJar::class.java
        ) {
            group = SATELLITE_SETUP_CATEGORY
            minecraftVersion.set(satellite.minecraftVersion)
            outputDirectory.set(satellite.workingDirectory)

            dependsOn(downloadMinecraftBootstrapJar)
        }

        val remapMinecraftServerJar by project.tasks.register(
            REMAP_MINECRAFT_JAR_TASK, RemapMinecraftServerJar::class.java
        ) {
            group = SATELLITE_SETUP_CATEGORY
            minecraftVersion.set(satellite.minecraftVersion)
            outputDirectory.set(satellite.workingDirectory)

            dependsOn(extractMinecraftBootstrapJar, convertMinecraftMappings)
        }

        val unzipRemapJar by project.tasks.register(UNZIP_REMAP_JAR_TASK, UnzipRemapJar::class.java) {
            group = SATELLITE_SETUP_CATEGORY
            minecraftVersion.set(satellite.minecraftVersion)
            outputDirectory.set(satellite.workingDirectory)

            dependsOn(remapMinecraftServerJar)
        }

        val decompileRemapClasses by project.tasks.register(DECOMPILE_REMAP_CLASSES_TASK, JavaExec::class.java) {
            group = SATELLITE_SETUP_CATEGORY

            workingDir = satellite.workingDirectory.asFile()
            classpath("${satellite.workingDirectory.asPath().absolutePathString()}/decompiler.jar")
            bundled.get().files.stream().map { "--add-external=${it.absolutePath}" }.forEach { args(it) }
            satellite.decompilerArguments.get().forEach { args(it) }
            args(
                getClassesFolder(satellite.workingDirectory.asPath(), satellite.minecraftVersion.get()),
                getJavaFolder(satellite.workingDirectory.asPath(), satellite.minecraftVersion.get())
            )

            dependsOn(unzipRemapJar, downloadVineFlower)

            onlyIf {
                return@onlyIf getJavaFolder(
                    satellite.workingDirectory.asPath(), satellite.minecraftVersion.get()
                ).notExists()
            }
        }

        val copyJavaMinecraftFiles by project.tasks.register(COPY_JAVA_MINECRAFT_FILES_TASK, Copy::class.java) {
            group = SATELLITE_SETUP_CATEGORY
            from(
                getJavaFolder(
                    satellite.workingDirectory.asPath(), satellite.minecraftVersion.get()
                )
            )
            into(project.file("src/main/java"))

            dependsOn(decompileRemapClasses)
        }

        project.tasks.register(SATELLITE_SETUP_TASK, DefaultTask::class.java) {
            group = SATELLITE_CATEGORY

            dependsOn(copyJavaMinecraftFiles)
        }

        project.tasks.register(SATELLITE_BUILD_PATCHES_TASK, BuildPatches::class.java) {
            group = SATELLITE_CATEGORY

            javaSource.set(project.file("src/main/java"))
            decompileSource.set(
                getJavaFolder(
                    satellite.workingDirectory.asPath(), satellite.minecraftVersion.get()
                ).toFile()
            )
            patchDirectory.set(project.file("patches"))
        }

        project.tasks.register(SATELLITE_APPLY_PATCHES_TASK, ApplyPatches::class.java) {
            group = SATELLITE_CATEGORY

            javaSource.set(project.file("src/main/java"))
            decompileSource.set(
                getJavaFolder(
                    satellite.workingDirectory.asPath(), satellite.minecraftVersion.get()
                ).toFile()
            )
            patchDirectory.set(project.file("patches"))
        }

        val javaExtension = project.extensions.getByType(JavaPluginExtension::class.java)
        val bootstrapSourceSet = javaExtension.sourceSets.create("bootstrap") {
            java.srcDirs("src/bootstrap/java")
        }

        val buildJar by project.tasks.register(SATELLITE_BUILD_JAR_TASK, Jar::class.java) {
            group = SATELLITE_ASSEMBLE_CATEGORY

            from(javaExtension.sourceSets.named("main").get().output)
            from(project.zipTree(minecraft.get().files.first())) {
                exclude("com/mojang/**")
                exclude("net/minecraft/**")
                exclude("META-INF/**")
            }
        }

        val buildChecksums by project.tasks.register(SATELLITE_BUILD_CHECKSUMS_TASK, Checksum::class) {
            group = SATELLITE_ASSEMBLE_CATEGORY

            val from = bundled.get().files.toMutableList()
            from.add(buildJar.outputs.files.singleFile)

            inputFiles.setFrom(from)
            outputDirectory.set(project.layout.buildDirectory.dir("checksums"))
            checksumAlgorithm.set(Checksum.Algorithm.SHA512)
            appendFileNameToChecksum.set(true)

            dependsOn(buildJar)
        }

        val generateLibrariesList by project.tasks.register(
            SATELLITE_GENERATE_LIBRARIES_LIST_TASK, GenerateLibrariesList::class
        ) {
            group = SATELLITE_ASSEMBLE_CATEGORY

            checksumDirectory.set(project.layout.buildDirectory.dir("checksums"))
            librariesList.set(satellite.buildDirectory.asFile().resolve("libraries.list"))

            dependsOn(buildChecksums)
        }

        val generateVersionsList by project.tasks.register(
            SATELLITE_GENERATE_VERSIONS_LIST_TASK, GenerateVersionsList::class
        ) {
            group = SATELLITE_ASSEMBLE_CATEGORY

            jarOutput.set(buildJar.outputs.files.singleFile)
            checksumDirectory.set(project.layout.buildDirectory.dir("checksums"))
            versionsList.set(satellite.buildDirectory.asFile().resolve("versions.list"))

            dependsOn(buildChecksums)
        }

        val bootstrapBuild by project.tasks.register(SATELLITE_BUILD_TASK, Jar::class.java) {
            group = SATELLITE_ASSEMBLE_CATEGORY
            archiveAppendix.set("bootstrap")

            from(bootstrapSourceSet.output)
            into("META-INF/versions") {
                from(buildJar.outputs.files.singleFile)
            }
            into("META-INF") {
                from(generateLibrariesList.librariesList.asFile())
            }
            into("META-INF") {
                from(generateVersionsList.versionsList.asFile())
            }
            into("META-INF/libraries") {
                from(bundled.get().files)
                from(minecraft.get().files)
            }

            dependsOn(generateLibrariesList, generateVersionsList)
        }

        project.tasks.register(SATELLITE_ASSEMBLE_PROJECT_TASK, DefaultTask::class) {
            dependsOn(bootstrapBuild)
        }

        project.afterEvaluate {
            repositories {
                maven("https://libraries.minecraft.net/") {
                    name = "Minecraft"
                }
                mavenCentral()
            }

            dependencies {
                val librariesPath =
                    getLibrariesList(satellite.workingDirectory.asPath(), satellite.minecraftVersion.get())

                if (librariesPath.notExists()) {
                    return@dependencies
                }

                val libraries = Files.readAllLines(librariesPath).map {
                    it.trim().split("\t")[1]
                }

                for (library in libraries) {
                    "bundled"(library)
                }

                "minecraft"(
                    files(
                        getRemappedServer(
                            satellite.workingDirectory.asPath(),
                            satellite.minecraftVersion.get()
                        )
                    )
                )
            }
        }
    }
}
