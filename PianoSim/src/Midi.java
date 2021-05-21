import javafx.animation.FillTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Midi extends Application {

    private List<Note> notes = Arrays.asList(
            new Note("C", KeyCode.A, 60, Color.WHITE),
            new Note("C#", KeyCode.W, 61, Color.GRAY),
            new Note("D", KeyCode.S, 62, Color.WHITE),
            new Note("D#", KeyCode.E, 63, Color.GRAY),
            new Note("E", KeyCode.D, 64, Color.WHITE),
            new Note("F", KeyCode.F, 65, Color.WHITE),
            new Note("F#", KeyCode.T, 66, Color.GRAY),
            new Note("G", KeyCode.G, 67, Color.WHITE),
            new Note("G#", KeyCode.Y, 68, Color.GRAY),
            new Note("A", KeyCode.H, 69, Color.WHITE),
            new Note("A#", KeyCode.U, 60, Color.GRAY),
            new Note("B", KeyCode.J, 71, Color.WHITE)

    );


    private MidiChannel channel;

    private HBox root = new HBox(15);

    private Parent createContent() {
        loadChanel();


        root.setPrefSize(600, 150);

        notes.forEach(note -> {
            NoteView view = new NoteView(note);
            root.getChildren().addAll(view);
        });
        return root;
    }

    private void loadChanel() {
        try {
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            synth.loadInstrument(synth.getDefaultSoundbank().getInstruments()[0]);

            channel = synth.getChannels()[0];


        } catch (MidiUnavailableException e) {
            System.out.println("Cannot get Synthesizer");
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(createContent());
        final Set<String> pressedKeys = new HashSet<String>();
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent t) {
                String note = t.getText();
                if (!pressedKeys.contains(note)) {
                    pressedKeys.add(note);

                    onKeyPressed(t.getCode());
                }
            }
        });
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent t) {
                pressedKeys.remove(t.getText());

                onKeyReleased(t.getCode());
            }
        });
        stage.setScene(scene);
        stage.show();
    }


    private void onKeyPressed(KeyCode key) {
        root.getChildren()
                .stream()
                .map(view -> (NoteView) view)
                .filter(view -> view.note.key.equals(key))
                .forEach(view ->{

                    FillTransition ft = new FillTransition(
                            Duration.seconds(0.1),
                            view.backGround,
                            Color.WHITE,
                            Color.BLACK
                    );

                    ft.play();

                    channel.noteOn(view.note.number, 90);
                });
    }
    private void onKeyReleased(KeyCode key) {
        root.getChildren()
                .stream()
                .map(view -> (NoteView) view)
                .filter(view -> view.note.key.equals(key))
                .forEach(view ->{

                    FillTransition ft = new FillTransition(
                            Duration.seconds(0.1),
                            view.backGround,
                            Color.BLACK,
                            view.note.color
                    );

                    ft.play();

                });
    }

    private static class NoteView extends StackPane {
        private Note note;
        private Rectangle backGround = new Rectangle();

        NoteView(Note note){
            this.note = note;
            Color noteColor = note.color;
            backGround.setWidth(50);
            backGround.setHeight(150);
            backGround.setFill(note.color);
            backGround.setStroke(Color.BLACK);

            getChildren().addAll(backGround, new Text(note.name));

        }



    }

    private static class Note {
        private String name;
        private KeyCode key;
        private int number;
        private Color color;

        Note(String name, KeyCode key, int number, Color color){
            this.name = name;
            this.key = key;
            this.number = number;
            this.color = color;
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
