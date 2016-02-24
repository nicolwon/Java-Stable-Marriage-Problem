import java.util.ArrayList;

public class SMPSolver {
	
	private ArrayList<Participant> S = new ArrayList<Participant>(); //suitors
	private ArrayList<Participant> R = new ArrayList<Participant>(); //receivers
	private double avgSuitorRegret; 
	private double avgReceiverRegret;
	private double avgTotalRegret;
	private boolean matchesExist; //whether or not matches exist
	private boolean stable; 
	private long compTime; //computation time
	private boolean suitorFirst; //whether to print suitor stats first

	
	public SMPSolver() {
		this.avgSuitorRegret=0;
		this.avgReceiverRegret=0;
		this.avgTotalRegret=0;
		this.matchesExist=false;

	}
	
	public void initialize(ArrayList<Participant> suitor, ArrayList<Participant> receiver) {
//		this.S = suitor;
//		this.R = receiver;
//		this.suitorOpenings = suitorOpenings;
//		this.receiverOpenings = receiverOpenings;
		for (int i=0; i<suitor.size(); i++) {
			this.S.add(suitor.get(i));
		}
		for (int k=0; k<receiver.size(); k++) {
			this.R.add(receiver.get(k));
		}
	}
	
	public double getAvgSuitorRegret() {return this.avgSuitorRegret; }
	public double getAvgReceiverRegret() {return this.avgReceiverRegret; }
	public double getAvgTotalRegret() {return this.avgTotalRegret; }
	public boolean matchesExist() {return this.matchesExist; }
	public long getCompTime() {return this.compTime; }
	public ArrayList<Participant> getSuitor() {return this.S; }
	
	public void reset(ArrayList<Participant> S, ArrayList<Participant> R) { //reload
		this.S=S;
		this.R=R;
		this.avgSuitorRegret=0;
		this.avgReceiverRegret=0;
		this.avgTotalRegret=0;
		this.matchesExist=false;
	}
	
	public void clear() {
		this.S.clear();
		this.R.clear();
		this.avgSuitorRegret=0;
		this.avgReceiverRegret=0;
		this.avgTotalRegret=0;
		this.matchesExist=false;
	}
	
	public void match() {
		reset(S,R);
//		clear();
		long start = System.currentTimeMillis();
		boolean match = false, free = true, rankMatch=false;
		int temp1=-1, temp2=-1;
		
		
		if (matchingCanProceed()==false)
			return;
		else {
			for(int i = 0; i<S.size(); i++){
				for(int j = 0; j<R.size(); j++){
					S.get(i).addProposed();
				}
			}
			do {
				free=false;
				for (int i=0; i<S.size(); i++) { //for each suitor
					while (S.get(i).updateOpenings()>0) {
//						System.out.println(S.get(i).updateOpenings());
						free = true;
						rankMatch = false;
						for (int l=1; l<R.size()+1; l++) { //go through schools
							for (int k=0; k<R.size(); k++) { //go through ranks???
								if (S.get(i).getProposed(k)==false) { //if rank of school k is l
//									System.out.println(S.get(i).getRanking(k) +" " + l);
									if (S.get(i).getRanking(k)==l) { //if receiver k hasn't been proposed to yet
										rankMatch = true;
										temp1 = i;
										temp2 = k;
										break;
									}
								}
							}
							if (rankMatch)
								break;
							}
							if (rankMatch) {
//								System.out.format("Propose suitor %d, receiver %d\n", temp1, temp2);
								match = makeProposal(temp1, temp2);
	//								if (match) {
	//									S.get(i).updateOpenings(); // openings--
							}
//							}
						}
					}
			} while (free==true);
		}
		this.matchesExist = true;
		this.compTime = System.currentTimeMillis() - start;
		calcRegrets();
		printStatistics();
		if (S.size()>R.size())
			System.out.format("%d matches made in %dms!\n\n", S.size(), this.compTime);	
		else
			System.out.format("%d matches made in %dms!\n\n", R.size(), this.compTime);	
	}
	
