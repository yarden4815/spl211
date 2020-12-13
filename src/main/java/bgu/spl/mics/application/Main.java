package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Input;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;


/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {

		Gson gson = new Gson();
		JsonReader reader = null;
		try {
			reader = gson.fromJson(new FileReader(args[0]), JsonReader.class);
		}catch (FileNotFoundException f){f.printStackTrace();}
		if (reader != null){

		}
	}
}
