import javafx.stage.Stage;

import javafx.geometry.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

public class Accueil extends Application {

	
    //private String cheminImage;
    String cheminImage;
    Photo imageEtudie;
    double sigma;
	ACP function=new ACP();

    

	@Override
    public void start(Stage primaryStage) throws Exception {
    	
//----------------------------------------------------------------------------------------------------        
        //creation box pour les images
        HBox boxImage=new HBox();
        
        // Création des ImageView
        ImageView imageView1 = new ImageView();
        ImageView imageView2 = new ImageView();
        
        ImageView imageView3 = new ImageView();
        
        // Chargement de l'image à partir d'un fichier
        BufferedImage image_1= ImageIO.read(new File("donnees/default.jpg"));
        BufferedImage image_2= ImageIO.read(new File("donnees/default.jpg"));
        BufferedImage image_3= ImageIO.read(new File("donnees/default.jpg"));
        
        //conversion buffered image en image javafx
        WritableImage image1 = SwingFXUtils.toFXImage(image_1, null);
        WritableImage image2 = SwingFXUtils.toFXImage(image_2, null);
        WritableImage image3 = SwingFXUtils.toFXImage(image_3, null);
        
		// Attribution de l'image à l'ImageView
        imageView1.setImage(image1);
        imageView2.setImage(image2);
        imageView3.setImage(image3);
        
        //ajout des images a la box image
        boxImage.getChildren().add(imageView1);
        boxImage.getChildren().add(imageView2);
        boxImage.getChildren().add(imageView3);
        
        //ajout de marge
        Insets margin = new Insets(12);
        HBox.setMargin(imageView1, margin);
        HBox.setMargin(imageView2, margin);
        HBox.setMargin(imageView3, margin);      
        boxImage.getBorder();
//-----------------------------------------------------------------------------------------------------------------------------------------------------------
        //creation d'une vbox pour les fonctions
        HBox boxFunction=new HBox();

//------------------------------------------------------------------------------------------------------------------------------
        TextField t1=new TextField();
        Label l1=new Label("Veuillez rentrer le chemin absolu de l'image : ");
        Button btn_image=new Button("Valider");
        
        GridPane grid1=new GridPane();
        grid1.add(l1, 0, 0);
        grid1.add(t1, 1, 0);
        grid1.add(btn_image, 1, 3);

        GridPane.setMargin(l1, margin);

        boxFunction.getChildren().add(grid1);

        btn_image.setOnAction(event->{
        	if(t1.getText().isEmpty()) {
        		Alert alerte = new Alert(AlertType.INFORMATION);
              	alerte.setTitle("Erreur");
              	alerte.setHeaderText(null);
              	alerte.setContentText("Veuiller remplir tous les champs.");
              	alerte.showAndWait();
        	}else {
                 String chemin =t1.getText();
                 cheminImage=chemin;
                 Photo p=new Photo(cheminImage);
                 imageEtudie=p;
                try {
                	BufferedImage imaged=ImageIO.read(new File(chemin));
                    //conversion buffered image en image javafx
                    WritableImage imageOrigin = SwingFXUtils.toFXImage(imaged, null);  
            		// Attribution de l'image à l'ImageView
                    imageView1.setImage(imageOrigin);

                }catch(IOException e) {
                	Alert alerte = new Alert(AlertType.INFORMATION);
                  	alerte.setTitle("Erreur");
                  	alerte.setHeaderText(null);
                  	alerte.setContentText("Image introuvable.");
                  	alerte.showAndWait();
                }

        		
        	}
        	
        });
        
        
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
        TextField t2=new TextField();
        Label l2=new Label("Veuillez rentrer la valeur de sigma  : ");
        Button btn_sigma=new Button("Valider");
        
        GridPane grid2=new GridPane();
        grid2.add(l2, 0, 0);
        grid2.add(t2, 1, 0);
        grid2.add(btn_sigma, 1, 3);

        GridPane.setMargin(l2, margin);

        HBox.setMargin(grid1, margin);
        HBox.setMargin(grid2, margin);
        
        boxFunction.getChildren().add(grid2);

        btn_sigma.setOnAction(event->{ 
        	if(t2.getText().isEmpty()) {
        		Alert alerte = new Alert(AlertType.INFORMATION);
              	alerte.setTitle("Erreur");
              	alerte.setHeaderText(null);
              	alerte.setContentText("Veuiller remplir tous les champs.");
              	alerte.showAndWait();
        	}else {
                String sig=t2.getText();
                try {
                	float sigma=Float.parseFloat(sig);
                	this.sigma=sigma;
                	try {
                		Photo image = new Photo(cheminImage);
                        image.noising(image.getPhoto(), sigma);                                            
                        //conversion buffered image en image javafx                        
                        WritableImage imageBruitee = SwingFXUtils.toFXImage(image.getPhotoBruitee(), null);                    
                		// Attribution de l'image à l'ImageView                       
                        imageView2.setImage(imageBruitee);
                	}catch(Exception e) {
                		Alert alerte = new Alert(AlertType.INFORMATION);
                      	alerte.setTitle("Erreur");
                      	alerte.setHeaderText(null);
                      	alerte.setContentText("Veuillez choisir une image a bruitée.");
                      	alerte.showAndWait();
                	}                              	
                }catch(NumberFormatException e) {
                	Alert alerte = new Alert(AlertType.INFORMATION);
                	alerte.setTitle("Erreur");
	            	alerte.setHeaderText(null);
	           		alerte.setContentText("Veuillez entrer des réels!");	
	           		alerte.showAndWait();
	           		System.out.println("La chaîne de caractères n'est pas un nombre valide.");
                }       		
        	}              	        	
        });

//---------------------------------------------------------------------------------------------------------------------------------------------------------------
        Label l3=new Label("Veuillez selectioner le type de Patch:");
        ChoiceBox<String> t3 = new ChoiceBox<>();       
        t3.getItems().addAll("local", "global");
        
        Label l4=new Label("Veuillez selectioner le type de seuillage: ");
        ChoiceBox<String> t4 = new ChoiceBox<>();       
        t4.getItems().addAll("dur", "doux"); 
        
        Label l5=new Label("Veuillez selectioner le type de seuil: ");
        ChoiceBox<String> t5 = new ChoiceBox<>();       
        t5.getItems().addAll("seuilV", "seuilB"); 
        
        TextField t6=new TextField();
        Label l6=new Label("Veuillez rentrer la valeur pour la taille des patch  : ");
        
        Label l7=new Label("Valeur du seuil :");
      
        Label l8=new Label("Valeur de l'erreur MSE :");
        
        Label l9=new Label("Valeur de l'erreur PSNR :");

        
       
        
        Button btn_debruitage=new Button("Débruiter");
        GridPane grid3=new GridPane();
        grid3.add(l3, 0, 0);
        grid3.add(t3, 1, 0);
        grid3.add(l4, 0, 1);
        grid3.add(t4, 1, 1);
        grid3.add(l5, 0, 2);
        grid3.add(t5, 1, 2);
        grid3.add(l6, 0, 3);
        grid3.add(t6, 1, 3);
        grid3.add(btn_debruitage, 1, 4);
        grid3.add(l7, 0, 5);
        grid3.add(l8, 0, 6);
        grid3.add(l9, 0, 7);

        
        GridPane.setMargin(l3, margin);
        
        GridPane.setMargin(l4, margin);
        
        GridPane.setMargin(l5, margin);
        
        GridPane.setMargin(l6, margin);
        
        GridPane.setMargin(l7, margin);
        
        GridPane.setMargin(l8, margin);
        
        GridPane.setMargin(l9, margin);


       
        boxFunction.getChildren().add(grid3);
        
        btn_debruitage.setOnAction(event->{
        	if(t3.getValue()==null || t4.getValue()==null || t5.getValue()==null || t6.getText().isEmpty() ) {
        		Alert alerte = new Alert(AlertType.INFORMATION);
              	alerte.setTitle("Erreur");
              	alerte.setHeaderText(null);
              	alerte.setContentText("Veuiller remplir tous les champs.");
              	alerte.showAndWait();
        		
        	}else {
        		String choixPatch=t3.getValue();
        		String n=t6.getText();
        		int tailleImagette=0;
        		String seuillage=t4.getValue();
        		String typeSeuil=t5.getValue();

        		try {
        			int taillePatch =Integer.parseInt(n);
        			//debruitage imaeg
            		BufferedImage im=imageEtudie.ImageDen(cheminImage, sigma, choixPatch, taillePatch, tailleImagette, typeSeuil, seuillage);
                    //conversion buffered image en image javafx
            		if(typeSeuil.equalsIgnoreCase("seuilV")) {
                		l7.setText("Valeur du seuil :" +function.VisuShrink(imageEtudie.getNbPixel(), sigma));

            		}else {
                		l7.setText("Valeur du seuil :" +function.BayesShrink(taillePatch, imageEtudie.getPhoto()));

            		}            		
            		l8.setText("Valeur de l'erreur MSE :"+function.calculateMSE(imageEtudie.getPhoto(), im));
            		l9.setText("Valeur de l'erreur PSNR :"+function.calculatePSNR(imageEtudie.getPhoto(), im));
                    WritableImage imageDebruitee = SwingFXUtils.toFXImage(im, null);                    
            		// Attribution de l'image à l'ImageView                       
                    imageView3.setImage(imageDebruitee);
                    Alert alerte = new Alert(AlertType.INFORMATION);
                	alerte.setTitle("Etat");
	            	alerte.setHeaderText(null);
	           		alerte.setContentText("Débruitage terminé.");	
	           		alerte.showAndWait();

        		}catch(NumberFormatException e) {
                	Alert alerte = new Alert(AlertType.INFORMATION);
                	alerte.setTitle("Erreur");
	            	alerte.setHeaderText(null);
	           		alerte.setContentText("Veuillez entrer des entiers!");	
	           		alerte.showAndWait();
	           		System.out.println("La chaîne de caractères n'est pas un nombre valide.");
                } 
        		
        		
        	}
        	
        	
        });

        
//-------------------------------------------------------------------------------------------------------------------------------------------------------------        
        // Création du layout de la fenêtre principale
        VBox layout = new VBox();
        layout.getChildren().addAll(boxImage,boxFunction);        
        // Création de la scène
        Scene scene = new Scene(layout,1610,900);        
        // Configuration de la fenêtre principale
        primaryStage.setTitle("Débruitage d'image");      
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) throws Exception {
        Application.launch(args);
    }
}
