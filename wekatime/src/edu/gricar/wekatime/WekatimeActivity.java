package edu.gricar.wekatime;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;
import android.app.TabActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import android.widget.TabHost.TabContentFactory;

public class WekatimeActivity extends TabActivity {
	ApplicationWekatime app;
	Instances data;
	BufferedReader buf;
	private TabHost tabHost;
	TextView tv1, tv2, tv3;
	Chronometer chrono;
	Menu nMenu;
	Boolean delopoteka = false;
	String datoteka = "/sdcard/vreme.arff";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (ApplicationWekatime) getApplication();
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("j48").setContent(new TabContentFactory() {
			public View createTabContent(String arg0) {
				View a = (View)findViewById(R.id.tab1);
				return a;
			}
		}));

		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("IBk").setContent(new TabContentFactory() {
			public View createTabContent(String arg0) {
				View a = (View)findViewById(R.id.tab2);
				return a;
			}
		}));

		tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("NaiveBayes").setContent(new TabContentFactory() {
			public View createTabContent(String arg0) {
				View a = (View)findViewById(R.id.tab3);
				return a;
			}
		}));

		tabHost.setCurrentTab(1);
		tabHost.setCurrentTab(2);
		tabHost.setCurrentTab(0);

		tv1 = (TextView) findViewById(R.id.textView1);
		tv2 = (TextView) findViewById(R.id.textView2);
		tv3 = (TextView) findViewById(R.id.textView3);

		tv1.setMovementMethod(new ScrollingMovementMethod());
		tv2.setMovementMethod(new ScrollingMovementMethod());
		tv3.setMovementMethod(new ScrollingMovementMethod());

		chrono = (Chronometer) findViewById(R.id.chronometer1);
		Toast.makeText(this, "Izberi menu!", Toast.LENGTH_LONG).show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		nMenu = menu;
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.nastavitve_vreme, nMenu);
		return true;

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.Klasificiraj:
			try {
				if (delopoteka == false){
					delopoteka = true;
					chrono.setBase(SystemClock.elapsedRealtime());
					chrono.start();
					OnStart();
				}
				else
					Toast.makeText(this, "Poèakaj da se delo konèa!", Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		case R.id.arffgen:
			try {
				if (delopoteka == false){

					delopoteka = true;
					arffgenerator();
				}
				else
					Toast.makeText(this, "Poèakaj da se delo konèa!", Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;

		default:// Generic catch all for all the other menu resources
			if (!item.hasSubMenu()) {
				Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
				return true;
			}
			break;
		}

		return false;
	}

	void klasifikacijaLauncher() throws Exception{
		podatki();
		app.shrani[0] = j48();
		app.shrani[1] = ibk();
		app.shrani[2] = naivebayes();	
	}

	void podatki() throws Exception{
		buf = new BufferedReader(new FileReader(datoteka));
		data = new Instances(buf);
		buf.close();
		data.setClassIndex(data.numAttributes() - 1);
	}
	
	String j48() throws Exception{
		String[] optionj48 = new String[4];
		optionj48[0] = "-C";
		optionj48[1] = "0.25";
		optionj48[2] = "-M";
		optionj48[3] = "2";

		J48 j48 = new J48();
		j48.setUnpruned(true);
		//j48.setUnpruned(false);
		j48.setOptions(optionj48);
		FilteredClassifier fc = new FilteredClassifier();
		//fc.setFilter(rm);
		fc.setClassifier(j48);
		fc.buildClassifier(data);

		String[] options = new String[2];
		options[0] = "-t";
		options[1] = datoteka;

		String miki = j48.toSummaryString();
		String piki = data.toSummaryString();

		Evaluation eval = new Evaluation(data);
		eval.crossValidateModel(j48, data, 10, new Random(1));

		return eval.toSummaryString() + "\n" + piki + "\n" + miki + "\n" + Evaluation.evaluateModel(new J48(), options);
	}

	String ibk() throws Exception{
		String[] optionibk = new String[6];
		optionibk[0] = "-K";
		optionibk[1] = "1";
		optionibk[2] = "-W";
		optionibk[3] = "0";
		optionibk[4] = "-A";
		optionibk[5] = "weka.core.neighboursearch.LinearNNSearch -A \"weka.core.EuclideanDistance -R first-last\"";

		IBk ibk = new IBk();
		ibk.setOptions(optionibk);

		ibk.buildClassifier(data);
		String[] options = new String[2];
		options[0] = "-t";
		options[1] = datoteka;

		Evaluation eval = new Evaluation(data);
		eval.crossValidateModel(ibk, data, 10, new Random(1));

		return eval.toSummaryString() + "\n" + data.toSummaryString();
	}

	String naivebayes () throws Exception{
		NaiveBayes nb = new NaiveBayes();
		nb.buildClassifier(data);
		String[] options = new String[2];
		options[0] = "-t";
		options[1] = datoteka;

		Evaluation eval = new Evaluation(data);
		eval.crossValidateModel(nb, data, 10, new Random(1));

		return eval.toSummaryString() + "\n" + data.toSummaryString();
	}

	public class BackgroundAsyncTask extends AsyncTask<Void, Integer, String> {

		@Override
		protected String doInBackground(Void... params) {

			try {
				klasifikacijaLauncher();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(true);
		}

		protected void onPostExecute(String arg) {
			setProgressBarIndeterminateVisibility(false);
			tv1.setText(app.shrani[0]);
			tv2.setText(app.shrani[1]);
			tv3.setText(app.shrani[2]);
			chrono.stop();

			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			long mili = 1000;
			v.vibrate(mili);
			delopoteka = false;
		}
	}

	public void OnStart(){
		BackgroundAsyncTask mt = new BackgroundAsyncTask();
		mt.execute();
	}

	void arffgenerator(){
		try{
			setProgressBarIndeterminateVisibility(true);
			File datotekaizpis = new File(datoteka);
			FileWriter writer = new FileWriter(datotekaizpis);
			BufferedWriter out = new BufferedWriter(writer);

			out.write("@relation vreme" + "\n" + "\n");

			String[] opis = {"hladno", "toplo", "vetrovno", "dezevno", "snezi"};
			int temperatura = 0;
			String[] veter = {"0", "5", "10", "20", "50"};
			String[] moznost_padavin = {"0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"};

			String zdruzi = "";
			zdruzi = zdruzi + opis[0];
			for (int i=1; i<opis.length; i++){
				if (i<opis.length)
					zdruzi = zdruzi + ",";
				zdruzi = zdruzi + opis[i];
			}
			out.write("@attribute opis {" + zdruzi + "}" + "\n");

			out.write("@attribute temperatura NUMERIC" + "\n");

			zdruzi = "";
			zdruzi = zdruzi + veter[0];
			for (int i=1; i<veter.length; i++){
				if (i<veter.length)
					zdruzi = zdruzi + ",";
				zdruzi = zdruzi + veter[i];
			}
			out.write("@attribute veter {" + zdruzi + "}" + "\n");

			zdruzi = "";
			zdruzi = zdruzi + moznost_padavin[0];
			for (int i=1; i<moznost_padavin.length; i++){
				if (i<moznost_padavin.length)
					zdruzi = zdruzi + ",";
				zdruzi = zdruzi + moznost_padavin[i];
			}
			out.write("@attribute moznost_padavin {" + zdruzi + "}" + "\n" +
					"\n" + "@data" + "\n");

			Random r = new Random();
			for (int i=0; i<1500; i++){
				temperatura = r.nextInt(35);
				out.write(opis[r.nextInt(opis.length)] + "," + temperatura +
						"," + veter[r.nextInt(veter.length)] + ","
						+ moznost_padavin[r.nextInt(moznost_padavin.length)] + "\n");
			}

			out.close();
			delopoteka = false;
			setProgressBarIndeterminateVisibility(false);
			Toast.makeText(this, "Datoteka shranjena!", Toast.LENGTH_LONG).show();
		}

		catch(Exception e){
			e.printStackTrace();
		}
	}
}