/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("buildlogic.java-conventions")
    id("io.papermc.paperweight.userdev") version "1.7.1"
    id("io.github.goooler.shadow") version "8.1.7"
}

dependencies {
    paperweight.devBundle("com.starsrealm.nylon", "1.21-R0.0.4-STARSREALM-SNAPSHOT")
    compileOnly(libs.me.clip.placeholderapi)
}

description = "PlayerBalancerAddon"
