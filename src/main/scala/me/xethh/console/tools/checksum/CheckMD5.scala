package me.xethh.console.tools.checksum

import java.io.{File, FileInputStream}
import java.math.BigInteger
import java.security.MessageDigest

import scala.io.Source

object CheckMD5 {
  def md5(file: String): String = {
    md5File(new File(file))
  }
  def md5File(file: File): String = {
    val ch = new FileInputStream(file)
    val digest = MessageDigest.getInstance("MD5")
    Stream.continually(ch.read()).takeWhile(_ != -1).map{it=>it.toByte}.foreach(digest.update)
    val d = digest.digest()
    val bi = new BigInteger(1, d)
    bi.toString(16)
  }

  def main(args: Array[String]): Unit = {
    println(md5("Q:\\sbt_console\\SbtConsoleProject\\src\\main\\scala\\scripts\\bk\\pg\\BK_MYSQL.scala"))
  }

}
