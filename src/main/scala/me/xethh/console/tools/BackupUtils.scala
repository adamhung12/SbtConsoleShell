package me.xethh.console.tools

import java.io.{File, FilenameFilter}
import java.nio.file.{Path, Paths}
import java.util.regex.Pattern

import me.xethh.console.tools.BackupStrategy.BackupStrategy
import me.xethh.libs.toolkits.OSDetecting.{OS, OSDector}
import me.xethh.utils.dateManipulation.{DateFactory, DateFormatBuilder, DateFormatBuilderImpl}

object BackupStrategy extends Enumeration{
  type BackupStrategy = Value
  val REPLACING, MULTI_COPIES, CUSTOMIZED = Value
}
class BackupStrategyInstance(strategyType:BackupStrategy.Value)
case class ReplacingStrategyInstance(val path:String) extends BackupStrategyInstance(BackupStrategy.REPLACING)
case class MultiCopiesStrategyInstance(val path:String, copiesSize:Int) extends BackupStrategyInstance(BackupStrategy.MULTI_COPIES)

class FileOperation()
case class DeleteOperation(file:File) extends FileOperation()
case class CopyOperation(source:Path, dest:Path) extends FileOperation()

class BackupUtils(val path:String, val fileName:Option[String], strategies:Array[BackupStrategyInstance], val redirect:Option[String]) {
  val startTime = System.nanoTime()
  println(s"Start backup utils at ${DateFormatBuilderImpl.ISO8601().format(DateFactory.now().asDate())}")

  val SEP = CopyUtils.SystemSeparator

  val backupPath = {
    val pathFile  = Paths.get(path).toFile
    if(!pathFile.exists())
      throw new RuntimeException(s"Path[${path}] not exists")
    if(!pathFile.isDirectory)
      throw new RuntimeException(s"Path[$pathFile] is not director")
    val fileList = pathFile.listFiles(new FilenameFilter {
      val pattern = Pattern.compile(fileName.getOrElse(".*"))
      override def accept(dir: File, name: String): Boolean = pattern.matcher(name).matches()
    })
    if (fileList == null) Array.empty else fileList.map(_.toPath)
  }

  println("============= File List =============")
  println(s"Total ${backupPath.size} files to be backup")
  backupPath.zipWithIndex.foreach(f=>println(s"File[${f._2+1}]: ${f._1.toString}"))
  val operations = backupPath.map(source=>{
    (
      source,
      strategies.map(strategy=>{
        strategy match {
          case MultiCopiesStrategyInstance(path, copiesSize) =>
            val fileName = source.getFileName+"_"+DateFormatBuilderImpl.NUMBER_DATETIME().format(DateFactory.now().asDate())
            val pathFile = Paths.get(path).toFile
            if(!pathFile.exists())
              throw new RuntimeException(s"Path[${path}] not exists")
            if(!pathFile.isDirectory)
              throw new RuntimeException(s"Path[$pathFile] is not director")
            val pattern = Pattern.compile(source.getFileName+"_(\\d{14})$")
            val fileList= {
              val fileList = pathFile.listFiles(new FilenameFilter {
                override def accept(dir: File, name: String): Boolean = pattern.matcher(name).matches()
              })
              if(fileList==null) Array.empty else fileList.map(f => {
                val matcher = pattern.matcher(f.getName)
                matcher.matches()
                (DateFormatBuilderImpl.NUMBER_DATETIME().parse(matcher.group(1)),f)
              }).sortBy(_._1).reverse
            }

            val deleteOperation = if(fileList.size>=copiesSize)
              fileList.drop(copiesSize-1).map(f=>{
                DeleteOperation(f._2).asInstanceOf[FileOperation]
              })
            else Array.empty[FileOperation]

            val file = Paths.get(pathFile.toString+SEP+fileName)
            (MultiCopiesStrategyInstance(path, copiesSize), deleteOperation ++ Array(CopyOperation(source, file).asInstanceOf[FileOperation]))
          case ReplacingStrategyInstance(path) =>
            val fileName = source.getFileName
            val pathFile = Paths.get(path).toFile
            if(!pathFile.exists())
              throw new RuntimeException(s"Path[${path}] not exists")
            if(!pathFile.isDirectory)
              throw new RuntimeException(s"Path[$pathFile] is not director")
            val file = Paths.get(pathFile.toString+SEP+fileName)
            (ReplacingStrategyInstance(path), Array(CopyOperation(source, file).asInstanceOf[FileOperation]))
          case x:BackupStrategyInstance => throw new RuntimeException(s"Operation[${x.getClass}] not support.")
        }
      })
    )
  })

  println("============= Complete gather information =============")
  println(s"Total ${operations.map(it=>it._2.map(it=>it._2.length).sum).sum} operation to be run")

  var int = 0

  operations.foreach(it=>{
    val (path, arrays) = it
    arrays.foreach(it=>{
      val (bk, operations) = it
      operations.foreach {
        case DeleteOperation(file) =>
          int+=1
          println(s"** Delete[${int}] $file")
          file.delete()
        case CopyOperation(source, dest) =>
          int+=1
          println(s"** Copy[${int}] from $source to $dest")
          CopyUtils.copy(source.toString, Array(dest.toString))
      }
    })
  })

  println(s"End backup utils at ${DateFormatBuilderImpl.ISO8601().format(DateFactory.now().asDate())}")
  val duration = TimeDisplayUtils(System.nanoTime()-startTime)
  println(s"Backup successfully in duration $duration")
}
