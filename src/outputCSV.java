import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class outputCSV {

	private int mo;
	private int d;
	private int h;
	private int m;
	private File newdir;
	
	public outputCSV() {

		// 日時取得
		Calendar now = Calendar.getInstance(); // インスタンス化
		mo = now.get(Calendar.MONTH) + 1;// 月を取得
		d = now.get(Calendar.DATE); // 現在の日を取得
		h = now.get(Calendar.HOUR_OF_DAY);// 時を取得
		m = now.get(Calendar.MINUTE); // 分を取得


		newdir = new File("ex" + mo + "-" + d + "-" + h + "-" + m);
		int i = 0;
		while (newdir.exists() ) {
			i += 1;
			newdir = new File("ex" + mo + "-" + d + "-" + h + "-" + m + "-" + i);
		}
		System.out.println(newdir.toString());

		newdir.mkdir();

	}

	public void outputDouble(String filename, double[][] data) {

		// csvに書き込みaol
		try {
			File csv = new File(newdir.toString() + "/"
					+ newdir.toString() + filename
					+ ".csv"); // CSVデータファイル
			// 追記モード
			BufferedWriter bw = new BufferedWriter(new FileWriter(csv, true));
			// 新たなデータ行の追加

			for (int j = 0; j < data.length; j++) {
				for (int k = 0; k < data[j].length; k++) {
					bw.write(data[j][k] + ",");
				}
				bw.newLine();

			}

			bw.close();
		} catch (FileNotFoundException e) {
			// Fileオブジェクト生成時の例外捕捉
			e.printStackTrace();
		} catch (IOException e) {
			// BufferedWriterオブジェクトのクローズ時の例外捕捉
			e.printStackTrace();
		}
	}

	public void outputInt(String filename, int[][] data) {

		// csvに書き込みaol
		try {
			File csv = new File(newdir.toString() + "/"
					+ newdir.toString() + filename
					+ ".csv"); // CSVデータファイル
			// 追記モード
			BufferedWriter bw = new BufferedWriter(new FileWriter(csv, true));
			// 新たなデータ行の追加

			for (int j = 0; j < data.length; j++) {
				for (int k = 0; k < data[j].length; k++) {
					bw.write(data[j][k] + ",");
				}
				bw.newLine();

			}

			bw.close();
		} catch (FileNotFoundException e) {
			// Fileオブジェクト生成時の例外捕捉
			e.printStackTrace();
		} catch (IOException e) {
			// BufferedWriterオブジェクトのクローズ時の例外捕捉
			e.printStackTrace();
		}
	}

	public void outputParamater(ArrayList<String> name, ArrayList data) {

		// csvに書き込みaol
		try {
			File csv = new File(newdir.toString() + "/"
					+ newdir.toString() + "parameter.csv"); // CSVデータファイル
			// 追記モード
			BufferedWriter bw = new BufferedWriter(new FileWriter(csv, true));
			// 新たなデータ行の追加

			for (int j = 0; j < name.size(); j++) {

				bw.write(name.get(j) + ",");
				bw.write(data.get(j) + ",");

				bw.newLine();

			}

			bw.close();
		} catch (FileNotFoundException e) {
			// Fileオブジェクト生成時の例外捕捉
			e.printStackTrace();
		} catch (IOException e) {
			// BufferedWriterオブジェクトのクローズ時の例外捕捉
			e.printStackTrace();
		}

	}
}
