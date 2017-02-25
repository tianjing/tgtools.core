package tgtools.log;

import java.io.Serializable;

public interface ParameterizedMessage extends Serializable{
	  
    /** 
     * 获取参数列表 
     * @return 返回参数列表 
     */  
    public Object[] getParameters();  
      
    /** 
     * 获取指定索引位置的参数 
     * @param index 索引位置 
     * @return 返回参数列表中指定索引位置的参数值 
     * @throws IndexOutOfBoundsException 当index &gt;= 参数列表个数时，抛出此异常 
     * @see #getParameterCount() 
     */  
    public Object getParameter(int index) throws IndexOutOfBoundsException;  
      
    /** 
     * 获取参数个数 
     * @return 返回参数个数 
     */  
    public int getParameterCount();  
}
