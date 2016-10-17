package ai.seitok.natsuba.cereal.def;

import ai.seitok.natsuba.cereal.Bowl;
import ai.seitok.natsuba.cereal.BoxingService;
import ai.seitok.natsuba.cereal.BoxingServiceFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBoxingService<T> implements BoxingService<T> {

    private final Class<T> type;
    private final Constructor<T> constructor;
    private Map<Field, BoxingService<?>> serializableFields;

    public DefaultBoxingService(Class<T> serializing){
        Constructor<T> _constructor = null;
        try {
            if(!serializing.isAnnotationPresent(Bowl.class)){
                throw new IllegalStateException(serializing + " was not marked as a Bowl");
            }

            _constructor = serializing.getConstructor();
            serializableFields = new ConcurrentHashMap<>();

            for(Field field : serializing.getDeclaredFields()){
                if(Modifier.isTransient(field.getModifiers())){
                    continue;
                }
                serializableFields.put(field, BoxingServiceFactory.getService(field.getType()));
            }
        } catch (NoSuchMethodException e){
            e.printStackTrace();
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
        serializableFields.forEach((field, service) -> buffer.put(((BoxingService)service).serialize(get(object, field))));
        buffer.flip();
        return buffer;
    }

    @Override
    public T deserialize(ByteBuffer data){
        if(constructor == null)
            return null;

        final T ret;
        try {
            constructor.setAccessible(true);
            ret = constructor.newInstance();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e){
            throw new RuntimeException(e);
        }

        serializableFields.forEach((field, service) -> set(ret, field, service.deserialize(data)));
        return ret;
    }

    @Override
    @SuppressWarnings("unchecked") // todo: work around the need for a SuppressWarning.
    public int sizeOf(T object){
        return serializableFields
                .entrySet()
                .stream()
                .mapToInt(
                        entry -> ((BoxingService)entry.getValue()).sizeOf(get(object, entry.getKey()))
                )
                .sum();
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
            System.out.println("Failed to set field for deserialization");
            e.printStackTrace();
        }
    }

}
