import org.jetbrains.kotlin.org.apache.commons.io.output.ByteArrayOutputStream

plugins {
    kotlin("multiplatform")
}

group = "me.tomasan7"
version = "1.0"

repositories {
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when
    {
        hostOs == "Mac OS X" && isArm64 -> macosArm64("native")
        hostOs == "Mac OS X" && !isArm64 -> macosX64("native")
        hostOs == "Linux" && isArm64 -> linuxArm64("native")
        hostOs == "Linux" && !isArm64 -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }
}

tasks {
    val nativeMainBinaries by getting

    val testInputOutput by registering {
        group = "verification"
        description = "Builds an executable and tests inputs and outputs."
        dependsOn(nativeMainBinaries)

        val executableFilePath = layout.buildDirectory.file("bin/native/debugExecutable/${project.name}.kexe").get().asFile.absolutePath
        val tempFile = File(temporaryDir, "output.txt")
        val testDir = file("tests")
        val inputRegex = """input(\d*)\.txt""".toRegex()

        doFirst {
            val ioFiles = testDir.listFiles()

            if (ioFiles.count() % 2 != 0)
                throw GradleException("Test files are not in pairs.")

            val ioPairs = ioFiles
                .filter { it.name.matches(inputRegex) }
                .map { inputFile ->
                    val matchResult = inputRegex.matchEntire(inputFile.name)
                    val number = matchResult?.groupValues?.get(1)
                    val outputFile = ioFiles.find { it.name == "output$number.txt" }
                        ?: throw GradleException("Matching output file for ${inputFile.name} not found.")
                    inputFile to outputFile
                }

            for ((inputFile, outputFile) in ioPairs)
            {
                val process = ProcessBuilder(executableFilePath)
                    .redirectInput(inputFile)
                    .redirectOutput(tempFile)
                    .start()

                val exitCode = process.waitFor()
                if (exitCode != 0) {
                    throw GradleException("Executable process failed for ${inputFile.name}. Exit code: $exitCode")
                }

                val expectedOutput = outputFile.readText().trim()
                val actualOutput = tempFile.readText().trim()

                if (expectedOutput == actualOutput)
                    println("Passed: ${inputFile.name}")
                else
                    throw GradleException("""
                        Test failed for ${inputFile.name}:
                        EXPECTED: 
                        $expectedOutput
                        GOT:      
                        $actualOutput
                    """.trimIndent())
            }
        }
    }
}

