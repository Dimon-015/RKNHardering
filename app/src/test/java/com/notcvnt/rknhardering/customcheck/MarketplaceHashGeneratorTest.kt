package com.notcvnt.rknhardering.customcheck

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

/**
 * Utility test that recomputes [CustomCheckSerializer.canonicalHash] for every
 * profile under `marketplace/checks/` and prints the result. Maintainers run this
 * test after editing a profile to update the `expected_hash` field in
 * `marketplace/catalog.json` accordingly.
 *
 * Not an assertion test — it always passes; the output is consumed manually.
 */
@RunWith(RobolectricTestRunner::class)
class MarketplaceHashGeneratorTest {

    @Test
    fun `print canonical hash for every marketplace profile`() {
        val root = File(System.getProperty("user.dir") ?: ".")
            .let { dir -> generateSequence(dir) { it.parentFile }.first { File(it, "marketplace").isDirectory } }
        val checksDir = File(root, "marketplace/checks")
        check(checksDir.isDirectory) { "marketplace/checks not found under $root" }
        val files = checksDir.listFiles { f -> f.extension == "rkncheck" }.orEmpty().sortedBy { it.name }
        check(files.isNotEmpty()) { "no .rkncheck profiles in $checksDir" }
        println("=== marketplace canonical hashes ===")
        files.forEach { file ->
            val profile = CustomCheckSerializer.deserialize(file.readText())
            val hash = CustomCheckSerializer.canonicalHash(profile)
            println("${file.name}  $hash")
        }
        println("====================================")
    }
}
