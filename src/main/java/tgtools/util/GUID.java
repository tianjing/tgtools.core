 package tgtools.util;
 import java.util.UUID;
 
 public class GUID
 {
   public static String newGUID()
   {
     return UUID.randomUUID().toString().toUpperCase();
   }
 
   public static void main(String[] args)
   {
     for (int i = 0; i < 100; i++)
       System.out.println(newGUID());
   }
 }
