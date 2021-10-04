package tgtools.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class ZipStringUtilTest {

    @Test
    public void main() {
        String ss="H4sIAAAAAAAAACupLEi1zcxLy1dLzs8rSc0rsU3MyXHOLypwzk9JBYoVFfgl5qbaPtu84en2eU/2zX4 ZevT1jVP 3cAAPF7TLg5AAAA";
        String ss1="Bbxxbinsert,南京,奥体变,xxx";
        String res= ZipStringUtil.gunzip(ss);
        //System.out.println("gzip:"+gzip(ss1));
        System.out.println(ZipStringUtil.gunzip(ss));
    }
}