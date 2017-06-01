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

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Miguel Gamboa
 *         created on 08-03-2017
 */
public class FileRequest implements IRequest {

    @Override
    public CompletableFuture<Stream<String>> getContent(String path) {
        return CompletableFuture.supplyAsync(() -> {
            return getStream(path);
        });
    }

    /**
     * Sincrono
     */
    public static Stream<String> getStream(String path) {
        String[] parts = path.split("/");
        path = parts[parts.length-1]
                .replace('?', '-')
                .replace('&', '-')
                .replace('=', '-')
                .replace(',', '-')
                .substring(0,68);
        try {
            InputStream in = ClassLoader.getSystemResource(path).openStream();
            return StreamSupport.stream(new IteratorFromReader(in), false);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    @Override
    public void close(){

    }
}
