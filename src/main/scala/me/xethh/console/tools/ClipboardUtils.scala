package me.xethh.console.tools

import java.awt.Toolkit
import java.awt.datatransfer.{Clipboard, ClipboardOwner, DataFlavor, FlavorEvent, FlavorListener, StringSelection, Transferable}

private[tools] class ClipUtils extends ClipboardOwner{
  val clipboard = Toolkit.getDefaultToolkit.getSystemClipboard

  override def lostOwnership(clipboard: Clipboard, contents: Transferable): Unit = {
//    println(s"Losing control: $clipboard : $contents")
  }
}
object ClipUtils {
  private var clip:ClipUtils = new ClipUtils
  def reset = clip = new ClipUtils

  def clipboard:Clipboard = clip.clipboard

  def flavorType={
    clipboard.getContents(null).getTransferDataFlavors.foreach(println)
  }

  def read:String={
    val content = clipboard.getContents(null)
    println("Reading content type: "+(if(content==null) "null" else content.getClass))
    if(content!=null && content.isDataFlavorSupported(DataFlavor.stringFlavor)){
      content.getTransferData(DataFlavor.stringFlavor).asInstanceOf[String]
    }
    else throw new RuntimeException("DateFlavor not support")
  }
  def print:Unit={
    println(read)
  }

  def write(str:String)={
    clipboard.setContents(new StringSelection(str),  clip)
  }

  private var listening:Option[FlavorListener] = None
  def defaultListener = new FlavorListener {
    override def flavorsChanged(e: FlavorEvent): Unit = {
      println(s"New listener: ${e.toString}")
      println(s"Source: ${e.getSource}")
      clipboard.setContents(clipboard.getContents(null),clip)
    }
  }

  def listenToChange:Boolean={
    listenToChange(defaultListener)
  }
  def listenToChange(flavorListener: FlavorListener):Boolean={
    if(listening.isEmpty){
      clipboard.setContents(clipboard.getContents(null), clip)
      listening = Some(flavorListener)
      clipboard.addFlavorListener(listening.get)
      true
    }
    else false
  }
  def clearListener:Boolean={
    if(listening.isEmpty){
      false
    }
    else{
      clipboard.removeFlavorListener(listening.get)
      listening = None
      true
    }
  }

}
