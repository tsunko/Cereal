package ai.seitok.natsuba.cereal;

import java.util.Random;

@Bowl
public class ExampleBowl {

    public int aNumber = 4242564;
    public String aString = "The quick brown fox jumped over the lazy dog.";
//    public EmbeddedBowl aBowlInABowl = new EmbeddedBowl();

    // I'm probably going to get hate for this, but let it be known that this is just a test class.
    // Obviously, in production, we must always create objects that are thrown away the next GC cycle. /s
    public transient int notSerialized = (new Random()).nextInt();

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof ExampleBowl)){
            return false;
        }

        ExampleBowl other = (ExampleBowl)obj;
        return aNumber == other.aNumber;// &&
//               aString.equals(other.aString); //&&
//               aBowlInABowl.equals(other.aBowlInABowl);
    }

    @Bowl
    public static class EmbeddedBowl {

        public String embeddedString = Integer.toString((new Random()).nextInt(), 16);

        @Override
        public boolean equals(Object obj){
            return obj instanceof EmbeddedBowl && embeddedString.equals(((EmbeddedBowl)obj).embeddedString);
        }

    }

}
