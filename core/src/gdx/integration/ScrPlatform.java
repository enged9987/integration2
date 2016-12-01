//https://github.com/Mrgfhci/Drop
package gdx.integration;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import java.util.Iterator;

public class ScrPlatform implements Screen, InputProcessor {

    Game game;
    Animation araniDino[];
    SpriteBatch batch;
    Sprite sprBack, sprDinoAn;
    TextureRegion trTemp;
    Texture txDeadDino, txDino, txPlat, txSheet, txTemp;
    SprDino sprDino;
    SprPlatform sprPlatform;
    int nScreenWid = Gdx.graphics.getWidth(), nDinoHei, nScreenX, nLevelCount = 1,fW, fH, fSx, fSy,nFrame, nPos;
    float fScreenWidth = Gdx.graphics.getWidth(), fScreenHei = Gdx.graphics.getHeight(), fDist, fVBackX, fProgBar = 0;
    float aspectratio = (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
    private Array<SprPlatform> arsprPlatform;
    boolean isHitPlatform = false;
    OrthographicCamera camBack;
    ShapeRenderer shape = new ShapeRenderer();
    BitmapFont textFont, textFontLevel;
    String sLevel = "Level " + nLevelCount;
    private float fVy;
    private float fVx;

    public ScrPlatform(Game _game) {
        SetFont();
        game = _game;
        araniDino = new Animation[8];
        txSheet = new Texture("dino.png");
        fW = txSheet.getWidth() / 4;
        fH = txSheet.getHeight() / 4;
        for (int i = 0; i < 8; i++) {
            Sprite[] arSprVlad = new Sprite[8];
            for (int j = 0; j < 8; j++) {
                fSx = j * fW;
                fSy = i * fH;
                sprDinoAn = new Sprite(txSheet, fSx, fSy, fW, fH);
                arSprVlad[j] = new Sprite(sprDinoAn);
            }
            araniDino[i] = new Animation(7f, arSprVlad);

        batch = new SpriteBatch();
        txDino = new Texture("Dinosaur.png");
        txDeadDino = new Texture("dead.png");
        txPlat = new Texture("Platform.png");
        sprBack = new Sprite(new Texture(Gdx.files.internal("world.jpg")));
        sprBack.setSize(fScreenWidth, fScreenHei);        
        camBack = new OrthographicCamera(fScreenWidth /**aspectratio*/, fScreenHei);
        camBack.position.set(fScreenWidth / 2, fScreenHei / 2, 0);
        Gdx.input.setInputProcessor((this));
        Gdx.graphics.setWindowedMode(800, 500);
        sprDino = new SprDino(txDino, txDeadDino);
        sprPlatform = new SprPlatform(txPlat);
        arsprPlatform = new Array<SprPlatform>();
        arsprPlatform.add(sprPlatform);        
    }
    }

    public void SetFont() {
        FileHandle fontFile = Gdx.files.internal("Woods.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 25;
        parameter.color = Color.BLACK;
        textFont = generator.generateFont(parameter);
        parameter.size = 45;
        textFontLevel = generator.generateFont(parameter);

        generator.dispose();
    }

    @Override
    public void show() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void render(float f) {
        Gdx.gl.glClearColor(1, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camBack.update();
        sprDino.HitDetection(camBack.viewportWidth);
        nFrame++;
        if (nFrame > 7) {
            nFrame = 0;
        }
        trTemp = araniDino[nPos].getKeyFrame(nFrame, true);
        for (SprPlatform sprPlatform : arsprPlatform) {
            sprPlatform.update();
        }
        if (isHitPlatform()) {
            sprDino.Animate(txDeadDino);
        } else {
            sprDino.Animate(txDino);
        }
        sprDino.update();
        batch.begin();
        if ((nScreenX < -Gdx.graphics.getWidth() || nScreenX > Gdx.graphics.getWidth())) {
            nScreenX = 0;
        }
        
        batch.setProjectionMatrix(camBack.combined);
        batch.draw(sprBack, nScreenX, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(sprBack, nScreenX - Gdx.graphics.getWidth(), 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(sprBack, nScreenX + Gdx.graphics.getWidth(), 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        textFontLevel.draw(batch, sLevel, fScreenWidth / 6, (fScreenHei / 10) * 9);
        batch.draw(sprDino.getSprite(), sprDino.getX(), sprDino.getY());
        //batch.draw(trTemp, sprDino.getX(), sprDino.getY(), 150, 200);
        for (SprPlatform sprPlatform : arsprPlatform) {
            batch.draw(sprPlatform.getSprite(), sprPlatform.getX(), sprPlatform.getY());
        }
        if (sprBack.getX() > 0) {
            nScreenX += fVx;
        }
        nScreenX -= fVx;
        batch.end();
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.BLACK);
        shape.rect((fScreenWidth - (fScreenWidth / 3) * 2) / 2, fScreenHei / 120, (fScreenWidth / 3) * 2, fScreenWidth / 40);
        shape.setColor(Color.WHITE);
        shape.rect((fScreenWidth - (fScreenWidth / 3) * 2) / 2, fScreenHei / 120, fProgBar, fScreenWidth / 40);
        shape.end();
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (fProgBar <= 0) {
                fProgBar = 0;

            } else {
                fProgBar -= .7;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            fProgBar += .7;
        }
        if (fProgBar >= ((fScreenWidth / 3) * 2)) {
            nLevelCount++;
            fProgBar = 0;
            sLevel = "Level " + nLevelCount;
        }
        Iterator<SprPlatform> iter = arsprPlatform.iterator();
        while (iter.hasNext()) {
            SprPlatform sprPlatform = iter.next();
            if (sprPlatform.canSpawn() && (arsprPlatform.size < 2)) {
                sprPlatform = new SprPlatform(txPlat);
                arsprPlatform.add(sprPlatform);
            }
            if (sprPlatform.isOffScreen()) {
                iter.remove();
            }
        }
    }

    boolean isHitPlatform() {
        Iterator<SprPlatform> iter = arsprPlatform.iterator();
        while (iter.hasNext()) {
            SprPlatform sprPlatform = iter.next();
            if (sprDino.getSprite().getBoundingRectangle().overlaps(sprPlatform.getSprite().getBoundingRectangle())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void resize(int i, int i1) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void pause() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resume() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void hide() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dispose() {
        sprPlatform.getSprite().getTexture().dispose();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE && sprDino.bJump == false) {
            sprDino.vDir.set((float) sprDino.vDir.x, 25);
            sprDino.vGrav.set(0, (float) -0.5);
            sprDino.bJump = true;
        } else if (keycode == Input.Keys.A) {
            sprDino.vDir.set(-2, (float) sprDino.vDir.y);
            fVx = -2;
            nPos = 1;
        } else if (keycode == Input.Keys.D) {
            sprDino.vDir.set(2, (float) sprDino.vDir.y);
            fVx = 2;
            nPos = 2;
        } else if (keycode == Input.Keys.E) {
            System.exit(3);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.A) {
            sprDino.vDir.set(0, (float) sprDino.vDir.y);
            fVx = 0;
            nPos = 0;
        } else if (keycode == Input.Keys.D) {
            sprDino.vDir.set(0, (float) sprDino.vDir.y);
            fVx = 0;
            nPos = 0;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean scrolled(int i) {
        return false;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
