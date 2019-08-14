package me.phoboslabs.illuminati.util.levenshteindistance;

public class Levenshtein {
    private static final Levenshtein INSTANCE = new Levenshtein();

    public static Levenshtein getInstance() {
        return INSTANCE;
    }

    /**
     * GNU General Public License v3.0
     *
     * The Levenshtein Algorithm compare A and B string.
     * Returns 0(zero) If both are equal and Integer value if both are different.
     * The Integer value is the number of times you need to modify the two values to ve the equal.
     * Of course, the smaller the value is similar.
     * Additionally, Add flag for case sensitive identify.
     *
     * @param targetStr
     * @param compareStr
     * @param ignoreCase : case sensitive identify flag
     * @return the number of times you need to modify the two values
     *
     *  example)
     *   targetStr = "leekyoungil"
     *   compareStr = "leekyoungIl"
     *     - result = 1
     *
     *   if ignoreCase = true.
     *   targetStr = "leekyoungil"
     *   compareStr = "leekyoungIl"
     *     - result = 0
     *
     *   targetStr = "Levenshtein Algorithm"
     *   compareStr = "Levonshtein AIgorithm""
     *     - result = 2
     *
     *   targetStr = "Levenshtein Algorithm"
     *   compareStr = "Levenshtein Algorithm""
     *     - result = 0
     *
     *   if an error occurs will return 9999.
     */
    public int distance(CompareString targetStr, CompareString compareStr) {
        return this.distance(targetStr, compareStr, false);
    }
    public int distance(CompareString targetStr, CompareString compareStr, final boolean ignoreCase) {
        if (targetStr == null || compareStr == null) {
            throw new IllegalArgumentException("targetStr or compareStr must not be null.");
        }

        char[] targetStrCharArray = new char[targetStr.length()+1];
        targetStrCharArray[0] = ' ';
        System.arraycopy(ignoreCase ? targetStr.toCharArrayByIgnoreCase() : targetStr.toCharArray(), 0, targetStrCharArray, 1, targetStr.length());
        char[] compareStrCharArray = new char[compareStr.length()+1];
        compareStrCharArray[0] = ' ';
        System.arraycopy(ignoreCase ? compareStr.toCharArrayByIgnoreCase() : compareStr.toCharArray(), 0, compareStrCharArray, 1, compareStr.length());

        final int targetStrCharArrayLength = targetStrCharArray.length;
        final int compareStrCharArrayLength = compareStrCharArray.length;

        int[][] distanceArray = new int[targetStrCharArrayLength][compareStrCharArrayLength];

        int loopCnt = (targetStrCharArrayLength > compareStrCharArrayLength
                        ? targetStrCharArrayLength : compareStrCharArrayLength) + 1;

        int firstLoopCnt = 0;
        int secondLoopCnt;
        for (secondLoopCnt=0; secondLoopCnt<loopCnt; secondLoopCnt++) {
            if (this.indexInBound(distanceArray[firstLoopCnt], secondLoopCnt) == false) {
                firstLoopCnt++;
                secondLoopCnt = -1;
                continue;
            }
            if (targetStrCharArray[firstLoopCnt] == compareStrCharArray[secondLoopCnt]) {
                distanceArray[firstLoopCnt][secondLoopCnt] = (firstLoopCnt == 0)
                                                             ? 0 : distanceArray[firstLoopCnt-1][secondLoopCnt-1];
            } else {
                if (firstLoopCnt == 0 || secondLoopCnt == 0) {
                    distanceArray[firstLoopCnt][secondLoopCnt] = (firstLoopCnt == 0)
                                                                 ? secondLoopCnt : firstLoopCnt;
                } else {
                    final int left = distanceArray[firstLoopCnt-1][secondLoopCnt];
                    final int top = distanceArray[firstLoopCnt][secondLoopCnt-1];
                    final int diagonal = distanceArray[firstLoopCnt-1][secondLoopCnt-1];

                    distanceArray[firstLoopCnt][secondLoopCnt] = this.getSmallerOne(left, top, diagonal)+1;
                }
            }
            if (firstLoopCnt == distanceArray.length - 1
                    && secondLoopCnt == distanceArray[firstLoopCnt].length - 1) {
                break;
            }
        }
        return distanceArray[firstLoopCnt][secondLoopCnt];
    }

    private int getSmallerOne(final int left, final int top, final int diagonal) {
        if (left > top) {
            return top > diagonal ? diagonal : top;
        }
        return (left > diagonal) ? diagonal : left;
    }

    private boolean indexInBound(int[] data, final int index){
        return data != null && index >= 0 && index < data.length;
    }
}
