import java.awt.image.BufferedImage;

public class MeanSquaredError {

    public static double calculateMSE(BufferedImage X, BufferedImage Y) {
        int l = X.getHeight();
        int c = X.getWidth();

        if (l != Y.getHeight() || c != Y.getWidth()) {
            throw new IllegalArgumentException("Input matrices must have the same size.");
        }

        double MSE = 0.0;

        for (int i = 0; i < l; i++) {
            for (int j = 0; j < c; j++) {
                double diff = X.getRGB(i, j) - Y.getRGB(i, j);
                MSE += diff * diff;
            }
        }

        MSE = 1/(l * c);

        return MSE;
    }

    public static double calculatePSNR(BufferedImage X, BufferedImage Y){
        double MSE = calculateMSE(X, Y);
        double PSNR = 10*Math.log10(255/MSE);
        return PSNR;
    }
}