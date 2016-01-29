
import java.util.ArrayList;

public class main {

	// シミュレーション当たりのステップ数
	public static int simulationNum = 100000;
	// カスタマーエージェントの数
	public static int custNum = 100;
	// 一期あたりのステップ数
	public static int comBehaveSpan = 100;
	// 店舗エージェントの数
	public static int comNum = 2;
	// シミュレーションの実行回数
	public static int italationNum = 10;

	public static customer[] custList = new customer[custNum];
	public static company[] comList = new company[comNum];// num0=TESCO,num1=Sainsbury's

	public static ArrayList<company> comArray = new ArrayList<company>();

	public static MersenneTwister rnd = new MersenneTwister((int) (10000 * Math.random()));

	public static void main(String[] args) {

		// 外部パラメータファイルからの入力
		inputPara input = new inputPara("para.csv");
		double[][] paraSet = input.getPara();

		// 結果ファイル出力の準備
		double[][] output = new double[paraSet.length * paraSet.length][4 * 2 + 7 + 2];
		outputCSV outputCSV = new outputCSV();

		// 上のこーどと重複？
		custList = new customer[custNum];
		comList = new company[comNum];// num0=TESCO,num1=Sainsbury's
		comArray = new ArrayList<company>();

		for (int roop1 = 0; roop1 < paraSet.length; roop1++) {
			for (int roop2 = 0; roop2 < paraSet.length; roop2++) {
				// \add
				// outputCSV output = new outputCSV();
				// int[][] behavior = new int[simulationNum][custNum];
				// 各企業の期ごとの利益の推移
				int[][] profit = new int[simulationNum / comBehaveSpan][comNum];
				// 各企業の期ごとの売り上げの推移
				int[][] sales = new int[simulationNum / comBehaveSpan][comNum];
				// 各企業の期ごとのbudget?の推移
				int[][] budget = new int[simulationNum / comBehaveSpan][comNum];
				// 各企業の期ごとのcost?の推移
				int[][] cost = new int[simulationNum / comBehaveSpan][comNum];
				// 各企業の期ごとのsalesFreq?の推移
				int[][] salesFreq = new int[simulationNum / comBehaveSpan][comNum];
				double[] sumPreference = new double[simulationNum / comBehaveSpan];
				int[][][] responsiveStrategyList = new int[simulationNum / comBehaveSpan][comNum][3];
				// 各企業のがとった行動の履歴
				int[][] action = new int[simulationNum / comBehaveSpan][comNum];
				// 各店舗の行動の評価値の履歴
				double[][][] actionEvaluateHist = new double[comNum][simulationNum / comBehaveSpan][9];

				// 各顧客のステップごとの購入店舗
				int[][] customerBuyStoreHist = new int[simulationNum][custNum];
				// 各顧客のステップ・店舗ごとの保有ポイント数
				int[][][] customerHavePoint = new int[comNum][simulationNum][custNum];

				int[] CASE = new int[7];
				int deadCom = -1;
				int[] deadCount = new int[comList.length];
				for (int i = 0; i < 7; i++) {
					CASE[i] = 0;
				}
				for(int i=0;i<comList.length;i++){
					deadCount[i]=0;
				}

				//協調の発生回数
				int corp =0;
				
				// double[][] custmerBehavior = new
				// double[simulationNum][custNum * 3];

				double custmerUtility = 0;
				int win1 = 0;
				int win2 = 0;
				int draw = 0;

				// 各店舗のステップごとの売り上げの推移
				int[][] salesStep = new int[simulationNum][comNum];

				double[][][][] value = new double[simulationNum / comBehaveSpan][comNum][3][3];
				for (int i = 0; i < value.length; i++) {
					for (int j = 0; j < value[0].length; j++) {
						for (int k = 0; k < value[0][0].length; k++) {
							for (int l = 0; l < value[0][0][0].length; l++) {
								value[i][j][k][l] = 0;
							}
						}
					}
				}

				// double[][] costProfit = new double[simulationNum][custNum];

				for (int i = 0; i < sales.length; i++) {
					for (int j = 0; j < sales[0].length; j++) {
						sales[i][j] = 0;
						profit[i][j] = 0;
						budget[i][j] = 0;
						salesFreq[i][j] = 0;
					}
					sumPreference[i] = 0;
				}
				for (int i = 0; i < cost.length; i++) {
					for (int j = 0; j < cost[0].length; j++) {
						cost[i][j] = 0;
					}
				}
				int a = 0;

				// シミュレーションの開始
				for (int roop = 0; roop < italationNum; roop++) {

					// 合計で１になる4つの数字を3店舗分生成
					comArray.clear();
					double a1 = rnd.next();
					double a2 = rnd.next();
					double a3 = rnd.next();
					double a4 = rnd.next();
					double sumA = a1 + a2 + a3 + a4;
					a1 = a1 / sumA;
					a2 = a2 / sumA;
					a3 = a3 / sumA;
					a4 = a4 / sumA;
					double b1 = rnd.next();
					double b2 = rnd.next();
					double b3 = rnd.next();
					double b4 = rnd.next();
					double sumB = b1 + b2 + b3 + b4;
					b1 = b1 / sumB;
					b2 = b2 / sumB;
					b3 = b3 / sumB;
					b4 = b4 / sumB;
					double c1 = rnd.next();
					double c2 = rnd.next();
					double c3 = rnd.next();
					double c4 = rnd.next();
					double sumC = c1 + c2 + c3 + c4;
					c1 = c1 / sumC;
					c2 = c2 / sumC;
					c3 = c3 / sumC;
					c4 = c4 / sumC;

					// カスタマーエージェントの初期化
					for (int i = 0; i < custNum; i++) {
						if (i < 0) {
							custList[i] = new customer(0.02, 60);
						} else if (i < 100) {
							custList[i] = new customer(0.009, 60);
						} else {
							custList[i] = new customer(0, 60);
						}
					}

					// if (comNum == 2) {
					// comList[0] = new
					// company(0.063652287,0.272642587,0.110664001,0.553041126,0,
					// 0, 0,
					// 0, 0, 3);
					// comList[1] = new
					// company(0.277785522,0.305254168,0.138107472,0.278852838,
					// 0, 0, 0,
					// 0, 1, 3);
					// // comList[2] = new company(50, 50, 0, 0, 2, 3);
					// comArray.add(comList[0]);
					// comArray.add(comList[1]);
					// }

//					// 店舗エージェントの初期化
//					if (comNum == 2) {
//						comList[0] = new company(paraSet[roop1][0], paraSet[roop1][1], paraSet[roop1][2],
//								paraSet[roop1][3], 0, 0, 0, 0, 0, 3);
//						comList[1] = new company(paraSet[roop2][0], paraSet[roop2][1], paraSet[roop2][2],
//								paraSet[roop2][3], 0, 0, 0, 0, 1, 3);
//						comArray.add(comList[0]);
//						comArray.add(comList[1]);
//					}

					// 店舗エージェントの初期化（戦略の手動設定用）
					if (comNum == 2) {
						comList[0] = new company(0.75,0,0.25,0, 0, 0, 0, 0, 0, 3);
						comList[1] = new company(0.25,0.25,0,0.5, 0, 0, 0, 0, 1, 3);
						comArray.add(comList[0]);
						comArray.add(comList[1]);
					}


					if (comNum == 3) {
						comList[0] = new company(a1, a2, a3, a4, 0, 0, 0, 0, 0, 3);
						comList[1] = new company(b1, b2, b3, b4, 0, 0, 0, 0, 1, 3);
						comList[2] = new company(c1, c2, c4, c4, 0, 0, 0, 0, 2, 3);
						comArray.add(comList[0]);
						comArray.add(comList[1]);
						comArray.add(comList[2]);
					}

					// シミュレーション一回当たりのループ開始
					for (int i = 0; i < simulationNum; i++) {

						// System.out.println(i);

						// 毎ステップのカスタマーの行動
						for (int j = 0; j < custList.length; j++) {
							custList[j].action();
							// custmerBehavior[i][3*j]=custList[j].getChoice();
							// custmerBehavior[i][3*j+1]=custList[j].getLookDiscount();
							// custmerBehavior[i][3*j+2]=custList[j].getLookPoint();

							// 各顧客のステップごとの購入店舗と
							// 各顧客のステップ・店舗ごとの保有ポイント数を記録
							if (paraSet.length == 1 && italationNum == 1) {
								customerBuyStoreHist[i][j] = custList[j].getChoice();
								for (int k = 0; k < comArray.size(); k++) {
									customerHavePoint[k][i][j] = custList[j].getPrefComPoint(k);
								}
							}

						}
						// System.out.println(custList[0].getPrefComPoint(0)+"\t"+custList[0].getPrefComPoint(1));

						// 期ごとの店舗の行動
						if (i % comBehaveSpan == 0 && i != 0) {
							// System.out.println(comList[0].getSellNH()+"\t"+comList[1].getSellNH());

							for (int j = 0; j < comList.length; j++) {

								comList[j].calcAcounting();

								// add profit,sales to list
								sales[i / comBehaveSpan][j] = comList[j].getSales();
								budget[i / comBehaveSpan][j] = comList[j].getBudget();
								salesFreq[i / comBehaveSpan][j] += comList[j].getSalesFreq();
								profit[i / comBehaveSpan][j] = comList[j].getProfit();
								cost[i / comBehaveSpan][j] = (int) comList[j].getCost();

							}

							for (int j = 0; j < custList.length; j++) {
								sumPreference[i / comBehaveSpan] += custList[j].getUtility() / comBehaveSpan;
								custList[j].reset();
							}

							for (int j = 0; j < comList.length; j++) {

								comList[j].dicisionMaking();
								for (int k = 0; k < value[0][0].length; k++) {
									for (int l = 0; l < value[0][0][0].length; l++) {
										value[i / comBehaveSpan][j][k][l] += comList[j].getValue()[k][l];
									}
								}
								action[i / comBehaveSpan][j] = comList[j].getAction();
								responsiveStrategyList[i / comBehaveSpan][j] = comList[j].getResponsiveStrategy(3);

								// 各店舗の行動の評価値の履歴を記録
								if (paraSet.length == 1 && italationNum == 1) {
									for (int l = 0; l < 9; l++) {
										actionEvaluateHist[j][i / comBehaveSpan][l] = comList[j].getValue()[l / 3][l
												% 3];
									}
								}
								// if (j == 0) {
								// System.out.println(comList[j].getValue()[0][0]
								// + "\t" + comList[j].getValue()[0][1] + "\t"
								// + comList[j].getValue()[0][2] + "\t"
								// + comList[j].getValue()[1][0] + "\t"
								// + comList[j].getValue()[1][1] + "\t"
								// + comList[j].getValue()[1][2] + "\t"
								// + comList[j].getValue()[2][0] + "\t"
								// + comList[j].getValue()[2][1] + "\t"
								// + comList[j].getValue()[2][2]);
								// System.out.println(value[i /
								// comBehaveSpan][j][0][0]
								// + "\t"
								// + value[i / comBehaveSpan][j][0][1] + "\t"
								// + value[i / comBehaveSpan][j][0][2] + "\t"
								// + value[i / comBehaveSpan][j][1][0] + "\t"
								// + value[i / comBehaveSpan][j][1][1] + "\t"
								// + value[i / comBehaveSpan][j][1][2] + "\t"
								// + value[i / comBehaveSpan][j][2][0] + "\t"
								// + value[i / comBehaveSpan][j][2][1] + "\t"
								// + value[i / comBehaveSpan][j][2][2]);
								// }
							}

						}
						for (int j = 0; j < comList.length; j++) {
							// add profit,sales to list
							salesStep[i][j] += comList[j].getSellN();
						}

					}
					if ((double) comList[0].getSales()
							/ (double) (comList[0].getSales() + comList[1].getSales()) > 0.9) {
						win1++;
					} else if ((double) comList[1].getSales()
							/ (double) (comList[0].getSales() + comList[1].getSales()) > 0.9) {
						win2++;
					} else {
						draw++;
					}

					deadCom = -1;
					int deadTiming = -1;
					int finished = -1;
					int winCom = -1;

					int currCase = -1;
					int uCount = 0;
					int dCount = 0;
					int mCount = 0;

					int span = 20;

					int shareOOI = -1;

					// for(int i=0;i<simulationNum / comBehaveSpan;i++){
					// System.out.println(sales[i][0]+"\t"+sales[i][1]);
					// }

					if (sales[simulationNum / comBehaveSpan - 1][0] == 0) {
						deadCom = 0;
						winCom = 1;
						finished = 1;
					} else if (sales[simulationNum / comBehaveSpan - 1][1] == 0) {
						deadCom = 1;
						winCom = 0;
						finished = 1;
					} else {
						finished = 0;
					}

					if (finished == 1) {
						for (int i = simulationNum / comBehaveSpan - 1; i >= 0; i--) {
							if (sales[i][deadCom] != 0) {
								deadTiming = i;
								if (deadCom == 0 && sales[i][0] > sales[i][1]) {
									shareOOI = 1;
									break;
								} else if (deadCom == 0 && sales[i][1] > sales[i][0]) {
									shareOOI = 0;
									break;
								}

								if (deadCom == 1 && sales[i][0] > sales[i][1]) {
									shareOOI = 0;
									break;
								} else if (deadCom == 1 && sales[i][1] > sales[i][0]) {
									shareOOI = 1;
									break;
								}
							}
						}

						// ケースの判定
						if (deadTiming < 100) {
							CASE[6]++;
							currCase = 7;
						} else {
							if (shareOOI == 1) {
								for (int i = 0; i < span; i++) {
									if (action[deadTiming - i][deadCom] == 0) {
										uCount++;
									} else if (action[deadTiming - i][deadCom] == 1) {
										dCount++;
									} else if (action[deadTiming - i][deadCom] == 2) {
										mCount++;
									}
								}
								if (uCount > dCount && uCount > mCount) {
									CASE[0]++;
									currCase = 1;
								} else {
									CASE[1]++;
									currCase = 2;
								}
							} else if (shareOOI == 0) {
								for (int i = 0; i < span; i++) {
									if (action[deadTiming - i][deadCom] == 0) {
										uCount++;
									} else if (action[deadTiming - i][deadCom] == 1) {
										dCount++;
									} else if (action[deadTiming - i][deadCom] == 2) {
										mCount++;
									}
								}
								if (uCount > dCount && uCount > mCount) {
									CASE[2]++;
									currCase = 3;
								} else {
									CASE[3]++;
									currCase = 4;
								}
							}

						}

					} else if (finished == 0) {
						for (int j = 0; j < 2; j++) {
							for (int i = 0; i < span; i++) {

								if (action[simulationNum / comBehaveSpan - 1 - i][j] == 0) {
									uCount++;
								} else if (action[simulationNum / comBehaveSpan - 1 - i][j] == 1) {
									dCount++;
								} else if (action[simulationNum / comBehaveSpan - 1 - i][j] == 2) {
									mCount++;
								}

							}
							if (mCount < dCount || mCount < uCount) {
								CASE[5]++;
								currCase = 6;
								break;
							}
							if (j == 1) {
								CASE[4]++;
								currCase = 5;
							}
						}
					}

					// 撤退店舗のカウント
					if(deadCom!=-1)
					deadCount[deadCom]++;

					//協調回数のカウント
					for(int i=0;i<action.length-1;i++){
						if(action[i][0]==2&&action[i][1]==2&&action[i+1][0]==2&&action[i+1][1]==2){
							corp++;
							break;
						}
					}
					
					
					// System.out.println(roop + "\t"+ currCase + "\t"
					// +deadTiming);

					// if(currCase==6){
					// for (int i = 0; i < simulationNum / comBehaveSpan; i++) {
					// System.out.println(profit[i][0] / italationNum + "\t"
					// + profit[i][1] / italationNum + "\t" + sales[i][0]
					// + "\t" + sales[i][1] + "\t" + cost[i][0] + "\t"
					// + cost[i][1] + "\t" + budget[i][0] + "\t"
					// + budget[i][1] + "\t" + salesFreq[i][0] + "\t"
					// + salesFreq[i][1] + "\t" + sumPreference[i]);
					// }}

					for (int i = 0; i < sumPreference.length; i++) {
						custmerUtility += sumPreference[i];
					}
					custmerUtility = custmerUtility / sumPreference.length;

					// for (int i = 0; i < simulationNum / comBehaveSpan; i++) {
					// System.out.println(sales[i][0] + "\t" + sales[i][1]);
					// }
					// a += sales[(simulationNum / comBehaveSpan) - 1][0];
				}

				// System.out.println(a);
				// for (int i = 0; i < sales.length; i++) {
				// System.out.println(sales[i][0] + "\t" + sales[i][1]);
				// }
				// for (int i = 0; i < cost.length; i++) {
				// System.out.println(cost[i][0] + "\t" + cost[i][1] + "\t"
				// + cost[i][2] + "\t" + cost[i][3] + "\t" + cost[i][4] + "\t"
				// + cost[i][5] + "\t" + cost[i][6] + "\t" + cost[i][7]);
				// }

				// for (int i = 0; i < salesStep.length; i++) {
				// System.out.println(salesStep[i][0] + "\t" + salesStep[i][1]);
				// }
				// for (int i = 0; i < simulationNum / comBehaveSpan; i++) {
				// System.out.println(profit[i][0] / italationNum + "\t"
				// + profit[i][1] / italationNum);
				// }

				// if (comNum == 3) {
				// for (int i = 0; i < simulationNum / comBehaveSpan; i++) {
				// System.out.println(profit[i][0] / italationNum + "\t"
				// + profit[i][1] / italationNum + "\t"
				// + profit[i][2] / italationNum + "\t" + sales[i][0]
				// + "\t" + sales[i][1] + "\t" + sales[i][2] + "\t"
				// + cost[i][0] + "\t" + cost[i][1] + "\t" + cost[i][2]
				// + "\t" + budget[i][0] + "\t" + budget[i][1] + "\t"
				// + budget[i][2]);
				// }
				// System.out.println(a1 + "\t" + a2 + "\t" + a3 + "\t" + a4);
				// System.out.println(b1 + "\t" + b2 + "\t" + b3 + "\t" + b4);
				// System.out.println(c1 + "\t" + c2 + "\t" + c3 + "\t" + c4);
				//
				// }
				//
				// if (comNum == 2) {
				//
				// for (int i = 0; i < simulationNum / comBehaveSpan; i++) {
				// System.out.println(profit[i][0] / italationNum + "\t"
				// + profit[i][1] / italationNum + "\t" + sales[i][0]
				// + "\t" + sales[i][1] + "\t" + cost[i][0] + "\t"
				// + cost[i][1] + "\t" + budget[i][0] + "\t"
				// + budget[i][1] + "\t" + salesFreq[i][0] + "\t"
				// + salesFreq[i][1] + "\t" + sumPreference[i]);
				// }
				//
				// for (int i = 0; i < responsiveStrategyList.length; i++) {
				// for (int j = 0; j < responsiveStrategyList[i].length; j++) {
				// for (int k = 0; k < responsiveStrategyList[i][j].length; k++)
				// {
				// System.out
				// .print(responsiveStrategyList[i][j][k] + "\t");
				// }
				// }
				// System.out.println();
				// }
				//
				// for (int i = 0; i < value.length; i++) {
				// for (int j = 0; j < value[0].length; j++) {
				// for (int k = 0; k < value[0][0].length; k++) {
				// for (int l = 0; l < value[0][0][0].length; l++) {
				// System.out.print(value[i][j][k][l] + "\t");
				// }
				// }
				// }
				//
				// System.out.println();
				// }
				//
				// System.out.println(win1 + "\t" + win2 + "\t" + draw + "\t"
				// + custmerUtility / italationNum);
				// System.out.println(a1 + "\t" + a2 + "\t" + a3 + "\t" + a4);
				// System.out.println(b1 + "\t" + b2 + "\t" + b3 + "\t" + b4);
				// }
				// output.outputDouble("behavior", custmerBehavior);
				// for(int i=0;i<7;i++){
				// System.out.print(CASE[i]+"\t");
				// }
				// add
				// output

				// 結果の出力
				System.out.print(roop1 * paraSet.length + roop2 + "\t" + "/" + "\t" + paraSet.length * paraSet.length);
				for (int i = 0; i < 4; i++) {
					output[roop1 * paraSet.length + roop2][i] = paraSet[roop1][i];
					System.out.print("\t" + paraSet[roop1][i]);
				}
				for (int j = 0; j < 4; j++) {
					output[roop1 * paraSet.length + roop2][4 + j] = paraSet[roop2][j];
					System.out.print("\t" + paraSet[roop2][j]);
				}
				for (int j = 0; j < 7; j++) {
					output[roop1 * paraSet.length + roop2][8 + j] = CASE[j];
					System.out.print("\t" + CASE[j]);
				}
				// 撤退店舗の出力
					output[roop1 * paraSet.length + roop2][8 + 7]=deadCount[0];
					output[roop1 * paraSet.length + roop2][8 + 8]=deadCount[1];
				System.out.print("\t" + deadCount[0]+"\t" + deadCount[1]);
				System.out.print("\t" + corp);
				System.out.println();

				// 1試行の時のみ出力
				if (paraSet.length == 1 && italationNum == 1) {
					outputCSV.outputInt("customerBuyStoreHist", customerBuyStoreHist);
					outputCSV.outputInt("action", action);

					for (int k = 0; k < comNum; k++) {
						outputCSV.outputInt("customerHavePoint" + k, customerHavePoint[k]);
						outputCSV.outputDouble("actionEvaluateHist" + k, actionEvaluateHist[k]);

					}

				}
			}
		}

		outputCSV.outputDouble("output", output);

		// \add
	}
}

