import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.IOException;

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
    public Photo() {
        this.photo = null;
        try {
            this.photo = ImageIO.read(new File("../donnees/lena.png")); //ptet mettre ça dans le Main, à voir
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
     * Extrait une collection de patchs d'une image bruitée
     * @param s Entier représentant la taille d'un patch
     * @return une liste dynamique de patchs
     */
    private static int getPixelValue(BufferedImage image, int l, int c) {
        int rgb = image.getRGB(c, l);
        return (rgb >> 16) & 0xFF; // Extracting red channel value (assuming 8-bit/channel grayscale image)
    }

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

    private static void updatePospat(int[][] pospat, List<Integer> listL, List<Integer> listC) {
        for (int i = 0; i < listL.size(); i++) {
            for (int j = 0; j < listC.size(); j++) {
                pospat[listL.get(i)][listC.get(j)]++;
            }
        }
    }
    
    
    //Main
    public static void main(String[] args) {
        //RINE
    }

    

}