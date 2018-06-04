package puzzlesolver;

import java.util.Iterator;

public class ThreeDigitIterator implements Iterator<Integer> {
    private int[] myArr;
    private int index;

    /* An infinite cycle of the integers FIRST, SECOND, and THIRD in that order */
    public ThreeDigitIterator(int first, int second, int third) {
        myArr =  new int[3];
        myArr[0] = first;
        myArr[1] = second;
        myArr[2] = third;
        index = 0;
    }

    public boolean hasNext() {
        return true; // always true because this is an infinite cycle
    }

    public Integer next() {
        int toReturn = myArr[index];
        index = (index + 1) % myArr.length;
        return toReturn;
    }
}
