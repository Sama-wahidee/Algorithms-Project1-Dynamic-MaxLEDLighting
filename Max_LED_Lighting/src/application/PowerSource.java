package application;

public class PowerSource {
	int num;
	byte situation;
	private static final byte connected=1;
	private static final byte disconnect=0;
	
	public PowerSource(int num) {
		this.num = num;
		this.situation = disconnect;
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
