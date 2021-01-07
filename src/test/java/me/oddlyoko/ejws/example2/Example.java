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
		System.out.printf("B: %d%n", e.getB());
		System.out.printf("C: %d%n", e.getC());
		System.out.printf("D: %d%n", e.getD());
		System.out.printf("E: %d%n", e.getE());
		System.out.printf("F: %d%n", e.getF());
		System.out.printf("G: %d%n", e.getG());
		e.setA(5);
		System.out.printf("G: %d%n", e.getG());
		// long before = System.currentTimeMillis();
		// for (int i = 0; i < 100000; i++) {
		// e.setA(i);
		// e.getG();
		// }
		// long after = System.currentTimeMillis();
		// System.out.println(String.format("Time: %dms", after - before));
	}
}
