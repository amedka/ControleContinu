package problems;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;
import org.kohsuke.args4j.Option;

public class NurseRostering extends AbstractProblem {

	@Option(name = "-i", aliases = "--instance", usage = "Instance ID.", required = false)
	private Data data = Data.inst1;

	private int nb_nurses, nb_days, day_shift, lb_nightshift, ub_nightshift;
	private IntVar[][] NURSE;



	@Override
	public void buildModel() {

		nb_nurses = data.param(0);

		nb_days = data.param(1);

		day_shift = data.param(2);

		lb_nightshift = data.param(3);

		ub_nightshift = data.param(4);

	//	int day = 1;
	//	int night = 2;
	//	int dayoff = 3;
		
		model = new Model();

		NURSE =  model.intVarMatrix("NURSE", nb_nurses, nb_days, 1, 3);
	//Variables
		
		// Constraint1: In each four day period a nurse must have at least one day off
		for(int j=0; j<nb_nurses; j++) {
			IntVar lim = model.intVar(0);
			for (int i = 0; i < nb_days-3; i++ ) {
				IntVar[] temps = new IntVar[] {NURSE[j][i], NURSE[j][i+1], NURSE[j][i+2], NURSE[j][i+3]};
				
				model.not(model.count(3, temps, lim )).post();
			}
		}


		// Constraint2: no nurse can be scheduled for 3 night shifts in a row

		for (int i = 0; i<nb_nurses; i++) {
			for (int j=0; j<nb_days-2; j++) {
				IntVar[] temps = {NURSE[i][j], NURSE[i][j+1], NURSE[i][j+2]};
				model.not(model.count(2, temps,model.intVar(3))).post();
			}
		}

		// Constraint3: no nurse can be scheduled for a day shift after a night shift

		for (int i = 0; i<nb_nurses; i++) {
			for (int j=0; j<nb_days-1; j++) {
				IntVar t = NURSE[j][i+1].mul(3).intVar();
				model.arithm(NURSE[j][i], "+", t,"!=", 5).post();
			}
		}

		// Constraint4: day shift size and min/max night shift size

		for (int i = 0; i<nb_days; i++) {
			IntVar[] temps = new IntVar[nb_nurses];
			for (int j=0; j<nb_nurses; j++) {
				temps[j]= NURSE[j][i];
			
			}
			model.not(model.count(2, temps, model.intVar(ub_nightshift+1))).post();
			
			model.count(2, temps,model.intVar(lb_nightshift) ).post();
			
			
		    model.not(model.count(1, temps, model.intVar(day_shift+1))).post();
		    
			model.count(1, temps,model.intVar(day_shift) ).post();
			
			
		}

	}

	@Override
	public void configureSearch() {
		// TODO
	}

	@Override
	public void solve() {

		//model.getSolver().solve();
		Solution solution = model.getSolver().findSolution();

		if(solution != null){
		    System.out.println(solution.toString());
		}
		//printSolution

		model.getSolver().printStatistics();
	}

	public static void main(String[] args) {
		new NurseRostering().execute(args);
	}

	/////////////////////////////////// DATA ////////////////////////////
	/////////////////////////////////////////////////////////////////////

	enum Data {
		inst1(new int[] { 6, 7, 2, 2, 3 }),

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
