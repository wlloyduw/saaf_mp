package saaf;

import java.util.HashMap;

/**
 * A basic Response object that can be consumed by FaaS Inspector
 * to be used as additional output.
 * 
 * @author Wes Lloyd
 * @author Robert Cordingly
 */
public class Response {
    //
    // User Defined Attributes
    //
    //
    // ADD getters and setters for custom attributes here.
    //

    // Return value
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "value=" + this.getValue() + super.toString();
    }
    
    
    private int calls;
    public int getCalls()
    {
        return calls;
    }
    public void setCalls(int calls)
    {
        this.calls = calls;
    }

    
    private int totalCalls;
    public int getTotalCalls()
    {
        return totalCalls;
    }
    public void setTotalCalls(int totalCalls)
    {
        this.totalCalls = totalCalls;
    }
    
}
