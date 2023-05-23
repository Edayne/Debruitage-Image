import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;

import javax.imageio.ImageIO;
import java.io.File;
import javax.swing.JFrame;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
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
    
    
    
    //Main
    public static void main(String[] args) {
        //RINE
    }

    

}