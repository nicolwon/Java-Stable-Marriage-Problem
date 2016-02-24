
public class Test {

	public static void main(String[] args) {
		int sum=0;
		X:
			for (int i=-3, j=0, k=0; i<7; j++, i+=2, k=0) {
				for (;(j%2)>=k;) {
					sum+=i--*j++-++k%2;
					System.out.print(sum);
					if (sum<-30)
						break X;
				}
			}
	}
}
