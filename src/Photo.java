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
            this.photo = ImageIO.read(new File("donnees/lena.png")); //ptet mettre ça dans le Main, à voir
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
        Photo image = new Photo();
        image.noising(image.photo, 20);
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(image.photoBruitee)));
        frame.pack();
        frame.setVisible(true);
    }
}
