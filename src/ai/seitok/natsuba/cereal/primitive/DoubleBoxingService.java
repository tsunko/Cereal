package ai.seitok.natsuba.cereal.primitive;

import ai.seitok.natsuba.cereal.BoxingService;

import java.nio.ByteBuffer;

public class DoubleBoxingService implements BoxingService<Double> {

    @Override
    public ByteBuffer serialize(Double object){
        return (ByteBuffer)ByteBuffer.allocate(Double.BYTES).putDouble(object).flip();
    }

    @Override
    public Double unserialize(ByteBuffer data){
        return data.getDouble();
    }

    @Override
    public int sizeOf(Double object){
        return Double.BYTES;
    }

    @Override
    public Class<Double> getType(){
        return double.class;
    }
    
}
