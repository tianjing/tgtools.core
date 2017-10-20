package tgtools.util;

import org.junit.Assert;
import org.junit.Test;
import tgtools.exceptions.APPErrorException;

import static org.junit.Assert.*;

/**
 * @author 田径
 * @Title
 * @Description
 * @date 11:30
 */
public class ProcessHelperTest {
    @Test
    public void excute()  {
        try {
            ProcessHelper.excute("ping 192.168.88.128");
            Assert.assertTrue(true);
        } catch (APPErrorException e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void excute1() throws Exception {
        try {
            ProcessHelper.excute(new String[]{"C:\\Program Files\\Internet Explorer\\iexplore.exe","www.baidu.com"});
            Assert.assertTrue(true);
        } catch (APPErrorException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void excuteAsResult() throws Exception {
        try {
            ProcessHelper.excuteAsResult("ping 192.168.88.128",5);
            Assert.assertTrue(true);
        } catch (APPErrorException e) {
            Assert.assertTrue(false);
        }
    }

}