import java.util.ArrayList;
import java.util.List;
import java.awt.font.ImageGraphicAttribute;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Random;


public class Photo {
    //Attributes
    private BufferedImage photo;
    private BufferedImage photoBruitee;
    private int nbL;
    private int nbC;

    //Constructeurs
    public Photo(String chemin) {
        try {
            this.photo = ImageIO.read(new File(chemin)); //ptet mettre ça dans le Main, à voir
        } catch (IOException e) {
            System.out.println("Fichier introuvable, réessayez !");
        }
    }

    //Methodes
    /**
     * @return BufferedImage return the photo
     */
    public BufferedImage getPhoto() {
        return photo;
    }

    /**
     * @param photo the photo to set
     */
    public void setPhoto(BufferedImage photo) {
        this.photo = photo;
    }
    /**
     * @return BufferedImage return the photoBruitee
     */
    public BufferedImage getPhotoBruitee() {
        return photoBruitee;
    }

    /**
     * @param photoBruitee the photoBruitee to set
     */
    public void setPhotoBruitee(BufferedImage photoBruitee) {
        this.photoBruitee = photoBruitee;
    }

    /**
     * @return int return the nbL
     */
    public int getNbL() {
        return nbL;
    }

    /**
     * @param nbL the nbL to set
     */
    public void setNbL(int nbL) {
        this.nbL = nbL;
    }

    /**
     * @return int return the nbC
     */
    public int getNbC() {
        return nbC;
    }

    /**
     * @param nbC the nbC to set
     */
    public void setNbC(int nbC) {
        this.nbC = nbC;
    }

    /**
     * @return BufferedImage return la photo bruitée
     */
    public void noising(BufferedImage photo, double sigma) {
        nbL = photo.getHeight();
        nbC = photo.getWidth();
        this.photoBruitee = new BufferedImage(nbC, nbL, BufferedImage.TYPE_INT_RGB);
        for(int i=0; i<nbL;i++){
            for(int j=0; j<nbC;j++){
                Random random = new Random();
                int newPixel ;
                newPixel = (photo.getRGB(i,j)& 0xff) +(int) (random.nextGaussian()*sigma);
                if(newPixel < 0){
                    newPixel=0;
                }else if(newPixel>255){
                    newPixel=255;
                }
                int newGreyPixel =  (newPixel << 16) | (newPixel << 8) | newPixel;
                photoBruitee.setRGB(i, j, newGreyPixel);
            }
        }
    }

    /**
     * Convertit la valeur d'un pixel en un entier compréhensible entre 0 et 255
     * @param image L'image manipulée
     * @param l nombre de lignes de image
     * @param c nombre de colonnes de image
     * @return Entier correspondant au channel rouge de l'image
     */
    public int getPixelValue(BufferedImage image, int l, int c) {
        int rgb = image.getRGB(c, l);
        return (rgb >> 16) & 0xFF; // Extracting red channel value (assuming 8-bit/channel grayscale image)
    }

    /**
     * Extrait une collection de patchs d'une image bruitée
     * @param s Entier représentant la taille d'un patch
     * @return une liste dynamique de patchs
     */
    public List<int[][]> extractPatchs(BufferedImage photo, int s) {
        int l = photo.getHeight();
        int c = photo.getWidth();
        List<int[][]> listPatches = new ArrayList<>();
        int [][] pospat = new int[l][c];
        for(int i=0; i<l-s;i++){
            for(int j=0; j<c-s;j++){
                List<Integer> listL = new ArrayList<>();
                List<Integer> listC = new ArrayList<>();
                for(int k=i; k< i+s;k++){
                    listL.add((k%l)+1);
                }
                for(int k=j ; k<j+s; k++){
                    listC.add((k%c)+1);
                }

                int [][] patch = new int[s][s];
                for(int k=0; k< s; k++){
                    for(int m=0; m< s; m++){
                        patch[k][m] = getPixelValue(photo, listL.get(k)-1, listC.get(m)-1);
                    }
                }

                listPatches.add(patch);
                updatePospat(pospat,listL,listC);
            }
        }
        return listPatches;
    }

