package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Main extends JFrame implements KeyListener {

    private JTextArea [] [] grids;
    private int data [] [];
    private int [] allRect;
    private int rect;
    private int x, y;
    private int score = 0;
    private JLabel label;
    private JLabel label1;
    private boolean running;

    public Main(){
        grids = new JTextArea[29][16];
        data = new int[29][16];
        allRect = new int[] { 0x00cc, 0x8888, 0x000f, 0x0c44, 0x002e, 0x088c, 0x00e8, 0x0c88, 0x00e2, 0x044c, 0x008e,
                0x08c4, 0x006c, 0x04c8, 0x00c6, 0x08c8, 0x004e, 0x04c4, 0x00e4};
        label = new JLabel("score: 0");
        label1 = new JLabel("Начать игру");
        running = false;
        init();
    }

    public void init() {
        JPanel center = new JPanel();
        JPanel right = new JPanel();
        center.setLayout(new GridLayout(29, 16, 1, 1));
        for (int i = 0; i < grids.length; i++) {// инициализируем панель
            for (int j = 0; j < grids[i].length; j++) {
                grids[i][j] = new JTextArea(20, 20);
                grids[i][j].setBackground(Color.LIGHT_GRAY);
                grids[i][j].addKeyListener(this);
                if (j == 0 || j == grids[i].length - 1 || i == grids.length - 1) {
                    grids[i][j].setBackground(Color.BLACK);
                    data[i][j] = 1;
                }
                grids[i][j].setEditable(false); // Текстовая область не редактируется
                center.add(grids[i][j]); // Добавляем текстовую область на главную панель
            }


        }
        right.setLayout(new GridLayout(4, 1));
        right.add(new JLabel(" f : left        h : right"));
        right.add(new JLabel(" g : down   t : change"));
        right.add(label);
        label1.setForeground (Color.RED);
        right.add(label1)
        ;
        this.setLayout(new BorderLayout());
        this.add(center, BorderLayout.CENTER);
        this.add(right, BorderLayout.EAST);
        running = true;
        this.setSize (800, 1000);
        this.setVisible (true);
        this.setLocationRelativeTo (null);
        this.setResizable (false);
        this.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
    }



    public static void main(String[] args) {
        Main m = new Main (); // Создаем объект Main, в основном используемый для инициализации данных
        m.go (); // Запускаем игру
    }
    /*Начать игру*/
    public void go () {// запускаем игру
        while (true) {// Игра запускается до тех пор, пока игра не завершится ошибкой и не закончится, иначе она была выполнена
            if (running == false) {// Если игра не удалась
                break;
            }
            ranRect (); // Рисуем падающую форму сетки
            start (); // запускаем игру
        }
        label1.setText ("Игра окончена!"); // Игра окончена
    }
    /* Рисуем падающую сетку */
    public void ranRect() {
        rect = allRect [(int) (Math.random () * 19)]; // Произвольно генерируем типы блоков (всего 7 типов, 19 форм)
    }
    /* Функция запуска игры */
    public void start() {
        x = 0;
        y = 5; // Инициализируем положение падающего квадрата
        for (int i = 0; i <26; i ++) {// Всего 26 слоев, падающих один за другим
            try {
                Thread.sleep (1000); // Задержка на 1 секунду на слой
                if (canFall (x, y) == false) {// Если его нельзя отбросить
                    saveData (x, y); // Помечаем эту квадратную область data [] [] как 1, что указывает на наличие данных
                    for (int k = x; k <x + 4; k ++) {// Пройдите по 4 слоям, чтобы увидеть, есть ли квадраты в каждом слое, чтобы удалить этот ряд квадратов и подсчитать результат
                        int sum = 0;
                        for (int j = 1; j <= 15; j++) {
                            if (data[k][j] == 1) {
                                sum++;
                            }
                        }
                        if (sum == 15) {// Если в слое k есть блоки, удалите блоки в слое k
                            removeRow(k);
                        }
                    }
                    for (int j = 1; j <= 14; j ++) {// 4 верхних слоя игры не могут иметь квадратов, иначе игра завершится ошибкой
                        if (data[3][j] == 1) {
                            running = false;
                            break;
                        }
                    }
                    break;
                }
                // если его можно отбросить
                x ++; // слой плюс один
                fall (x, y); // Падаем на один слой
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
    /* Определяем, может ли падающий блок упасть */
    public boolean canFall(int m, int n) {
        int temp = 0x8000; // означает 1000 0000 0000 0000
        for (int i = 0; i <4; i ++) {// Проходим через 16 квадратов (4 * 4)
            for (int j = 0; j <4; j++) {
                if((temp & rect)!= 0) {//когда здесь квадрат
                    if (data [m + 1] [n] ==1) // Если на следующем месте стоит квадрат, сразу вернуть false
                        return false;
                }
                n ++; //Столбец плюс один
                temp >>= 1;
            }
            m ++; // Следующая строка
            n = n-4; // Вернуться к первому столбцу
        }
        return true;// можно отбросить, чтобы вернуть true
    }


    /* Сохраняем соответствующие данные неубывающего блока как 1, указывая, что в этой координате есть блок */
    public void saveData(int m, int n) {
        int temp = 0x8000; // означает 1000 0000 0000 0000
        for (int i = 0; i <4; i ++) {// Проходим через 16 квадратов (4 * 4)
            for (int j = 0; j < 4; j++) {
                if ((temp & rect)!= 0) {
                    data [m] [n] = 1; // массив данных хранится как 1
                }
                n ++; // Следующий столбец
                temp >>= 1;
            }
            m ++; // Следующая строка
            n = n-4; // Вернуться к первому столбцу
        }

    }

    /* Удаляем все квадраты в ряду рядов, и вышеперечисленные будут спускаться по очереди */

    public void removeRow(int row) {
        for (int i = row; i >= 1; i--) {
            for (int j = 1; j <= 14; j++) {
                data[i][j] = data[i - 1][j];//
            }
        }
        reflesh (); // Обновляем область главной панели игры после удаления блока строки
        score += 40; // Оценка плюс 10;
        label.setText ("score:" + score); // Показать счет

    }

    /* Обновляем область главной панели игры после удаления блока строки */
    public void reflesh() {
        for(int i = 1; i < 28; i++){
            for( int j = 1; j < 15; j++) {
                if (data [i] [j] == 1) {
                    grids [i] [j].setBackground(Color.yellow);
                }
                else {
                    grids [i] [j].setBackground(Color.lightGray);
                }
            }
        }
    }

    /* Блок отбрасывает слой */
    public void fall (int m, int n) {
        if (m> 0) // когда блок падает на один уровень
            clear (m-1, n); // Очищаем цветные квадраты на предыдущем слое
        draw (m, n); // перерисовываем квадратное изображение
    }

    /* Очищаем цветные области до падения блока */
    public void clear (int m, int n) {
        int temp = 0x8000; // означает 1000 0000 0000 0000
        for (int i = 0; i < 4; i++) {// Проходим через 16 квадратов (4 * 4)
            for (int j = 0; j < 4; j++) {
                if ((temp & rect)!= 0) {// Когда здесь квадрат
                    grids [m] [n].setBackground(Color.LIGHT_GRAY);// Очищаем цвет и превращаем его в белый
                }
                n ++; // Следующий столбец
                temp >>= 1;
            }
            m ++; // Следующая строка
            n = n-4; // Вернуться к первому столбцу
        }
    }

    /* Рисуем изображение обратного блока */
    public void draw(int m, int n){
        int temp = 0x8000; // означает 1000 0000 0000 0000
        for (int i = 0; i < 4; i++) {// Проходим через 16 квадратов (4 * 4)
            for (int j = 0; j < 4; j++) {
                if ((temp & rect)!= 0) {// Когда здесь квадрат
                    grids [m] [n].setBackground(Color.yellow);// Место с квадратами становится зеленым
                }
                n ++; // Следующий столбец
                temp >>= 1;
            }
            m ++; // Следующая строка
            n = n-4; // Вернуться к первому столбцу
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {
        if(e.getKeyChar () == 'f') { // Перемещаем квадрат влево
            if (running == false) {
                return;
            }
            if (y <= 1) // Когда попадает в левую стену
                return;
            int temp = 0x8000; // означает 1000 0000 0000 0000
            for (int i = x; i <x + 4; i++) {
                for(int j = y; j < y+4; j++) {
                    if ((rect & temp)!= 0) {
                        if (data [i] [j-1] ==1) {
                            return;
                        }
                    }
                    temp >>=1;
                }
            }
            clear(x, y); // Когда вы можете двигаться влево, очистите цвет квадрата перед перемещением влево
            y--;
            draw (x, y); // Затем перерисовываем изображение квадрата после сдвига влево
        }

        if (e.getKeyChar() == 'h') {// Квадрат перемещается вправо
            if (running == false) {
                return;
            }
            int temp = 0x8000;
            int m = x, n = y;
            int num = 7;
            for (int i = 0; i<4; i++) {
                for (int j = 0; j<4; j++) {
                    if ((temp & rect) != 0) {
                        if (n > num) {
                            num = n;
                        }
                    }
                    temp >>= 1;
                    n++;
                }
                m++;
                n = n-4;
            }
            if (num >= 14) {
                return;
            }
            temp = 0x8000;
            for (int i = x; i < x + 4; i++) {
                for (int j = y; j < y + 4; j++) {
                    if ((rect & temp) != 0) {
                        if (data[i][j + 1] == 1) {
                            return;
                        }
                    }
                    temp >>= 1;
                }
            }
            clear (x, y); // Когда вы можете двигаться вправо, очистите цвет квадрата перед перемещением вправо
            y++;
            draw (x, y); // Затем перерисовываем изображение квадрата после перемещения вправо
        }
        if (e.getKeyChar() == 'g') {// Блок перемещается вниз
            if (running == false) {
                return;
            }
            if (canFall(x, y) == false) {
                saveData(x, y);
                return;
            }
            clear (x, y); // Когда вы можете двигаться вниз, очистите цвет квадрата перед перемещением вниз
            x++;
            draw (x, y); // Затем перерисовываем изображение квадрата после движения вниз
        }
        if (e.getKeyChar() == 't') {// Изменение формы поля
            if (running == false) {
                return;
            }
            int i = 0;
            for (i = 0; i <= allRect.length; i++) {// Перебираем 19 квадратных фигур
                if(allRect [i] == rect) // Находим форму, соответствующую падающему квадрату, а затем меняем форму
                    break;
            }
            if (i == 0) // квадратный блок без изменения формы, это блок типа 1
                return;
            clear(x, y);
            if (i == 1 || i == 2) {// тип блочной графики 2
                rect = allRect[i == 1 ? 2 : 1];
                if (y > 7)
                    y = 7;
            }
            if (i>= 3 && i <= 6) {// тип графического блока 3
                rect = allRect[i + 1 > 6 ? 3 : i + 1];
            }
            if (i>= 7 && i <= 10) {// тип графики блока 4
                rect = allRect[i + 1 > 10 ? 7 : i + 1];
            }
            if (i == 11 || i == 12) {// тип блочной графики 5
                rect = allRect[i == 11 ? 12 : 11];
            }
            if (i == 13 || i == 14) {// тип блочной графики 6
                rect = allRect[i == 13 ? 14 : 13];
            }
            if (i>= 15 && i <= 18) {// тип графического блока 7
                rect = allRect[i + 1 > 18 ? 15 : i + 1];
            }
            draw(x, y);
        }

    }


}
