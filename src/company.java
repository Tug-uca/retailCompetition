import java.util.ArrayList;


public class company {

	private int discountFlat = 100;
	private int pointFlat = 100;
	private int discountHierarchy;
	private int pointHierarchy;

	private double prob1;
	private double prob2;
	private double prob3;
	private double prob4;

	private int sellNum;
	private int sellNumHie;
	private int sales;
	private int salesFreq;
	private int profit;
	private int preSales;
	private int preProfit;
	private int budget;

	private int preAction;// 0 is down,1 is up
	private int action = 0;

	private int FC = 600000;
	// private int FC = 0;

	private int VC = 700;
	private int unitPrice = 1000;

	private double cost = 150;

	private int countOfDeath = 0;
	private int death = 0;

	private double[][] value = new double[9][3];
	int s;
	private double alpha = 0.8;
	private double gamma = 0.1;

	private int scenario;

	private int strategy;// 0 is tesco, 1 is sainsbury's

	public company(double p1, double p2, double p3, double p4, int df, int pf,
			int dh, int ph, int st, int sce) {
		discountFlat = df;
		pointFlat = pf;
		discountHierarchy = dh;
		pointHierarchy = ph;

		prob1 = p1;
		prob2 = p2;
		prob3 = p3;
		prob4 = p4;

		strategy = st;
		scenario = sce;
		sellNum = 0;
		sales = 0;
		salesFreq = 0;
		profit = 0;
		budget = 0;

		for (int i = 0; i < value.length; i++) {
			for (int j = 0; j < value[0].length; j++) {
				value[i][j] = 0;
			}
		}
	}

	private int calcProfit() {

		double VCsum = 0;
		double a = (cost * (getProb1() + getProb2()) + VC) * sellNum;
		double b = cost * (prob3 + prob4) * sellNumHie;

		VCsum = a + b;
		int prof = (int) ((sellNum * unitPrice) - VCsum - FC);
		// if(strategy==0)System.out.println(a +"\t" +b);
		// if(strategy==0)System.out.println(discountHierarchy +
		// "\t"+pointHierarchy +"\t"+sellNumHie +"\t" +b);
		// if(strategy==0)System.out.println(discountFlat + "\t" +pointFlat +
		// "\t" +sellNum+ "\t"+discountHierarchy + "\t"+pointHierarchy
		// +"\t"+sellNumHie +"\t" + VCsum);
		// if(strategy==0)System.out.println(sellNumHie);

		return prof;
	}

	public void calcAcounting() {

		preSales = sales;
		preProfit = profit;
		sales = sellNum;
		salesFreq = sellNumHie;
		profit = calcProfit();
		sellNum = 0;
		sellNumHie = 0;

		if (death != 1) {
			budget += profit;
		}
		// if (profit <= 0) {
		// if (countOfDeath <= 5) {
		// countOfDeath++;
		// } else {
		// if (main.comArray.size() > 1) {
		// main.comArray.remove(main.comList[strategy]);
		// death = 1;
		// }
		// }
		// } else {
		// countOfDeath = 0;
		// }

		if (budget < 0) {
			if (main.comArray.size() > 1) {
				main.comArray.remove(main.comList[strategy]);
				death = 1;
			}
		}

		if (death != 1) {
			// Q値の更新
			double afterQ = value[action][getMaxQAction(action)];
			// value[s][action] = value[s][action] + alpha
			// * (gamma * afterQ - value[s][action]);
			value[s][action] = value[s][action] - alpha * value[s][action];

			// 報酬の加算
			value[s][action] += alpha * (profit - preProfit);

			// if(strategy==0){
			// System.out.println(value[s][action]+"\t"+s+"\t"+action+"\t"+profit+"\t"+preProfit+"\t"+alpha
			// * (profit-preProfit));
			// }
		}

		preAction = action;

	}

