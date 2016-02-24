import java.util.ArrayList;

public class Participant {
	private String name; //name
	private ArrayList<Integer> rankings = new ArrayList<Integer>(); // rankings of participants
	private ArrayList<Integer> matches = new ArrayList<Integer>(); // match indices
	private int regret; //total regret
	private int maxMatches; // max # of allowed matches/openings
	private ArrayList<Boolean> proposed = new ArrayList<Boolean>();
	private int openings;
	
	public Participant() {
		this.name=null;
		this.regret = 0;
		this.maxMatches=0;
	}
	

	public Participant (String name, int maxMatches, ArrayList<Integer> rankingsArray) {
		this.name = name;
		this.maxMatches = maxMatches;
		this.rankings = rankingsArray;
		this.openings = maxMatches;
//		this.nParticipants = 
	}
	
	public String getName(){ return this.name; }
	public int getRanking(int i) { return this.rankings.get(i); }
	public int getMatch(int m) { return this.matches.get(m); }
	public int getRegret() {return this.regret; }
	public int getMaxMatches() {return this.maxMatches; }
	public int getNParticipants() {return this.rankings.size(); }
	public int getNMatches() {return this.matches.size(); }
//	TODO public boolean isFull()
	public boolean getProposed(int i) {return this.proposed.get(i); }
	public int updateOpenings() {
		this.openings = maxMatches - matches.size();
		return this.openings;
	}

	
	public void setName(String name) { this.name = name; }
	public void setRanking (int i, int r) { this.rankings.set(i, r) ; }
	public void setMatchNull(int m) {
		for (int i=0; i<matches.size(); i++) {
			if (matches.get(i)==m) {
				this.matches.remove(i);
			}
		}
		//this.matches.remove(m); 
		}
	public void setRegret(int r) {this.regret = r; }
//	TODO public void setNParticipants(int n) {this.nP
	public void setMaxMatches(int n) {this.maxMatches = n; }

	public void setProposed (int r, boolean p) {this.proposed.set(r,p); } //school ranked r has been proposed to
	public void addProposed(){this.proposed.add(false);}
	public void addMatch(int n){this.matches.add(n); }
//	public void setOpenings(int o) {this.openings = o; }
	
	public void clearMatches() {
		this.matches.clear();
	}
//	public int findRankingByID(int k) { //find studentID
//	}
	public int getWorstMatch() {
		int tempRank=this.rankings.get(this.matches.get(0)); //get ranking of first match
		int temp=0, worstMatch;
		
		for (int i=1; i<matches.size(); i++) {
			if (tempRank<this.rankings.get(this.matches.get(i))) {
				tempRank = this.rankings.get(this.matches.get(i));
				temp = i; //temp is school in position temp
			}
		}
		worstMatch = this.matches.get(temp); //index of least-preferred
		return worstMatch;
	}
	public void unmatch(int k) {
		
	}
//	public int getSingleMatchedRegret(int k) { //get regret from match with k
//		
//	}
	public double calcRegret() {
		double regret=0;
		for (int i=0; i<matches.size(); i++) {
			regret += ((this.getRanking(this.matches.get(i))-1));
		}
		return regret;
	}
		
	}

