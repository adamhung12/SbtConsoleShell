class HeartbeatMailEliminate(host:String, port:Int, username:String, password:String) {
  import java.util.Properties

  import javax.mail._

  val session = Session.getInstance(new Properties())
  val store = {
    val store = session.getStore("imaps")
    store.connect(host, port, username,password)
    store
  }

  def listAllFolder = {
    store.getDefaultFolder.list()
  }

  def listFolder(folderName:String)={
    store.getFolder(folderName)
  }

  def allMessages(folderName:String, readMode:Int): (Folder, Array[Message]) ={
    val folder = {
      val folder = store.getFolder(folderName)
      if(!folder.exists())
        throw new RuntimeException(s"Folder $folderName not exists")
      folder
    }

    folder.open(readMode)
    (folder, folder.getMessages)
  }
}
object ImapsClient{
  def apply(host: String, port: Int, username: String, password: String): HeartbeatMailEliminate = new HeartbeatMailEliminate(host, port, username, password)
}

import javax.mail.Flags.Flag
import javax.mail.Folder
import javax.mail.internet._
val password = "dfnmdorkeocixpis"
val username = "adamhung12@gmail.com"
val (host, port) = ("imap.gmail.com",993)

val mailClient = ImapsClient(host, port, username, password)

mailClient.store.getDefaultFolder.list.foreach(println)
val inbox = mailClient.store.getFolder("INBOX")

inbox.open(Folder.READ_WRITE)
val (messages,messageCount) = (inbox.getMessages, inbox.getMessageCount)

println(s"Total message count in inbox: ${messageCount}")
messages.zipWithIndex.filter(item=>{
  val (msg, index) = item
  println(s"Processing ${index}/${messageCount}")
  msg.getFrom.size==1
}).count(_=>true)
val groupedMessages = messages.groupBy(_.getFrom()(0).asInstanceOf[InternetAddress].getAddress).toList
println(s"${groupedMessages.size} distinct senders")
groupedMessages.sortBy(_._2.size).foreach(x=>println(s"${x._2.size} messages from ${x._1}"))

val hktvmall = mailClient.store.getFolder("HKTVMall")
groupedMessages.filter(item=>item._1.toString.toLowerCase.contains("hktvmall.com"))
inbox.copyMessages(groupedMessages.filter(item=>item._1.toString.toLowerCase.contains("hktvmall.com"))(0)._2,hktvmall)
inbox.copyMessages(groupedMessages.filter(item=>item._1.toString.toLowerCase.contains("hktvmall.com"))(1)._2,hktvmall)

groupedMessages.filter(item=>item._1.toString.toLowerCase.contains("hktvmall.com"))(0)._2.foreach(item=>println(item.getSubject))
groupedMessages.filter(item=>item._1.toString.toLowerCase.contains("hktvmall.com"))(0)._2.foreach(item=>item.setFlag(Flag.DELETED, true))


groupedMessages.filter(_._2.size>50).foreach(x=>println(x._1))
val custFolder = mailClient.store.getFolder("CustFolder")
custFolder.create(Folder.HOLDS_MESSAGES)

import javax.mail.internet.InternetAddress
groupedMessages.filter(_._2.size>50).foreach(item=>println(s"${item._1}: ${item._2.size}"))
groupedMessages.filter(_._2.size>50).foreach(item=>custFolder.getFolder(item._1.toString).create(Folder.HOLDS_MESSAGES))
groupedMessages.filter(_._2.size>50).foreach(item=>custFolder.getFolder(item._1.toString).delete(false))
groupedMessages.filter(_._2.size>50).foreach(item=>custFolder.getFolder(item._1.asInstanceOf[InternetAddress].getAddress).create(Folder.HOLDS_MESSAGES))
groupedMessages.filter(_._2.size>50).foreach(item=>{
  val (addr, msgs) = item
  val address = addr.asInstanceOf[InternetAddress]
  println(s"Processing address: ${address.getAddress} with ${msgs.size} mails")
  val total = msgs.size
  println(s"Copying $total message to folder ${address.getAddress}")
  inbox.copyMessages(msgs,custFolder.getFolder(address.getAddress))
})

groupedMessages.filter(_._2.size>50).foreach(item=>{
  val (addr, msgs) = item
  val address = addr.asInstanceOf[InternetAddress]
  println(s"Deleting address: ${address.getAddress} with ${msgs.size} mails")
  val total = msgs.size
  msgs.zipWithIndex.foreach(item=>{
    val(msg, index) = item
    println(s"Deleting ${index+1}/$total}")
    msg.setFlag(Flag.DELETED, true)
  })
})
