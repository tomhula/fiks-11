plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "fiks-11"
include("trasa")
project(":trasa").projectDir = file("kolo1/trasa")