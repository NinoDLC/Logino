plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kover)
    alias(libs.plugins.ksp)
}

android {
    namespace = "fr.delcey.logino"
    compileSdk = 34

    defaultConfig {
        applicationId = "fr.delcey.logino"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("${project.rootDir}/keystore.jks")
            storePassword = "seloger"
            keyAlias = "debug"
            keyPassword = "seloger"
        }
        create("release") {
            storeFile = file("${project.rootDir}/keystore.jks")
            storePassword = "seloger"
            keyAlias = "release"
            keyPassword = "seloger"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

hilt {
    enableAggregatingTask = true
}

dependencies {
    implementation(project(":ui"))
    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    testImplementation(libs.junit)
}

dependencies {
    kover(project(":ui"))
    kover(project(":domain"))
    kover(project(":data"))
}

kover {
    reports {
        filters {
            excludes {
                androidGeneratedClasses()
                annotatedBy(
                    "dagger.internal.DaggerGenerated",
                    "dagger.Module"
                )
                inheritedFrom("com.squareup.moshi.JsonAdapter")
                packages(
                    "dagger.hilt.internal.aggregatedroot.codegen",
                    "hilt_aggregated_deps",

                    "fr.delcey.logino.ui.navigation",
                    "fr.delcey.logino.ui.utils",
                )
                classes(
                    "*_ProvideFactory\$InstanceHolder", // From Hilt, not annotated so this is the last way to do so

                    // Remove code below once Kover can handle UI tests!
                    "*Adapter",
                    "*Adapter\$*",
                )
            }
        }
    }
}