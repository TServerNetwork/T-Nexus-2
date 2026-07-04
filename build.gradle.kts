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

val buildNumber = providers.gradleProperty("buildNumber")
                           .orElse(providers.environmentVariable("GITHUB_RUN_NUMBER"))
						   .getOrElse("0")
						   .trim()

require(Regex("""\d+""").matches(buildNumber)) {
	"buildNumber must be a non-negative integer."
}

val isGitHubActions = providers.environmentVariable("GITHUB_ACTIONS")
                               .map { it.equals("true", ignoreCase = true) }
							   .getOrElse(false)

val localSuffix = if (isGitHubActions) "" else "-local"
val artifactVersion = "$versionWithSuffix-b$buildNumber$localSuffix"

version = artifactVersion

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
	processResources {
		inputs.property("version", artifactVersion)

		filesMatching("paper-plugin.yml") {
			expand("version" to artifactVersion)
		}
	}
	jar {
		archiveBaseName.set("T-Nexus")
		archiveVersion.set(artifactVersion)
	}
	runServer {
		minecraftVersion("26.1.2")
	}
}