package application;

public class LED {
	int num;
	byte situation;
	private static final byte off=0;
	private static final byte on=1;
	
	public LED(int num) {
		this.num = num;
		this.situation = off;
	}
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public byte getSituation() {
		return situation;
	}
	
	public void setSituation(byte situation) {
		this.situation = situation;
	}

}
