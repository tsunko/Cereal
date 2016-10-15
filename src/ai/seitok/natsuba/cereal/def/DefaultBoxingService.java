package ai.seitok.natsuba.cereal.def;

import ai.seitok.natsuba.cereal.Bowl;
import ai.seitok.natsuba.cereal.BoxingService;
import ai.seitok.natsuba.cereal.BoxingServiceFactory;
import ai.seitok.natsuba.cereal.Grain;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DefaultBoxingService<T> implements BoxingService<T> {

    private final Class<T> type;
    private final Constructor<T> constructor;
    private Map<Field, BoxingService<?>> serializableFields;

    public DefaultBoxingService(Class<T> serializing){
        Constructor<T> _constructor = null;
        try {
            if(!serializing.isAnnotationPresent(Bowl.class)){
                throw new IllegalStateException("type was not marked as a Bowl");
            }

            _constructor = serializing.getConstructor();
            serializableFields = new ConcurrentHashMap<>();

            for(Field field : serializing.getDeclaredFields()){
                if(!field.isAnnotationPresent(Grain.class)) continue;
                serializableFields.put(field, BoxingServiceFactory.getService(field.getType()));
            }
        } catch (NoSuchMethodException e){
            System.out.println("Constructor not found. Does " + serializing.getName() + " have a no-arg constructor?");
        } finally {
            constructor = _constructor;
            type = serializing;
        }
    }

    @Override
    @SuppressWarnings("unchecked") // todo: work around the need for a SuppressWarning.
    public ByteBuffer serialize(T object){
        ByteBuffer buffer = ByteBuffer.allocate(sizeOf(object));
        // read sizeOf's comment as to why <?> is not used
        serializableFields.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                v -> (BoxingService)v.getValue()
        )).forEach((field, service) -> {
            ByteBuffer elemBuf = service.serialize(get(object, field));
            buffer.put(elemBuf);
        });
        buffer.flip();
        return buffer;
    }

    @Override
    public T unserialize(ByteBuffer data){
        if(constructor == null)
            return null;

        final T ret;
        try {
            constructor.setAccessible(true);
            ret = constructor.newInstance();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e){
            throw new RuntimeException(e);
        }

        serializableFields.forEach((field, service) -> set(ret, field, service.unserialize(data)));
        return ret;
    }

    @Override
    @SuppressWarnings("unchecked") // todo: work around the need for a SuppressWarning.
    public int sizeOf(T object){
        int size = 0;
        int valSize;

        // we should technically have <?> after BoxingService
        // but we have to erase the generic in order to actually perform
        // service.sizeOf(fieldVal) as BoxingService.sizeOf(Object)
        // otherwise, it's invoked as BoxingService<?>.sizeOf(?)
        // in which can't be called because there isn't a type named "?".
        serializableFields
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    item -> (BoxingService)item.getValue()
                )).entrySet()
                .stream()
                .mapToInt(entry -> entry.getValue().sizeOf(get(object, entry.getKey())))
                .sum();

        BoxingService service;
        for(Map.Entry<Field, BoxingService<?>> entry : serializableFields.entrySet()){
            service = entry.getValue();
            valSize = service.sizeOf(get(object, entry.getKey()));
            size += valSize;
        }

        return size;
    }

    @Override
    public Class<T> getType(){
        return type;
    }

    private Object get(T obj, Field field){
        try {
            return field.get(obj);
        } catch (ReflectiveOperationException e){
            System.out.println("Failed to fetch field for serialization");
            e.printStackTrace();
            return null;
        }
    }

    private void set(T obj, Field field, Object value){
        try {
            field.set(obj, value);
        } catch (ReflectiveOperationException e){
            System.out.println("Failed to set field for serialization");
        }
    }

}
