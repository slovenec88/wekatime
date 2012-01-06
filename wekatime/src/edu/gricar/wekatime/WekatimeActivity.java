package edu.gricar.wekatime;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import android.app.Activity;
import android.os.Bundle;

import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

public class WekatimeActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        beri();
    }
    void beri(){
    try {
		BufferedReader buf = new BufferedReader(new FileReader("/sdcard/car.arff"));
		 try {
			Instances data = new Instances(buf);
			buf.close();
			data.setClassIndex(data.numAttributes() - 1);
			System.out.println(data.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
	Remove rm = new Remove();
	 rm.setAttributeIndices("1");  // remove 1st attribute
	
	J48 j48 = new J48();
	j48.setUnpruned(true); 
	FilteredClassifier fc = new FilteredClassifier();
	fc.setFilter(rm);
	fc.setClassifier(j48);

	 
	
	
    }
    
   
    
}