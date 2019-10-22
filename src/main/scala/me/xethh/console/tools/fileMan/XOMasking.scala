package me.xethh.console.tools.fileMan

import java.io.{InputStream, OutputStream}

object XOMasking {
  def unmask(os:OutputStream, mask:Array[Int]):OutputStream = new OutputStream{
    var index:Int = -1;
    def nexIndex():Int = {
      index=index+1
      index
    }

    override def write(b: Int): Unit = {
      os.write(b ^ mask(nexIndex() % mask.length))
    }
  }

  def mask(is:InputStream, mask:Array[Int]):InputStream = new InputStream {
    var index:Int = -1;
    def nexIndex():Int = {
      index=index+1
      index
    }

    override def read(): Int = {
      val read = is.read()
      index = nexIndex() % mask.length
      val m = mask(index)
      val rs = read ^ m
      rs
//      is.read() ^ mask(nexIndex() % mask.length)
    }
  }

  def main(args: Array[String]): Unit = {
    val is = new InputStream {
      var index=0
      val data = Array(1,2,4,5,6)
      override def read(): Int = data({index=index+1;index-1} % data.length)

      override def available(): Int = 10 - index
    }
    val os = new OutputStream {
      override def write(b: Int): Unit = println(b)
    }
    val mIs = mask(is, Array(2,3,4,4,6,0,7,7))
    val mOs = unmask(os, Array(2,3,4,4,6,0,7,7))
    val inputs = Stream.continually(mIs.read).take(10).map(it=>
      it
    ).toArray
    inputs.foreach(println)
    println("--------------")
    inputs.foreach(mOs.write)
  }

}
