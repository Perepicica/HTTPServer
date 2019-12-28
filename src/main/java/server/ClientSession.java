package server;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class ClientSession implements Runnable {

    @Override
    public void run() {
        try {
            String header = readHeader();
            int year = getYear(header);
            send(formAnswer(year));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ClientSession(Socket socket) throws IOException {
        this.socket = socket;
        initialize();
    }

    private void initialize() throws IOException {
        in = socket.getInputStream();
        out = socket.getOutputStream();
    }

    private String readHeader() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String ln = null;
        while (true) {
            ln = reader.readLine();
            if (ln == null || ln.isEmpty()) {
                break;
            }
            builder.append(ln + System.getProperty("line.separator"));
        }
        return builder.toString();
    }

    private int getYear(String header) {
        int from = header.indexOf("?") + 1;
        int to = header.indexOf(" ", from);
        int year;
        String yearParam;
        String params = header.substring(from, to);
        int paramIndex = params.indexOf("=");
        if (paramIndex == -1 || !params.substring(0, paramIndex).equals("year")) {
            // неверно задан параметр
            return -1;
        }
        try {
            year = Integer.parseInt(params.substring(paramIndex + 1, params.length()));
            return year;
        } catch (NumberFormatException e) {
            // год - не число
            return -2;
        }
    }

    private String formAnswer(int year) {
        JSONObject answer = new JSONObject();
        String data = String.valueOf(year);
        if (year == -1) {
            answer.put("errorCode", 1);
            answer.put("dataMessage", "invalid parameter");
            return answer.toJSONString();
        }
        if (year == -2 || data.length() != 4) {
            answer.put("errorCode", 2);
            answer.put("dataMessage", "invalid year");
            return answer.toJSONString();
        }
        answer.put("errorCode", 200);
        if (year % 4 != 0 || year % 100 == 0 && year % 400 != 0) {
            answer.put("dataMessage", "13/09/"+data.substring(2,4));
        } else {
            answer.put("dataMessage", "14/09/"+data.substring(2,4));
        }
        return StringEscapeUtils.unescapeJava(answer.toJSONString());
    }

    private void send(String ans) throws IOException {
        PrintStream answer = new PrintStream(out, true, "UTF-8");
        answer.print(ans);
        answer.print("\n");
    }

    private Socket socket;
    private InputStream in = null;
    private OutputStream out = null;

}

