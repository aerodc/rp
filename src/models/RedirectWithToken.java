package models;

public class RedirectWithToken {

    private String tokenValue;

    private Redirect redirect;

    public RedirectWithToken(String tokenValue, Redirect redirect) {
        this.tokenValue = tokenValue;
        this.redirect = redirect;
    }

    public RedirectWithToken() {
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public Redirect getRedirect() {
        return redirect;
    }

    public void setRedirect(Redirect redirect) {
        this.redirect = redirect;
    }

}
