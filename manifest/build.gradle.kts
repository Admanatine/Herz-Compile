plugins {
    id("java")
    id("application")
    id("com.gradleup.shadow") version "9.6.1"
}

group = "net.ada"
version = "0"

sourceSets {
    main {
        java.srcDir("src/main/java")
        java.srcDir("src/manifests/java")
    }
}
application {
    mainClass = "net.ada.manifest.Main"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.14.0")
}

