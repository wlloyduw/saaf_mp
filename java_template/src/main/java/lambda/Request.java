/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lambda;

/**
 *
 * @author wlloyd
 */
public class Request {
    String data;
    int rounds;
    int currentround;
    int nodespread;
    boolean sleep = false;
    
    public String toString()
    {
        return "data=" + data + " rounds=" + Integer.toString(rounds) + " currentround=" + Integer.toString(currentround) + 
                " nodespread=" + Integer.toString(nodespread) + " sleep="+Boolean.toString(sleep);
    }
    
    public String getData()
    {
        return data;
    }
    public void setData(String data)
    {
        this.data = data;
    }
    
    public Request(String data)
    {
        this.data = data;
    }
    public int getRounds()
    {
        return rounds;
    }
    
    public void setRounds(int rounds)
    {
        this.rounds = rounds;
    }
    public int getCurrentround()
    {
        return this.currentround;
    }
    public void setCurrentround(int currentround)
    {
        this.currentround = currentround;
    }
    
    public int getNodespread()
    {
        return this.nodespread;
    }
    public void setNodespread(int nodespread)
    {
        this.nodespread = nodespread;
    }
    
    public boolean getSleep()
    {
        return this.sleep;
    }
    public void setSleep(boolean sleep)
    {
        this.sleep = sleep;
    }
    
    public Request()
    {
        
    }
}
