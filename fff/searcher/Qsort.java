package searcher;

public class Qsort {

	public static Url_power[] quickSort(Url_power[] L) {          
        Qsort(L,1,L.length-1);  
        return L;
    }  
      
    public static void Qsort(Url_power[] L,int low,int high) {  
        int pivot;  
        if(low<high) {  
            //将L[low,high]一分为二,算出枢轴值pivot,该值得位置固定,不用再变化   
            pivot=partition0(L,low,high);  
  
            //对两边的数组分别排序  
            Qsort(L,low,pivot-1);  
            Qsort(L,pivot+1,high);            
        }                 
    }  
      
    //  选择一个枢轴值(关键字) 把它放到某个位置 使其左边的值都比它小 右边的值都比它大  
    public static int partition0(Url_power[] L,int low,int high) {  
        int pivotkey;  
        pivotkey=L[low].power;  
        //顺序很重要，要先从右边找
        while(low<high) {  
            while(low<high && L[high].power>=pivotkey) {  //从后往前找到比key小的放到前面去  
                high--;  
            }  
            swap(L,low,high);     
            while(low<high && L[low].power<=pivotkey) {  //从前往后找到比key大的 放到后面去  
                low++;  
            }  
            swap(L,low,high);  
        } //遍历所有记录  low的位置即为 key所在位置, 且固定,不用再改变  
        return low;  
    }  
 //交换数组的两个位置  
    public static void swap(Url_power[] L,int i,int j) {  
          
        Url_power temp=L[i];  
        L[i]=L[j];  
        L[j]=temp;        
    }
}
