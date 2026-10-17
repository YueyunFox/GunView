package z.fox.bullet;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

import mindustry.gen.Bullet;

public class MortarBulletType extends BulletType {
    public float uptime = 90f;
    public float falltime = 90f;
    public float flytime = 60f;
    public float skyheight = 3500f;
    public float maxFallRange = 300f;
    public BulletType bullet;

    //绘制外观
    public Color color = Pal.engine.cpy();
    public float size = 7f;

    public float hitdelay=uptime + flytime + falltime;
    static class MortarData {
        float x;
        float y;
    }
    public float fallbackRange = 200f;
    public MortarBulletType(BulletType bullet,float range){
        this.bullet = bullet;
        this.bullet.lifetime = 0;
        this.maxFallRange=range;

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

        keepVelocity = false;
    }
    @Override
    public void init(Bullet b){
        super.init(b);
        //TODO 我想写的是当目标坐标无效时，将目标设为fallbackRange为半径的原内的随机地点,先实现简单版本的
        if (b.aimX == -1 && b.aimY == -1){
            b.remove();
            return;
        }
        Tmp.v1.set(b.aimX - b.originX, b.aimY - b.originY).limit(maxFallRange);

        MortarData data = new MortarData();
        data.x = b.originX + Tmp.v1.x;
        data.y = b.originY + Tmp.v1.y;
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
        } else if (b.time < uptime) {
            b.y = b.originY + b.time / uptime * skyheight;
        } else if (b.time >= uptime + flytime) {
            b.x = a.x;
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

        if (rising) {
            //上升
            float p = b.time / uptime;
            alpha = 1f - p * p * p * p * p;
            scale = (1f - alpha) * 0.3f + 1f;
        } else {
            //下落
            float p = fallProgress(b);
            alpha = Interp.pow3Out.apply(p);
            scale = (1f - alpha) * 0.3f + 1f;
        }
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
