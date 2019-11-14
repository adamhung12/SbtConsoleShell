package me.xethh.console.tools.control

import java.io.{Closeable, InputStream}

import scala.util.control.NonFatal

object TryWithResources {
  private def closeAndAddSuppressed(e: Throwable,
                                    resource: AutoCloseable): Unit = {
    if (e != null) {
      try {
        resource.close()
      } catch {
        case NonFatal(suppressed) =>
          e.addSuppressed(suppressed)
      }
    } else {
      resource.close()
    }
  }
  def tryResource[Y,R <: Closeable](x:R)(oper:(R=>Y)):Y = {
    require(x != null, "resource is null")
    var exception: Throwable = null
    try{
      oper(x)
    }
    catch {
      case NonFatal(e) =>
        exception = e
        throw e
    }
    finally {
      closeAndAddSuppressed(exception, x)
    }
  }

}
