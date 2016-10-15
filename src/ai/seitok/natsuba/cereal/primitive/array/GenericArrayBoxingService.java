package ai.seitok.natsuba.cereal.primitive.array;

import ai.seitok.natsuba.cereal.BoxingService;
import ai.seitok.natsuba.cereal.BoxingServiceFactory;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;

public class GenericArrayBoxingService<T> implements BoxingService<T[]> {

    private static final BoxingService<String> STRING_BOXING_SERVICE = BoxingServiceFactory.getService(String.class);
    private BoxingService<T> elementService;
    private Class<T> elementType;

    public GenericArrayBoxingService(BoxingService<T> elementBoxer){
        this.elementType = elementBoxer.getType();
        this.elementService = elementBoxer;
    }

    @Override
    public ByteBuffer serialize(T[] object){
        ByteBuffer buffer = ByteBuffer.allocate(sizeOf(object));
        ByteBuffer elemBuf;
        buffer.put((ByteBuffer)STRING_BOXING_SERVICE.serialize(elementType.getName()).flip());
        buffer.putInt(object.length);
        for(T elem : object){
            elemBuf = elementService.serialize(elem);
            buffer.put(elemBuf);
        }
        buffer.flip();
        return buffer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T[] unserialize(ByteBuffer data){
        Class<T> componentType;

        try {
            componentType = (Class<T>)Class.forName(STRING_BOXING_SERVICE.unserialize(data));
        } catch (ClassNotFoundException e){
            e.printStackTrace();
            return null;
        }

        T[] array = (T[])Array.newInstance(componentType, data.getInt());
        for(int i=0; i < array.length; i++){
            array[i] = elementService.unserialize(data);
        }
        return array;
    }

    @Override
    public int sizeOf(T[] object){
        int size = 0;
        for(T elem : object){
            size += elementService.sizeOf(elem);
        }
        return Integer.BYTES + STRING_BOXING_SERVICE.sizeOf(elementType.getName()) + size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<T[]> getType(){
        return getGenericArray();
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T[]> getGenericArray(T... elem){
        return (Class<T[]>)elem.getClass();
    }

}
