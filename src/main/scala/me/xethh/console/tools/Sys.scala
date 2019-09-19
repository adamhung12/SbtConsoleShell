package me.xethh.console.tools
import java.nio.file.{Path, Paths}
import java.util.regex.Pattern

import me.xethh.console.tools.FileStatusUtils._
import me.xethh.libs.toolkits.OSDetecting.{OS, OSDector}

trait Indexed{
  val index:Int
}
case class IndexedValue(val index:Int, val value:String) extends Indexed{
  override def toString: String = s"${"%06d".format(index)}\tvalue"
}

trait Displayable{
  def _display():Unit
}
trait IndexFilter[B <: Indexed, X <: IndexFilter[B,X]]{
  def toOriginalList():List[B]
  def toIndexedFilter():X = convertToIndexedFilter(toOriginalList())
  def convertToIndexedFilter(list:List[B]):X
  def index(index:Int):X = convertToIndexedFilter(toOriginalList().filter(_.index==index))
  def index(range:Range):X = convertToIndexedFilter(toOriginalList().filter(it => range.contains(it.index)))
  def +(otherViewer:X):X = convertToIndexedFilter(toOriginalList()++otherViewer.toOriginalList())
  def -(otherViewer:B):X = convertToIndexedFilter(toOriginalList().filter(_.index!=otherViewer.index))
}
case class ValueViewer(list:List[IndexedValue]) extends Displayable with IndexFilter[IndexedValue, ValueViewer] {
  implicit private def list2ValueViewer(list:List[IndexedValue]):ValueViewer = ValueViewer(list.sortBy(_.index))
  override def _display(): Unit = list.foreach(println)

  override def convertToIndexedFilter(list: List[IndexedValue]): ValueViewer = list2ValueViewer(list)

  override def toString: String = list.mkString("\n")

  override def toOriginalList(): List[IndexedValue] = list
}
case class IndexedKeyPair(val index:Int, val key:String, val value:String) extends Indexed{
  override def toString=s"${"%06d".format(index)}\t${key}\t${value}"
}
case class PropertiesViewer(val list:List[IndexedKeyPair]) extends Displayable with IndexFilter[IndexedKeyPair, PropertiesViewer] {
  implicit private def list2PropertiesViewer(list:List[IndexedKeyPair]): PropertiesViewer = PropertiesViewer(list.sortBy(_.index))

  override def _display():Unit = toOriginalList().foreach(println)

  def grepKey(str:String):PropertiesViewer = toOriginalList().filter(_.key.toLowerCase.contains(str.toLowerCase))
  def egrepKey(str:String):PropertiesViewer = {
    val pattern = Pattern.compile(str)
    toOriginalList().filter{ it=>pattern.matcher(it.key).matches()}
  }
  def grepValue(str:String):PropertiesViewer = toOriginalList().filter(_.value.toLowerCase.contains(str.toLowerCase))
  def egrepValue(str:String):PropertiesViewer = {
    val pattern = Pattern.compile(str)
    toOriginalList().filter{ it=>pattern.matcher(it.value).matches()}
  }

  override def convertToIndexedFilter(list: List[IndexedKeyPair]): PropertiesViewer = list
  override def toString: String = list.mkString("\n")

  override def toOriginalList(): List[IndexedKeyPair] = list
}
trait Sys{
  val userHome:Path
  val appHome:Path
  val mainStorage:Path

  val os = OSDector.detect()
  val osInfo:OSInfo = OSInfo()

}

case class OSInfo(){
  val SEP = if(OS.Windows==OSDector.detect()) "\\" else "/"
  import sys.process._
  def systemPropertyAdvance = "systempropertiesadvanced" !
}
case class Mem(){
  var mkp1:Option[IndexedKeyPair]=None
  var mkp2:Option[IndexedKeyPair]=None
  var mkp3:Option[IndexedKeyPair]=None
  var mkp4:Option[IndexedKeyPair]=None
  var mkp5:Option[IndexedKeyPair]=None
  var mkp6:Option[IndexedKeyPair]=None
  var mkp7:Option[IndexedKeyPair]=None
  var mkp8:Option[IndexedKeyPair]=None
  var mkp9:Option[IndexedKeyPair]=None
  var mkp10:Option[IndexedKeyPair]=None
  var mv1:Option[IndexedValue]=None
  var mv2:Option[IndexedValue]=None
  var mv3:Option[IndexedValue]=None
  var mv4:Option[IndexedValue]=None
  var mv5:Option[IndexedValue]=None
  var mv6:Option[IndexedValue]=None
  var mv7:Option[IndexedValue]=None
  var mv8:Option[IndexedValue]=None
  var mv9:Option[IndexedValue]=None
  var mv10:Option[IndexedValue]=None
  var mp1:Path=null
  var mp2:Path=null
  var mp3:Path=null
  var mp4:Path=null
  var mp5:Path=null
  var mp6:Path=null
  var mp7:Path=null
  var mp8:Path=null
  var mp9:Path=null
  var mp10:Path=null
}

case class SysImpl() extends Sys{
  override val mainStorage: Path = {
    val path = if(System.getProperty("x_sbt_console_base_entry")!=null) Paths.get(System.getProperty("x_sbt_console_base_entry")) else Paths.get(System.getProperty("user.home"))
    path.toFile.checkExist.checkIsDirector.toPath
  }
  override val appHome: Path = {
    val path = Paths.get(System.getProperty("user.dir"))
    path.toFile.checkExist.checkIsDirector.toPath
  }
  override val userHome: Path = {
    val path = Paths.get(System.getProperty("user.home"))
    path.toFile.checkExist.checkIsDirector.toPath
  }
}

object Sys{
  var sys:Sys = SysImpl()
  def envExist(prop:String)=if(System.getenv(prop)!=null) System.getenv(prop) else throw new RuntimeException(s"Environment variable[${prop}] not exists")
  def propertyExist(prop:String)=if(System.getProperty(prop)!=null) System.getProperty(prop) else throw new RuntimeException(s"Environment variable[${prop}] not exists")
  var pwd:MutableFile = MutableFile(sys.appHome.toFile)
}
