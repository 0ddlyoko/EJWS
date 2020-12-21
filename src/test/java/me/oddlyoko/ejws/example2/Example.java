package me.oddlyoko.ejws.example2;

import me.oddlyoko.ejws.EJWS;
import me.oddlyoko.ejws.example2.model.Example1;
import me.oddlyoko.ejws.exceptions.ModelLoadException;

public class Example {

	public static void main(String[] args) throws ModelLoadException {
		EJWS ejws = new EJWS();
		// Load classes
		ejws.getModelManager().loadModel(Example1.class);

		Example1 e = ejws.getModelManager().newInstance(Example1.class);
		e.setA(1);
		System.out.println(String.format("B: %d", e.getB()));
		System.out.println(String.format("C: %d", e.getC()));
		System.out.println(String.format("D: %d", e.getD()));
		System.out.println(String.format("E: %d", e.getE()));
		System.out.println(String.format("F: %d", e.getF()));
		System.out.println(String.format("G: %d", e.getG()));
		e.setA(5);
		System.out.println(String.format("G: %d", e.getG()));
		// long before = System.currentTimeMillis();
		// for (int i = 0; i < 100000; i++) {
		// e.setA(i);
		// e.getG();
		// }
		// long after = System.currentTimeMillis();
		// System.out.println(String.format("Time: %dms", after - before));
	}
}
