import org.jreleaser.model.Active

group = "ru.code4a"
version = file("version").readText().trim()

plugins {
  kotlin("jvm") version "2.0.21"

  id("org.kordamp.gradle.jandex") version "1.0.0"

  `java-library`
  `maven-publish`
  id("org.jreleaser") version "1.13.1"
}

java {
  withJavadocJar()
  withSourcesJar()
}

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      artifactId = "quarkus-transactional-rw-lib"

      from(components["java"])

      pom {
        name = "Quarkus Transactional Read-Write Library"
        description =
          "This library provides enhanced transactional management for Quarkus applications, offering distinct handling for read-only and write transactions. It allows for fine-grained control over transaction propagation and execution."
        url = "https://github.com/4ait/quarkus-transactional-rw-lib"
        inceptionYear = "2024"
        licenses {
          license {
            name = "The Apache License, Version 2.0"
            url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
          }
        }
        developers {
          developer {
            id = "tikara"
            name = "Evgeniy Simonenko"
            email = "tiikara93@gmail.com"
            organization.set("4A LLC")
            roles.set(
              listOf(
                "Software Developer",
                "Head of Development"
              )
            )
          }
        }
        organization {
          name = "4A LLC"
          url = "https://4ait.ru"
        }
        scm {
          connection = "scm:git:git://github.com:4ait/quarkus-transactional-rw-lib.git"
          developerConnection = "scm:git:ssh://github.com:4ait/quarkus-transactional-rw-lib.git"
          url = "https://github.com/4ait/quarkus-transactional-rw-lib"
        }
      }
    }
  }
  repositories {
    maven {
      url =
        layout.buildDirectory
          .dir("staging-deploy")
          .get()
          .asFile
          .toURI()
    }
  }
}

repositories {
  mavenCentral()
}

tasks.withType<Test> {
  useJUnitPlatform()
  dependsOn(tasks["jandex"])
}

dependencies {
  implementation("io.quarkus:quarkus-arc:3.12.3")
  implementation("io.quarkus:quarkus-narayana-jta:3.12.3")
}

tasks.named("compileTestKotlin", org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java) {
  compilerOptions {
    freeCompilerArgs.add("-Xdebug")
  }
}

jreleaser {
  project {
    copyright.set("4A LLC")
  }
  gitRootSearch.set(true)
  signing {
    active.set(Active.ALWAYS)
    armored.set(true)
  }
  release {
    github {
      overwrite.set(true)
      branch.set("master")
    }
  }
  deploy {
    maven {
      mavenCentral {
        create("maven-central") {
          active.set(Active.ALWAYS)
          url.set("https://central.sonatype.com/api/v1/publisher")
          stagingRepositories.add("build/staging-deploy")
          retryDelay.set(30)
        }
      }
    }
  }
}
