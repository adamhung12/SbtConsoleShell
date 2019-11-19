package me.xethh.console.actors

import java.io.{File, FileInputStream, FileOutputStream}

import akka.actor.{ActorSystem, Props}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import me.xethh.console.actors.ActorModel.{BackupInstruction, CopyInstruction, ErrorActor, InstructionActor, InstructionFileDiscover, InstructionRecordActor, MoveInstruction, RouterActor}
import me.xethh.console.actors.DirectorListener.{InstructionDiscoverActor, ListenerActor}
import me.xethh.console.tools.control.HoldObject.keep
import org.apache.commons.io.IOUtils
import protocol.{EncryptProtocol, EncryptingProtocolProvider, SpecOutputStream}
import protocol.m217845.P217845
import protocol.m428702.P428702

object AkkaSys extends App {
  val sys = ActorSystem("System")
  val errorActor = sys.actorOf(Props[ErrorActor], "errorActor")
  val routerActor = sys.actorOf(Props[RouterActor], "routerActor")
  val instructionActor = sys.actorOf(Props[InstructionActor],"instructionActor")
  val instructionRecordActor = sys.actorOf(Props[InstructionRecordActor],"instructionRecordActor")
  val instructionDiscoverActor = sys.actorOf(Props[InstructionDiscoverActor], "instructionDiscoverActor")

//  new ListenerActor("Q:\\temp\\instructions\\")

  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  val base = "Q:\\temp\\instructions\\from"
  val toBase = "Q:\\temp\\instructions\\to"
  val bkProtocol = P217845
  var encrypted:String = _
  var en:EncryptProtocol = _
  var os:SpecOutputStream = _
  val instructionProtocol = P428702

  val block = {
    val source = s"$base/x.html"
    val dest1 = s"$base/a.html"
    val dest2 = s"$base/b.html"
    val dest3 = s"$base/c.html"
    keep {new File(dest1)} check {_.exists()} exec {_.delete()}
    keep {new File(dest2)} check {_.exists()} exec {_.delete()}
    keep {new File(dest3)} check {_.exists()} exec {_.delete()}
    IOUtils.copy(new FileInputStream(source), new FileOutputStream(dest1))
    IOUtils.copy(new FileInputStream(source), new FileOutputStream(dest2))
    IOUtils.copy(new FileInputStream(source), new FileOutputStream(dest3))
  }

  encrypted = mapper.writeValueAsString(MoveInstruction("a.html", s"$base", s"$toBase", None))
  en = EncryptingProtocolProvider(instructionProtocol.protocolId)
  keep{new File(s"$base/a.instr")} check(_.exists()) exec(_.delete())

  os = en.initSingle(s"${base}/a.instr")
  os.write(encrypted.getBytes())
  en.writeHeader()

  encrypted = mapper.writeValueAsString(BackupInstruction(bkProtocol.protocolId, "Q:\\temp\\server601_user", "Q:\\temp\\x.tar.gz.crypt"))
  en = EncryptingProtocolProvider(instructionProtocol.protocolId)
  keep{new File(s"$base/b.instr")} check(_.exists()) exec(_.delete())
  os = en.initSingle(s"${base}/b.instr")
  os = en.dataStream()
  os.write(encrypted.getBytes())
  en.writeHeader()

  encrypted = mapper.writeValueAsString(MoveInstruction("b.html", s"$base", s"$toBase", Some("c.html")))
  en = EncryptingProtocolProvider(instructionProtocol.protocolId)
  keep{new File(s"$base/c.instr")} check(_.exists()) exec(_.delete())
  en.initSingle(s"${base}/c.instr")
  os = en.dataStream()
  os.write(encrypted.getBytes())
  en.writeHeader()

  encrypted = mapper.writeValueAsString(CopyInstruction("c.html", s"$base", s"$toBase", Some("d.html")))
  en = EncryptingProtocolProvider(instructionProtocol.protocolId)
  keep{new File(s"$base/d.instr")} check(_.exists()) exec(_.delete())
  en.initSingle(s"${base}/d.instr")
  os = en.dataStream()
  os.write(encrypted.getBytes())
  en.writeHeader()

  routerActor ! InstructionFileDiscover(s"$base/a.instr")
  routerActor ! InstructionFileDiscover(s"$base/b.instr")
  routerActor ! InstructionFileDiscover(s"$base/c.instr")
  routerActor ! InstructionFileDiscover(s"$base/d.instr")

  println("")
}
