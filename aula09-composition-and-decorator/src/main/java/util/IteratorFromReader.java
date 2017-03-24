/*
 * Copyright (c) 2017, Miguel Gamboa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * @author Miguel Gamboa
 *         created on 15-03-2017
 */
public class IteratorFromReader implements Iterator<String> {
    final BufferedReader reader;
    final InputStream in;
    private String nextLine;

    public IteratorFromReader(InputStream in) {
        this.in = in;
        this.reader = new BufferedReader(new InputStreamReader(in));
        this.nextLine = moveNext();
    }

    private String moveNext() {
        try {
            String line = reader.readLine();
            if(line == null) {
                in.close();
                reader.close();
            }
            return line;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasNext() {
        return nextLine != null;
    }

    @Override
    public String next() {
        String curr = nextLine;
        nextLine = moveNext();
        return curr;
    }
}
