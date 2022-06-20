import org.jetbrains.changelog.date
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
  // Java support
  id("java")
  // Kotlin support
  kotlin("jvm") version "1.7.0"
  // Gradle IntelliJ Plugin
  id("org.jetbrains.intellij") version "1.5.2"
  // Gradle Changelog Plugin
  id("org.jetbrains.changelog") version "1.3.1"
  // Gradle Qodana Plugin
  id("org.jetbrains.qodana") version "0.1.13"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

// Configure project's dependencies
repositories {
  mavenLocal()
  maven(url = "https://maven.aliyun.com/nexus/content/groups/public/")
  mavenCentral()
  maven(url = "https://maven-central.storage-download.googleapis.com/repos/central/data/")
  maven(url = "https://repo.eclipse.org/content/groups/releases/")
  maven(url = "https://www.jetbrains.com/intellij-repository/releases")
  maven(url = "https://www.jetbrains.com/intellij-repository/snapshots")
}


dependencies {
  implementation(project(":agent"))
  implementation("com.h2database:h2:2.1.210")
  implementation("org.apache.httpcomponents:httpclient:4.5.5")
  implementation("org.springframework.boot:spring-boot-starter-validation:2.6.4")
  implementation("org.springframework.boot:spring-boot-starter-web:2.6.4")
  implementation("org.springframework.boot:spring-boot-starter-webflux:2.6.4")
  implementation("org.javassist:javassist:3.22.0-GA")
}

// Configure Gradle IntelliJ Plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
  pluginName.set(properties("pluginName"))
  version.set(properties("platformVersion"))
  type.set(properties("platformType"))

  // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
  plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))

  // Plugin sandbox Dir
  // sandboxDir.set(properties("sandboxDir"))

  instrumentCode.set(false)
  downloadSources.set(false)
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
  version.set(properties("pluginVersion"))
  groups.set(emptyList())
  path.set(properties("changelogPath"))
  header.set(provider { "[${project.version}] - ${date("yyyy-MM-dd")}" })
  itemPrefix.set("-")
  unreleasedTerm.set("Unreleased")
}

// Configure Gradle Qodana Plugin - read more: https://github.com/JetBrains/gradle-qodana-plugin
qodana {
  cachePath.set(projectDir.resolve(".qodana").canonicalPath)
  reportPath.set(projectDir.resolve("build/reports/inspections").canonicalPath)
  saveReport.set(true)
  showReport.set(System.getenv("QODANA_SHOW_REPORT")?.toBoolean() ?: false)
  showReportPort.set(8888)
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
  withSourcesJar()
  withJavadocJar()
}

tasks {
  runIde {
    systemProperties["idea.auto.reload.plugins"] = false
    jvmArgs = listOf(
      "-Xms2048m",
      "-Xmx2048m",
    )
  }

  patchPluginXml{
    sinceBuild.set("213")
    untilBuild.set("223.*")
  }
}

tasks.withType<JavaCompile> {
  options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_11.toString()
  }
}

// tasks.withType<Jar> {
//   manifest {
//     attributes(
//       mapOf(
//         "Implementation-Title" to project.name,
//         "Implementation-Version" to project.version
//       )
//     )
//   }
// }
