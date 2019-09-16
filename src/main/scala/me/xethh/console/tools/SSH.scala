package me.xethh.console.tools

import java.util.Properties

import com.jcraft.jsch.{Channel, JSch}

class SSH(username:String, password:String, host:String, port:Int) {
  val jsch = new JSch
  val session = jsch.getSession(username,password,port)
  session.setPassword(password)
  val config = new Properties
  config.put("StrictHostKeyChecking", "no")
  session.setConfig(config)
  session.connect()

  def shell(): Channel ={
    session.openChannel("shell")
  }

}
object SSH{
}
