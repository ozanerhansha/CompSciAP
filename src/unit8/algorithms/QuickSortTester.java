package unit8.algorithms;
/**
   This program tests the quick sort algorithm.
*/
public class QuickSortTester
{  
   public static void main(String[] args)
   {  
      int[] a = ArrayUtil.randomIntArray(20, 100);
      ArrayUtil.print(a);
      QuickSorter sorter = new QuickSorter(a);
      sorter.sort();
      ArrayUtil.print(a);
   }
}

