import java.io.IOException;
import java.util.ArrayList;

public class School extends Participant{

	private String name;
	private double alpha; //GPA weight
	private ArrayList<Integer> rankings = new ArrayList<Integer>(); //rankings of students
	private int student; //index of matched student

	private ArrayList<Double> composite= new ArrayList<Double>(); //composite score
	//private int maxMatches;
	
	
	public School(){
		super();
		this.setName(null);
		this.alpha = 0.;
		this.student = -1;
		this.setRegret(0);
		this.setMaxMatches(0);
		
	}
	public School(String name, double alpha, int maxMatches){
		super();
		this.setName(name);
		this.alpha = alpha;
		this.student = -1;
		this.setRegret(0);
		this.setMaxMatches(maxMatches);
	}
	
	//getters
	public String getName(){ return this.name; }
	public double getAlpha() {return this.alpha; }
	public int getRanking(int i) { return this.rankings.get(i); } //get ranking of student i
	public int getStudent() { return this.student; }
	public ArrayList<Integer> getRankingsArray() {return this.rankings; }
//	public int getRegret() {return this.regret; }
	
	//setters
	public void setName(String name) { this.name = name; }
	public void setAlpha (double alpha) {this.alpha = alpha; }
	public void setRanking (int i, int r) { this.rankings.set(i, r) ; } //set student i with ranking r
	public void setStudent(int i) {this.student = i; }
//	public void setRegret(int r) {this.regret = r; }
	public void setNStudents (int n) { //set rankings array size
		
	}
	
	// find student ID based on ranking
	public int findRankingByID(ArrayList<Student> S, int rank) {
		int i, ind=0;
		for (i=0; i<S.size(); i++) { //for each student
			if (rankings.get(i)==rank) {
				ind = i;
				break;
			}
			else
				continue;
		}
		return ind;
	}
	
	//get new info from the user
	public void editInfo(ArrayList<Student> S, boolean canEditRankings) throws IOException{
		
		System.out.print("\nName: ");
		this.setName(Pro5_wongsoon.cin.readLine());
		this.setAlpha(Pro5_wongsoon.getDouble("GPA weight: ", 0., 1.));
		this.setMaxMatches(Pro5_wongsoon.getinfinityInteger("Maximum number of matches: ", 1, 2000));
//		System.out.println();
		
		calcRankings(S);
	}
	
	//calculate rankings based on weight alpha
	public void calcRankings(ArrayList<Student> S) {
		int i, j, k;
		
		this.composite.clear();
		for (i=0; i<S.size(); i++) { //composite score array
		composite.add(this.alpha*(S.get(i).getGPA()) + ((1-this.alpha)*(S.get(i).getES())));
		}	
		
//		for (int m=0; m<S.size(); m++) {
//			rankings.add(m, 0);
//		}
		
		this.rankings.clear();
		for (j=0; j<S.size(); j++) {
			rankings.add(j, 1);
			for (k=0; k<S.size(); k++) {
				if (j==k)
					continue;
				else if (composite.get(j)<composite.get(k)) {
					rankings.set(j, rankings.get(j)+1);
				}
				else if (composite.get(j)>composite.get(k)) {
					continue;
				}
				else {
					if (j<k) {
						continue;
					}
					else {
						rankings.set(j, rankings.get(j)+1);
					}
				}
				}
			this.setRanking(j, rankings.get(j));
		}
	}
	

	
	//print school info and assigned student in tabular format
	public void print(ArrayList<Student> S, boolean rankingsSet, int index, SMPSolver smp1) {
		System.out.format("%-40s    %4d    %.2f  ", this.getName(), this.getMaxMatches(), this.getAlpha());
		if (!smp1.matchesExist()) {
			String a = "-";
			System.out.format("%-40s", a);
		}
		else
//			System.out.format("%-40s", S.get(this.getStudent()).getName());
			smp1.printMatchesRowSchool(index);
		
		printRankings(S, rankingsSet);
		System.out.println("");
		return;
	}
	
	//print the rankings separated by a comma
	public void printRankings(ArrayList<Student> S, boolean rankingsAssigned) {
		int i, j;
		if (rankingsAssigned==false) {
			System.out.print("-");
		}
		else {
			for (i=0; i<S.size(); i++) {
				for (j=0; j<S.size(); j++) {
					if (rankings.get(j)==i+1) {
						if (i+1==S.size()) {

							System.out.format(S.get(j).getName());
						}
						else {

							System.out.print(S.get(j).getName()+", ");
						}
					}
					else
						continue;
				}
			}
		}
		return;
	}
	
	
}
