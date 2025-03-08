plugins {
	java
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "indus340.tech"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(23)
	}
}

repositories {
	mavenCentral()
	maven {
		url = uri("https://jitpack.io")
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("dev.langchain4j:langchain4j-spring-boot-starter:1.0.0-beta1")
	implementation("dev.langchain4j:langchain4j-core:1.0.0-beta1")
	implementation("dev.langchain4j:langchain4j-open-ai:1.0.0-beta1")
	implementation("dev.langchain4j:langchain4j-mistral-ai:1.0.0-beta1")
	implementation("org.graalvm.sdk:graal-sdk:24.1.2")
	implementation("org.graalvm.truffle:truffle-api:24.1.2")
	implementation("org.graalvm.js:js:24.1.2")
	implementation("org.graalvm.python:python:24.1.2")
	implementation("org.jsoup:jsoup:1.18.3")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}