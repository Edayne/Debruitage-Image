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
        frame.getContentPane().add(new JLabel(new ImageIcon(lena.getPhoto())));
        frame.getContentPane().add(new JLabel(new ImageIcon(lena.getPhotoBruitee())));
        frame.setTitle("Image bruitée : sigma^2 = "+ sigma2);
        frame.pack();
        frame.setVisible(true);

        //Extraction des patchs et vectorisation
        int taillePatch = 3;

        System.out.println("Démarrage de l'extraction des patchs... (s = "+taillePatch+")");
        ArrayList<int[][]> listPatches = lena.extractPatchs(lena.getPhotoBruitee(), taillePatch);
        int[][] posPatchs = lena.extractPosPatchs(lena.getPhotoBruitee(), taillePatch);
        System.out.println("\tNombre de patchs extraits = " + listPatches.size());
        System.out.println("\tExtraction terminée !\n");

        System.out.println("\tDébut de la vectorisation...");
        ArrayList<int[]> listVectPatch = lena.vectorPatchs(listPatches);
        System.out.println("\tVectorisation terminée !\n");
        
        System.out.println("\tCalcul des vecteurs centrés...");
        ArrayList<double[]> listVectPatchCent = outilsAcp.calculerVecteursCentres(listVectPatch);
        System.out.println("\tCréation de la liste des vecteurs centrés terminée !");

        System.out.println("Fin de l'extraction des patchs !\n");

        //Application de l'ACP
        System.out.println("Démarrage de l'ACP...");

        double[] mV = outilsAcp.calculVecteurMoyen(listVectPatch);

        System.out.println("\tCalcul de la base de projection...");
        double[][] baseACP = outilsAcp.acp(listVectPatch);
        System.out.println("\tCréation de la base orthonormale !\n");

        System.out.println("\tProjection des vecteurs dans la base de l'ACP...");
        double[][] projection = outilsAcp.proj(baseACP, listVectPatchCent); //Stocke les alpha_i, coordonnées des vecteurs centrés dans la base de l'ACP
        
        System.out.println("\tProjection réussie !");
        System.out.println("Fin de l'ACP !\n");

        //Tous les seuillages possibles
        System.out.println("Début du seuillage des coefficients des vecteurs post-projection...");
        int nbPixels = lena.getPhoto().getWidth() * lena.getPhoto().getHeight();
        double seuilV = outilsAcp.VisuShrink(nbPixels, sigma);
        double seuilB = outilsAcp.BayesShrink(sigma, lena.getPhoto());

        System.out.println("Seuils utilisés :");
        System.out.println("\tSeuil VisuShrink = " + seuilV);
        System.out.println("\tSeuil BayesShrink = " + seuilB);

        System.out.print("\n\tSeuillage dur avec le seuil V...");
        double[][] projSeuilDurV = outilsAcp.seuillageDur(seuilV, projection);
        System.out.println("\tTerminé !");
        System.out.print("\tSeuillage dur avec le seuil B...");
        double[][] projSeuilDurB = outilsAcp.seuillageDur(seuilB, projection);
        System.out.println("\tTerminé !");
        System.out.print("\tSeuillage doux avec le seuil V...");
        double[][] projSeuilDouxV = outilsAcp.seuillageDoux(seuilV, projection);
        System.out.println("\tTerminé !");
        System.out.print("\tSeuillage doux avec le seuil B...");
        double[][] projSeuilDouxB = outilsAcp.seuillageDoux(seuilB, projection);
        System.out.println("\tTerminé !");

        System.out.println("Fin du seuillage !\n");

        //Débruitage selon les différents seuillages
        System.out.println("Début du débruitage...");

        System.out.print("\tSeuil V dur...");
        List<double[]> listDebDurV = lena.ImageDebr(projSeuilDurV, mV, baseACP);
        BufferedImage imageDurV = lena.toBufferedImage(listDebDurV); 
        //PB ICI, listDebDurV devrait contenir la liste des patchs vectorisés donc une liste de taille 250k à peu pres mais il n'y a que s^2=9 vecteurs on dirait
        System.out.println("\tTerminé !");
        System.out.print("\tSeuil B dur...");
        List<double[]> listDebDurB = lena.ImageDebr(projSeuilDurB, mV, baseACP);
        BufferedImage imageDurB = lena.toBufferedImage(listDebDurB);
        System.out.println("\tTerminé !");
        System.out.print("\tSeuil V doux...");
        List<double[]> listDebDouxV = lena.ImageDebr(projSeuilDouxV, mV, baseACP);
        BufferedImage imageDouxV = lena.toBufferedImage(listDebDouxV);
        System.out.println("\tTerminé !");
        System.out.print("\tSeuil B doux...");
        List<double[]> listDebDouxB = lena.ImageDebr(projSeuilDouxB, mV, baseACP);
        BufferedImage imageDouxB = lena.toBufferedImage(listDebDouxB);
        System.out.println("\tTerminé !");
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
        frameDurV.setTitle("Dur V");
        frameDurV.getContentPane().add(new JLabel(new ImageIcon(nvImageDurV)));
        frameDurV.pack();
        frameDurV.setVisible(true);

        JFrame frameDurB = new JFrame();
        frameDurB.getContentPane().setLayout(new FlowLayout());
        frameDurB.setTitle("Dur B");
        frameDurB.getContentPane().add(new JLabel(new ImageIcon(nvImageDurB)));
        frameDurB.pack();
        frameDurB.setVisible(true);

        JFrame frameDouxV = new JFrame();
        frameDouxV.getContentPane().setLayout(new FlowLayout());
        frameDouxV.setTitle("Doux V");
        frameDouxV.getContentPane().add(new JLabel(new ImageIcon(nvImageDouxV)));
        frameDouxV.pack();
        frameDouxV.setVisible(true);

        JFrame frameDouxB = new JFrame();
        frameDouxB.getContentPane().setLayout(new FlowLayout());
        frameDouxB.setTitle("Doux B");
        frameDouxB.getContentPane().add(new JLabel(new ImageIcon(nvImageDouxB)));
        frameDouxB.pack();
        frameDouxB.setVisible(true);
    }
}
