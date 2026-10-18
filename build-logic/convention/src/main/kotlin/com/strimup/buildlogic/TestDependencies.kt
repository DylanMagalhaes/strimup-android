package com.strimup.buildlogic

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureTestDependencies() {
    dependencies {
        add("testImplementation", libs.library("junit"))
        add("testImplementation", libs.library("truth"))
        add("testImplementation", libs.library("kotlinx-coroutines-test"))
        add("testImplementation", libs.library("turbine"))
        add("testImplementation", libs.library("androidx-arch-core-testing"))

        add("androidTestImplementation", libs.library("androidx-junit"))
        add("androidTestImplementation", libs.library("androidx-espresso-core"))
        add("androidTestImplementation", libs.library("androidx-test-core"))
        add("androidTestImplementation", libs.library("truth"))
        add("androidTestImplementation", libs.library("kotlinx-coroutines-test"))
    }
}
