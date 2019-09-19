package scripts

import java.io.File
import java.nio.file.{Path, Paths}

import me.xethh.console.display.MapViewerable
import me.xethh.console.tools.{ClipUtils, StringConversionExt, Sys}


class OSPath(val path:String)

case class Clipable(str:Any){
  def toClip=ClipUtils.write(str.toString)
}
case class Openable(path:Path){
  import me.xethh.console.tools.FileStatusUtils._

  import sys.process._
  def openDir={
    path.checkExist.checkCanRead.checkIsDirector
    s"start ${path.toString}"
  }
}

case class PredefScript() {
  import PredefScript._

  import scala.collection.JavaConverters._
  System.getenv().asScala.filter(_._1.toLowerCase().contains("sbt")).foreach(it=>println(s"${it._1}\t${it._2}"))
  val sbtConsoleConfigPath:Path = Sys.envExist("x_sbt_console_config_home")


//  val software:ValueViewerClip = ValueViewerClip(ValueViewer(List(IndexedValue(0, "E:\\nextcloud\\Softwares"))))
//  val usb:ValueViewerClip = ValueViewerClip(ValueViewer(List(IndexedValue(0, "f:"))))


}

case class StringExt(val str:String){
  def toPath:Path = Paths.get(str)
}
case class OptionalExt[X](val x:X){
  def toSome:Option[X]=Some(x)
}

object PredefScript{
  implicit def str2Path(str:String):Path = Paths.get(str)
  implicit def file2Path(f:File):Path = f.toPath
  implicit def str2StringExt(str:String):StringExt = StringExt(str)
  implicit def anyToOptionalExt[X](x:X):OptionalExt[X] = OptionalExt(x)
  implicit def str2Clipable(str:String):Clipable = Clipable(str)
  implicit def path2Clipable(str:Path):Clipable = Clipable(str)
  implicit def path2Clipable(str:File):Clipable = Clipable(str)
  implicit def path2Openable(path:Path):Openable = Openable(path)

}
