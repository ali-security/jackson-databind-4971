package tools.jackson.databind.testutil;

import java.nio.charset.StandardCharsets;
import java.util.*;

import tools.jackson.databind.*;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.type.TypeFactory;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Class containing test utility methods.
 * The methods are migrated from {@code BaseMapTest} and {@code BaseTest},
 * as part of JUnit 5 migration.
 */
public class DatabindTestUtil
{
   /*
    /**********************************************************
    /* Shared helper classes
    /**********************************************************
     */

    public static enum ABC { A, B, C; }

    public static class Point {
        public int x, y;

        protected Point() { } // for deser
        public Point(int x0, int y0) {
            x = x0;
            y = y0;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point)) {
                return false;
            }
            Point other = (Point) o;
            return (other.x == x) && (other.y == y);
        }

        @Override
        public String toString() {
            return String.format("[x=%d, y=%d]", x, y);
        }
    }

    /*
    /**********************************************************
    /* Factory methods
    /**********************************************************
     */

    private static ObjectMapper SHARED_MAPPER;

    public static ObjectMapper sharedMapper() {
        if (SHARED_MAPPER == null) {
            SHARED_MAPPER = newJsonMapper();
        }
        return SHARED_MAPPER;
    }

    public static TypeFactory newTypeFactory() {
        // this is a work-around; no null modifier added
        return TypeFactory.defaultInstance().withModifier(null);
    }

    /*
    /**********************************************************
    /* Mapper construction helpers
    /**********************************************************
     */

    public static ObjectMapper newJsonMapper() {
        return new JsonMapper();
    }

    public static JsonMapper.Builder jsonMapperBuilder() {
        return JsonMapper.builder();
    }

    /*
    /**********************************************************
    /* Helper methods, serialization
    /**********************************************************
     */

    @SuppressWarnings("unchecked")
    public static Map<String,Object> writeAndMap(ObjectMapper m, Object value)
    {
        String str = m.writeValueAsString(value);
        return (Map<String,Object>) m.readValue(str, LinkedHashMap.class);
    }

    /*
    /**********************************************************
    /* Encoding or String representations
    /**********************************************************
     */

    public static String a2q(String json) {
        return json.replace("'", "\"");
    }

    public static String q(String str) {
        return '"'+str+'"';
    }

    public static byte[] utf8Bytes(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    /*
    /**********************************************************
    /* Additional assertion methods
    /**********************************************************
     */

    public static void verifyException(Exception e, Class<?> expType, String expMsg)
        throws Exception
    {
        if (e.getClass() != expType) {
            fail("Expected exception of type "+expType.getName()+", got "+e.getClass().getName());
        }
        if (expMsg != null) {
            verifyException(e, expMsg);
        }
    }

    /**
     * @param e Exception to check
     * @param anyMatches Array of Strings of which AT LEAST ONE ("any") has to be included
     *    in {@code e.getMessage()} -- using case-INSENSITIVE comparison
     */
    public static void verifyException(Throwable e, String... anyMatches)
    {
        String msg = e.getMessage();
        String lmsg = (msg == null) ? "" : msg.toLowerCase();
        for (String match : anyMatches) {
            String lmatch = match.toLowerCase();
            if (lmsg.contains(lmatch)) {
                return;
            }
        }
        fail("Expected an exception with one of substrings ("
            + Arrays.asList(anyMatches)+"): got one (of type "+e.getClass().getName()
            +") with message \""+msg+"\"");
    }
}