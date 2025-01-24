plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "fiks-11"

include("trasa")
project(":trasa").projectDir = file("kolo1/trasa")

include("posadka")
project(":posadka").projectDir = file("kolo1/posadka")

include("zavody")
project(":zavody").projectDir = file("kolo2/zavody")

include("nadrze")
project(":nadrze").projectDir = file("kolo2/nadrze")

include("hvezdy")
project(":hvezdy").projectDir = file("kolo3/hvezdy")

include("prohledavanivesmiru")
project(":prohledavanivesmiru").projectDir = file("kolo4/prohledavanivesmiru")
