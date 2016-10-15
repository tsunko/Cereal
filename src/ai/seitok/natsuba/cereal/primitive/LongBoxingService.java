package ai.seitok.natsuba.cereal.primitive;

import ai.seitok.natsuba.cereal.BoxingService;

import java.nio.ByteBuffer;

public class LongBoxingService implements BoxingService<Long> {

    @Override
    public ByteBuffer serialize(Long object){
        return (ByteBuffer)ByteBuffer.allocate(Long.BYTES).putLong(object).flip();
    }

    @Override
    public Long unserialize(ByteBuffer data){
        return data.getLong();
    }

    @Override
    public int sizeOf(Long object){
        return Long.BYTES;
    }

    @Override
    public Class<Long> getType(){
        return long.class;
    }

}