	public void dicisionMaking() {

		if (main.comNum == 2) {
			if (strategy == 0) {
				s = main.comList[1].getPreAction();
			} else {
				s = main.comList[0].getPreAction();
			}
		}

		if (main.comNum == 3) {
			if (strategy == 0) {
				s = 3 * main.comList[1].getPreAction()
						+ main.comList[2].getPreAction();
			} else if (strategy == 1) {
				s = 3 * main.comList[2].getPreAction()
						+ main.comList[0].getPreAction();
			} else {
				s = 3 * main.comList[0].getPreAction()
						+ main.comList[1].getPreAction();
			}
		}

		if (death != 1) {
			action = getMaxQAction(s);

			// if (main.rnd.next() < 0.02) {
			// action = (int) (3 * main.rnd.next());
			// }
		} else {
			action = 2;
		}

		if (sales == 10000) {
			action = 1;
		}

		this.act(action);

		// if(strategy==0)
		// System.out.println(value[0][0]+"\t"+value[0][1]+"\t"+value[0][2]+"\t"+value[1][0]+"\t"+value[1][1]+"\t"+value[1][2]+"\t"+value[2][0]+"\t"+value[2][1]+"\t"+value[2][2]);
		//
	}

	// 状態stateにおいて、最大のQ値となる行動を返す
	public int getMaxQAction(int state) {
		double max = value[state][0];
		int maxA = 0;
		ArrayList<Integer> maxArray = new ArrayList<Integer>();

		for (int i = 0; i < value[0].length; i++) {
			if (max < value[state][i]) {
				max = value[state][i];
				maxA = i;
				maxArray = new ArrayList<Integer>();
				maxArray.add(maxA);
			} else if (max == value[state][i])
				maxArray.add(i);
		}

		if (maxArray.size() > 1) {
			int r = (int) (maxArray.size() * main.rnd.next());
			maxA = maxArray.get(r);
		}

		// System.out.println(value[0][0]+"\t"+value[0][1]+"\t"+value[0][2]+"\t"+value[1][0]+"\t"+value[1][1]+"\t"+value[1][2]+"\t"+value[2][0]+"\t"+value[2][1]+"\t"+value[2][2]+"\t"+maxA);
		return maxA;
	}

	private void act(int action) {

		int unit = 5;

		if (action == 0) {
			cost += unit;
		} else if (action == 1) {
			if (cost > 0) {
				cost -= unit;
			}
		} else {
		}

	}

	public void sell(int flatOrHierarchy) {
		if (flatOrHierarchy == 0) {
			sellNum++;
		} else {
			sellNum++;
			sellNumHie++;
		}

	}

	public int getSellNH() {
		return this.sellNumHie;
	}

	public int getDiscountFlat() {
		discountFlat = (int) (cost * prob1);
		return discountFlat;
	}

	public int getPointFlat() {
		pointFlat = (int) (cost * prob2);
		return pointFlat;
	}

	public int getDiscountHierarchy() {
		discountHierarchy = (int) (cost * prob3);
		return getDiscountFlat() + discountHierarchy;
	}

	public int getOnlyDiscountHierarchy() {
		return discountHierarchy;
	}

	public int getPointHierarchy() {
		pointHierarchy = (int) (cost * prob4);
		return getDiscountFlat() + pointHierarchy;
	}

	public int getOnlyPointHierarchy() {
		return pointHierarchy;
	}

	public double[][] getValue() {
		return value;
	}

	public int getSales() {
		return this.sales;
	}

	public int getSellN() {
		return this.sellNum;
	}

	public int getSalesFreq() {
		return this.salesFreq;
	}

	public int getProfit() {
		return this.profit;
	}

	public int getPreAction() {
		return this.preAction;
	}

	public double getCost() {
		return cost;
	}

	public double getProb1() {
		return prob1;
	}

	public double getProb2() {
		return prob2;
	}

	public double getProb3() {
		return prob1 + prob3;
	}

	public double getProb4() {
		return prob2 + prob4;
	}

	public int getBudget() {
		return budget;
	}

	public int[] getResponsiveStrategy(int num) {
		int[] list = new int[num];

		for (int i = 0; i < num; i++) {
			list[i] = getMaxQAction(i);
		}

		return list;
	}

	public int getAction() {
		return action;
	}
}
