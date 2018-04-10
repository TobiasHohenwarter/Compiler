package yapl.impl;

public class Register {

	private int number;
	private boolean occupied;

	public Register(int numbr) {
		this.number = numbr;
		this.occupied = false;
	}

	public int getNumber() {
		return this.number;
	}

	public boolean getOccupied() {
		return this.occupied;
	}

	public void alloc() {
		this.occupied = true;
	}

	public void free() {
		this.occupied = false;
	}
}
