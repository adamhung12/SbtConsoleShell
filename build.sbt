name := "SbtConsoleProject"

version := "1.0.0"

//scalaVersion := "2.13.0"

libraryDependencies += "javax.mail" % "mail" % "1.4"
libraryDependencies += "me.xethh.utils" % "DateUtils" % "6.0.0.RC1-RELEASE"
libraryDependencies += "com.jcraft" % "jsch" % "0.1.55"
libraryDependencies += "me.xethh.libs.toolkits" % "commons" % "2.0.0"
libraryDependencies += "org.jsoup" % "jsoup" % "1.12.1"
libraryDependencies += "me.xethh.utils" % "JDBCProvider" % "1.1.1-RELEASE"
libraryDependencies += "me.xethh.libs.toolkits" % "commons" % "2.1.3"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.48"
libraryDependencies += "com.microsoft.sqlserver" % "mssql-jdbc" % "7.4.1.jre8"
libraryDependencies += "org.apache.commons" % "commons-compress" % "1.19"
libraryDependencies += "org.apache.commons" % "commons-vfs2" % "2.4.1"
libraryDependencies += "com.jcraft" % "jsch" % "0.1.55"


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

