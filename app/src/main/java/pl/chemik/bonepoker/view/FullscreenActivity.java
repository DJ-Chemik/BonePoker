package pl.chemik.bonepoker.view;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import pl.chemik.bonepoker.R;
import pl.chemik.bonepoker.gameObjects.GameObject;
import pl.chemik.bonepoker.logic.SystemGry;
import pl.chemik.bonepoker.logic.TesterFigur;
import pl.chemik.bonepoker.logic.figures.HashGenerator;
import pl.chemik.bonepoker.network.ServerConnect;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the systemGry UI (i.e.
 * status bar and navigation/systemGry bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the systemGry UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the systemGry UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * systemGry UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        // Set up the user interaction to manually show or hide the systemGry UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);


        /////////////////////////////////////////////////////////////////////////
        zainicjujKomponentyWidoku();
        systemGry.setNumerTury(1);
        systemGry.setNumerRundy(1);

        tvRunda.setText("Runda " + 1 + "/3");
        tvTura.setText("Tura " + 1 + "/2");
        GameObject.setPunktyGracza(0);
        GameObject.setPunktyPrzeciwnika(0);


    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);

    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the systemGry bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    //////////////////////////////////////////////////////////////////////////
    // NIŻEJ STREFA ROBOCZA
    /////////////////////////////////////////////////////////////////////////


    private SystemGry systemGry = new SystemGry(1);
    TesterFigur testerFigur = new TesterFigur(systemGry.getListaGraczy().get(0));
    HashGenerator hashGenerator = testerFigur.getHashGenerator();
    private ServerConnect serverConnect = GameObject.getServerConnect();

    ArrayList<Button> buttons = new ArrayList<>();
    TextView tvRunda;
    TextView tvTura;
    TextView tvWynikGracza;
    TextView tvWynikPrzeciwnika;
    TextView tvTmpWynikGracza;
    TextView tvTmpWynikPrzeciwnika;
    Button bLos;
    TextView tvNazwaFigury;

    private void zainicjujKomponentyWidoku() {
        buttons.add((Button) findViewById(R.id.bone1));
        buttons.add((Button) findViewById(R.id.bone2));
        buttons.add((Button) findViewById(R.id.bone3));
        buttons.add((Button) findViewById(R.id.bone4));
        buttons.add((Button) findViewById(R.id.bone5));
        bLos = findViewById(R.id.buttonLosujKosci);
        tvNazwaFigury = findViewById(R.id.tvNazwaFigury);
        tvRunda = findViewById(R.id.tvRunda);
        tvTura = findViewById(R.id.tvTura);
        tvWynikGracza = findViewById(R.id.tvWynikGracza);
        tvWynikPrzeciwnika = findViewById(R.id.tvWynikPrzeciwnika);
        tvTmpWynikGracza = findViewById(R.id.tvTmpWynikGracza);
        tvTmpWynikPrzeciwnika = findViewById(R.id.tvTmpWynikPrzeciwnika);
    }


    /**
     * Zaznacza lub odznacza kość w zależności czy już była zaznaczona.
     *
     * @param numer - wartość od 1 do 5
     */
    private void zaznaczKoscDoWymiany(Integer numer) {
        if (!systemGry.getListaGraczy().get(0).getNumeryKosciDoWymiany().contains(numer)) {
            if (systemGry.getNumerTury() == 2) {
                systemGry.getListaGraczy().get(0).addNumerKosciDoWymiany(numer);
                buttons.get(numer - 1).setBackgroundColor(Color.DKGRAY);
            }

        } else {
            if (systemGry.getNumerTury() == 2) {
                systemGry.getListaGraczy().get(0).removeNumerKosciDoWymiany(numer);
                buttons.get(numer - 1).setBackgroundColor(Color.WHITE); //zamien na cos takiego: buttons.get(numer - 1).setBackground (i tam domyślny materiał)

            }
        }
    }

    public void clickButtonBone1(View view) {
        zaznaczKoscDoWymiany(1);
    }

    public void clickButtonBone2(View view) {
        zaznaczKoscDoWymiany(2);
    }

    public void clickButtonBone3(View view) {
        zaznaczKoscDoWymiany(3);
    }

    public void clickButtonBone4(View view) {
        zaznaczKoscDoWymiany(4);
    }

    public void clickButtonBone5(View view) {
        zaznaczKoscDoWymiany(5);
    }

    private void wypiszNumeryKosci() {
        for (int i = 0; i < 5; i++) {
            buttons.get(i).setText(Integer.toString(systemGry.getListaGraczy().get(0).getKosci().get(i).getLiczbaOczek()));
        }
    }

    public void LosujKosci(View view) {
        int numerTury = systemGry.getNumerTury();

        if (numerTury == 1) {
            rozegrajTure1();
            wypiszNumeryKosci();

        } else if (numerTury == 2) {
            rozegrajTure2();
            wypiszNumeryKosci();


        }
    }

    private void rozegrajTure1() {
        systemGry.getListaGraczy().get(0).losujWszystkieKosci();
        bLos.setText("Wymień zaznaczone niżej kości");
        String nazwaFigury = testerFigur.znajdzFiguryIZwrocNazwe();
        tvNazwaFigury.setText("Twoja figura to: " + nazwaFigury);
        if (GameObject.getMojNumerGracza()==1){
            hashGenerator.setHash0(hashGenerator.PREFIX_PLAYER_1_RESULTS); //Hash który został wygenerowany oznacz jako infomracyjny o wynikach
        }
        if (GameObject.getMojNumerGracza()==2){
            hashGenerator.setHash0(hashGenerator.PREFIX_PLAYER_2_RESULTS); //Hash który został wygenerowany oznacz jako infomracyjny o wynikach
        }

        zaktualizujWyniki();
        systemGry.setNumerTury(2);
        tvTura.setText("Tura " + 2 + "/2");
        tvNazwaFigury.setVisibility(View.VISIBLE);

    }

    private void rozegrajTure2() {

        ArrayList<Integer> numeryKosci = systemGry.getListaGraczy().get(0).getNumeryKosciDoWymiany();
        for (Integer i : numeryKosci) {
            systemGry.getListaGraczy().get(0).losujKosc(i);
        }
        for (Button b : buttons) {
            b.setBackgroundColor(Color.WHITE);
        }
        bLos.setVisibility(View.INVISIBLE);
        tvNazwaFigury.setText("Twoja figura to: " + testerFigur.znajdzFiguryIZwrocNazwe());
        if (GameObject.getMojNumerGracza()==1){
            hashGenerator.setHash0(hashGenerator.PREFIX_PLAYER_1_RESULTS); //Hash który został wygenerowany oznacz jako infomracyjny o wynikach
        }
        if (GameObject.getMojNumerGracza()==2){
            hashGenerator.setHash0(hashGenerator.PREFIX_PLAYER_2_RESULTS); //Hash który został wygenerowany oznacz jako infomracyjny o wynikach
        }

        resetujDoNowejTury();
        if (systemGry.getNumerRundy()<3){
            systemGry.setNumerRundy(systemGry.getNumerRundy() + 1);
            tvRunda.setText("Runda " + systemGry.getNumerRundy() + "/3");

             /*String infinitySymbol;
        try {
            infinitySymbol = new String(String.valueOf(Character.toString('\u221E')).getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            infinitySymbol = "999";
        }
        tvTura.setText("Tura " + infinitySymbol + "/2");*/
            zaktualizujWyniki();
            systemGry.setNumerTury(1);
            tvTura.setText("Tura " + 1 + "/2");
        }else if(systemGry.getNumerRundy()==3){
            zaktualizujWyniki();
            serverConnect.connect();
            serverConnect.send(ServerConnect.HASH_END_GAME);
            if (GameObject.getPunktyGracza()>GameObject.getPunktyPrzeciwnika()){
                tvNazwaFigury.setText("Wygrałeś grę :)");
            }else if(GameObject.getPunktyGracza()<GameObject.getPunktyPrzeciwnika()){
                tvNazwaFigury.setText("Niestety przegrałeś :(");
            }else{
                tvNazwaFigury.setText("Remis :|");
            }


        }
    }

    private void zaktualizujWyniki(){
        serverConnect.connect();
        serverConnect.send(hashGenerator.getHash());
        String otrzymanyHash = serverConnect.recvResult();
        hashGenerator.setHash(otrzymanyHash);
        tvTmpWynikGracza.setText("Ty w tej turze: " + hashGenerator.getHash2());
        tvTmpWynikPrzeciwnika.setText("Przeciwnik w tej turze: " + hashGenerator.getHash3());

        if (systemGry.getNumerTury() == 2) {
            GameObject.addPunktyGracza(hashGenerator.getHash2());
            GameObject.addPunktyPrzeciwnika(hashGenerator.getHash3());
        }
        tvWynikGracza.setText("Twój Wynik: " + GameObject.getPunktyGracza());
        tvWynikPrzeciwnika.setText("Przeciwnik: " + GameObject.getPunktyPrzeciwnika());

    }

    private void resetujDoNowejTury(){
        tvTmpWynikGracza.setText("Ty w tej turze: " + 0);
        tvTmpWynikPrzeciwnika.setText("Przeciwnik w tej turze: " + 0);
        buttons.get(0).setText("Kość 1");
        buttons.get(1).setText("Kość 2");
        buttons.get(2).setText("Kość 3");
        buttons.get(3).setText("Kość 4");
        buttons.get(4).setText("Kość 5");
        if (systemGry.getNumerRundy()!=3){
            tvNazwaFigury.setVisibility(View.INVISIBLE);
            bLos.setText("Losuj Kości");
            bLos.setVisibility(View.VISIBLE);
        }else{
            /*bLos.setText("Kliknij by rozegrać kolejną grę :)");
            bLos.setVisibility(View.VISIBLE);*/
        }

    }


}
