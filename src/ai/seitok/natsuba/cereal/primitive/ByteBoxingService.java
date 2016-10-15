package ai.seitok.natsuba.cereal.primitive;

import ai.seitok.natsuba.cereal.BoxingService;

import java.nio.ByteBuffer;

public class ByteBoxingService implements BoxingService<Byte> {

    @Override
    public ByteBuffer serialize(Byte object){
        return (ByteBuffer)ByteBuffer.allocate(Byte.BYTES).put(object).flip();
    }

    @Override
    public Byte deserialize(ByteBuffer data){
        return data.get();
    }

    @Override
    public int sizeOf(Byte object){
        return Byte.BYTES;
    }

    @Override
    public Class<Byte> getType(){
        return byte.class;
    }

}