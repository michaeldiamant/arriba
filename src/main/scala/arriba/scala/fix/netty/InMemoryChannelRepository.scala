package arriba.scala.fix.netty

import org.jboss.netty.channel.{ChannelFuture, Channel, ChannelFutureListener}
import org.jboss.netty.util.internal.ConcurrentHashMap

final class InMemoryChannelRepository[ID] extends ChannelRepository[ID] {


  def add(id: ID, channel: Channel) {
    val previousChannel: Channel = this.idToChannel.putIfAbsent(id, channel)
    if (null == previousChannel) {
      channel.getCloseFuture.addListener(this.removeChannelListener)
    }
  }

  def remove(channel: Channel) {
    val iterator = this.idToChannel.entrySet.iterator
    while (iterator.hasNext) {
      if (channel eq iterator.next.getValue) {
        iterator.remove()
        return
      }
    }
  }

  def remove(id: ID) {
    this.idToChannel.remove(id)
  }

  def find(id: ID): Channel = {
    val channel: Channel = this.idToChannel.get(id)
    if (null == channel) {
      throw new Exception("Received unknown channel ID: " + id + ".")
    }
    channel
  }

  private val idToChannel: ConcurrentHashMap[ID, Channel] = new ConcurrentHashMap[ID, Channel]
  private val removeChannelListener: ChannelFutureListener = new ChannelFutureListener {
    def operationComplete(future: ChannelFuture) {
      InMemoryChannelRepository.this.remove(future.getChannel)
    }
  }
}

