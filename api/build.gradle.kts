plugins {
    id("java")
}

group = "dev.speedslicer"
version = "1.0-SNAPSHOT"

sourceSets {
    main {
        java.srcDir("src/java")
    }
}
repositories {
    mavenCentral()
}

dependencies {
}

tasks.test {
    useJUnitPlatform()
}