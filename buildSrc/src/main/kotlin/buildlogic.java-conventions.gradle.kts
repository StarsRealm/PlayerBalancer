import java.util.*

/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal()
    maven {
        name = "AliYun-Snapshot"
        url = uri("https://packages.aliyun.com/maven/repository/2421751-snapshot-i7Aufp/")
        credentials {
            username = project.findProperty("aliyun.package.user") as String? ?: System.getenv("ALY_USER")
            password = project.findProperty("aliyun.package.password") as String? ?: System.getenv("ALY_PASSWORD")
        }
    }
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }

    maven {
        url = uri("https://repo.codemc.org/repository/maven-public")
    }

    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }

    maven {
        url = uri("https://simonsator.de/repo")
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
}

group = "com.jaimemartz"
version = "2.3.6"
java.sourceCompatibility = JavaVersion.VERSION_21

publishing {
    repositories {
        maven {
            name = "AliYun-Snapshot"
            url = uri("https://packages.aliyun.com/maven/repository/2421751-snapshot-i7Aufp/")
            credentials {
                username = project.findProperty("aliyun.package.user") as String? ?: System.getenv("ALY_USER")
                password = project.findProperty("aliyun.package.password") as String? ?: System.getenv("ALY_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("gpr") {
            from(project.components["java"])
            groupId = project.group.toString()
            artifactId = project.name.lowercase(Locale.ENGLISH)
            version = project.version.toString()
        }
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}
