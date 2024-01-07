package io.violabs.geordi;

import org.junit.jupiter.api.Test;

public class SimpleJavaTests extends UnitSim {

    @Test
    void bareMinimumTest() {
        test(false, slice -> {
            slice.expect(it -> 15);

            slice.whenever(it -> "Hello Universe!".length());
            return null;
        });
    }

    @Test
    void bareMinimumWithSetup() {
        test(false, slice -> {
            slice.given(it -> {
                it.put("greeting", "Hello Universe!");
                return null;
            });

            slice.expect(it -> "Hello Universe!");

            slice.whenever(it -> {
                Greeting greetingGroup = new Greeting(it);
                return greetingGroup.greeting;
            });
            return null;
        });
    }

    class Greeting {
        String greeting;

        Greeting(TestSlice.DynamicProperties properties) {
            this.greeting = (String) properties.get("greeting");
        }
    }
}
