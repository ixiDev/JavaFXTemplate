import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("application")
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
    id("org.openjfx.javafxplugin") version "0.0.10"
    id("org.panteleyev.jpackageplugin") version "1.3.1"
}

group = "ixidev.javafxtemplate"
version = "1.0.0"

repositories {
    mavenCentral()

}
buildscript {
    repositories {
        jcenter()
        google()
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.1.0")
    }
}

//********** Proguard Config ********

ext["proguardOutput"] = "build/proguard/jar/${project.name}-${project.version}.jar"
ext["proguardEnabled"] = true

//********** Proguard Config ********

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}


application {
    mainModule.set("ixidev.javafxtemplate")
    mainClass.set("ixidev.javafxtemplate.AppLauncher")
}


javafx {
    version = "16"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
}

task("copyDependencies", Copy::class) {
    from(configurations.runtimeClasspath).into("$buildDir/jmods")
}

task("copyJar", Copy::class) {
    if (ext["proguardEnabled"] == true) {
        dependsOn("proguard")
        from(ext["proguardOutput"]).into("$buildDir/jmods")
    } else
        from(tasks.jar).into("$buildDir/jmods")
}

task("proguard", proguard.gradle.ProGuardTask::class) {
    dependsOn("build", "copyDependencies")
    injars(tasks.jar)
    outjars(ext["proguardOutput"])
    libraryjars("$buildDir/jmods")
    allowaccessmodification()
    printmapping("build/proguard/proguard-mapping.map")
    configuration("proguard.conf")
}


tasks.jpackage {

    if (ext["proguardEnabled"] == true)
        dependsOn("copyJar")
    else
        dependsOn("build", "copyDependencies", "copyJar")

    appName = project.name
    appVersion = project.version.toString()
    vendor = "ixiDev"
    copyright = "Copyright (c) 2021 ixiDev"
    runtimeImage = System.getProperty("java.home")
    module = "ixidev.javafxtemplate/ixidev.javafxtemplate.AppLauncher"
    modulePaths = listOf(File("$buildDir/jmods").path)
    destination = "$buildDir/dist"
    javaOptions = listOf("-Dfile.encoding=UTF-8")

//    mac {
//        icon = "icons/icons.icns"
//    }

    linux {
        linuxShortcut = true
        linuxDebMaintainer = "abdelmajid.idali@gmail.com"
        linuxAppCategory = "Application/Tools"
        linuxPackageName = project.name.toLowerCase().trim()
        icon = "art/linux_icon.png"
    }
    windows {
        icon = "art/win_icon.ico"
        winMenu = true
        winDirChooser = true
        winShortcut = true
    }
}