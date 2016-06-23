package security;

import java.util.Date;

public class Token {

    private Date createdDate;

    private String tokenValue;

    public Token() {}

    public Token(String tokenValue) {
        this.createdDate = new Date();
        this.tokenValue = tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    @Override
    public String toString() {
        return "Token [createdDate=" + createdDate + ", tokenValue=" + tokenValue + "]";
    }

}
