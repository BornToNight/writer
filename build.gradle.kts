val springBootStarterVer: String by project // 3.3.1-SNAPSHOT
val springAdminVer: String by project // 3.2.3
val postgreSQLVer: String by project // 42.7.3
val jacksonVer: String by project // 2.17.0
val micrometerPrometheusVer: String by project // 1.12.4
val lombokVer: String by project // 1.18.32
val kafkaVer: String by project // 3.2.0
val junitVer: String by project // 1.11.0-M2
val logstashEncoderVer: String by project // 8.0

plugins {
	java
	id("org.springframework.boot") version "3.3.1-SNAPSHOT"
	id("io.spring.dependency-management") version "1.1.5"
}

group = "ru.pachan"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web:$springBootStarterVer")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springBootStarterVer")
	implementation("org.springframework.boot:spring-boot-starter-actuator:$springBootStarterVer")
	implementation("de.codecentric:spring-boot-admin-starter-client:$springAdminVer")
	implementation("io.micrometer:micrometer-registry-prometheus:$micrometerPrometheusVer")
	implementation("org.springframework.kafka:spring-kafka:$kafkaVer")
	implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVer")
	implementation("net.logstash.logback:logstash-logback-encoder:$logstashEncoderVer")
	runtimeOnly("org.postgresql:postgresql:$postgreSQLVer")
	testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootStarterVer")
	testImplementation("org.springframework.kafka:spring-kafka-test:$kafkaVer")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitVer")
	compileOnly("org.projectlombok:lombok:$lombokVer")
	annotationProcessor("org.projectlombok:lombok:$lombokVer")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
