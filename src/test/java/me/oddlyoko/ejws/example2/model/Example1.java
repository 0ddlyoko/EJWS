package me.oddlyoko.ejws.example2.model;

import me.oddlyoko.ejws.model.Fields;
import me.oddlyoko.ejws.model.Models;

@Models.Model(stored = true, primary = { "id" })
public class Example1 {
	@Fields.Field
	private long id;
	@Fields.Field
	private int a;
	@Fields.Field(compute = "computeB")
	private int b;
	@Fields.Field(compute = "computeC")
	private int c;
	@Fields.Field(compute = "computeD")
	private int d;
	@Fields.Field(compute = "computeE")
	private int e;
	@Fields.Field(compute = "computeF")
	private int f;
	@Fields.Field(compute = "computeG")
	private int g;
	@Fields.Field(compute = "computeH")
	private int h;

	@Models.Require({ "a" })
	public void computeB() {
		b = a + 1;
	}

	@Models.Require({ "b" })
	public void computeC() {
		c = b + 2;
	}

	@Models.Require({ "b" })
	public void computeD() {
		d = b - 2;
	}

	@Models.Require({ "c", "d" })
	public void computeE() {
		e = c + d;
	}

	@Models.Require({ "a", "b", "c", "d", "e" })
	public void computeF() {
		f = a + b + c + d + e;
	}

	@Models.Require({ "h", "f", "e" })
	public void computeG() {
		g = h + f + e;
	}

	@Models.Require({ "e", "a" })
	public void computeH() {
		h = e + a;
	}

	public long getId() {
		return id;
	}

	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}

	public int getB() {
		return b;
	}

	public int getC() {
		return c;
	}

	public int getD() {
		return d;
	}

	public int getE() {
		return e;
	}

	public int getF() {
		return f;
	}

	public int getG() {
		return g;
	}

	public int getH() {
		return h;
	}
}
