package arriba.scala.fix.netty

import org.jboss.netty.channel.Channel

trait ChannelRepository[ID] {
  def add(id: ID, channel: Channel)

  def remove(channel: Channel)

  def remove(id: ID)

  def find(id: ID): Channel
}