	public boolean makeProposal(int suitorIndex, int receiverIndex) {
		boolean match = false;
		int lpIndex=-1; //index of least-preferred match
		
		S.get(suitorIndex).setProposed(receiverIndex,true);
		if (R.get(receiverIndex).updateOpenings()>0) {
			makeEngagement(suitorIndex, receiverIndex);
			match = true;
		}
		else {
			//least preferred student has ranking of rankings.size()
//			for (int i=0; i<R.get(receiverIndex).getNParticipants(); i++) { //go through every ranking
//				if (R.get(receiverIndex).getRanking(i)==R.get(receiverIndex).getNParticipants()) {
//					lpIndex = i;
//				}
//				if (R.get(receiverIndex).getRanking(suitorIndex)<R.get(receiverIndex).getRanking(lpIndex)) {
//					makeEngagement(suitorIndex, receiverIndex, lpIndex);
//					match=true;
//				}
//			}
			if (R.get(receiverIndex).getRanking(suitorIndex)<R.get(receiverIndex).getRanking(R.get(receiverIndex).getWorstMatch())) {
				makeEngagement(suitorIndex, receiverIndex);
				match = true;
			}
		}
		return match;
	}
	
	public void makeEngagement(int suitorIndex, int receiverIndex) {
//		S.get(suitor).setSchool(receiver);
//		if (R.get(receiver).getStudent()!=-1)
//			S.get(R.get(receiver).getStudent()).setSchool(-1); //set previously matched student's school to -1
//		R.get(receiver).setStudent(suitor);
		if (R.get(receiverIndex).updateOpenings()>0) {
			R.get(receiverIndex).addMatch(suitorIndex);
			R.get(receiverIndex).updateOpenings();
			
			S.get(suitorIndex).addMatch(receiverIndex);
			S.get(suitorIndex).updateOpenings();
		}
		else {
//			S.get(R.get(receiverIndex).getWorstMatch()).setOpenings(S.get(R.get(receiverIndex).getWorstMatch()).getOpenings()+1);
			S.get(R.get(receiverIndex).getWorstMatch()).setMatchNull(receiverIndex); //unmatch receiver from least preferred school
			S.get(R.get(receiverIndex).getWorstMatch()).updateOpenings();
			
			R.get(receiverIndex).setMatchNull(R.get(receiverIndex).getWorstMatch()); //unmatch least preferred school from receiver
			R.get(receiverIndex).updateOpenings();
			
			R.get(receiverIndex).addMatch(suitorIndex);
			R.get(receiverIndex).updateOpenings();
			
			S.get(suitorIndex).addMatch(receiverIndex);
			S.get(suitorIndex).updateOpenings();
		}
	}
	
	public boolean matchingCanProceed() {
		boolean proceed;
		int suitorOpenings=0, receiverOpenings=0;
		
		for (int i=0; i<S.size(); i++) {
			suitorOpenings+=S.get(i).getMaxMatches();
		}
		for (int k=0; k<R.size(); k++) {
			receiverOpenings+=R.get(k).getMaxMatches();
		}
		
		if (S.size()!=0 && R.size()!=0 && suitorOpenings==receiverOpenings)
			proceed = true;
		else if (S.size()==0) {
			System.out.println("ERROR: No suitors are loaded!\n");
			proceed = false;
		}
		else if (R.size()==0) {
			System.out.println("ERROR: No receivers are loaded!\n");
			proceed = false;
		}
		else {
			System.out.println("ERROR: The number of suitor and receiver openings must be equal!\n");
			proceed = false;
		}
	return proceed;
	}
	
