package id.coba.kotlinpintar;


public class EpcDataModel {
	
	private int id;
	private String rssi;
	private String epc;

	public void setepcid(int epcid) {
		this.id = epcid;
	}


//	public EpcDataModel(String rssi, String epc) {
//		super();
//		this.rssi = rssi;
//		this.epc = epc;
//	}

	public EpcDataModel() {
		super();
	}
	
	
	

	public int getepcid() {
		return id;
	}


	public String getrssi() {
		return rssi;
	}


	public void setrssi(String rssi) {
		this.rssi = rssi;
	}
	
	public String getepc() {
		return epc;
	}
	public void setepc(String epc) {
		this.epc = epc;
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "rssi" + this.rssi + ",epc " + this.epc;
	}

}
