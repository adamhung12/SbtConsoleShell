package me.xethh.console.tools.sftp

import java.io.File

import org.apache.commons.vfs2.FileSystemOptions
import org.apache.commons.vfs2.provider.sftp.{IdentityInfo, SftpFileSystemConfigBuilder}

object VFS {
  def conn(hostname:String, username:String, password:String, keyPath:String, passphrase:String, remoteFile:String):String={
    if(keyPath!=null)
      s"sftp://${username}@${hostname}/${remoteFile}"
    else
      s"sftp://${username}:${password}@${hostname}/"
  }
  def createDefaultOptions(keyPath:String, passphrase:String)={
    val options = new FileSystemOptions()
    SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(options, "no")
    SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, true)
    SftpFileSystemConfigBuilder.getInstance().setSessionTimeoutMillis(options, 10000)
    if(keyPath!=null){
      val identityInfo = if(passphrase!=null)
        new IdentityInfo(new File(keyPath), passphrase.getBytes)
      else
        new IdentityInfo(new File(keyPath))
      SftpFileSystemConfigBuilder.getInstance().setIdentityProvider(options, identityInfo)
    }
    options
  }


}