    /**
     * Fonction secondaire de extractPatchs() permettant de mettre à jour la position des patchs
     * @param pospat Matrice contenant la position des patchs
     * @param listL 
     * @param listC
     */
    public static void updatePospat(int[][] pospat, List<Integer> listL, List<Integer> listC) {
        for (int i = 0; i < listL.size(); i++) {
            for (int j = 0; j < listC.size(); j++) {
                pospat[listL.get(i)][listC.get(j)]++;
            }
        }
    }
    
    /**
     * Convertit une matrice en BufferedImage
     * @param matrix matrice d'entiers
     * @return Une BufferedImage
     */
    public BufferedImage arrayToImage(int[][] matrix) {
        int width = matrix.length;
        int height = matrix[0].length;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                int pixel=matrix[i][j];
                System.out.println("The pixel in Matrix: "+pixel);
                bufferedImage.setRGB(i, j, pixel);
                System.out.println("The pixel in BufferedImage: " + bufferedImage.getRGB(i, j));
            }
        }
        return bufferedImage;
    }

    /**
     * Convertit une BufferedImage en matric
     * @param bufferedImage une image
     * @return une matrice d'entiers
     */
    public int[][] imageToArray(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth(null);
        int height = bufferedImage.getHeight(null);
        int[][] pixels = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixels[i][j] = bufferedImage.getRGB(i, j);
            }
        }
        return pixels;
    }

    /**
     * Reconstruit une image à partir d'une collection de patchs
     * @param listPatchs Liste des patchs extraits
     * @param posPatchs Matrice contenant le nombre de patchs dans lequel le pixel à la coordonnée associée apparait
     * @return L'image recréée
     */
    public BufferedImage reconstructPatch(List<int[][]> listPatchs, int[][] posPatchs) {
        int s = listPatchs.get(0).length; //Taille d'un patch
        int L = posPatchs.length;
        int C = posPatchs[0].length;
        int[][] sommePatch = new int[L][C];
        for (int j = 0; j < C-s; j++){
            for (int i = 0; i < L-s; i++) {
                int[][] patchActuel = new int[s][s];
                for (int iPatch = 0; iPatch<s; iPatch++){
                    for (int jPatch = 0; jPatch<s; jPatch++) {
                        sommePatch[i][j] += patchActuel[iPatch][jPatch];
                    }
                }
            }
        }
        for (int i = 0; i < L; i++) {
            for (int j = 0; j < C; j++) {
                sommePatch[i][j] /= posPatchs[i][j];
            }
        }
        
        BufferedImage image = arrayToImage(sommePatch); //On convertit la matrice en un format d'image
        return image;
    }

    /**
     * Découpe l'image en une collection d'images de taille W
     * @param photo L'image initiale
     * @param tailleW La taille des imagettes extraites
     * @return Une collection de petites images
     */
    public ArrayList<BufferedImage> decoupeImage(BufferedImage photo, int tailleW){
        ArrayList<BufferedImage> listImagette = new ArrayList<>();
        int L = photo.getHeight();
        int C = photo.getWidth();
        int nbImL = L/tailleW; 
        int nbImC = C/tailleW;
        for (int i = 0; i < nbImL; i++) {
            for (int j = 0; j < nbImC; j++) {
                BufferedImage imagette = photo.getSubimage(i*tailleW, j*tailleW, tailleW, tailleW);
                listImagette.add(imagette);
            }
        }
        return listImagette;
    }

    /**
     * Transforme une liste de int[][] en une liste de int[], soit une vectorisation de matrice
     * @param listPatchs
     * @return
     */
    public List<int[]> vectorPatchs(List<int[][]> listPatchs) {
        List<int[]> listPatchVect = new ArrayList<>();
        for (int[][] patch : listPatchs) {
            int s = patch.length;
            int[] patchVect = new int[s*s];

            int k=0; //Indice de parcours de la version vectorisée du patch
            for (int i=0; i<s; i++){
                for (int j=0; j<s; j++){
                    patchVect[k] = patch[i][j];
                    k++;
                }
            }
        }

        return listPatchVect;
    }
}