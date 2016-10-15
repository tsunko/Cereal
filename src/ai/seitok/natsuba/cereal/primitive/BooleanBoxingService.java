package ai.seitok.natsuba.cereal.primitive;

import ai.seitok.natsuba.cereal.BoxingService;

import java.nio.ByteBuffer;

public class BooleanBoxingService implements BoxingService<Boolean> {

    @Override
    public ByteBuffer serialize(Boolean object){
        return ByteBuffer.allocate(1).put((byte)(object ? 1 : 0));
    }

    @Override
    public Boolean unserialize(ByteBuffer data){
        return data.get() == 1;
    }

    @Override
    public int sizeOf(Boolean object){
        return 1;
    }

    @Override
    public Class<Boolean> getType(){
        return boolean.class;
    }


}