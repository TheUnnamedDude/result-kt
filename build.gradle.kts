plugins {
    kotlin("multiplatform") version "1.3.61"
}

repositories {
    jcenter()
}

group = "no.nav"
version = "1.0-SNAPSHOT"

kotlin {
    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        jvm().compilations["test"].defaultSourceSet {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
    }
}
