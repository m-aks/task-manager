name := """task-manager"""
organization := "com.m-aks"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play"           % "5.0.0" % Test

libraryDependencies += "org.firebirdsql.jdbc"   %  "jaybird"                      % "4.0.0.java11"
libraryDependencies += "org.scalikejdbc"        %% "scalikejdbc"                  % "3.5.0"
libraryDependencies += "org.scalikejdbc"        %% "scalikejdbc-config"           % "3.5.0"
libraryDependencies += "org.scalikejdbc"        %% "scalikejdbc-play-initializer" % "2.8.0-scalikejdbc-3.5"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.m-aks.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.m-aks.binders._"