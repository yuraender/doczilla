val jettyVersion = "9.4.58.v20250814"

dependencies {
    implementation("org.eclipse.jetty:jetty-server:${jettyVersion}")
    implementation("org.eclipse.jetty:jetty-servlet:${jettyVersion}")
    implementation("redis.clients:jedis:6.2.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.1")
    implementation("org.knowm.xchart:xchart:3.8.8")
}

tasks.shadowJar {
    archiveFileName.set("ForecastService.jar")
    manifest {
        attributes["Main-Class"] = "doczilla.forecast.Main"
    }
}
