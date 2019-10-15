package me.xethh.console.tools.fileMan

import java.io.{File, FileInputStream, FileOutputStream}

import org.apache.commons.compress.archivers.tar.{TarArchiveEntry, TarArchiveInputStream, TarArchiveOutputStream}
import org.apache.commons.compress.compressors.gzip.{GzipCompressorInputStream, GzipCompressorOutputStream}
import org.apache.commons.compress.utils.IOUtils

object FileMan {
  def unTar(tarArchiveInputStream: TarArchiveInputStream, unTarToStr:String)={

    def decompress(in: TarArchiveInputStream, out: File): Unit = {
      try {
        val fin = in
        try {
          var entry:TarArchiveEntry = null
          while ( {
            entry = fin.getNextTarEntry
            entry != null
          }) {
            if (!entry.isDirectory){
              val curfile = new File(out, entry.getName)
              val parent = curfile.getParentFile
              if (!parent.exists) parent.mkdirs
              IOUtils.copy(fin, new FileOutputStream(curfile))
            }
          }
        } finally if (fin != null) fin.close()
      }
    }
    decompress(tarArchiveInputStream, new File(unTarToStr))
  }

  def unTarOnly(unTarFileStr:String, unTarToStr:String)={
    val unTarfile = new File(unTarFileStr)
    if(!unTarfile.exists()) throw new RuntimeException(s"Un Tar Target[${unTarfile}] not exists")

    val unTarTo = new File(unTarToStr)
    if(unTarTo.exists())
      throw new RuntimeException(s"Un Tar Destination[${unTarTo}] already exist")

    val fin = new TarArchiveInputStream(new FileInputStream(unTarfile))
    unTar(fin, unTarTo.toString)
  }
  def unTarGz(unTarFileStr:String, unTarToStr:String)={
    val unTarfile = new File(unTarFileStr)
    if(!unTarfile.exists()) throw new RuntimeException(s"Un Tar Target[${unTarfile}] not exists")

    val unTarTo = new File(unTarToStr)
    if(unTarTo.exists())
      throw new RuntimeException(s"Un Tar Destination[${unTarTo}] already exist")

    val fin = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(unTarfile)))
    unTar(fin, unTarTo.toString)
  }
  def tar(out: TarArchiveOutputStream, file: File, finalResult: File){
    def addToArchiveCompression(out: TarArchiveOutputStream, file: File, dir: String, fileBase:String): Unit = {
      val entry = dir
      if (file.isFile) {
        out.putArchiveEntry(new TarArchiveEntry(file, entry.replace(fileBase, "")))
        try {
          val in = new FileInputStream(file)
          try IOUtils.copy(in, out)
          finally if (in != null) in.close()
        }
        out.closeArchiveEntry()
      }
      else if (file.isDirectory) {
        val children = file.listFiles
        if (children != null) for (child <- children) {
          addToArchiveCompression(out, child, child.toString, fileBase)
        }
      }
      else System.out.println(file.getName + " is not supported")
    }
    addToArchiveCompression(out, file, finalResult.toString, file.toString)
    out.flush()
    out.close()
  }
  def tarGz(pathTarget:String, resultTar:String): Unit= {
    if(new File(resultTar).exists())
      throw new RuntimeException(s"Path[${resultTar}] already exists")
    else{
      val taos = new TarArchiveOutputStream(new GzipCompressorOutputStream(new FileOutputStream(resultTar)))
      tar(taos, new File(pathTarget), new File(resultTar))
    }

  }
  def tarOnly(pathTarget:String, resultTar:String): Unit= {
    if(new File(resultTar).exists())
      throw new RuntimeException("Path already exists")
    else{
      val taos = new TarArchiveOutputStream(new FileOutputStream(resultTar))
      tar(taos, new File(pathTarget), new File(resultTar))
    }
  }
}
