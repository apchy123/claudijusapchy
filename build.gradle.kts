plugins {
	kotlin("jvm") version "2.0.0"
	id("fabric-loom") version "1.7.4"
}

version = "1.0.0"
group = "com.claudijusapchy.ratprotection"

repositories {
	mavenCentral()
}

dependencies {
	minecraft("com.mojang:minecraft:1.21.1")
	mappings(loom.officialMojangMappings())
	modImplementation("net.fabricmc:fabric-loader:0.16.5")
	modImplementation("net.fabricmc.fabric-api:fabric-api:0.102.0+1.21.1")
	modImplementation("net.fabricmc:fabric-language-kotlin:1.11.0+kotlin.2.0.0")
}

java {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}