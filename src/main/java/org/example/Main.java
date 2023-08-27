package org.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main extends Application {

    float t = 0.f;  // Переменная, в которую записывается сколько прошло времени. Будет использоваться чтобы враги не стреляли постоянно.
    Random r= new Random();
    Integer shootChance = 990;

    Pane root = new Pane(); // Наше игровое поле, на котором отображается все, что происходит (рисуются и обновляются спрайты)
    Sprite player = new Sprite(30, 40, 500, 400, "player", Color.BLUEVIOLET);  // Создаем спрайт игрока
    InputController controller = new InputController();
    int  killsCnt=0;

    int LivesCnt=3;

     Text killsText =new Text(10,25, "kills: " + Integer.toString(killsCnt));
     Font font=new Font(30);

     Text liveText=new Text(900, 25, "lives: " + Integer.toString((LivesCnt)));

    private Parent createContent() // Метод для настройки и наполнения root спрайтами
    {
        root.setPrefSize(1000, 800);  // Устанавливаем размеры root
        root.setStyle("-fx-background-color: #000020;");  // Устанавливаем фон root
        root.getChildren().add(player);  // Добавляем спрайт игрока на root

       killsText.setFont(font);
       killsText.setFill(Color.BLUE);
        root.getChildren().add(killsText);

           liveText.setFont(font);
           liveText.setFill(Color.BLUE);
        root.getChildren().add(liveText);


        Sprite enemy1 = new Sprite(50, 50, 250, 150, "enemy", Color.CYAN);  // Создаем спрайт врага
        Sprite enemy3 = new Sprite(50, 50, 500, 150, "enemy", Color.CYAN);  // Создаем спрайт врага
        Sprite enemy2 = new Sprite(50, 50, 750, 150, "enemy", Color.CYAN);  // Создаем спрайт врага
        root.getChildren().add(enemy1);  // Добавляем спрайт врага на root
        root.getChildren().add(enemy3);  // Добавляем спрайт врага на root
        root.getChildren().add(enemy2);  // Добавляем спрайт врага на root



        AnimationTimer timer = new AnimationTimer() {  // Создаем таймер для обновления root (чтобы спрайты могли двигаться)
            @Override
            public void handle(long l) {
                t+=0.015;
                if (controller.spacePressed.get())
                {
                    if (t>1)
                    {
                        shoot(player);
                        t=0;
                    }
                }
                if (controller.aPressed.get())
                {
                    player.moveLeft();
                }
                if (controller.dPressed.get())
                {
                    player.moveRight();
                }
                if (controller.sPressed.get())
                {
                    player.moveDown();
                }
                if (controller.wPressed.get())
                {
                    player.moveUp();
                }
                    update();  // В каждом цикле таймера выполняем метод update(), который обновляет положения спрайтов и просчитывает попадания ракет}
                }
        };

        timer.start();  // Запускаем таймер
        return root;  // Возвращаем настроенный root (в то место, где будет вызван метод createContent() r122)
    }

    private List<Sprite> sprites()  // Метод, который перебирает все объекты на root и выбирает из них только спрайты
    {
        List<Node> childrenList = new LinkedList<>(); // Список для всех объектов на root
        List<Sprite> spriteList = new ArrayList<>();  // Список для спрайтов

        childrenList = root.getChildren();  // Собираем все объекты на root в список

        for (Node n: childrenList)  // Проходим по всем объектам на root (которые мы собрали в список) циклом for, то есть берем по очереди каждый объект из списка и помешаем его в переменную n
        {
            if(n instanceof Sprite)  // Если объект, записанный в переменную n является спрайтом  (n instanceof Sprite == true)
            {
                spriteList.add((Sprite) n); // Добавляем этот объект в список спрайтов
            }
        }
        return spriteList;  // Возвращаем список спрайтов
    }

    private void update()  // Метод для обновления положений спрайтов и расчета попаданий ракет (и других действий)
    {
        t += 0.016;  // При каждом апдейте добавляем к переменной t какое-то время (сколько занимает один апдейт), чтобы знать сколько прошло с какого-то события

        int enemyCnt=0;
        for (Sprite s: sprites())  // с помощью метода sprites() получаем список спрайтов и прохожим по ним циклом (по очереди помещаем каждый спрайт в переменную s)
        {
            switch (s.type)  // В переменной s записан какой-то спрайт, в зависимости от его типа (player, enemy, player_rocket, enemy_rocket) выбираем что с ним делать
            {
                case "player_rocket": // Если тип спрайта player_rocket
                    s.moveUp(); // При каждом апдейте двигаем его вверх

                    List<Sprite> enemies = new ArrayList<>();  // Создаем список для врагов (чтобы рассчитать попадания)
                    enemies = sprites().stream().filter(new Predicate<Sprite>() {  // Получаем все спрайты методом sprites() и фильтруем из них только врагов
                        @Override
                        public boolean test(Sprite sprite) {
                            return sprite.type.equals("enemy");
                        }
                    }).collect(Collectors.toList());  // и помещаем их в список врагов

                    for (Sprite enemy : enemies)  // Перебираем список врагов циклом for (по очереди записываем каждого врага в переменную enemy)
                    {
                        if (s.getBoundsInParent().intersects(enemy.getBoundsInParent()))  // s - спрайт ракеты игрока (в этом кейсе), enemy - спрайт врага -> если границы ракеты пересекаются с границами врага
                        {
                            enemy.isDead = true;  // Враг уничтожен
                            s.isDead = true;  // ракета уничтожена
                            killsCnt++;
                            killsText.setText( "kills: " + Integer.toString(killsCnt));
                        }
                    }
                    break;

                case "enemy_rocket": // Если тип спрайта enemy_rocket
                    s.moveDown();  // При каждом апдейте двигаем его вниз
                    if (s.getBoundsInParent().intersects(player.getBoundsInParent())) // s - спрайт ракеты врага (в этом кейсе) -> если границы ракеты пересекаются с границами игрока
                    {
                        player.isDead = true;// Игрок уничтожен
                        s.isDead = true; // Ракета уничтожена
                    }
                    break;

                case "enemy":  // Если тип спрайта enemy
//                    if(t > 1)  // Если в переменной t (которую мы увеличиваем при каждом апдейте) больше чем 1.0
//                    {
//                        shoot(s);  // враг делает выстрел (s - в этом кейсе спрайт врага)
//                        t = 0f;  // обнуляем переменную t, чтобы враг не стрелял постоянно, а подождал пока в t снова не будет польше 1.0
//                    }
                    int MoveChance = r.nextInt(100);
                    if (MoveChance < 20)
                    {
                        if (s.getTranslateX() > 0) {
                            s.moveLeft();
                        }
                    }
                    if (MoveChance > 81)
                        {
                            if (s.getTranslateX() <  1000-s.getWidth()) {
                                s.moveRight();
                            }
                    }

                    int chance = r.nextInt(1000);
                    if (chance > shootChance)
                    {
                        shoot(s);
                    }
                    enemyCnt++;
                    break;
            }
        }
        if(enemyCnt==0)
        {
            Random r=new Random();

            Sprite enemy1 = new Sprite(50, 50, r.nextInt(0,250), 150, "enemy", Color.CYAN);  // Создаем спрайт врага
            Sprite enemy3 = new Sprite(50, 50, r.nextInt(250,500), 150, "enemy", Color.CYAN);  // Создаем спрайт врага
            Sprite enemy2 = new Sprite(50, 50, r.nextInt(500,1000), 150, "enemy", Color.CYAN);  // Создаем спрайт врага
            root.getChildren().add(enemy1);  // Добавляем спрайт врага на root
            root.getChildren().add(enemy3);  // Добавляем спрайт врага на root
            root.getChildren().add(enemy2);  // Добавляем спрайт врага на root
            shootChance-=50;
        }
        for (Sprite s: sprites())  // с помощью метода sprites() получаем список спрайтов и прохожим по ним циклом (по очереди помещаем каждый спрайт в переменную s)
        {
            if(s.isDead == true)  // если спрайт уничтожен
            {
                root.getChildren().remove(s);  // удаляем его с root
            }
        }
    }

    @Override
    public void start(Stage stage) throws Exception {  // Абстрактный метод класса Application (наследование от него на r20), для которого сы должны написать логику
        Scene scene = new Scene(createContent());  // Создаем "рабочий стол" и помещаем на него наше игровое поле (которое уде наполнено методом createContent())

//        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {  // Прикручиваем к нашему "рабочему столу" детектор событий с клавиатуры (реагирует на нажатия клавиш)
//            @Override
//            public void handle(KeyEvent keyEvent) {
//                switch (keyEvent.getCode())  // В зависимости от нажатой клавиши выбираем что делать
//                {
//                    case A:  // клавиша A
//                        player.moveLeft();  // двигаем игрока влево
//                        break;
//                    case D:  // клавиша D
//                        player.moveRight();  // двигаем игрока вправо
//                        break;
//                    case W:  // клавиша W
//                        player.moveUp();  // двигаем игрока вверх
//                        break;
//                    case S:  // клавиша S
//                        player.moveDown();  // двигаем игрока вниз
//                        break;
//                    case SPACE:  // клавиша SPACE
//                        if (player.isDead == false) {
//                            shoot(player);  // игрок стреляет
//                        }
//                        break;
//                    case R:
//                        if (LivesCnt>0)
//                        {
//                            player.isDead = false;
//                            root.getChildren().add(player);
//                            LivesCnt--;
//                            liveText.setText("Lives: "+Integer.toString(LivesCnt));
//                        }
//                }
//            }
//
//
//        });
        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode()== KeyCode.A)
            {
            controller.aPressed.set(true);

            }
            if (keyEvent.getCode()== KeyCode.D)
            {
                controller.dPressed.set(true);

            }
            if (keyEvent.getCode()== KeyCode.S)
            {
                controller.sPressed.set(true);

            }
            if (keyEvent.getCode()== KeyCode.W)
            {
                controller.wPressed.set(true);

            }
            if (keyEvent.getCode()== KeyCode.SPACE)
            {
                controller.spacePressed.set(true);

            }
        } );
        scene.setOnKeyReleased(keyEvent ->
        {
            if (keyEvent.getCode()== KeyCode.A)
            {
                controller.aPressed.set(false);

            }
            if (keyEvent.getCode()== KeyCode.D)
            {
                controller.dPressed.set(false);

            }
            if (keyEvent.getCode()== KeyCode.S)
            {
                controller.sPressed.set(false);

            }
            if (keyEvent.getCode()== KeyCode.W)
            {
                controller.wPressed.set(false);

            }
            if (keyEvent.getCode()== KeyCode.SPACE)
            {
                controller.spacePressed.set(false);

            }
        });

        stage.setScene(scene);  // отправляем наш "рабочий стол" на экран
        stage.show();  // показываем экран
    }

    private void shoot(Sprite who) // Метод для стрельбы, с помощью аргумента who выбираем кто стреляет
    {
        String rocketType; // создаем переменную rocketType, в которую запишем разный тип ракеты в зависимости от того кто стреляет
        if(who.type.equals("player"))  // если стрелял игрок (who.type.equals("player") == true) <- тип стреляющего спрайта == player
        {
            rocketType = "player_rocket";  // записываем в переменную rocketType значение player_rocket (ракета игрока)
        }
        else {  // если стрелял игрок (who.type.equals("player") == false) <- тип стреляющего спрайта != player
            rocketType = "enemy_rocket";  // записываем в переменную rocketType значение enemy_rocket (ракета врага)
        }

        Sprite rocket = new Sprite(5, 10, (int)(who.getTranslateX() + who.getWidth()/2) , (int)who.getTranslateY(),  // создаем спрайт ракеты и в качестве аргумента, который обозначает тип спрайта передаем переменную rocketType (в которую записано чья это ракета)
                rocketType, Color.CORAL);
        root.getChildren().add(rocket);  // добавляем ракету на root
    }

    public static void main(String[] args) {  // главный метод, с которого стартует программа
        launch(args);  // метод, который запускает приложения JavaFX
    }
}