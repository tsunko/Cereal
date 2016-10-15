package ai.seitok.natsuba.cereal;

import ai.seitok.natsuba.cereal.def.DefaultBoxingService;
import ai.seitok.natsuba.cereal.obj.StringBoxingService;
import ai.seitok.natsuba.cereal.primitive.*;
import ai.seitok.natsuba.cereal.primitive.array.GenericArrayBoxingService;

import java.util.Map;

public final class BoxingServiceFactory {

    private BoxingServiceFactory(){}

    private static final Map<Class<?>, BoxingService<?>> SERVICES = new NoDupeHashMap<>();

    static {
        BoxingService service;

        service = new BooleanBoxingService();
        SERVICES.put(Boolean.class, service);
        SERVICES.put(boolean.class, service);

        service = new ByteBoxingService();
        SERVICES.put(Byte.class, service);
        SERVICES.put(byte.class, service);

        service = new ShortBoxingService();
        SERVICES.put(Short.class, service);
        SERVICES.put(short.class, service);

        service = new IntBoxingService();
        SERVICES.put(Integer.class, service);
        SERVICES.put(int.class, service);

        service = new FloatBoxingService();
        SERVICES.put(Float.class, service);
        SERVICES.put(float.class, service);

        service = new LongBoxingService();
        SERVICES.put(Long.class, service);
        SERVICES.put(long.class, service);

        service = new DoubleBoxingService();
        SERVICES.put(Double.class, service);
        SERVICES.put(double.class, service);

        service = new StringBoxingService();
        SERVICES.put(String.class, service);
    }

    @SuppressWarnings("unchecked")
    public static <P> BoxingService<P> getService(Class<P> klass){
        BoxingService<?> service = SERVICES.get(klass);
        if(service == null){
            service = new DefaultBoxingService<>(klass);
        }
        return (BoxingService<P>)service;
    }

    public static <P> BoxingService<P[]> getArrayService(Class<P> elementClass){
        BoxingService<P> service = getService(elementClass);
        return new GenericArrayBoxingService<>(service);
    }

    public static void registerBoxingService(Class<? extends BoxingService> boxingService){
        try {
            SERVICES.put(boxingService, boxingService.newInstance());
        } catch (InstantiationException e){
            System.out.println("Failed to register " + boxingService + " because we failed to create an instance of it");
            e.printStackTrace();
        } catch (IllegalAccessException e){
            System.out.println("Failed to register " + boxingService + " because we had no access to its constructor");
            e.printStackTrace();
        }
    }

}