class customer {

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

	// ロックインされていると1,されていないと0
	private int lockIn = 0;
	// ロックインされているとき、どの店にロックインされているか
	private int lockInCom = 100;
	// ロックインされるポイント数の基準
	private int threshold = 1000;
	// 店ごとの保有ポイント数
	private ArrayList<Integer> point = new ArrayList<Integer>();

	private double intercept = 0.3;
	private double high;

	private double lookDiscount;
	private double lookPoint;
	private int choice;

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

	}

	public void reset() {

		for (int i = 0; i < main.comNum; i++) {
			flatOrHierarchy[i] = 0;

		}

		// 先期ロックインされていれば優良顧客化
		if (lockIn == 1) {
			flatOrHierarchy[lockInCom] = 1;
		}
		lockIn = 0;
		lockInCom = 100;

		point.clear();
		for (int i = 0; i < main.comArray.size(); i++) {
			point.add(0);
		}
		u = 0;
	}

	// 顧客の購買行動、mainから呼び出される
	public void action() {

		int com;

		com = choice();

		choice = com;
		buyAndLearn(com);
	}

	// 購入店舗の選択
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

		// 店舗ごとの値引きの割合の初期化と取得
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

		// 店舗ごとのポイントの割合の初期化と取得
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

		// 店舗ごとの販促費の総額を取得
		for (int i = 0; i < main.comArray.size(); i++) {
			costValue[i] = (double) main.comArray.get(i).getCost();
		}

		// 値引きの割合、ポイントの割合、販促費の総額をもとに、店舗ごとの選好を計算
		double[] preference = new double[main.comArray.size()];
		double sumPreference = 0;
		for (int i = 0; i < main.comArray.size(); i++) {
			preference[i] = (calcPreference(discountValue[i], pointValue[i], point.get(i), costValue[i]));
			// System.out.println(preference[i] +
			// "\t"+discountValue[i]+"\t"+pointValue[i]+"\t"+point.get(i));
			sumPreference += (preference[i]);
		}

		// ルーレット選択を実施
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

