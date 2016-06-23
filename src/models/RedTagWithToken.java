package models;

public class RedTagWithToken {

    private String tokenValue;

    private RedirectTag redirectTag;

    private Redirect redirect;

    public RedTagWithToken() {
    }

    public RedTagWithToken(String tokenValue, RedirectTag redirectTag, Redirect redirect) {
        this.tokenValue = tokenValue;
        this.redirectTag = redirectTag;
        this.redirect = redirect;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public RedirectTag getRedirectTag() {
        return redirectTag;
    }

    public void setRedirectTag(RedirectTag redirectTag) {
        this.redirectTag = redirectTag;
    }

    public Redirect getRedirect() {
        return redirect;
    }

    public void setRedirect(Redirect redirect) {
        this.redirect = redirect;
    }

    @Override
    public String toString() {
        return "RedTagWithToken [tokenValue=" + tokenValue + ", redirectTag=" + redirectTag + "]";
    }

}
