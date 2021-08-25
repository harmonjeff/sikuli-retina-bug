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
        // Setting this property seems to make the capture and find functions of Sikuli API function correctly
        // Though if it is enabled the isRetina check at the bottom no longer functions.
        System.setProperty("sun.java2d.uiScale", "1.0");
    }
    public static void main( String[] args )
    {
        Screen.showMonitors();

        // Open bring the TextEdit windows to the foreground
        App textEdit = new App("TextEdit");
        textEdit.open();
        textEdit.focus();

        // Get all of the open windows and check to see if they are Retina
        List<Region> textEditWindows = textEdit.getWindows();
        for (Region region: textEditWindows){
            boolean regionOnRetina = isRetina(region);
            System.out.println(region.toStringShort() + ", isRetina="+regionOnRetina);
        }

        // Setup to write the captured images to the user home in the "SikuliRetinaBug" folder
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

        // Capture images of the whole screen
        ScreenImage screenImage = Screen.getScreen(0).capture();
        screenImage.save(imagePath, "Screen0");
        screenImage = Screen.getScreen(1).capture();
        screenImage.save(imagePath, "Screen1");
    }

    // The best method I could find to test to see if the screen a region is on is a Mac Retina screen
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
