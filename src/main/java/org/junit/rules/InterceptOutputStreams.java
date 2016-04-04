package org.junit.rules;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.EnumMap;
import java.util.EnumSet;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A {@link TestRule} that will intercept content written to {@link System#out} and/or
 * {@link System#err} and collate it for use by the test.
 *
 * <p>Create it as follows, the constructor takes the default streams to intercept for all methods:
 * <pre>
 * {@literal @TestRule public InterceptOutputStreams ios = new InterceptOutputStreams(Streams.OUT);}
 * </pre>
 *
 * <p>The content can be retrieved as follows:
 * <pre>
 *  String out = ios.contents(Streams.OUT);
 *  assertEquals("..expected output..", out);
 * </pre>
 *
 * <p>The default streams to intercept can be overridden per method as follows:
 * <pre>
 * {@literal @Streams(Streams.ERR)}
 * {@literal @Test}
 *  public void interceptSystemErr() {
 *      String err = ios.contents(Streams.ERR);
 *      assertEquals("..expected error output..", err);
 *  }
 * </pre>
 *
 * @since 4.13
 */
public class InterceptOutputStreams implements TestRule {

    /**
     * The streams that can be intercepted.
     */
    public enum Stream {
        OUT {
            @Override
            PrintStream get() {
                return System.out;
            }

            @Override
            void set(PrintStream stream) {
                System.setOut(stream);
            }
        },
        ERR {
            @Override
            PrintStream get() {
                return System.err;
            }

            @Override
            void set(PrintStream stream) {
                System.setErr(stream);
            }
        };

        abstract PrintStream get();

        abstract void set(PrintStream stream);
    }

    /**
     * Specify the streams to intercept for a specific method.
     *
     * <p>If this annotation is not present then the default streams as specified in the rule
     * constructor will be intercepted. If this is present then it will specify the streams to
     * intercept. If {@link #value()} is an empty array then no streams will be intercepted.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Streams {
        Stream[] value() default {};
    }

    /**
     * The default streams to intercept.
     */
    private final EnumSet<Stream> defaultStreams;

    /**
     * The current streams to intercept, null if not in the middle of a test.
     */
    private EnumSet<Stream> currentStreams;

    /**
     * A map from the stream to the information held about that stream.
     */
    private final EnumMap<Stream, State> streams2State;

    /**
     * The streams to intercept.
     */
    public InterceptOutputStreams(Stream... defaultStreams) {
        this.defaultStreams = createEnumSet(defaultStreams);
        streams2State = new EnumMap<Stream, State>(Stream.class);
    }

    private static EnumSet<Stream> createEnumSet(Stream[] streams) {
        return streams.length == 0 ? EnumSet.noneOf(Stream.class) : EnumSet.of(streams[0], streams);
    }

    /**
     * Get the intercepted contents for the stream.
     * @param stream the stream whose contents are required.
     * @return the intercepted contents.
     * @throws IllegalStateException if the stream contents are not being intercepted (in which
     *     case the developer needs to add {@code stream} to the constructor parameters), or if the
     *     test is not actually running at the moment.
     */
    public String contents(Stream stream) {
        if (currentStreams == null) {
            throw new IllegalStateException(
                "Attempting to access stream contents outside the test");
        }

        if (!currentStreams.contains(stream)) {
            EnumSet<Stream> args = currentStreams.clone();
            args.add(stream);
            StringBuilder message = new StringBuilder()
                .append("Not intercepting ").append(stream).append(" output, try:\n");
            if (currentStreams == defaultStreams) {
                message.append("    new ").append(InterceptOutputStreams.class.getSimpleName())
                    .append("(");
                appendCommaSeparated(args, message);
                message.append(")");
            } else {
                message.append("    @Streams({");
                appendCommaSeparated(args, message);
                message.append("})");
            }
            throw new IllegalStateException(message.toString());
        }

        // Guaranteed to be initialized for each stream by this time so no risk of being null.
        State state = streams2State.get(stream);
        return state.contents();
    }

    private void appendCommaSeparated(EnumSet<Stream> args, StringBuilder message) {
        String separator = "";
        for (Stream arg : args) {
            message.append(separator).append("Streams.").append(arg);
            separator = ", ";
        }
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        Streams streamsAnnotation = description.getAnnotation(Streams.class);
        if (streamsAnnotation != null) {
            currentStreams = createEnumSet(streamsAnnotation.value());
        } else {
            currentStreams = defaultStreams;
        }
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                for (Stream stream : currentStreams) {
                    State state = new State(stream);
                    streams2State.put(stream, state);
                }

                try {
                    base.evaluate();
                } finally {
                    for (State state : streams2State.values()) {
                        state.reset();
                    }
                    streams2State.clear();
                    currentStreams = null;
                }
            }
        };
    }

    public boolean intercepting(Stream stream) {
        if (currentStreams == null) {
            throw new IllegalStateException(
                "Attempting to access stream contents outside the test");
        }

        return currentStreams.contains(stream);
    }

    private static class State {
        private final PrintStream original;
        private final ByteArrayOutputStream baos;
        private final Stream stream;

        State(Stream stream) throws IOException {
            this.stream = stream;
            original = stream.get();
            baos = new ByteArrayOutputStream();
            stream.set(new PrintStream(baos, true, "UTF-8"));
        }

        String contents() {
            try {
                return baos.toString("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        void reset() {
            stream.set(original);
        }
    }
}
