import me.phoboslabs.illuminati.algorithm.illuminatialgorithmlevenshteindistance.Levenshtein
import spock.lang.Specification

class LevenshteinTest extends Specification {

    def "Levenshtein test"(final String targetStr, final String compareStr, final boolean ignoreCase, final int result) {
        expect:
            Levenshtein.getInstance().distance(targetStr, compareStr, ignoreCase) == result
        where:
            targetStr | compareStr | ignoreCase | result
            "leekyoungil" | "LeekyoungIl" | false | 2
            "microSoft" | "microsoFt" | true | 0
            "test" | "treeValue" | true | 7
            "test" | "TreeValue" | false | 8
            "microSoft" | "microsoFt" | false | 2
            "leekyoungil" | "leekyoungil" | false | 0
    }
}
