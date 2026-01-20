package com.elvish.plugin.textmate

import org.jetbrains.plugins.textmate.api.TextMateBundleProvider
import org.jetbrains.plugins.textmate.api.TextMateBundleProvider.PluginBundle
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Path

class ElvishTextMateBundleProvider : TextMateBundleProvider {
    override fun getBundles(): List<PluginBundle> {
        val bundleUrl = javaClass.getResource("/textmate") ?: return emptyList()

        val path = try {
            // Try direct path first (works in dev mode)
            Path.of(bundleUrl.toURI())
        } catch (e: Exception) {
            // For JAR resources, we need to handle differently
            // Extract the bundle path from the URL
            val urlString = bundleUrl.toString()
            if (urlString.startsWith("jar:")) {
                // Extract path within JAR and use FileSystem
                val jarUri = URI.create(urlString.substringBefore("!"))
                val pathInJar = urlString.substringAfter("!")
                try {
                    val fs = FileSystems.newFileSystem(jarUri, emptyMap<String, Any>())
                    fs.getPath(pathInJar)
                } catch (e2: Exception) {
                    return emptyList()
                }
            } else {
                return emptyList()
            }
        }

        return listOf(PluginBundle("Elvish", path))
    }
}
