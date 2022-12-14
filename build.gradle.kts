plugins {
	java
	id("com.github.johnrengelman.shadow") version "7.1.2" apply false
	id("io.papermc.paperweight.patcher") version "1.3.9"
}

val paperMavenPublicUrl = "https://papermc.io/repo/repository/maven-public/"

repositories {
	mavenCentral()
	maven(paperMavenPublicUrl) {
		content { onlyForConfigurations(configurations.paperclip.name) }
	}
}

dependencies {
	remapper("net.fabricmc:tiny-remapper:0.8.6:fat")
	decompiler("net.minecraftforge:forgeflower:1.5.605.9")
	paperclip("io.papermc:paperclip:3.0.2")
}


allprojects {
	apply(plugin = "java")
	apply(plugin = "maven-publish")

	java {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of(17))
		}
	}
}

subprojects {
	tasks.withType<JavaCompile> {
		options.encoding = Charsets.UTF_8.name()
		options.release.set(17)
	}
	tasks.withType<Javadoc> {
		options.encoding = Charsets.UTF_8.name()
	}
	tasks.withType<ProcessResources> {
		filteringCharset = Charsets.UTF_8.name()
	}

	repositories {
		mavenCentral()
		maven(paperMavenPublicUrl)
	}
}

paperweight {
	serverProject.set(project(":catpaper-server"))

	remapRepo.set(paperMavenPublicUrl)
	decompileRepo.set(paperMavenPublicUrl)

	usePaperUpstream(providers.gradleProperty("paperRef")) {
		withPaperPatcher {
			apiPatchDir.set(layout.projectDirectory.dir("patches/api"))
			apiOutputDir.set(layout.projectDirectory.dir("catpaper-api"))

			serverPatchDir.set(layout.projectDirectory.dir("patches/server"))
			serverOutputDir.set(layout.projectDirectory.dir("catpaper-server"))
		}
	}
}
