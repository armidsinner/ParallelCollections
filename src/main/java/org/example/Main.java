package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    private static final BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);

    private static final AtomicInteger maxCountA = new AtomicInteger(0);
    private static final AtomicInteger maxCountB = new AtomicInteger(0);
    private static final AtomicInteger maxCountC = new AtomicInteger(0);

    private static final AtomicReference<String> maxTextA = new AtomicReference<String>("");
    private static final AtomicReference<String> maxTextB = new AtomicReference<String>("");
    private static final AtomicReference<String> maxTextC = new AtomicReference<String>("");

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();

        Thread textGenerator = new Thread(() -> {
            String letters = "abc";
            int length = 100000;
            for (int i = 0; i < 10000; i++) {
                String text = generateText(letters, length);
                try {
                    if (text.contains("a")) {
                        queueA.put(text);
                    }
                    if (text.contains("b")) {
                        queueB.put(text);
                    }
                    if (text.contains("c")) {
                        queueC.put(text);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queueA.add("");
            queueB.add("");
            queueC.add("");
        });
        textGenerator.start();

        Thread threadA = new Thread(() -> {
            while (true) {
                try {
                    String text = queueA.take();
                    if (text.isEmpty()) {
                        break;
                    }
                    int count = countCharacter(text, 'a');
                    if (count > maxCountA.get()) {
                        maxCountA.set(count);
                        maxTextA.set(text);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        threadA.start();
        threads.add(threadA);

        Thread threadB = new Thread(() -> {
            while (true) {
                try {
                    String text = queueB.take();
                    if (text.isEmpty()) {
                        break;
                    }
                    int count = countCharacter(text, 'b');
                    if (count > maxCountB.get()) {
                        maxCountB.set(count);
                        maxTextB.set(text);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        threadB.start();
        threads.add(threadB);

        Thread threadC = new Thread(() -> {
            while (true) {
                try {
                    String text = queueC.take();
                    if (text.isEmpty()) {
                        break;
                    }
                    int count = countCharacter(text, 'c');
                    if (count > maxCountC.get()) {
                        maxCountC.set(count);
                        maxTextC.set(text);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        threadC.start();
        threads.add(threadC);

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Текст с наибольшим количеством 'a': " + maxTextA.get() + " (Одинаковых символов: " + maxCountA.get() + ")");
        System.out.println("Текст с наибольшим количеством 'b': " + maxTextB.get() + " (Одинаковых символов: " + maxCountB.get() + ")");
        System.out.println("Текст с наибольшим количеством 'c': " + maxTextC.get() + " (Одинаковых символов: " + maxCountC.get() + ")");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int countCharacter(String text, char character) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == character) {
                count++;
            }
        }
        return count;
    }
}


