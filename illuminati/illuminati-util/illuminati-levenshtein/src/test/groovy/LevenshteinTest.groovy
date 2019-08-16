import me.phoboslabs.illuminati.util.levenshteindistance.CompareString
import me.phoboslabs.illuminati.util.levenshteindistance.Levenshtein
import spock.lang.Specification

class LevenshteinTest extends Specification {

    def "Levenshtein test"(final String targetStr, final String compareStr, final boolean ignoreCase, final int result) {
        expect:
            Levenshtein.getInstance().distance(new CompareString(targetStr.toCharArray()), new CompareString(compareStr.toCharArray()), ignoreCase) == result

        where:
            targetStr | compareStr | ignoreCase | result
            "leekyoungil" | "LeekyoungIl" | false | 2
            "microSoft" | "microsoFt" | true | 0
            "test" | "treeValue" | true | 7
            "test" | "TreeValue" | false | 8
            "microSoft" | "microsoFt" | false | 2
            "leekyoungil" | "leekyoungil" | false | 0
            "" | "" | true | 0
            "" | "a" | true | 1
            "aaapppp" | "" | true | 7
            "frog" | "fog" | true | 1
            "fly" | "ant" | true | 3
            "elephant" | "hippo" | true | 7
            "hippo" | "elephant" | true | 7
            "hippo" | "zzzzzzzz" | true | 8
            "hello" | "hallo" | true | 1
    }

    def "Levenshtein IllegalArgumentException 1 test"() {
        setup:
            final String compareStr = "test";

        when:
            Levenshtein.getInstance().distance(null, new CompareString(compareStr.toCharArray()), false)

        then:
            thrown IllegalArgumentException
    }

    def "Levenshtein IllegalArgumentException 2 test"() {
        setup:
            final String targetStr = "TEST";

        when:
            Levenshtein.getInstance().distance(new CompareString(targetStr.toCharArray()), null, false)

        then:
            thrown IllegalArgumentException
    }

    def "Levenshtein IllegalArgumentException 3 test"() {
        when:
            Levenshtein.getInstance().distance(null, null, false)

        then:
            thrown IllegalArgumentException
    }
}
