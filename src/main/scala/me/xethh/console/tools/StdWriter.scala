package me.xethh.console.tools

import java.io.{FileOutputStream, OutputStream, OutputStreamWriter, PrintStream}
import java.nio.charset.StandardCharsets

class StdWriter(val processed:OutputStream) extends PrintStream(processed){
  def this(parent:PrintStream, fileName:String)= {
    this(new OutputStream {
//      val fos = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8)
      val fos = new FileOutputStream(fileName)

      override def write(b: Int): Unit = {
        parent.write(b)
        fos.write(b)
        fos.flush()
      }
    })
  }
}

object StdWriter {
  var intercepted = false
  def intercepting(fileName:String):Option[PrintStream]= {
    if (intercepted) {
      None
    }
    else {
      val parent = System.out
      System.setOut(new StdWriter(parent, fileName))
      Some(System.out)
    }
  }
}
