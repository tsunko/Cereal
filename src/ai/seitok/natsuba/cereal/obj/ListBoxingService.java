package ai.seitok.natsuba.cereal.obj;

import ai.seitok.natsuba.cereal.BoxingService;
import ai.seitok.natsuba.cereal.BoxingServiceFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ListBoxingService<T> implements BoxingService<List<T>> {

    private static final BoxingService<String> STRING_SERVICE = BoxingServiceFactory.getService(String.class);

    @Override
    public ByteBuffer serialize(List<T> list){
        ByteBuffer buf = ByteBuffer.allocate(sizeOf(list));
        boolean diverse = isDiverse(list);
        buf.putInt(list.size());
        buf.put((byte)(diverse ? 1 : 0));

        BoxingService service = null;
        if(diverse){
            for(T elem : list){
                service = BoxingServiceFactory.getService(elem.getClass());
                buf.put(STRING_SERVICE.serialize(elem.getClass().getName()));
                buf.put(service.serialize(elem));
            }
        } else {
            for(T elem : list){
                if(service == null){
                    service = BoxingServiceFactory.getService(elem.getClass());
                    buf.put(STRING_SERVICE.serialize(elem.getClass().getName()));
                }
                buf.put(service.serialize(elem));
            }
        }

        buf.flip();
        return buf;
    }

    @Override
    public List<T> deserialize(ByteBuffer data){
        int len = data.getInt();
        boolean isDiverseList = data.get() == 1;
        List<T> list = new ArrayList<>();
        BoxingService service;

        try {
            if(isDiverseList){
                while(len-- > 0){
                    service = BoxingServiceFactory.getService(Class.forName(STRING_SERVICE.deserialize(data)));
                    list.add((T)service.deserialize(data));
                }
            } else {
                service = BoxingServiceFactory.getService(Class.forName(STRING_SERVICE.deserialize(data)));
                while(len-- > 0){
                    list.add((T)service.deserialize(data));
                }
            }
        } catch (ClassNotFoundException e){
            // log error
        }

        return list;
    }

    @Override
    public int sizeOf(List<T> list){
        boolean diverse = isDiverse(list);
        int size = Integer.BYTES;
        size += 1; // allow single byte to indicate if dynamic or not
        if(list.size() < 0) return size;

        BoxingService service;
        if(diverse){
            for(Object elem : list){
                service = BoxingServiceFactory.getService(elem.getClass());
                size += STRING_SERVICE.sizeOf(elem.getClass().getName());
                size += service.sizeOf(elem);
            }
        } else {
            size += STRING_SERVICE.sizeOf(list.get(0).getClass().getName());
            for(Object elem : list){
                service = BoxingServiceFactory.getService(elem.getClass());
                size += service.sizeOf(elem);
            }
        }
        return size;
    }

    @Override
    public Class<List<T>> getType(){
        Class<?> klass = new ArrayList<T>().getClass();
        return (Class<List<T>>)klass;
    }

    private boolean isDiverse(List<?> list){
        Class<?> lastClass = null;
        for(Object o : list){
            if(o == null) continue;

            if(lastClass == null){
                lastClass = o.getClass();
                continue;
            }

            if(o.getClass() != lastClass){
                return true;
            }
        }
        return false;
    }


}
