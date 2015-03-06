package LZW;

/**
 * LZW解压缩算法
 * @author lyq
 *
 */
public class Client {
	public static void main(String[] args){
		//源文件地址
		String srcFilePath = "C:\\Users\\lyq\\Desktop\\icon\\srcFile.txt";
		//压缩后的文件名
		String desFileName = "compressedFile.txt";
		//压缩文件的位置
		String desFileLoc = "C:\\Users\\lyq\\Desktop\\icon\\";
		//解压后的文件名
		String unCompressedFilePath = "C:\\Users\\lyq\\Desktop\\icon\\unCompressedFile.txt";
		
		LZWTool tool = new LZWTool(srcFilePath, desFileLoc, desFileName);
		//压缩文件
		tool.compress();
		
		//解压文件
		tool.unCompress(desFileLoc + desFileName, unCompressedFilePath);
	}
}
