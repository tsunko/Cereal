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

    public ListBoxingService(Class<?> listClass){
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
    }

    @Override
    public ByteBuffer serialize(List<T> list){
        ByteBuffer buf = ByteBuffer.allocate(sizeOf(list));
        buf.putInt(list.size());

        BoxingService service;
        ByteBuffer elemBuf;
        for(T elem : list){
            service = BoxingServiceFactory.getService(elem.getClass());
            elemBuf = service.serialize(elem);
            buf.put(elemBuf);
        }

        buf.flip();
        return buf;
    }

    @Override
    public List<T> deserialize(ByteBuffer data){
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
            // TODO: Dynamically figure out objects. Not sure how.
//            list.add(elementBoxer.deserialize(data));
        }

        return list;
    }

    @Override
    public int sizeOf(List<T> list){
        int size = Integer.BYTES;
        BoxingService service;
        for(T elem : list){
            service = BoxingServiceFactory.getService(elem.getClass());
            size += service.sizeOf(elem);
        }
        return size;
    }

    @Override
    public Class<List<T>> getType(){
        return (Class<List<T>>)listConstructor.getDeclaringClass();
    }

}
