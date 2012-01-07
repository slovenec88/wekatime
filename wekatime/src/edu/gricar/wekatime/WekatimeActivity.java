package edu.gricar.wekatime;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

import android.app.Application;
import android.app.TabActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;
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


		try {
			OnStart();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void klasifikacijaLauncher() throws Exception{
		podatki();
		app.shrani[0] = j48();
		app.shrani[1] = ibk();
		app.shrani[2] = naivebayes();	
	}

	void podatki() throws Exception{
		buf = new BufferedReader(new FileReader("/sdcard/car.arff"));
		data = new Instances(buf);
		buf.close();
		data.setClassIndex(data.numAttributes() - 1);

	}
	String j48() throws Exception{
		System.out.println("j48:");

		String[] optionj48 = new String[4];
		optionj48[0] = "-C";
		optionj48[1] = "0.25";
		optionj48[2] = "-M";
		optionj48[3] = "2";

		J48 j48 = new J48();
		j48.setUnpruned(true); 
		j48.setOptions(optionj48);
		FilteredClassifier fc = new FilteredClassifier();
		//fc.setFilter(rm);
		fc.setClassifier(j48);
		fc.buildClassifier(data);

		System.out.println(data.toSummaryString());
		System.out.println(j48.toSummaryString());

		String[] options = new String[2];
		options[0] = "-t";
		options[1] = "/sdcard/car.arff";


		Evaluation eval = new Evaluation(data);
		eval.crossValidateModel(j48, data, 10, new Random(1));

		System.out.println(eval.toSummaryString());

		return eval.toSummaryString();
	}

	String ibk() throws Exception{
		System.out.println("IBK:");

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

		System.out.println(data.toSummaryString());

		String[] options = new String[2];
		options[0] = "-t";
		options[1] = "/sdcard/car.arff";


		Evaluation eval = new Evaluation(data);
		eval.crossValidateModel(ibk, data, 10, new Random(1));

		System.out.println(eval.toSummaryString());

		return eval.toSummaryString();
	}

	String naivebayes () throws Exception{
		System.out.println("NAIVE BAYES:");

		NaiveBayes nb = new NaiveBayes();
		nb.buildClassifier(data);
		System.out.println(data.toSummaryString());


		String[] options = new String[2];
		options[0] = "-t";
		options[1] = "/sdcard/car.arff";


		Evaluation eval = new Evaluation(data);
		eval.crossValidateModel(nb, data, 10, new Random(1));

		System.out.println(eval.toSummaryString());

		return eval.toSummaryString();
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
		}
	}



	public void OnStart(){
		BackgroundAsyncTask mt = new BackgroundAsyncTask();
		mt.execute();
	}




}