package me.oddlyoko.ejws.example2.model;

import lombok.Getter;
import lombok.Setter;
import me.oddlyoko.ejws.model.Fields;
import me.oddlyoko.ejws.model.Models;

@Getter
@Models.Model(stored = true)
public class Example1 {
	@Fields.Field
	@Setter
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
}
