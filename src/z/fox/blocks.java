package z.fox;

import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.UnitSorts;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.type.Category;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.Turret;
import z.fox.bullet.MortarBulletType;

import static mindustry.type.ItemStack.with;

public class blocks {
    public static void load() {

        Turret a = new ItemTurret("测") {{
            requirements(Category.turret, with(Items.copper, 1000,Items.lead, 1000,Items.graphite, 600,Items.titanium, 600, Items.silicon, 500, Items.plastanium, 250, Items.metaglass, 200));
            ammo(
                Items.plastanium, new MortarBulletType(1010,true,
                    new BasicBulletType(0f, 90) {{
                    width = 9f;
                    height = 9f;
                    lifetime = 1f;
                    //范围伤害
                    splashDamageRadius = 160;
                    splashDamage =250;
                    //闪电
                    lightning = 10;
                    lightningLength = 15;
                    lightningLengthRand = 25;
                    lightningDamage = 30;
                    //治疗方块压制效果的范围
                    suppressionRange = 10;
                    suppressionDuration = 60f * 5f;
                    //火焰
                    incendAmount = 90;
                    incendSpread = splashDamageRadius + 10;//生成范围
                    incendChance = 0.5f;//生成概率
                    //液体坑
                    puddles = 130;
                    puddleRange = splashDamageRadius + 10;
                    puddleAmount = 35f;//液体量
                    puddleLiquid = Liquids.slag;

                    //buildingDamageMultiplier = 0.20f;
                    }}
                ){{
                    size = 14;
                    ammoMultiplier = 1f;
                }}
            );
            shoot = new ShootBarrel(){{
                barrels = new float[]{
                        0,0,0,
                        0,0,0,
                        0,0,0
                };
                shots = 3;
                shotDelay = 20f;
            }};
            maxAmmo = 100;
            ammoPerShot = 10;
            recoil = 0.5f;
            reload = 600f;
            range = 1000;
            ammoUseEffect = Fx.casing1;
            health = 3000;
            inaccuracy = 2f;
            rotateSpeed = 0;
            shootCone = 361;
            size = 5;
            coolant = consumeCoolant(0.1f);
            researchCostMultiplier = 0.05f;
            shake = 6f;
            unitSort = UnitSorts.strongest;
        }};
    }
}
