plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.20")
    implementation("com.gradleup.shadow:shadow-gradle-plugin:8.3.3")
}