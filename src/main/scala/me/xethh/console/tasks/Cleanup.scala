package me.xethh.console.tasks

import me.xethh.console.tools.{CopyUtils, Sys}

class Cleanup {

}
object Cleanup{
  def main(args: Array[String]): Unit = {
    import me.xethh.console.tools.FileCopyUtils._
    import me.xethh.console.tools.FileTraverseUtils._
    import me.xethh.console.tools.StringConversionExt._
    val sbtHome = Sys.envExist("x_sbt_console_sbt_project_home").p
    println(s"Sbt console home ${sbtHome}")
    val dynamicPath = sbtHome/"src"/"main"/"scala"/"dynamic"
    println(s"Dynamic path ${dynamicPath}")
    val configHome = Sys.envExist("x_sbt_console_config_home").p
    println(s"Sbt console config home ${configHome}")
    val consoleProfile = Sys.envExist("x_sbt_console_config_profile")
    println(s"Sbt console console profile ${consoleProfile}")
    val profilePath = configHome/"profiles"/consoleProfile/"dynamic"
    dynamicPath.deleteChild()
    dynamicPath.cpStructure(profilePath)
        .foreach{it=>
          if(it._1.exists() && it._1.isFile){
            println(s"Copy file from ${it._1} to ${it._2}")
            CopyUtils.copy(it._1.toPath.toString, Array(it._2.toPath.toString))
          }
        }

    println("Sbt console init clean up completed")
  }

}
