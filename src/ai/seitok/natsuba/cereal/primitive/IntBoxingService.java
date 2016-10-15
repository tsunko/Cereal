package ai.seitok.natsuba.cereal.primitive;

import ai.seitok.natsuba.cereal.BoxingService;

import java.nio.ByteBuffer;

public class IntBoxingService implements BoxingService<Integer> {

    @Override
    public ByteBuffer serialize(Integer object){
        return (ByteBuffer)ByteBuffer.allocate(Integer.BYTES).putInt(object).flip();
    }

    @Override
    public Integer deserialize(ByteBuffer data){
        return data.getInt();
    }

    @Override
    public int sizeOf(Integer object){
        return Integer.BYTES;
    }

    @Override
    public Class<Integer> getType(){
        return int.class;
    }

}
