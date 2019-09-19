package me.xethh.console.tools

import java.nio.file.{Files, Path, Paths, StandardCopyOption}
import java.util.concurrent.TimeUnit

import me.xethh.libs.toolkits.OSDetecting.{OS, OSDector}
import me.xethh.utils.dateManipulation.{DateFactory, DateFormatBuilderImpl}

class CopyUtils(val source:String, val dests:Array[String], val redirect:Option[String]=None) {
  private val startTime = System.nanoTime()
  if(redirect.nonEmpty) {
    println(s"Redirecting out to: ${redirect.get}")
    StdWriter.intercepting(redirect.get)
  }
  println(s"Start time: ${DateFormatBuilderImpl.ISO8601().format(DateFactory.now().asDate())}")

  println(s"Copying source file: ${source}")
  println(s"Copied source to ${dests.size} destinations")
  val sourcePath = Paths.get(source)
  dests.zipWithIndex.foreach(item=>{
    val (destinationString, index) = item
    println(s"Item[$index] ==> Copying to $destinationString")
    import me.xethh.console.tools.FileStatusUtils._
    val destPath:Path = Paths.get(destinationString)

    Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.COPY_ATTRIBUTES)
  })
  println(s"End time: ${DateFormatBuilderImpl.ISO8601().format(DateFactory.now().asDate())}")
  val duration = TimeDisplayUtils(System.nanoTime()-startTime)
  println(s"Copied successfully in duration $duration")
}

object CopyUtils{
  val SystemSeparator = OSDector.detect() match {
    case OS.Windows => "\\"
    case OS.Linux | OS.Mac | OS.Solaris => "/"
  }

  def copy(source:String, dests:Array[String])={
    new CopyUtils(source, dests, None)
  }
}
