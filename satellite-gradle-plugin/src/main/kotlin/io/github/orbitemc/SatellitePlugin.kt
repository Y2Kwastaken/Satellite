package io.github.orbitemc

import de.undercouch.gradle.tasks.download.Download
import de.undercouch.gradle.tasks.download.DownloadTaskPlugin
import io.github.orbitemc.extension.SatelliteExtension
import io.github.orbitemc.task.DownloadMinecraftMeta
import io.github.orbitemc.task.UnzipRemapJar
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
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.JavaExec
import kotlin.io.path.absolutePathString
import kotlin.io.path.notExists
import org.gradle.kotlin.dsl.*
import java.nio.file.Files
import java.util.Arrays

class SatellitePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply("java")
        project.plugins.apply(DownloadTaskPlugin::class.java)

        val satellite = project.extensions.create(SATELLITE, SatelliteExtension::class.java, project, project.objects)

        val bundled by project.configurations.registering
        project.configurations.named("implementation") {
            extendsFrom(bundled.get())
        }

        val downloadMinecraftMeta =
            project.tasks.register(DOWNLOAD_MINECRAFT_META_TASK, DownloadMinecraftMeta::class.java) {
                group = SATELLITE_SETUP_CATEGORY
                minecraftVersion.set(satellite.minecraftVersion)
                outputDirectory.set(satellite.workingDirectory)
            }

        val downloadMinecraftBootstrapJar =
            project.tasks.register(DOWNLOAD_MINECRAFT_BOOTSTRAP_JAR_TASK, DownloadMinecraftBootstrapJar::class.java) {
                group = SATELLITE_SETUP_CATEGORY
                minecraftVersion.set(satellite.minecraftVersion)
                outputDirectory.set(satellite.workingDirectory)

                dependsOn(downloadMinecraftMeta)
            }

        val downloadMinecraftMappings =
            project.tasks.register(DOWNLOAD_MINECRAFT_MAPPINGS_TASK, DownloadMinecraftMappings::class.java) {
                group = SATELLITE_SETUP_CATEGORY
                minecraftVersion.set(satellite.minecraftVersion)
                outputDirectory.set(satellite.workingDirectory)

                dependsOn(downloadMinecraftMeta)
            }

        val downloadVineFlower = project.tasks.register(DOWNLOAD_VINEFLOWER_TASK, Download::class.java) {
            group = SATELLITE_SETUP_CATEGORY
            src(getVineFlowerLink(satellite.vineflowerVersion.get()))
            dest(satellite.workingDirectory.asFile().resolve("vineflower.jar"))
            overwrite(false)

            onlyIf {
                return@onlyIf satellite.workingDirectory.asPath().resolve("vineflower.jar").notExists()
            }
        }

        val convertMinecraftMappings =
            project.tasks.register(CONVERT_MINECRAFT_MAPPINGS_TASK, ConvertMinecraftMappings::class.java) {
                group = SATELLITE_SETUP_CATEGORY
                minecraftVersion.set(satellite.minecraftVersion)
                outputDirectory.set(satellite.workingDirectory)

                dependsOn(downloadMinecraftMappings)
            }

        val extractMinecraftBootstrapJar =
            project.tasks.register(EXTRACT_MINECRAFT_BOOTSTRAP_JAR_TASK, ExtractMinecraftBootstrapJar::class.java) {
                group = SATELLITE_SETUP_CATEGORY
                minecraftVersion.set(satellite.minecraftVersion)
                outputDirectory.set(satellite.workingDirectory)

                dependsOn(downloadMinecraftBootstrapJar)
            }

        val remapMinecraftServerJar =
            project.tasks.register(REMAP_MINECRAFT_JAR_TASK, RemapMinecraftServerJar::class.java) {
                group = SATELLITE_SETUP_CATEGORY
                minecraftVersion.set(satellite.minecraftVersion)
                outputDirectory.set(satellite.workingDirectory)

                dependsOn(extractMinecraftBootstrapJar, convertMinecraftMappings)
            }

        val unzipRemapJar = project.tasks.register(UNZIP_REMAP_JAR_TASK, UnzipRemapJar::class.java) {
            group = SATELLITE_SETUP_CATEGORY
            minecraftVersion.set(satellite.minecraftVersion)
            outputDirectory.set(satellite.workingDirectory)

            dependsOn(remapMinecraftServerJar)
        }

        val decompileRemapClasses = project.tasks.register(DECOMPILE_REMAP_CLASSES_TASK, JavaExec::class.java) {
            group = SATELLITE_SETUP_CATEGORY

            mainClass.set("org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler")
            workingDir = satellite.workingDirectory.asFile()
            classpath("${satellite.workingDirectory.asPath().absolutePathString()}/vineflower.jar")
            satellite.decompilerArguments.get().forEach { args(it) }
            args(
                getVersionDecompileClassDir(satellite.workingDirectory.asPath(), satellite.minecraftVersion.get()),
                getVersionDecompileJavaDir(satellite.workingDirectory.asPath(), satellite.minecraftVersion.get())
            )

            dependsOn(unzipRemapJar, downloadVineFlower)

            onlyIf {
                return@onlyIf getVersionDecompileJavaDir(
                    satellite.workingDirectory.asPath(), satellite.minecraftVersion.get()
                ).notExists()
            }
        }

        val copyJavaMinecraftFiles = project.tasks.register(COPY_JAVA_MINECRAFT_FILES_TASK, Copy::class.java) {
            group = SATELLITE_SETUP_CATEGORY
            from(
                getVersionDecompileJavaDir(
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
                getVersionDecompileJavaDir(
                    satellite.workingDirectory.asPath(), satellite.minecraftVersion.get()
                ).toFile()
            )
            patchDirectory.set(project.file("patches"))
        }

        project.tasks.register(SATELLITE_APPLY_PATCHES_TASK, ApplyPatches::class.java) {
            group = SATELLITE_CATEGORY

            javaSource.set(project.file("src/main/java"))
            decompileSource.set(
                getVersionDecompileJavaDir(
                    satellite.workingDirectory.asPath(), satellite.minecraftVersion.get()
                ).toFile()
            )
            patchDirectory.set(project.file("patches"))
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
                    getVersionLibrariesFile(satellite.workingDirectory.asPath(), satellite.minecraftVersion.get())
                val libraries = Files.readAllLines(librariesPath).map {
                    it.trim().split("\t")[1]
                }

                for (library in libraries) {
                    "bundled"(library)
                }
            }
        }
    }
}
