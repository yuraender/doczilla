val jettyVersion = "9.4.58.v20250814"
val jacksonVersion = "2.20.1"

dependencies {
    implementation("org.eclipse.jetty:jetty-server:$jettyVersion")
    implementation("org.eclipse.jetty:jetty-servlet:$jettyVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
}

tasks.shadowJar {
    archiveFileName.set("FileShareService.jar")
    manifest {
        attributes["Main-Class"] = "doczilla.fileshare.Main"
    }
}
