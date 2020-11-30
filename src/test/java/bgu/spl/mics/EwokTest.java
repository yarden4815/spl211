package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Ewok;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EwokTest {

    Ewok ewok;

    @BeforeEach
    void setUp() {
        ewok = new Ewok(1);
    }



    // also testing release.
    @Test
    void acquire() {
        ewok.acquire();
        assertFalse(ewok.isAvailable());
        ewok.release();
        assertTrue(ewok.isAvailable());
    }

}

