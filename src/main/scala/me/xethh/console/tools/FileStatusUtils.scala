package me.xethh.console.tools

import java.io.{File, FileInputStream, FileOutputStream}
import java.nio.file.{Files, Path, Paths}

case class MutableFile(override val f:File) extends FileTraverseUtils(null) {
  implicit def toSelfAgain(f:File):MutableFile = new MutableFile(f)

  var ff:FileTraverseUtils = new FileTraverseUtils(f)
  override def treeAll(): Array[File] = ff.treeAll()

  override def tree(level: Int): Array[File] = ff.tree(level)

  override def treeView: Unit = ff.treeView

  override def /(str: String): File = {
    ff = ff/str
    ff.f
  }

  override def cd(str: String): File = {
    ff = ff.cd(str)
    ff.f
  }

  override def cd(index: Int): File = {
    ff = ff.cd(index)
    ff.f
  }

  override def select(str: String): File = {
    ff = ff.select(str)
    ff.f
  }

  override def select(index: Int): File = {
    ff = ff.select(index)
    ff.f
  }

  override def virtual(str: String): File = {
    ff = ff.virtual(str)
    ff.f
  }

  override def parent: File = {
    ff = ff.parent
    ff.f
  }

  override def deleteChild(): File = {
    ff = ff.deleteChild()
    ff.f
  }

  override def delete(str: String): File = {
    ff = ff.delete(str)
    ff.f
  }

  override def delete(index: Int): File = {
    ff = ff.delete(index)
    ff.f
  }
}
class FileCopyUtils(f:File){
  import FileTraverseUtils._
  import FileStatusUtils._
  import StringConversionExt._
  def cpStructure(dest:File):Array[(File,File)]={
    f.checkExist.checkIsDirector
    dest.checkExist.checkIsDirector
    dest.tree().flatMap {
      it => {
        if (it.isDirectory) {
          val subDirectory = f.virtual(it.getName)
          if (subDirectory.exists()) {
            println(s"File[$subDirectory] already exist")
          }
          else {
            println(s"File[$subDirectory] not exist, adding now")
            subDirectory.mkdir()
          }
          (it, subDirectory) +: new FileCopyUtils(subDirectory).cpStructure(dest.select(it.getName))
        }
        else {
          Array((it, f.virtual(it.getName)))
        }
      }
    }
  }
}
object FileCopyUtils{
  implicit def file2FileCopyUtils(f:File) = new FileCopyUtils(f)
  implicit def path2FileCopyUtils(p:Path) = new FileCopyUtils(p.toFile)
}
class FileTraverseUtils(val f:File){

  implicit def file2FileUtils(f:File): FileStatusUtils = new FileStatusUtils(f)
  implicit def path2FileUtils(p:Path): FileStatusUtils = new FileStatusUtils(p.toFile)
  implicit def toSelf(f:File):FileTraverseUtils = new FileTraverseUtils(f)

  def treeAll():Array[File]={
    (f.tree().filter(_.isDirectory).map(it=>it.treeAll()).flatMap(it=>it)    )++tree()
  }
  def tree(level:Int=0):Array[File] = {
    f.checkExist.isDirectory
    level match {
      case 0 => {
        f.checkExist.isDirectory
        val subFile= {
          val subFile = f.listFiles()
          if(subFile==null) Array.empty[File] else subFile
        }
        subFile
      }
      case x if x>0 => ((tree(0).filter(_.isDirectory).map(it=>it.tree(level-1))).flatMap(it=>it))++tree(0)
      case x if x<0 => throw new RuntimeException(s"Tree[${f.toString}] traverse fail")
    }
  }

  def treeView:Unit = {
    println(s"Path: ${f.toString}")
    tree().zipWithIndex.map{it=>println(s"${"% 6d".format(it._2+1)}\t[${if(it._1.isFile) "F" else "D" }]${it._1.getName}")}
  }

  def /(str:String):File={
    str match {
      case ".." => parent
      case "." => f
      case x => virtual(x)
    }
  }

