plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.zhile"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
//    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3") // 检查最新版本
//    implementation ("org.springframework.boot:spring-boot-starter-security")


    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    runtimeOnly("com.oracle.database.jdbc:ojdbc11")
    runtimeOnly("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Apache POI - 用于处理 .xls 和 .xlsx 文件
    implementation("org.apache.poi:poi:5.4.1") // 用于 .xls 格式
    implementation("org.apache.poi:poi-ooxml:5.4.1") // 用于 .xlsx 格式 (需要同时引入 poi)
    // 注意：POI 5.x 版本中不需要单独引入 poi-ooxml-schemas，它已包含在 poi-ooxml 中
    // 确保 xmlbeans 版本与 POI 5.4.1 兼容
    implementation("org.apache.xmlbeans:xmlbeans:5.1.1") // Apache POI 5.4.1 推荐的 XMLBeans 版本


    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // JSON处理
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")


    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}