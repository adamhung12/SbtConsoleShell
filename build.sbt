name := "SbtConsoleProject"

version := "1.0.0"

//scalaVersion := "2.13.0"
resolvers += Resolver.mavenLocal

libraryDependencies += "javax.mail" % "mail" % "1.4"
libraryDependencies += "me.xethh.utils" % "DateUtils" % "6.0.0.RC1-RELEASE"
libraryDependencies += "com.jcraft" % "jsch" % "0.1.55"
libraryDependencies += "org.jsoup" % "jsoup" % "1.10.3"
libraryDependencies += "me.xethh.utils" % "JDBCProvider" % "1.1.1-RELEASE"
libraryDependencies += "me.xethh.libs.toolkits" % "commons" % "2.1.5"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.48"
libraryDependencies += "com.microsoft.sqlserver" % "mssql-jdbc" % "7.4.1.jre8"
libraryDependencies += "org.apache.commons" % "commons-compress" % "1.19"
libraryDependencies += "org.apache.commons" % "commons-vfs2" % "2.4.1"
libraryDependencies += "com.jcraft" % "jsch" % "0.1.55"
libraryDependencies += "me.xethh.libs.toolkits" % "crawler" % "1.4.5-RELEASE"
libraryDependencies += "javax.xml.stream" % "stax-api" % "1.0-2"
libraryDependencies += "org.apache.nutch" % "nutch" % "2.4"
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.12.1"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.12.1"
libraryDependencies += "com.google.zxing" % "core" % "3.4.0"
libraryDependencies += "com.google.zxing" % "javase" % "3.4.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"
libraryDependencies += "org.openpnp" % "opencv" % "3.4.2-1"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.0"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.10.0"
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.10.0"
libraryDependencies += "org.reflections" % "reflections" % "0.9.11"

lazy val cleanup = taskKey[Unit]("cleanup")
cleanup := {
  (runMain in Compile).toTask(" me.xethh.console.tasks.Cleanup ").value
}

//(compile in Compile) := ((compile in Compile) dependsOn cleanup).value

initialCommands in console :=
  """
    |import me.xethh.console.tools._
    |import me.xethh.console.scripts.Predef._
    |import scripts.PredefScript._
    |import scripts.PredefScript
    |import me.xethh.console.display.ViewAndPickTools._
    |import me.xethh.console.tools.StringConversionExt._
    |import me.xethh.console.tools.FileTraverseUtils._
    |val sy = Sys.sys
    |val pwd = Sys.pwd
    |val mem = Mem()
    |val ps = PredefScript()
    |val path = dynamic.StaticPaths
    |val app = dynamic.StaticApps
    |
    |""".stripMargin

