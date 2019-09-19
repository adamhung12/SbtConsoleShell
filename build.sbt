name := "SbtConsoleProject"

version := "1.0.0"

//scalaVersion := "2.13.0"

libraryDependencies += "javax.mail" % "mail" % "1.4"
libraryDependencies += "me.xethh.utils" % "DateUtils" % "6.0.0.RC1-RELEASE"
libraryDependencies += "com.jcraft" % "jsch" % "0.1.55"
libraryDependencies += "me.xethh.libs.toolkits" % "commons" % "2.0.0"

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

