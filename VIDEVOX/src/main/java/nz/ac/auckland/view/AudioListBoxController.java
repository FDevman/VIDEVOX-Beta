package nz.ac.auckland.view;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import nz.ac.auckland.application.Playable;
import nz.ac.auckland.application.VidevoxPlayer;

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

	public void setPlayable(String name, Playable p) {
		_media = p;
		_name = name;

		// Set values
		_playableName.setText(_name);
		if (p != null) {
			_activeBox.setSelected(_media.isActive());
			_volSlider.setValue(_media.getMediaPlayer().getVolume());

			_startOffset.setText(String.format("%1$,.2f", p.getStartOffset().toSeconds()));

			// Set action listener for volume slider
			_volSlider.valueProperty().addListener(new InvalidationListener() {

				@Override
				public void invalidated(Observable observable) {
					_media.getMediaPlayer().setVolume(_volSlider.getValue());
				}
			});
			if (_name.equals(VidevoxPlayer.getPlayer().getVideoName())) {
				_startOffset.setDisable(true);
			} else {
				// Set action listener for start offset
				_startOffset.textProperty().addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue,
							String newValue) {
						// Check format, if invalid set it back to the old value
						if (newValue.equals("")) {
							_startOffset.setText("0");
							return;
						}
						try {
							Double.parseDouble(newValue);
							VidevoxPlayer.getPlayer().setStartOffset(_name, Double.parseDouble(newValue));
						} catch (NumberFormatException e) {
							_startOffset.setText(oldValue);
						}
					}
				});
			}
			// Listen for box checks/unchecks
			_activeBox.selectedProperty().addListener(new InvalidationListener() {

				@Override
				public void invalidated(Observable observable) {
					boxCheck();
				}
			});
		} else {
			_startOffset.setDisable(true);
			_volSlider.setDisable(true);
			_activeBox.setDisable(true);
		}
	}

	private void boxCheck() {
		VidevoxPlayer.getPlayer().setActive(_name, _activeBox.isSelected());
	}

}
