/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.services.files;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.core.properties.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.UploadedFileInfo;
import uk.ac.ebi.fg.gwt.resumable.server.FileChunkInfo;
import uk.ac.ebi.fg.gwt.resumable.server.UploadStorage;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class AnnotareUploadStorage implements UploadStorage {

//    private static final Logger logger = LoggerFactory.getLogger(AnnotareUploadStorage.class);

    private final File rootDirectory;

    @Inject
    public AnnotareUploadStorage(AnnotareProperties properties) {
        rootDirectory = properties.getHttpUploadDir();
    }

    @Override
    public boolean hasChunk(FileChunkInfo info) throws IOException {
        return getOrCreateStorageInfo((AnnotareFileChunkInfo)info).hasChunk(info.chunkNumber);
    }

    @Override
    public boolean hasAllChunks(FileChunkInfo info) throws IOException {
        return getOrCreateStorageInfo((AnnotareFileChunkInfo)info).hasAllChunks(info);
    }

    @Override
    public void storeChunk(FileChunkInfo info, InputStream stream, long length) throws IOException {
        StorageInfo storageInfo = getOrCreateStorageInfo((AnnotareFileChunkInfo)info);

        if (!storageInfo.hasChunk(info.chunkNumber)) {
            try (RandomAccessFile raf = new RandomAccessFile(storageInfo.storageFile, "rw")) {

                //Seek to offset
                raf.seek((info.chunkNumber - 1) * (long) info.chunkSize);

                long read = 0;
                byte[] buffer = new byte[Math.min(info.chunkSize, 16384)];
                while (read < length) {
                    int r = stream.read(buffer);
                    if (r < 0) {
                        break;
                    }
                    raf.write(buffer, 0, r);
                    read += r;
                }
            }
            storageInfo.addChunk(info.chunkNumber);
            writeStorageInfo(storageInfo);

            if (storageInfo.hasAllChunks(info)) {
                String newStorageFile = storageInfo.storageFile.replaceFirst("[.]upload$", "");
                if (!new File(storageInfo.storageFile).renameTo(new File(newStorageFile))) {
                    throw new IOException("Unable to rename file " + storageInfo.storageFile + " to " + storageInfo.fileName);
                }
                storageInfo.storageFile = newStorageFile;
                writeStorageInfo(storageInfo);
            }
        }
    }

    public File getUploadedFile(long userId, UploadedFileInfo fileInfo) throws IOException {
        String fileTitle = "File " + fileInfo.getFileName();

        StorageInfo storageInfo = getStorageInfo(userId, fileInfo.getFileName());

        if (null == storageInfo) {
            throw new IOException(fileTitle + " is not in uploaded files registry");
        }

        if (!storageInfo.isComplete) {
            throw new IOException(fileTitle + " is not completely downloaded");
        }

        File file = new File(storageInfo.storageFile);

        if (!file.isFile() || !file.canRead()) {
            throw new IOException(fileTitle + " is missing from the filesystem or cannot be read");
        }

        if (file.length() != fileInfo.getFileSize()) {
            throw new IOException(fileTitle + " size mismatch: received " +
                        file.length() + " bytes, expected " + fileInfo.getFileSize() + " bytes");
        }

        return file;
    }

    public void removeUploadedFile(long userId, UploadedFileInfo fileInfo, boolean shouldDeleteFile) throws IOException {
        StorageInfo storageInfo = getStorageInfo(userId, fileInfo.getFileName());

        if (null == storageInfo) {
            throw new IOException(fileInfo.getFileName() + " is not in uploaded files registry");
        }

        deleteStorageInfo(userId, fileInfo.getFileName());

        if (shouldDeleteFile && !new File(storageInfo.storageFile).delete()) {
            throw new IOException("Unable to delete " + fileInfo.getFileName());
        }
    }

    private final Map<String, StorageInfo> storageInfoMap = new ConcurrentHashMap<>();

    private StorageInfo getOrCreateStorageInfo(AnnotareFileChunkInfo info) throws IOException {
        String userId = String.format("u%s", info.userId);
        String key = getKey(info.userId, info.fileName);

        if (storageInfoMap.containsKey(key)) {
            return storageInfoMap.get(key);
        }

        StorageInfo storageInfo;

        File userDirectory = new File(rootDirectory, userId);
        if (!userDirectory.exists()) {
            userDirectory.mkdirs();
        }
        File infoFile = new File(userDirectory, info.fileName + ".info");
        if (infoFile.exists()) {
            storageInfo = readStorageInfo(infoFile);
        } else {
            storageInfo = createStorageInfo(infoFile, info);
        }
        storageInfoMap.put(key, storageInfo);
        return storageInfo;
    }

    private StorageInfo getStorageInfo(long userId, String fileName) throws IOException {
        String key = getKey(userId, fileName);

        if (storageInfoMap.containsKey(key)) {
            return storageInfoMap.get(key);
        }

        StorageInfo storageInfo;

        File infoFile = new File(rootDirectory + File.separator + key + ".info");
        if (!infoFile.exists() || !infoFile.canRead()) {
            return null;
        }

        storageInfo = readStorageInfo(infoFile);
        storageInfoMap.put(key, storageInfo);

        return storageInfo;
    }

    private StorageInfo readStorageInfo(File infoFile) throws IOException {
        Kryo kryo = new Kryo();
        try (Input in = new Input(new FileInputStream(infoFile))) {
            return kryo.readObject(in, StorageInfo.class);
        }
    }

    private StorageInfo createStorageInfo(File infoFile, AnnotareFileChunkInfo info) {
        StorageInfo storageInfo = new StorageInfo();
        storageInfo.fileName = info.fileName;
        storageInfo.storageFile = infoFile.getParentFile() + File.separator + info.fileName + ".upload";
        storageInfo.infoFile = infoFile.getAbsolutePath();
        storageInfo.isComplete = false;
        storageInfo.chunks = new ConcurrentSkipListSet<>();

        return storageInfo;
    }

    private void writeStorageInfo(StorageInfo info) throws IOException {
        Kryo kryo = new Kryo();
        try (Output out = new Output(new FileOutputStream(info.infoFile))) {
            kryo.writeObject(out, info);
        }
    }

    private void deleteStorageInfo(long userId, String fileName) throws IOException {
        String key = getKey(userId, fileName);

        if (storageInfoMap.containsKey(key)) {
            storageInfoMap.remove(key);
        }

        new File(rootDirectory + File.separator + key + ".info").delete();
    }

    private static String getKey(long userId, String fileName) {
        return String.format("u%d%s%s", userId, File.separator, fileName);
    }
//    private static String getKey(String userId, String fileId) {
//        try {
//            MessageDigest m = MessageDigest.getInstance("MD5");
//            m.reset();
//            m.update(fileId.getBytes());
//            byte[] digest = m.digest();
//            BigInteger bigInt = new BigInteger(1, digest);
//            return userId + "_" + bigInt.toString(Character.MAX_RADIX);
//        } catch (NoSuchAlgorithmException x) {
//            throw new RuntimeException(x);
//        }
//    }

    private static class StorageInfo implements Serializable {
        String fileName;
        String storageFile;
        String infoFile;
        boolean isComplete;
        Set<Integer> chunks;

        public boolean hasChunk(int chunkNumber) {
            return chunks.contains(chunkNumber);
        }

        public void addChunk(int chunkNumber) {
            chunks.add(chunkNumber);
        }

        public boolean hasAllChunks(FileChunkInfo info) {
            if (isComplete) {
                return true;
            }
            int count = (int) Math.floor(((double) info.fileSize) / ((double) info.chunkSize));
            if (chunks.size() >= count) {
                isComplete = true;
            }
            return isComplete;
        }
    }
}
