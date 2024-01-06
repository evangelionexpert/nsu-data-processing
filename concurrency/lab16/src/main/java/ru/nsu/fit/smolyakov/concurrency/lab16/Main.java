package ru.nsu.fit.smolyakov.concurrency.lab16;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            throw new IllegalArgumentException();
        }

        try (NaiveHttpClient client = new NaiveHttpClient(args[0])) {
            client.sendGetRequest();

            while (true) {
                String responseLine;
                int cnt = 0;
                while (cnt < 24 && (responseLine = client.getResponseLine()) != null) {
                    System.out.println(responseLine);
                    cnt++;
                }

                if (cnt == 24) {
                    System.out.println("---- press space to scroll down ----");
                    new ProcessBuilder("/bin/bash", "-c", "read -r -s -d ' '")
                        .inheritIO()
                        .start()
                        .waitFor();

//                    int ch;
//                    while ((ch = System.in.read()) != 0x20) {
//                    }

                    cnt = 0;
                } else {
                    System.out.println("! done goodbye !");
                    break;
                }
            }
        }
    }
}

