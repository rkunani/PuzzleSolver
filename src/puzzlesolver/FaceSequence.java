package puzzlesolver;

import java.util.ArrayList;
import java.util.List;

public class FaceSequence {
    private String[] arr;

    public FaceSequence(String first, String second, String third, String fourth) {
        arr = new String[4];
        arr[0] = first;
        arr[1] = second;
        arr[2] = third;
        arr[3] = fourth;
    }

    /* Returns the face after oldFace when rotating in the given DIRECTION */
    public String nextFace(String oldFace, String direction) {
        int arrIndex = getArrIndex(oldFace);
        int nextIndex;
        if (direction.equals("cw")) {
            nextIndex = plusOne(arrIndex);
        } else {
            nextIndex = minusOne(arrIndex);
        }
        return arr[nextIndex];
    }

    /* Converts the FaceSequence into a List */
    public List<String> toList() {
        List<String> faceList = new ArrayList<>();
        for (int i = 0; i < arr.length; i += 1) {
            faceList.add(arr[i]);
        }
        return faceList;
    }

    private int getArrIndex(String oldFace) {
        for (int i = 0; i < arr.length; i += 1) {
            if (arr[i].equals(oldFace)) {
                return i;
            }
        }
        return -1;
    }

    private int plusOne(int index) {
        return (index + 1) % arr.length; // arbitrarily chose edgeArr.length (both have same length)
    }

    private int minusOne(int index) {
        if (index - 1 >= 0) {
            return index - 1;
        }
        return (index - 1) + arr.length;
    }
}
