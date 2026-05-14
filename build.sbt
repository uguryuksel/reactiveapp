name := """reactiveApp"""
organization := "com.reactive"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.18"

resolvers += "Maven Central" at "https://repo1.maven.org/maven2/"

libraryDependencies += guice

libraryDependencies += "io.reactivex.rxjava3" % "rxjava" % "3.1.8"