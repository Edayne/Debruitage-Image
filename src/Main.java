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
import java.util.Scanner;

public class Main {
	// ImageIO.read(PerformanceTest.class.getResource("donnees/lena.jpg"));
	public static void main(String[] args) throws IOException {
        //Instances des classes utiles
		Photo lena = new Photo("donnees/lena.png"); //Stocke l'image et les méthodes des patchs
        ACP outilsAcp = new ACP(); //Boîte à outils mathématique

        // double sigma2 = 20.0;
        // double sigma = Math.sqrt(sigma2);
        // for (int i=1; i<4; i++) {
        //     //Bruitage de l'image
        //     sigma2 = i*10.0;
        //     sigma = Math.sqrt(sigma2);
        //     lena.noising(lena.getPhoto(), sigma2);

        //     //Affichage d'une image
        //     JFrame frame = new JFrame();
        //     frame.getContentPane().setLayout(new FlowLayout());
        //     frame.getContentPane().add(new JLabel(new ImageIcon(lena.getPhoto())));
        //     frame.getContentPane().add(new JLabel(new ImageIcon(lena.getPhotoBruitee())));
        //     frame.setTitle("Image bruitée : sigma^2 = "+ sigma2);
        //     frame.pack();
        //     frame.setVisible(true);
        // }

        //Bruitage de l'image
        Scanner scanner = new Scanner(System.in);
        System.out.print("Entrez sigma : ");
        double sigma = scanner.nextDouble();
        double sigma2 = sigma*sigma;
        lena.noising(lena.getPhoto(), sigma);

        //Affichage d'une image
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(lena.getPhoto())));
        frame.getContentPane().add(new JLabel(new ImageIcon(lena.getPhotoBruitee())));
        frame.setTitle("Image bruitée : sigma^2 = "+ sigma2);
        frame.pack();
        frame.setVisible(false);

        //Extraction des patchs et vectorisation
        System.out.print("\nEntrez la taille des patchs que vous souhaitez utiliser (évitez de dépasser 10-15): ");
        int taillePatch = scanner.nextInt();
        scanner.close();

        System.out.println("\nDémarrage de l'extraction des patchs... (s = "+taillePatch+")");
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
        ArrayList<double[]> listDebDurV = lena.ImageDebr(projSeuilDurV, mV, baseACP);
        ArrayList<double[][]> listDebDurV2 = lena.unvectorizeList(listDebDurV);
        BufferedImage imageDurV2 = lena.reconstructPatch(listDebDurV2, posPatchs);
        //BufferedImage imageDurV = lena.toBufferedImage(listDebDurV); 
        System.out.println("\tTerminé !");

        System.out.print("\tSeuil B dur...");
        ArrayList<double[]> listDebDurB = lena.ImageDebr(projSeuilDurB, mV, baseACP);
        ArrayList<double[][]> listDebDurB2 = lena.unvectorizeList(listDebDurB);
        BufferedImage imageDurB = lena.reconstructPatch(listDebDurB2, posPatchs);
        System.out.println("\tTerminé !");

        System.out.print("\tSeuil V doux...");
        ArrayList<double[]> listDebDouxV = lena.ImageDebr(projSeuilDouxV, mV, baseACP);
        ArrayList<double[][]> listDebDouxV2 = lena.unvectorizeList(listDebDouxV);
        BufferedImage imageDouxV = lena.reconstructPatch(listDebDouxV2, posPatchs);
        //BufferedImage imageDouxV = lena.toBufferedImage(listDebDouxV);
        System.out.println("\tTerminé !");

        System.out.print("\tSeuil B doux...");
        ArrayList<double[]> listDebDouxB = lena.ImageDebr(projSeuilDouxB, mV, baseACP);
        ArrayList<double[][]> listDebDouxB2 = lena.unvectorizeList(listDebDouxB);
        BufferedImage imageDouxB = lena.reconstructPatch(listDebDouxB2, posPatchs);
        System.out.println("\tTerminé !");

        System.out.println("Fin du débruitage !\n");

        //Calcul des erreurs
        System.out.println("Calcul des erreurs... (entre l'image initiale et l'image restaurée)");
        double[] listErreurMSE = new double[4];
        double[] listErreurPSNR = new double[4];

        listErreurMSE[0] = outilsAcp.calculateMSE(lena.getPhoto(), imageDurV2);
        listErreurMSE[1] = outilsAcp.calculateMSE(lena.getPhoto(), imageDurB);
        listErreurMSE[2] = outilsAcp.calculateMSE(lena.getPhoto(), imageDouxV);
        listErreurMSE[3] = outilsAcp.calculateMSE(lena.getPhoto(), imageDouxB);
        
        listErreurPSNR[0] = outilsAcp.calculatePSNR(lena.getPhoto(), imageDurV2);
        listErreurPSNR[1] = outilsAcp.calculatePSNR(lena.getPhoto(), imageDurB);
        listErreurPSNR[2] = outilsAcp.calculatePSNR(lena.getPhoto(), imageDouxV);
        listErreurPSNR[3] = outilsAcp.calculatePSNR(lena.getPhoto(), imageDouxB);
        
        System.out.println("\tL'erreur entre l'image initiale et l'image bruitée est (MSE) : " + outilsAcp.calculateMSE(lena.getPhoto(), lena.getPhotoBruitee()));
        System.out.println("\tL'erreur entre l'image initiale et l'image bruitée est (PSNR) : " + outilsAcp.calculatePSNR(lena.getPhoto(), lena.getPhotoBruitee()));
        //System.out.println("\n\tL'erreur pour le seuillage dur pour le seuil V (MSE) : " + listErreurMSE[0]);
        //System.out.println("\tL'erreur pour le seuillage dur pour le seuil V (PSNR) : " + listErreurPSNR[0]);
        System.out.println("\n\tL'erreur pour le seuillage dur pour le seuil B (MSE) : " + listErreurMSE[1]);
        System.out.println("\tL'erreur pour le seuillage dur pour le seuil B (PSNR) : " + listErreurPSNR[1]);
        //System.out.println("\n\tL'erreur pour le seuillage doux pour le seuil V (MSE) : " + listErreurMSE[2]);
        //System.out.println("\tL'erreur pour le seuillage doux pour le seuil V (PSNR) : " + listErreurPSNR[2]);
        System.out.println("\n\tL'erreur pour le seuillage doux pour le seuil B (MSE) : " + listErreurMSE[3]);
        System.out.println("\tL'erreur pour le seuillage doux pour le seuil B (PSNR) : " + listErreurPSNR[3]);
        System.out.println("Fin des erreurs !\n");
        
        //Ré-affichage de l'image restaurée
        System.out.println("Affichage des images restaurées...");
        // BufferedImage nvImageDurV = lena.toBufferedImage(listDebDurV);
        // BufferedImage nvImageDurB = lena.toBufferedImage(listDebDurB);
        // BufferedImage nvImageDouxV = lena.toBufferedImage(listDebDouxV);
        // BufferedImage nvImageDouxB = lena.toBufferedImage(listDebDouxB);


        JFrame frameDurV = new JFrame();
        frameDurV.getContentPane().setLayout(new FlowLayout());
        frameDurV.setTitle("Dur V");
        frameDurV.getContentPane().add(new JLabel(new ImageIcon(imageDurV2)));
        frameDurV.pack();
        frameDurV.setVisible(false);

        JFrame frameDurB = new JFrame();
        frameDurB.getContentPane().setLayout(new FlowLayout());
        frameDurB.setTitle("Dur B");
        frameDurB.getContentPane().add(new JLabel(new ImageIcon(imageDurB)));
        frameDurB.pack();
        frameDurB.setVisible(false);

        JFrame frameDouxV = new JFrame();
        frameDouxV.getContentPane().setLayout(new FlowLayout());
        frameDouxV.setTitle("Doux V");
        frameDouxV.getContentPane().add(new JLabel(new ImageIcon(imageDouxV)));
        frameDouxV.pack();
        frameDouxV.setVisible(false);

        JFrame frameDouxB = new JFrame();
        frameDouxB.getContentPane().setLayout(new FlowLayout());
        frameDouxB.setTitle("Doux B");
        frameDouxB.getContentPane().add(new JLabel(new ImageIcon(imageDouxB)));
        frameDouxB.pack();
        frameDouxB.setVisible(false);

        System.out.println("\nFIN!");

        // System.out.println(listVectPatchCent.get(0)[0]);
        // double somme = 0;

        // for (int i=0; i<projection[0].length; i++){
        //     somme += projection[0][i]*baseACP[i][0]; //somme des alpha_i pour V1 
        // }
        // System.out.println(somme); 
    }
}