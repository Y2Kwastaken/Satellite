package io.github.kryptidemc.extension

import org.gradle.api.model.ObjectFactory

abstract class VineyardExtension(factory: ObjectFactory) {
    val minecraftVersion = factory.property(String::class.java)
    val decompilerArguments = factory.listProperty(String::class.java)

    init {
        decompilerArguments.convention(
            listOf(
                "-udv=1",
                "-ump=0",
                "-asc=1",
                "-rbr=0"
            )
        )
    }
}
