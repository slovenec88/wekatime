package edu.gricar.wekatime;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class WekatimeActivity extends Activity {
	Instances data;
	BufferedReader buf;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		try {
			podatki();
			j48();
			ibk();
			naivebayes();	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void podatki() throws Exception{
		buf = new BufferedReader(new FileReader("/sdcard/car.arff"));
		data = new Instances(buf);
		buf.close();
		data.setClassIndex(data.numAttributes() - 1);
		
	}
	void j48() throws Exception{
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
	}
	
	void ibk() throws Exception{
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
	}
	
	void naivebayes () throws Exception{
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
	}
	
	

}