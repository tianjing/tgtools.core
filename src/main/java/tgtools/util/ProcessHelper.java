package tgtools.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import tgtools.exceptions.APPErrorException;

import tgtools.tasks.Task;
import tgtools.tasks.TaskContext;

/**
 * @author 田径
 * @Title Porcess帮助类
 * @Description
 * @date 9:43
 */
public class ProcessHelper {

    /**
     * 执行命令不返回结果（不阻塞线程）
     * @author 田径
     * @date 2017/10/20
     *
     * @param pCommand 命令
     *
     * @throws APPErrorException
     */
    public static void excute(String pCommand) throws APPErrorException {
        java.lang.Process process = null;

        try {
            process = Runtime.getRuntime().exec(pCommand);
        } catch (Exception e) {
            throw new APPErrorException("命令执行错误。原因：" + e.getMessage(), e);
        } finally {
            process = null;
        }
    }

    /**
     * 执行批量命令不返回结果（不阻塞线程）
     * @author 田径
     * @date 2017/10/20
     *
     * @param pCommands (第一个字符应该是exe,bat等可执行文件，命令无效如ping；第二开始时参数；)
     *
     * @throws APPErrorException
     */
    public static void excute(String[] pCommands) throws APPErrorException {
        java.lang.Process process = null;

        try {
            process = Runtime.getRuntime().exec(pCommands);
        } catch (Exception e) {
            throw new APPErrorException("命令执行错误。原因：" + e.getMessage(), e);
        } finally {
            process = null;
        }
    }

    /**
     * 执行命令并等待返回结果（阻塞线程）
     * @author 田径
     * @date 2017/10/20
     *
     * @param pCommand 命令
     * @param pTimeOut 超时时间（超过时间则关闭，并返回期间得到的返回值）
     *
     * @return
     *
     * @throws APPErrorException
     */
    public static String excuteAsResult(String pCommand, int pTimeOut) throws APPErrorException {
        java.lang.Process process = null;

        try {
            process = Runtime.getRuntime().exec(pCommand);

            ByteArrayOutputStream resultOutStream = new ByteArrayOutputStream();
            InputStream           errorInStream   = new BufferedInputStream(process.getErrorStream());
            InputStream           processInStream = new BufferedInputStream(process.getInputStream());

            new MyTask(process, pTimeOut).runThread(null);

            int    num = 0;
            byte[] bs  = new byte[1024];

            while ((num = errorInStream.read(bs)) != -1) {
                resultOutStream.write(bs, 0, num);
            }

            while ((num = processInStream.read(bs)) != -1) {
                resultOutStream.write(bs, 0, num);
            }

            String result = new String(resultOutStream.toByteArray());

            System.out.println(result);
            errorInStream.close();
            errorInStream = null;
            processInStream.close();
            processInStream = null;
            resultOutStream.close();
            resultOutStream = null;

            return result;
        } catch (IOException e) {
            throw new APPErrorException("命令执行错误。原因：" + e.getMessage(), e);
        } finally {
            if (process != null) {
                process.destroy();
            }

            process = null;
        }
    }
    /**
     * @author 田径
     * @title
     * @description
     * @date 2017/10/20
     */
    private static class MyTask extends Task {

        /** Field description */
        private static final int MIN_TIMEOUT = 1000;

        /**
         * Field description
         */
        private Process mPorcess;

        /** Field description */
        private int mTimeOut;

        private MyTask(Process pPorcess, int pTimeOut) {
            mPorcess = pPorcess;
            mTimeOut = pTimeOut;
        }

        @Override
        protected boolean canCancel() {
            return false;
        }

        @Override
        public void run(TaskContext pParam) {
            if (mTimeOut < MIN_TIMEOUT) {
                mTimeOut = MIN_TIMEOUT;
            }

            try {
                Thread.sleep(mTimeOut);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (null != mPorcess) {
                mPorcess.destroy();
            }

            return;
        }
    }
}
