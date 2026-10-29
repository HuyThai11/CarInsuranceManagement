package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


public class FileUtils {


    public static boolean writeBinaryFile(String filename, Object data) {
        if (filename == null || data == null) {
            return false;
        }
        try (FileOutputStream fos = new FileOutputStream(filename);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(data);
            oos.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean atomicSaveTwoFiles(String filename1, Object data1, String filename2, Object data2) {
        if (filename1 == null || data1 == null || filename2 == null || data2 == null) {
            return false;
        }

        Path target1 = Paths.get(filename1);
        Path target2 = Paths.get(filename2);
        Path tmp1 = Paths.get(filename1 + ".tmp");
        Path tmp2 = Paths.get(filename2 + ".tmp");
        Path bak1 = Paths.get(filename1 + ".bak");
        Path bak2 = Paths.get(filename2 + ".bak");

        // Giai đoạn 1: Ghi cả hai ra file tạm (.tmp)
        boolean writeTmp1 = writeBinaryFile(tmp1.toString(), data1);
        boolean writeTmp2 = writeBinaryFile(tmp2.toString(), data2);

        if (!writeTmp1 || !writeTmp2) {
            safeDelete(tmp1);
            safeDelete(tmp2);
            return false;
        }

       
        boolean target1Exists = Files.exists(target1);
        boolean target2Exists = Files.exists(target2);
        boolean hasBak1 = false;
        boolean hasBak2 = false;

        if (target1Exists) {
            hasBak1 = safeBackup(target1, bak1);
            if (!hasBak1) {
                
                safeDelete(tmp1);
                safeDelete(tmp2);
                return false;
            }
        }

        if (target2Exists) {
            hasBak2 = safeBackup(target2, bak2);
            if (!hasBak2) {

                safeDelete(tmp1);
                safeDelete(tmp2);
                if (hasBak1) safeDelete(bak1);
                return false;
            }
        }

        // Giai đoạn 3: Cập nhật file đích từ file tạm
        try {
            Files.copy(tmp1, target1, StandardCopyOption.REPLACE_EXISTING);
            try {
                Files.copy(tmp2, target2, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                // Nếu copy sang file 2 thất bại: Rollback file 1
                if (target1Exists && hasBak1) {
                    Files.copy(bak1, target1, StandardCopyOption.REPLACE_EXISTING);
                } else if (!target1Exists) {
                    safeDelete(target1);
                }
                throw e;
            }

            // Ghi thành công cả 2: Dọn dẹp các file backup và tmp
            safeDelete(tmp1);
            safeDelete(tmp2);
            safeDelete(bak1);
            safeDelete(bak2);
            return true;
        } catch (IOException e) {
            safeDelete(tmp1);
            safeDelete(tmp2);
            if (hasBak1) safeDelete(bak1);
            if (hasBak2) safeDelete(bak2);
            return false;
        }
    }

    private static boolean safeBackup(Path source, Path backup) {
        if (Files.exists(source)) {
            try {
                Files.copy(source, backup, StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    private static void safeDelete(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }

    /**
     * Đọc đối tượng từ file nhị phân lên bộ nhớ.
     * @param filename Tên đường dẫn file cần nạp
     * @return Đối tượng đọc được từ file, hoặc null nếu file chưa tồn tại hoặc lỗi đọc
     */
    public static Object readBinaryFile(String filename) {
        if (filename == null) {
            return null;
        }
        File file = new File(filename);
        if (!file.exists() || file.length() == 0) {
            return null;
        }
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}
