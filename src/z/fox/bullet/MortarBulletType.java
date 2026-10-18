package z.fox.bullet;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

import mindustry.gen.Bullet;

public class MortarBulletType extends BulletType {
    public float uptime = 90f;
    public float falltime = 90f;
    public float flytime = 60f;
    public float skyheight = 3500f;
    public float maxFallRange;
    public boolean prediction;//是否进行重定向
    public float strength = 0.65f;//预测强度
    public BulletType bullet;

    //绘制外观
    public Color color = Pal.engine.cpy();
    public float size = 7f;

    public float hitdelay=uptime + flytime + falltime;

    static class MortarData {
        float x;
        float y;
        Unit dr;
        int id;
        boolean redirect = false;//是否重定向过
    }
    public float fallbackRange = 200f;
    public MortarBulletType(float range,boolean prediction,BulletType bullet) {
        this.bullet = bullet;
        this.maxFallRange=range;
        this.prediction=prediction;

        hitEffect = Fx.none;
        despawnEffect = Fx.none;
        shootEffect = Fx.none;
        smokeEffect = Fx.none;

        collides = false;
        keepVelocity = false;
        hittable = false;
        reflectable = false;
        absorbable = false;

        lifetime = uptime + falltime + flytime + 5;
        speed = 0;
        hitSize = 0;
    }
    @Override
    public void init(Bullet b){
        super.init(b);
        MortarData data = new MortarData();
        //TODO 我想写的是当目标坐标无效时，将目标设为fallbackRange为半径的原内的随机地点,先实现简单版本的
        if (b.aimX == -1 && b.aimY == -1){
            b.remove();
            return;
        }
        //计算落点
        Tmp.v1.set(b.aimX - b.originX, b.aimY - b.originY).limit(maxFallRange);
        data.x = b.originX + Tmp.v1.x;
        data.y = b.originY + Tmp.v1.y;
        //落点修正
        if (prediction){
            Unit drunit=Units.closestEnemy(b.team,b.aimX,b.aimY,40,Unit::isValid);
            if (drunit != null){
                data.x = data.x + (drunit.deltaX() * hitdelay) * strength;
                data.y = data.y + (drunit.deltaY() * hitdelay) * strength;
                data.dr = drunit;
                data.id = drunit.id();
            }
        }
        b.data = data;
    }
    @Override
    public void update(Bullet b) {
        super.update(b);
        if (!(b.data() instanceof MortarData a)) {
            b.remove();
            return;
        }
        if (b.time >= hitdelay) {
            bullet.create(b,a.x,a.y,270f);
            b.hit = true;
            b.remove();
            return;
        } else if (b.time < uptime) {
            b.y = b.originY + b.time / uptime * skyheight;
        } else if (b.time >= uptime + flytime) {
            if (!a.redirect) {
                a.redirect = true;
                if (prediction && a.dr != null && a.dr.isValid() && a.id == a.dr.id()  ) {
                    a.x = a.dr.x() + (a.dr.deltaX() * falltime) * strength;
                    a.y = a.dr.y() + (a.dr.deltaY() * falltime) * strength;
                }
                b.x = a.x;
            }
            b.y = Mathf.lerp(a.y + skyheight, a.y, (b.time - uptime - flytime) / falltime);
        }
    }


    //下落进度
    private float fallProgress(Bullet b) {
        return Mathf.clamp((b.time - uptime - flytime) / falltime, 0f, 1f);
    }
    @Override
    public void draw(Bullet b) {
        if (!(b.data() instanceof MortarData data)) return;

        float total = uptime + flytime + falltime;
        boolean rising = b.time < uptime;

        //悬停阶段不可见 已经落地那一帧也不画
        if (b.time >= uptime && b.time < uptime + flytime) return;
        if (b.time >= total) return;

        //透明度和缩放
        float alpha, scale;

        float pp;
        if (rising) {
            //上升
            pp = b.time / uptime;
            alpha = 1f - pp * pp * pp * pp * pp;
        } else {
            //下落
            pp = fallProgress(b);
            alpha = Interp.pow3Out.apply(pp);
        }
        scale = (1f - alpha) * 0.3f + 1f;
        //阴影
        if (!rising) {
            float p = fallProgress(b);
            Draw.z(Layer.debris);
            Draw.color(0f, 0f, 0f, 0.35f * p);
            Fill.circle(data.x, data.y, size * 1.6f * p);
        }

        Draw.z(Layer.flyingUnit + 1f);

        //上升时尾焰
        if (rising) {
            float p = b.time / uptime;
            Draw.color(Pal.engine);
            Draw.alpha(alpha * 0.8f);
            Drawf.tri(b.x, b.y - size * scale * 0.5f,
                    size * 1.1f * scale,
                    size * (1.5f + 2.5f * p) * scale,
                    270f);
        }


        //弹体
        Draw.color(color);
        Draw.alpha(alpha);
        Fill.circle(b.x, b.y, size * scale);

        Draw.reset();

        //发光
        Drawf.light(b.x, b.y, size * 4f * scale, color, 0.55f * alpha);
    }
}
