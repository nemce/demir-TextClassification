
package demir.tc.classification;
/**
 * An implementation of the heap sort algorithm as described in Cormen et al. 
 * Introduction to Algorithms. This class sorts two arrays, with respect to the
 * values of the elements in the first array. There is also an option to sort
 * only N entries with the highest values. In this case, there are two options:
 * <ul>
 * <li>The top N entries can be sorted in ascending order, so that the 
 * N elements with the highest values are placed in the array's last 
 * N positions (the highest entry of the arrays will be in 
 * array[array.length-1], the second highest in array[array.length-2] and 
 * so on.</li> 
 * <li>The top N entries can be sorted in descending order, so that 
 * the N elements with the highest values are placed in the array's first
 * N positions (the highest entry of the array will be in array[0], the second
 * highest entry will be in array[1] and so on.</li>
 * </ul>
 * 
 * @author Vassilis Plachouras
  */
public class DemirHeapSort {
	/**
	 * Builds a maximum heap.
	 * @param A int[] the array which will be transformed into a heap.
	 * @param B int[] the array which will be transformed into a heap,
	 *		based on the values of the first argument.
	 */
        private static <T> int buildMaxHeap(Double[] A, T[] B) 
        {
		final int heapSize = A.length;
		for (int i = heapSize/2; i > 0; i--)
			maxHeapify(A, B, i, heapSize);
		return heapSize;
	}
	/**
	 * Sorts the given arrays in ascending order, using heap-sort.
	 * @param A double[] the first array to be sorted.
	 * @param B int[] the second array to be sorted, according to the
	 *		values of the first array.
	 */
	public static final <T>  void ascendingHeapSort(Double[] A, T[] B) {
		int heapSize = buildMaxHeap(A, B);

		//temporary variables for swaps
		double tmpDouble;
		T tmpInt;

		for (int i = A.length; i > 0; i--) {
			//swap elements in i-1 with 0
			tmpDouble = A[i - 1];
			A[i - 1] = A[0];
			A[0] = tmpDouble;

			tmpInt = B[i - 1];
			B[i - 1] = B[0];
			B[0] = tmpInt;

			heapSize--;
			maxHeapify(A, B, 1, heapSize);
		}
	}
	/**
	 * Sorts the given arrays in descending order, using heap-sort.
	 * @param A double[] the first array to be sorted.
	 * @param B int[] the second array to be sorted, according to the
	 *		values of the first array.
	 */
	public static final <T>  void descendingHeapSort(Double[] A, T[] B) {
		DemirHeapSort.ascendingHeapSort(A, B);
		reverse(A, B, A.length);
	}
	/**
	 * Sorts the top <tt>topElements</tt> of the given array in
	 * ascending order, using heap sort.
	 * @param A double[] the first array to be sorted.
	 * @param B int[] the second array to be sorted, according to the
	 *		values of the first array.
	 * @param topElements int the number of elements to be sorted.
	 */
	public static final <T>  void ascendingHeapSort(Double[] A, T[] B, int topElements) {
		int heapSize = buildMaxHeap(A, B);
		int end = A.length - topElements;

		//temporary variables for swaps
		double tmpDouble;
		T tmpInt;
		short tmpShort;


		for (int i = A.length; i > end; i--) {
			//swap elements in i-1 with 0
			tmpDouble = A[i - 1];
			A[i - 1] = A[0];
			A[0] = tmpDouble;

			tmpInt = B[i - 1];
			B[i - 1] = B[0];
			B[0] = tmpInt;

			heapSize--;
			maxHeapify(A, B, 1, heapSize);
		}
	}
	/**
	 * Reverses the elements of the two arrays, after they have
	 * been sorted.
	 * @param A double[] the first array.
	 * @param B int[] the second array.
	 * @param topElements int the number of elements to be reversed.
	 */
	private static <T> void reverse(final Double[] A, final T[] B, final int topElements) {
		//reversing the top elements
		final int length = A.length;
		final int elems = //topElements
			topElements > length/2 
			? length/2 
			: topElements;
		//if (elems > A.length/2)
		//	elems = A.length/2;
		int j;
		//temporary swap variables
		double tmpDouble;
		T tmpInt;

		for (int i=0; i<elems; i++) {
			j = length - i - 1;
			//swap elements in i with those in j
			tmpDouble = A[i]; A[i] = A[j]; A[j] = tmpDouble;
			tmpInt = B[i]; B[i] = B[j]; B[j] = tmpInt;
		}
	}
	/**
	 * Sorts the top <tt>topElements</tt> of the given array in
	 * descending order, using heap sort for sorting the values
	 * in ascending order and then reversing the order of a
	 * specified number of elements.
	 * @param A double[] the first array to be sorted.
	 * @param B int[] the second array to be sorted, according to the
	 *		values of the first array.
	 * @param topElements int the number of elements to be sorted.
	 */
	public static final <T>  void descendingHeapSort(final Double[] A, final T[] B, final int topElements) {
		ascendingHeapSort(A, B, topElements);
		reverse(A, B, topElements);
	}
	/**
	 * Maintains the heap property.
	 * @param A int[] The array on which we operate.
	 * @param i int a position in the array. This number is
	 * between 1 and A.length inclusive.
	 */
	private static  <T> void maxHeapify(final Double[] A, final T[] B, final int i, final int heapSize) {
		final int l = 2 * i;
		final int r = 2 * i + 1;

		int largest = 
			(l <= heapSize && A[l - 1] > A[i - 1])
				? l
				: i;
		//if (l <= heapSize && A[l - 1] > A[i - 1])
		//	largest = l;
		//else
		//	largest = i;
		if (r <= heapSize && A[r - 1] > A[largest - 1])
			largest = r;

		//temporary variables for swaps
		double tmpDouble;
		T tmpInt;


		if (largest != i) {
			tmpDouble = A[largest - 1];
			A[largest - 1] = A[i - 1];
			A[i - 1] = tmpDouble;
			tmpInt = B[largest - 1];
			B[largest - 1] = B[i - 1];
			B[i - 1] = tmpInt;
			maxHeapify(A, B, largest, heapSize);
		}
	}
}
