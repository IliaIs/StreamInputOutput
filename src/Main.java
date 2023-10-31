import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {
    static StringBuilder sb = new StringBuilder();
    public static void main(String[] args) {


        File src = new File("/Users/ilyaisaev/Games", "src" );
        File res = new File("/Users/ilyaisaev/Games", "res" );
        File savegame = new File("/Users/ilyaisaev/Games", "savegame" );
        File temp = new File("/Users/ilyaisaev/Games", "temp" );

        File main = new File("/Users/ilyaisaev/Games/src", "main" );
        File test = new File("/Users/ilyaisaev/Games/src", "test" );

        File mainJava = new File("/Users/ilyaisaev/Games/src/main", "Main.java" );
        File utilsJava = new File("/Users/ilyaisaev/Games/src/main", "Utils.java" );

        File drawables = new File("/Users/ilyaisaev/Games/res", "drawables" );
        File vectors = new File("/Users/ilyaisaev/Games/res", "vectors" );
        File icons = new File("/Users/ilyaisaev/Games/res", "icons" );

        File tempTxt = new File("/Users/ilyaisaev/Games/temp", "temp.txt" );

        File savePlayer1 = new File("/Users/ilyaisaev/Games/savegame/savePlayer1.dat");
        File savePlayer2 = new File("/Users/ilyaisaev/Games/savegame/savePlayer2.dat");
        File savePlayer3 = new File("/Users/ilyaisaev/Games/savegame/savePlayer3.dat");

        List<File> fileList = Arrays.asList(
                tempTxt, mainJava, utilsJava,
                savePlayer1, savePlayer2, savePlayer3
        );

        List<File> dirList = Arrays.asList(
                src, res, savegame, temp, main,
                test, drawables, vectors, icons
        );

        for (File dir : dirList) {
            createDir(dir);
        }
        for (File file : fileList) {
            if (!file.getName().contains("Player")) {
                createFile(file);
            }
        }


        String logs = sb.toString();
        try (FileWriter writer = new FileWriter(tempTxt)) {
            writer.write(logs);
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }

        GameProgress player1 = new GameProgress(56, 9, 15, 165.5);
        GameProgress player2 = new GameProgress(99, 10, 25, 119.9);
        GameProgress player3 = new GameProgress(30, 17, 35, 50.6);

        saveGame("/Users/ilyaisaev/Games/savegame/savePlayer1.dat", player1);
        saveGame("/Users/ilyaisaev/Games/savegame/savePlayer2.dat", player2);
        saveGame("/Users/ilyaisaev/Games/savegame/savePlayer3.dat", player3);

        zipFiles("/Users/ilyaisaev/Games/savegame/AllSave.zip",
                Arrays.asList(
                        "/Users/ilyaisaev/Games/savegame/savePlayer1.dat",
                        "/Users/ilyaisaev/Games/savegame/savePlayer2.dat",
                        "/Users/ilyaisaev/Games/savegame/savePlayer3.dat"
                )
        );

        for (File file : fileList) {
            if (file.getName().contains("Player")) {
                deleteFile(file);
            }
        }

        openZip("/Users/ilyaisaev/Games/savegame/AllSave.zip",
                "/Users/ilyaisaev/Games/savegame/"
        );

        GameProgress first = openProgress("/Users/ilyaisaev/Games/savegame/savePlayer1.dat");
        GameProgress second = openProgress("/Users/ilyaisaev/Games/savegame/savePlayer2.dat");
        GameProgress third = openProgress("/Users/ilyaisaev/Games/savegame/savePlayer3.dat");

        System.out.println(
                first.toString() + "\n"
                + second.toString() + "\n"
                + third.toString()
        );
    }
    public static GameProgress openProgress(String path) {
        GameProgress gameProgress = null;

        try (FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis)) {
                gameProgress = (GameProgress) ois.readObject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return gameProgress;
    }
    public static void openZip(String pathZip, String pathFile) {
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(pathZip))) {
            ZipEntry entry;
            String name;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName();
                System.out.println(name);
                try (FileOutputStream fout = new FileOutputStream(pathFile + name)) {
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        fout.write(c);
                    }
                    fout.flush();
                    zin.closeEntry();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    public static void saveGame(String path, GameProgress player) {
        try (FileOutputStream fos = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(player);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void zipFiles(String pathZip, List<String> pathFile) {
        int count = 1;
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(pathZip))) {
            for (String path : pathFile) {
                try (FileInputStream fis = new FileInputStream(path)){
                    ZipEntry entry = new ZipEntry("savePlayer" + count + ".dat");
                    zout.putNextEntry(entry);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    zout.write(buffer);
                    zout.closeEntry();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
                count++;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void createDir(File file) {
        if (file.mkdir()) {
            sb.append("Директория " +
                    file.getName() + " успешно создана" + "\n");
        } else {
            sb.append("При создании директории  " +
                    file.getName() + " что-то пошло не так" + "\n");
        }
    }
    public static void createFile(File file) {
        try {
           if (file.createNewFile()) {
               sb.append("Файл " + file.getName() +
                       " успешно создан" + "\n");
           } else {
               sb.append("При создании файла " +
                       file.getName() + " что-то пошло не так" + "\n");
           }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void deleteFile(File file) {
        if (file.delete()) {
            System.out.println("Файл удален");
        } else {
            System.out.println("Файл не найден");
        }
    }
}