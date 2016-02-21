import java.util.ArrayList;
import java.util.Random;


public class customer {

	private double[] prefCom = new double[main.comNum];
	private double[] prefComDiscount = new double[main.comNum];
	private int[] prefComPoint = new int[main.comNum];

	private double u;
	// parameta
	private double alpha = 0.8;
	private int mp1 = 100;
	private int mp2 = 2000;
	private double p1 = 0.8;
	private double p2 = 1.2;

	private int lockIn = 0;
	private int lockInCom = 100;
	private int threshold = 1000;
	private ArrayList<Integer> point = new ArrayList<Integer>();
	private ArrayList<Integer> purchaseNum = new ArrayList<Integer>();//期中の購買回数

	private double intercept = 0.3;
	private double high;

	private double lookDiscount;
	private double lookPoint;
	private int choice;

	//ポイントの保有上限、ここに達するとポイントを利用する
	private int maxPoint;

	private double budget = 0;

	private int[] flatOrHierarchy = new int[main.comNum];

	public customer(double intercep, int th) {

		high = intercep;
		threshold = th;

		u = 0;

		for (int i = 0; i < main.comNum; i++) {
			prefCom[i] = 0;
			prefComDiscount[i] = 0;
			prefComPoint[i] = 0;
		}
		for (int i = 0; i < main.comNum; i++) {
			point.add(0);
		}
		for (int i = 0; i < main.comNum; i++) {
			purchaseNum.add(0);
		}
		//maxPointは平均100、分散100の正規分布で与えられる
		Random  tmp = new Random();
		maxPoint = (int)(tmp.nextGaussian()*10)+100;

	}

	public void reset() {

		for (int i = 0; i < main.comNum; i++) {
			flatOrHierarchy[i] = 0;

		}

		if (lockIn == 1) {
			flatOrHierarchy[lockInCom] = 1;
		}
		lockIn = 0;
		lockInCom = 100;

//ポイントの利用はランダムになるよう修正
//		point.clear();
//		for (int i = 0; i < main.comArray.size(); i++) {
//			point.add(0);
//		}
		purchaseNum.clear();
		for (int i = 0; i < main.comArray.size(); i++) {
			purchaseNum.add(0);
		}
		u = 0;
	}

	public void action() {

		int com;

		com = choice();

		choice = com;
		buyAndLearn(com);
	}

	private int choice() {

		int com = 100;
		// if (prefCom[0] > prefCom[1]) {
		// com = 0;
		// } else if (prefCom[0] < prefCom[1]) {
		// com = 1;
		// } else {
		// com = (int) (2 * (main.rnd.next()));
		// }
		// if (lockIn == 0) {

		double[] discountValue = new double[main.comArray.size()];
		double[] pointValue = new double[main.comArray.size()];
		double[] costValue = new double[main.comArray.size()];

		for (int i = 0; i < main.comArray.size(); i++) {
			discountValue[i] = 0;
		}
		for (int i = 0; i < main.comArray.size(); i++) {
			if (flatOrHierarchy[i] == 1) {
				discountValue[i] = (double) main.comArray.get(i).getProb3();
			} else {
				discountValue[i] = (double) main.comArray.get(i).getProb1();
			}
		}
		for (int i = 0; i < main.comArray.size(); i++) {
			pointValue[i] = 0;
		}
		for (int i = 0; i < main.comArray.size(); i++) {
			if (flatOrHierarchy[i] == 1) {
				pointValue[i] = (double) main.comArray.get(i).getProb4();
			} else {
				pointValue[i] = (double) main.comArray.get(i).getProb2();
			}
		}

		for (int i = 0; i < main.comArray.size(); i++) {
			costValue[i] = (double) main.comArray.get(i).getCost();
		}

		double[] preference = new double[main.comArray.size()];
		double sumPreference = 0;
		for (int i = 0; i < main.comArray.size(); i++) {
			preference[i] = (calcPreference(discountValue[i], pointValue[i],
					point.get(i), costValue[i]));
//			 System.out.println(preference[i] +
//			 "\t"+discountValue[i]+"\t"+pointValue[i]+"\t"+point.get(i));
			sumPreference += (preference[i]);
		}

		// if (high == 2.8)
		// System.out.println(preference[0] / sumPreference);
		double numerator = 0;
		double rnd = main.rnd.next();
		for (int i = 0; i < main.comArray.size(); i++) {
			numerator += preference[i];
			if (rnd < numerator / sumPreference) {
				com = i;
				lookDiscount = discountValue[i] * (1 + costValue[i]);
				lookPoint = pointValue[i] * (1 + costValue[i]);
				u += preference[i];
				break;
			}
		}
		// System.out.println(preference[0] + "\t" +preference[1]+ "\t"
		// +com);
		// } else {
		// com = lockInCom;
		// }

		if (com == 100) {
			com = (int) (main.comArray.size() * main.rnd.next());
		}

		// if (main.rnd.next() < 0.05) {
		// com = 1;
		// }
		return com;
	}

	private double calcPreference(double discount, double point,
			int mountOfPoint, double cost) {

		double p = high * (double) mountOfPoint + intercept;

		// double preference = Math.log(cost + 1.1)
		// * ((double) discount + (double) point * p);
		double preference = (cost + 1)
				* ((double) discount + (double) point * p);
		// if(high==2.8)
		// System.out.println(Math.log(cost+1.1)+"\t"+discount + "\t" + point +
		// "\t" +preference);
		return preference;
	}

	private void buyAndLearn(int comNum) {

		main.comArray.get(comNum).sell(flatOrHierarchy[comNum]);

		point.set(comNum, point.get(comNum) + 1);
		if(point.get(comNum)>=this.maxPoint){
			point.set(comNum, 0);
		}

		purchaseNum.set(comNum, purchaseNum.get(comNum) + 1);


		if (lockIn == 0 && purchaseNum.get(comNum) > threshold) {
			lockIn = 1;
			lockInCom = comNum;
		}
	}

	public double getLookDiscount() {
		return lookDiscount;
	}

	public double getLookPoint() {
		return lookPoint;
	}

	public int getChoice() {
		return choice;
	}

	public int getPrefComPoint(int i) {
		return point.get(i);
	}

	public double getUtility() {
		return u;
	}

	public ArrayList<Integer> getPuchaseNum() {
		return purchaseNum;
	}

	public void setPuchaseNum(ArrayList<Integer> puchaseNum) {
		this.purchaseNum = puchaseNum;
	}

	public int getMaxPoint() {
		return maxPoint;
	}

	public void setMaxPoint(int maxPoint) {
		this.maxPoint = maxPoint;
	}

}