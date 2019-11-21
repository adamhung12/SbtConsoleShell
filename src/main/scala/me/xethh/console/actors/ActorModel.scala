package me.xethh.console.actors

import java.io.{File, FileInputStream, FileOutputStream, InputStream}
import java.util.Date
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorSelection}
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import me.xethh.console.tools.control.HoldObject.{keep, keep2}
import me.xethh.console.tools.control.InputStreamReader
import me.xethh.console.tools.fileMan.FileMan
import me.xethh.utils.dateManipulation.{DateFactory, DateFormatBuilderImpl}
import org.apache.commons.io.IOUtils
import protocol.{DecryptingProtocolProvider, EncryptingProtocolProvider}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source

object ActorModel{
  case class InstructionFileDiscover(filename:String)
  case class InstructionCompleted(id:Long, log:List[String])
  case class RawInstructionMaterial(id:Long, isProvider:()=>InputStream)

  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
  @JsonSubTypes(value = Array(
    new Type(value = classOf[MoveInstruction], name="[Moving Instruction]"),
    new Type(value = classOf[BackupInstruction], name="[Backup Instruction]"),
    new Type(value = classOf[CopyInstruction], name="[Copy Instruction]")
  ))
  abstract class Instruction() extends Serializable
  case class CopyInstruction(filename:String, source:String, dest:String, rename:Option[String]) extends Instruction()
  case class MoveInstruction(filename:String, source:String, dest:String, rename:Option[String]) extends Instruction()
  case class BackupInstruction(mode:Int, source:String, dest:String) extends Instruction()

  case class NoSupportedEncryption(description:String)
  case class InstructionFileNotFound(description:String)
  class InstructionActor extends Actor{
    val routerActor = context.actorSelection("/user/routerActor")
    override def receive: Receive = {
      case x:(Long, Instruction)=>
        val id = x._1
        x._2 match {
          case CopyInstruction(filename, source, dest, renaming) =>
            val bf:ListBuffer[String] = ListBuffer.empty
            val dateStr = DateFormatBuilderImpl.ISO8601().format(DateFactory.now.asDate)
            bf+=s"[$dateStr] Start copying from Source[$source] to Destination[$dest]"

            keep2 (new File(s"$source/$filename"), new File(dest)) exec {(source, dest) =>
              assert(source.exists())
              assert(dest.exists() && dest.isDirectory)
              val is = new FileInputStream(source).getChannel
              val os = new FileOutputStream(dest.getPath.concat(s"/${if(renaming.isEmpty) filename else renaming.get}")).getChannel
              is.transferTo(0,source.length(), os)
              is.close()
              os.close()

              val endDateStr = DateFormatBuilderImpl.ISO8601().format(DateFactory.now.asDate)
              bf+=s"[$endDateStr] End moving process"
              routerActor ! InstructionCompleted(id, bf.toList)
            }
          case MoveInstruction(filename, source, dest, renaming) =>
            val bf:ListBuffer[String] = ListBuffer.empty
            val dateStr = DateFormatBuilderImpl.ISO8601().format(DateFactory.now.asDate)
            bf+=s"[$dateStr] Start moving from Source[$source] to Destination[$dest]"

            keep2 (new File(s"$source/$filename"), new File(dest)) exec {(source, dest) =>
              assert(source.exists())
              assert(dest.exists() && dest.isDirectory)
              val is = new FileInputStream(source).getChannel
              val os = new FileOutputStream(dest.getPath.concat(s"/${if(renaming.isEmpty) filename else renaming.get}")).getChannel
              is.transferTo(0,source.length(), os)
              is.close()
              os.close()

              val df = DateFormatBuilderImpl.ISO8601()
              bf+=s"[${df.format(new Date)}] delete original file: ${source.delete()}"

              val endDateStr = DateFormatBuilderImpl.ISO8601().format(DateFactory.now.asDate)
              bf+=s"[$endDateStr] End moving process"
              routerActor ! InstructionCompleted(id, bf.toList)
            }
          case BackupInstruction(mode, source, dest)=>
            val bf:ListBuffer[String] = ListBuffer.empty
            val dateStr = DateFormatBuilderImpl.ISO8601().format(DateFactory.now.asDate)
            bf+=s"[$dateStr] Start TarGz on $source to $dest"
            keep{ new File(source) } exec {it=>
              assert(it.exists())
            }
            val en = EncryptingProtocolProvider(mode)
            val os= en.initSingle(dest)
            FileMan.tarGz(source, os, Seq.empty)
            val endDateStr = DateFormatBuilderImpl.ISO8601().format(DateFactory.now.asDate)
            bf+=s"[$endDateStr] End TarGz"
            routerActor ! InstructionCompleted(id, bf.toList)
        }
    }
  }
  class InstructionRecordActor extends Actor{
    val idProvider = new AtomicInteger(0)
    val errorActor: ActorSelection = context.actorSelection("/user/errorActor")
    val routerActor: ActorSelection = context.actorSelection("/user/routerActor")

