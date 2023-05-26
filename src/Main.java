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
        System.out.println("Démarrage de l'extraction des patchs...");
        List<int[][]> listPatches = lena.extractPatchs(lena.getPhotoBruitee(), 20);
        System.out.println("\textract terminé");
        List<int[]> listVectPatch = lena.vectorPatchs(listPatches);
        System.out.println("\tvectorisation terminée");
        List<double[]> listVectPatchCent = outilsAcp.calculerVecteursCentres(listVectPatch);
        System.out.println("\tcentralisation terminée");
        System.out.println("Fin de l'extraction des patchs !n");

        //Application de l'ACP
        System.out.println("Démarrage de l'ACP...");
        double[] mV = outilsAcp.calculVecteurMoyen(listVectPatch);
        double[][] baseACP = outilsAcp.acp(listVectPatch);
        double[][] projection = outilsAcp.proj(baseACP, listVectPatchCent); //Stocke les alpha_i, coordonnées des vecteurs dans la base de l'ACP
        System.out.println("Fin de l'ACP !\n");

        //Tous les seuillages possibles
        System.out.println("Calcul des seuils...");
        int nbPixels = lena.getPhoto().getWidth() * lena.getPhoto().getHeight();
        double seuilV = outilsAcp.VisuShrink(nbPixels, sigma);
        double seuilB = outilsAcp.BayesShrink(sigma, lena.getPhoto());

        double[][] projSeuilDurV = outilsAcp.seuillageDur(seuilV, projection);
        double[][] projSeuilDurB = outilsAcp.seuillageDur(seuilB, projection);
        double[][] projSeuilDouxV = outilsAcp.seuillageDoux(seuilV, projection);
        double[][] projSeuilDouxB = outilsAcp.seuillageDoux(seuilB, projection);

        //Débruitage selon les différents seuillages
        System.out.println("Débruitage en fonction des seuils...");
        List<double[]> listDebDurV = lena.ImageDebr(projSeuilDurV, mV, baseACP);
        BufferedImage imageDurV = lena.toBufferedImage(listDebDurV);
        List<double[]> listDebDurB = lena.ImageDebr(projSeuilDurB, mV, baseACP);
        BufferedImage imageDurB = lena.toBufferedImage(listDebDurB);
        List<double[]> listDebDouxV = lena.ImageDebr(projSeuilDouxV, mV, baseACP);
        BufferedImage imageDouxV = lena.toBufferedImage(listDebDouxV);
        List<double[]> listDebDouxB = lena.ImageDebr(projSeuilDouxB, mV, baseACP);
        BufferedImage imageDouxB = lena.toBufferedImage(listDebDouxB);
        System.out.println("Fin du débruitage !\n");

        //Calcul des erreurs
        System.out.println("Calcul des erreurs...");
        double[] listErreurMSE = new double[4];
        double[] listErreurPSNR = new double[4];

        listErreurMSE[0] = outilsAcp.calculateMSE(lena.getPhoto(), imageDurV);
        listErreurMSE[1] = outilsAcp.calculateMSE(lena.getPhoto(), imageDurB);
        listErreurMSE[2] = outilsAcp.calculateMSE(lena.getPhoto(), imageDouxV);
        listErreurMSE[3] = outilsAcp.calculateMSE(lena.getPhoto(), imageDouxB);
        
        listErreurPSNR[0] = outilsAcp.calculatePSNR(lena.getPhoto(), imageDurV);
        listErreurPSNR[1] = outilsAcp.calculatePSNR(lena.getPhoto(), imageDurB);
        listErreurPSNR[2] = outilsAcp.calculatePSNR(lena.getPhoto(), imageDouxV);
        listErreurPSNR[3] = outilsAcp.calculatePSNR(lena.getPhoto(), imageDouxB);

        
        System.out.println("L'erreur pour le seuillage dur pour le seuil V (MSE) :" + listErreurMSE[0]);
        System.out.println("L'erreur pour le seuillage dur pour le seuil V (PSNR) :" + listErreurPSNR[0]);
        System.out.println("L'erreur pour le seuillage dur pour le seuil B (MSE) :" + listErreurMSE[1]);
        System.out.println("L'erreur pour le seuillage dur pour le seuil B (PSNR) :" + listErreurPSNR[1]);
        System.out.println("L'erreur pour le seuillage doux pour le seuil V (MSE) :" + listErreurMSE[2]);
        System.out.println("L'erreur pour le seuillage doux pour le seuil V (PSNR) :" + listErreurPSNR[2]);
        System.out.println("L'erreur pour le seuillage doux pour le seuil B (MSE) :" + listErreurMSE[3]);
        System.out.println("L'erreur pour le seuillage doux pour le seuil B (PSNR) :" + listErreurPSNR[3]);
        System.out.println("Fin des erreurs !\n");
        
        //Ré-affichage de l'image restaurée
        System.out.println("Affichage des images restaurées...");
        BufferedImage nvImageDurV = lena.toBufferedImage(listDebDurV);
        BufferedImage nvImageDurB = lena.toBufferedImage(listDebDurB);
        BufferedImage nvImageDouxV = lena.toBufferedImage(listDebDouxV);
        BufferedImage nvImageDouxB = lena.toBufferedImage(listDebDouxB);

        JFrame frameDurV = new JFrame();
        frameDurV.getContentPane().setLayout(new FlowLayout());
        frameDurV.getContentPane().add(new JLabel(new ImageIcon(nvImageDurV)));
        frameDurV.pack();
        frameDurV.setVisible(true);

        JFrame frameDurB = new JFrame();
        frameDurB.getContentPane().setLayout(new FlowLayout());
        frameDurB.getContentPane().add(new JLabel(new ImageIcon(nvImageDurB)));
        frameDurB.pack();
        frameDurB.setVisible(true);

        JFrame frameDouxV = new JFrame();
        frameDouxV.getContentPane().setLayout(new FlowLayout());
        frameDouxV.getContentPane().add(new JLabel(new ImageIcon(nvImageDouxV)));
        frameDouxV.pack();
        frameDouxV.setVisible(true);

        JFrame frameDouxB = new JFrame();
        frameDouxB.getContentPane().setLayout(new FlowLayout());
        frameDouxB.getContentPane().add(new JLabel(new ImageIcon(nvImageDouxB)));
        frameDouxB.pack();
        frameDouxB.setVisible(true);
    }
}
