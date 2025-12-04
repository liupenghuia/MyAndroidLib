import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.example.mylibrary"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    // ✅【修改点 1】使用官方 API 自动生成并包含源码 Jar
    // 这会自动处理所有依赖关系，彻底解决 Metadata 报错
    publishing {
        singleVariant("release") {
            withSourcesJar()
            // if (needsJavadoc) withJavadocJar() // 需要 Javadoc 也可以加上这个
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

// ------------------- 上传到 Maven 仓库的配置 --------------------------

// ❌【修改点 2】删除这部分手动创建 Task 的代码，容易产生依赖冲突
// val androidSourcesJar by tasks.registering(Jar::class) { ... } 删除掉！

val libGroupId = "com.penguin.pplib"
val libArtifactId = "ppkit"

val libVersion = if (project.hasProperty("targetVersion")) {
    project.property("targetVersion").toString()
} else {
    "1.0.1-SNAPSHOT"
}

afterEvaluate {
    var finalUser: String? = null
    var finalKey: String? = null

    if (project.hasProperty("githubUser")) {
        finalUser = project.property("githubUser") as String?
        finalKey = project.property("githubToken") as String?
    }

    if (finalUser == null) {
        val localFile = project.rootProject.file("local.properties")
        if (localFile.exists()) {
            val props = Properties()
            localFile.inputStream().use { stream -> props.load(stream) }
            finalUser = props.getProperty("gpr.user")
            finalKey = props.getProperty("gpr.key")
        }
    }

    if (finalUser == null) {
        finalUser = System.getenv("GITHUB_ACTOR")
        finalKey = System.getenv("GITHUB_TOKEN")
    }

    println("=============================================")
    println("🔍 [Gradle调试信息]")
    println("   目标版本: $libVersion")
    println("   读取用户: $finalUser")
    println("   仓库地址: https://maven.pkg.github.com/liupenghuia/MyAndroidLib")
    println("=============================================")

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("release") {
                groupId = libGroupId
                artifactId = libArtifactId
                version = libVersion

                // ✅【修改点 3】仅保留这一行
                // 因为上面配置了 withSourcesJar()，这里的 components 已经自动包含了 AAR 和 SourcesJar
                from(components.getByName("release"))

                // ❌【修改点 4】删除 artifact(androidSourcesJar)，不要手动添加，否则会重复报错
            }
        }

        repositories {
            maven {
                isAllowInsecureProtocol = true
                name = "MyAndroidLib"
                url = uri("https://maven.pkg.github.com/liupenghuia/MyAndroidLib")
                credentials {
                    username = finalUser
                    password = finalKey
                }
            }
        }
    }
}
