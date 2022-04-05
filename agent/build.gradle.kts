plugins {
    id ("java")
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
    implementation("org.ow2.asm:asm:9.2")
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "Gradle",
            "Implementation-Version" to archiveVersion,
            "Premain-Class" to "com.ggggght.agent.Launcher",
            "Can-Redefine-Classes" to true,
            "Can-Retransform-Classes" to true,
        )
    }
}