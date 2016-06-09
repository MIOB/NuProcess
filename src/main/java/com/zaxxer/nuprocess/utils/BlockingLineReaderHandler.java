package com.zaxxer.nuprocess.utils;

import com.zaxxer.nuprocess.codec.NuAbstractCharsetHandler;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CoderResult;
import java.util.LinkedList;
import java.util.Queue;

public class BlockingLineReaderHandler extends NuAbstractCharsetHandler {

    private Queue<String> stdout = new LinkedList<>();
    private Queue<String> stderr = new LinkedList<>();

    public BlockingLineReaderHandler(String encoding) {
        super(Charset.forName(encoding));
    }

    public BlockingLineReaderHandler() {
        this("UTF-8");
    }

    @Override
    protected synchronized void onStdoutChars(CharBuffer buffer, boolean closed, CoderResult coderResult) {
        getLines(buffer, stdout);
    }

    @Override
    protected synchronized void onStderrChars(CharBuffer buffer, boolean closed, CoderResult coderResult) {
        getLines(buffer, stderr);
    }

    public synchronized String getStdoutLine() {
        return stdout.poll();
    }

    public synchronized String getStderrLine() {
        return stderr.poll();
    }

    private void getLines(CharBuffer buffer, Queue<String> queue) {
        String line;
        while ((line = getLineFromBuffer(buffer)) != null) {
            queue.offer(line);
        }
    }

    private String getLineFromBuffer(CharBuffer buffer) {
        for (int i = 0; i < buffer.remaining(); i++) {
            if (buffer.get(buffer.position() + i) == '\n') {
                String result = buffer.subSequence(0, i).toString();
                buffer.position(buffer.position() + i + 1);
                return result;
            }
        }
        return null;
    }
}
