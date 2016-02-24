import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Pro5_wongsoon {

public static BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));

	
	public static void main(String[] args) throws IOException {
		String choice;
		boolean rankingsSet=false;
		int nSchoolOpenings=0, nStudents;
		
		ArrayList<Student> stu = new ArrayList<Student>();
		ArrayList<School> scl = new ArrayList<School>();
		ArrayList<Participant> suitor = new ArrayList<Participant>();
		ArrayList<Participant> receiver = new ArrayList<Participant>();
		ArrayList<Participant> suitor1 = new ArrayList<Participant>();
		ArrayList<Participant> receiver1 = new ArrayList<Participant>();
		
		
//		SMPSolver smp1 = new SMPSolver();
//		SMPSolver smp2 = new SMPSolver();
		SMPSolver smp1 = new SMPSolver(); //student-optimal
		SMPSolver smp2 = new SMPSolver(); //school-optimal

		
		do {
			displayMenu();
			choice = getChoice();
			if (choice.equals("L")||choice.equals("l")) {
				nSchoolOpenings = loadSchools(scl);
				if (nSchoolOpenings==-1) {
					stu.clear();
				}
				nStudents = loadStudents(scl, stu, nSchoolOpenings);
				if (scl.size()!=0 && stu.size()!=0)
					rankingsSet=true;
				
			}
			else if (choice.equals("E")||choice.equals("e")) {
				editData(stu, scl, rankingsSet, smp1);
				smp1.modify(stu, scl, false);
				smp2.modify(stu, scl, true);
			}
			else if (choice.equals("P")||choice.equals("p")) {
				smp1.modify(stu, scl, false);
				smp2.modify(stu, scl, true);
				printStudents(stu, scl, rankingsSet, smp1);
				printSchools(stu, scl, rankingsSet, smp1);
			}
			else if (choice.equals("M")||choice.equals("m")) {
				for (int i=0; i<stu.size(); i++) {
					suitor.add(new Participant(stu.get(i).getName(), 1, stu.get(i).getRankingsArray()));
//					suitor1.add(new Participant(stu.get(i).getName(), 1, stu.get(i).getRankingsArray()));
				}
				for (int k=0; k<scl.size(); k++) {
					receiver.add(new Participant(scl.get(k).getName(), scl.get(k).getMaxMatches(), scl.get(k).getRankingsArray()));
//					receiver1.add(new Participant(scl.get(k).getName(), scl.get(k).getMaxMatches(), scl.get(k).getRankingsArray()));
				}
				smp1.clear();
				smp1.initialize(suitor, receiver);
				System.out.println("STUDENT-OPTIMAL MATCHING\n");
				smp1.match();
				
				suitor.clear();
				receiver.clear();
				
				for (int i=0; i<stu.size(); i++) {
					receiver1.add(new Participant(stu.get(i).getName(), 1, stu.get(i).getRankingsArray()));
//					suitor1.add(new Participant(stu.get(i).getName(), 1, stu.get(i).getRankingsArray()));
				}
				for (int k=0; k<scl.size(); k++) {
					suitor1.add(new Participant(scl.get(k).getName(), scl.get(k).getMaxMatches(), scl.get(k).getRankingsArray()));
//					receiver1.add(new Participant(scl.get(k).getName(), scl.get(k).getMaxMatches(), scl.get(k).getRankingsArray()));
				}
				
				smp2.clear();
				smp2.initialize(suitor1, receiver1);
				System.out.println("SCHOOL-OPTIMAL MATCHING\n");
				smp2.match();
				suitor1.clear();
				receiver1.clear();
			} 
			else if (choice.equals("D")||choice.equals("d")) {		
				smp1.calcRegrets();
				smp1.isStable();
				System.out.println("STUDENT-OPTIMAL SOLUTION\n");
				smp1.print(true);
				
				smp2.calcRegrets();
				smp2.isStable();
				System.out.println("SCHOOL-OPTIMAL SOLUTION\n");
				smp2.print(false);
			}
			else if (choice.equals("X")||choice.equals("x")) {
				smp1.calcRegrets();
				smp1.isStable();
				smp2.calcRegrets();
				smp2.isStable();
				
				if (!smp1.matchesExist())
					System.out.println("ERROR: No matches exist!\n");
				else
					printComparison(smp1, smp2);
			}
			else if (choice.equals("R")||choice.equals("r")) {
				//smp.reset(stu,scl);
				stu.clear();
				scl.clear();
				smp1.clear();
				smp2.clear();
				rankingsSet=false;
				nSchoolOpenings=0;
				nStudents=0;
				System.out.println("Database cleared!\n");
			}
			else if (choice.equals("Q")||choice.equals("q"))
				break;
			else
				System.out.println("ERROR: Invalid menu choice!\n");
		} while (!choice.equals("Q")||!choice.equals("q"));
			
		System.out.print("Hasta luego!");
	}
	
	public static void displayMenu() {
		System.out.println("JAVA STABLE MARRIAGE PROBLEM v3\n");
		System.out.println("L - Load students and schools from file");
		System.out.println("E - Edit students and schools");
		System.out.println("P - Print students and schools");
		System.out.println("M - Match students and schools using Gale-Shapley algorithm");
		System.out.println("D - Display matches");
		System.out.println("X - Compare student-optimal and school-optimal matches");
		System.out.println("R - Reset database");
		System.out.println("Q - Quit");
		return;

	}


	public static String getChoice() throws IOException {
			String input;
			System.out.print("\nEnter choice: ");
			input = cin.readLine();
			System.out.println("");
			return input;
		}
	
	public static int loadSchools(ArrayList<School> H) throws IOException {
		boolean done=true;
		int load=0, total = 0, maxMatches, nSchoolOpenings=0;
		String name, line, filename;
		double alpha;
		
		while (done==true) {
			System.out.print("Enter school file name (0 to cancel): ");
			filename = cin.readLine();
			System.out.println();
			
			if (filename.equals("0")) {
				System.out.println("File loading process canceled.\n");
				nSchoolOpenings = -1;
				break;
			}
			else {
				File file = new File(filename);
				
				if (file.exists()) {
					BufferedReader fin = new BufferedReader (new FileReader(filename));
					do {
						line = fin.readLine();
						if (line!= null) {
							total++;
							String[] splitString = line.split(",");
							name = splitString[0];
							alpha = Double.parseDouble(splitString[1]);
							maxMatches = Integer.parseInt(splitString[2]);
							if (alpha>=0 && alpha<=1) {
								load++;
								H.add(new School(name, alpha, maxMatches));
								nSchoolOpenings+=maxMatches;
							}
						}	
					} while (line!=null);
					System.out.format("%d of %d schools loaded!\n\n", load, total);
					fin.close();
					break;
				}
				else {
					System.out.println("ERROR: File not found!\n");	
				}
				//fin.close();
			}
		}
		return nSchoolOpenings;
	}
		
	public static int loadStudents(ArrayList<School> H, ArrayList<Student> S, int nSchools) throws IOException{
		boolean done=true, repeat=false;
		int load=0, total = 0, ES;
		String name, line, filename;
		double GPA;
		
		ArrayList<Integer> ranking = new ArrayList<Integer>();
		
		while (done==true) {
			System.out.print("Enter student file name (0 to cancel): ");
			filename = cin.readLine();
			System.out.println();
			
			if (filename.equals("0")) {
				System.out.println("File loading process canceled.\n");
				break;
			}
			else {
			File file = new File(filename);
			
			if (file.exists()) {
				BufferedReader fin = new BufferedReader (new FileReader(filename));	
				do {
				line = fin.readLine();
					if (line!= null) {
						//ranking.clear();
						repeat=false;
						total++;
						String[] splitString = line.split(",");
						name = splitString[0];
						GPA = Double.parseDouble(splitString[1]);
						ES = Integer.parseInt(splitString[2]);
						if (H.size()==(splitString.length-3)) {
							for (int i=1; i<H.size()+1; i++) { //for each school numbered 1,2,3...
								for (int m=0; m<H.size(); m++) { //for each school
									if (Integer.parseInt(splitString[m+3])==i) //if split string = rank 1						
										ranking.add(m+1); //if spli
								}
							}
//							maxMatches = Integer.parseInt(splitString[H.size()+3]);
							
						}
						else
							continue;
						
						if (GPA<0.0 || GPA>4.0) {
							repeat = true;
						}
						else if (ES<0 || ES>5)
							repeat = true;
						else for (int j=0; j<H.size(); j++) {
							if (ranking.get(j)<0 && ranking.get(j)>H.size())
								repeat = true;
							else {
								for (int k=0; k<j; k++) {
									if (ranking.get(k)==ranking.get(j))
										repeat = true;
								}
							}
						}
						if (repeat==false) {
							load++;
							S.add(new Student(name, GPA, ES, ranking));

						}
					}
					ranking.clear();
				} while (line!=null);
				System.out.format("%d of %d students loaded!\n\n", load, total);
				fin.close();
				break;
			}
			else {
				System.out.println("ERROR: File not found!\n");	
			}
			}
		}
		
		for (int l=0; l<H.size(); l++) {
			H.get(l).calcRankings(S);
		}
		return S.size();
	}

	
	public static void editData(ArrayList<Student> S, ArrayList<School> H, boolean rankingsSet, SMPSolver smp1) throws IOException {
		String input;
		do {
		System.out.println("Edit data");
		System.out.println("---------");
		System.out.println("S - Edit students");
		System.out.println("H - Edit high schools");
		System.out.println("Q - Quit");
		System.out.println("");
		System.out.print("Enter choice: ");
		input = cin.readLine();
		//System.out.println("");
		
		if (input.equals("S")||input.equals("s"))
			editStudents(S, H, rankingsSet, smp1);
		else if (input.equals("H")||input.equals("h"))
			editSchools(S, H, rankingsSet, smp1);
		else if (input.equals("Q")||input.equals("q"))
			break;
		else 
			System.out.println("\nERROR: Invalid menu choice!\n");
		} while (!input.equals("S") || !input.equals("s")|| !input.equals("H") || !input.equals("h")|| !input.equals("Q") || !input.equals("q"));
		System.out.println();
		return;
	}
	
	public static void editStudents(ArrayList<Student> S, ArrayList<School> H, boolean rankingsSet, SMPSolver smp1) throws IOException {
		int i;
		if (S.size()==0) {
			System.out.println("\nERROR: No students are loaded!\n");
			return;
		}
		else {
		
			do {
				editPrintStudents(S,H,rankingsSet, smp1);
				i = getInteger("Enter student (0 to quit): ", 0, S.size());
				if (i==0){
					//System.out.println();
					break;
				}
				else
					S.get(i-1).editInfo(S, H, rankingsSet);
			} while (i!=0);
			System.out.println();
			
			for (int l=0; l<H.size(); l++) {
				H.get(l).calcRankings(S);
			}
			
		return;
		}
	}
	
	 public static void editSchools(ArrayList<Student> S, ArrayList<School> H, boolean rankingsSet, SMPSolver smp1) throws IOException {
		 int i;
		 if (H.size()==0) {
			 System.out.println("\nERROR: No schools are loaded!\n");
			 return;
		 }
		 else {
			 
			 
			 do {
				editPrintSchools(S, H, rankingsSet, smp1);	
				i = getInteger("Enter school (0 to quit): ", 0, H.size());
				if (i==0)
					break;
				else 
					H.get(i-1).editInfo(S, rankingsSet);
				} while (i!=0);
			 System.out.println();
			 
			 for (int l=0; l<H.size(); l++) {
					H.get(l).calcRankings(S);
				}
			 
			 return;
		 }
	 }
	 
	 public static void printStudents(ArrayList<Student> S, ArrayList<School> H, boolean rankingsSet, SMPSolver smp1) {
		 if (S.size()==0) {
			 System.out.println("ERROR: No students are loaded!\n");
			 return;
		 }
		 else {
			 System.out.println("STUDENTS:\n");
			 int i;
			 System.out.println(" #   Name                                         GPA  ES  Assigned school                         Preferred school order");
			 System.out.println("---------------------------------------------------------------------------------------------------------------------------");
			 for (i=0; i<S.size(); i++) {
				 System.out.format("%3d. ", i+1);
				 smp1.modify(S, H, false);
				 S.get(i).print(H, rankingsSet, i, smp1);
			 }
			 System.out.println("---------------------------------------------------------------------------------------------------------------------------\n");
			 return;
		 }
	 }
	
	 public static void editPrintStudents(ArrayList<Student> S, ArrayList<School> H, boolean rankingsSet, SMPSolver smp1) {
		if (S.size()==0) {
				 System.out.println("ERROR: No students are loaded!\n");
				 return;
		}
		else {
			System.out.println();
			int i;
			System.out.println(" #   Name                                         GPA  ES  Assigned school                         Preferred school order");
			System.out.println("---------------------------------------------------------------------------------------------------------------------------");
			for (i=0; i<S.size(); i++) {
				System.out.format("%3d. ", i+1);
				smp1.modify(S, H, false);
				S.get(i).print(H, rankingsSet, i, smp1);
			}
			System.out.println("---------------------------------------------------------------------------------------------------------------------------");
			return;
		} 
	 }
	 
	 public static void printSchools(ArrayList<Student> S, ArrayList<School> H, boolean rankingsSet, SMPSolver smp1) {
		 if (H.size()==0) {
			 System.out.println("ERROR: No schools are loaded!\n");
			 return;
		 }
		 else {
			System.out.println("SCHOOLS:\n");
			 int i;
			 System.out.println(" #   Name                                     # spots  Weight  Assigned students                       Preferred student order");
			 System.out.println("------------------------------------------------------------------------------------------------------------------------------");
			 for (i=0; i<H.size(); i++) {
				 System.out.format("%3d. ", i+1);
				 smp1.modify(S, H, false);
				 H.get(i).print(S, rankingsSet, i, smp1);
			 }
			 System.out.println("------------------------------------------------------------------------------------------------------------------------------\n");
			 return;
		 } 
	 }
	 
	 public static void editPrintSchools(ArrayList<Student> S, ArrayList<School> H, boolean rankingsSet, SMPSolver smp1) {
		 if (H.size()==0) {
			 System.out.println("\nERROR: No schools are loaded!\n");
			 return;
		 }
		 else {
			 System.out.println();
			 int i;
			 System.out.println(" #   Name                                     # spots  Weight  Assigned students                       Preferred student order");
			 System.out.println("------------------------------------------------------------------------------------------------------------------------------");
			 for (i=0; i<H.size(); i++) {
				 System.out.format("%3d. ", i+1);
				 smp1.modify(S, H, false);
				 H.get(i).print(S, rankingsSet, i, smp1);
			 }
			 System.out.println("------------------------------------------------------------------------------------------------------------------------------");
			 return;
		 } 
	 }
	 
		public static int getInteger(String prompt, int LB, int UB) {
			boolean valid;
			int num=0;
			 System.out.print(prompt);
			 do {
				 valid = true;
				 num=0;
				 try {
					 num = Integer.parseInt(cin.readLine());
				 }
				 catch (NumberFormatException|IOException e) {
					 System.out.format("\nERROR: Input must be an integer in [%d, %d]!\n\n",LB, UB);
					 System.out.print(prompt);
					 valid = false;
					 continue;

				 }
				 if (num<LB || num>UB) {
					 System.out.format("\nERROR: Input must be an integer in [%d, %d]!\n\n",LB, UB);
					 System.out.print(prompt);
					 valid = false;
				 }
			 } while (!valid);
			 return num;
		}
		public static int getinfinityInteger(String prompt, int LB, int UB) {
			boolean valid;
			int num=0;
			 System.out.print(prompt);
			 do {
				 valid = true;
				 num=0;
				 try {
					 num = Integer.parseInt(cin.readLine());
				 }
				 catch (NumberFormatException|IOException e) {
					 System.out.format("\nERROR: Input must be an integer in [%d, infinity]!\n\n",LB);
					 System.out.print(prompt);
					 valid = false;
					 continue;

				 }
				 if (num<LB || num>UB) {
					 System.out.format("\nERROR: Input must be an integer in [%d, infinity]!\n\n",LB);
					 System.out.print(prompt);
					 valid = false;
				 }
			 } while (!valid);
			 return num;
		}
		
		
		 public static double getDouble(String prompt, double LB, double UB) {
			 boolean valid;
			 double num=0.00;
			 System.out.print(prompt);
			 do {
				 valid = true;
				 num=0.00;
				 try {
					 num = Double.parseDouble(cin.readLine());
				 }
				 catch (NumberFormatException|IOException e) {
					 System.out.format("\nERROR: Input must be a real number in [%.2f, %.2f]!\n\n", LB, UB);
					 System.out.print(prompt);
					 valid = false;
					 continue;
				 }
				 if (num<LB || num>UB) {
					 System.out.format("\nERROR: Input must be a real number in [%.2f, %.2f]!\n\n", LB, UB);
					 System.out.print(prompt);
					 valid = false;
				 }
			 } while (!valid);
			 return num;
	
		 }
		 
		 public static void printComparison(SMPSolver smp1, SMPSolver smp2) {
			 String stable1, stable2, tie = "Tie", school="School-opt", student="Student-opt";
			 
			 System.out.println("Solution              Stable    Avg school regret   Avg student regret     Avg total regret       Comp time (ms)");
			 System.out.println("----------------------------------------------------------------------------------------------------------------");
			 System.out.print("Student optimal          ");
				if (smp1.isStable()==false)
					stable1 = "No";
				else
					stable1 = "Yes";
			 System.out.format("%3s", stable1);
			 System.out.format("%21.2f%21.2f%21.2f%20d\n", smp1.getAvgReceiverRegret(), smp1.getAvgSuitorRegret(), smp1.getAvgTotalRegret(), smp1.getCompTime());
			 System.out.print("School optimal           ");
			 if (smp2.isStable()==false)
					stable2 = "No";
				else
					stable2 = "Yes";
			 System.out.format("%3s", stable2);
			 System.out.format("%21.2f%21.2f%21.2f%20d\n", smp2.getAvgSuitorRegret(), smp2.getAvgReceiverRegret(), smp2.getAvgTotalRegret(), smp2.getCompTime());
			
			 System.out.println("----------------------------------------------------------------------------------------------------------------");
			 System.out.print("WINNER           ");
			 if (stable1==stable2) 
				 System.out.format("%11s",tie);
			 else if (stable1=="Yes") 
				 System.out.format("%11s", student);
			 else
				 System.out.format("%11s", school);
			 
			 System.out.print("          ");
			 if (smp1.getAvgReceiverRegret()==smp2.getAvgSuitorRegret())
				 System.out.format("%11s",tie);
			 else if (smp1.getAvgReceiverRegret()<smp2.getAvgSuitorRegret())
				 System.out.format("%11s", student);
			 else
				 System.out.format("%11s", school);
			 
			 System.out.print("          ");
			 if (smp1.getAvgSuitorRegret()==smp2.getAvgReceiverRegret())
				 System.out.format("%11s",tie);
			 else if (smp1.getAvgSuitorRegret()<smp2.getAvgReceiverRegret())
				 System.out.format("%11s", student);
			 else
				 System.out.format("%11s", school);
			 
			 System.out.print("          ");
			 if (smp1.getAvgTotalRegret()==smp2.getAvgTotalRegret())
				 System.out.format("%11s",tie);
			 else if (smp1.getAvgTotalRegret()<smp2.getAvgTotalRegret())
				 System.out.format("%11s", student);
			 else
				 System.out.format("%11s", school);
			 
			 System.out.print("          ");
			 if (smp1.getCompTime()==smp2.getCompTime())
				 System.out.format("%11s",tie);
			 else if (smp1.getCompTime()<smp2.getCompTime())
				 System.out.format("%11s", student);
			 else
				 System.out.format("%11s", school);
			 
			 System.out.println();
			 System.out.println("----------------------------------------------------------------------------------------------------------------\n");
		 }
		 
		 
}
