package z.fox;

import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.type.Category;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.Turret;
import z.fox.bullet.MortarBulletType;

import static mindustry.type.ItemStack.with;

public class blocks {
    public static void load() {
        BulletType a = new BasicBulletType(2.5f, 9) {{
            width = 7f;
            height = 9f;
            lifetime = 60f;
            ammoMultiplier = 2;
        }};
        Turret aa = new ItemTurret("测") {{
            requirements(Category.turret, with(Items.copper, 35));
            ammo(
                    Items.copper, new MortarBulletType(a,610)
            );
            recoil = 0.5f;
            shootY = 3f;
            reload = 200f;
            range = 600;
            shootCone = 15f;
            ammoUseEffect = Fx.casing1;
            health = 250;
            inaccuracy = 2f;
            rotateSpeed = 10f;
            coolant = consumeCoolant(0.1f);
            researchCostMultiplier = 0.05f;
        }};
    }
}
