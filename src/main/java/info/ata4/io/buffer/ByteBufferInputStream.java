package info.ata4.io.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.InvalidMarkException;

public class ByteBufferInputStream extends InputStream {
    private final ByteBuffer buf;
    private int markPos;
    private int markReadLimit;

    public ByteBufferInputStream(ByteBuffer buf) {
        this.buf = buf;
    }

    public int available() throws IOException {
        return this.buf.remaining();
    }

    public boolean markSupported() {
        return true;
    }

    public synchronized void mark(int readLimit) {
        this.buf.mark();
        this.markPos = this.buf.position();
        this.markReadLimit = readLimit;
    }

    public synchronized void reset() throws IOException {
        if (this.buf.position() - this.markPos > this.markReadLimit) {
            throw new IOException("Invalid mark");
        } else {
            try {
                this.buf.reset();
            } catch (InvalidMarkException var2) {
                throw new IOException(var2);
            }
        }
    }

    public long skip(long n) throws IOException {
        if (n <= 0L) {
            return 0L;
        } else {
            int posOld = this.buf.position();
            int posNew = (int)Math.min((long)this.buf.limit(), (long)posOld + n);
            this.buf.position(posNew);
            return (long)(posNew - posOld);
        }
    }

    public synchronized int read() throws IOException {
        if (!this.buf.hasRemaining()) {
            return -1;
        } else {
            try {
                return 255 & this.buf.get();
            } catch (BufferUnderflowException var2) {
                throw new IOException(var2);
            }
        }
    }

    public synchronized int read(byte[] bytes, int off, int len) throws IOException {
        if (!this.buf.hasRemaining()) {
            return -1;
        } else {
            len = Math.min(len, this.buf.remaining());

            try {
                this.buf.get(bytes, off, len);
                return len;
            } catch (BufferUnderflowException var5) {
                throw new IOException(var5);
            }
        }
    }
}