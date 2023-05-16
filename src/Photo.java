import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.File;


public class Photo {
    //Attributes
    private BufferedImage photo;
    private Integer[] taille = new Integer[2]; //Tableau de taille 2 permettant de stocker l, nb lignes, et c, nb colonnes

    //Constructeurs
    public Photo() {
        this.photo = null;
        try {
            this.photo = ImageIO.read(new File("donnees/lena.jpg"));
        } catch (IOException e) {
        }
    }

    //Methodes


}
