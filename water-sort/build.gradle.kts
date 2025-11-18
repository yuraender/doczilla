tasks.shadowJar {
    archiveFileName.set("WaterSort.jar")
    manifest {
        attributes["Main-Class"] = "doczilla.watersort.Main"
    }
}
