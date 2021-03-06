plugins {
    // Java support
    id("java")
}

group = "helloworld.hotswap"
version = "0.0.1"

repositories {
    mavenLocal()
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation("org.javassist:javassist:3.22.0-GA")
    implementation("org.ow2.asm:asm-commons:9.2") {
        exclude ("org.ow2.asm","asm-tree")
        exclude ("org.ow2.asm","asm-analysis")
    }
    implementation("com.h2database:h2:2.1.210")
    implementation("org.springframework.data:spring-data-jpa:2.6.3")
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "Gradle",
            "Implementation-Version" to archiveVersion,
            "Premain-Class" to "com.ggggght.agent.AgentBootstrap",
            "Can-Redefine-Classes" to true,
            "Can-Retransform-Classes" to true,
        )
    }
}