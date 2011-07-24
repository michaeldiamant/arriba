package arriba.fix.netty;

import org.jboss.netty.channel.Channel;


public interface ChannelRepository<ID> {

    void add(ID id, Channel channel);

    void remove(Channel channel);

    void remove(ID id);

    Channel find(ID id);
}
