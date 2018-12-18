package puzzlesolver;

/* Each CubePosition corresponds to a static location on a Rubik's Cube
 * (e.g. the bottom left square on the orange face) */
public class CubePosition {
        String face;
        int position;

        CubePosition(String face, int position) {
            this.face = face;
            this.position = position;
        }

        /* Two CubePositions are equal if they have the same
        FACE and POSITION.
         */
        @Override
        public boolean equals(Object other) {
            if (this.getClass() != other.getClass()) {
                return false;
            }
            CubePosition otherCube = (CubePosition) other;
            boolean faceEqual = this.face.equals(otherCube.face);
            boolean posEqual = this.position == otherCube.position;
            return faceEqual && posEqual;
        }

        /* hashCode defined this way to allow retrieval from cubeMap.
         * Although this may increase collisions, it is not significant
         * due to the small number of items in the HashMap. */
        @Override
        public int hashCode() {
            int stringHash = this.face.hashCode();
            int posHash = this.position;
            return stringHash + posHash;
        }
}
