package com.accelerator;

import org.junit.Test;

public class DefaultTest {

    @Test
    public void switchTest() throws Exception {
        int a = 1;
        switch (a) {
            case 1:
                System.out.println("1");
            default:
                System.out.println("default");
        }

    }
}