//		//(選好が最大の店舗を選択、同一の時はランダム、2店舗の時のみ対応)
//		try{
//		if(preference[0]>preference[1]){
//			com=0;
//		}else if(preference[0]<preference[1]){
//			com=1;
//		}else if(main.rnd.next()<0.5){
//			com=0;
//		}else{
//			com=1;
//		}
//		}catch(ArrayIndexOutOfBoundsException e){
//			com=0;
//		}
		// System.out.println(preference[0] + "\t" +preference[1]+ "\t"
		// +com);
		// } else {
		// com = lockInCom;
		// }

		// このコードをなくしたらどうなる？
		if (com == 100) {
			com = (int) (main.comArray.size() * main.rnd.next());
		}

		// if (main.rnd.next() < 0.05) {
		// com = 1;
		// }
		return com;
	}

	// 値引きの割合、ポイントの割合、販促費の総額をもとに、店舗ごとの選好を計算
	private double calcPreference(double discount, double point, int mountOfPoint, double cost) {

		double p = high * (double) mountOfPoint + intercept;

		// double preference = Math.log(cost + 1.1)
		// * ((double) discount + (double) point * p);
		double preference = (cost + 1) * ((double) discount * (1 - p) + (double) point * p);

		// if(high==2.8)
		// System.out.println(Math.log(cost+1.1)+"\t"+discount + "\t" + point +
		// "\t" +preference);
		return preference;
	}

	private void buyAndLearn(int comNum) {

		// 商品の購入
		main.comArray.get(comNum).sell(flatOrHierarchy[comNum]);

		// ポイントを入手し、一定ポイント以上ならロックイン状態
		// ただし、優良顧客となるのは次期から
		point.set(comNum, point.get(comNum) + 1);
		if (lockIn == 0 && point.get(comNum) > threshold) {
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
}

class company {

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

	public company(double p1, double p2, double p3, double p4, int df, int pf, int dh, int ph, int st, int sce) {
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

	// 当期利益の計算
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

	// その期の会計を計算と学習
	public void calcAcounting() {

		preSales = sales;
		preProfit = profit;
		sales = sellNum;
		salesFreq = sellNumHie;
		profit = calcProfit();
		sellNum = 0;
		sellNumHie = 0;

		// 内部留保を計算し、ゼロで撤退
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

		// 会計の結果をもとに、学習を行う
		if (death != 1) {

			// Q値の更新
			// 下の行はたぶんいらない
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

	// Q値が最大の行動をactionに代入する、sには先期の行動を代入する
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
				s = 3 * main.comList[1].getPreAction() + main.comList[2].getPreAction();
			} else if (strategy == 1) {
				s = 3 * main.comList[2].getPreAction() + main.comList[0].getPreAction();
			} else {
				s = 3 * main.comList[0].getPreAction() + main.comList[1].getPreAction();
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

	// actionに応じて、値上げ、値下げ、維持を行う
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

	// 商品の販売、カスタマーから呼び出される
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

	// 反応戦略を返す
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
