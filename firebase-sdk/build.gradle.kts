plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("maven-publish")
//    aplicar esses no modulo app do projeto que consome a lib
//    alias(libs.plugins.google.firebase.crashlytics)
//    alias(libs.plugins.google.firebase.firebase.perf)
//    alias(libs.plugins.google.firebase.appdistribution)
}

android {
    namespace = "br.com.wgc.firebase_sdk"
    compileSdk = 36

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    publishing {
        singleVariant("release")
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE.md,LICENSE-notice.md}"
        }
    }
    experimentalProperties["android.experimental.enableScreenshotTest"] = true

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.play.services.ads)
    implementation(libs.firebase.config)
    implementation(libs.firebase.inappmessaging.display)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.perf)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.ml.vision)
    implementation(libs.firebase.functions)
    implementation(libs.firebase.ai)
    implementation(libs.firebase.vertexai)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "br.com.wgc"
            artifactId = "firebase-sdk"
            version = project.findProperty("version_name") as? String ?: "0.0.1-SNAPSHOT"
            afterEvaluate {
                from(components["release"])
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Gabriel-do-Carmo-97/DataSourceAndroid")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
