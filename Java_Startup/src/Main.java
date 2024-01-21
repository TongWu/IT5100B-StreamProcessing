import java.util.OptionalInt;
import java.util.stream.IntStream;

class Main {
    public static void main(String[] args) {
        // Example map reducer
        OptionalInt sumSquares =
                IntStream.rangeClosed(1, 10) /* Generate number from 1 to 10 */
                        .map(x -> x * x) /* Map x to square of x */
                        .filter(x -> x % 2 == 0) /* filter out odd number */
                        .reduce(Integer::sum); /* Sum all numbers together */
        // Check the sumSquares availability first
        if (sumSquares.isPresent()) {
            // Then get the answer from stream
            int temp = sumSquares.getAsInt();
            System.out.println(temp);
        }
    }
}
