package me.xethh.console.tools.control

import java.io.InputStream

object InputStreamReader {
  def skipByte(inputStream: InputStream, take:Int) : Unit = (1 to take).foreach(it=>inputStream.read())
  def readAllByteArray(inputStream: InputStream):Array[Byte] =
    Stream.continually(inputStream.read()).takeWhile(_ != -1).map{_.toByte}.toArray
  def takeByteArray(inputStream: InputStream, take:Int):Array[Byte] =
    Stream.continually(inputStream.read()).take(take).map{_.toByte}.toArray
  def readBoolean(inputStream: InputStream):Boolean = {
    if(inputStream.read()==0) true else false
  }
  def readInt(inputStream:InputStream):Int = {
    def firstByte(byte: Int)= byte <<24
    def secondByte(byte: Int)= byte <<16
    def thirdByte(byte: Int)= byte << 8
    def fourthByte(byte: Int)= byte
    val s = Stream.continually(inputStream.read).take(4).toArray
    firstByte(s(0) ) | secondByte(s(1) ) | thirdByte(s(2)) | fourthByte(s(3))
  }

}
