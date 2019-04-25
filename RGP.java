import java.util.*;
import java.awt.*;

class RPG{
    public static int dice;
    public static Random rnd = new Random();
    public static ArrayList<Hero> heroList = new ArrayList<Hero>();    
    public static ArrayList<Enemie> enemieList = new ArrayList<>();
    public static Scanner in = new Scanner(System.in);
    public static void main(String[] args){
        boolean DidILuse=false;
        String name;
        String input;
        String heroClass;
        System.out.println("Введите имя и класс героя");
        name = in.next();
        heroClass = in.next();
        heroList.add(HeroMaker.MakeYoureHero(heroClass, name));
        Fighting.CheckHerose(heroList);
        while(!DidILuse){
            input = in.next();
            switch (input) {
                case "heal":
                    heroList.get(0).Heal();
                    break;
                case "move":
                    dice = rnd.nextInt(20);
                    System.out.println("Дайс выпал на цифру " + (dice+1));
                    if(dice <= 5){
                        System.out.println("Бой начинается");
                        Fighting.Fihgt(heroList, enemieList);
                    }else{
                        if(dice >= 15){
                            System.out.println("Ты нашёл зелье хила");
                            heroList.get(0).hpPotion++;
                            heroList.get(0).moveCount++;
                        }else{
                            System.out.println("Ты прошёл в следующую комнату");
                            heroList.get(0).moveCount++;
                        }
                    }
                    break;
                case "check":
                    Fighting.CheckHerose(heroList);
                    break;
                default:
                    break;
            }   
            for(int i = 0; i < heroList.size(); i++){
                heroList.get(i).LevelUp();
            }
            DidILuse = CheckAliveHeroes();
        }
        Fighting.CheckHerose(heroList);
    }

    public static boolean CheckAliveHeroes(){
        int k=0;
        for(int i = 0; i < heroList.size(); i++){
            if(heroList.get(i).hp<=0){
                heroList.get(i).dead = true;
                k++;
            }
        }
        if(k==heroList.size()){
            return true;
        }else {
            return false;
        }
    }

    public static void GenerateEnemies(){
        int enemieCount = 1+rnd.nextInt(4);
        int nextEnRnd;
        for(int i = 0; i < enemieCount; i++){
            nextEnRnd = rnd.nextInt(3);
            switch (nextEnRnd) {
                case 0:
                    enemieList.add(new WeakGoblin());
                    break;
                case 1:
                    enemieList.add(new Goblin());
                    break;
                case 2:
                    enemieList.add(new StrongGoblin());
                    break;
                default:
                    break;
            }
        }
    }
}

class Fighting{
    public static void Apears(ArrayList<Enemie> enemieList){
        System.out.println("Из тени вышло " + enemieList.size() + " гоблина");
        for(int i = 0; i < enemieList.size(); i++){ 
            System.out.print((i+1)+": ");
            enemieList.get(i).GetStats();
        }
    }
    public static void CheckEnemies(ArrayList<Enemie> enemieList){
        for(int i = 0; i < enemieList.size(); i++){ 
            if(!enemieList.get(i).isDead){
            System.out.print((i+1)+": ");
            enemieList.get(i).GetStats();
            }
        }
    }
    public static void CheckHerose(ArrayList<Hero> heroList){
        for(int i = 0; i < heroList.size(); i++){ 
            System.out.print((i+1)+": ");
            heroList.get(i).GetNameAndOther();
        }
    }
    public static void Step(ArrayList<Hero> heroList, ArrayList<Enemie> enemieList,int index){
        enemieList.get(index).Hited(heroList.get(0).dmg);
        if(enemieList.get(index).hp>0){
        System.out.print("Враг №" + (index+1) + " получил " + heroList.get(0).dmg + " урона");
        System.out.println(" теперь у него " + enemieList.get(index).hp + " жизней");
        }else{
            System.out.print("Враг №" + (index+1) + " получил " + heroList.get(0).dmg + " урона");
            heroList.get(0).exp += enemieList.get(index).Death();
            heroList.get(0).LevelUp();
        }
}

    public static boolean CheckAliveEnemies(ArrayList<Enemie> enemieList){
        int k=0;
        for(int i = 0; i < enemieList.size(); i++){
            if(enemieList.get(i).isDead){
                k++;
            }
        }
        if(k==enemieList.size()){
            enemieList.clear();
            return true;
        }else {
            return false;
        }
    }
    public static void Fihgt(ArrayList<Hero> heroList, ArrayList<Enemie> enemieList){
        boolean didIWin = false;
        String input;
        int index;
        RPG.GenerateEnemies();
        Apears(enemieList);
        while(!didIWin){
            System.out.println("Attack или heal");
            input = RPG.in.next();
            if(input.equals("attack")){
                System.out.println("Каво?");
                index = RPG.in.nextInt();
                Step(heroList, enemieList,(index-1));
            }else{
                heroList.get(0).Heal();
            }
            for(int i = 0 ; i < enemieList.size(); i++){
                heroList.get(0).hp -= enemieList.get(i).Hit();
                System.out.println("Тебе дали по бошке на " + enemieList.get(i).Hit() + " урона, у тебя осталось " + heroList.get(0).hp + " жизней");
            }
            if(RPG.CheckAliveHeroes()){
                System.out.println("Пизда, ты здох");
                break;
            }
            heroList.get(0).GetNameAndOther();
            CheckEnemies(enemieList);
            didIWin = CheckAliveEnemies(enemieList);    
        }
        System.out.println("Бой окончен");
    }
}

