package com.intexh.bidong.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

public class FileUtils {

    /**
     * 保存路径的文件夹名称
     */
    public static  String DIR_NAME = "ppstar_video";

    /**
     * 给指定的文件名按照时间命名
     */
    private static  SimpleDateFormat OUTGOING_DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");

    /**
     * 得到指定的Video保存路径
     * @return
     */
    public static File getDoneVideoPath() {
        File dir = new File(Environment.getExternalStorageDirectory()
                + File.separator + DIR_NAME);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    public static String parseNameByPath(String path){
    	String[] array = path.split(File.separator);
    	if(null != array && array.length > 0){
    		return array[array.length-1];
    	}else{
    		return path;
    	}
    }
    
    public static void copyFile(String inputPath, String outputPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            //create output directory if it doesn't exist
            File dir = new File (outputPath); 
            if (!dir.exists()) {
            	dir.createNewFile();
            }

            in = new FileInputStream(inputPath);        
            out = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;        

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
                catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }
    
    /**
	 * 创建目录和文件
	 * 
	 * @param path
	 *            路径
	 * @param filename
	 *            文件名
	 * @return file 返回创建的文件描述符
	 */
	public static File createFile(String path, String filename) {
		File dir = new File(path);
		File file = null;
		if (!dir.exists()) {
			// 按照指定的路径创建文件夹
			dir.mkdirs();
		}

		file = new File(path + filename);
		if (!file.exists()) {
			// 在指定的文件夹中创建文件
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return file;
	}

    public static boolean isExists(String fileName){
        String filePath = getLocalFilePath(fileName);
        try{
            File f = new File(filePath);
            if(!f.exists()){
                return false;
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public static String getLocalFilePath(String fileName){
        return getDoneVideoPath() + File.separator + fileName;
    }

    public static String genBaseFileName(){
        return UserUtils.getUserid() + "-" + System.currentTimeMillis(); //OUTGOING_DATE_FORMAT.format(new Date());
    }
    
    public static String genVideoFileName(){
    	return genBaseFileName() + ".mp4";
    }
    
    public static String genPicFileName(){
    	return genBaseFileName() + ".jpg";
    }

    public static void deleteFile(String path){
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
    }

    /**
     * 得到输出的Video／Image保存路径
     * 不带扩展名,加.pm4就是视频，加.jpg就是截图
     * @return
     */
    public static String newOutgoingFilePath() {
        return getDoneVideoPath() + File.separator + genBaseFileName();
    }

}
