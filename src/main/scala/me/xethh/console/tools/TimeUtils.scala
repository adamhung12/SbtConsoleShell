package me.xethh.console.tools

import java.util.Date
import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration

class TimeDisplayUtils(duration:Duration){
  def toHumanReadableString(duration:Long):String={
    s"${TimeUnit.DAYS.convert(duration, TimeUnit.NANOSECONDS)} days ${TimeUnit.HOURS.convert(duration, TimeUnit.NANOSECONDS)} hours ${TimeUnit.MINUTES.convert(duration, TimeUnit.NANOSECONDS)} minutes ${TimeUnit.SECONDS.convert(duration, TimeUnit.NANOSECONDS)} seconds"
  }
}
object TimeDisplayUtils {
  implicit def duration2TimeDisplayUtils(duration:Duration) = new TimeDisplayUtils(duration)

  def apply(duration: Duration): TimeDisplayUtils = new TimeDisplayUtils(duration)
  def apply(duration: Long): TimeDisplayUtils = new TimeDisplayUtils(Duration(duration, TimeUnit.MILLISECONDS))
}
