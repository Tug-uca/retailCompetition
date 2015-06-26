import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class inputPara {


	String filename;
	double[][] paraSet;


	public inputPara(String file){
		filename=file;

		ArrayList<ArrayList> listList=new ArrayList<ArrayList>();
		ArrayList<Double> list =new ArrayList<Double>();
		// csvファイルの読み込み
		try {
			File csv = new File(filename); // CSVデータファイル

			BufferedReader br = new BufferedReader(new FileReader(csv));


			// 最終行まで読み込む
			String line = "";
			while ((line = br.readLine()) != null) {

				// 1行をデータの要素に分割
				StringTokenizer st = new StringTokenizer(line, ",");

				list = new ArrayList<Double>();

				while (st.hasMoreTokens()) {
					// 1行の各要素をタブ区切りで表示
					list.add(Double.parseDouble(st.nextToken()));
				}
				listList.add(list);
			}
			br.close();

		} catch (FileNotFoundException e) {
			// Fileオブジェクト生成時の例外捕捉
			e.printStackTrace();
		} catch (IOException e) {
			// BufferedReaderオブジェクトのクローズ時の例外捕捉
			e.printStackTrace();
		}

		//ArrayListから配列へ変形
		paraSet=new double[listList.size()][list.size()];

		for(int i=0;i<listList.size();i++){
			for(int j=0;j<list.size();j++){
				list=listList.get(i);
				paraSet[i][j]=list.get(j);
//System.out.println(paraSet[i][j]);
			}
		}
		
	}


	public double[][] getPara(){
		return paraSet;
	}
}
