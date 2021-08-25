package com.phototaco.sikuli.test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Set;

import org.sikuli.script.App;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;
import org.sikuli.script.ScreenImage;

public class SikuliRetinaBug 
{
    static {
        System.setProperty("sun.java2d.uiScale", "1.0");
    }
    public static void main( String[] args )
    {
        Screen.showMonitors();
        App textEdit = new App("TextEdit");
        textEdit.open();
        textEdit.focus();

        //Show all of the regions
        List<Region> textEditWindows = textEdit.getWindows();
        for (Region region: textEditWindows){
            System.out.println(region.toStringShort());
        }

        String imagePath = System.getProperty("user.home") + File.separator + "SikuliRetinaBug" + File.separator;
        File capturedImagesFile = new File(imagePath);
        if (!capturedImagesFile.exists()) {
            FileAttribute<Set<java.nio.file.attribute.PosixFilePermission>> perms = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-x---"));
            try {
                Files.createDirectories(capturedImagesFile.toPath(), perms );
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        ScreenImage screenImage = Screen.getScreen(0).capture();
        screenImage.save(imagePath, "Screen0");
        screenImage = Screen.getScreen(1).capture();
        screenImage.save(imagePath, "Screen1");
    }
}
