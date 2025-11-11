plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.freefair.lombok") version "8.14.3"
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "io.freefair.lombok")

    group = "ru.yuraender"
    version = "1.0.0"

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    repositories {
        mavenCentral()
    }

    dependencies {

    }

    tasks.build {
        dependsOn(tasks.shadowJar)
    }

    tasks.jar.get().enabled = false
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.shadowJar {
        minimize()
        mergeServiceFiles()
        archiveClassifier.set("")
    }
}
