 package tgtools.util;
 import java.util.UUID;
 /**
  * @author tianjing
  */
 public class GUID
 {
   public static String newGUID()
   {
     return UUID.randomUUID().toString().toUpperCase();
   }

 }
