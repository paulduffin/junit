package org.junit.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.InterceptOutputStreams.Stream;
import org.junit.rules.InterceptOutputStreams.Streams;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.model.Statement;

/**
 * Tests for {@link InterceptOutputStreams}.
 */
@RunWith(JUnit4.class)
public class InterceptOutputStreamsTest {

    @Rule
    public InterceptOutputStreams iosRule = new InterceptOutputStreams(Stream.OUT, Stream.ERR);

    @Before
    public void setUp() {
        System.out.println("Before Tests OUT");
        System.err.println("Before Tests ERR");
    }

    @After
    public void tearDown() {
        System.out.println("After Tests OUT");
        System.err.println("After Tests ERR");
        if (iosRule.intercepting(Stream.OUT)) {
            assertTrue(iosRule.contents(Stream.OUT).endsWith("\nAfter Tests OUT\n"));
        }
        if (iosRule.intercepting(Stream.ERR)) {
            assertTrue(iosRule.contents(Stream.ERR).endsWith("\nAfter Tests ERR\n"));
        }
    }

    @Test
    public void ctor_noStreams() throws Throwable {
        final InterceptOutputStreams ios = new InterceptOutputStreams();

        Statement statement = new Statement() {
            @Override
            public void evaluate() throws Throwable {
                System.out.println("OUT");
                System.err.println("ERR");

                checkNotInterceptingDefault(ios, Stream.OUT);
                checkNotInterceptingDefault(ios, Stream.ERR);
            }
        };

        statement = ios.apply(statement, Description.EMPTY);
        statement.evaluate();
    }

    @Test
    public void contents_accessOutsideTest() throws Throwable {
        final InterceptOutputStreams ios = new InterceptOutputStreams(Stream.OUT, Stream.ERR);

        Statement statement = new Statement() {
            @Override
            public void evaluate() throws Throwable {
                System.out.println("Hello");
                System.err.println("World");
                assertEquals("Hello\n", ios.contents(Stream.OUT));
                assertEquals("World\n", ios.contents(Stream.ERR));

                // Make sure that the outer rule doesn't see the output,
                assertEquals("Before Tests OUT\n", iosRule.contents(Stream.OUT));
                assertEquals("Before Tests ERR\n", iosRule.contents(Stream.ERR));
            }
        };

        try {
            ios.contents(Stream.OUT);
            fail("did not detect attempt to access content from outside test");
        } catch(IllegalStateException e) {
            assertEquals("Attempting to access stream contents outside the test", e.getMessage());
        }

        statement = ios.apply(statement, Description.EMPTY);
        statement.evaluate();

        try {
            ios.contents(Stream.ERR);
            fail("did not detect attempt to access content from outside test");
        } catch(IllegalStateException e) {
            assertEquals("Attempting to access stream contents outside the test", e.getMessage());
        }
    }

    @Test
    public void contents_notIntercepting() throws Throwable {
        final InterceptOutputStreams ios = new InterceptOutputStreams(Stream.OUT);

        Statement statement = new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    ios.contents(Stream.ERR);
                    fail("did not detect attempt to access content from unintercepted stream");
                } catch (IllegalStateException e) {
                    assertEquals("Not intercepting ERR output, try:\n"
                            + "    new " + InterceptOutputStreams.class.getSimpleName()
                            + "(Streams.OUT, Streams.ERR)", e.getMessage());
                }
            }
        };

        statement = ios.apply(statement, Description.EMPTY);
        statement.evaluate();
    }

    @Test
    public void nesting() throws Throwable {
        InterceptOutputStreams ios = new InterceptOutputStreams(Stream.OUT, Stream.ERR);

        Statement statement = new Statement() {
            @Override
            public void evaluate() throws Throwable {
                System.out.println("Inner OUT");
                System.err.println("Inner ERR");
                throw new UnsupportedOperationException();
            }
        };

        statement = ios.apply(statement, Description.EMPTY);
        try {
            System.out.println("Outer before OUT");
            System.err.println("Outer before ERR");
            statement.evaluate();
            fail("did not propagate exception");
        } catch (UnsupportedOperationException e) {
            System.out.println("Outer after OUT");
            System.err.println("Outer after ERR");
        }

        // The Inner OUT and Inner ERR must not appear in here, if they do then it means that the
        // nested rule did not intercept the content.
        assertEquals(""
            + "Before Tests OUT\n"
            + "Outer before OUT\n"
            + "Outer after OUT\n", iosRule.contents(Stream.OUT));
        assertEquals(""
            + "Before Tests ERR\n"
            + "Outer before ERR\n"
            + "Outer after ERR\n", iosRule.contents(Stream.ERR));
    }

    @Test
    @Streams()
    public void perMethodOverride_noStreams() {

        System.out.println("OUT: This must not be intercepted");
        System.err.println("ERR: This must not be intercepted");

        checkNotInterceptingMethod(iosRule, Stream.OUT);
        checkNotInterceptingMethod(iosRule, Stream.ERR);
    }

    private void checkNotInterceptingDefault(InterceptOutputStreams ios, Stream stream) {
        try {
            ios.contents(stream);
            fail("Did not detect that " + stream + " was not being intercepted");
        } catch (IllegalStateException e) {
            assertEquals("Not intercepting " + stream + " output, try:\n"
                + "    new InterceptOutputStreams(Streams." + stream + ")", e.getMessage());
        }
    }

    private void checkNotInterceptingMethod(InterceptOutputStreams ios, Stream stream) {
        try {
            ios.contents(stream);
            fail("Did not detect that " + stream + " was not being intercepted");
        } catch (IllegalStateException e) {
            assertEquals("Not intercepting " + stream + " output, try:\n"
                + "    @Streams({Streams." + stream + "})", e.getMessage());
        }
    }

    private void checkNotInterceptingMethodNeedBoth(InterceptOutputStreams ios, Stream stream) {
        try {
            ios.contents(stream);
            fail("Did not detect that " + stream + " was not being intercepted");
        } catch (IllegalStateException e) {
            assertEquals("Not intercepting " + stream + " output, try:\n"
                    + "    @Streams({Streams." + Stream.OUT + ", Streams." + Stream.ERR + "})",
                e.getMessage());
        }
    }

    @Test
    @Streams(Stream.ERR)
    public void perMethodOverride_ERR() {

        System.out.println("OUT: This must not be intercepted");
        System.err.println("ERR: This must be intercepted");

        checkNotInterceptingMethodNeedBoth(iosRule, Stream.OUT);
        assertEquals("Before Tests ERR\n"
            + "ERR: This must be intercepted\n", iosRule.contents(Stream.ERR));
    }

    @Test
    @Streams(Stream.OUT)
    public void perMethodOverride_OUT() {

        System.out.println("OUT: This must be intercepted");
        System.err.println("ERR: This must not be intercepted");

        assertEquals("Before Tests OUT\n"
            + "OUT: This must be intercepted\n", iosRule.contents(Stream.OUT));
        checkNotInterceptingMethodNeedBoth(iosRule, Stream.ERR);
    }
}
