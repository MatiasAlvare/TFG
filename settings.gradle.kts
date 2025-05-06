pluginManagement {
    repositories {
        // Aquí buscamos los plugins de Gradle
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.android.application" -> useVersion("8.2.2")
                "org.jetbrains.kotlin.android" -> useVersion("1.9.25")
                "com.google.gms.google-services" -> useVersion("4.4.2")
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // Aquí buscamos todas tus dependencias (Compose, Firebase, etc.)
        google()
        mavenCentral()
    }
}

rootProject.name = "TFG_MATIAS"
include(":app")
