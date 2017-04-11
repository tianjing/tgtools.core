package tgtools.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import tgtools.exceptions.APPErrorException;

/**
 * 利用apache提供的ant.jar,提供对单个文件与目录的压缩，并支持是否需要创建压缩源目录、中文路径
 *
 * @Title：
 * @Description：ZipCompress
 * @Version 1.2
 */
public class ZipCompress {

	private static boolean isCreateSrcDir = true;// 是否创建源目录

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main1(String[] args) throws IOException {
		String src = "f:\\中文包";// 指定压缩源，可以是目录或文件
		String src1="F:\\中文包\\Hallo.png";
		String src2="F:\\中文包\\说明.docx";
		String decompressDir = "f:\\depress";// 解压路径
		String archive = "f:\\中文压缩文件.zip";// 压缩包路径
		String comment = "Java Zip 测试.";// 压缩包注释
		// // ----压缩文件或目录
		// writeByApacheZipOutputStream(src, archive, comment);
		// /*
		// * 读压缩文件，注释掉，因为使用的是apache的压缩类，所以使用java类库中 解压类时出错，这里不能运行
		// */
		// // readByZipInputStream(archive, decompressDir);
		// // ----使用apace ZipFile读取压缩文件
		// readByApacheZipFile(archive, decompressDir);


	}
	private static String getPrefixDir(String p_Src)
	{
		File srcFile =new File(p_Src);
		// 获取压缩源所在父目录
		String src = p_Src.replaceAll("\\\\", "/");

		String prefixDir = null;
		if (srcFile.isFile()) {
			prefixDir = src.substring(0, src.lastIndexOf("/") + 1);
		} else {
			prefixDir = (src.replaceAll("/$", "") + "/");
		}

		// 如果不是根目录
		if (prefixDir.indexOf("/") != (prefixDir.length() - 1)
				&& isCreateSrcDir) {
			prefixDir = prefixDir.replaceAll("[^/]+/$", "");
		}
		return prefixDir;
	}
	private static ZipOutputStream createZipOutputStream(OutputStream p_OutputSteam,String p_Comment)
	{
		CheckedOutputStream csum = new CheckedOutputStream(p_OutputSteam,
				new CRC32());

		ZipOutputStream zos = new ZipOutputStream(csum);
		// 支持中文
		zos.setEncoding("GBK");
		// 设置压缩包注释
		zos.setComment(p_Comment);
		// 启用压缩
		zos.setMethod(ZipOutputStream.DEFLATED);
		// 压缩级别为最强压缩，但时间要花得多一点
		zos.setLevel(Deflater.BEST_COMPRESSION);

		return zos;
	}

