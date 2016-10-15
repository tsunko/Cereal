package ai.seitok.natsuba.cereal.primitive;

import ai.seitok.natsuba.cereal.BoxingService;

import java.nio.ByteBuffer;

public class ShortBoxingService implements BoxingService<Short> {

    @Override
    public ByteBuffer serialize(Short object){
        return (ByteBuffer)ByteBuffer.allocate(Short.BYTES).putShort(object).flip();
    }

    @Override
    public Short deserialize(ByteBuffer data){
        return data.getShort();
    }

    @Override
    public int sizeOf(Short object){
        return Short.BYTES;
    }

    @Override
    public Class<Short> getType(){
        return short.class;
    }

}
