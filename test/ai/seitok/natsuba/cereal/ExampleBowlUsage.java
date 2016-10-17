package ai.seitok.natsuba.cereal;

import java.nio.ByteBuffer;

public class ExampleBowlUsage {

    public static void main(String[] args){
        // Create a new object for dummy testing.
        ExampleBowl bowl = new ExampleBowl();
        System.out.println(bowl);

        // Grab a service (this will default to a new instance of DefaultBoxingService)
        BoxingService<ExampleBowl> bowlService = BoxingServiceFactory.getService(ExampleBowl.class);
        // Serialize our bowl into bytes
        ByteBuffer bowlAsBytes = bowlService.serialize(bowl);
//        System.out.println(Arrays.toString(BoxingServiceFactory.getService(String.class).serialize(bowl.aString).array()));

        /*
         * Here we can do a bunch of things to bowlAsBytes such as:
         *      - Network transportation
         *      - Store bytes onto disk as persistent data
         *      - Toy with the data inside to discover exploits/flaws.
         *
         * However, for now, we'll just stick to a simple in-memory deserialization.
         */

        // Deserialize the object.
        ExampleBowl theSameBowl = bowlService.deserialize(bowlAsBytes);
        System.out.println(theSameBowl);

        // Now check the sample bowl and validate it matches the original.
        assert bowl.equals(theSameBowl);
    }

}
