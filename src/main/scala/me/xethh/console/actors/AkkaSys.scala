package me.xethh.console.actors

import java.io.{ByteArrayInputStream, File, FileInputStream}

import akka.actor.{ActorSystem, Props}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import me.xethh.console.actors.ActorModel.{BackupInstruction, ErrorActor, InstructionActor, InstructionFileDiscover, InstructionRecordActor, MoveInstruction, RawInstructionMaterial, RouterActor}
import me.xethh.console.tools.control.HoldObject.keep
import protocol.m217845.{P217845, PH217845}
import protocol.m428702.P428702
import protocol.{DecryptingProtocolProvider, EncryptingProtocolProvider}

import scala.io.Source

object AkkaSys extends App {
  val sys = ActorSystem("System")
  val errorActor = sys.actorOf(Props[ErrorActor], "errorActor")
  val routerActor = sys.actorOf(Props[RouterActor], "routerActor")
  val instructionActor = sys.actorOf(Props[InstructionActor],"instructionActor")
  val instructionRecordActor = sys.actorOf(Props[InstructionRecordActor],"instructionRecordActor")

  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  val base = "Q:\\temp\\instructions\\from"
  val toBase = "Q:\\temp\\instructions\\to"
  var encrypted = mapper.writeValueAsString(MoveInstruction("a.html", s"$base", s"$toBase"))
  val instructionProtocol = P428702
  var en = EncryptingProtocolProvider(instructionProtocol.protocolId)
  keep{new File(s"$base/a.instr")} check(_.exists()) exec(_.delete())
  var os = en.initSingle(s"${base}/a.instr")
  os.write(encrypted.getBytes())
  en.writeHeader()


  val bkProtocol = P217845
  encrypted = mapper.writeValueAsString(BackupInstruction(bkProtocol.protocolId, "Q:\\temp\\server601_user", "Q:\\temp\\x.tar.gz.crypt"))
  en = EncryptingProtocolProvider(instructionProtocol.protocolId)
  keep{new File(s"$base/b.instr")} check(_.exists()) exec(_.delete())
  os = en.initSingle(s"${base}/b.instr")
  os = en.dataStream()
  os.write(encrypted.getBytes())
  en.writeHeader()

  routerActor ! InstructionFileDiscover(s"$base/a.instr")
  routerActor ! InstructionFileDiscover(s"$base/b.instr")

  println("")
}
