package tgtools.log;

import java.util.Arrays;

public class JDBCLogMessage implements ParameterizedMessage {  
    private static final long serialVersionUID = 1709063421963292637L;  
    private Object[] params;  
      
    public JDBCLogMessage(Object... params) {  
        this.params = params;  
    }  
  
    public Object[] getParameters() {  
        return this.params;  
    }  
  
    public Object getParameter(int index) throws IndexOutOfBoundsException {  
        return this.params[index];  
    }  
  
    public int getParameterCount() {  
        return this.params.length;  
    }  
  
    @Override  
    public String toString() {  
        return Arrays.toString(this.params);  
    }  
}  