    val instructionBkPath = "Q:\\temp\\instructions\\bk"

    val log:mutable.Map[Long, (File, ListBuffer[ String])] = mutable.Map.empty[Long,(File, ListBuffer[String])]
    override def receive: Receive = {
      case InstructionCompleted(id, list)=>
        val is = new FileInputStream(log(id)._1)
        val os = new FileOutputStream(new File(s"$instructionBkPath/instruction_${"%010d".format(id)}.instr"))
        IOUtils.copy(is, os)
        is.close()
        os.close()
        list.foreach(it=>log(id)._2.append(it))
        val date = DateFormatBuilderImpl.ISO8601().format(DateFactory.now.asDate())
        log(id)._2.append(s"[${date}] Deleting instr file[${log(id)._1.toString}]: ${log(id)._1.delete()}")
        val fos = new FileOutputStream(new File(s"$instructionBkPath/instruction_${"%010d".format(id)}.instr.log"))
        log(id)._2.toList.foreach(it=>fos.write(s"$it\r\n".getBytes()))
        fos.close()
      case InstructionFileDiscover(filename)=>
        val file = new File(filename)
        if(file.exists() && file.isFile && file.getName.endsWith(".instr")){
          val id = idProvider.incrementAndGet()
          if(!log.contains(id))
            log.put(id, (file,ListBuffer.empty))
          val date = DateFormatBuilderImpl.ISO8601().format(DateFactory.now.asDate())
          log(id)._2.append(s"[${date}] Inserting instruction file: ${file.getName}")
          routerActor ! RawInstructionMaterial(id, ()=>new FileInputStream(file))
        }
        else{
          if(!file.exists())
            errorActor ! InstructionFileNotFound(s"file[${file.toString}] not found")
          if(!file.isFile)
            errorActor ! InstructionFileNotFound(s"file[${file.toString}] is not file")
          if(!file.getName.endsWith(".instr"))
            errorActor ! InstructionFileNotFound(s"file[${file.toString}] is not match file name requirement")
        }
    }
  }
  class RouterActor extends Actor{
    val errorActor = context.actorSelection("/user/errorActor")
    val instructionActor = context.actorSelection("/user/instructionActor")
    val instructionRecordActor = context.actorSelection("/user/instructionRecordActor")
    val mapper = new ObjectMapper
    mapper.registerModule(DefaultScalaModule)

    override def receive: Receive = {
      case x:InstructionFileDiscover  =>
        instructionRecordActor ! x
      case x:InstructionCompleted =>
        instructionRecordActor ! x
      case RawInstructionMaterial(id, isProvider) =>
        val is = isProvider()
        val instructionProtocol = 428702
        if(InputStreamReader.readInt(is) == instructionProtocol){
          is.close()
          val de = DecryptingProtocolProvider(instructionProtocol)()
          de.initSingle(isProvider)
          val deIs = de.is()
          val s = Source.fromInputStream(deIs).mkString
          deIs.close()
          val ob = mapper.readValue(s, classOf[Instruction])
          instructionActor ! (id,ob)
          println(s"raw material received $s")
        }
        else{
          println("Encryption not supported")
          errorActor!NoSupportedEncryption(s"$id fail to decrypt, due to encryption not support")
        }
        is.close()

    }
  }

  class ErrorActor extends Actor{
    override def receive: Receive = {
      case NoSupportedEncryption(x)=>
        println(x)
    }
  }
}
