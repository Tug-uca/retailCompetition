import java.util.ArrayList;

public class main {

	// シミュレーション当たりのステップ数
	public static int simulationNum = 30000;
	// カスタマーエージェントの数
	public static int custNum = 100;
	// 一期あたりのステップ数
	public static int comBehaveSpan = 100;
	// 店舗エージェントの数
	public static int comNum = 3;
	// シミュレーションの実行回数
	public static int italationNum = 10000;

	public static customer[] custList = new customer[custNum];
	public static company[] comList = new company[comNum];// num0=TESCO,num1=Sainsbury's

	public static ArrayList<company> comArray = new ArrayList<company>();

	public static MersenneTwister rnd = new MersenneTwister((int) (10000 * Math.random()));

	public static void main(String[] args) {

		// 外部パラメータファイルからの入力
		inputPara input = new inputPara("paraOne.csv");
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

												comList[0] = new company(paraSet[roop1][0],
												paraSet[roop1][1], paraSet[roop1][2],
												paraSet[roop1][3], 0, 0, 0, 0, 0, 3);
										comList[1] = new company(paraSet[roop2][0],
												paraSet[roop2][1], paraSet[roop2][2],
												paraSet[roop2][3], 0, 0, 0, 0, 1, 3);



						//手動設定
						comList[0] = new company(0.5,
								0.5, 0, 0, 0, 0, 0, 0, 0, 3);
						comList[1] = new company(0.5,
								0, 0.5, 0, 0, 0, 0, 0, 1, 3);

						// comList[2] = new company(50, 50, 0, 0, 2, 3);
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
							//最適反応に関するログ
//							System.out.println(i / comBehaveSpan + "\t" + comList[0].getResponsiveStrategy(3)[0] + "\t"
//									+ comList[0].getResponsiveStrategy(3)[1] + "\t"
//									+ comList[0].getResponsiveStrategy(3)[1] + "\t"
//									+ comList[1].getResponsiveStrategy(3)[0] + "\t"
//									+ comList[1].getResponsiveStrategy(3)[1] + "\t"
//									+ comList[1].getResponsiveStrategy(3)[2]);

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
					
					
					System.out.print(roop);
					for(int i =1 ; i<simulationNum / comBehaveSpan;i=i+5){
						System.out.print("\t"+cost[i][0]);						
					}
					for(int i =1 ; i<simulationNum / comBehaveSpan;i=i+5){
						System.out.print("\t"+cost[i][1]);						
					}
					for(int i =1 ; i<simulationNum / comBehaveSpan;i=i+5){
						System.out.print("\t"+cost[i][2]);						
					}					System.out.print("\t"+((budget[simulationNum / comBehaveSpan-1][0]) / Math.abs(budget[simulationNum / comBehaveSpan-1][0])+1)/2);
					System.out.print("\t"+((budget[simulationNum / comBehaveSpan-1][1]) / Math.abs(budget[simulationNum / comBehaveSpan-1][1])+1)/2);
					System.out.print("\t"+((budget[simulationNum / comBehaveSpan-1][2]) / Math.abs(budget[simulationNum / comBehaveSpan-1][2])+1)/2);
					System.out.println("");
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

//				 if (comNum == 3) {
//				 for (int i = 0; i < simulationNum / comBehaveSpan; i++) {
//				 System.out.println(profit[i][0] / italationNum + "\t"
//				 + profit[i][1] / italationNum + "\t"
//				 + profit[i][2] / italationNum + "\t" + sales[i][0]
//				 + "\t" + sales[i][1] + "\t" + sales[i][2] + "\t"
//				 + cost[i][0] + "\t" + cost[i][1] + "\t" + cost[i][2]
//				 + "\t" + budget[i][0] + "\t" + budget[i][1] + "\t"
//				 + budget[i][2]);
//				 }
////				 System.out.println(a1 + "\t" + a2 + "\t" + a3 + "\t" + a4);
////				 System.out.println(b1 + "\t" + b2 + "\t" + b3 + "\t" + b4);
////				 System.out.println(c1 + "\t" + c2 + "\t" + c3 + "\t" + c4);
//				
//				 }
//				
//				 if (comNum == 2) {
//				
//				 for (int i = 0; i < simulationNum / comBehaveSpan; i++) {
//				 System.out.println(profit[i][0] / italationNum + "\t"
//				 + profit[i][1] / italationNum + "\t" + sales[i][0]
//				 + "\t" + sales[i][1] + "\t" + cost[i][0] + "\t"
//				 + cost[i][1] + "\t" + budget[i][0] + "\t"
//				 + budget[i][1] + "\t" + salesFreq[i][0] + "\t"
//				 + salesFreq[i][1] + "\t" + sumPreference[i]);
//				 }}
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
//				System.out.print(roop1 * paraSet.length + roop2 + "\t" + "/" + "\t" + paraSet.length * paraSet.length);
//				for (int i = 0; i < 4; i++) {
//					output[roop1 * paraSet.length + roop2][i] = paraSet[roop1][i];
//					System.out.print("\t" + paraSet[roop1][i]);
//				}
//				for (int j = 0; j < 4; j++) {
//					output[roop1 * paraSet.length + roop2][4 + j] = paraSet[roop2][j];
//					System.out.print("\t" + paraSet[roop2][j]);
//				}
//				for (int j = 0; j < CASE.length; j++) {
//					output[roop1 * paraSet.length + roop2][8 + j] = CASE[j];
//					System.out.print("\t" + CASE[j]);
//				}
//				// 撤退店舗の出力
//					output[roop1 * paraSet.length + roop2][8 + 7]=deadCount[0];
//					output[roop1 * paraSet.length + roop2][8 + 8]=deadCount[1];
//				System.out.print("\t" + deadCount[0]+"\t" + deadCount[1]);
//				System.out.print("\t" + corp);
//				System.out.println();
//
//				// 1試行の時のみ出力
//				if (paraSet.length == 1 && italationNum == 1) {
//					outputCSV.outputInt("customerBuyStoreHist", customerBuyStoreHist);
//					outputCSV.outputInt("action", action);
//
//					for (int k = 0; k < comNum; k++) {
//						outputCSV.outputInt("customerHavePoint" + k, customerHavePoint[k]);
//						outputCSV.outputDouble("actionEvaluateHist" + k, actionEvaluateHist[k]);
//
//					}
//
//				}
			}
		}

		outputCSV.outputDouble("output", output);

		// \add
	}
}