	public void calcRegrets() {
		double suitorRegret=0, receiverRegret=0;;
		
		for (int i=0; i<S.size(); i++) {
			suitorRegret += S.get(i).calcRegret(); //sum up regret from each suitor
		}
		this.avgSuitorRegret = (suitorRegret/S.size());
		
		for (int k=0; k<R.size(); k++) {
			receiverRegret += R.get(k).calcRegret();
		}
		this.avgReceiverRegret = (receiverRegret/R.size());
		
		this.avgTotalRegret = ((this.avgSuitorRegret*S.size())+(this.avgReceiverRegret*R.size()))/(S.size()+R.size());
//		double regret = 0, sumSuitorRegret=0, sumReceiverRegret=0;
//		for (int i=0; i<S.size(); i++) { //avgStudentRegret
//			for(int j = 0; j<R.size(); j++){
//				if(S.get(i).getSchool() == S.get(i).getRanking(j)-1){
//					regret = j;
//					break;
//				}
//			}
//			//regret = S.get(i).getSchool()-1;
//			sumSuitorRegret+=regret;
//		}
//		this.avgSuitorRegret= (sumSuitorRegret/S.size());
//		
//		//R.get(i).getRanking(i)
//		
//		for (int k=0; k<R.size(); k++) { //avgSchoolRegret
//			regret = R.get(k).getRanking(R.get(k).getStudent()) - 1;
//			sumReceiverRegret+=regret;
//		}
//		this.avgReceiverRegret = (sumReceiverRegret/R.size());
//		
//		this.avgTotalRegret = (this.avgSuitorRegret + this.avgReceiverRegret)/2;
//		return;
	}
	
//	public static double avgStudentRegret(ArrayList<Participant> S, ArrayList<Participant> H, int nStudents, int nSchools) {
//		 int k;
//		 double regret, sumRegret=0; //Student
//		 for (k=0; k<nStudents; k++) {
//			 S.get(k).findRankingByID(H, 1);
//			 regret = S.get(k).getRanking(S.get(k).getSchool()) - 1;
//			 sumRegret+=regret;
//		 }
//		 sumRegret/=nStudents;
//		 return sumRegret;
//	 }
	
	public boolean isStable() {
		boolean stable = true, alreadyMatched=false;
		int temp1=-1, temp2=-1;

		 
		 for (int i=0; i<S.size(); i++) {
			 for (int j=0; j<R.size(); j++) {
				 if (S.get(i).getRanking(j)<S.get(i).getRanking(S.get(i).getWorstMatch())) { //if receiver ranking<worst match ranking
					 alreadyMatched=false;
					 for (int k=0; k<S.get(i).getNMatches(); k++) {
						 if (S.get(i).getMatch(k)==j) { //if receiver is in matches
							 alreadyMatched = true;
							 continue;
						 }
						 else {
							 temp1 = i;
							 temp2 = j;
						 }
					 }
					 if (!alreadyMatched) {
						 if (R.get(temp2).getRanking(temp1)<R.get(temp2).getRanking(R.get(temp2).getWorstMatch())) {
							 stable = false;
							 break;
						 }  
					 }
				 }
			 }
		 }
		 return stable;
	}
	
	public void print(boolean studentSuitor) {
		if (this.matchesExist==false) {
			System.out.println("ERROR: No matches exist!\n");
			return;
		}
		else {
			printMatches(studentSuitor);
			printStatistics();
		}
	}
	
	public void printMatches(boolean studentSuitor) {
		System.out.println("Matches:");
		System.out.println("--------");
		 
//		int i;
//		for (i=0; i<S.size(); i++) {
//		System.out.format(R.get(i).getName()+": "+S.get(R.get(i).getStudent()).getName()+"\n");
//		}
//		System.out.println();
		if (!studentSuitor) {
			for (int i=0; i<S.size(); i++) { //for each suitor
				System.out.format("%s: ", S.get(i).getName());
				for (int j=0; j<S.get(i).getNMatches(); j++) { //go through suitor's matches
					if (j==S.get(i).getNMatches()-1) { //if not the last match
						System.out.println(R.get(S.get(i).getMatch(j)).getName());
					}
					else {
						System.out.print((R.get(S.get(i).getMatch(j)).getName())+", ");
					}
				}
			}
		}
		else {
			for (int k=0; k<R.size(); k++) { //for each suitor
				System.out.format("%s: ", R.get(k).getName());
				for (int l=0; l<R.get(k).getNMatches(); l++) { //go through suitor's matches
					if (l==R.get(k).getNMatches()-1) { //if not the last match
						System.out.println(S.get(R.get(k).getMatch(l)).getName());
					}
					else {
						System.out.print((S.get(R.get(k).getMatch(l)).getName())+", ");
					}
				}
			}
		}
			System.out.println();
	}
	
