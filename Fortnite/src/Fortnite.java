package problems;


import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;

import problems.AbstractProblem;
import org.chocosolver.solver.variables.IntVar;



public class Fortnite extends AbstractProblem {

	//@Option(name = "-i", aliases = "--instance", usage = "Instance ID.", required = true)
	Data data = Data.inst3;

	int nb_sessions, nb_days, nb_players_per_session, min_sessions, max_sessions , nb_players;
	private IntVar[][] variables;
	

	@Override
	public void buildModel() {

		nb_sessions = data.param(0);

		nb_days = data.param(1);

		nb_players_per_session = data.param(2);

		min_sessions = data.param(3);

		max_sessions = data.param(4);
		
		nb_players = nb_players_per_session * nb_sessions;

		model = new Model();
		
		variables = model.intVarMatrix("variables", nb_players, nb_days, 1, nb_sessions);
		
		// repartition des joueur en sessions, pas plus de nb_players_per_session dans chaque session
		IntVar max =  model.intVar(nb_players_per_session);
		for (int d= 0; d<nb_days; d++ ) {
		IntVar[] temps = new IntVar[nb_players];
			for (int p=0; p<nb_players; p++) {
				temps[p]= variables[p][d];
			}
			for (int s=1; s<nb_sessions+1; s++) {
				
				model.count(s, temps, max).post();
			}
			
		
		}
		
		// si deux joueurs jouent ensemble un jours ils ne jouent pas ensemble le lendemain

		
		
		
		//deux joueurs jouent ensemble entre min et max sessions
		IntVar minS =  model.intVar(min_sessions);
		IntVar maxS =  model.intVar(max_sessions+1);
		for(int p1=0; p1<nb_players-1; p1++) {
			for  (int p2 =p1+1; p2<nb_players; p2++) {
				IntVar[] temps = new IntVar[nb_days];
				for (int d =0; d<nb_days; d++) {
					temps[d]= variables[p1][d].sub(variables[p2][d]).abs().intVar();
				}
				for (int i=0; i<nb_days-1; i++) {
					// si deux joueur jouent ensemble ils ne peuvent pas jouer ensemble a nouveaux le lendemain
					model.arithm(temps[i], "+", temps[i+1], "!=", 0).post();
				}
				//max session
				model.not(model.count(0, temps, maxS)).post();
				//min session
				if (min_sessions>0) {model.count(0, temps, minS).post();}
				
			}

		}
		
	}
	
		//TODO


	@Override
	public void configureSearch() {
		//TODO
	}

	@Override
	public void solve() {
		
		Solution solution = model.getSolver().findSolution();

		if(solution != null){
		    System.out.println(solution.toString());
		}
		

		model.getSolver().printStatistics();
		
		
	}

	public static void main(String[] args) {
		new Fortnite().execute(args);
	}

	/////////////////////////////////// DATA ////////////////////////////
	/////////////////////////////////////////////////////////////////////

	enum Data {
		inst1(new int[] { 2, 3, 2, 1, 1 }),

		inst2(new int[] { 4, 5, 4, 1, 1 }),

		inst3(new int[] { 5, 8, 2, 0, 1 }),

		inst4(new int[] { 5, 6, 3, 0, 1 }),

		inst5(new int[] { 5, 6, 5, 1, 1 }),

		inst6(new int[] { 6, 3, 6, 0, 2 }),

		inst7(new int[] { 7, 7, 7, 0, 7 }),

		inst8(new int[] { 8, 5, 4, 0, 1 }),

		inst9(new int[] { 8, 9, 8, 0, 1 }),

		inst10(new int[] { 10, 7, 10, 0, 7 }),

		;
		final int[] param;

		Data(int[] param) {
			this.param = param;
		}

		int param(int i) {
			return param[i];
		}
	}
}
