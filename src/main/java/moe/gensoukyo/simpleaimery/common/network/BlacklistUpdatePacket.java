package moe.gensoukyo.simpleaimery.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.nio.charset.StandardCharsets;

/**
 * @author ChloePrime
 */
public class BlacklistUpdatePacket implements IMessage {
    boolean isWhite;
    String[] list;
    public BlacklistUpdatePacket() {}

    public BlacklistUpdatePacket(boolean isWhite, String[] list) {
        this.isWhite = isWhite;
        this.list = list;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(isWhite);
        buf.writeInt(list.length);
        for (String s : list) {
            buf.writeInt(s.length());
            buf.writeCharSequence(s, StandardCharsets.UTF_8);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.isWhite = buf.readBoolean();
        int arrLength = buf.readInt();
        this.list = new String[arrLength];
        for (int i = 0; i < arrLength; i++) {
            int strLen = buf.readInt();
            this.list[i] = buf.readCharSequence(strLen, StandardCharsets.UTF_8).toString();
        }
    }
}
