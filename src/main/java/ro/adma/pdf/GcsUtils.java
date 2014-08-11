package ro.adma.pdf;

import com.google.appengine.tools.cloudstorage.*;
import ro.appenigne.web.framework.utils.Log;
import ro.appenigne.web.framework.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

public class GcsUtils {
    public static String getDefaultBucket() {
        return Utils.getAppId() + ".appspot.com";
    }

    public static void writeFile(String fileName, String content, String contentType) {
        GcsFilename gcsFileName = new GcsFilename(GcsUtils.getDefaultBucket(), fileName);
        writeFile(gcsFileName, content, contentType);
    }

    public static void writeFile(GcsFilename gcsFileName, String content, String contentType) {
        try {
            writeFile(gcsFileName, content.getBytes("UTF-8"), contentType);
        } catch (IOException e) {
            Log.w(e);
        }
    }

    public static void writeFile(String fileName, byte[] content, String contentType) {
        GcsFilename gcsFileName = new GcsFilename(GcsUtils.getDefaultBucket(), fileName);
        writeFile(gcsFileName, content, contentType);
    }

    public static void writeFile(GcsFilename gcsFileName, byte[] content, String contentType) {
        try {
            GcsFileOptions options = new GcsFileOptions.Builder().mimeType(contentType).contentEncoding("UTF-8").build();
            GcsService gcsService = GcsServiceFactory.createGcsService();
            GcsOutputChannel writeChannel;

            writeChannel = gcsService.createOrReplace(gcsFileName, options);
            writeChannel.write(ByteBuffer.wrap(content));
            writeChannel.close();
        } catch (IOException e) {
            Log.w(e);
        }
    }

    public static String readFile(String fileName) {
        GcsFilename gcsFilename = getGcsFileName(fileName);
        return readFile(gcsFilename);
    }

    public static String readFile(GcsFilename gcsFilename) {
        try {
            GcsService gcsService = GcsServiceFactory.createGcsService();
            StringBuilder builder = new StringBuilder();
            GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(gcsFilename, 0, 1024 * 1024);
            BufferedReader reader = new BufferedReader(Channels.newReader(readChannel, "UTF8"));
            String aux;
            while ((aux = reader.readLine()) != null) {
                builder.append(aux).append("\r\n");
            }
            reader.close();
            return builder.toString();
        } catch (IOException e) {
            Log.w(e);
            return null;
        }
    }

    public static boolean deleteFile(String fileName) {
        return deleteFile(getGcsFileName(fileName));
    }

    public static boolean deleteFile(GcsFilename gcsFileName) {
        GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
        try {
            return gcsService.delete(gcsFileName);
        } catch (IOException e) {
            Log.w(e);
        }
        return false;
    }

    public static GcsFilename getGcsFileName(String fileName) {
        return new GcsFilename(GcsUtils.getDefaultBucket(), fileName);
    }
}
