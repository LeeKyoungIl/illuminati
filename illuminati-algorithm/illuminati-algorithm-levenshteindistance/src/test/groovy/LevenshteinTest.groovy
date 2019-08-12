import me.phoboslabs.illuminati.algorithm.illuminatialgorithmlevenshteindistance.Levenshtein
import spock.lang.Specification

class LevenshteinTest extends Specification {

    def "Levenshtein test"() {
        setup:
            final String targetStr = "microSoft";
            final String compareStr = "microsoFt";

        when:
            int result;
            long beforeTime = System.currentTimeMillis();
            for (int i=0; i<=100000; i++) {
                result = Levenshtein.getInstance().distance(targetStr, compareStr, true);
            }
            long afterTime = System.currentTimeMillis();
            long secDiffTime = (afterTime - beforeTime);
            println("result (ms) : "+secDiffTime);

        then:
            result == 2;
    }
}
