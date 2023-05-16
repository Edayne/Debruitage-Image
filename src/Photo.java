import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.File;
import javax.swing.JFrame;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.ImageIcon;


public class Photo {
    //Attributes
    private BufferedImage photo;
    private Integer[] taille = new Integer[2]; //Tableau de taille 2 permettant de stocker l, nb lignes, et c, nb colonnes

    //Constructeurs
    public Photo() {
        this.photo = null;
        try {
            this.photo = ImageIO.read(new File("../donnees/lena.png"));
        } catch (IOException e) {
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

    //Main
    public static void main(String[] args) {
        Photo image = new Photo();
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(image.photo)));
        frame.pack();
        frame.setVisible(true);

    }

    

}
