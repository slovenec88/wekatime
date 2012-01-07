package edu.gricar.wekatime;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.clusterers.ClusterEvaluation;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

public class WekatimeActivity extends Activity {
	Instances data;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		try {
			beri();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	void beri() throws Exception{

		BufferedReader buf = new BufferedReader(new FileReader("/sdcard/car.arff"));

		data = new Instances(buf);
		buf.close();
		data.setClassIndex(data.numAttributes() - 1);
		//System.out.println(data.toString());

		Remove rm = new Remove();
		rm.setAttributeIndices("1");  // remove 1st attribute

		String[] optionj48 = new String[4];
		optionj48[0] = "-C";
		optionj48[1] = "0.25";
		optionj48[2] = "-M";
		optionj48[3] = "2";
		
		J48 j48 = new J48();
		j48.setUnpruned(true); 
		j48.setOptions(optionj48);
		FilteredClassifier fc = new FilteredClassifier();
		fc.setFilter(rm);
		fc.setClassifier(j48);
		fc.buildClassifier(data);
		Log.i("WekaLog", data.toSummaryString());
		Log.i("WekaLog", j48.toSummaryString());
		
		 String[] options = new String[2];
		 options[0] = "-t";
		 options[1] = "/sdcard/car.arff";
		 
		 
		 Evaluation eval = new Evaluation(data);
		 eval.crossValidateModel(j48, data, 10, new Random(1));
		 
		 //System.out.println(Evaluation.evaluateModel(new J48(), options));
		 System.out.println(eval.toSummaryString());

		 
		 
		/*for (int i = 0; i < data.numInstances(); i++) {
			double pred = 0;

			pred = fc.classifyInstance(data.instance(i));

			System.out.print("ID: " + data.instance(i).value(0));
			System.out.print(", actual: " + data.classAttribute().value((int) data.instance(i).classValue()));
			System.out.println(", predicted: " + data.classAttribute().value((int) pred));

			System.out.println("do tu sem pridem");
		}*/
	}

}