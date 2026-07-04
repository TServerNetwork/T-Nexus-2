plugins {
	java
	id("xyz.jpenilla.run-paper") version "3.0.2"
}

group = "network.tserver"
version = "2.0.0-SNAPSHOT"

repositories {
	maven {
		name = "papermc"
		url = uri("https://repo.papermc.io/repository/maven-public/")
	}
}

dependencies {
	compileOnly("io.papermc.paper:paper-api:26.1.2.build.72-stable")
}

java {
	toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks {
	runServer {
		minecraftVersion("26.1.2")
	}
}