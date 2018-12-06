package net.earthcomputer.bedrockified;

public class BedrockSeed {

    public static long getBedrockSeed(String str, long _defaultLong) {
        int _default = (int) _defaultLong;

        int bedrockSeed;

        if (str.length() < 2)
            bedrockSeed = _default;
        else if (str.charAt(0) == '-' && str.length() == 2)
            bedrockSeed = _default;
        else {
            str = str.trim();
            if (str.isEmpty())
                bedrockSeed = _default;
            else if (str.charAt(0) == '0' || (str.length() > 1 && str.charAt(0) == '-' && str.charAt(1) == '0')) { // bedrock doesn't contain length check but we want to prevent a crash
                bedrockSeed = str.hashCode();
            } else if (!isIntegral(str)) {
                bedrockSeed = str.hashCode();
            } else {
                try {
                    bedrockSeed = Integer.parseInt(str);
                    if (!str.equals("-1") && bedrockSeed == -1)
                        bedrockSeed = _default;
                } catch (NumberFormatException e) {
                    bedrockSeed = _default;
                }
            }
        }

        return Integer.toUnsignedLong(bedrockSeed);
    }

    private static boolean isIntegral(String str) {
        if (str.startsWith("-"))
            str = str.substring(1);
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9')
                return false;
        }
        return true;
    }

}
