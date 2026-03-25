import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageMorph {

    public static void main(String[] args) {

        String inputFolder = "images";
        String outputFolder = "loadedImages";

        // Creates output folder if it doesn't exist
        File outDir = new File(outputFolder);
        if (!outDir.exists()) {
            if (outDir.mkdir()) {
                System.out.println("Created folder: " + outputFolder);
            } else {
                System.out.println("Failed to create output folder.");
                return;
            }
        }

        for (int i = 1; i <= 8; i++) {
            double alpha = i / 9.0;

            try {
                BufferedImage w0 = ImageIO.read(new File(inputFolder + "/W0.t" + i + ".jpg"));
                BufferedImage w1 = ImageIO.read(new File(inputFolder + "/W1.t" + i + ".jpg"));

                // Checks if images loaded properly
                if (w0 == null || w1 == null) {
                    System.out.println("Error: Could not load images for t" + i);
                    continue;
                }

                int width = w0.getWidth();
                int height = w0.getHeight();

                // Checks the size to match
                if (width != w1.getWidth() || height != w1.getHeight()) {
                    System.out.println("Error: Image size mismatch at t" + i);
                    continue;
                }

                BufferedImage blended = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {

                        int rgb0 = w0.getRGB(x, y);
                        int rgb1 = w1.getRGB(x, y);

                        int r0 = (rgb0 >> 16) & 0xFF;
                        int g0 = (rgb0 >> 8) & 0xFF;
                        int b0 = rgb0 & 0xFF;

                        int r1 = (rgb1 >> 16) & 0xFF;
                        int g1 = (rgb1 >> 8) & 0xFF;
                        int b1 = rgb1 & 0xFF;

                        int r = (int) ((1 - alpha) * r0 + alpha * r1);
                        int g = (int) ((1 - alpha) * g0 + alpha * g1);
                        int b = (int) ((1 - alpha) * b0 + alpha * b1);

                        int blendedRGB = (r << 16) | (g << 8) | b;

                        blended.setRGB(x, y, blendedRGB);
                    }
                }

                // Save to output folder with safeguard
                String baseName = outputFolder + "/blend.t" + i + ".jpg";
                File outputFile = getUniqueFile(baseName);

                ImageIO.write(blended, "jpg", outputFile);

                System.out.println("Saved " + outputFile.getPath());

            } catch (IOException e) {
                System.out.println("Error processing t" + i + ": " + e.getMessage());
            }
        }
    }

    // Helper method that avoids overwriting files
    private static File getUniqueFile(String baseName) {
        File file = new File(baseName);

        if (!file.exists()) {
            return file;
        }

        int counter = 1;
        String name = baseName.substring(0, baseName.lastIndexOf('.'));
        String ext = baseName.substring(baseName.lastIndexOf('.'));

        while (file.exists()) {
            file = new File(name + "_" + counter + ext);
            counter++;
        }

        System.out.println(baseName + " already exists → saving as " + file.getName());
        return file;
    }
}