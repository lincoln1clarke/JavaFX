package application;//

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.text.DecimalFormat;

public class SampleController implements Initializable{

    @FXML
    private TextField antecedent;

    @FXML
    private TextField result;
    
    @FXML
    private ComboBox<String> cboQuantities;
    
    @FXML
    private ComboBox<String> cboAntecedent;

    @FXML
    private ComboBox<String> cboResult;
    
    
    static ObservableList<String> unitesVolumes = FXCollections.observableArrayList("Millilitres", "Centilitres", "Litres", "cm³", "dm³", "m³", "in³", "ft³");//Noms des unités pour le combobox
    static double []volumeRelationships = {1, 10, 1000, 1, 1000, 1000000, 16.3871, 28316.8};//Relations entre les unités, en la même ordre que les noms des unités.
    
    static ObservableList<String> unitQuantities = FXCollections.observableArrayList("de volume", "de masse", "d' énergie", "de temps");
    
    static ObservableList<String> unitesMasse = FXCollections.observableArrayList("Gramme", "Kilogramme", "Milligramme", "Livre", "Once", "Tonne métrique", "Tonne impérial", "Tonne américain");//Noms des unités pour le combobox
    static double [] massRelationships = {1, 1000, 0.001, 453.592, 28.3495, 1000000, 1016050, 907185};//Relations entre les unités, en la même ordre que les noms des unités.
    
    static ObservableList<String> unitesEnergie = FXCollections.observableArrayList("Joule", "Wh", "kWh", "Gramme Calorie", "Kilo Calorie", "Pied-Livre");//Noms des unités pour le combobox
    static double [] energyRelationships = {1, 3600, 3600000, 28.3495, 1000000, 1016050, 907185};//Relations entre les unités, en la même ordre que les noms des unités.
    
    static ObservableList<String> unitesTemps = FXCollections.observableArrayList("Secondes", "Minutes", "Heures", "Jours", "Semaines", "Mois (30 Jours)", "Ans");//Noms des unités pour le combobox
    static double [] timeRelationships = {1, 60, 3600, 86400, 604800, 2592000, 31536000};//Relations entre les unités, en la même ordre que les noms des unités.
    //L'ordre des unités est important.  Ayant le même ordre que les noms, les indexes sont identiques, permettant a la méthode de saisir l'indexe selectionné par le combobox, est d'utiliser cet index pour trouver les facteurs de proportionnalité entre les unités.
    
    static ObservableList<ObservableList<String>> unitesTout = FXCollections.observableArrayList(unitesVolumes, unitesMasse, unitesEnergie, unitesTemps);
    static ObservableList<double []> allRelationships = FXCollections.observableArrayList(volumeRelationships, massRelationships, energyRelationships, timeRelationships);
    //Crée des listes a partir des listes déjà crées.  Cela nous permet d'avoir plusieurs quantités (Volume, Masse...)
    
    private static final DecimalFormat round = new DecimalFormat("0.0000");
    //Initialize un objet qui permet d'arrondir les nombres, utilisant la classe DecimalFormat
    
    
    
    @FXML
    void checkAndConvert(KeyEvent e) {
    	TextField txt=(TextField)e.getSource();
    	String sourceID = txt.getId();
    	try{
    		if(sourceID.equals("antecedent")) {
    			result.setText(round.format(convert(sourceID, Double.parseDouble(txt.getText()), cboAntecedent.getSelectionModel().getSelectedIndex(), cboResult.getSelectionModel().getSelectedIndex(), cboQuantities.getSelectionModel().getSelectedIndex())));
    		}else {
    			antecedent.setText(round.format(convert(sourceID, Double.parseDouble(txt.getText()), cboAntecedent.getSelectionModel().getSelectedIndex(), cboResult.getSelectionModel().getSelectedIndex(), cboQuantities.getSelectionModel().getSelectedIndex())));
    		}
    	}catch(Exception ex){
    		if(txt.getText().equals("")) {
    			result.setText("");
    			antecedent.setText("");
    		}else {
    			if(txt.getText().equals("-")) {
    				String text = txt.getText();
    				String num = "";
    				for(int i = 0; i<text.length(); i++) {
    					if(text.charAt(i) != '-') {
    						num += text.charAt(i);
    					}
    				}
    			}else {
    				String text = txt.getText();
    				String num = "";
    				for(int i = 0; i<text.length(); i++) {
    					if(Character.isDigit(text.charAt(i))) {
    						num += text.charAt(i);
    					}
    				}
    				
    				txt.setText(num);
    				Alert alert = new Alert(AlertType.ERROR);
    				alert.setHeaderText("ERREUR");
        			alert.setTitle("Attention");
        			alert.setContentText("Entrée numérique seulement");
        			alert.show();
    				}
    				checkAndConvert(e);
    			}
    		}
    	}
    
