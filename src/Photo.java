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
    private Integer[] taille = new Integer[2]; //Tableau de taille 2 permettant de stocker l, nb lignes, et c, nb colonnes

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

   /* 
   */ /**
     * 
     * @return BufferedImage return la photo bruité
     *//* */
    public void noising(BufferedImage photo, double sigma) {
        this.photoBruitee = photo;
        taille[0] = photo.getHeight();
        taille[1] = photo.getWidth();
        for(int i=0; i<taille[0];i++){
            for(int j=0; j<taille[1];j++){
                photoBruitee.setRGB(i, j, 0);
                Random random = new Random();
                photoBruitee.setRGB(i, j, (int) (random.nextGaussian()*sigma));
            }
        }
    }
    
    
    //Main
    public static void main(String[] args) {
        Photo image = new Photo();
        image.noising(image.photo, 10);
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(image.photoBruitee)));
        frame.pack();
        frame.setVisible(true);
    }
}
