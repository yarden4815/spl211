package bgu.spl.mics.application;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.passiveObjects.Input;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;


/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {

		if (args.length != 2){
			System.out.println("Invalid input.");
			return;
		}

		Gson Input = new Gson();
		Input input = null;
		try(Reader reader = new FileReader(args[0])) {
			input = Input.fromJson(reader, Input.class);
		} catch (IOException e){e.printStackTrace();}
		if (input == null) {
			throw new NullPointerException();
		}

		simulate(input);

		Gson output = new Gson();
		try(Writer writer = new FileWriter(args[1])){
			output.toJson(Diary.getInstance(), writer);
		}catch (IOException e){e.printStackTrace();}

	}

	private static void simulate(Input input) {
		MessageBus messageBus = MessageBusImpl.getInstance();
		Ewoks ewoks = Ewoks.getInstance(input.getEwoks());
		Diary diary = Diary.getInstance();
		Thread Lando = new Thread(new LandoMicroservice(input.getLando()));
		Thread R2D2 = new Thread(new R2D2Microservice(input.getR2D2()));
		Thread C3PO = new Thread(new C3POMicroservice());
		Thread HanSolo = new Thread(new HanSoloMicroservice());
		Thread leia = new Thread(new LeiaMicroservice(input.getAttacks()));
		Lando.start();
		R2D2.start();
		C3PO.start();
		HanSolo.start();
		leia.start();
		try {
			Lando.join();
			R2D2.join();
			C3PO.join();
			HanSolo.join();
			leia.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
