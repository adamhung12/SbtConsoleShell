package me.xethh.console.tools.fileMan

import java.io.{File, FileInputStream, FileOutputStream, FilenameFilter, InputStream, OutputStream}
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributeView

import me.xethh.libs.toolkits.OSDetecting.{OS, OSDector}
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
              val os = new FileOutputStream(curfile)
              IOUtils.copy(fin, os)
              os.flush()
              os.close()
              curfile.setLastModified(entry.getLastModifiedDate.getTime)
            }
          }
        } finally if (fin != null) fin.close()
      }
    }
    decompress(tarArchiveInputStream, new File(unTarToStr))
  }

  def unTarStream(inputStream: InputStream) = new TarArchiveInputStream(inputStream)
  def unTarOnlyTo(inputStream: InputStream, unTarToStr:String)={
    val unTarTo = new File(unTarToStr)
    if(unTarTo.exists())
      throw new RuntimeException(s"Un Tar Destination[${unTarTo}] already exist")

    unTar(unTarStream(inputStream), unTarTo.toString)
  }
  def unTarGzTo(inputStream: InputStream, unTarToStr:String)={
    val unTarTo = new File(unTarToStr)
    if(unTarTo.exists())
      throw new RuntimeException(s"Un Tar Destination[${unTarTo}] already exist")

    unTar(unTarStream(inputStream), unTarTo.toString)
  }

  def tarWithoutFilter(out: TarArchiveOutputStream, file: File, finalResult: File): Unit ={
    tar(out, file, finalResult, List.empty);
  }

  def tar(out: TarArchiveOutputStream, file: File, finalResult: File, filters: Seq[FilenameFilter]){
    def addToArchiveCompression(out: TarArchiveOutputStream, file: File, dir: String, fileBase:String, filters: Seq[FilenameFilter]): Unit = {
      val entry = dir
      if (file.isFile) {
        val archiveEntry = new TarArchiveEntry(file, entry.replace(fileBase, ""))
        archiveEntry.setSize(file.length())
        archiveEntry.setModTime(file.lastModified())
        archiveEntry.setMode(
          (if(file.canRead) 4 else 0)+
            (if(file.canWrite) 2 else 0)+
            (if(file.canExecute) 1 else 0)
        )
        out.putArchiveEntry(archiveEntry)
        try {
          val in = new FileInputStream(file)
          try IOUtils.copy(in, out)
          finally if (in != null) in.close()
        }
        out.closeArchiveEntry()
        out.flush()
      }
      else if (file.isDirectory) {
        val children = file.listFiles
        if (children != null) for (child <- children) {
          addToArchiveCompression(out, child, child.toString, fileBase, filters)
        }
      }
      else System.out.println(file.getName + " is not supported")
    }
    addToArchiveCompression(out, file, finalResult.toString, file.toString)
    out.flush()
    out.close()
  }
  def tarGz(pathTarget:String, outputStream:OutputStream, filters: Seq[FilenameFilter]): Unit= {
    val taos = new TarArchiveOutputStream(new GzipCompressorOutputStream(outputStream))
    tar(taos, new File(pathTarget), new File(pathTarget),filters)
  }
  def tarOnly(pathTarget:String,outputStream: OutputStream, filters: Seq[FilenameFilter]): Unit= {
    val taos = new TarArchiveOutputStream(outputStream)
    tar(taos, new File(pathTarget), new File(pathTarget), filters)
  }
}
