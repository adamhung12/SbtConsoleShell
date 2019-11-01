package me.xethh.console.tools.fileMan

import java.io.{InputStream, OutputStream}

object XOMasking {
  def maskOutputStream(os:OutputStream, mask:Array[Int]):OutputStream = new OutputStream {
    var index:Int = -1;
    def nexIndex():Int = {
      index=index+1
      index % mask.length
    }

    override def write(b: Int): Unit = {
      os.write(mask(nexIndex()) ^ b)
    }
  }

  def maskInputStream(is:InputStream, mask:Array[Int]):InputStream = new InputStream {
    var index:Int = -1;
    def nexIndex():Int = {
      index=index+1
      index % mask.length
    }

    override def read(): Int =  {
      val value = is.read()
      if(value!= -1)
        mask(nexIndex()) ^ value
      else
        value
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
    val mIs = maskInputStream(is, Array(2,3,4,4,6,0,7,7))
    val mOs = maskOutputStream(os, Array(2,3,4,4,6,0,7,7))
    val inputs = Stream.continually(mIs.read).take(10).map(it=>
      it
    ).toArray
    inputs.foreach(println)
    println("--------------")
    inputs.foreach(mOs.write)
  }

}