	/**
	 * 对文件夹或者文件进行压缩
	 *
	 * @param src
	 * @param archive
	 * @param comment
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void writeByApacheZipOutputStream(String src, String archive,
			String comment) throws APPErrorException {
		FileOutputStream f = null;
		try {
			// ----压缩文件：
			try {
				f = new FileOutputStream(archive);
			} catch (FileNotFoundException e) {
				throw new APPErrorException("创建压缩文件出错",e);
			}
			// 使用指定校验和创建输出流
			writeByApacheZipOutputStream(src, f, comment,true);
		} finally {
			if (null != f) {
				try {
					f.close();
				} catch (Exception e) {
				}
			}
			// 注：校验和要在流关闭后才准备，一定要放在流被关闭后使用
			// System.out.println("Checksum: " + csum.getChecksum().getValue());
		}
	}

	/**
	 * 对文件夹或者文件进行压缩 并返回压缩后的内容
	 *
	 * @param src
	 * @param comment
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static byte[] writeByApacheZipOutputStream(String src, String comment)
			throws APPErrorException {
		ByteArrayOutputStream mem = new ByteArrayOutputStream();
		try {
			writeByApacheZipOutputStream(src, mem, comment,true);
			try {
				mem.flush();
			} catch (IOException e) {
				throw new APPErrorException("写入文件失败",e);
			}
			return mem.toByteArray();
		} finally {
			if (null != mem) {
				try {
					mem.close();
				} catch (Exception ex) {
				}
			}
		}

	}
	public static void writeByApacheZipOutputStream(String src,
													OutputStream archive, String comment,boolean include) throws APPErrorException {

		BufferedOutputStream out = null;
		try {
			// ----压缩文件：
			// f = new FileOutputStream(archive);
			// 使用指定校验和创建输出流
			CheckedOutputStream csum = new CheckedOutputStream(archive,
					new CRC32());

			ZipOutputStream zos = new ZipOutputStream(csum);
			// 支持中文
			zos.setEncoding("GBK");
			out = new BufferedOutputStream(zos);
			// 设置压缩包注释
			zos.setComment(comment);
			// 启用压缩
			zos.setMethod(ZipOutputStream.DEFLATED);
			// 压缩级别为最强压缩，但时间要花得多一点
			zos.setLevel(Deflater.BEST_COMPRESSION);

			File srcFile = new File(src);

			if (!srcFile.exists()
					|| (srcFile.isDirectory() && srcFile.list().length == 0)) {
				throw new APPErrorException(
						"File must exist and  ZIP file must have at least one entry.");
			}
			// 获取压缩源所在父目录
			src = src.replaceAll("\\\\", "/");
			String prefixDir = null;
			if (srcFile.isFile()) {
				prefixDir = src.substring(0, src.lastIndexOf("/") + 1);
			} else {
				prefixDir = (src.replaceAll("/$", "") + "/");
			}

			// 如果不是根目录
			if (prefixDir.indexOf("/") != (prefixDir.length() - 1)
					&& isCreateSrcDir) {
				prefixDir = prefixDir.replaceAll("[^/]+/$", "");
			}

			// 开始压缩
			try {
				writeRecursive(zos, out, srcFile, prefixDir,include,true);
			} catch (IOException e) {
				throw new APPErrorException("写ZIP出错",e);
			}
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			if (null != out) {
				try {
					out.close();
				} catch (Exception e) {
				}
			}

			// 注：校验和要在流关闭后才准备，一定要放在流被关闭后使用
			// System.out.println("Checksum: " + csum.getChecksum().getValue());
		}



	}
	/**
	 * 对文件夹或者文件进行压缩
	 *
	 * @param src
	 * @param archive
	 * @param comment
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void writeByApacheZipOutputStream(String src,
			OutputStream archive, String comment) throws APPErrorException {
		writeByApacheZipOutputStream(src,archive,comment,true);
	}

	/**
	 * 使用 org.apache.tools.zip.ZipFile 解压文件，它与 java 类库中的 java.util.zip.ZipFile
	 * 使用方式是一新的，只不过多了设置编码方式的 接口。
	 *
	 * 注，apache 没有提供 ZipInputStream 类，所以只能使用它提供的ZipFile 来读取压缩文件。
	 *
	 * @param archive
	 *            压缩包路径
	 * @param decompressDir
	 *            解压路径
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ZipException
	 */
	public static void readByApacheZipFile(String archive, String decompressDir)
			throws IOException, FileNotFoundException, ZipException {
		BufferedInputStream bi;

		ZipFile zf = new ZipFile(archive, "GBK");// 支持中文

		Enumeration e = zf.getEntries();
		while (e.hasMoreElements()) {
			ZipEntry ze2 = (ZipEntry) e.nextElement();
			String entryName = ze2.getName();
			String path = decompressDir + "/" + entryName;
			if (ze2.isDirectory()) {
				System.out.println("正在创建解压目录 - " + entryName);
				File decompressDirFile = new File(path);
				if (!decompressDirFile.exists()) {
					decompressDirFile.mkdirs();
				}
			} else {
				System.out.println("正在创建解压文件 - " + entryName);
				String fileDir = path.substring(0, path.lastIndexOf("/"));
				File fileDirFile = new File(fileDir);
				if (!fileDirFile.exists()) {
					fileDirFile.mkdirs();
				}
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(decompressDir + "/" + entryName));

				bi = new BufferedInputStream(zf.getInputStream(ze2));
				byte[] readContent = new byte[1024];
				int readCount = bi.read(readContent);
				while (readCount != -1) {
					bos.write(readContent, 0, readCount);
					readCount = bi.read(readContent);
				}
				bos.close();
			}
		}
		zf.close();
	}

	/**
	 * 使用 java api 中的 ZipInputStream 类解压文件，但如果压缩时采用了
	 * org.apache.tools.zip.ZipOutputStream时，而不是 java 类库中的
	 * java.util.zip.ZipOutputStream时，该方法不能使用，原因就是编码方 式不一致导致，运行时会抛如下异常：
	 * java.lang.IllegalArgumentException at
	 * java.util.zip.ZipInputStream.getUTF8String(ZipInputStream.java:290)
	 *
	 * 当然，如果压缩包使用的是java类库的java.util.zip.ZipOutputStream 压缩而成是不会有问题的，但它不支持中文
	 *
	 * @param archive
	 *            压缩包路径
	 * @param decompressDir
	 *            解压路径
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void readByZipInputStream(String archive, String decompressDir)
			throws FileNotFoundException, IOException {
		BufferedInputStream bi;
		// ----解压文件(ZIP文件的解压缩实质上就是从输入流中读取数据):
		System.out.println("开始读压缩文件");

		FileInputStream fi = new FileInputStream(archive);
		CheckedInputStream csumi = new CheckedInputStream(fi, new CRC32());
		ZipInputStream in2 = new ZipInputStream(csumi);
		bi = new BufferedInputStream(in2);
		java.util.zip.ZipEntry ze;// 压缩文件条目
		// 遍历压缩包中的文件条目
		while ((ze = in2.getNextEntry()) != null) {
			String entryName = ze.getName();
			if (ze.isDirectory()) {
				System.out.println("正在创建解压目录 - " + entryName);
				File decompressDirFile = new File(decompressDir + "/"
						+ entryName);
				if (!decompressDirFile.exists()) {
					decompressDirFile.mkdirs();
				}
			} else {
				System.out.println("正在创建解压文件 - " + entryName);
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(decompressDir + "/" + entryName));
				byte[] buffer = new byte[1024];
				int readCount = bi.read(buffer);

				while (readCount != -1) {
					bos.write(buffer, 0, readCount);
					readCount = bi.read(buffer);
				}
				bos.close();
			}
		}
		bi.close();
		System.out.println("Checksum: " + csumi.getChecksum().getValue());
	}
	private static void writeRecursive(ZipOutputStream zos,
			BufferedOutputStream bo,String p_FileName,byte[] p_FileData)
			throws IOException, FileNotFoundException {
		ZipEntry zipEntry = new ZipEntry(p_FileName);
		zos.putNextEntry(zipEntry);
		bo.write(p_FileData);
		bo.flush();
	}
	/**
	 * 递归压缩
	 *
	 * 使用 org.apache.tools.zip.ZipOutputStream 类进行压缩，它的好处就是支持中文路径， 而Java类库中的
	 * java.util.zip.ZipOutputStream 压缩中文文件名时压缩包会出现乱码。 使用 apache 中的这个类与 java
	 * 类库中的用法是一新的，只是能设置编码方式了。
	 *
	 * @param zos
	 * @param bo
	 * @param srcFile
	 * @param prefixDir
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static void writeRecursive(ZipOutputStream zos,
			BufferedOutputStream bo, File srcFile, String prefixDir,boolean include,boolean first)
			throws IOException, FileNotFoundException {
		ZipEntry zipEntry;

		String filePath = srcFile.getAbsolutePath().replaceAll("\\\\", "/")
				.replaceAll("//", "/");

		if (srcFile.isDirectory()) {
			filePath = filePath.replaceAll("/$", "") + "/";
		}
		String entryName = filePath.replace(prefixDir, "").replaceAll("/$", "");
		if (srcFile.isDirectory()) {
			if (!"".equals(entryName)) {
				System.out.println("正在创建目录 - " + srcFile.getAbsolutePath()
						+ "  entryName=" + entryName);

				// 如果是目录，则需要在写目录后面加上 /
				zipEntry = new ZipEntry(entryName + "/");
				zos.putNextEntry(zipEntry);
			}
			if(!first&&!include&&srcFile.isDirectory())
			{
				return;
			}
			File srcFiles[] = srcFile.listFiles();
			for (int i = 0; i < srcFiles.length; i++) {
				writeRecursive(zos, bo, srcFiles[i], prefixDir,include,false);
			}
		} else {
			System.out.println("正在写文件 - " + srcFile.getAbsolutePath()
					+ "  entryName=" + entryName);
			BufferedInputStream bi = new BufferedInputStream(
					new FileInputStream(srcFile));

			// 开始写入新的ZIP文件条目并将流定位到条目数据的开始处
			zipEntry = new ZipEntry(entryName);
			zos.putNextEntry(zipEntry);
			byte[] buffer = new byte[1024];
			int readCount = bi.read(buffer);

			while (readCount != -1) {
				bo.write(buffer, 0, readCount);
				readCount = bi.read(buffer);
			}
			// 注，在使用缓冲流写压缩文件时，一个条件完后一定要刷新一把，不
			// 然可能有的内容就会存入到后面条目中去了
			bo.flush();
			// 文件读完后关闭
			bi.close();
		}
	}

	public static void main(String[] args)  {
		String input="C:\\Works\\DQ\\javademos\\FTPDownLoadService\\file";
		String output="C:\\Works\\DQ\\javademos\\FTPDownLoadService\\file/back/111.zip";
		try {
			FileOutputStream out=new FileOutputStream(output);
			writeByApacheZipOutputStream(input,out,"",false);
		//	out.flush();
			//out.close();
		} catch (APPErrorException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
