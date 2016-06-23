package models;

public class CamDesc {

    private int camId;

    private String desc;

    private int adServer;

    public CamDesc(int camId, String desc) {
        this.camId = camId;
        this.desc = desc;
    }

    public CamDesc() {
    }

    public int getCamId() {
        return camId;
    }

    public void setCamId(int camId) {
        this.camId = camId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getAdServer() {
        return adServer;
    }

    public void setAdServer(int adServer) {
        this.adServer = adServer;
    }

}
