package tgtools.xml;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：17:25
 */
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlSerializable
{

}