  def cd(str:String):File = {
    select(str).checkIsDirector
  }
  def cd(index:Int):File = {
    select(index).checkIsDirector
  }
  def select(str:String):File = {
    f.checkExist.checkIsDirector
    val subPath:Path=(Paths.get(f.toPath.toString+Sys.sys.osInfo.SEP+str))
    subPath.checkExist
  }
  def select(index:Int):File = {
    f.checkExist.checkIsDirector
    val found = tree().zipWithIndex.find(_._2==index-1)
    if(found.isEmpty) throw new RuntimeException(s"Sub directory item[${index}] of ${f.toString} doesn't exists")
    val subPath:Path=(Paths.get(f.toPath.toString+Sys.sys.osInfo.SEP+found.get._1.getName))
    subPath.checkExist
  }
  def virtual(str:String):File = {
    val subPath:Path=(Paths.get(f.toPath.toString+Sys.sys.osInfo.SEP+str))
    subPath.toFile
  }
  def parent:File = f.checkExist.getParentFile
  def deleteChild():File = {
    tree().foreach{it=>{
      f.delete(it.getName)
    }}
    f
  }
  def delete(str:String):File = {
    println(s"XXX ${f.toPath}\\${str}")
    val selectedFile = f.select(str)
    selectedFile.checkExist.isFile match{
      case true=>
        println(s"Deleting ${selectedFile.toString}")
        selectedFile.delete()
      case false=>
        selectedFile.tree().foreach{
          it=> if (it.isFile) {
            println(s"Deleting ${it.toString}")
            it.delete()
          } else {
            for(nf <- it.tree())
              it.delete(nf.getName)
            println(s"Deleting ${it.toString}")
            if(it.exists())
              it.delete()
          }
        }
        selectedFile.delete()
    }
    f
  }
  def delete(index:Int):File = {
    f.delete(f.select(index).checkExist.getName)
  }
}
object FileTraverseUtils{
  implicit def file2Self(f:File):FileTraverseUtils = new FileTraverseUtils(f)
  implicit def path2Self(f:Path):FileTraverseUtils = new FileTraverseUtils(f.toFile)
}

class FileStatusUtils(f:File) {
  def checkExist:File  = if(f.exists()) f else throw new RuntimeException(s"File[${f.toString}] not exists")
  def checkNotExist:File  = if(!f.exists()) f else throw new RuntimeException(s"File[${f.toString}] exists")
  def checkIsDirector:File = if(f.isDirectory) f else throw new RuntimeException(s"File[${f.toString}] is not directory")
  def checkIsFile:File = if(f.isFile) f else throw new RuntimeException(s"File[${f.toString}] is not file")
  def checkIsHidden:File = if(f.isHidden) f else throw new RuntimeException(s"File[${f.toString}] is not file")
  def checkCanRead:File = if(f.canRead) f else throw new RuntimeException(s"File[${f.toString}] is not readable")
  def checkCanWrite:File = if(f.canWrite) f else throw new RuntimeException(s"File[${f.toString}] is not readable")
  def checkCanExecutable:File = if(f.canExecute) f else throw new RuntimeException(s"File[${f.toString}] is not readable")
}
object FileStatusUtils{
  implicit def toSelf(f:File): FileStatusUtils = new FileStatusUtils(f)
  implicit def path2FileUtils(p:Path): FileStatusUtils = new FileStatusUtils(p.toFile)
}

class StringConversionExt(string: String){
  implicit def str2StringConversionExt(s:String): StringConversionExt = new StringConversionExt(s)
  def p:Path = Paths.get(string)
  def f:File = new File(string)
  def fis:FileInputStream = new FileInputStream(string.f)
  def fos:FileOutputStream = new FileOutputStream(string.f)
  def trimHead(str:String) = if(string.length<str.length) throw new RuntimeException(s"Target string[${string}] is shorter then trimming string") else string.substring(str.length)
  def trimTail(str:String) = if(string.length<str.length) throw new RuntimeException(s"Target string[${string}] is shorter then trimming string") else string.substring(str.length)
}
object StringConversionExt{
  implicit def str2StringConversionExt(s:String): StringConversionExt = new StringConversionExt(s)
}

