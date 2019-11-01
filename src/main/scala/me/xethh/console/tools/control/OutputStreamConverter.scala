package me.xethh.console.tools.control

import java.io.{InputStream, OutputStream}

object OutputStreamConverter {
  def writeSomeValue(outputStream:OutputStream, byteSize:Int, value:Int):Unit = (1 to byteSize).foreach{ it=>
    outputStream.write(value)
    outputStream.flush()
  }
  def writeEmpty(outputStream:OutputStream,byteSize:Int):Unit = writeSomeValue(outputStream, byteSize, 0)
  def writeBoolean(boolean:Boolean) = if(boolean) 1 else 0
  def writeInt(int:Int):Array[Byte] = {
    Array(
      (int>>24).toByte,
      (int>>16).toByte,
      (int>>8).toByte,
      (int).toByte
    )
  }

}
