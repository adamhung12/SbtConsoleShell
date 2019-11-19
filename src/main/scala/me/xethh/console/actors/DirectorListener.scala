package me.xethh.console.actors

import java.io.File
import java.nio.file.{FileSystems, StandardWatchEventKinds}

import akka.actor.{Actor, Cancellable}
import me.xethh.console.actors.ActorModel.InstructionFileDiscover

object DirectorListener{
  case class PathToCheck(path:String)
  class InstructionDiscoverActor extends Actor{
    val router = AkkaSys.sys.actorSelection("/user/routerActor")
    override def receive: Receive = {
      case PathToCheck(path) =>
        println("Received actor")
        val files = Option(new File(path).listFiles()).getOrElse(Array.empty).filter(_.isFile).filter(_.getName.endsWith(".instr"))
        files.foreach{it=>
          println(s"Send file ${it.toString}")
          router ! InstructionFileDiscover(it.toString)
        }
    }
  }

  class ListenerActor(pathStr:String){
    var schedule:Cancellable = _
    val service = FileSystems.getDefault.newWatchService()
    val path = new File(pathStr).toPath
    path.register(service, StandardWatchEventKinds.ENTRY_CREATE)
    import AkkaSys.sys.dispatcher

    import scala.concurrent.duration._
    val instructionDiscoverActor = AkkaSys.sys.actorSelection("/user/instructionDiscoverActor")
    val cancellable = schedule = AkkaSys.sys.scheduler.scheduleWithFixedDelay(10 second, 10 second){()=>
      Option(service.poll()) match {
        case Some(x) =>
          import scala.collection.JavaConverters._
          x.pollEvents().asScala.foreach{it=>
            println(it.context())
          }
          instructionDiscoverActor ! PathToCheck(path.toFile.toString)
          x.reset()
          println(s"Send check instruction on event(${x.toString}).")
        case _ =>
          println("No thing new")
      }
    }

  }
}