	public void printStatistics() {
		System.out.format("Stable matching? ");
		if (isStable()==false)
			System.out.println("No");
		else
			System.out.println("Yes");
		
		System.out.format("Average suitor regret: %.2f\n", this.avgSuitorRegret);
		System.out.format("Average receiver regret: %.2f\n", this.avgReceiverRegret);
		System.out.format("Average total regret: %.2f\n\n", this.avgTotalRegret);
	}
//	public void printStatsRow ( String rowHeading ) {// print stats as row
//		String stable;
//		if (isStable()==false)
//			stable = "No";
//		else
//			stable = "Yes";
//		
//		System.out.print("%3s                 ", stable);
//		
//				+ "
//		
//		Yes                 6.00                 0.00                 1.71                    0
//	}
	public void printMatchesRowStudent(int m) {
		for (int i=0; i<S.get(m).getNMatches(); i++) { //for each of suitor's matches
			if (i==S.get(m).getNMatches()-1)
				System.out.format("%-40s", R.get(S.get(m).getMatch(i)).getName());
			else {
				System.out.print((R.get(S.get(m).getMatch(i)).getName()) + ", ");
			}
		}
	}
	
	public void printMatchesRowSchool(int m) {
		String compiled="";
		
		for (int i=0; i<R.get(m).getNMatches(); i++) { //for each of suitor's matches
//			compiled=null;
			if (i==R.get(m).getNMatches()-1)
//				System.out.print(S.get(R.get(m).getMatch(i)).getName());
				compiled += ((S.get(R.get(m).getMatch(i)).getName()));
			else if (i==0 && R.get(m).getNMatches()==1) {
				compiled = (S.get(R.get(m).getMatch(i)).getName());
			}
			else if (i==0) {
				compiled = (S.get(R.get(m).getMatch(i)).getName()) + ", ";
			}
			else {
//				System.out.print((S.get(R.get(m).getMatch(i)).getName()) + ", ");
				compiled += ((S.get(R.get(m).getMatch(i)).getName())+", ");
			}
			
		}
		System.out.format("%-40s", compiled);
	}
	
	public void modify(ArrayList<Student> students, ArrayList <School> schools, boolean schoolFirst){
		if(!schoolFirst){ //student = suitor
			for(int i = 0; i<S.size(); i++){
				this.S.get(i).setName(students.get(i).getName());
				for(int j = 0; j<R.size(); j++){
					this.S.get(i).setRanking(j, students.get(i).getRanking(j));
				}
			}
			for(int i = 0; i<R.size(); i++){
				this.R.get(i).setName(schools.get(i).getName());
				this.R.get(i).setMaxMatches(schools.get(i).getMaxMatches());
				for(int j = 0; j<S.size(); j++){
					this.R.get(i).setRanking(j, schools.get(i).getRanking(j));
				}
			}
		}
		else{
			for(int i = 0; i<S.size(); i++){
				this.S.get(i).setName(schools.get(i).getName());
				this.S.get(i).setMaxMatches(schools.get(i).getMaxMatches());
				for(int j = 0; j<R.size(); j++){
					this.S.get(i).setRanking(j, schools.get(i).getRanking(j));
				}
			}
			for(int i = 0; i<R.size(); i++){
				this.R.get(i).setName(students.get(i).getName());
				for(int j = 0; j<S.size(); j++){
					this.R.get(i).setRanking(j, students.get(i).getRanking(j));
				}
			}
		}
	}
}
