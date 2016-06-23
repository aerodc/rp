package models;

public class TokenResp {

    private String responseCode;

    private String token;

    private String url;

    public TokenResp(String responseCode, String token) {
        this.responseCode = responseCode;
        this.token = token;
    }

    public TokenResp() {

    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
