import java.io.IOException;
import java.util.ArrayList;

public class Student extends Participant{

	private String name;	//name
	private double GPA;		//GPA
	private int ES;			//extracurricular score
	private ArrayList<Integer> rankings = new ArrayList<Integer>();	//rankings of schools
	private int school;		//index of matched school
	private int regret;		//regret
//	private ArrayList<Boolean> proposed = new ArrayList<Boolean>();
	
	public Student() {
		this.name = null;
		this.GPA = 0.;
		this.ES = 0;
		this.school = -1;
		this.regret = 0;
		this.setMaxMatches(1);
	}
	public Student(String name, double GPA, int ES, ArrayList<Integer> rankings){
		this.name = name;
		this.GPA = GPA;
		this.ES = ES;
		for(int i = 0; i<rankings.size(); i++){
			this.rankings.add(rankings.get(i));
			//this.rankings.add(rankings.get(i), i+1);
		

			
//			this.proposed.ensureCapacity(rankings.size());
//			for (int k = 0; k<this.proposed.size(); k++) {
//				this.proposed.add(false);
//			}
		this.setMaxMatches(1);	

//		for (int k=0; k<rankings.size(); k++) {
//			this.proposed.add(false);
//		}
		}
		//this.rankings = rankings;
		this.school=-1;
	}
	
	//getters
	public String getName(){ return this.name; }
	public double getGPA(){ return this.GPA; }
	public int getES(){ return this.ES; }
	public int getRanking(int i){ return this.rankings.get(i); } //get ranking of school i
	public int getSchool(){ return this.school; }
	public int getRegret(){ return this.regret; }
	public ArrayList<Integer> getRankingsArray() {return this.rankings; }
	
	//setters
	public void setName(String name) { this.name = name; }
	public void setGPA(double GPA) { this.GPA = GPA; }
	public void setES (int ES) { this.ES = ES; }
	public void setRanking (int i,int r){ this.rankings.set(i,r); //give school i ranking r
	}
	public void setSchool (int i) { this.school = i; }
	public void setRegret (int r) { this.regret = r; }
	public void setNSchools (int n) { //set rankings array size
		
	}
//	public void setProposed (int r, boolean p) {this.proposed.set(r,p); } //school ranked r has been proposed to
//	public void addProposed(){this.proposed.add(false);}

	//find school by ranking
	public int findRankingByID (ArrayList<School> H, int rank) {
		int i, ind=0;
		for (i=0; i<H.size(); i++) { //for each school
			if (rankings.get(i)==rank) {
				ind = i;
				break;
			}
			else
				continue;
		}
		return ind;
	}
	
	// get new info from the user
	public void editInfo (ArrayList<Student> S, ArrayList<School> H, boolean canEditRankings) throws IOException {
		String edit;
		int i;
		
		System.out.print("\nName: ");
		this.setName(Pro5_wongsoon.cin.readLine());
		this.setGPA(Pro5_wongsoon.getDouble("GPA: ", 0., 4.));
		this.setES(Pro5_wongsoon.getInteger("Extracurricular score: ",0,5));
		this.setMaxMatches(Pro5_wongsoon.getinfinityInteger("Maximum number of matches: ", 1, 2000));
		this.setMaxMatches(1);
		
		if (canEditRankings==false) {
			System.out.println();
			return;
		}
		else {
			do {
				System.out.print("Edit rankings (y/n): ");
				edit = Pro5_wongsoon.cin.readLine();
				if (edit.equals("Y")||edit.equals("y"))
					editRankings(H);
				else if (edit.equals("N")||edit.equals("n")) {
					//System.out.println();
					break;
				}
				else
					System.out.println("ERROR: Choice must be 'y' or 'n'!");
			} while (!edit.equals("Y") &&!edit.equals("y") &&!edit.equals("N")&&!edit.equals("n"));
		}
		//System.out.println();
		for (i=0; i<H.size(); i++) {
			H.get(i).calcRankings(S);
		}
		return;
	}
	
	//edit rankings
	public void editRankings (ArrayList<School> H) {
		int i, l, k;
		for (k=0; k<H.size(); k++) {
			this.setRanking(k, 0);
		}
		System.out.format("\nParticipant %s's rankings:\n", this.getName());
		int attempt;
		for (i=0; i<H.size(); i++) { //for each school
			attempt = Pro5_wongsoon.getInteger("School "+H.get(i).getName()+": ", 1, H.size());
			for (l=0; l<i; l++) {
				while (this.getRanking(l)==attempt) {
					System.out.format("ERROR: Rank %d already used!\n\n", attempt);
					attempt = Pro5_wongsoon.getInteger("School "+H.get(i).getName()+": ", 1, H.size());
					l=0;
				}
			}
//				int temp = this.rankings.get(attempt-1);
//				this.rankings.set(attempt-1, i+1);
//				this.rankings.set(this.rankings.indexOf(i+1), temp);
				this.rankings.set(i, attempt);
		}
		System.out.println();
		//System.out.println();
		return;
	}
	//print student info and assigned school in tabular format
	public void print(ArrayList<School> H, boolean rankingsSet, int index, SMPSolver smp1) {

		System.out.format("%-44s%4.2f  %2d  ", this.getName(), this.getGPA(), this.getES());
			if (!smp1.matchesExist()) {
				String a = "-";
				System.out.format("%-40s", a);
			}
			else
//				System.out.format("%-27s", H.get(this.getSchool()).getName());
				smp1.printMatchesRowStudent(index);
				
		
		printRankings(H, rankingsSet);
		System.out.println("");
		return;	
	}
	
	// print the rankings separated by a comma
//	public void printRankings(ArrayList<School> H, boolean rankingsAssigned) {
//		int i, j;
//		if (rankingsAssigned==false) {
//			System.out.print("-");
//		}
//		else {
//			//for (i=1; i<(H.size()+1); i++) {
//				for (j=0; j<H.size(); j++) {
//					//if (rankings.get(j)==i) {
//						if (j==H.size()-1)
//							System.out.format(H.get(rankings.get(j)-1).getName());
//						else {
//							System.out.print(H.get(rankings.get(j)-1).getName()+", ");
//						}
//					//}
//					//else{
//						//continue;
//				}
//			//}
//		}
//		return;
//	}
	
	public void printRankings(ArrayList<School> h, boolean rankingsAssigned) {
		int i, j;
		if (rankingsAssigned==false) {
			System.out.print("-");
		}
		else {
			for (i=0; i<h.size(); i++) {
				for (j=0; j<h.size(); j++) {
					if (rankings.get(j)==i+1) {
						if (i+1==h.size()) {

							System.out.format(h.get(j).getName());
						}
						else {

							System.out.print(h.get(j).getName()+", ");
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
