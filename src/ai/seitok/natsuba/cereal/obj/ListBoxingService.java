package ai.seitok.natsuba.cereal.obj;

import ai.seitok.natsuba.cereal.BoxingService;
import ai.seitok.natsuba.cereal.BoxingServiceFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.List;

public class ListBoxingService<T> implements BoxingService<List<T>> {

    private Constructor<?> listConstructor;
    private boolean hasSizeConstructor;
    private BoxingService<T> elementBoxer;

    public ListBoxingService(Class<?> listClass, Class<T> listType){
        try {
            listConstructor = listClass.getConstructor(int.class);
            hasSizeConstructor = true;
        } catch (NoSuchMethodException e1){
            try {
                listConstructor = listClass.getConstructor();
                hasSizeConstructor = false;
            } catch (NoSuchMethodException e2){
                System.out.println("no int-constructor nor default constructor found for list class " + listClass.getName());
            }
        }

        elementBoxer = BoxingServiceFactory.getService(listType);
    }

    @Override
    public ByteBuffer serialize(List<T> list){
        ByteBuffer buf = ByteBuffer.allocate(sizeOf(list));
        buf.putInt(list.size());

        for(T elem : list){
            ByteBuffer elemBuf = elementBoxer.serialize(elem);
            buf.put(elemBuf);
        }

        buf.flip();
        return buf;
    }

    @Override
    public List<T> unserialize(ByteBuffer data){
        int len = data.getInt();
        List<T> list;

        try {
            if(hasSizeConstructor){
                list = (List<T>)listConstructor.newInstance(len);
            } else {
                list = (List<T>)listConstructor.newInstance();
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e){
            throw new RuntimeException(e); // throw it up more
        }

        while(len-- > 0){
            list.add(elementBoxer.unserialize(data));
        }

        return list;
    }

    @Override
    public int sizeOf(List<T> list){
        int size = Integer.BYTES;
        for(T elem : list){
            size += elementBoxer.sizeOf(elem);
        }
        return size;
    }

    @Override
    public Class<List<T>> getType(){
        return (Class<List<T>>)listConstructor.getDeclaringClass();
    }

}
