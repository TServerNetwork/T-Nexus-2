plugins {
	java
	id("xyz.jpenilla.run-paper") version "3.0.2"
}

group = "network.tserver"

val semanticVersion = providers.gradleProperty("version").get()
val versionSuffix   = providers.gradleProperty("versionSuffix")
                               .orNull
							   ?.trim()
							   .orEmpty()

require(Regex("""\d+\.\d+\.\d+""").matches(semanticVersion)) {
	"version must use numeric Semantic Versioning, for example: 2.0.0"
}

require(
	versionSuffix.isEmpty() ||
	Regex("""[0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*""").matches(versionSuffix)
) {
	"versionSuffix must be empty or a valid pre-release identifier."
}
val versionWithSuffix = if (versionSuffix.isEmpty()) {
	semanticVersion
} else {
	"$semanticVersion-$versionSuffix"
}

val buildNumberFile = layout.projectDirectory.file(".build-number").asFile

val currentBuildNumber = if (buildNumberFile.exists()) {
	buildNumberFile.readText()
	               .trim()
				   .toIntOrNull()
				   ?: error(".build-number must contain an integer.")
} else {
	0
}
val buildNumber = currentBuildNumber + 1

val artifactVersion = "$versionWithSuffix-b$buildNumber"

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

val incrementBuildNumber by tasks.registering {
	group = "build"
	description = "Persists the build number used by the generated JAR."

	dependsOn(tasks.named("classes"))
	outputs.upToDateWhen { false }

	doLast {
		buildNumberFile.writeText(buildNumber.toString())
	}
}

tasks {
	processResources {
		inputs.property("version", artifactVersion)

		filesMatching("paper-plugin.yml") {
			expand("version" to artifactVersion)
		}
	}
	jar {
		dependsOn(incrementBuildNumber)

		archiveBaseName.set("T-Nexus")
		archiveVersion.set(artifactVersion)
	}
	runServer {
		minecraftVersion("26.1.2")
	}
}