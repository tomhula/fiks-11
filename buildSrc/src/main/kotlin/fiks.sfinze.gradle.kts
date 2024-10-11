plugins {
    kotlin("jvm")
    id("com.gradleup.shadow")
    application
}

group = "me.tomasan7"
version = "1.0"

repositories {
    mavenCentral()
}

application {
    mainClass = "MainKt"
}

tasks {
    shadowJar {
        archiveFileName = project.name + ".jar"
    }
}

/*
tasks {
    @OptIn(InternalKotlinGradlePluginApi::class)
    withType<KotlinJvmRun> {
        val inputFile = file("stdin.txt")
        standardInput = if (inputFile.exists())
            inputFile.inputStream()
        else
            System.`in`

        doFirst {
            if (inputFile.exists())
                println("Using ${inputFile.name} as standard input.")
        }
    }

    val testInputOutput by registering {
        group = "verification"
        description = "Builds an executable and tests inputs and outputs."
        dependsOn(nativeMainBinaries)

        val executableFilePath =
            layout.buildDirectory.file("bin/native/debugExecutable/${project.name}.kexe").get().asFile.absolutePath
        val tempFile = File(temporaryDir, "output")
        val testDir = file("tests")
        val inputRegex = """(\d+).in""".toRegex()

        doFirst {
            val ioFiles = testDir.listFiles()!!

            if (ioFiles.count() % 2 != 0)
                throw GradleException("Test files are not in pairs.")

            val ioPairs = ioFiles
                .filter { it.name.matches(inputRegex) }
                .map { inputFile ->
                    val matchResult = inputRegex.matchEntire(inputFile.name)
                    val number = matchResult?.groupValues?.get(1)?.toInt()
                    val outputFile = ioFiles.find { it.name == "$number.out" }
                        ?: throw GradleException("Matching output file for ${inputFile.name} not found.")
                    number!! to (inputFile to outputFile)
                }
                .sortedBy { it.first }
                .map { it.second }

            for ((inputFile, outputFile) in ioPairs)
            {
                val process = ProcessBuilder(executableFilePath)
                    .redirectInput(inputFile)
                    .redirectOutput(tempFile)
                    .start()

                val exitCode = process.waitFor()
                if (exitCode != 0)
                    throw GradleException("Executable process failed for ${inputFile.name}. Exit code: $exitCode")

                val expectedOutput = outputFile.readText().trim()
                val actualOutput = tempFile.readText().trim()

                if (expectedOutput == actualOutput)
                    println("Passed: ${inputFile.name}")
                else
                    throw GradleException("""
                        |Test failed for ${inputFile.name}:
                        |EXPECTED:
                        |$expectedOutput
                        |GOT:
                        |$actualOutput
                    """.trimMargin()
                    )
            }
        }
    }
}
*/

