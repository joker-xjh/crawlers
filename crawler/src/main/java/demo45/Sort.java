package demo45;


public class Sort {
	
	private static void swap(int[] array, int i, int j) {
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
	
	public static void quickSort1(int[] array, int left, int right) {
		if(left < right) {
			int pivot = array[left];
			int i = left+1, j = right;
			while(i <= j) {
				while(i<=j && array[i] <= pivot)
					i++;
				while(i<=j && array[j] > pivot)
					j--;
				if(i<=j) {
					swap(array, i, j);
					i++;
					j--;
				}
			}
			swap(array, left, j);
			quickSort1(array, left, j-1);
			quickSort1(array, j+1, right);
		}
	}
	
	public static void quickSort2(int[] array, int left, int right) {
		if(left < right) {
			int pivot = array[left];
			int i = left, j = right;
			while(i < j) {
				while(i<j && array[j] > pivot)
					j--;
				array[i] = array[j];
				while(i<j && array[i]<=pivot)
					i++;
				array[j] = array[i];
			}
			array[i] = pivot;
			quickSort2(array, left, i-1);
			quickSort2(array, i+1, right);
		}
	}
	
	
	public static void quickSort3(int[] array, int left, int right) {
		if(left < right) {
			int pivot = array[left];
			int i = left, j = left + 1;
			while(j <= right) {
				if(array[j] <= pivot) {
					i++;
					swap(array, i, j);
					j++;
				}
				else {
					j++;
				}
			}
			swap(array, left, i);
			quickSort3(array, left, i-1);
			quickSort3(array, i+1, right);
		}
	}
	
	
	public static void quickSort3Way(int[] array, int left, int right) {
		if(left < right) {
			int pivot = array[left];
			int i = left, k = array[left+1], j = right;
			while(k <= j) {
				if(array[k] < pivot)
					swap(array, i++, k++);
				else if(array[k] > pivot)
					swap(array, j--, k);
				else
					k++;
			}
			quickSort3Way(array, left, i-1);
			quickSort3Way(array, j+1, right);
		}
	}
	
	public static void QuickSortDualPivot(int[] array, int left, int right) {
		if(right <= left)
			return;
		if(array[left] > array[right])
			swap(array, left, right);
		int pivot1 = array[left], pivot2 = array[right];
		int i = left, j = right, k =left+1;
		
		loop:
			while(k < j) {
				if(array[k] < pivot1) {
					i++;
					swap(array, i, k);
					k++;
				}
				else if(array[k] >= pivot1 && array[k] <= pivot2)
					k++;
				else {
					while(array[--j] > pivot2) {
						if(j <= k)
							break loop;
					}
					if(array[j] >= pivot1 && array[j] <= pivot2) {
						swap(array, k, j);
						k++;
					}
					else {
						i++;
						swap(array, i, j);
						swap(array, j, k);
						k++;
					}
				}
			}
		
		swap(array, left, i);
		swap(array, right, j);
		QuickSortDualPivot(array, left, i-1);
		QuickSortDualPivot(array, i+1, j-1);
		QuickSortDualPivot(array, j+1, right);
	}
	
	
	
	
	
	
	
	

}
