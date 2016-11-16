package gdx.integration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import java.util.Vector;

public class SprDino extends Sprite {

    private float fScreenWid, faspRat;            
    Texture txDino, txDeadDino;
    Vector2 vPos, vDir, vGrav;
    private Sprite sprDino;
    boolean bJump;

    SprDino(Texture _txDino, Texture _txDeadDino) {
        txDino = _txDino;
        txDeadDino = _txDeadDino;
        sprDino = new Sprite(txDino);
        vPos = new Vector2((0), 0);
        vDir = new Vector2(0, 0);
        vGrav = new Vector2(0, 0);
    }

    void update() {
        if (bJump) {
            vGrav.set(0, (float) (vGrav.y * 1.1));
        }
        if (vPos.y < 0) {
            vDir.set(vDir.x, 0);
            vGrav.set(0, 0);
            vPos.set(vPos.x, 0);
            bJump = false;
        }
        vDir.add(vGrav);
        vPos.add(vDir);
        sprDino.setPosition(vPos.x, vPos.y);
    }

    void HitDetection(float _ScreenWid) {
        fScreenWid = _ScreenWid;
        if ((sprDino.getX()+sprDino.getWidth() >= fScreenWid)) {       
            vPos.x = fScreenWid - (sprDino.getWidth());  
        } else if (sprDino.getX() <= 0) {
            vPos.x = 0;
        }

    }

    void Animate(Texture _txDinoState) {
        sprDino.setTexture(_txDinoState);
    }

    public Sprite getSprite() {
        return sprDino;
    }

    //@Override
    public float getX() {
        return vPos.x;
    }

    //@Override
    public float getY() {
        return vPos.y;
    }
}
