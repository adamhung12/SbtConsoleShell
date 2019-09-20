package me.xethh.console.tools.mail.imap

class ImapClient(host:String, port:Int, username:String, password:String) {
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

object ImapClient{
  def apply(host: String, port: Int, username: String, password: String): ImapClient = new ImapClient(host, port, username, password)
  def main(args: Array[String]): Unit = {
    import javax.mail.{Flags, Folder}
    import me.xethh.utils.dateManipulation.DateFactory
    val username = "seth.hung@xethh.me"
    val password = "RhbSBDbUKfU6"
    val host = "imappro.zoho.com"
    val port = 993
    val eliminator = new ImapClient(host, port, username, password)
    val (inbox, messages) = eliminator.allMessages("INBOX",Folder.READ_WRITE)

    println(s"Total Message count ${messages.size}")
    import javax.mail.internet.InternetAddress
    val groupedMail = messages.filter(it=>it.getFrom.head.asInstanceOf[InternetAddress].getAddress=="admin@xethh.me").groupBy(m=>{
      val view = DateFactory.from(m.getSentDate).view()
      s"${view.year}:${"%02d".format(view.month().toCalNumber)}:${"%02d".format(view.day())}"
    }).toList.sortBy(_._1)
    groupedMail.foreach{it=>println(s"${it._1}: ${it._2.size}")}
    inbox.open(Folder.READ_WRITE)
    groupedMail.flatMap{it=>it._2}.foreach({it=>it.setFlag(Flags.Flag.DELETED,true)})
    inbox.close(true)


    groupedMail.foreach(messagePair=>{
      println("Processing date: "+messagePair._1)
      val (heartBeatMail, nonHeartBeatMails) = messagePair._2.partition(_.getSubject.toLowerCase.startsWith("heart beat"))
      print(s"Summary: ${heartBeatMail.size} heart beat mails, ${nonHeartBeatMails.size} non heart beat mails")

      if(heartBeatMail.size>0){
        println(s"Further process: ${messagePair._1}")
        heartBeatMail.zipWithIndex.foreach(x=>{
          println(s"Deleting ${x._2}: ${x._1.getSubject}[${x._1.getSentDate}]")
          x._1.setFlag(Flags.Flag.DELETED, true)
        })
        println(s"Deleted ${heartBeatMail.size} messages")
      }

    })

    inbox.close(true)
    eliminator.store.close()

  }
}