    static double convert(String sourceID, double input, int antecedentUnit, int convertedUnit, int quantity) {//Méthode pour retourner la conversion d'unités.
    	//La source est passée à elle pour qu'elle sache quels valeurs utiliser pour l'opération.
    	//Le double a convertir est aussi passé à elle, ainsi que les unités réglés par l'utilisateur.
    	if(sourceID.equals("antecedent")) {
        	input = input*allRelationships.get(quantity)[antecedentUnit];//Multiplier par la valeur correspondante dans allRelationships, un tableau définissant les relations entre tous les unités.
        	//Essentiellement, on multiplie le nombre dans la boite à droite par le facteur correspondant a l'unité dans le comboBox à droite.
        	//Ensuite on divise cette nouvelle valeur par le facteur correspondant a l'unité dans le comboBox gauche.
        	input = input/allRelationships.get(quantity)[convertedUnit];
        	return input;//Retourner la valeur convertit.
        }else {
        	//Même qu'avant, mais change les facteurs utilisés pour les opérations, car si cette branche else est exécuté c'est car la source est le membre gauche, et on veut donc modifier le membre droite.
        	input = input*allRelationships.get(quantity)[convertedUnit];
        	input = input/allRelationships.get(quantity)[antecedentUnit];
        	return input;
        	}
    	}
    
    @FXML
    void changeUnit(ActionEvent e) {//Actionné lorsqu'une des combobox d'unités sont changés. 
    								//Pas la quantité (volume, temps...) mais l'unité
    	ComboBox<String> cbo = (ComboBox<String>)e.getSource();//Crée un objet representant un combobox a partir de la source
    	String sourceID = cbo.getId();//Enrégistre la source dans une variable
    	//System.out.println(sourceID);//pour debugging
    	try{//En utilisant la source, nous changeons le résultat opposé. 
    		//Si l'unité de droite est changée, la valeur droite démeure le même et la valeur gauche change.
    		if(sourceID.equals("cboAntecedent")) {
    			result.setText(round.format(convert("antecedent", Double.parseDouble(antecedent.getText()), cboAntecedent.getSelectionModel().getSelectedIndex(), cboResult.getSelectionModel().getSelectedIndex(), cboQuantities.getSelectionModel().getSelectedIndex())));
    			//Fixe la valeur dans le textBox resultat à la valeur retournée par la fonction convert. 
    			//Cette valeur est arrondie.
    		}else {
    			antecedent.setText(round.format(convert("result",Double.parseDouble(result.getText()), cboAntecedent.getSelectionModel().getSelectedIndex(), cboResult.getSelectionModel().getSelectedIndex(), cboQuantities.getSelectionModel().getSelectedIndex())));
    			//Fixe la valeur dans le textBox antécédent à la valeur retournée par la fonction convert. 
    			//Cette valeur est arrondie.  Adjustement de la valeur antécédent car l'unité a été changé de l'autre côté.
    		}
    	}catch(Exception ex){
    		//Il ne devrait pas y avoir d'erreur ici car c'est seulement pour changer l'unité.
    		//Le texte est déjà vérifié, est l'autre fonction supprime les lettres, alors seul les nombres restent.
    		//J'ai quand même mis dans un try catch au cas où.  C'est mieux pour l'utilisateur.
    		}
    	}
    
    @FXML
    void changeQuantity(ActionEvent e) {
    	ComboBox<String> cbo = (ComboBox<String>)e.getSource();
    	cboAntecedent.setItems(unitesTout.get(cbo.getSelectionModel().getSelectedIndex()));
    	cboResult.setItems(unitesTout.get(cbo.getSelectionModel().getSelectedIndex()));
    	cboAntecedent.getSelectionModel().selectFirst();
    	cboResult.getSelectionModel().select(1);
    	result.setText("");
    	antecedent.setText("");
    }
    
    @FXML
    void exitApp(ActionEvent event) {
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Quitter?");
    	alert.setHeaderText("Attention");
    	alert.setContentText("Voulez-vous quitter?");
    	Optional<ButtonType> result= alert.showAndWait();
    	if(result.get() == ButtonType.OK) {
    		System.exit(0);
    	}
    }
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {//Méthode pour initializer les combobox.  Ça assigne les bons valeurs.
		cboQuantities.setItems(unitQuantities);
		cboQuantities.getSelectionModel().selectFirst();
		cboAntecedent.setItems(unitesVolumes);
		cboResult.setItems(unitesVolumes);
		cboAntecedent.getSelectionModel().selectFirst();
		cboResult.getSelectionModel().select(1);
	}

}