class HeroMaker{
    public static Hero MakeYoureHero(String hero, String name){
        switch (hero) {
            case "warrior" :
                return (new Warrior(name));

            case "thief" :
                return (new Thief(name));

            case "wizard" :
                return (new Wizard(name));
            default:
            return (new Warrior(name));
        }
    }
}

//#region Heroes

abstract class Hero{
    public String name = "Великий герой";
    int maxHp;
    int hpPotion = 0;
    boolean dead = false;
    public String className;
    int hp;
    int exp;
    int level = 1;
    int mana;
    public int dmg;
    int moveCount = 0;
    public abstract int Hit();
    public abstract void MoveNext();
    public abstract void LevelUp();
    public void Heal(){
        if(hpPotion==0){
            System.out.println("А личиться то нечем!");
        }else{
            if(hp>=(maxHp-10)){
                hp = maxHp;
            }
            else{
                hp += 10;
            }
        }
    }
    public void GetNameAndOther(){
        System.out.println("**********************");
        System.out.println(name + " " + className);
        System.out.println("Жизни: " + hp);
        System.out.println("Маны:" + mana);
        System.out.println("Урона: "+ dmg);
        System.out.println("зелий лечения: "+ hpPotion);
        System.out.println("Экспы: " + exp);
        System.out.println("Уровень: " + level);
        System.out.println("До " + (level+1) + " уровня осталось " + (3*level-exp) + " экспы" );
        if(dead == true){
            System.out.println("Мёртв");
        }
        System.out.println("*********************");
    }
    public void GetStats(){
        System.out.println("Уровень: " + level);
        System.out.println("Жизней: " + hp);
        System.out.println("Экспы: " + exp);
        System.out.println("Урона: " + dmg);
        
    }
}

class Warrior extends Hero{
    public Warrior(String name){
        hp = 40;
        className = "Воин";
        maxHp = 40;
        exp = 0;
        dmg = 20;
        mana = 0;
        this.name = name;
    }
    public void MoveNext(){
        moveCount++;
        hp++;
    } 

    Hit(){
        return dmg;
    }

    public void LevelUp(){
        if(exp>= 3*level){
            dmg += 5*level;
            maxHp += 2*level;
            hp = maxHp;
            level++;
            exp = 0;
            System.out.println("Воин получил новый уровень, теперь у него " + dmg + " урона и " + hp + " жизней!");
        }
    }
    
}

class Thief extends Hero{
    public Thief(String name){
        hp = 25;
        className = "Вор";
        maxHp = 25;
        dmg = 20;
        exp = 0;
        mana = 0;
        this.name = name;
    }
    public void MoveNext(){
        moveCount++;

        hp++;
    } 

    Hit(){
        return dmg;
    }

    public void LevelUp(){
        if(exp>= 3*level){
            dmg += 5*level;
            maxHp += 2*level;
            hp = maxHp;
            level++;
            exp = 0;
            System.out.println("Вор получил новый уровень, теперь у него " + dmg + " урона и " + hp + " жизней!");
        }
    }
}

class Wizard extends Hero{
    public Wizard(String name){
        hp = 30;
        className = "Маг";
        maxHp = 30;
        dmg = 250;
        exp = 0;
        mana = 12;
        this.name = name;
    }
    public void MoveNext(){
        moveCount++;
        hp++;
        mana += 1;
    }

    Hit(){
        mana -= 3;
        return dmg;
    }

    public void LevelUp(){
        if(exp>= 3*level){
            dmg += 5*level;
            maxHp += 2*level;
            hp = maxHp;
            level++;
            exp = 0;
            System.out.println("Маг получил новый уровень, теперь у него " + dmg + " урона и " + hp + " жизней!");
        }
    }
}

abstract class Warwar extends Hero{
    public Warwar(String name){
        hp = 40;
        className = "Варвар";
        maxHp = 40;
        dmg = 19;
        exp = 0;
        mana = 0;
        this.name = name;
    }
    public void MoveNext(){
        moveCount++;
        hp++;
    } 

    public void LevelUp(){
        if(exp>= 3*level){
            dmg += 5*level;
            System.out.println("Варвар получил новый уровень, теперь у него " + dmg + " урона и " + hp + " жизней!");
        }
    }
}

//#endregion

//#region Enemies
abstract class Enemie{
    int hp;
    boolean isDead = false;
    public String name;
    int dmg;
    int expDrop;
    public void Hited(int damage){
        hp -= damage;
    }
    public int Hit(){
        return dmg;
    }
    public int Death(){
        System.out.println(". Враг умер!");
        System.out.println("вы получили " + expDrop + " опыта");
        isDead = true;
        return expDrop;
    }
    public void GetStats(){
        System.out.println("------------------------------");
        System.out.println(name);
        System.out.println("Жизни: " + hp);
        System.out.println("Урон: " + dmg);
        System.out.println("Опыта падает: " + expDrop);
        System.out.println("------------------------------");
    }
}

class WeakGoblin extends Enemie {
    public WeakGoblin(){
        hp = 5;
        dmg = 1;
        expDrop = 1;
        name = "Слабый Гоблин";
    }
}
class Goblin extends Enemie{
    public Goblin(){
        hp = 20;
        dmg = 5;
        expDrop = 5;
        name = "Простой Гоблин";
    }
}

class StrongGoblin extends Enemie{
    public StrongGoblin(){
        hp = 45;
        dmg = 15;
        expDrop = 15;
        name = "Сильный Гоблин";
    }
}
//#endregion
