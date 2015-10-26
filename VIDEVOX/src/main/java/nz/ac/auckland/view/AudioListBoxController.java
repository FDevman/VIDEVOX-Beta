package nz.ac.auckland.view;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import nz.ac.auckland.application.Playable;
import nz.ac.auckland.application.VidevoxApplication;

public class AudioListBoxController extends VIDEVOXController {

	@FXML
	private CheckBox _activeBox;
	
	@FXML
	private TextField _startOffset;
	
	@FXML
	private Slider _volSlider;
	
	@FXML
	private Label _playableName;
	
	private Playable _media;
	
	private String _name;
	
	AudioListBoxController(Playable p, String name) {
		_media = p;
		_name = name;
	}

	@Override
	public void setMainApp(VidevoxApplication app) {
		super.setMainApp(app);
		_playableName.setText(_name);
		_activeBox.setSelected(_media.isActive());
	}

}
