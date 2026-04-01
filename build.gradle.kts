plugins {
	kotlin("jvm") version "2.2.20"
	id("fabric-loom") version "1.11.7"
}

version = "1.0.0"
group = "com.claudijusapchy.ratprotection"

repositories {
	mavenCentral()
	maven("https://maven.fabricmc.net/")
}

dependencies {
	minecraft("com.mojang:minecraft:1.21.10")
	mappings(loom.officialMojangMappings())
	modImplementation("net.fabricmc:fabric-loader:0.16.10")
	modImplementation("net.fabricmc.fabric-api:fabric-api:0.138.3+1.21.10")
	modImplementation("net.fabricmc:fabric-language-kotlin:1.13.6+kotlin.2.2.20")
	implementation("com.google.code.gson:gson:2.11.0")
}

loom {
	mixin {
		defaultRefmapName.set("ratprotection.refmap.json")
	}
}

kotlin {
	jvmToolchain(21)
}

java {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}