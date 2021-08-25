package com.phototaco.sikuli.test;

import java.awt.*;
import java.awt.geom.AffineTransform;
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
        //Setting this property seems to make the capture and find functions using Sikuli API function correctly
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
            boolean regionOnRetina = isRetina(region);
            System.out.println(region.toStringShort() + ", isRetina="+regionOnRetina);
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

    private static boolean isRetina(Region region) {
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screenDevices = graphicsEnvironment.getScreenDevices();
        boolean isRetina = false;
        for (int i=0; i<screenDevices.length; i++) {
            GraphicsDevice screenDevice = screenDevices[i];
            GraphicsConfiguration graphicsConfiguration = screenDevice.getDefaultConfiguration();
            AffineTransform defaultTransform = graphicsConfiguration.getDefaultTransform();
            System.out.println("screenDevice["+i+"].IDString: "+ screenDevice.getIDstring() + 
                " isRetina=" + !defaultTransform.isIdentity() + 
                ",scaleX=" + defaultTransform.getScaleX() + 
                ",scaleY="+defaultTransform.getScaleY()+
                ",bounds="+graphicsConfiguration.getBounds());
            // See if the current screen has the some x-coordinate as the screen where the region is at
            if (region.getScreen().getX() == graphicsConfiguration.getBounds().x) isRetina = !defaultTransform.isIdentity();
        }
        return isRetina;
    }
}
