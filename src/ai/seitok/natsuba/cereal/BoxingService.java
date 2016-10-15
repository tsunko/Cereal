package ai.seitok.natsuba.cereal;

import java.nio.ByteBuffer;

/**
 * A serialization service featuring very basic methods
 * @param <T>
 */
public interface BoxingService<T> {

    public ByteBuffer serialize(T object);

    public T deserialize(ByteBuffer data);

    public int sizeOf(T object);

    public Class<T> getType();

}
