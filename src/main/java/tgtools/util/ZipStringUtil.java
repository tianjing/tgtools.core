package tgtools.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
/**
 * @author tianjing
 */
public class ZipStringUtil {
	/**
	 * 
	 * 使用gzip进行压缩
	 */
	public static String gzip(String primStr) {
		if (primStr == null || primStr.length() == 0) {
			return primStr;
		}
		try {
			return org.apache.commons.codec.binary.Base64.encodeBase64String(gzip(primStr.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			LogHelper.error("","gzip压缩出错；字符集转换出错："+e.getMessage(),"ZipStringUtil.gzip(String)",e);
		}
		return null;
	}
	public static byte[] gzip(byte[] pContent) {
		if (pContent == null || pContent.length == 0) {
			return pContent;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		GZIPOutputStream gzip = null;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(pContent);
		} catch (IOException e) {
			LogHelper.error("","GZIP压缩写入出错；原因："+e.getMessage(),"ZipStringUtil.gzip(byte[])",e);
		} finally {
			if (gzip != null) {
				try {
					gzip.close();
				} catch (IOException e) {
					LogHelper.error("","GZIP关闭流出错；原因："+e.getMessage(),"ZipStringUtil.gzip(byte[])",e);
				}
			}
		}
		return out.toByteArray();
	}
		/**
         *
         * <p>
         * Description:使用gzip进行解压缩
         * </p>
         *
         * @param compressedStr
         * @return
         */
	public static String gunzip(String compressedStr) {
		if (compressedStr == null) {
			return null;
		}

		byte[] compressed = null;
		if(compressedStr.contains(" "))
		{
			compressedStr=StringUtil.replace(compressedStr," ","+");
		}
		try {
			compressed =org.apache.commons.codec.binary.Base64.decodeBase64(compressedStr);
			return gunzip(compressed);
		}
		catch (Exception e)
		{
			LogHelper.error("","gunzip解码失败；原因："+e.getMessage(),"ZipStringUtil.gunzip(String)",e);
		}
		return null;
	}
	public static String gunzip(byte[] compressedStr) {
		if (compressedStr == null) {
			return null;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = null;
		GZIPInputStream ginzip = null;
		byte[] compressed = null;
		String decompressed = null;
		try {
			compressed =compressedStr;
			in = new ByteArrayInputStream(compressed);
			ginzip = new GZIPInputStream(in);

			byte[] buffer = new byte[1024];
			int offset = -1;
			while ((offset = ginzip.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			decompressed = out.toString("UTF-8");
		} catch (IOException e) {
			LogHelper.error("","gunzip解码失败；原因："+e.getMessage(),"ZipStringUtil.gunzip(byte[])",e);
		} finally {
			if (ginzip != null) {
				try {
					ginzip.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		return decompressed;
	}
		/**
         * 使用zip进行压缩
         *
         * @param str
         *            压缩前的文本
         * @return 返回压缩后的文本
         */
	public static final String zip(String str) {
		if (str == null) {
			return null;
		}
		byte[] compressed;
		ByteArrayOutputStream out = null;
		ZipOutputStream zout = null;
		String compressedStr = null;
		try {
			out = new ByteArrayOutputStream();
			zout = new ZipOutputStream(out);
			zout.putNextEntry(new ZipEntry("0"));
			zout.write(str.getBytes("UTF-8"));
			zout.closeEntry();
			compressed = out.toByteArray();
			compressedStr = new sun.misc.BASE64Encoder()
					.encodeBuffer(compressed);
		} catch (IOException e) {
			compressed = null;
		} finally {
			if (zout != null) {
				try {
					zout.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		return compressedStr;
	}

	/**
	 * 使用zip进行解压缩
	 * 
	 * @param compressedStr
	 *            压缩后的文本
	 * @return 解压后的字符串
	 */
	public static final String unzip(String compressedStr) {
		if (compressedStr == null) {
			return null;
		}

		ByteArrayOutputStream out = null;
		ByteArrayInputStream in = null;
		ZipInputStream zin = null;
		String decompressed = null;
		try {
			byte[] compressed = new sun.misc.BASE64Decoder()
					.decodeBuffer(compressedStr);
			out = new ByteArrayOutputStream();
			in = new ByteArrayInputStream(compressed);
			zin = new ZipInputStream(in);
			zin.getNextEntry();
			byte[] buffer = new byte[1024];
			int offset = -1;
			while ((offset = zin.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			decompressed = out.toString("UTF-8");
		} catch (IOException e) {
			decompressed = null;
		} finally {
			if (zin != null) {
				try {
					zin.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		return decompressed;
	}

}
