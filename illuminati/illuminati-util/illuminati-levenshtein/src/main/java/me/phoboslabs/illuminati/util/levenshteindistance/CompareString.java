package me.phoboslabs.illuminati.util.levenshteindistance;

import java.util.Arrays;

public class CompareString implements CharSequence {

    private final char[] stringCharArray;

    public CompareString(char[] stringCharArray) {
        if (stringCharArray == null) {
            throw new IllegalArgumentException("The stringCharArray must not be null.");
        }
        this.stringCharArray = stringCharArray;
    }

    @Override
    public int length() {
        return this.stringCharArray.length;
    }

    @Override
    public char charAt(int index) {
        return this.stringCharArray[index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        Arrays.fill(this.stringCharArray, '\u0000');
    }

    public char[] toCharArray() {
        return this.stringCharArray;
    }

    public char[] toCharArrayByIgnoreCase() {
        char[] ignoreCaseArray = new char[this.length()];
        for (int i=0; i<this.length(); i++) {
            ignoreCaseArray[i] = Character.toLowerCase(this.stringCharArray[i]);
        }
        return ignoreCaseArray;
    }
}
