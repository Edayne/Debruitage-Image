import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import javax.swing.ImageIcon;

public class Main {

	// ImageIO.read(PerformanceTest.class.getResource("donnees/lena.jpg"));
	public static void main(String[] args) throws IOException {
        //Instances des classes utiles
		Photo lena = new Photo("donnees/lena.png"); //Stocke l'image et les méthodes des patchs
        ACP outilsAcp = new ACP(); //Boîte à outils mathématique

        //Bruitage de l'image
        double sigma2 = 20.0;
        double sigma = Math.sqrt(sigma2);
		lena.noising(lena.getPhoto(), sigma2);

        //Affichage d'une image
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(lena.getPhotoBruitee())));
        frame.pack();
        frame.setVisible(true);

        //Extraction des patchs et vectorisation
        List<int[][]> listPatches = lena.extractPatchs(lena.getPhotoBruitee(), 20);
        List<int[]> listVectPatch = lena.vectorPatchs(listPatches);
        List<double[]> listVectPatchCent = outilsAcp.calculerVecteursCentres(listVectPatch);

        //Application de l'ACP
        double[] mV = outilsAcp.calculVecteurMoyen(listVectPatch);
        double[][] baseACP = outilsAcp.acp(listVectPatch);
        double[][] projection = outilsAcp.proj(baseACP, listVectPatchCent); //Stocke les alpha_i, coordonnées des vecteurs dans la base de l'ACP

        //Tous les seuillages possibles
        int nbPixels = lena.getPhoto().getWidth() * lena.getPhoto().getHeight();
        double seuilV = outilsAcp.VisuShrink(nbPixels, sigma);
        double seuilB = outilsAcp.BayesShrink(sigma, lena.getPhoto());

        double[][] projSeuilDurV = outilsAcp.seuillageDur(seuilV, projection);
        double[][] projSeuilDurB = outilsAcp.seuillageDur(seuilB, projection);
        double[][] projSeuilDouxV = outilsAcp.seuillageDoux(seuilV, projection);
        double[][] projSeuilDouxB = outilsAcp.seuillageDoux(seuilB, projection);

        //Débruitage selon les différents seuillages
        List<double[]> listDebDurV = lena.ImageDebr(projSeuilDurV, mV, baseACP);
        BufferedImage imageDurV = lena.toBufferedImage(listDebDurV);
        List<double[]> listDebDurB = lena.ImageDebr(projSeuilDurB, mV, baseACP);
        BufferedImage imageDurB = lena.toBufferedImage(listDebDurB);
        List<double[]> listDebDouxV = lena.ImageDebr(projSeuilDouxV, mV, baseACP);
        BufferedImage imageDouxV = lena.toBufferedImage(listDebDouxV);
        List<double[]> listDebDouxB = lena.ImageDebr(projSeuilDouxB, mV, baseACP);
        BufferedImage imageDouxB = lena.toBufferedImage(listDebDouxB);

        //Calcul des erreurs
        double[] listErreurMSE = new double[4];
        double[] listErreurPSNR = new double[4];

        listErreurMSE[0] = outilsAcp.calculateMSE(lena.getPhoto(), imageDurV);
        listErreurMSE[1] = outilsAcp.calculateMSE(lena.getPhoto(), imageDurB);
        listErreurMSE[2] = outilsAcp.calculateMSE(lena.getPhoto(), imageDouxV);
        listErreurMSE[3] = outilsAcp.calculateMSE(lena.getPhoto(), imageDouxB);
        
        listErreurPSNR[0] = outilsAcp.calculatePSNR(lena.getPhoto(), imageDurV);
        listErreurPSNR[1] = outilsAcp.calculatePSNR(lena.getPhoto(), imageDurB);
        listErreurPSNR[2] = outilsAcp.calculatePSNR(lena.getPhoto(), imageDouxV);
        listErreurPSNR[3] = outilsAcp.calculatePSNR(lena.getPhoto(), imageDouxV);

        for (int i=0; i<4; i++){
            System.out.println("L'erreur pour le seuillage dur");
        }
    }
}
