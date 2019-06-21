package io.github.mkckr0.mynote;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tool {
    public static void copyFile(String from, String to) throws IOException {
        byte[] bytes = new byte[1024];
        FileInputStream inputStream = new FileInputStream(from);
        FileOutputStream outputStream = new FileOutputStream(to);
        while (inputStream.read(bytes) > 0) {
            outputStream.write(bytes);
        }
        inputStream.close();
        outputStream.close();
    }

    public static File createFile(Context context, String prefix, String suffix, String dir) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String fileName = prefix + "_" + timeStamp + "_";
        File storageDir = new File(context.getExternalFilesDir(null).getAbsolutePath() + File.separator + dir);
        if (storageDir.exists() == false) {
            storageDir.mkdir();
        }
        try {
            return File.createTempFile(fileName, suffix, storageDir);
        } catch (IOException e) {
            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT);
            return null;
        }
    }

    public static Uri createFileForUri(Context context, String prefix, String suffix, String dir) {
        File file = createFile(context, prefix, suffix, dir);
        if (file == null) {
            return null;
        }
        return FileProvider.getUriForFile(context, "io.github.mkckr0.mynote.fileprovider", file);
    }

    public static File createImage(Context context) {
        return createFile(context, "JPG", ".jpg", "image");
    }

    public static Uri getUri(Context context, File file) {
        return FileProvider.getUriForFile(context, "io.github.mkckr0.mynote.fileprovider", file);
    }

    public static void writeWaveFileHeader(FileOutputStream out, int SampleRate, int NumChannels, int BitsPerSample) throws IOException {
        byte[] header = new byte[44];
        int ByteRate = SampleRate * NumChannels * BitsPerSample / 8;
        int BlockAlign = NumChannels * BitsPerSample / 8;
        int ChunckSize = (int) out.getChannel().size() - 8;
        int Subchunck2Size = ChunckSize - 36;

        // The "RIFF" chunck descriptor
        // Chunck ID
        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        // ChunckSize
        header[4] = (byte) (ChunckSize & 0xff);
        header[5] = (byte) ((ChunckSize >> 8) & 0xff);
        header[6] = (byte) ((ChunckSize >> 16) & 0xff);
        header[7] = (byte) ((ChunckSize >> 24) & 0xff);
        // Format
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';

        // The "fmt" sub-chunck
        // Subchunck1ID
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        // Subchunck1Size
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        // AudioFormat
        header[20] = 1;
        header[21] = 0;
        // NumChannels
        header[22] = (byte) NumChannels;
        header[23] = 0;
        // SampleRate
        header[24] = (byte) (SampleRate & 0xff);
        header[25] = (byte) ((SampleRate >> 8) & 0xff);
        header[26] = (byte) ((SampleRate >> 16) & 0xff);
        header[27] = (byte) ((SampleRate >> 24) & 0xff);
        // ByteRate
        header[28] = (byte) (ByteRate & 0xff);
        header[29] = (byte) ((ByteRate >> 8) & 0xff);
        header[30] = (byte) ((ByteRate >> 16) & 0xff);
        header[31] = (byte) ((ByteRate >> 24) & 0xff);
        // BlockAlign
        header[32] = (byte) BlockAlign;
        header[33] = 0;
        // BitsPerSample
        header[34] = (byte) BitsPerSample;
        header[35] = 0;

        // The "data" sub-chunck
        // Subchunck2ID
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        // Subchunck2Size
        header[40] = (byte) (Subchunck2Size & 0xff);
        header[41] = (byte) ((Subchunck2Size >> 8) & 0xff);
        header[42] = (byte) ((Subchunck2Size >> 16) & 0xff);
        header[43] = (byte) ((Subchunck2Size >> 24) & 0xff);
        out.write(header, 0, 44);
    }
}
