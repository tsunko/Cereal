package ai.seitok.natsuba.cereal.primitive;

import ai.seitok.natsuba.cereal.BoxingService;

import java.nio.ByteBuffer;

public class FloatBoxingService implements BoxingService<Float> {

    @Override
    public ByteBuffer serialize(Float object){
        return (ByteBuffer)ByteBuffer.allocate(Float.BYTES).putFloat(object).flip();
    }

    @Override
    public Float deserialize(ByteBuffer data){
        return data.getFloat();
    }

    @Override
    public int sizeOf(Float object){
        return Float.BYTES;
    }

    @Override
    public Class<Float> getType(){
        return float.class;
    }
    
}
