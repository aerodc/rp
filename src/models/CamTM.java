package models;

public class CamTM {

    private String advNameTM;

    private String camNameTM;

    private int camId;

    public CamTM(String advNameTM, String camNameTM, int camId) {
        this.advNameTM = advNameTM;
        this.camNameTM = camNameTM;
        this.camId = camId;
    }

    public CamTM() {
    }

    public String getAdvNameTM() {
        return advNameTM;
    }

    public void setAdvNameTM(String advNameTM) {
        this.advNameTM = advNameTM;
    }

    public String getCamNameTM() {
        return camNameTM;
    }

    public void setCamNameTM(String camNameTM) {
        this.camNameTM = camNameTM;
    }

    public int getCamId() {
        return camId;
    }

    public void setCamId(int camId) {
        this.camId = camId;
    }

}
