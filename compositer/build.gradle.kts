plugins {
    id("java")
    id("application")
    id("com.gradleup.shadow") version "9.6.1"

}

group = "dev.speedslicer"
version = "0.0"

application {
    mainClass = "net.ada.compositer.Main"
}

sourceSets {
    main {
        java.srcDir("src/main/java")
        java.srcDir("../manifest/src/manifests/java")
    }
}
repositories {
    mavenCentral()
}
dependencies {
    implementation("net.lenni0451.classtransform:core:1.15.1")
    implementation("net.lenni0451.classtransform:mixinstranslator:1.15.1")
    implementation("net.lenni0451.classtransform:mixinsdummy:1.15.1")
    implementation("net.lenni0451.classtransform:additionalclassprovider:1.15.1")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("commons-io:commons-io:2.22.0")
    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")
}