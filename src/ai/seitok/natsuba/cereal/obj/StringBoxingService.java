package ai.seitok.natsuba.cereal.obj;

import ai.seitok.natsuba.cereal.BoxingService;

import java.nio.ByteBuffer;

public class StringBoxingService implements BoxingService<String> {

    @Override
    public ByteBuffer serialize(String string){
        ByteBuffer buf = ByteBuffer.allocate(sizeOf(string));
        buf.putInt(string.length());
        for(char c : string.toCharArray()){
            buf.putChar(c);
        }
        buf.flip();
        return buf;
    }

    @Override
    public String deserialize(ByteBuffer data){
        int len = data.getInt();
        char[] chars = new char[len];
        for(int i=0; i < chars.length; i++){
            chars[i] = data.getChar();
        }
        return new String(chars);
    }

    @Override
    public int sizeOf(String string){
        return Integer.BYTES + (Character.BYTES * string.length());
    }

    @Override
    public Class<String> getType(){
        return String.class;
    }

}
