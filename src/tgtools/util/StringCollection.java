 package tgtools.util;
 
 import java.util.ArrayList;
 /**
  * 字符串集合对象
  * @author TianJing
  *
  */
 public class StringCollection extends ArrayList<String>
 {
   private static final long serialVersionUID = 8566000968839931341L;
 
   public String first()
   {
     return size() != 0 ? (String)get(0) : null;
   }
 
   public void add(String[] source)
   {
    for (int i = 0; i < source.length; i++)
       add(source[i]);
   }
 }
