package ai.seitok.natsuba.cereal.obj;

import ai.seitok.natsuba.cereal.BoxingService;

import java.nio.ByteBuffer;

public class NullBoxingService implements BoxingService {


    @Override
    public ByteBuffer serialize(Object theNull){
        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put((byte)0);
        return buffer;
    }

    @Override
    public Object deserialize(ByteBuffer data){
        data.get(); // Consume a single byte
        return null;
    }

    @Override
    public int sizeOf(Object object){
        return 1; // We need a dummy number
    }

    @Override
    public Class<?> getType(){
        return Object.class;
    }

}
