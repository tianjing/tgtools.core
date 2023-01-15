package tgtools.util.id;

public class SnowflakeIdFactory {

    static SnowFlake snowFlake;
    static long datacenterId = 0;
    static long machineId = 0;

    static {
        snowFlake = new SnowFlake(0, 0);
    }

    public static SnowFlake getSnowFlake() {
        return snowFlake;
    }

    public static long nextId() {
        return getSnowFlake().nextId();
    }

    public static String nextIdStr() {
        return String.valueOf(nextId());
    }


}
