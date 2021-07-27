package fakecom.myretail.myRetailAPIServiceJava;

import org.springframework.stereotype.Component;

@Component
public class CurrentPrice {

    private String value;
    private String currency_code;

    public CurrentPrice() {
        value = "";
        currency_code = "";
    } // end constructor CurrentPrice()

    public boolean isNumeric(String value) {
        boolean output;
        try {
            Double.parseDouble(value);
            output = true;
        } catch (NumberFormatException e) {
            output = false;
        } // end try catch
        return output;
    } // end isNumeric()

    public void setValue(String value) {
        this.value = value;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public String getValue() {
        return value;
    } // end getValue()

    public String getCurrency_code() {
        return currency_code;
    } // end getCurrency_code
} // end class CurrentPrice