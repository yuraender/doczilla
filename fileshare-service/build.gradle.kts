val jettyVersion = "11.0.26"

dependencies {
    implementation("org.eclipse.jetty:jetty-server:$jettyVersion")
    implementation("org.eclipse.jetty:jetty-servlet:$jettyVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.1")
}

tasks.shadowJar {
    archiveFileName.set("FileShareService.jar")
    manifest {
        attributes["Main-Class"] = "doczilla.fileshare.Main"
    }
}
